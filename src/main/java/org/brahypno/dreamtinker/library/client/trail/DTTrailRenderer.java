package org.brahypno.dreamtinker.library.client.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.brahypno.dreamtinker.config.DreamtinkerClientConfig;
import org.brahypno.dreamtinker.library.client.DTRenderTypes;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

public final class DTTrailRenderer {
    private static final double EPSILON = 1.0E-7D;
    private static final ThreadLocal<Workspace> WORKSPACE = ThreadLocal.withInitial(Workspace::new);

    private DTTrailRenderer() {}

    public record Layer(ResourceLocation texture, float halfWidth, float alphaMultiplier, int radialPlanes, float planeAlpha) {}

    /**
     * All layers share point interpolation, fade ages, LOD selection and frames.
     */
    public static void renderEntityTrailLayers(
            PoseStack pose, MultiBufferSource buffer, Entity entity, float partialTicks,
            DTClientTrail trail, int argb, Layer[] layers) {
        Workspace workspace = WORKSPACE.get();
        int count = prepare(workspace, trail, partialTicks);
        if (count < 2)
            return;
        for (Layer layer : layers) {
            fillStyle(workspace, count, layer.halfWidth(), layer.alphaMultiplier(), argb);
            renderPrepared(pose, buffer, entity, partialTicks, workspace, count,
                           layer.texture(), layer.radialPlanes(), layer.planeAlpha());
        }
    }

    public static void renderEntityTrail(
            PoseStack pose, MultiBufferSource buffer, Entity entity, float partialTicks,
            DTClientTrail trail, ResourceLocation texture, int argb, float halfWidth,
            float alphaMultiplier) {
        renderEntityTrailVolume(
                pose, buffer, entity, partialTicks, trail, texture, argb,
                halfWidth, alphaMultiplier, 3, 0.42F
        );
    }

    public static void renderEntityTrailVolume(
            PoseStack pose, MultiBufferSource buffer, Entity entity, float partialTicks,
            DTClientTrail trail, ResourceLocation texture, int argb, float halfWidth,
            float alphaMultiplier, int radialPlanes, float planeAlpha) {
        Workspace workspace = WORKSPACE.get();
        int count = prepare(workspace, trail, partialTicks);
        if (count < 2)
            return;
        fillStyle(workspace, count, halfWidth, alphaMultiplier, argb);
        renderPrepared(pose, buffer, entity, partialTicks, workspace, count, texture, radialPlanes, planeAlpha);
    }

    private static int prepare(Workspace workspace, DTClientTrail trail, float partialTicks) {
        List<DTClientTrail.Point> points = trail.points();
        int originalCount = points.size();
        if (originalCount < 2)
            return 0;
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        int step = sampleStep(trail.bounds(), camera);
        int count = (originalCount - 2 + step) / step + 1;
        workspace.ensureCapacity(count);
        fillPoints(workspace, points, count, originalCount, step, partialTicks, trail);
        buildFrames(workspace, count);
        return count;
    }

    private static int sampleStep(AABB bounds, Vec3 camera) {
        if (!DreamtinkerClientConfig.TRAIL_DISTANCE_LOD.get() || bounds == null)
            return 1;
        double dx = Math.max(0.0D, Math.max(bounds.minX - camera.x, camera.x - bounds.maxX));
        double dy = Math.max(0.0D, Math.max(bounds.minY - camera.y, camera.y - bounds.maxY));
        double dz = Math.max(0.0D, Math.max(bounds.minZ - camera.z, camera.z - bounds.maxZ));
        double distanceSqr = dx * dx + dy * dy + dz * dz;
        return distanceSqr <= 32.0D * 32.0D ? 1 : distanceSqr <= 64.0D * 64.0D ? 2 : 4;
    }

