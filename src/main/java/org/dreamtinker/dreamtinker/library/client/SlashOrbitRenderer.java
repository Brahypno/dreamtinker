package org.dreamtinker.dreamtinker.library.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.entity.SlashOrbitEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * 水平回旋斩渲染器（只狼风格）
 * 特性：
 * - 多道同心弧叠加（RINGS/RING_STEP）
 * - 贴图U重复 + 滚动（U_REPEAT/SCROLL_SPD）
 * - 颜色渐变（SOLID/ANGULAR/RADIAL/LENGTH/TIME_RAINBOW + HSV/RGB）
 * - 自发光（FULL_BRIGHT）
 * - 第一人称可见性优化（轻微仰角 TILT_DEG）
 */
public class SlashOrbitRenderer extends EntityRenderer<SlashOrbitEntity> {

    private static final ResourceLocation TEX =
            Dreamtinker.getLocation("textures/entity/slash_blade.png");

    private static final float DENSITY = 1.35f; // 整体透明度乘子
    private static final float MIN_ALPHA = 0.55f; // 最低透明度夹底
    private static final int U_SLICES = 48;   // 沿弧方向
    private static final int V_SLICES = 16;   // 沿径向方向
    private static final float R_MID_UV = 0.70f;
    private static final float R_HALF_T_UV = 0.11f;                   // 0.22/2
    private static final float R_IN_UV = R_MID_UV - R_HALF_T_UV;  // ≈0.59
    private static final float R_OUT_UV = R_MID_UV + R_HALF_T_UV;  // ≈0.81
    private static final int MAX_INNER_LAYERS = 7;
    private static final float STEP_OVERLAP = 0.70f; // 相邻层中心间距 = STEP_OVERLAP * 可见带宽
    private static final float MIN_STEP_WORLD = 0.01f; // 最小步长(防止停滞)


    public SlashOrbitRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(SlashOrbitEntity e) {
        return TEX;
    }

