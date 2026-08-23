package org.brahypno.dreamtinker.tools.modifiers.events;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.network.DNetwork;
import org.brahypno.dreamtinker.network.S2CUseRemainPacket;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.dreamtinker.tools.DreamtinkerTools;
import org.brahypno.esotericismtinker.utils.CompatUtils.CuriosCompat;
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
    private static final String TAG_BORROW_SESSION = Dreamtinker.MODID + ":weapon_dreams_session";

    private static final Map<UUID, Pending> PENDING = new HashMap<>();

    public static void startChosenDisplay(ServerPlayer sp, int slot, ItemStack proxySnap, int cooldownTicks, boolean proxyInCurio) {
        UUID session = UUID.randomUUID();
        markBorrowed(sp.getMainHandItem(), session);
        int slotId = 36 + sp.getInventory().selected;

        sp.connection.send(new ClientboundContainerSetSlotPacket(
                sp.inventoryMenu.containerId,
                sp.inventoryMenu.incrementStateId(),
                slotId,
                sp.getMainHandItem().copy()
        ));

        PENDING.put(sp.getUUID(), new Pending(
                session,
                proxySnap,
                16,
                sp.getInventory().selected,
                cooldownTicks,
                slot,
                proxyInCurio
        ));
    }

    public static void endChosen(ServerPlayer sp) {
        Pending pending = PENDING.remove(sp.getUUID());
        if (pending == null)
            return;

        finishChosen(sp, pending);
    }

    public static void syncBorrowedUseState(ServerPlayer sp) {
        Pending pending = PENDING.get(sp.getUUID());
        if (pending == null || pending.useStateSynced || !sp.isUsingItem()
            || !belongsToSession(sp.getUseItem(), pending.session))
            return;

        // Fast-forward the borrowed item's first use to the designed full-charge
        // window. Never increase the remaining time if it has already charged
        // further, and do this only once for the current borrow transaction.
        int acceleratedRemaining = (int) (sp.getUseItem().getUseDuration() * 0.4F);
        sp.useItemRemaining = Math.min(sp.getUseItemRemainingTicks(), acceleratedRemaining);
        pending.useStateSynced = true;
        DNetwork.CHANNEL.send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sp),
                new S2CUseRemainPacket(sp.getId(), 0, sp.getUseItemRemainingTicks(), true)
        );
    }

    private static void finishChosen(ServerPlayer sp, Pending pending) {
        if (sp.isUsingItem() && belongsToSession(sp.getUseItem(), pending.session))
            sp.stopUsingItem();

        int borrowedSlot = findBorrowedSlot(sp, pending.session);
        ItemStack candidate = ItemStack.EMPTY;
        if (borrowedSlot >= 0){
            candidate = sp.getInventory().getItem(borrowedSlot).copy();
            clearBorrowMarker(candidate);
            sp.getInventory().setItem(borrowedSlot, ItemStack.EMPTY);
        }

        ItemStack proxyForWrite = getProxyForWrite(sp, pending);

        if (proxyForWrite.getItem() instanceof IModifiable){
            ToolStack silenceGlove = ToolStack.from(proxyForWrite);
            ModifierEntry entry = silenceGlove.getModifier(DreamtinkerModifiers.Ids.weapon_slots);

            if (entry.getLevel() > 0){
                entry.getHook(ToolInventoryCapability.HOOK)
                     .setStack(silenceGlove, entry, pending.slot, candidate);

                silenceGlove.updateStack(proxyForWrite);
            }
        }

        if (!pending.proxyInCurio){
            ItemStack displaced = sp.getInventory().getItem(pending.selectedAtStart).copy();
            sp.getInventory().setItem(pending.selectedAtStart, ItemStack.EMPTY);
            if (!displaced.isEmpty() && !sp.getInventory().add(displaced))
                sp.drop(displaced, false);

            sp.getInventory().setItem(pending.selectedAtStart, proxyForWrite.copy());
        }
        sp.getInventory().setChanged();

        sp.getCooldowns().addCooldown(DreamtinkerTools.silence_glove.get(), pending.cooldownTicks);

        ItemStack displayed = sp.getInventory().getItem(pending.selectedAtStart).copy();
        int slotId = 36 + pending.selectedAtStart;

        sp.connection.send(new ClientboundContainerSetSlotPacket(
                sp.inventoryMenu.containerId,
                sp.inventoryMenu.incrementStateId(),
                slotId,
                displayed
        ));
    }

    private static void markBorrowed(ItemStack stack, UUID session) {
        if (!stack.isEmpty())
            stack.getOrCreateTag().putUUID(TAG_BORROW_SESSION, session);
    }

    private static boolean belongsToSession(ItemStack stack, UUID session) {
        return !stack.isEmpty()
               && stack.hasTag()
               && stack.getTag().hasUUID(TAG_BORROW_SESSION)
               && session.equals(stack.getTag().getUUID(TAG_BORROW_SESSION));
    }

    private static void clearBorrowMarker(ItemStack stack) {
        if (stack.hasTag())
            stack.getTag().remove(TAG_BORROW_SESSION);
    }

    private static int findBorrowedSlot(ServerPlayer sp, UUID session) {
        for (int slot = 0; slot < sp.getInventory().getContainerSize(); slot++) {
            if (belongsToSession(sp.getInventory().getItem(slot), session))
                return slot;
        }
        return -1;
    }

    private static ItemStack getProxyForWrite(ServerPlayer sp, Pending pending) {
        if (!pending.proxyInCurio)
            return pending.proxySnap.copy();

        ItemStack curioProxy = CuriosCompat.findPreferredGlove(sp);
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
            int borrowedSlot = findBorrowedSlot(sp, pending.session);
            boolean borrowedInOriginalSlot = borrowedSlot == pending.selectedAtStart;
            boolean usingBorrowed = sp.isUsingItem() && belongsToSession(sp.getUseItem(), pending.session);

            if (usingBorrowed)
                syncBorrowedUseState(sp);

            if (sp.getInventory().selected == pending.selectedAtStart && borrowedInOriginalSlot){
                if (pending.ticks > 0){
                    pending.ticks--;
                    continue;
                }

                if (isMining(sp) || usingBorrowed)
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
    public static void onItemToss(ItemTossEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer sp))
            return;

        Pending pending = PENDING.get(sp.getUUID());
        ItemStack tossed = event.getEntity().getItem();
        if (pending == null || !belongsToSession(tossed, pending.session))
            return;

        clearBorrowMarker(tossed);
        endChosen(sp);
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
        int ticks;
        final int cooldownTicks;
        final int slot;
        final boolean proxyInCurio;
        boolean useStateSynced;

        Pending(UUID session, ItemStack proxySnap, int ticks, int selectedAtStart, int cooldownTicks, int slot, boolean proxyInCurio) {
            this.session = session;
            this.proxySnap = proxySnap.copy();
            this.ticks = ticks;
            this.selectedAtStart = selectedAtStart;
            this.cooldownTicks = cooldownTicks;
            this.slot = slot;
            this.proxyInCurio = proxyInCurio;
        }
    }
}
