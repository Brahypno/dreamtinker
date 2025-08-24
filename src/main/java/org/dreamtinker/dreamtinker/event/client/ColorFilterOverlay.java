package org.dreamtinker.dreamtinker.event.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.client.ClientMask;

// ColorFilterOverlay.java  (Client-only)
@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ColorFilterOverlay {
    public static final net.minecraftforge.client.gui.overlay.IGuiOverlay OVERLAY = (gui, g, pt, w, h) -> {
        if (!ClientMask.enabled)
            return;
        float fac = ClientMask.alphaFactor();
        if (fac <= 0f)
            return;
        int a = (int) (((ClientMask.argb >>> 24) & 0xFF) * fac) & 0xFF;
        int argb = (a << 24) | (ClientMask.argb & 0xFFFFFF);
        g.fill(0, 0, w, h, argb);              // 整屏铺色
    };

    @SubscribeEvent
    public static void onReg(RegisterGuiOverlaysEvent e) {
        e.registerBelowAll("world_filter", OVERLAY);
    }
}
/*
// 若想连同任何 Screen 也覆盖，再加（可选）：
@Mod.EventBusSubscriber(value = Dist.CLIENT)
class ScreenOverlay {
    @SubscribeEvent
    public static void onScreen(net.minecraftforge.client.event.ScreenEvent.Render.Post e) {
        if (!ClientMask.enabled)
            return;
        float fac = ClientMask.alphaFactor();
        if (fac <= 0f)
            return;
        int w = e.getScreen().width, h = e.getScreen().height;
        int a = (int) (((ClientMask.argb >>> 24) & 0xFF) * fac) & 0xFF;
        e.getGuiGraphics().fill(0, 0, w, h, (a << 24) | (ClientMask.argb & 0xFFFFFF));
    }
}
*/
