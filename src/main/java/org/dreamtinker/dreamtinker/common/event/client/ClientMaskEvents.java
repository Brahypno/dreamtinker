package org.dreamtinker.dreamtinker.common.event.client;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.client.ClientColorIsolationRenderer;
import org.dreamtinker.dreamtinker.library.client.ClientMask;
import org.dreamtinker.dreamtinker.library.client.sound.ClientSoundChecker;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, value = Dist.CLIENT)
public final class ClientMaskEvents {
    private ClientMaskEvents() {}

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL)
            ClientColorIsolationRenderer.render(event.getPartialTick());
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientMask.clearNow();
        ClientSoundChecker.clearAllSoundCaches();
    }

    @SubscribeEvent
    public static void onClone(ClientPlayerNetworkEvent.Clone event) {
        ClientMask.clearNow();
    }
}
