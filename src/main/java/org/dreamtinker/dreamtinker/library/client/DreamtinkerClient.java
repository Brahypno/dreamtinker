package org.dreamtinker.dreamtinker.library.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.dreamtinker.dreamtinker.library.client.Overlay.PerfectOverlay;
import org.dreamtinker.dreamtinker.library.client.book.DTBook;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;
import static slimeknights.tconstruct.shared.CommonsClientEvents.unicodeFontRender;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DreamtinkerClient {
    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent e) {
        e.registerAboveAll("perfect_overlay", PerfectOverlay.INSTANCE);
    }

    public static void onConstruct() {
        DTBook.initBook();
    }

    @SubscribeEvent
    static void clientSetup(final FMLClientSetupEvent event) {
        DTBook.HYPNAGOGIC_TRANSMUTE.fontRenderer = unicodeFontRender();

    }
}
