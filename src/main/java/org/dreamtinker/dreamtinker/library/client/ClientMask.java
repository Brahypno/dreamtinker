package org.dreamtinker.dreamtinker.library.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientMask {
    public static boolean enabled = false;
    public static ColorMaskMode mode = ColorMaskMode.NONE;

    public static int argb = 0x99000000;
    public static int range = 72;
    public static float grayStrength = 0.36F;
    public static float vividStrength = 1.15F;

    private static long startTick = 0;
    private static long endTick = Long.MAX_VALUE;
    private static int fadeIn = 6, fadeOut = 6;
    private static boolean fadingOut = false;

    private ClientMask() {}

    public static void enable(ColorMaskMode newMode, int colorARGB, int colorRange, float gray, float vivid, int fadeInTicks) {
        var mc = Minecraft.getInstance();
        if (mc.level == null)
            return;

        mode = newMode == null ? ColorMaskMode.NONE : newMode;
        argb = colorARGB;

        if (mode == ColorMaskMode.OVERLAY && (argb >>> 24) == 0)
            argb |= 0x99000000;

        range = Mth.clamp(colorRange, 0, 255);
        grayStrength = Mth.clamp(gray, 0.0F, 1.0F);
        vividStrength = Mth.clamp(vivid, 0.0F, 3.0F);

        fadeIn = Math.max(0, fadeInTicks);
        fadingOut = false;
        startTick = mc.level.getGameTime();
        endTick = Long.MAX_VALUE;
        enabled = mode != ColorMaskMode.NONE;
    }

    public static void disable(int fadeOutTicks) {
        var mc = Minecraft.getInstance();
        if (mc.level == null){
            clearNow();
            return;
        }

        fadeOut = Math.max(0, fadeOutTicks);
        if (fadeOut == 0)
            clearNow();
        else {
            fadingOut = true;
            endTick = mc.level.getGameTime() + fadeOut;
        }
    }

    public static void clearNow() {
        enabled = false;
        mode = ColorMaskMode.NONE;
        fadingOut = false;
        endTick = Long.MAX_VALUE;
        ClientColorIsolationRenderer.close();
    }

    public static float alphaFactor() {
        var mc = Minecraft.getInstance();
        if (!enabled || mc.level == null)
            return 0F;

        long now = mc.level.getGameTime();

        if (fadingOut){
            if (now >= endTick){
                clearNow();
                return 0F;
            }
            return (endTick - now) / (float) Math.max(1, fadeOut);
        }

        if (fadeIn <= 0)
            return 1F;
        return Mth.clamp((now - startTick) / (float) fadeIn, 0F, 1F);
    }

    public static float targetR() {
        return ((argb >> 16) & 255) / 255.0F;
    }

    public static float targetG() {
        return ((argb >> 8) & 255) / 255.0F;
    }

    public static float targetB() {
        return (argb & 255) / 255.0F;
    }

    public static float range01() {
        return range / 255.0F;
    }

    public static float argbAlphaFactor() {
        int a = (argb >>> 24) & 255;
        return a == 0 ? 1.0F : a / 255.0F;
    }
}