    @Override
    public void render(SlashOrbitEntity e, float yaw, float pt, PoseStack pose, MultiBufferSource buf, int packed) {
        float age = e.tickCount + pt;
        float life = Math.max(1f, e.life());
        float fade = 1f - age / life;
        if (Float.isNaN(fade) || Float.isInfinite(fade))
            fade = 1f;
        fade = Mth.clamp(fade * DENSITY, MIN_ALPHA, 1f);
        if (fade <= 0f)
            return;

        float r = e.radius();
        float th = Math.max(0.02f, e.thickness()); // 视觉厚度由“圈层叠加”体现

        float phaseDeg = (float) Math.toDegrees(e.omega() * age);

        VertexConsumer vc = buf.getBuffer(RenderType.entityTranslucent(TEX));
        int light = LightTexture.FULL_BRIGHT, ov = OverlayTexture.NO_OVERLAY;

        pose.pushPose();
        // 轻微仰角 + 围绕Y旋转
        pose.mulPose(Axis.XP.rotationDegrees(-6f));
        pose.mulPose(Axis.YP.rotationDegrees(phaseDeg));

        // 基准缩放：让贴图“中径”对应世界半径 r（作为最外层）
        float baseScale = r / R_MID_UV;
        pose.scale(baseScale, 1f, baseScale);
        // 基准缩放：让贴图“中径”对应世界半径 r（作为最外层）
        java.util.ArrayList<Float> centers = new java.util.ArrayList<>(MAX_INNER_LAYERS);
        for (float rc = r; rc >= r - th - 1e-4f && centers.size() < MAX_INNER_LAYERS; ) {
            centers.add(rc);

            // 该层相对外层的缩放（= rc / r，防止除0）
            float layerScaleRatio = Math.max(0.05f, rc / Math.max(0.05f, r));
            float halfWorld = R_HALF_T_UV * baseScale * layerScaleRatio;
            float step = Math.max(MIN_STEP_WORLD, STEP_OVERLAP * (2f * halfWorld)); // 恒定重叠率
            rc -= step;
        }

        // 保险：若没覆盖到最里边界，再补一层“最内中心”
        if (centers.isEmpty() || centers.get(centers.size() - 1) > r - th + 1e-3f){
            centers.add(Math.max(r - th, 0.05f));
        }

        // —— 上色参数（保留你的方式）——
        int colA = e.colorA(), colB = e.colorB();
        SlashOrbitEntity.GradMode mode = e.gradMode();
        boolean useHSV = e.useHSV();
        float hueRoll = e.hueShiftSpd() * (e.tickCount + pt);

        // —— 按“外→内”绘制（半透明排序更稳）——
        for (float rCenter : centers) {
            float layerScaleRatio = Math.max(0.05f, (rCenter / R_MID_UV) / baseScale);  // 相对最外层的缩放
            pose.pushPose();
            pose.scale(layerScaleRatio, 1f, layerScaleRatio);

            // —— 你的细分四边形绘制保持不变（仅把 m/n 与 alpha 换成当前层的）—— //
            Matrix4f m = pose.last().pose();
            Matrix3f n = pose.last().normal();

            for (int iu = 0; iu < U_SLICES; iu++) {
                float u0 = (float) iu / U_SLICES;
                float u1 = (float) (iu + 1) / U_SLICES;
                for (int iv = 0; iv < V_SLICES; iv++) {
                    float v0 = (float) iv / V_SLICES;
                    float v1 = (float) (iv + 1) / V_SLICES;

                    // 径向参数（贴图中心归一半径→映射到 [R_IN_UV,R_OUT_UV]）
                    float radial00 = radial01FromUV(u0, v1);
                    float radial10 = radial01FromUV(u1, v1);
                    float radial11 = radial01FromUV(u1, v0);
                    float radial01 = radial01FromUV(u0, v0);

                    // 颜色渐变（完全保留原逻辑）
                    int c00 = mix(colA, colB, gradT(mode, u0, radial00, hueRoll), useHSV);
                    int c10 = mix(colA, colB, gradT(mode, u1, radial10, hueRoll), useHSV);
                    int c11 = mix(colA, colB, gradT(mode, u1, radial11, hueRoll), useHSV);
                    int c01 = mix(colA, colB, gradT(mode, u0, radial01, hueRoll), useHSV);

                    // 顶点位置（单位方 [0,1]^2 → [-1,1]^2；v 轴反向）
                    float x0 = Mth.lerp(u0, -1f, 1f);
                    float x1 = Mth.lerp(u1, -1f, 1f);
                    float z0 = Mth.lerp(1f - v1, -1f, 1f);
                    float z1 = Mth.lerp(1f - v0, -1f, 1f);

                    // 两个三角（正面）
                    putARGB(vc, m, n, x0, 0, z0, u0, v1, c00, fade, light, ov);
                    putARGB(vc, m, n, x1, 0, z0, u1, v1, c10, fade, light, ov);
                    putARGB(vc, m, n, x1, 0, z1, u1, v0, c11, fade, light, ov);

                    putARGB(vc, m, n, x0, 0, z0, u0, v1, c00, fade, light, ov);
                    putARGB(vc, m, n, x1, 0, z1, u1, v0, c11, fade, light, ov);
                    putARGB(vc, m, n, x0, 0, z1, u0, v0, c01, fade, light, ov);
                }
            }

            pose.popPose();
        }

        pose.popPose();
    }


    private static void putARGB(
            VertexConsumer vc, Matrix4f m, Matrix3f n,
            float x, float y, float z, float u, float v,
            int argb, float mulA, int light, int ov) {
        float a = ((argb >>> 24) & 255) / 255f * mulA;
        float r = ((argb >> 16) & 255) / 255f;
        float g = ((argb >> 8) & 255) / 255f;
        float b = ((argb) & 255) / 255f;
        vc.vertex(m, x, y, z).color(r, g, b, a).uv(u, v).overlayCoords(ov).uv2(light).normal(n, 0, 1, 0).endVertex();
    }

    // 将 UV -> 以 (0.5,0.5) 为中心的“归一半径”，再映射到贴图弧带的 [R_IN_UV, R_OUT_UV] → 0..1
    private static float radial01FromUV(float u, float v) {
        float du = u - 0.5f, dv = v - 0.5f;
        float rad = Mth.clamp((float) Math.sqrt(du * du + dv * dv) / 0.5f, 0f, 1f); // 0..1
        return Mth.clamp((rad - R_IN_UV) / (R_OUT_UV - R_IN_UV), 0f, 1f);
    }

