package org.dreamtinker.dreamtinker.library.client.particle;

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
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class ColoredSweepParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final float scaleX;
    private final float scaleY;

    protected ColoredSweepParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites, ColoredSweepOptions options) {
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

        float x = (float) (Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x());
        float y = (float) (Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y());
        float z = (float) (Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z());

        Quaternionf rotation = new Quaternionf(camera.rotation());

        float renderRoll = Mth.lerp(partialTick, this.oRoll, this.roll);
        if (renderRoll != 0.0F){
            rotation.rotateZ(renderRoll);
        }

        float size = this.getQuadSize(partialTick);
        float halfX = size * this.scaleX;
        float halfY = size * this.scaleY;

        Vector3f[] corners = new Vector3f[]{
                new Vector3f(-halfX, -halfY, 0.0F),
                new Vector3f(-halfX, halfY, 0.0F),
                new Vector3f(halfX, halfY, 0.0F),
                new Vector3f(halfX, -halfY, 0.0F)
        };

        for (Vector3f corner : corners) {
            corner.rotate(rotation);
            corner.add(x, y, z);
        }

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(partialTick);

        consumer.vertex(corners[0].x(), corners[0].y(), corners[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        consumer.vertex(corners[1].x(), corners[1].y(), corners[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        consumer.vertex(corners[2].x(), corners[2].y(), corners[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        consumer.vertex(corners[3].x(), corners[3].y(), corners[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
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
        public Particle createParticle(ColoredSweepOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new ColoredSweepParticle(level, x, y, z, this.sprites, options);
        }
    }
}