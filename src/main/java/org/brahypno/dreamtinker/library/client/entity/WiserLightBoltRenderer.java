package org.brahypno.dreamtinker.library.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.brahypno.dreamtinker.Entity.WiserLightBolt;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class WiserLightBoltRenderer extends LightningBoltRenderer {
    public WiserLightBoltRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    private static void renderArc(PoseStack poseStack, VertexConsumer consumer, RandomSource random, Vec3 from, Vec3 to, float width, float alpha) {
        Vec3 delta = to.subtract(from);
        if (delta.lengthSqr() < 0.0001D)
            return;

        Vec3 dir = delta.normalize();
        Vec3 sideA = dir.cross(new Vec3(0, 1, 0));
        if (sideA.lengthSqr() < 0.0001D)
            sideA = dir.cross(new Vec3(1, 0, 0));

        sideA = sideA.normalize();
        Vec3 sideB = dir.cross(sideA).normalize();

        List<Vec3> points = new ArrayList<>();
        int segments = 8;

        for (int i = 0; i <= segments; i++) {
            double t = i / (double) segments;
            double shake = Math.sin(Math.PI * t) * 0.35D;
            Vec3 base = from.lerp(to, t);
            Vec3 offset = sideA.scale((random.nextDouble() - 0.5D) * shake).add(sideB.scale((random.nextDouble() - 0.5D) * shake));
            points.add(i == 0 || i == segments ? base : base.add(offset));
        }

        Matrix4f matrix = poseStack.last().pose();

        for (int i = 0; i < points.size() - 1; i++) {
            drawSegment(matrix, consumer, points.get(i), points.get(i + 1), sideA.scale(width), alpha);
            drawSegment(matrix, consumer, points.get(i), points.get(i + 1), sideB.scale(width), alpha);
        }
    }

    private static void drawSegment(Matrix4f matrix, VertexConsumer consumer, Vec3 a, Vec3 b, Vec3 side, float alpha) {
        vertex(matrix, consumer, a.add(side), alpha);
        vertex(matrix, consumer, b.add(side), alpha);
        vertex(matrix, consumer, b.subtract(side), alpha);
        vertex(matrix, consumer, a.subtract(side), alpha);
    }

    private static void vertex(Matrix4f matrix, VertexConsumer consumer, Vec3 pos, float alpha) {
        consumer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z).color(0.55F, 0.75F, 1.0F, alpha).endVertex();
    }

    @Override
    public void render(net.minecraft.world.entity.LightningBolt entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light) {
        super.render(entity, yaw, partialTick, poseStack, buffer, light);

        if (!(entity instanceof WiserLightBolt wiser))
            return;

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        int index = 0;

        for (WiserLightBolt.ChainArc arc : wiser.getChainArcs()) {
            RandomSource random = RandomSource.create(wiser.getId() * 31L + index * 997L + wiser.tickCount / 2L);
            renderArc(poseStack, consumer, random, arc.from(), arc.to(), 0.08F, 0.55F);
            renderArc(poseStack, consumer, random, arc.from(), arc.to(), 0.035F, 0.95F);
            index++;
        }
    }
}
