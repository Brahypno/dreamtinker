package org.brahypno.dreamtinker.library.client.entity;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.Entity.SlashOrbitEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class SlashOrbitRenderer extends EntityRenderer<SlashOrbitEntity> {
    private static final ResourceLocation TEX =
            Dreamtinker.getLocation("textures/entity/slash_blade.png");
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEX);

    private static final float R_MID_UV = 0.70f;
    private static final float R_HALF_T_UV = 0.11f;
    private static final float R_IN_UV = R_MID_UV - R_HALF_T_UV;
    private static final float R_OUT_UV = R_MID_UV + R_HALF_T_UV;

    private static final float MIN_STEP_WORLD = 0.01f;
    private static final float DENSITY = 1.65f;
    private static final float MIN_ALPHA = 0.72f;
    private static final float STEP_OVERLAP = 0.42f;

    /**
     * SOLID/TIME_RAINBOW: 1 quad/layer
     * ANGULAR/LENGTH: ANGULAR_SLICES quads/layer
     * RADIAL: RADIAL_U_SLICES * RADIAL_V_SLICES quads/layer
     */
    private static final int ANGULAR_SLICES = 32;
    private static final int RADIAL_U_SLICES = 32;
    private static final int RADIAL_V_SLICES = 8;
    private static final int MAX_INNER_LAYERS = 16;

    private static final int RADIAL_STRIDE = RADIAL_V_SLICES + 1;
    private static final float[] RADIAL_T =
            createRadialParameters();

    /**
     * EntityRenderer is used on the render thread, so these scratch buffers can
     * be reused without allocating arrays for every entity and every frame.
     */
    private final int[] angularColors = new int[ANGULAR_SLICES + 1];
    private final int[] radialColors =
            new int[(RADIAL_U_SLICES + 1) * RADIAL_STRIDE];

    private int mixColorA;
    private int mixColorB;
    private boolean mixHsv;
    private float hueA;
    private float saturationA;
    private float valueA;
    private float hueB;
    private float saturationB;
    private float valueB;

    public SlashOrbitRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(SlashOrbitEntity entity) {
        return TEX;
    }

    @Override
    public void render(
            SlashOrbitEntity entity, float yaw, float partialTick,
            PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float age = entity.tickCount + partialTick;
        float life = Math.max(1f, entity.life());
        if (age >= life){
            return;
        }

        float fade = Mth.clamp((1f - age / life) * DENSITY, MIN_ALPHA, 1f);
        float radius = entity.radius();
        float thickness = Math.max(0.02f, entity.thickness());
        int layerCount = getLayerCount(radius, thickness);
        float innerRadius = Math.max(0.05f, radius - thickness);

        prepareColorMixer(entity.colorA(), entity.colorB(), entity.useHSV());

        SlashOrbitEntity.GradMode mode = entity.gradMode();
        float hueRoll = entity.hueShiftSpd() * age;
        if (mode == SlashOrbitEntity.GradMode.ANGULAR
            || mode == SlashOrbitEntity.GradMode.LENGTH){
            prepareAngularColors(mode, hueRoll);
        }else if (mode == SlashOrbitEntity.GradMode.RADIAL){
            prepareRadialColors(hueRoll);
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(-6f));
        poseStack.mulPose(Axis.YP.rotationDegrees(
                (float) Math.toDegrees(entity.omega() * age)));

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        VertexConsumer consumer = bufferSource.getBuffer(RENDER_TYPE);

        for (int layer = 0; layer < layerCount; layer++) {
            float layerT = layerCount == 1
                           ? 0f
                           : (float) layer / (layerCount - 1);
            float layerRadius = Mth.lerp(layerT, radius, innerRadius);
            float scale = layerRadius / R_MID_UV;

            switch (mode) {
                case SOLID -> renderSolidLayer(
                        consumer, pose, normal, scale, mixColorA, fade);
                case TIME_RAINBOW -> renderSolidLayer(
                        consumer, pose, normal, scale,
                        mixPrepared(wrap01(hueRoll)), fade);
                case ANGULAR, LENGTH -> renderAngularLayer(
                        consumer, pose, normal, scale, fade);
                case RADIAL -> renderRadialLayer(
                        consumer, pose, normal, scale, fade);
            }
        }

        poseStack.popPose();
    }

    private static int getLayerCount(float radius, float thickness) {
        float baseScale = radius / R_MID_UV;
        float innerRadius = Math.max(0.05f, radius - thickness);
        float visibleBandWorld =
                Math.max(0.02f, (R_OUT_UV - R_IN_UV) * baseScale);
        float stepWorld =
                Math.max(MIN_STEP_WORLD, visibleBandWorld * STEP_OVERLAP);
        return Mth.clamp(
                (int) Math.ceil((radius - innerRadius) / stepWorld) + 1,
                1, MAX_INNER_LAYERS);
    }

    private void prepareAngularColors(
            SlashOrbitEntity.GradMode mode, float hueRoll) {
        for (int i = 0; i <= ANGULAR_SLICES; i++) {
            float alongArc = (float) i / ANGULAR_SLICES;
            float gradient = mode == SlashOrbitEntity.GradMode.ANGULAR
                             ? wrap01(alongArc + hueRoll)
                             : wrap01(alongArc);
            angularColors[i] = mixPrepared(gradient);
        }
    }

    private void prepareRadialColors(float hueRoll) {
        for (int i = 0; i < RADIAL_T.length; i++) {
            radialColors[i] = mixPrepared(wrap01(RADIAL_T[i] + hueRoll));
        }
    }

    private static void renderSolidLayer(
            VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
            float scale, int color, float alpha) {
        putQuad(
                consumer, pose, normal, scale,
                0f, 1f, 0f, 1f,
                color, color, color, color, alpha);
    }

    private void renderAngularLayer(
            VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
            float scale, float alpha) {
        for (int uIndex = 0; uIndex < ANGULAR_SLICES; uIndex++) {
            float u0 = (float) uIndex / ANGULAR_SLICES;
            float u1 = (float) (uIndex + 1) / ANGULAR_SLICES;
            int color0 = angularColors[uIndex];
            int color1 = angularColors[uIndex + 1];

            putQuad(
                    consumer, pose, normal, scale,
                    u0, u1, 0f, 1f,
                    color0, color1, color1, color0, alpha);
        }
    }

    private void renderRadialLayer(
            VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
            float scale, float alpha) {
        for (int uIndex = 0; uIndex < RADIAL_U_SLICES; uIndex++) {
            float u0 = (float) uIndex / RADIAL_U_SLICES;
            float u1 = (float) (uIndex + 1) / RADIAL_U_SLICES;

            for (int vIndex = 0; vIndex < RADIAL_V_SLICES; vIndex++) {
                float v0 = (float) vIndex / RADIAL_V_SLICES;
                float v1 = (float) (vIndex + 1) / RADIAL_V_SLICES;

                int lowerLeft = radialIndex(uIndex, vIndex + 1);
                int lowerRight = radialIndex(uIndex + 1, vIndex + 1);
                int upperRight = radialIndex(uIndex + 1, vIndex);
                int upperLeft = radialIndex(uIndex, vIndex);

                putQuad(
                        consumer, pose, normal, scale,
                        u0, u1, v0, v1,
                        radialColors[lowerLeft],
                        radialColors[lowerRight],
                        radialColors[upperRight],
                        radialColors[upperLeft],
                        alpha);
            }
        }
    }

    private static void putQuad(
            VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
            float scale, float u0, float u1, float v0, float v1,
            int color00, int color10, int color11, int color01,
            float alpha) {
        float x0 = (u0 * 2f - 1f) * scale;
        float x1 = (u1 * 2f - 1f) * scale;
        float z0 = (1f - v1 * 2f) * scale;
        float z1 = (1f - v0 * 2f) * scale;

        putVertex(consumer, pose, normal, x0, z0, u0, v1, color00, alpha);
        putVertex(consumer, pose, normal, x1, z0, u1, v1, color10, alpha);
        putVertex(consumer, pose, normal, x1, z1, u1, v0, color11, alpha);
        putVertex(consumer, pose, normal, x0, z1, u0, v0, color01, alpha);
    }

    private static void putVertex(
            VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
            float x, float z, float u, float v, int argb, float alpha) {
        float vertexAlpha = ((argb >>> 24) & 255) / 255f * alpha;
        float red = ((argb >>> 16) & 255) / 255f;
        float green = ((argb >>> 8) & 255) / 255f;
        float blue = (argb & 255) / 255f;

        consumer.vertex(pose, x, 0f, z)
                .color(red, green, blue, vertexAlpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, 0f, 1f, 0f)
                .endVertex();
    }

    private static float[] createRadialParameters() {
        float[] result =
                new float[(RADIAL_U_SLICES + 1) * RADIAL_STRIDE];

        for (int uIndex = 0; uIndex <= RADIAL_U_SLICES; uIndex++) {
            float u = (float) uIndex / RADIAL_U_SLICES;
            for (int vIndex = 0; vIndex <= RADIAL_V_SLICES; vIndex++) {
                float v = (float) vIndex / RADIAL_V_SLICES;
                result[radialIndex(uIndex, vIndex)] =
                        radial01FromUv(u, v);
            }
        }
        return result;
    }

    private static int radialIndex(int uIndex, int vIndex) {
        return uIndex * RADIAL_STRIDE + vIndex;
    }

    private static float radial01FromUv(float u, float v) {
        float du = u - 0.5f;
        float dv = v - 0.5f;
        float normalizedRadius =
                Mth.clamp((float) Math.sqrt(du * du + dv * dv) / 0.5f, 0f, 1f);
        return Mth.clamp(
                (normalizedRadius - R_IN_UV) / (R_OUT_UV - R_IN_UV),
                0f, 1f);
    }

    private void prepareColorMixer(int colorA, int colorB, boolean hsv) {
        mixColorA = colorA;
        mixColorB = colorB;
        mixHsv = hsv && colorA != colorB;

        if (mixHsv){
            decodeHsv(colorA, true);
            decodeHsv(colorB, false);
        }
    }

    private void decodeHsv(int argb, boolean first) {
        float red = ((argb >>> 16) & 255) / 255f;
        float green = ((argb >>> 8) & 255) / 255f;
        float blue = (argb & 255) / 255f;
        float max = Math.max(red, Math.max(green, blue));
        float min = Math.min(red, Math.min(green, blue));
        float delta = max - min;

        float hue = 0f;
        if (delta > 1.0E-6f){
            if (max == red){
                hue = ((green - blue) / delta) % 6f;
            }else if (max == green){
                hue = (blue - red) / delta + 2f;
            }else {
                hue = (red - green) / delta + 4f;
            }
            hue /= 6f;
            if (hue < 0f){
                hue += 1f;
            }
        }

        float saturation = max == 0f ? 0f : delta / max;
        if (first){
            hueA = hue;
            saturationA = saturation;
            valueA = max;
        }else {
            hueB = hue;
            saturationB = saturation;
            valueB = max;
        }
    }

    private int mixPrepared(float t) {
        t = Mth.clamp(t, 0f, 1f);
        if (mixColorA == mixColorB || t <= 0f){
            return mixColorA;
        }
        if (t >= 1f){
            return mixColorB;
        }
        return mixHsv ? mixHsv(t) : mixRgb(t);
    }

    private int mixRgb(float t) {
        int alpha = lerpChannel(
                (mixColorA >>> 24) & 255, (mixColorB >>> 24) & 255, t);
        int red = lerpChannel(
                (mixColorA >>> 16) & 255, (mixColorB >>> 16) & 255, t);
        int green = lerpChannel(
                (mixColorA >>> 8) & 255, (mixColorB >>> 8) & 255, t);
        int blue = lerpChannel(
                mixColorA & 255, mixColorB & 255, t);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private int mixHsv(float t) {
        float hueDelta = hueB - hueA;
        if (hueDelta > 0.5f){
            hueDelta -= 1f;
        }else if (hueDelta < -0.5f){
            hueDelta += 1f;
        }

        float hue = wrap01(hueA + hueDelta * t);
        float saturation = Mth.lerp(t, saturationA, saturationB);
        float value = Mth.lerp(t, valueA, valueB);
        int rgb = hsvToRgb(hue, saturation, value);
        int alpha = lerpChannel(
                (mixColorA >>> 24) & 255, (mixColorB >>> 24) & 255, t);
        return alpha << 24 | rgb;
    }

    private static int lerpChannel(int from, int to, float t) {
        return Mth.clamp((int) (from + (to - from) * t + 0.5f), 0, 255);
    }

    private static int hsvToRgb(float hue, float saturation, float value) {
        float sector = hue * 6f;
        int index = Mth.floor(sector) % 6;
        float fraction = sector - Mth.floor(sector);
        float p = value * (1f - saturation);
        float q = value * (1f - fraction * saturation);
        float t = value * (1f - (1f - fraction) * saturation);

        float red;
        float green;
        float blue;
        switch (index) {
            case 0 -> {
                red = value;
                green = t;
                blue = p;
            }
            case 1 -> {
                red = q;
                green = value;
                blue = p;
            }
            case 2 -> {
                red = p;
                green = value;
                blue = t;
            }
            case 3 -> {
                red = p;
                green = q;
                blue = value;
            }
            case 4 -> {
                red = t;
                green = p;
                blue = value;
            }
            default -> {
                red = value;
                green = p;
                blue = q;
            }
        }

        int packedRed = Mth.clamp((int) (red * 255f + 0.5f), 0, 255);
        int packedGreen = Mth.clamp((int) (green * 255f + 0.5f), 0, 255);
        int packedBlue = Mth.clamp((int) (blue * 255f + 0.5f), 0, 255);
        return packedRed << 16 | packedGreen << 8 | packedBlue;
    }

    private static float wrap01(float value) {
        value %= 1f;
        return value < 0f ? value + 1f : value;
    }
}
