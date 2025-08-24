package org.dreamtinker.dreamtinker.client;

public final class ClientMask {
    public static boolean enabled = false;
    public static int argb = 0x88000000;
    private static long startTick = 0;
    private static long endTick = Long.MAX_VALUE; // 常驻：Long.MAX_VALUE
    private static int fadeIn = 6, fadeOut = 6;
    private static boolean fadingOut = false;

    public static void enable(int colorARGB, int fadeInTicks) {
        var mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.level == null)
            return;
        argb = colorARGB;
        if ((argb >>> 24) == 0){              // 没带 alpha 就补上
            argb |= 0x99000000;                  // 默认 0x99 (~60%) 透明
        }
        fadeIn = Math.max(0, fadeInTicks);
        fadingOut = false;
        startTick = mc.level.getGameTime();
        endTick = Long.MAX_VALUE;     // 常驻
        enabled = true;
    }

    public static void disable(int fadeOutTicks) {
        var mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.level == null){
            enabled = false;
            return;
        }
        fadeOut = Math.max(0, fadeOutTicks);
        if (fadeOut == 0)
            enabled = false;
        else {
            fadingOut = true;
            endTick = mc.level.getGameTime() + fadeOut;
        }
    }

    /**
     * 0..1 透明度因子（含淡入/淡出）
     */
    public static float alphaFactor() {
        var mc = net.minecraft.client.Minecraft.getInstance();
        if (!enabled || mc.level == null)
            return 0f;
        long now = mc.level.getGameTime();
        if (fadingOut){
            if (now >= endTick){
                enabled = false;
                return 0f;
            }
            return (endTick - now) / (float) Math.max(1, fadeOut);
        }
        if (fadeIn <= 0)
            return 1f;
        return Math.min(1f, (now - startTick) / (float) fadeIn);
    }
}