    // === 渐变：与之前保持一致（可复用你已有版本） ==========================
    private static float gradT(SlashOrbitEntity.GradMode mode, float alongArc01, float radial01, float hueRoll) {
        return switch (mode) {
            case SOLID -> wrap01(hueRoll);               // 如不想随时间滚动可返回 0
            case ANGULAR -> wrap01(alongArc01 + hueRoll);  // 沿弧角（此处用 u 近似）
            case LENGTH -> wrap01(alongArc01);            // 与 ANGULAR 同义
            case RADIAL -> wrap01(radial01 + hueRoll);    // 内→外
            case TIME_RAINBOW -> wrap01(hueRoll);               // 纯时间
        };
    }

    private static float wrap01(float v) {
        v %= 1f;
        if (v < 0)
            v += 1f;
        return v;
    }

    private static int mix(int a, int b, float t, boolean hsv) {
        t = Mth.clamp(t, 0f, 1f);
        return hsv ? lerpHSV_ARGB(a, b, t) : lerpRGB_ARGB(a, b, t);
    }

    private static int lerpRGB_ARGB(int a, int b, float t) {
        int aa = (int) Mth.lerp(t, (a >>> 24) & 255, (b >>> 24) & 255);
        int rr = (int) Mth.lerp(t, (a >> 16) & 255, (b >> 16) & 255);
        int gg = (int) Mth.lerp(t, (a >> 8) & 255, (b >> 8) & 255);
        int bb = (int) Mth.lerp(t, a & 255, b & 255);
        return (aa << 24) | (rr << 16) | (gg << 8) | bb;
    }

    private static int lerpHSV_ARGB(int a, int b, float t) {
        float[] ah = rgbToHsv(((a >> 16) & 255) / 255f, ((a >> 8) & 255) / 255f, (a & 255) / 255f);
        float[] bh = rgbToHsv(((b >> 16) & 255) / 255f, ((b >> 8) & 255) / 255f, (b & 255) / 255f);
        float dh = (((bh[0] - ah[0] + 1f) + 0.5f) % 1f) - 0.5f;  // 最近环向差
        float h = (ah[0] + dh * t + 1f) % 1f;
        float s = Mth.lerp(t, ah[1], bh[1]);
        float v = Mth.lerp(t, ah[2], bh[2]);
        int rgb = hsvToRgb(h, s, v);
        int aa = (int) Mth.lerp(t, (a >>> 24) & 255, (b >>> 24) & 255);
        return (aa << 24) | rgb;
    }

    private static float[] rgbToHsv(float r, float g, float b) {
        float max = Math.max(r, Math.max(g, b)), min = Math.min(r, Math.min(g, b)), d = max - min;
        float h = 0f;
        if (d > 1e-6){
            if (max == r)
                h = (g - b) / d % 6f;
            else if (max == g)
                h = (b - r) / d + 2f;
            else
                h = (r - g) / d + 4f;
            h /= 6f;
            if (h < 0)
                h += 1f;
        }
        float s = max == 0 ? 0 : d / max;
        return new float[]{h, s, max};
    }

    private static int hsvToRgb(float h, float s, float v) {
        float i = (float) Math.floor(h * 6f), f = h * 6f - i;
        float p = v * (1 - s), q = v * (1 - f * s), t = v * (1 - (1 - f) * s);
        float R, G, B;
        switch (((int) i) % 6) {
            case 0 -> {
                R = v;
                G = t;
                B = p;
            }
            case 1 -> {
                R = q;
                G = v;
                B = p;
            }
            case 2 -> {
                R = p;
                G = v;
                B = t;
            }
            case 3 -> {
                R = p;
                G = q;
                B = v;
            }
            case 4 -> {
                R = t;
                G = p;
                B = v;
            }
            default -> {
                R = v;
                G = p;
                B = q;
            }
        }
        return ((int) (R * 255 + 0.5) << 16) | ((int) (G * 255 + 0.5) << 8) | ((int) (B * 255 + 0.5));
    }
}
