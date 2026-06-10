package org.brahypno.dreamtinker.library.client.utils.model;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.awt.*;

public final class RainbowTextUtil {
    /**
     * 更接近 Minecraft 视觉风格的高饱和虹色色板
     */
    public static final int[] RAINBOW_PALETTE = {
            0xFFFF5555, // 红
            0xFFFFAA00, // 橙
            0xFFFFFF55, // 黄
            0xFF55FF55, // 绿
            0xFF55FFFF, // 青
            0xFF5555FF, // 蓝
            0xFFFF55FF  // 紫
    };
    /**
     * 柔和版，可选
     */
    public static final int[] SOFT_RAINBOW_PALETTE = {
            0xFFFF6B8A,
            0xFFFFA94D,
            0xFFFFE066,
            0xFF69DB7C,
            0xFF66D9E8,
            0xFF748FFC,
            0xFFB197FC
    };

    private RainbowTextUtil() {}

    /* ------------------------------------------------------------ */
    /* 便捷入口：本地化 key                                           */
    /* ------------------------------------------------------------ */

    /**
     * 默认静态虹色：适合一般名称、标题
     */
    public static MutableComponent rainbowKey(String key, Object... args) {
        return rainbowKey(key, Style.EMPTY, args);
    }

    /**
     * 默认静态虹色：适合一般名称、标题
     */
    public static MutableComponent rainbowKey(String key, Style baseStyle, Object... args) {
        String text = Component.translatable(key, args).getString();
        return gradientByPalette(text, baseStyle, RAINBOW_PALETTE, 0.0f, 1.0f);
    }

    /**
     * 限定色带的静态虹色：更适合中文短词，避免首尾撞色过猛
     * 例如 startT=0.05f, endT=0.70f
     */
    public static MutableComponent rainbowKeyBand(String key, float startT, float endT, Object... args) {
        return rainbowKeyBand(key, Style.EMPTY, startT, endT, args);
    }

    /**
     * 限定色带的静态虹色：更适合中文短词，避免首尾撞色过猛
     * 例如 startT=0.05f, endT=0.70f
     */
    public static MutableComponent rainbowKeyBand(String key, Style baseStyle, float startT, float endT, Object... args) {
        String text = Component.translatable(key, args).getString();
        return gradientByPalette(text, baseStyle, RAINBOW_PALETTE, startT, endT);
    }

    /**
     * 动态循环虹色：适合 GUI 每 tick 刷新的流动效果
     * offset 取值任意，会自动 wrap 到 0~1
     */
    public static MutableComponent rainbowKeyLoop(String key, float offset, Object... args) {
        return rainbowKeyLoop(key, Style.EMPTY, offset, args);
    }

    /**
     * 动态循环虹色：适合 GUI 每 tick 刷新的流动效果
     * offset 取值任意，会自动 wrap 到 0~1
     */
    public static MutableComponent rainbowKeyLoop(String key, Style baseStyle, float offset, Object... args) {
        String text = Component.translatable(key, args).getString();
        return gradientLoopByPalette(text, baseStyle, RAINBOW_PALETTE, offset);
    }

    /* ------------------------------------------------------------ */
    /* 便捷入口：字面文本                                              */
    /* ------------------------------------------------------------ */

    public static MutableComponent rainbowLiteral(String text) {
        return rainbowLiteral(text, Style.EMPTY);
    }

    public static MutableComponent rainbowLiteral(String text, Style baseStyle) {
        return gradientByPalette(text, baseStyle, RAINBOW_PALETTE, 0.0f, 1.0f);
    }

    public static MutableComponent rainbowLiteralBand(String text, float startT, float endT) {
        return rainbowLiteralBand(text, Style.EMPTY, startT, endT);
    }

    public static MutableComponent rainbowLiteralBand(String text, Style baseStyle, float startT, float endT) {
        return gradientByPalette(text, baseStyle, RAINBOW_PALETTE, startT, endT);
    }

