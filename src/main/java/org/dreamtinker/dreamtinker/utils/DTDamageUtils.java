package org.dreamtinker.dreamtinker.utils;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class DTDamageUtils {
    public static boolean damageHandler(@Nullable Entity entity, DamageSource damageSource, float damageAmount) {
        if (entity == null){
            return false;
        }

        LivingEntity victim = DTHelper.getLivingTarget(entity);
        if (null != victim && !victim.level().isClientSide){
            return DTMethodHandler.invokeLivingHurt(victim, damageSource, damageAmount);
        }
        return entity.hurt(damageSource, damageAmount);
    }
}
