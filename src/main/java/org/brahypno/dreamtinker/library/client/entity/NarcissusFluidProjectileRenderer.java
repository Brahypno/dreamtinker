package org.brahypno.dreamtinker.library.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.Entity.NarcissusFluidProjectile;
import org.brahypno.dreamtinker.library.client.trail.DTClientTrail;
import org.brahypno.dreamtinker.library.client.trail.DTTrailRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class NarcissusFluidProjectileRenderer<T extends NarcissusFluidProjectile> extends EntityRenderer<T> {
    private static final ResourceLocation TEX =
            Dreamtinker.getLocation("textures/entity/narcissus_fluid_projectile.png");
    private static final ResourceLocation NARCISSUS_MASK_TRAIL_TEX =
            Dreamtinker.getLocation("textures/entity/narcissus_fluid_trail_mask.png");
    private static final ResourceLocation NARCISSUS_COLORED_TRAIL_TEX =
            Dreamtinker.getLocation("textures/entity/narcissus_fluid_trail_colored.png");
    private static final ResourceLocation CONCENTRATED_TRAIL_TEX =
            Dreamtinker.getLocation("textures/entity/narcissus_fluid_concentrated_trail.png");
    private static final RenderType PROJECTILE_RENDER_TYPE = RenderType.entityTranslucent(TEX);

    /**
     * 这是一个很宽松的硬上限；实际剔除主要依赖完整拖尾包围盒的视锥测试。
     * 192 格以外主体与拖尾在正常画面中已经几乎不可辨识。
     */
    private static final double MAX_RENDER_DISTANCE = 192.0D;
    private static final double MAX_RENDER_DISTANCE_SQR = MAX_RENDER_DISTANCE * MAX_RENDER_DISTANCE;
    private static final double VISUAL_BOUNDS_INFLATE = 0.35D;

    public NarcissusFluidProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return TEX;
    }

    @Override
    public boolean shouldRender(T entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        AABB visualBounds = getVisualBounds(entity);
        if (distanceToSqr(visualBounds, cameraX, cameraY, cameraZ) > MAX_RENDER_DISTANCE_SQR){
            return false;
        }
        return frustum.isVisible(visualBounds);
    }

    @Override
    public void render(T entity, float yaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int light) {
        // 可见时完全保留原来的三层体积拖尾，不做层数或采样降级。
        DTTrailRenderer.renderEntityTrailVolume(
                pose, buffer, entity, partialTicks, entity.trail,
                NARCISSUS_MASK_TRAIL_TEX, entity.getColor(),
                0.14F, 0.18F, 4, 0.28F
        );
        DTTrailRenderer.renderEntityTrailVolume(
                pose, buffer, entity, partialTicks, entity.trail,
                NARCISSUS_COLORED_TRAIL_TEX, entity.getColor(),
                0.13F, 0.62F, 4, 0.36F
        );
        DTTrailRenderer.renderEntityTrailVolume(
                pose, buffer, entity, partialTicks, entity.shortTrail,
                CONCENTRATED_TRAIL_TEX, 0xF8FFF6E8,
                0.045F, 0.88F, 5, 0.34F
        );

        pose.pushPose();
        pose.scale(0.9F, 0.9F, 0.9F);

        Vec3 movement = entity.getDeltaMovement();
        if (movement.lengthSqr() > 1.0E-4D){
            double horizontal = Math.sqrt(movement.x * movement.x + movement.z * movement.z);
            float yawRotation = (float) Math.toDegrees(Math.atan2(movement.z, movement.x)) - 90.0F;
            float pitchRotation = (float) -Math.toDegrees(Math.atan2(movement.y, horizontal));
            pose.mulPose(Axis.YP.rotationDegrees(yawRotation));
            pose.mulPose(Axis.ZP.rotationDegrees(pitchRotation));
            pose.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }

        VertexConsumer consumer = buffer.getBuffer(PROJECTILE_RENDER_TYPE);
        int argb = entity.getColor() == 0 ? 0xCC2376DD : entity.getColor();
        if ((argb & 0xFF000000) == 0){
            argb |= 0xFF000000;
        }

        pose.mulPose(Axis.YP.rotationDegrees(90.0F));
        drawBillboardTex(
                pose, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                0.55F, 0.75F, argb
        );
        pose.popPose();
    }

    private static AABB getVisualBounds(NarcissusFluidProjectile entity) {
        AABB entityBounds = entity.getBoundingBoxForCulling();
        BoundsAccumulator bounds = new BoundsAccumulator(entityBounds);
        includeTrail(bounds, entity.trail);
        includeTrail(bounds, entity.shortTrail);
        return bounds.toAabb().inflate(VISUAL_BOUNDS_INFLATE);
    }

    private static void includeTrail(BoundsAccumulator bounds, DTClientTrail trail) {
        for (DTClientTrail.Point point : trail.points()) {
            Vec3 position = point.getRawPosition();
            bounds.include(position.x, position.y, position.z);
        }
    }

    private static double distanceToSqr(AABB bounds, double x, double y, double z) {
        double dx = x < bounds.minX ? bounds.minX - x : x > bounds.maxX ? x - bounds.maxX : 0.0D;
        double dy = y < bounds.minY ? bounds.minY - y : y > bounds.maxY ? y - bounds.maxY : 0.0D;
        double dz = z < bounds.minZ ? bounds.minZ - z : z > bounds.maxZ ? z - bounds.maxZ : 0.0D;
        return dx * dx + dy * dy + dz * dz;
    }

    private static void drawBillboardTex(
            PoseStack pose, VertexConsumer consumer, int light, int overlay,
            float width, float height, int argb) {
        Matrix4f matrix = pose.last().pose();
        Matrix3f normal = pose.last().normal();
        int alpha = argb >>> 24 & 255;
        int red = argb >>> 16 & 255;
        int green = argb >>> 8 & 255;
        int blue = argb & 255;

        vertex(consumer, matrix, normal, -width, -height, 0.0F, red, green, blue, alpha, 0.0F, 1.0F, light, overlay, 1.0F);
        vertex(consumer, matrix, normal, width, -height, 0.0F, red, green, blue, alpha, 1.0F, 1.0F, light, overlay, 1.0F);
        vertex(consumer, matrix, normal, width, height, 0.0F, red, green, blue, alpha, 1.0F, 0.0F, light, overlay, 1.0F);
        vertex(consumer, matrix, normal, -width, height, 0.0F, red, green, blue, alpha, 0.0F, 0.0F, light, overlay, 1.0F);

        vertex(consumer, matrix, normal, -width, height, 0.0F, red, green, blue, alpha, 0.0F, 0.0F, light, overlay, -1.0F);
        vertex(consumer, matrix, normal, width, height, 0.0F, red, green, blue, alpha, 1.0F, 0.0F, light, overlay, -1.0F);
        vertex(consumer, matrix, normal, width, -height, 0.0F, red, green, blue, alpha, 1.0F, 1.0F, light, overlay, -1.0F);
        vertex(consumer, matrix, normal, -width, -height, 0.0F, red, green, blue, alpha, 0.0F, 1.0F, light, overlay, -1.0F);
    }

    private static void vertex(
            VertexConsumer consumer, Matrix4f matrix, Matrix3f normal,
            float x, float y, float z,
            int red, int green, int blue, int alpha,
            float u, float v, int light, int overlay, float normalZ) {
        consumer.vertex(matrix, x, y, z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0.0F, 0.0F, normalZ)
                .endVertex();
    }

    @Override
    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return 15;
    }

    private static final class BoundsAccumulator {
        private double minX;
        private double minY;
        private double minZ;
        private double maxX;
        private double maxY;
        private double maxZ;

        private BoundsAccumulator(AABB bounds) {
            minX = bounds.minX;
            minY = bounds.minY;
            minZ = bounds.minZ;
            maxX = bounds.maxX;
            maxY = bounds.maxY;
            maxZ = bounds.maxZ;
        }

        private void include(double x, double y, double z) {
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }

        private AABB toAabb() {
            return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }
}