    public static MutableComponent rainbowLiteralLoop(String text, float offset) {
        return rainbowLiteralLoop(text, Style.EMPTY, offset);
    }

    public static MutableComponent rainbowLiteralLoop(String text, Style baseStyle, float offset) {
        return gradientLoopByPalette(text, baseStyle, RAINBOW_PALETTE, offset);
    }

    /* ------------------------------------------------------------ */
    /* 便捷入口：已有 Component                                        */
    /* 注意：这里是按 getString() 取最终显示文本，原颜色/事件不保留       */
    /* ------------------------------------------------------------ */

    public static MutableComponent rainbowComponent(Component component) {
        return rainbowComponent(component, Style.EMPTY);
    }

    public static MutableComponent rainbowComponent(Component component, Style baseStyle) {
        return gradientByPalette(component.getString(), baseStyle, RAINBOW_PALETTE, 0.0f, 1.0f);
    }

    public static MutableComponent rainbowComponentBand(Component component, float startT, float endT) {
        return rainbowComponentBand(component, Style.EMPTY, startT, endT);
    }

    public static MutableComponent rainbowComponentBand(Component component, Style baseStyle, float startT, float endT) {
        return gradientByPalette(component.getString(), baseStyle, RAINBOW_PALETTE, startT, endT);
    }

    public static MutableComponent rainbowComponentLoop(Component component, float offset) {
        return rainbowComponentLoop(component, Style.EMPTY, offset);
    }

    public static MutableComponent rainbowComponentLoop(Component component, Style baseStyle, float offset) {
        return gradientLoopByPalette(component.getString(), baseStyle, RAINBOW_PALETTE, offset);
    }

    /* ------------------------------------------------------------ */
    /* 核心：静态渐变（不循环）                                         */
    /* ------------------------------------------------------------ */

    /**
     * 静态色板渐变，不循环。
     * startT/endT 取值通常在 0~1，超出会自动夹取。
     */
    public static MutableComponent gradientByPalette(String text, Style baseStyle, int[] palette, float startT, float endT) {
        MutableComponent result = Component.empty();

        if (text == null || text.isEmpty()){
            return result;
        }
        if (palette == null || palette.length == 0){
            return Component.literal(text).setStyle(baseStyle);
        }
        if (palette.length == 1){
            return Component.literal(text).setStyle(baseStyle.withColor(stripAlpha(palette[0])));
        }

        startT = clamp01(startT);
        endT = clamp01(endT);

        // 允许用户传反，自动纠正
        if (endT < startT){
            float temp = startT;
            startT = endT;
            endT = temp;
        }

        int visualCount = countRenderableChars(text);
        if (visualCount <= 0){
            return Component.literal(text).setStyle(baseStyle);
        }

        int visualIndex = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (Character.isWhitespace(ch)){
                result.append(Component.literal(String.valueOf(ch)).setStyle(baseStyle));
                continue;
            }

            float u = visualCount == 1 ? 0.5f : (float) visualIndex / (float) (visualCount - 1);
            float t = lerp(startT, endT, u);
            int color = samplePaletteNoLoop(palette, t);

            result.append(Component.literal(String.valueOf(ch))
                                   .setStyle(baseStyle.withColor(stripAlpha(color))));
            visualIndex++;
        }

