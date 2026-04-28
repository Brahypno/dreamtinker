package org.dreamtinker.dreamtinker.library.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.Entity.WingSlashProjectile;
import org.jetbrains.annotations.NotNull;

public class WingSlashProjectileRenderer extends EntityRenderer<WingSlashProjectile> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            Dreamtinker.MODID,
            "textures/entity/wing_slash_projectile.png"
    );

    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE);

    private static final float TEX_W = 96.0F;
    private static final float TEX_H = 32.0F;

    // atlas 横向三段
    private static final float TAIL_X0 = 0.0F;
    private static final float TAIL_X1 = 24.0F;

    private static final float BODY_X0 = 24.0F;
    private static final float BODY_X1 = 56.0F;

    private static final float HEAD_X0 = 56.0F;
    private static final float HEAD_X1 = 96.0F;

    // atlas 纵向两层
    private static final float CORE_Y0 = 0.0F;
    private static final float CORE_Y1 = 16.0F;

    private static final float HALO_Y0 = 16.0F;
    private static final float HALO_Y1 = 32.0F;

    public WingSlashProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    private static void alignToMotion(WingSlashProjectile entity, PoseStack poseStack) {
        Vec3 motion = entity.getDeltaMovement();

        if (motion.lengthSqr() <= 1.0E-7D){
            return;
        }

        motion = motion.normalize();

        float yaw = (float) (Mth.atan2(motion.x, motion.z) * Mth.RAD_TO_DEG);
        float pitch = (float) (Mth.atan2(
                motion.y,
                Math.sqrt(motion.x * motion.x + motion.z * motion.z)
        ) * Mth.RAD_TO_DEG);

        /*
         * 先让本地 +Z 对齐运动方向，再转到本地 +X 为前进方向。
         * 如果发现贴图前后反了，把 90 改成 -90 或再加 180。
         */
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
    }

    private static void renderSwordQiLayer(
            VertexConsumer consumer,
            PoseStack poseStack,
            float startX,
            float tailLength,
            float bodyLength,
            float headLength,
            float width,
            int light,
            int r,
            int g,
            int b,
            int a,
            float texY0,
            float texY1
    ) {
        float x = startX;

        // tail：小幅缩放，保持羽裂形状
        renderSegment(
                consumer,
                poseStack.last(),
                x,
                x + tailLength,
                width * 1.10F,
                TAIL_X0,
                TAIL_X1,
                texY0,
                texY1,
                light,
                r,
                g,
                b,
                a
        );
        x += tailLength;

        // body：主要拉伸段
        renderSegment(
                consumer,
                poseStack.last(),
                x,
                x + bodyLength,
                width,
                BODY_X0,
                BODY_X1,
                texY0,
                texY1,
                light,
                r,
                g,
                b,
                a
        );
        x += bodyLength;

        // head：基本固定比例，避免剑锋变钝
        renderSegment(
                consumer,
                poseStack.last(),
                x,
                x + headLength,
                width * 0.95F,
                HEAD_X0,
                HEAD_X1,
                texY0,
                texY1,
                light,
                r,
                g,
                b,
                a
        );
    }

    private static void renderSegment(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x0,
            float x1,
            float width,
            float texX0,
            float texX1,
            float texY0,
            float texY1,
            int light,
            int r,
            int g,
            int b,
            int a
    ) {
        float y0 = -width * 0.5F;
        float y1 = width * 0.5F;

        float u0 = texX0 / TEX_W;
        float u1 = texX1 / TEX_W;
        float v0 = texY0 / TEX_H;
        float v1 = texY1 / TEX_H;

        vertex(consumer, pose, x0, y0, 0.0F, u0, v1, light, r, g, b, a);
        vertex(consumer, pose, x1, y0, 0.0F, u1, v1, light, r, g, b, a);
        vertex(consumer, pose, x1, y1, 0.0F, u1, v0, light, r, g, b, a);
        vertex(consumer, pose, x0, y1, 0.0F, u0, v0, light, r, g, b, a);
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float u,
            float v,
            int light,
            int r,
            int g,
            int b,
            int a
    ) {
        consumer.vertex(pose.pose(), x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(0)
                .uv2(light)
                .normal(pose.normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public void render(
            @NotNull WingSlashProjectile entity,
            float entityYaw,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource bufferSource,
            int packedLight
    ) {
        poseStack.pushPose();

        alignToMotion(entity, poseStack);

        float age = entity.tickCount + partialTick;
        float spinSpeed = entity.getSpinSpeed();
        if (spinSpeed != 0.0F){
            poseStack.mulPose(Axis.XP.rotationDegrees(age * spinSpeed));
        }

        int rgb = entity.getColor();
        int r = (rgb >> 16) & 255;
        int g = (rgb >> 8) & 255;
        int b = rgb & 255;
        int a = Mth.clamp(entity.getAlpha(), 0, 255);

        float powerScale = Mth.clamp(entity.getPower() / 8.0F, 0.75F, 1.35F);

        /*
         * 这三个长度是世界空间中的基础长度。
         * 形变时只强拉 body，head 基本固定，tail 小幅变化。
         */
        float tailLength = 0.34F * entity.getLengthScale() * Mth.lerp(powerScale - 0.75F, 0.95F, 1.08F);
        float bodyLength = 0.72F * entity.getLengthScale() * powerScale;
        float headLength = 0.54F * Mth.clamp(entity.getLengthScale(), 0.85F, 1.18F);

        float coreWidth = 0.30F * entity.getWidthScale() * Mth.clamp(powerScale, 0.85F, 1.20F);
        float haloWidth = coreWidth * 1.75F;

        VertexConsumer consumer = bufferSource.getBuffer(RENDER_TYPE);
        int light = 0xF000F0;

        /*
         * 本地 X 轴为剑气方向：
         * tail 在左，head 在右。
         */
        float x0 = -(tailLength + bodyLength + headLength) * 0.5F;

        // halo 先画，core 后画
        renderSwordQiLayer(
                consumer,
                poseStack,
                x0,
                tailLength,
                bodyLength,
                headLength,
                haloWidth,
                light,
                r,
                g,
                b,
                (int) (a * 0.58F),
                HALO_Y0,
                HALO_Y1
        );

        renderSwordQiLayer(
                consumer,
                poseStack,
                x0,
                tailLength,
                bodyLength,
                headLength,
                coreWidth,
                light,
                r,
                g,
                b,
                a,
                CORE_Y0,
                CORE_Y1
        );

        /*
         * 交叉第二片，避免从侧面看过薄。
         * 第二片更窄、更淡。
         */
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

        renderSwordQiLayer(
                consumer,
                poseStack,
                x0,
                tailLength,
                bodyLength,
                headLength,
                haloWidth * 0.70F,
                light,
                r,
                g,
                b,
                (int) (a * 0.34F),
                HALO_Y0,
                HALO_Y1
        );

        renderSwordQiLayer(
                consumer,
                poseStack,
                x0,
                tailLength,
                bodyLength,
                headLength,
                coreWidth * 0.72F,
                light,
                r,
                g,
                b,
                (int) (a * 0.72F),
                CORE_Y0,
                CORE_Y1
        );

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull WingSlashProjectile entity) {
        return TEXTURE;
    }
}