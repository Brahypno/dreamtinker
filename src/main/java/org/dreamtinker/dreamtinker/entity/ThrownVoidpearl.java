package org.dreamtinker.dreamtinker.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.dreamtinker.dreamtinker.utils.DThelper;
import org.jetbrains.annotations.NotNull;

public class ThrownVoidpearl extends ThrownEnderpearl {
    public ThrownVoidpearl(EntityType<? extends ThrownEnderpearl> p_37491_, Level p_37492_) {
        super(p_37491_, p_37492_);
    }

    public ThrownVoidpearl(Level p_37499_, LivingEntity p_37500_) {
        super(p_37499_, p_37500_);
    }

    protected void onHitEntity(@NotNull EntityHitResult p_37502_) {
        super.onHitEntity(p_37502_);
        if (p_37502_.getEntity() instanceof LivingEntity le)
            for (int i = 0; i < 64; ++i)
                if (DThelper.teleport(le))
                    return;

    }

    protected void onHit(@NotNull HitResult p_37504_) {
        if (p_37504_.getType() == HitResult.Type.ENTITY && ((EntityHitResult) p_37504_).getEntity() instanceof LivingEntity){
            this.onHitEntity((EntityHitResult) p_37504_);
            if (!this.level().isClientSide)
                this.discard();
            return;
        }
        for (int i = 0; i < 32; ++i) {
            this.level()
                .addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * (double) 2.0F, this.getZ(), this.random.nextGaussian(),
                             (double) 0.0F, this.random.nextGaussian());
        }
        super.onHit(p_37504_);
    }
}