        return result;
    }

    /* ------------------------------------------------------------ */
    /* 核心：循环渐变（用于动态流动）                                   */
    /* ------------------------------------------------------------ */

    /**
     * 循环色板渐变，适合动态效果。
     * offset 每变化一点，整串颜色向前平移。
     */
    public static MutableComponent gradientLoopByPalette(String text, Style baseStyle, int[] palette, float offset) {
        MutableComponent result = Component.empty();

        if (text == null || text.isEmpty()){
            return result;
        }
        if (palette == null || palette.length == 0){
            return Component.literal(text).setStyle(baseStyle);
        }
        if (palette.length == 1){
            return Component.literal(text).setStyle(baseStyle.withColor(stripAlpha(palette[0])));
        }

        int visualCount = countRenderableChars(text);
        if (visualCount <= 0){
            return Component.literal(text).setStyle(baseStyle);
        }

        offset = wrap01(offset);

        int visualIndex = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (Character.isWhitespace(ch)){
                result.append(Component.literal(String.valueOf(ch)).setStyle(baseStyle));
                continue;
            }

            float u = visualCount == 1 ? 0f : (float) visualIndex / (float) visualCount;
            float t = wrap01(offset + u);
            int color = samplePaletteLoop(palette, t);

            result.append(Component.literal(String.valueOf(ch))
                                   .setStyle(baseStyle.withColor(stripAlpha(color))));
            visualIndex++;
        }

        return result;
    }

    /* ------------------------------------------------------------ */
    /* 核心：HSB 版循环虹色，可选                                        */
    /* ------------------------------------------------------------ */

    /**
     * 如果你以后想完全不用色板，而是直接沿色相环循环，可以用这个。
     */
    public static MutableComponent rainbowLiteralLoopHSB(String text, Style baseStyle, float offset, float saturation, float brightness) {
        MutableComponent result = Component.empty();

        if (text == null || text.isEmpty()){
            return result;
        }

        saturation = clamp01(saturation);
        brightness = clamp01(brightness);
        offset = wrap01(offset);

        int visualCount = countRenderableChars(text);
        if (visualCount <= 0){
            return Component.literal(text).setStyle(baseStyle);
        }

        int visualIndex = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (Character.isWhitespace(ch)){
                result.append(Component.literal(String.valueOf(ch)).setStyle(baseStyle));
                continue;
            }

            float u = visualCount == 1 ? 0f : (float) visualIndex / (float) visualCount;
            float hue = wrap01(offset + u);
            int rgb = Color.HSBtoRGB(hue, saturation, brightness);

            result.append(Component.literal(String.valueOf(ch))
                                   .setStyle(baseStyle.withColor(rgb & 0xFFFFFF)));
            visualIndex++;
        }

        return result;
    }

    /* ------------------------------------------------------------ */
    /* 采样                                                            */
    /* ------------------------------------------------------------ */

    /**
     * 静态渐变采样：从头到尾，不回环
     */
    private static int samplePaletteNoLoop(int[] palette, float t) {
        t = clamp01(t);

        float scaled = t * (palette.length - 1);
        int idx = (int) Math.floor(scaled);
        int next = Math.min(idx + 1, palette.length - 1);
        float localT = scaled - idx;

        return lerpColor(palette[idx], palette[next], localT);
    }

    /**
     * 循环渐变采样：尾部接回头部
     */
    private static int samplePaletteLoop(int[] palette, float t) {
        t = wrap01(t);

        float scaled = t * palette.length;
        int idx = (int) Math.floor(scaled) % palette.length;
        int next = (idx + 1) % palette.length;
        float localT = scaled - (float) Math.floor(scaled);

        return lerpColor(palette[idx], palette[next], localT);
    }

    /* ------------------------------------------------------------ */
    /* 工具                                                            */
    /* ------------------------------------------------------------ */

    private static int countRenderableChars(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))){
                count++;
            }
        }
        return count;
    }

    private static int stripAlpha(int argb) {
        return argb & 0xFFFFFF;
    }

    private static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    private static float wrap01(float v) {
        float r = v % 1.0f;
        return r < 0f ? r + 1.0f : r;
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * clamp01(t);
    }

    private static int lerpColor(int c1, int c2, float t) {
        t = clamp01(t);

        int a1 = (c1 >>> 24) & 0xFF;
        int r1 = (c1 >>> 16) & 0xFF;
        int g1 = (c1 >>> 8) & 0xFF;
        int b1 = c1 & 0xFF;

        int a2 = (c2 >>> 24) & 0xFF;
        int r2 = (c2 >>> 16) & 0xFF;
        int g2 = (c2 >>> 8) & 0xFF;
        int b2 = c2 & 0xFF;

        int a = (int) (a1 + (a2 - a1) * t);
        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);

        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}