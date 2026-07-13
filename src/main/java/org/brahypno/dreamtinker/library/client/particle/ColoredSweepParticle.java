package org.brahypno.dreamtinker.library.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class ColoredSweepParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final float scaleX;
    private final float scaleY;

    protected ColoredSweepParticle(
            ClientLevel level, double x, double y, double z,
            SpriteSet sprites, ColoredSweepOptions options) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.lifetime = 4;
        this.quadSize = options.size();
        this.scaleX = options.scaleX();
        this.scaleY = options.scaleY();
        this.rCol = options.r();
        this.gCol = options.g();
        this.bCol = options.b();
        this.alpha = options.alpha();
        this.roll = options.roll();
        this.oRoll = this.roll;
        this.hasPhysics = false;
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime){
            this.remove();
            return;
        }
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        Vec3 cameraPos = camera.getPosition();
        float centerX = (float) (Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x());
        float centerY = (float) (Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y());
        float centerZ = (float) (Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z());

        Quaternionf rotation = camera.rotation();
        float qx = rotation.x();
        float qy = rotation.y();
        float qz = rotation.z();
        float qw = rotation.w();

        float rightX = 1.0F - 2.0F * (qy * qy + qz * qz);
        float rightY = 2.0F * (qx * qy + qz * qw);
        float rightZ = 2.0F * (qx * qz - qy * qw);
        float upX = 2.0F * (qx * qy - qz * qw);
        float upY = 1.0F - 2.0F * (qx * qx + qz * qz);
        float upZ = 2.0F * (qy * qz + qx * qw);

        float renderRoll = Mth.lerp(partialTick, this.oRoll, this.roll);
        float cos = Mth.cos(renderRoll);
        float sin = Mth.sin(renderRoll);

        // 等价于 camera.rotation() * rotateZ(renderRoll)，但不创建 Quaternionf/Vector3f。
        float axisXX = rightX * cos + upX * sin;
        float axisXY = rightY * cos + upY * sin;
        float axisXZ = rightZ * cos + upZ * sin;
        float axisYX = -rightX * sin + upX * cos;
        float axisYY = -rightY * sin + upY * cos;
        float axisYZ = -rightZ * sin + upZ * cos;

        float size = this.getQuadSize(partialTick);
        float halfX = size * this.scaleX;
        float halfY = size * this.scaleY;
        float xX = axisXX * halfX;
        float xY = axisXY * halfX;
        float xZ = axisXZ * halfX;
        float yX = axisYX * halfY;
        float yY = axisYY * halfY;
        float yZ = axisYZ * halfY;

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(partialTick);

        vertex(consumer, centerX - xX - yX, centerY - xY - yY, centerZ - xZ - yZ, u1, v1, light);
        vertex(consumer, centerX - xX + yX, centerY - xY + yY, centerZ - xZ + yZ, u1, v0, light);
        vertex(consumer, centerX + xX + yX, centerY + xY + yY, centerZ + xZ + yZ, u0, v0, light);
        vertex(consumer, centerX + xX - yX, centerY + xY - yY, centerZ + xZ - yZ, u0, v1, light);
    }

    private void vertex(VertexConsumer consumer, float x, float y, float z, float u, float v, int light) {
        consumer.vertex(x, y, z)
                .uv(u, v)
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(light)
                .endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<ColoredSweepOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(
                ColoredSweepOptions options, ClientLevel level,
                double x, double y, double z,
                double xd, double yd, double zd) {
            return new ColoredSweepParticle(level, x, y, z, this.sprites, options);
        }
    }
}
