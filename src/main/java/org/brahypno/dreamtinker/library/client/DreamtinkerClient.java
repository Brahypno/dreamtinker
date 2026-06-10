package org.brahypno.dreamtinker.library.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.brahypno.dreamtinker.library.client.Overlay.PerfectOverlay;
import org.brahypno.dreamtinker.library.client.book.DTBook;
import org.brahypno.dreamtinker.library.compact.eidolon.DTEidolonCompact;

import static org.brahypno.dreamtinker.Dreamtinker.MODID;
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
    public static void clientSetup(final FMLClientSetupEvent event) {
        DTBook.HYPNAGOGIC_TRANSMUTE.fontRenderer = unicodeFontRender();
        if (ModList.get().isLoaded("eidolon")){
            MinecraftForge.EVENT_BUS.addListener((Event e) -> DTEidolonCompact.onAnyForgeEvent(e));
        }
    }
}
