package org.dreamtinker.dreamtinker.library.modifiers.fluid;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;

import javax.annotation.Nullable;

public final class FluidEffectOwnerHelper {
    private FluidEffectOwnerHelper() {}

    /**
     * Uses FluidEffectContext#createDamageSource() first, then attempts to find the living owner.
     * The owner may be null for generic/environment/unowned fluid effects.
     */
    @Nullable
    public static LivingEntity getLivingOwner(FluidEffectContext context) {
        return getLivingOwner(context.createDamageSource());
    }

    /**
     * Priority:
     * 1. DamageSource#getEntity(): causing entity / attacker
     * 2. DamageSource#getDirectEntity(): projectile/direct entity
     * 3. Projectile#getOwner()
     */
    @Nullable
    public static LivingEntity getLivingOwner(DamageSource source) {
        Entity causing = source.getEntity();
        if (causing instanceof LivingEntity living){
            return living;
        }

        Entity direct = source.getDirectEntity();
        if (direct instanceof Projectile projectile){
            Entity owner = projectile.getOwner();
            if (owner instanceof LivingEntity livingOwner){
                return livingOwner;
            }
        }

        if (direct instanceof LivingEntity directLiving){
            return directLiving;
        }

        return null;
    }
}