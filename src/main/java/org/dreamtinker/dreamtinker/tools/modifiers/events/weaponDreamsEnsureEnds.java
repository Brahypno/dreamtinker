package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static org.dreamtinker.dreamtinker.tools.modifiers.tools.silence_glove.weapon_dreams.computeProxyCooldownTicks;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class weaponDreamsEnsureEnds {
    // 保存每个玩家一个 pending
    static final Map<UUID, Pending> PENDING = new HashMap<>();

    static final class Pending {
        final UUID session;
        final ItemStack proxySnap;
        int ticks;                // 延迟计数
        final boolean waitCooldown;
        int selectedAtStart;
        final boolean isPlayerMining;

        Pending(UUID s, ItemStack snap, int delay, boolean waitCd, int selected, boolean isMining) {
            session = s;
            proxySnap = snap.copy();
            ticks = delay;
            waitCooldown = waitCd;
            selectedAtStart = selected;
            isPlayerMining = isMining;
        }
    }

    public static void startChosenDisplay(ServerPlayer sp, ItemStack chosen, ItemStack proxySnap, boolean isMining) {
        // 若有旧的，先立即还原
        endChosen(sp);

        int slotId = 36 + sp.getInventory().selected;
        sp.connection.send(new ClientboundContainerSetSlotPacket(
                sp.inventoryMenu.containerId,
                sp.inventoryMenu.incrementStateId(),
                slotId, chosen.copy()));

        UUID sid = UUID.randomUUID();
        PENDING.put(sp.getUUID(), new Pending(sid, proxySnap, /*delay*/8, /*waitCd*/false, sp.getInventory().selected, isMining));
    }

    static void endChosen(ServerPlayer sp) {
        Pending p = PENDING.remove(sp.getUUID());
        if (p == null)
            return;
        if (p.isPlayerMining){
            sp.setItemInHand(InteractionHand.MAIN_HAND, p.proxySnap);
            sp.getInventory().setChanged();
            int cooldownTicks = computeProxyCooldownTicks(p.proxySnap);
            sp.getCooldowns().addCooldown(p.proxySnap.getItem(), cooldownTicks);
        }
        int slotId = 36 + sp.getInventory().selected;
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

            if (sp.getInventory().selected == p.selectedAtStart){
                if (p.ticks > 0){
                    p.ticks--;
                    continue;
                }
                if (p.waitCooldown && sp.getAttackStrengthScale(0) < 1.0F)
                    continue;
                if (isMining(sp))
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
