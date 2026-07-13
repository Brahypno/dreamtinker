package org.brahypno.dreamtinker.library.client.Overlay;

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

    private static long startTick = 0L;
    private static long endTick = Long.MAX_VALUE;
    private static int fadeIn = 6;
    private static int fadeOut = 6;
    private static boolean fadingOut = false;

    private ClientMask() {}

    public static void enable(
            ColorMaskMode newMode, int colorARGB, int colorRange,
            float gray, float vivid, int fadeInTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null){
            return;
        }

        ColorMaskMode requestedMode = newMode == null ? ColorMaskMode.NONE : newMode;
        if (requiresPostEffect(requestedMode)
            && ClientColorIsolationRenderer.isPermanentlyDisabled()){
            clearNow();
            return;
        }

        mode = requestedMode;
        argb = colorARGB;
        if (mode == ColorMaskMode.OVERLAY && (argb >>> 24) == 0){
            argb |= 0x99000000;
        }

        range = Mth.clamp(colorRange, 0, 255);
        grayStrength = Mth.clamp(gray, 0.0F, 1.0F);
        vividStrength = Mth.clamp(vivid, 0.0F, 3.0F);
        fadeIn = Math.max(0, fadeInTicks);
        fadingOut = false;
        startTick = minecraft.level.getGameTime();
        endTick = Long.MAX_VALUE;
        enabled = mode != ColorMaskMode.NONE;
    }

    public static void disable(int fadeOutTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null){
            clearNow();
            return;
        }

        fadeOut = Math.max(0, fadeOutTicks);
        if (fadeOut == 0){
            clearNow();
        }else {
            fadingOut = true;
            endTick = minecraft.level.getGameTime() + fadeOut;
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
        Minecraft minecraft = Minecraft.getInstance();
        if (!enabled || minecraft.level == null){
            return 0.0F;
        }

        long now = minecraft.level.getGameTime();
        if (fadingOut){
            if (now >= endTick){
                clearNow();
                return 0.0F;
            }
            return (endTick - now) / (float) Math.max(1, fadeOut);
        }

        if (fadeIn <= 0){
            return 1.0F;
        }
        return Mth.clamp((now - startTick) / (float) fadeIn, 0.0F, 1.0F);
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
        int alpha = (argb >>> 24) & 255;
        return alpha == 0 ? 1.0F : alpha / 255.0F;
    }

    private static boolean requiresPostEffect(ColorMaskMode value) {
        return value == ColorMaskMode.COLOR_ISOLATION
               || value == ColorMaskMode.ATMOSPHERE;
    }
}
