package org.dreamtinker.dreamtinker.library.client.trail;

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
import org.dreamtinker.dreamtinker.library.client.DTRenderTypes;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

public final class DTTrailRenderer {
    private DTTrailRenderer() {}

    public static void renderEntityTrail(
            PoseStack pose,
            MultiBufferSource buffer,
            Entity entity,
            float partialTicks,
            DTClientTrail trail,
            ResourceLocation texture,
            int argb,
            float halfWidth,
            float alphaMultiplier
    ) {
        renderEntityTrailVolume(
                pose,
                buffer,
                entity,
                partialTicks,
                trail,
                texture,
                argb,
                halfWidth,
                alphaMultiplier,
                3,
                0.42F
        );
    }

    public static void renderEntityTrailVolume(
            PoseStack pose, MultiBufferSource buffer, Entity entity, float partialTicks,
            DTClientTrail trail, ResourceLocation texture, int argb, float halfWidth,
            float alphaMultiplier, int radialPlanes, float planeAlpha
    ) {
        List<DTClientTrail.Point> points = trail.points();
        if (points.size() < 2)
            return;

        int color = normalizeArgb(argb, 0xFFFFFFFF);
        double ox = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double oy = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double oz = Mth.lerp(partialTicks, entity.zo, entity.getZ());
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        pose.pushPose();
        pose.translate(-ox, -oy, -oz);

        VertexConsumer vc = buffer.getBuffer(DTRenderTypes.additiveTrail(texture));
        Matrix4f matrix = pose.last().pose();
        Matrix3f normal = pose.last().normal();
        int count = points.size();
        Vec3[] worlds = new Vec3[count], axisA = new Vec3[count], axisB = new Vec3[count];
        float[] widths = new float[count];
        int[] colors = new int[count];

        for (int i = 0; i < count; i++) {
            DTClientTrail.Point p = points.get(i);
            worlds[i] = p.getPosition(partialTicks);
            float fade = 1.0F - Mth.clamp((p.getAge() - partialTicks) / trail.lifespan(), 0.0F, 1.0F);
            widths[i] = halfWidth * fade;
            colors[i] = multiplyAlpha(color, alphaMultiplier * fade * fade);
        }

        for (int i = 0; i < count; i++) {
            Vec3 prev = worlds[Math.max(0, i - 1)];
            Vec3 cur = worlds[i];
            Vec3 next = worlds[Math.min(count - 1, i + 1)];
            Vec3 tangent = getTangent(prev, cur, next);
            Vec3 a = getCameraFacingSide(tangent, cur, camera);
            Vec3 b = tangent.cross(a);
            if (b.lengthSqr() < 1.0E-7D)
                b = fallbackPerpendicular(tangent);
            axisA[i] = a.normalize();
            axisB[i] = b.normalize();
        }

        radialPlanes = Math.max(2, radialPlanes);
        float halfStep = (float) Math.PI / (radialPlanes * 2.0F);
        float twistStep = 0.37F;

        for (int plane = 0; plane < radialPlanes; plane++) {
            for (int i = 0; i < count - 1; i++) {
                Vec3 world0 = worlds[i], world1 = worlds[i + 1];
                if (world1.subtract(world0).lengthSqr() < 1.0E-7D)
                    continue;

                float a0 = halfStep + (float) Math.PI * plane / radialPlanes + i * twistStep;
                float a1 = halfStep + (float) Math.PI * plane / radialPlanes + (i + 1) * twistStep;
                Vec3 side0 = axisA[i].scale(Math.cos(a0)).add(axisB[i].scale(Math.sin(a0))).normalize().scale(widths[i]);
                Vec3 side1 = axisA[i + 1].scale(Math.cos(a1)).add(axisB[i + 1].scale(Math.sin(a1))).normalize().scale(widths[i + 1]);

                int c0 = multiplyAlpha(colors[i], planeAlpha);
                int c1 = multiplyAlpha(colors[i + 1], planeAlpha);
                float v0 = (float) i / (count - 1);
                float v1 = (float) (i + 1) / (count - 1);
                drawSegment(vc, matrix, normal, world0, world1, side0, side1, c0, c1, v0, v1);
            }
        }

        pose.popPose();
    }


    private static void vertex(
            VertexConsumer vc,
            Matrix4f matrix,
            Matrix3f normal,
            Vec3 pos,
            int argb,
            float u,
            float v
    ) {
        int a = (argb >>> 24) & 255;
        int r = (argb >>> 16) & 255;
        int g = (argb >>> 8) & 255;
        int b = argb & 255;

        vc.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
          .color(r, g, b, a)
          .uv(u, v)
          .overlayCoords(OverlayTexture.NO_OVERLAY)
          .uv2(LightTexture.FULL_BRIGHT)
          .normal(normal, 0, 1, 0)
          .endVertex();
    }

    public static int normalizeArgb(int argb, int fallback) {
        if (argb == 0){
            argb = fallback;
        }

        if ((argb & 0xFF000000) == 0){
            argb |= 0xFF000000;
        }

        return argb;
    }

