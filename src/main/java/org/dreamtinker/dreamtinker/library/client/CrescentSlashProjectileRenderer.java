package org.dreamtinker.dreamtinker.library.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.Entity.CrescentSlashProjectile;
import org.jetbrains.annotations.NotNull;

public class CrescentSlashProjectileRenderer extends AbstractSlashProjectileRenderer<CrescentSlashProjectile> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            Dreamtinker.MODID,
            "textures/entity/crescent_slash_projectile.png"
    );

    private static final float TEX_W = 64.0F;
    private static final float TEX_H = 32.0F;

    private static final float CORE_Y0 = 0.0F;
    private static final float CORE_Y1 = 16.0F;

    private static final float HALO_Y0 = 16.0F;
    private static final float HALO_Y1 = 32.0F;

    public CrescentSlashProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSlash(
            CrescentSlashProjectile entity,
            float partialTick,
            PoseStack poseStack,
            VertexConsumer consumer,
            int light,
            int r,
            int g,
            int b,
            int a
    ) {
        float powerScale = Mth.clamp(entity.getPower() / 8.0F, 0.80F, 1.45F);

        float coreLength = 1.85F * entity.getLengthScale() * powerScale;
        float coreWidth = 1.15F * entity.getWidthScale() * Mth.clamp(powerScale, 0.90F, 1.25F);

        float haloLength = coreLength * 1.10F;
        float haloWidth = coreWidth * 1.28F;

        renderCenteredHorizontalTexturedQuad(
                consumer,
                poseStack.last(),
                haloLength,
                haloWidth,
                0.0F,
                TEX_W,
                TEX_H,
                0.0F,
                HALO_Y0,
                TEX_W,
                HALO_Y1,
                light,
                r,
                g,
                b,
                (int) (a * 0.52F)
        );

        renderCenteredTexturedQuad(
                consumer,
                poseStack.last(),
                coreLength,
                coreWidth,
                0.0F,
                TEX_W,
                TEX_H,
                0.0F,
                CORE_Y0,
                TEX_W,
                CORE_Y1,
                light,
                r,
                g,
                b,
                a
        );
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CrescentSlashProjectile entity) {
        return TEXTURE;
    }
}