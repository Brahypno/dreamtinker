package org.dreamtinker.dreamtinker.tools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.dreamtinker.dreamtinker.Entity.NarcissusFluidProjectile;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class NarcissusFluidProjectileRenderer<T extends NarcissusFluidProjectile> extends EntityRenderer<T> {
    private TextureAtlasSprite s0, s1;

    public NarcissusFluidProjectileRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
        s0 = atlas.getSprite(new ResourceLocation("block/soul_fire_0"));
        s1 = atlas.getSprite(new ResourceLocation("block/soul_fire_1"));
    }

    private void ensureSprites() {
        if (s0 == null || s1 == null){
            var getter = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
            s0 = getter.apply(new ResourceLocation("minecraft", "block/soul_fire_0"));
            s1 = getter.apply(new ResourceLocation("minecraft", "block/soul_fire_1"));
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T e) {return InventoryMenu.BLOCK_ATLAS;}

    @Override
    public void render(T e, float yaw, float pt, PoseStack pose, MultiBufferSource buf, int light) {
        ensureSprites();
        pose.pushPose();

        // 体积大小
        float s = 0.9f;
        pose.scale(s, s, s);

        // 让火苗更像“实体”：朝运动方向&轻微自转（可按需保留/删掉）
        Vec3 d = e.getDeltaMovement();
        if (d.lengthSqr() > 1.0E-4){
            float yRot = (float) (Mth.atan2(d.z, d.x) * (180F / Math.PI)) - 90f;
            pose.mulPose(Axis.YP.rotationDegrees(yRot));
        }
        float spin = (e.tickCount + pt) * 6.0f;        // 6°/tick 的小转动
        pose.mulPose(Axis.YP.rotationDegrees(spin));

        // 用实体渲染管线绑定“方块图集”；不剔除背面
        VertexConsumer vc = buf.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS));
        int overlay = OverlayTexture.NO_OVERLAY;
        int packed = LightTexture.FULL_BRIGHT;        // 自发光：夜里也亮

        // 第一片面（Z 朝向）
        drawBillboard(pose, vc, s0, packed, overlay, 0.55f, 0.75f);
        drawBillboard(pose, vc, s1, packed, overlay, 0.50f, 0.68f);

        // 旋转 90° 画第二片面（X 朝向），形成十字形
        pose.mulPose(Axis.YP.rotationDegrees(90f));
        drawBillboard(pose, vc, s0, packed, overlay, 0.55f, 0.75f);
        drawBillboard(pose, vc, s1, packed, overlay, 0.50f, 0.68f);

        pose.popPose();
    }

    // ★ 补全“顶点六件套”：color → uv → overlay → uv2 → normal → endVertex()
    private void drawBillboard(
            PoseStack pose, VertexConsumer vc, TextureAtlasSprite sp,
            int light, int overlay, float w, float h) {
        Matrix4f m = pose.last().pose();
        Matrix3f n = pose.last().normal();
        float u0 = sp.getU0(), v0 = sp.getV0(), u1 = sp.getU1(), v1 = sp.getV1();

        // 正面
        vc.vertex(m, -w, -h, 0).color(255, 255, 255, 255).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(n, 0, 0, 1).endVertex();
        vc.vertex(m, w, -h, 0).color(255, 255, 255, 255).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(n, 0, 0, 1).endVertex();
        vc.vertex(m, w, h, 0).color(255, 255, 255, 255).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(n, 0, 0, 1).endVertex();
        vc.vertex(m, -w, h, 0).color(255, 255, 255, 255).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(n, 0, 0, 1).endVertex();

        // 背面（为了法线正确，虽用了 NoCull 仍建议补一遍反法线）
        vc.vertex(m, -w, h, 0).color(255, 255, 255, 255).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(n, 0, 0, -1).endVertex();
        vc.vertex(m, w, h, 0).color(255, 255, 255, 255).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(n, 0, 0, -1).endVertex();
        vc.vertex(m, w, -h, 0).color(255, 255, 255, 255).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(n, 0, 0, -1).endVertex();
        vc.vertex(m, -w, -h, 0).color(255, 255, 255, 255).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(n, 0, 0, -1).endVertex();
    }
}