    public static int multiplyAlpha(int argb, float multiplier) {
        int a = (argb >>> 24) & 255;
        int r = (argb >>> 16) & 255;
        int g = (argb >>> 8) & 255;
        int b = argb & 255;

        a = Mth.clamp((int) (a * multiplier), 0, 255);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static void drawSegment(
            VertexConsumer vc,
            Matrix4f matrix,
            Matrix3f normal,
            Vec3 world0,
            Vec3 world1,
            Vec3 side0,
            Vec3 side1,
            int c0,
            int c1,
            float v0,
            float v1
    ) {
        vertex(vc, matrix, normal, world0.add(side0), c0, 0.0F, v0);
        vertex(vc, matrix, normal, world0.subtract(side0), c0, 1.0F, v0);
        vertex(vc, matrix, normal, world1.subtract(side1), c1, 1.0F, v1);
        vertex(vc, matrix, normal, world1.add(side1), c1, 0.0F, v1);
    }

    private static Vec3 getTangent(Vec3 prev, Vec3 current, Vec3 next) {
        Vec3 tangent = next.subtract(prev);

        if (tangent.lengthSqr() < 1.0E-7D){
            tangent = next.subtract(current);
        }

        if (tangent.lengthSqr() < 1.0E-7D){
            tangent = current.subtract(prev);
        }

        if (tangent.lengthSqr() < 1.0E-7D){
            return new Vec3(0, 1, 0);
        }

        return tangent.normalize();
    }

    private static Vec3 getCameraFacingSide(Vec3 tangent, Vec3 current, Vec3 camera) {
        Vec3 toCamera = current.subtract(camera);

        if (toCamera.lengthSqr() < 1.0E-7D){
            toCamera = new Vec3(0, 1, 0);
        }

        Vec3 side = toCamera.cross(tangent);

        if (side.lengthSqr() < 1.0E-7D){
            side = fallbackPerpendicular(tangent);
        }

        if (!Double.isFinite(side.x) || !Double.isFinite(side.y) || !Double.isFinite(side.z)){
            side = fallbackPerpendicular(tangent);
        }

        return side.normalize();
    }

    private static Vec3 fallbackPerpendicular(Vec3 tangent) {
        Vec3 up = Math.abs(tangent.y) < 0.9D ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 side = tangent.cross(up);

        if (side.lengthSqr() < 1.0E-7D){
            return new Vec3(1, 0, 0);
        }

        return side.normalize();
    }

    public static void renderEntityTrailStrip(
            PoseStack pose, MultiBufferSource buffer, Entity entity, float pt,
            DTClientTrail trail, ResourceLocation tex, int argb, float halfWidth, float alpha
    ) {
        List<DTClientTrail.Point> points = trail.points();
        if (points.size() < 2)
            return;

        int color = normalizeArgb(argb, 0xFFFFFFFF);
        double ox = Mth.lerp(pt, entity.xo, entity.getX());
        double oy = Mth.lerp(pt, entity.yo, entity.getY());
        double oz = Mth.lerp(pt, entity.zo, entity.getZ());
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        pose.pushPose();
        pose.translate(-ox, -oy, -oz);

        VertexConsumer vc = buffer.getBuffer(DTRenderTypes.additiveTrailStrip(tex));
        Matrix4f matrix = pose.last().pose();
        Matrix3f normal = pose.last().normal();
        int count = points.size();

        for (int i = 0; i < count; i++) {
            Vec3 prev = points.get(Math.max(0, i - 1)).getPosition(pt);
            Vec3 cur = points.get(i).getPosition(pt);
            Vec3 next = points.get(Math.min(count - 1, i + 1)).getPosition(pt);

            float fade = 1.0F - Mth.clamp((points.get(i).getAge() - pt) / trail.lifespan(), 0.0F, 1.0F);
            float width = halfWidth * fade;
            int c = multiplyAlpha(color, alpha * fade * fade);

            Vec3 side = getTrailSideSmooth(prev, cur, next, camera, width);
            float v = (float) i / (float) (count - 1);

            vertex(vc, matrix, normal, cur.add(side), c, 0.0F, v);
            vertex(vc, matrix, normal, cur.subtract(side), c, 1.0F, v);
        }

        pose.popPose();
    }

    private static Vec3 getTrailSideSmooth(Vec3 prev, Vec3 current, Vec3 next, Vec3 camera, float width) {
        Vec3 tangent = next.subtract(prev);

        if (tangent.lengthSqr() < 1.0E-7D){
            tangent = next.subtract(current);
        }

        if (tangent.lengthSqr() < 1.0E-7D){
            tangent = current.subtract(prev);
        }

        if (tangent.lengthSqr() < 1.0E-7D){
            return new Vec3(width, 0, 0);
        }

        Vec3 toCamera = current.subtract(camera);

        if (toCamera.lengthSqr() < 1.0E-7D){
            toCamera = new Vec3(0, 1, 0);
        }

        Vec3 side = toCamera.cross(tangent).normalize().scale(width);

        if (!Double.isFinite(side.x) || !Double.isFinite(side.y) || !Double.isFinite(side.z)){
            return new Vec3(width, 0, 0);
        }

        return side;
    }
}