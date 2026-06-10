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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.Entity.NarcissusFluidProjectile;
import org.brahypno.dreamtinker.library.client.trail.DTTrailRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class NarcissusFluidProjectileRenderer<T extends NarcissusFluidProjectile> extends EntityRenderer<T> {
    private TextureAtlasSprite s0, s1;
    private static final ResourceLocation TEX =
            Dreamtinker.getLocation("textures/entity/narcissus_fluid_projectile.png");
    private static final ResourceLocation NARCISSUS_MASK_TRAIL_TEX =
            Dreamtinker.getLocation("textures/entity/narcissus_fluid_trail_mask.png");
    private static final ResourceLocation NARCISSUS_COLORED_TRAIL_TEX =
            Dreamtinker.getLocation("textures/entity/narcissus_fluid_trail_colored.png");
    private static final ResourceLocation CONCENTRATED_TRAIL_TEX =
            Dreamtinker.getLocation("textures/entity/narcissus_fluid_concentrated_trail.png");

    public NarcissusFluidProjectileRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T e) {return TEX;}//InventoryMenu.BLOCK_ATLAS;}

    @Override
    public void render(NarcissusFluidProjectile e, float yaw, float pt, PoseStack pose, MultiBufferSource buf, int light) {
        // 外层：宽、淡、主色
        DTTrailRenderer.renderEntityTrailVolume(pose, buf, e, pt, e.trail, NARCISSUS_MASK_TRAIL_TEX, e.getColor(), 0.14F, 0.18F, 4, 0.28f);

        // 中层：正常主色
        DTTrailRenderer.renderEntityTrailVolume(pose, buf, e, pt, e.trail, NARCISSUS_COLORED_TRAIL_TEX, e.getColor(), 0.13F, 0.62F, 4, 0.36f);

        // 内层：白色/淡色亮芯
        DTTrailRenderer.renderEntityTrailVolume(pose, buf, e, pt, e.shortTrail, CONCENTRATED_TRAIL_TEX, 0xF8FFF6E8, 0.045F, 0.88F, 5, 0.34f);
        pose.pushPose();

        // 体积大小
        float s = 0.9f;
        pose.scale(s, s, s);

        // 让火苗更像“实体”：朝运动方向&轻微自转（可按需保留/删掉）
        Vec3 d = e.getDeltaMovement();
        if (d.lengthSqr() > 1.0E-4){
            double h = Math.sqrt(d.x * d.x + d.z * d.z);

            // 水平朝向
            float yawRot = (float) (Math.atan2(d.z, d.x) * 180.0 / Math.PI) - 90.0F;

            // 俯仰：向上飞时负角，向下飞时正角（通常这样更像原版弹射物）
            float pitchRot = (float) (-(Math.atan2(d.y, h) * 180.0 / Math.PI));

            pose.mulPose(Axis.YP.rotationDegrees(yawRot));
            pose.mulPose(Axis.ZP.rotationDegrees(pitchRot));
            // 让“贴图朝上”改成“箭头朝前”
            pose.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }

        // 用实体渲染管线绑定“方块图集”；不剔除背面
        VertexConsumer vc = buf.getBuffer(RenderType.entityTranslucent(TEX));
        int overlay = OverlayTexture.NO_OVERLAY;
        int packed = LightTexture.FULL_BRIGHT;

        int argb = 0 == e.getColor() ? 0xCC2376dd : e.getColor();
        if ((argb & 0xFF000000) == 0)
            argb |= 0xFF000000;


        // 第一片面（Z 朝向）
        //drawBillboard(pose, vc, s0, packed, overlay, 0.55f, 0.75f);
        //drawBillboard(pose, vc, s1, packed, overlay, 0.50f, 0.68f);

        // 旋转 90° 画第二片面（X 朝向），形成十字形
        //pose.mulPose(Axis.YP.rotationDegrees(90f));
        //drawBillboard(pose, vc, s0, packed, overlay, 0.55f, 0.75f);
        //drawBillboard(pose, vc, s1, packed, overlay, 0.50f, 0.68f);

        pose.mulPose(Axis.YP.rotationDegrees(90f));
        drawBillboardTex(pose, vc, packed, overlay, 0.55f, 0.75f, argb);

        pose.popPose();
    }

    private void drawBillboardTex(PoseStack pose, VertexConsumer vc, int light, int overlay, float w, float h, int argb) {
        Matrix4f m = pose.last().pose();
        Matrix3f n = pose.last().normal();

        int a = (argb >>> 24) & 255;
        int r = (argb >>> 16) & 255;
        int g = (argb >>> 8) & 255;
        int b = argb & 255;

        float u0 = 0f, v0 = 0f, u1 = 1f, v1 = 1f;

        vc.vertex(m, -w, -h, 0).color(r, g, b, a).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(n, 0, 0, 1).endVertex();
        vc.vertex(m, w, -h, 0).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(n, 0, 0, 1).endVertex();
        vc.vertex(m, w, h, 0).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(n, 0, 0, 1).endVertex();
        vc.vertex(m, -w, h, 0).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(n, 0, 0, 1).endVertex();

        vc.vertex(m, -w, h, 0).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(n, 0, 0, -1).endVertex();
        vc.vertex(m, w, h, 0).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(n, 0, 0, -1).endVertex();
        vc.vertex(m, w, -h, 0).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(n, 0, 0, -1).endVertex();
        vc.vertex(m, -w, -h, 0).color(r, g, b, a).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(n, 0, 0, -1).endVertex();
    }

    @Override
    protected int getBlockLightLevel(NarcissusFluidProjectile entity, BlockPos pos) {
        return 15;
    }
}
