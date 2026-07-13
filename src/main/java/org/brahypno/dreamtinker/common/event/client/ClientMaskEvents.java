package org.brahypno.dreamtinker.common.event.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.library.client.Overlay.ClientColorIsolationRenderer;
import org.brahypno.dreamtinker.library.client.Overlay.ClientMask;
import org.brahypno.dreamtinker.library.client.sound.ClientSoundChecker;
import org.brahypno.dreamtinker.library.client.trail.DTTrailRenderer;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, value = Dist.CLIENT)
public final class ClientMaskEvents {
    private ClientMaskEvents() {}

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL){
            ClientColorIsolationRenderer.render(event.getPartialTick());
        }
    }

    /**
     * 清除上一世界/上一服务器残留的静态客户端状态。
     * 清理后只有新的有效同步包才能重新开启效果。
     */
    @SubscribeEvent
    public static void onLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        resetVisualState();
        ClientColorIsolationRenderer.resetFailureState();
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        resetVisualState();
        ClientSoundChecker.clearAllSoundCaches();
        DTTrailRenderer.clearWorkspace();
    }

    @SubscribeEvent
    public static void onClone(ClientPlayerNetworkEvent.Clone event) {
        resetVisualState();
        DTTrailRenderer.clearWorkspace();
    }

    private static void resetVisualState() {
        ClientMask.clearNow();
        WallVisionRenderer.resetClientState();
        DTTrailRenderer.clearWorkspace();
    }
}
