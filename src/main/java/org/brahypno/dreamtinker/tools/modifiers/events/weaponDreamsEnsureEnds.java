package org.brahypno.dreamtinker.tools.modifiers.events;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.network.DNetwork;
import org.brahypno.dreamtinker.network.S2CUseRemainPacket;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.dreamtinker.tools.DreamtinkerTools;
import org.brahypno.esotericismtinker.utils.CompactUtils.CuriosCompact;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class weaponDreamsEnsureEnds {
    public static final ResourceLocation TAG_LAST_USE = Dreamtinker.getLocation("weapon_dreams_last_use");

    private static final Map<UUID, Pending> PENDING = new HashMap<>();

    public static void startChosenDisplay(ServerPlayer sp, int slot, ItemStack proxySnap, int cooldownTicks, boolean mainEmpty) {
        int slotId = 36 + sp.getInventory().selected;

        sp.connection.send(new ClientboundContainerSetSlotPacket(
                sp.inventoryMenu.containerId,
                sp.inventoryMenu.incrementStateId(),
                slotId,
                sp.getMainHandItem().copy()
        ));

        PENDING.put(sp.getUUID(), new Pending(
                UUID.randomUUID(),
                proxySnap,
                16,
                false,
                sp.getInventory().selected,
                cooldownTicks,
                slot,
                mainEmpty
        ));
    }

    public static void endChosen(ServerPlayer sp) {
        Pending pending = PENDING.remove(sp.getUUID());
        if (pending == null)
            return;

        finishChosen(sp, pending);
    }

    private static void finishChosen(ServerPlayer sp, Pending pending) {
        ItemStack candidate = sp.getInventory().getItem(pending.selectedAtStart).copy();
        ItemStack proxyForWrite = getProxyForWrite(sp, pending);

        if (!candidate.isEmpty() && proxyForWrite.getItem() instanceof IModifiable){
            ToolStack silenceGlove = ToolStack.from(proxyForWrite);
            ModifierEntry entry = silenceGlove.getModifier(DreamtinkerModifiers.Ids.weapon_slots);

            if (entry.getLevel() > 0){
                entry.getHook(ToolInventoryCapability.HOOK)
                     .setStack(silenceGlove, entry, pending.slot, candidate);

                silenceGlove.updateStack(proxyForWrite);
            }
        }

        ItemStack restore = pending.empty ? ItemStack.EMPTY : proxyForWrite.copy();

        sp.getInventory().setItem(pending.selectedAtStart, restore);
        sp.getInventory().setChanged();

        sp.getCooldowns().addCooldown(DreamtinkerTools.silence_glove.get(), pending.cooldownTicks);

        int slotId = 36 + pending.selectedAtStart;

        sp.connection.send(new ClientboundContainerSetSlotPacket(
                sp.inventoryMenu.containerId,
                sp.inventoryMenu.incrementStateId(),
                slotId,
                restore.copy()
        ));
    }

    private static ItemStack getProxyForWrite(ServerPlayer sp, Pending pending) {
        if (!pending.empty)
            return pending.proxySnap.copy();

        ItemStack curioProxy = CuriosCompact.findPreferredGlove(sp);
        if (!curioProxy.isEmpty())
            return curioProxy;

        return pending.proxySnap.copy();
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || PENDING.isEmpty())
            return;

        for (Iterator<Map.Entry<UUID, Pending>> it = PENDING.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<UUID, Pending> entry = it.next();
            ServerPlayer sp = event.getServer().getPlayerList().getPlayer(entry.getKey());

            if (sp == null){
                it.remove();
                continue;
            }

            Pending pending = entry.getValue();

            if (sp.isUsingItem() && sp.getTicksUsingItem() < sp.getUseItemRemainingTicks()){
                sp.useItemRemaining = (int) (sp.getUseItem().getUseDuration() * 0.4);

                S2CUseRemainPacket packet = new S2CUseRemainPacket(
                        sp.getId(),
                        0,
                        sp.useItemRemaining,
                        true
                );

                DNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sp), packet);
            }

            if (sp.getInventory().selected == pending.selectedAtStart){
                if (pending.ticks > 0){
                    pending.ticks--;
                    continue;
                }

                if (pending.waitCooldown && sp.getAttackStrengthScale(0) < 1.0F)
                    continue;
                if (isMining(sp) || sp.isUsingItem())
                    continue;
            }

            it.remove();
            finishChosen(sp, pending);
        }
    }

    private static boolean isMining(ServerPlayer sp) {
        return sp.gameMode.isDestroyingBlock;
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp))
            return;
        endChosen(sp);
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer sp))
            return;
        endChosen(sp);
    }

    @SubscribeEvent
    public static void onChangeDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp))
            return;
        endChosen(sp);
    }

    private static final class Pending {
        final UUID session;
        final ItemStack proxySnap;
        final int selectedAtStart;
        final boolean waitCooldown;
        int ticks;
        final int cooldownTicks;
        final int slot;
        final boolean empty;

        Pending(UUID session, ItemStack proxySnap, int ticks, boolean waitCooldown, int selectedAtStart, int cooldownTicks, int slot, boolean empty) {
            this.session = session;
            this.proxySnap = proxySnap.copy();
            this.ticks = ticks;
            this.waitCooldown = waitCooldown;
            this.selectedAtStart = selectedAtStart;
            this.cooldownTicks = cooldownTicks;
            this.slot = slot;
            this.empty = empty;
        }
    }
}