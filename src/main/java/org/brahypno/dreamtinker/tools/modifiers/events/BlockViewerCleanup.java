package org.brahypno.dreamtinker.tools.modifiers.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.library.client.utils.BlockViewerService;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Dreamtinker.MODID)
public final class BlockViewerCleanup {
    private BlockViewerCleanup() {}

    // 监听登出并清理该玩家的状态
    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp)
            clearServerPlayer(sp);
    }

    // 服务器停服时清所有
    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent e) {
        BlockViewerService.clearAll();
    }

    // 玩家死亡时也清掉
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide)
            return;
        if (entity instanceof ServerPlayer player)
            clearServerPlayer(player);
    }

    private static void clearServerPlayer(ServerPlayer player) {
        BlockViewerService.sendBlockViewOff(player);
        BlockViewerService.clear(player);
    }
}
