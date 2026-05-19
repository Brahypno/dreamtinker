package org.dreamtinker.dreamtinker.utils;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.entity.PartEntity;

public class DTDamageUtils {
    public static boolean damageHandler(Entity entity, DamageSource damageSource, float damageAmount) {
        LivingEntity victim = entity instanceof LivingEntity ? (LivingEntity) entity : null;
        if (entity instanceof PartEntity<?> pl){
            victim = pl.getControllingPassenger();
        }
        if (null != victim && !victim.level().isClientSide){
            return DTMethodHandler.invokeLivingHurt(victim, damageSource, damageAmount);
        }
        return entity.hurt(damageSource, damageAmount);
    }
}
