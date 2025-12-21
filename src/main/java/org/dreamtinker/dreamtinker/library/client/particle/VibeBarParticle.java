package org.dreamtinker.dreamtinker.library.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class VibeBarParticle extends TextureSheetParticle {
    private final int targetId;
    private final Vec3 barDir;     // 水平单位向量
    private final Vec3 attackDir;  // 与 barDir 垂直的水平单位向量（≈ attacker->target）
    private final float along;
    private final float amplitude;
    private final float frequencyHz;
    private final float yFrac;
    private final float phase;
    private final float baseAlpha;

    protected VibeBarParticle(
            ClientLevel level, SpriteSet sprites, VibeBarParticleOptions opt,
            double x, double y, double z) {
        super(level, x, y, z, 0, 0, 0);

        this.targetId = opt.targetId();

        Vec3 d = new Vec3(opt.barDirX(), 0, opt.barDirZ());
        if (d.lengthSqr() < 1.0e-6)
            d = new Vec3(1, 0, 0);
        this.barDir = d.normalize();
        this.attackDir = new Vec3(barDir.z, 0, -barDir.x).normalize();

        this.along = opt.along();
        this.amplitude = opt.amplitude();
        this.frequencyHz = opt.frequencyHz();
        this.yFrac = opt.yFrac();
        this.phase = opt.phase();

        this.lifetime = opt.lifetimeTicks();

        // 颜色（ARGB）
        int argb = opt.argb();
        float a = ((argb >>> 24) & 255) / 255f;
        float r = ((argb >>> 16) & 255) / 255f;
        float g = ((argb >>> 8) & 255) / 255f;
        float b = (argb & 255) / 255f;

        this.setColor(r, g, b);
        this.alpha = a;
        this.baseAlpha = a;

        // 粒子视觉参数：大小、重力、速度（我们自己控制位置，所以速度为 0）
        this.quadSize = 1.30f;     // 0.06~0.16 都可调
        this.gravity = 0.0f;
        this.hasPhysics = false;

        this.pickSprite(sprites);
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime){
            this.remove();
            return;
        }

        Entity e = this.level.getEntity(this.targetId);
        if (!(e instanceof LivingEntity target) || !target.isAlive()){
            if (this.age < 2)
                return; // 容错等一下
            this.remove();
            return;
        }

        double y = target.getY() + target.getBbHeight() * this.yFrac;
        Vec3 base = new Vec3(target.getX(), y, target.getZ());

        double timeSec = this.level.getGameTime() / 20.0;

        double s = Math.sin(timeSec * this.frequencyHz * (Math.PI * 2.0) + this.phase);
        double hop = ((hashNoise(target.getId(), this.level.getGameTime()) - 0.5) * 2.0) * 0.35;
        double jitter = (s * 0.75 + hop * 0.25) * this.amplitude;

        Vec3 p = base.add(this.barDir.scale(this.along))
                     .add(this.attackDir.scale(jitter));

        // 维护上一帧坐标，保证插值正常
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.setPos(p.x, p.y, p.z);

        float flicker = 0.75f + 0.25f * (float) Math.sin(timeSec * 20.0 + this.phase);
        this.alpha = Mth.clamp(this.baseAlpha * flicker, 0f, 1f);
    }


    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        // 可选：fullbright，更像电
        return 0xF000F0;
    }

    private static double hashNoise(int id, long t) {
        long x = (id * 1103515245L + 12345L) ^ (t * 1013904223L);
        x ^= (x >>> 16);
        x *= 0x7feb352dL;
        x ^= (x >>> 15);
        x *= 0x846ca68bL;
        x ^= (x >>> 16);
        return (x & 0xFFFF) / (double) 0xFFFF;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<VibeBarParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {this.sprites = sprites;}

        @Override
        public Particle createParticle(
                VibeBarParticleOptions opt, ClientLevel level,
                double x, double y, double z,
                double xd, double yd, double zd) {
            return new VibeBarParticle(level, sprites, opt, x, y, z);
        }
    }
}