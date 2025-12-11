package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.network.Dnetwork;
import org.dreamtinker.dreamtinker.network.S2CUseRemainPacket;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;
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
    // 保存每个玩家一个 pending
    static final Map<UUID, Pending> PENDING = new HashMap<>();

    static final class Pending {
        final UUID session;
        final ItemStack proxySnap;
        int ticks;                // 延迟计数
        final boolean waitCooldown;
        int selectedAtStart;
        final int cooldownTicks;
        final int slot;
        final boolean empty;

        Pending(UUID s, ItemStack snap, int delay, boolean waitCd, int selected, int cooldownTicks, int slot, Boolean MainEmpty) {
            session = s;
            proxySnap = snap.copy();
            ticks = delay;
            waitCooldown = waitCd;
            selectedAtStart = selected;
            this.cooldownTicks = cooldownTicks;
            this.slot = slot;
            this.empty = MainEmpty;
        }
    }

    public static void startChosenDisplay(ServerPlayer sp, int slot, ItemStack proxySnap, int cooldownTicks, boolean MainEmpty) {
        // 若有旧的，先立即还原
        endChosen(sp);

        int slotId = 36 + sp.getInventory().selected;
        sp.connection.send(new ClientboundContainerSetSlotPacket(
                sp.inventoryMenu.containerId,
                sp.inventoryMenu.incrementStateId(),
                slotId, sp.getMainHandItem()));

        UUID sid = UUID.randomUUID();

        PENDING.put(sp.getUUID(), new Pending(sid, proxySnap, /*delay*/16, /*waitCd*/false, sp.getInventory().selected, cooldownTicks, slot, MainEmpty));
    }

    static void endChosen(ServerPlayer sp) {
        Pending p = PENDING.remove(sp.getUUID());
        if (p == null)
            return;
        ItemStack after = sp.getInventory().getItem(p.selectedAtStart).copy();
        if (p.proxySnap.getItem() instanceof IModifiable){
            ToolStack silenceGlove = ToolStack.from(p.proxySnap);
            ModifierEntry entry = silenceGlove.getModifier(DreamtinkerModifiers.Ids.weapon_slots);
            entry.getHook(ToolInventoryCapability.HOOK).setStack(silenceGlove, entry, p.slot, after);
        }

        sp.getInventory().setItem(p.selectedAtStart, p.empty ? ItemStack.EMPTY : p.proxySnap);
        sp.getInventory().setChanged();
        sp.getCooldowns().addCooldown(DreamtinkerTools.silence_glove.get(), p.cooldownTicks);

        int slotId = 36 + p.selectedAtStart;

        sp.connection.send(new ClientboundContainerSetSlotPacket(
                sp.inventoryMenu.containerId,
                sp.inventoryMenu.incrementStateId(),
                slotId, p.proxySnap.copy()));
    }

    private static boolean isMining(ServerPlayer sp) {
        // （当鼠标左键持续按住并处于破坏进度时为 true）
        return sp.gameMode.isDestroyingBlock;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || PENDING.isEmpty())
            return;
        for (Iterator<Map.Entry<UUID, Pending>> it = PENDING.entrySet().iterator(); it.hasNext(); ) {
            var en = it.next();
            ServerPlayer sp = e.getServer().getPlayerList().getPlayer(en.getKey());
            if (sp == null){
                it.remove();
                continue;
            }
            Pending p = en.getValue();
            if (sp.isUsingItem() && sp.getTicksUsingItem() < sp.getUseItemRemainingTicks()){
                sp.useItemRemaining = (int) (sp.getUseItem().getUseDuration() * 0.4);
                S2CUseRemainPacket pkt = new S2CUseRemainPacket(
                        sp.getId(),
                        0,
                        sp.useItemRemaining,
                        true
                );
                Dnetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sp), pkt);
            }

            if (sp.getInventory().selected == p.selectedAtStart){
                if (p.ticks > 0){
                    p.ticks--;
                    continue;
                }
                if (p.waitCooldown && sp.getAttackStrengthScale(0) < 1.0F)
                    continue;
                if (isMining(sp) || sp.isUsingItem())
                    continue;
            }
            // 触发还原
            endChosen(sp);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer sp))
            return;
        endChosen(sp);
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone e) {
        if (!(e.getEntity() instanceof ServerPlayer sp))
            return;
        endChosen(sp);
    }

    @SubscribeEvent
    public static void onChangeDim(PlayerEvent.PlayerChangedDimensionEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer sp))
            return;
        endChosen(sp);
    }

}
