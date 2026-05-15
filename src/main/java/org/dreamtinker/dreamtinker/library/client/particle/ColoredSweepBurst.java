package org.dreamtinker.dreamtinker.library.client.particle;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class ColoredSweepBurst {
    private int rgb = 0xFFFFFF, alpha = 255;
    private float size = 1.0F, scaleX = 1.8F, scaleY = 0.55F, roll = 0.0F;
    private float forward = 0.65F, upward = 0.15F, side = 0.0F;

    public static ColoredSweepBurst create() {
        return new ColoredSweepBurst();
    }

    public ColoredSweepBurst color(int rgb, int alpha) {
        this.rgb = rgb;
        this.alpha = alpha;
        return this;
    }

    public ColoredSweepBurst shape(float size, float scaleX, float scaleY) {
        this.size = size;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        return this;
    }

    public ColoredSweepBurst angle(float roll) {
        this.roll = roll;
        return this;
    }

    public ColoredSweepBurst offset(float forward, float upward, float side) {
        this.forward = forward;
        this.upward = upward;
        this.side = side;
        return this;
    }

    public ColoredSweepBurst offsetSide(float side) {
        this.side = side;
        return this;
    }

    public void spawnFrom(Entity entity) {
        if (!(entity.level() instanceof ServerLevel level))
            return;

        Vec3 look = entity.getLookAngle();
        Vec3 flatLook = new Vec3(look.x, 0.0D, look.z);
        if (flatLook.lengthSqr() < 1.0E-6D)
            flatLook = Vec3.directionFromRotation(0.0F, entity.getYRot());

        flatLook = flatLook.normalize();
        Vec3 right = new Vec3(-flatLook.z, 0.0D, flatLook.x);

        Vec3 pos = entity.position()
                         .add(flatLook.scale(this.forward))
                         .add(right.scale(this.side))
                         .add(0.0D, entity.getBbHeight() * 0.55D + this.upward, 0.0D);

        level.sendParticles(
                ColoredSweepOptions.ofRGB(this.rgb, this.alpha, this.size, this.scaleX, this.scaleY, this.roll),
                pos.x, pos.y, pos.z,
                1,
                0.0D, 0.0D, 0.0D,
                0.0D
        );
    }
}
