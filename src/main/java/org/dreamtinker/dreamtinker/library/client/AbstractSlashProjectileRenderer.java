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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.dreamtinker.dreamtinker.Entity.AbstractSlashProjectile;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSlashProjectileRenderer<T extends AbstractSlashProjectile> extends EntityRenderer<T> {
    protected AbstractSlashProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    protected static void renderTexturedQuad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x0,
            float y0,
            float x1,
            float y1,
            float z,
            float texW,
            float texH,
            float texX0,
            float texY0,
            float texX1,
            float texY1,
            int light,
            int r,
            int g,
            int b,
            int a
    ) {
        float u0 = texX0 / texW;
        float u1 = texX1 / texW;
        float v0 = texY0 / texH;
        float v1 = texY1 / texH;

        vertex(consumer, pose, x0, y0, z, u0, v1, light, r, g, b, a);
        vertex(consumer, pose, x1, y0, z, u1, v1, light, r, g, b, a);
        vertex(consumer, pose, x1, y1, z, u1, v0, light, r, g, b, a);
        vertex(consumer, pose, x0, y1, z, u0, v0, light, r, g, b, a);
    }

    protected static void renderCenteredTexturedQuad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float length,
            float width,
            float z,
            float texW,
            float texH,
            float texX0,
            float texY0,
            float texX1,
            float texY1,
            int light,
            int r,
            int g,
            int b,
            int a
    ) {
        renderTexturedQuad(
                consumer,
                pose,
                -length * 0.5F,
                -width * 0.5F,
                length * 0.5F,
                width * 0.5F,
                z,
                texW,
                texH,
                texX0,
                texY0,
                texX1,
                texY1,
                light,
                r,
                g,
                b,
                a
        );
    }

    protected static void renderCenteredHorizontalTexturedQuad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float length,
            float width,
            float y,
            float texW,
            float texH,
            float texX0,
            float texY0,
            float texX1,
            float texY1,
            int light,
            int r,
            int g,
            int b,
            int a
    ) {
        float x0 = -length * 0.5F;
        float x1 = length * 0.5F;
        float z0 = -width * 0.5F;
        float z1 = width * 0.5F;

        float u0 = texX0 / texW;
        float u1 = texX1 / texW;
        float v0 = texY0 / texH;
        float v1 = texY1 / texH;

        vertex(consumer, pose, x0, y, z0, u0, v1, light, r, g, b, a);
        vertex(consumer, pose, x1, y, z0, u1, v1, light, r, g, b, a);
        vertex(consumer, pose, x1, y, z1, u1, v0, light, r, g, b, a);
        vertex(consumer, pose, x0, y, z1, u0, v0, light, r, g, b, a);
    }

    protected static void renderHorizontalSegment(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x0,
            float x1,
            float width,
            float texW,
            float texH,
            float texX0,
            float texY0,
            float texX1,
            float texY1,
            int light,
            int r,
            int g,
            int b,
            int a
    ) {
        renderTexturedQuad(
                consumer,
                pose,
                x0,
                -width * 0.5F,
                x1,
                width * 0.5F,
                0.0F,
                texW,
                texH,
                texX0,
                texY0,
                texX1,
                texY1,
                light,
                r,
                g,
                b,
                a
        );
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

    protected RenderType getRenderType(T entity) {
        return RenderType.entityTranslucent(this.getTextureLocation(entity));
    }

    protected int getRenderLight(T entity, int packedLight) {
        return 0xF000F0;
    }

    protected boolean shouldAlignToMotion(T entity) {
        return true;
    }

    protected float getForwardRotationDegrees(T entity) {
        return 90.0F;
    }

    protected void beforeRenderSlash(T entity, float partialTick, PoseStack poseStack) {}

    protected abstract void renderSlash(
            T entity,
            float partialTick,
            PoseStack poseStack,
            VertexConsumer consumer,
            int light,
            int r,
            int g,
            int b,
            int a
    );

    protected void alignToMotion(T entity, PoseStack poseStack) {
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

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));
        poseStack.mulPose(Axis.YP.rotationDegrees(this.getForwardRotationDegrees(entity)));
    }

    @Override
    public void render(
            @NotNull T entity,
            float entityYaw,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource bufferSource,
            int packedLight
    ) {
        poseStack.pushPose();

        if (this.shouldAlignToMotion(entity)){
            this.alignToMotion(entity, poseStack);
        }

        this.beforeRenderSlash(entity, partialTick, poseStack);

        int rgb = entity.getColor();
        int r = (rgb >> 16) & 255;
        int g = (rgb >> 8) & 255;
        int b = rgb & 255;
        int a = Mth.clamp(entity.getAlpha(), 0, 255);

        VertexConsumer consumer = bufferSource.getBuffer(this.getRenderType(entity));
        int light = this.getRenderLight(entity, packedLight);

        this.renderSlashWithThickness(entity, partialTick, poseStack, consumer, light, r, g, b, a);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    protected void renderSlashWithThickness(
            T entity,
            float partialTick,
            PoseStack poseStack,
            VertexConsumer consumer,
            int light,
            int r,
            int g,
            int b,
            int a
    ) {
        this.renderSlash(entity, partialTick, poseStack, consumer, light, r, g, b, a);

        float thicknessAlpha = this.getThicknessAlpha(entity);
        if (thicknessAlpha <= 0.0F){
            return;
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        this.renderSlash(entity, partialTick, poseStack, consumer, light, r, g, b, (int) (a * thicknessAlpha));
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(this.getThicknessAngle(entity)));
        this.renderSlash(entity, partialTick, poseStack, consumer, light, r, g, b, (int) (a * thicknessAlpha * 0.65F));
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(-this.getThicknessAngle(entity)));
        this.renderSlash(entity, partialTick, poseStack, consumer, light, r, g, b, (int) (a * thicknessAlpha * 0.65F));
        poseStack.popPose();
    }

    protected float getThicknessAlpha(T entity) {
        return 0.35F;
    }

    protected float getThicknessAngle(T entity) {
        return 35.0F;
    }

    @Override
    public abstract @NotNull ResourceLocation getTextureLocation(@NotNull T entity);
}