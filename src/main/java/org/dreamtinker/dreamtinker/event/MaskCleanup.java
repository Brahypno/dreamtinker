package org.dreamtinker.dreamtinker.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.utils.MaskService;

// 监听登出并清理该玩家的遮罩状态
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Dreamtinker.MODID)
public final class MaskCleanup {
    @SubscribeEvent
    public static void onLogout(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent e) {
        if (e.getEntity() instanceof net.minecraft.server.level.ServerPlayer sp){
            MaskService.sendMaskOff(sp, 0);
            MaskService.clear(sp); // 只在下线时清
        }
    }

    // （可选）服务器停服时清所有
    @SubscribeEvent
    public static void onServerStopped(net.minecraftforge.event.server.ServerStoppedEvent e) {
        MaskService.clearAll();
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {//Be kind to clear the mask on Player death
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide)
            return;
        if (entity instanceof ServerPlayer player){
            MaskService.sendMaskOff(player, 0);
            MaskService.clear(player);
        }
    }
}

