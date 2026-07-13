package org.brahypno.dreamtinker.library.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class VibeBarParticle extends TextureSheetParticle {
    private static final double TWO_PI = Math.PI * 2.0D;

    private final int targetId;
    private final double barDirX;
    private final double barDirZ;
    private final double attackDirX;
    private final double attackDirZ;
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

        double directionX = opt.barDirX();
        double directionZ = opt.barDirZ();
        double lengthSqr = directionX * directionX + directionZ * directionZ;
        if (lengthSqr < 1.0E-6D){
            directionX = 1.0D;
            directionZ = 0.0D;
        }else {
            double inverseLength = 1.0D / Math.sqrt(lengthSqr);
            directionX *= inverseLength;
            directionZ *= inverseLength;
        }

        this.barDirX = directionX;
        this.barDirZ = directionZ;
        this.attackDirX = directionZ;
        this.attackDirZ = -directionX;
        this.along = opt.along();
        this.amplitude = opt.amplitude();
        this.frequencyHz = opt.frequencyHz();
        this.yFrac = opt.yFrac();
        this.phase = opt.phase();
        this.lifetime = opt.lifetimeTicks();

        int argb = opt.argb();
        float a = ((argb >>> 24) & 255) / 255.0F;
        float r = ((argb >>> 16) & 255) / 255.0F;
        float g = ((argb >>> 8) & 255) / 255.0F;
        float b = (argb & 255) / 255.0F;
        this.setColor(r, g, b);
        this.alpha = a;
        this.baseAlpha = a;

        this.quadSize = 1.30F;
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.pickSprite(sprites);
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime){
            this.remove();
            return;
        }

        Entity entity = this.level.getEntity(this.targetId);
        if (!(entity instanceof LivingEntity target) || !target.isAlive()){
            if (this.age < 2)
                return;
            this.remove();
            return;
        }

        long gameTime = this.level.getGameTime();
        double timeSec = gameTime * 0.05D;
        double wave = Math.sin(timeSec * this.frequencyHz * TWO_PI + this.phase);
        double hop = ((hashNoise(target.getId(), gameTime) - 0.5D) * 2.0D) * 0.35D;
        double jitter = (wave * 0.75D + hop * 0.25D) * this.amplitude;

        double nextX = target.getX() + this.barDirX * this.along + this.attackDirX * jitter;
        double nextY = target.getY() + target.getBbHeight() * this.yFrac;
        double nextZ = target.getZ() + this.barDirZ * this.along + this.attackDirZ * jitter;

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.setPos(nextX, nextY, nextZ);

        float flicker = 0.75F + 0.25F * (float) Math.sin(timeSec * 20.0D + this.phase);
        this.alpha = Mth.clamp(this.baseAlpha * flicker, 0.0F, 1.0F);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    private static double hashNoise(int id, long time) {
        long x = (id * 1103515245L + 12345L) ^ (time * 1013904223L);
        x ^= x >>> 16;
        x *= 0x7feb352dL;
        x ^= x >>> 15;
        x *= 0x846ca68bL;
        x ^= x >>> 16;
        return (x & 0xFFFF) / (double) 0xFFFF;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<VibeBarParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                VibeBarParticleOptions opt, ClientLevel level,
                double x, double y, double z,
                double xd, double yd, double zd) {
            return new VibeBarParticle(level, this.sprites, opt, x, y, z);
        }
    }
}