    private static void renderPrepared(
            PoseStack pose, MultiBufferSource buffer, Entity entity, float partialTicks,
            Workspace workspace, int count, ResourceLocation texture, int radialPlanes, float planeAlpha) {
        double originX = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double originY = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double originZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

        pose.pushPose();
        pose.translate(-originX, -originY, -originZ);
        VertexConsumer consumer = buffer.getBuffer(DTRenderTypes.additiveTrail(texture));
        Matrix4f matrix = pose.last().pose();
        Matrix3f normal = pose.last().normal();

        int planes = Math.max(2, radialPlanes);
        double halfStep = Math.PI / (planes * 2.0D);
        for (int plane = 0; plane < planes; plane++) {
            double angle = halfStep + Math.PI * plane / planes;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            for (int i = 0; i < count - 1; i++) {
                int next = i + 1;
                double dx = workspace.x[next] - workspace.x[i];
                double dy = workspace.y[next] - workspace.y[i];
                double dz = workspace.z[next] - workspace.z[i];
                if (dx * dx + dy * dy + dz * dz < EPSILON){
                    continue;
                }

                double side0X = (workspace.axisAX[i] * cos + workspace.axisBX[i] * sin) * workspace.width[i];
                double side0Y = (workspace.axisAY[i] * cos + workspace.axisBY[i] * sin) * workspace.width[i];
                double side0Z = (workspace.axisAZ[i] * cos + workspace.axisBZ[i] * sin) * workspace.width[i];
                double side1X = (workspace.axisAX[next] * cos + workspace.axisBX[next] * sin) * workspace.width[next];
                double side1Y = (workspace.axisAY[next] * cos + workspace.axisBY[next] * sin) * workspace.width[next];
                double side1Z = (workspace.axisAZ[next] * cos + workspace.axisBZ[next] * sin) * workspace.width[next];

                int color0 = multiplyAlpha(workspace.color[i], planeAlpha);
                int color1 = multiplyAlpha(workspace.color[next], planeAlpha);
                float v0 = workspace.v[i];
                float v1 = workspace.v[next];
                drawSegment(
                        consumer, matrix, normal,
                        workspace.x[i], workspace.y[i], workspace.z[i],
                        workspace.x[next], workspace.y[next], workspace.z[next],
                        side0X, side0Y, side0Z,
                        side1X, side1Y, side1Z,
                        color0, color1, v0, v1
                );
            }
        }
        pose.popPose();
    }

    private static void fillPoints(
            Workspace workspace, List<DTClientTrail.Point> points, int count, int originalCount, int step,
            float partialTicks, DTClientTrail trail) {
        for (int i = 0; i < count; i++) {
            int sourceIndex = Math.min(i * step, originalCount - 1);
            DTClientTrail.Point point = points.get(sourceIndex);
            workspace.v[i] = sourceIndex / (float) (originalCount - 1);
            workspace.x[i] = point.getX(partialTicks);
            workspace.y[i] = point.getY(partialTicks);
            workspace.z[i] = point.getZ(partialTicks);
            workspace.axisAX[i] = point.axisX();
            workspace.axisAY[i] = point.axisY();
            workspace.axisAZ[i] = point.axisZ();
            workspace.fade[i] = 1.0F - Mth.clamp(
                    (point.getAge() - 1.0F + partialTicks) / trail.lifespan(), 0.0F, 1.0F
            );
        }
    }

    private static void fillStyle(Workspace workspace, int count, float halfWidth, float alphaMultiplier, int argb) {
        int color = normalizeArgb(argb, 0xFFFFFFFF);
        for (int i = 0; i < count; i++) {
            float fade = workspace.fade[i];
            workspace.width[i] = halfWidth * fade;
            workspace.color[i] = multiplyAlpha(color, alphaMultiplier * fade * fade);
        }
    }

