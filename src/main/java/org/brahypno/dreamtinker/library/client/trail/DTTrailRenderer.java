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
import net.minecraft.world.phys.Vec3;
import org.brahypno.dreamtinker.library.client.DTRenderTypes;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

public final class DTTrailRenderer {
    private static final double EPSILON = 1.0E-7D;
    private static final double TWIST_STEP = 0.37D;
    private static final double TWIST_COS = Math.cos(TWIST_STEP);
    private static final double TWIST_SIN = Math.sin(TWIST_STEP);
    private static final ThreadLocal<Workspace> WORKSPACE = ThreadLocal.withInitial(Workspace::new);

    private DTTrailRenderer() {}

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
        List<DTClientTrail.Point> points = trail.points();
        int count = points.size();
        if (count < 2)
            return;

        Workspace workspace = WORKSPACE.get();
        workspace.ensureCapacity(count);
        fillPoints(workspace, points, count, partialTicks, trail, halfWidth, alphaMultiplier, argb);

        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        buildFrames(workspace, count, camera.x, camera.y, camera.z);

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
            double cos0 = Math.cos(angle);
            double sin0 = Math.sin(angle);

            for (int i = 0; i < count - 1; i++) {
                int next = i + 1;
                double dx = workspace.x[next] - workspace.x[i];
                double dy = workspace.y[next] - workspace.y[i];
                double dz = workspace.z[next] - workspace.z[i];
                if (dx * dx + dy * dy + dz * dz < EPSILON){
                    double nextCos = cos0 * TWIST_COS - sin0 * TWIST_SIN;
                    sin0 = sin0 * TWIST_COS + cos0 * TWIST_SIN;
                    cos0 = nextCos;
                    continue;
                }

                double cos1 = cos0 * TWIST_COS - sin0 * TWIST_SIN;
                double sin1 = sin0 * TWIST_COS + cos0 * TWIST_SIN;

                double side0X = (workspace.axisAX[i] * cos0 + workspace.axisBX[i] * sin0) * workspace.width[i];
                double side0Y = (workspace.axisAY[i] * cos0 + workspace.axisBY[i] * sin0) * workspace.width[i];
                double side0Z = (workspace.axisAZ[i] * cos0 + workspace.axisBZ[i] * sin0) * workspace.width[i];
                double side1X = (workspace.axisAX[next] * cos1 + workspace.axisBX[next] * sin1) * workspace.width[next];
                double side1Y = (workspace.axisAY[next] * cos1 + workspace.axisBY[next] * sin1) * workspace.width[next];
                double side1Z = (workspace.axisAZ[next] * cos1 + workspace.axisBZ[next] * sin1) * workspace.width[next];

                int color0 = multiplyAlpha(workspace.color[i], planeAlpha);
                int color1 = multiplyAlpha(workspace.color[next], planeAlpha);
                float v0 = i / (float) (count - 1);
                float v1 = next / (float) (count - 1);
                drawSegment(
                        consumer, matrix, normal,
                        workspace.x[i], workspace.y[i], workspace.z[i],
                        workspace.x[next], workspace.y[next], workspace.z[next],
                        side0X, side0Y, side0Z,
                        side1X, side1Y, side1Z,
                        color0, color1, v0, v1
                );

                cos0 = cos1;
                sin0 = sin1;
            }
        }
        pose.popPose();
    }

    private static void fillPoints(
            Workspace workspace, List<DTClientTrail.Point> points, int count, float partialTicks,
            DTClientTrail trail, float halfWidth, float alphaMultiplier, int argb) {
        int color = normalizeArgb(argb, 0xFFFFFFFF);
        for (int i = 0; i < count; i++) {
            DTClientTrail.Point point = points.get(i);
            workspace.x[i] = point.getX(partialTicks);
            workspace.y[i] = point.getY(partialTicks);
            workspace.z[i] = point.getZ(partialTicks);
            float fade = 1.0F - Mth.clamp(
                    (point.getAge() - partialTicks) / trail.lifespan(), 0.0F, 1.0F
            );
            workspace.width[i] = halfWidth * fade;
            workspace.color[i] = multiplyAlpha(color, alphaMultiplier * fade * fade);
        }
    }

    private static void buildFrames(
            Workspace workspace, int count, double cameraX, double cameraY, double cameraZ) {
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

            double toCameraX = workspace.x[i] - cameraX;
            double toCameraY = workspace.y[i] - cameraY;
            double toCameraZ = workspace.z[i] - cameraZ;
            if (toCameraX * toCameraX + toCameraY * toCameraY + toCameraZ * toCameraZ < EPSILON){
                toCameraX = 0.0D;
                toCameraY = 1.0D;
                toCameraZ = 0.0D;
            }

            double axisAX = toCameraY * tangentZ - toCameraZ * tangentY;
            double axisAY = toCameraZ * tangentX - toCameraX * tangentZ;
            double axisAZ = toCameraX * tangentY - toCameraY * tangentX;
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
        }
    }

    public static void clearWorkspace() {
        WORKSPACE.remove();
    }
}
