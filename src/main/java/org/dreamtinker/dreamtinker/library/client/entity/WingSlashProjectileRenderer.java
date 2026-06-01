package org.dreamtinker.dreamtinker.library.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.Entity.WingSlashProjectile;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class WingSlashProjectileRenderer extends AbstractSlashProjectileRenderer<WingSlashProjectile> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            Dreamtinker.MODID,
            "textures/entity/wing_slash_projectile.png"
    );

    private static final float TEX_W = 96.0F;
    private static final float TEX_H = 32.0F;

    private static final float TAIL_X0 = 0.0F;
    private static final float TAIL_X1 = 24.0F;

    private static final float BODY_X0 = 24.0F;
    private static final float BODY_X1 = 56.0F;

    private static final float HEAD_X0 = 56.0F;
    private static final float HEAD_X1 = 96.0F;

    private static final float CORE_Y0 = 0.0F;
    private static final float CORE_Y1 = 16.0F;

    private static final float HALO_Y0 = 16.0F;
    private static final float HALO_Y1 = 32.0F;

    public WingSlashProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void beforeRenderSlash(WingSlashProjectile entity, float partialTick, PoseStack poseStack) {
        float spinSpeed = entity.getSpinSpeed();

        if (spinSpeed != 0.0F){
            poseStack.mulPose(Axis.XP.rotationDegrees((entity.tickCount + partialTick) * spinSpeed));
        }
    }

    @Override
    protected void renderSlash(WingSlashProjectile entity, float partialTick, PoseStack poseStack, VertexConsumer consumer, int light, int r, int g, int b, int a) {
        float powerScale = Mth.clamp(entity.getPower() / 8.0F, 0.75F, 1.35F);
        float tailLength = 0.34F * entity.getLengthScale() * Mth.lerp(powerScale - 0.75F, 0.95F, 1.08F);
        float bodyLength = 0.72F * entity.getLengthScale() * powerScale;
        float headLength = 0.54F * Mth.clamp(entity.getLengthScale(), 0.85F, 1.18F);
        float coreWidth = 0.30F * entity.getWidthScale() * Mth.clamp(powerScale, 0.85F, 1.20F);
        float haloWidth = coreWidth * 1.75F;
        float depth = 0.105F * entity.getWidthScale() * Mth.clamp(powerScale, 0.85F, 1.25F);
        float x0 = -(tailLength + bodyLength + headLength) * 0.5F;

        this.renderSwordQiLayer(consumer, poseStack, x0, tailLength, bodyLength, headLength, haloWidth, depth * 0.65F, light, r, g, b, alpha(a, 0.48F), 0.16F,
                                HALO_Y0, HALO_Y1);
        this.renderSwordQiLayer(consumer, poseStack, x0, tailLength, bodyLength, headLength, coreWidth, depth, light, r, g, b, a, 0.35F, CORE_Y0, CORE_Y1);
    }

    private void renderSwordQiLayer(VertexConsumer consumer, PoseStack poseStack, float startX, float tailLength, float bodyLength, float headLength, float width, float depth, int light, int r, int g, int b, int a, float sideAlpha, float texY0, float texY1) {
        float x = startX;

        renderExtrudedHorizontalSegment(consumer, poseStack.last(), x, x + tailLength, width * 1.10F, depth * 0.72F, TEX_W, TEX_H, TAIL_X0, texY0, TAIL_X1,
                                        texY1, light, r, g, b, a, sideAlpha);
        x += tailLength;

        renderExtrudedHorizontalSegment(consumer, poseStack.last(), x, x + bodyLength, width, depth, TEX_W, TEX_H, BODY_X0, texY0, BODY_X1, texY1, light, r, g,
                                        b, a, sideAlpha);
        x += bodyLength;

        renderExtrudedHorizontalSegment(consumer, poseStack.last(), x, x + headLength, width * 0.95F, depth * 0.82F, TEX_W, TEX_H, HEAD_X0, texY0, HEAD_X1,
                                        texY1, light, r, g, b, a, sideAlpha);
    }

    @Override
    protected float getThicknessAlpha(WingSlashProjectile entity) {
        return 0.08F;
    }

    @Override
    protected float getThicknessAngle(WingSlashProjectile entity) {
        return 24.0F;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull WingSlashProjectile entity) {
        return TEXTURE;
    }
}