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

public class WiserLightBoltRenderer extends LightningBoltRenderer {
    private static final int SEGMENTS = 8;
    private static final int POINTS = SEGMENTS + 1;
    private static final double MIN_LENGTH_SQR = 1.0E-4D;
    private static final double[] SHAKE = createShakeTable();

    private final RandomSource arcRandom = RandomSource.create();
    private final ArcWorkspace workspace = new ArcWorkspace();

    public WiserLightBoltRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    private static double[] createShakeTable() {
        double[] result = new double[POINTS];
        for (int i = 0; i < POINTS; i++) {
            double t = i / (double) SEGMENTS;
            result[i] = Math.sin(Math.PI * t) * 0.35D;
        }
        return result;
    }

    private void renderArc(PoseStack poseStack, VertexConsumer consumer, Vec3 from, Vec3 to, float width, float alpha) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        double lengthSqr = dx * dx + dy * dy + dz * dz;
        if (lengthSqr < MIN_LENGTH_SQR)
            return;

        double inverseLength = 1.0D / Math.sqrt(lengthSqr);
        double dirX = dx * inverseLength;
        double dirY = dy * inverseLength;
        double dirZ = dz * inverseLength;

        double sideAX = -dirZ;
        double sideAY = 0.0D;
        double sideAZ = dirX;
        double sideALengthSqr = sideAX * sideAX + sideAZ * sideAZ;
        if (sideALengthSqr < MIN_LENGTH_SQR){
            sideAX = 0.0D;
            sideAY = dirZ;
            sideAZ = -dirY;
            sideALengthSqr = sideAY * sideAY + sideAZ * sideAZ;
        }

        double inverseSideALength = 1.0D / Math.sqrt(sideALengthSqr);
        sideAX *= inverseSideALength;
        sideAY *= inverseSideALength;
        sideAZ *= inverseSideALength;

        double sideBX = dirY * sideAZ - dirZ * sideAY;
        double sideBY = dirZ * sideAX - dirX * sideAZ;
        double sideBZ = dirX * sideAY - dirY * sideAX;

        fillArcPoints(from.x, from.y, from.z, dx, dy, dz,
                      sideAX, sideAY, sideAZ, sideBX, sideBY, sideBZ);

        Matrix4f matrix = poseStack.last().pose();
        double sideAWidthX = sideAX * width;
        double sideAWidthY = sideAY * width;
        double sideAWidthZ = sideAZ * width;
        double sideBWidthX = sideBX * width;
        double sideBWidthY = sideBY * width;
        double sideBWidthZ = sideBZ * width;

        for (int i = 0; i < SEGMENTS; i++) {
            drawSegment(matrix, consumer, i, sideAWidthX, sideAWidthY, sideAWidthZ, alpha);
            drawSegment(matrix, consumer, i, sideBWidthX, sideBWidthY, sideBWidthZ, alpha);
        }
    }

    private void fillArcPoints(
            double fromX, double fromY, double fromZ,
            double dx, double dy, double dz,
            double sideAX, double sideAY, double sideAZ,
            double sideBX, double sideBY, double sideBZ) {
        for (int i = 0; i < POINTS; i++) {
            double t = i / (double) SEGMENTS;
            double baseX = fromX + dx * t;
            double baseY = fromY + dy * t;
            double baseZ = fromZ + dz * t;

            // 与旧实现保持相同的随机数消耗顺序，包括两个端点。
            double offsetA = (this.arcRandom.nextDouble() - 0.5D) * SHAKE[i];
            double offsetB = (this.arcRandom.nextDouble() - 0.5D) * SHAKE[i];
            if (i == 0 || i == SEGMENTS){
                this.workspace.x[i] = baseX;
                this.workspace.y[i] = baseY;
                this.workspace.z[i] = baseZ;
            }else {
                this.workspace.x[i] = baseX + sideAX * offsetA + sideBX * offsetB;
                this.workspace.y[i] = baseY + sideAY * offsetA + sideBY * offsetB;
                this.workspace.z[i] = baseZ + sideAZ * offsetA + sideBZ * offsetB;
            }
        }
    }

    private void drawSegment(
            Matrix4f matrix, VertexConsumer consumer, int index,
            double sideX, double sideY, double sideZ, float alpha) {
        double ax = this.workspace.x[index];
        double ay = this.workspace.y[index];
        double az = this.workspace.z[index];
        double bx = this.workspace.x[index + 1];
        double by = this.workspace.y[index + 1];
        double bz = this.workspace.z[index + 1];

        vertex(matrix, consumer, ax + sideX, ay + sideY, az + sideZ, alpha);
        vertex(matrix, consumer, bx + sideX, by + sideY, bz + sideZ, alpha);
        vertex(matrix, consumer, bx - sideX, by - sideY, bz - sideZ, alpha);
        vertex(matrix, consumer, ax - sideX, ay - sideY, az - sideZ, alpha);
    }

    private static void vertex(
            Matrix4f matrix, VertexConsumer consumer,
            double x, double y, double z, float alpha) {
        consumer.vertex(matrix, (float) x, (float) y, (float) z)
                .color(0.55F, 0.75F, 1.0F, alpha)
                .endVertex();
    }

    @Override
    public void render(
            net.minecraft.world.entity.LightningBolt entity, float yaw, float partialTick,
            PoseStack poseStack, MultiBufferSource buffer, int light) {
        super.render(entity, yaw, partialTick, poseStack, buffer, light);
        if (!(entity instanceof WiserLightBolt wiser))
            return;

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        int index = 0;
        for (WiserLightBolt.ChainArc arc : wiser.getChainArcs()) {
            this.arcRandom.setSeed(wiser.getId() * 31L + index * 997L + wiser.tickCount / 2L);
            renderArc(poseStack, consumer, arc.from(), arc.to(), 0.08F, 0.55F);
            renderArc(poseStack, consumer, arc.from(), arc.to(), 0.035F, 0.95F);
            index++;
        }
    }

    private static final class ArcWorkspace {
        private final double[] x = new double[POINTS];
        private final double[] y = new double[POINTS];
        private final double[] z = new double[POINTS];
    }
}