    private static void buildFrames(Workspace workspace, int count) {
        for (int i = 0; i < count; i++) {
            int previous = Math.max(0, i - 1);
            int next = Math.min(count - 1, i + 1);
            double tangentX = workspace.x[next] - workspace.x[previous];
            double tangentY = workspace.y[next] - workspace.y[previous];
            double tangentZ = workspace.z[next] - workspace.z[previous];
            double tangentLengthSqr = tangentX * tangentX + tangentY * tangentY + tangentZ * tangentZ;
            if (tangentLengthSqr < EPSILON){
                tangentX = 0.0D;
                tangentY = 1.0D;
                tangentZ = 0.0D;
            }else {
                double inverseLength = 1.0D / Math.sqrt(tangentLengthSqr);
                tangentX *= inverseLength;
                tangentY *= inverseLength;
                tangentZ *= inverseLength;
            }

            // Transported sample axes keep the cross-section independent of the camera and LOD.
            double projection = workspace.axisAX[i] * tangentX + workspace.axisAY[i] * tangentY + workspace.axisAZ[i] * tangentZ;
            double axisAX = workspace.axisAX[i] - tangentX * projection;
            double axisAY = workspace.axisAY[i] - tangentY * projection;
            double axisAZ = workspace.axisAZ[i] - tangentZ * projection;
            double axisALengthSqr = axisAX * axisAX + axisAY * axisAY + axisAZ * axisAZ;
            if (axisALengthSqr < EPSILON || !finite(axisAX, axisAY, axisAZ)){
                if (Math.abs(tangentY) < 0.9D){
                    axisAX = -tangentZ;
                    axisAY = 0.0D;
                    axisAZ = tangentX;
                }else {
                    axisAX = 0.0D;
                    axisAY = tangentZ;
                    axisAZ = -tangentY;
                }
                axisALengthSqr = axisAX * axisAX + axisAY * axisAY + axisAZ * axisAZ;
            }
            double inverseAxisALength = axisALengthSqr < EPSILON ? 1.0D : 1.0D / Math.sqrt(axisALengthSqr);
            axisAX *= inverseAxisALength;
            axisAY *= inverseAxisALength;
            axisAZ *= inverseAxisALength;

            double axisBX = tangentY * axisAZ - tangentZ * axisAY;
            double axisBY = tangentZ * axisAX - tangentX * axisAZ;
            double axisBZ = tangentX * axisAY - tangentY * axisAX;
            double axisBLengthSqr = axisBX * axisBX + axisBY * axisBY + axisBZ * axisBZ;
            if (axisBLengthSqr < EPSILON || !finite(axisBX, axisBY, axisBZ)){
                axisBX = 1.0D;
                axisBY = 0.0D;
                axisBZ = 0.0D;
            }else {
                double inverseAxisBLength = 1.0D / Math.sqrt(axisBLengthSqr);
                axisBX *= inverseAxisBLength;
                axisBY *= inverseAxisBLength;
                axisBZ *= inverseAxisBLength;
            }

            workspace.axisAX[i] = axisAX;
            workspace.axisAY[i] = axisAY;
            workspace.axisAZ[i] = axisAZ;
            workspace.axisBX[i] = axisBX;
            workspace.axisBY[i] = axisBY;
            workspace.axisBZ[i] = axisBZ;
        }
    }

    private static void drawSegment(
            VertexConsumer consumer, Matrix4f matrix, Matrix3f normal,
            double x0, double y0, double z0, double x1, double y1, double z1,
            double side0X, double side0Y, double side0Z,
            double side1X, double side1Y, double side1Z,
            int color0, int color1, float v0, float v1) {
        vertex(consumer, matrix, normal, x0 + side0X, y0 + side0Y, z0 + side0Z, color0, 0.0F, v0);
        vertex(consumer, matrix, normal, x0 - side0X, y0 - side0Y, z0 - side0Z, color0, 1.0F, v0);
        vertex(consumer, matrix, normal, x1 - side1X, y1 - side1Y, z1 - side1Z, color1, 1.0F, v1);
        vertex(consumer, matrix, normal, x1 + side1X, y1 + side1Y, z1 + side1Z, color1, 0.0F, v1);
    }

    private static void vertex(
            VertexConsumer consumer, Matrix4f matrix, Matrix3f normal,
            double x, double y, double z, int argb, float u, float v) {
        consumer.vertex(matrix, (float) x, (float) y, (float) z)
                .color(argb >>> 16 & 255, argb >>> 8 & 255, argb & 255, argb >>> 24 & 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    public static int normalizeArgb(int argb, int fallback) {
        if (argb == 0)
            argb = fallback;
        if ((argb & 0xFF000000) == 0)
            argb |= 0xFF000000;
        return argb;
    }

    public static int multiplyAlpha(int argb, float multiplier) {
        int alpha = Mth.clamp((int) ((argb >>> 24 & 255) * multiplier), 0, 255);
        return alpha << 24 | argb & 0xFFFFFF;
    }

    private static boolean finite(double x, double y, double z) {
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z);
    }

    private static final class Workspace {
        private double[] x = new double[32];
        private double[] y = new double[32];
        private double[] z = new double[32];
        private double[] axisAX = new double[32];
        private double[] axisAY = new double[32];
        private double[] axisAZ = new double[32];
        private double[] axisBX = new double[32];
        private double[] axisBY = new double[32];
        private double[] axisBZ = new double[32];
        private float[] width = new float[32];
        private int[] color = new int[32];
        private float[] fade = new float[32];
        private float[] v = new float[32];

        private void ensureCapacity(int required) {
            if (required <= x.length)
                return;
            int capacity = x.length;
            while (capacity < required)
                capacity <<= 1;
            x = new double[capacity];
            y = new double[capacity];
            z = new double[capacity];
            axisAX = new double[capacity];
            axisAY = new double[capacity];
            axisAZ = new double[capacity];
            axisBX = new double[capacity];
            axisBY = new double[capacity];
            axisBZ = new double[capacity];
            width = new float[capacity];
            color = new int[capacity];
            fade = new float[capacity];
            v = new float[capacity];
        }
    }

    public static void clearWorkspace() {
        WORKSPACE.remove();
    }
}
