package org.brahypno.dreamtinker.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class ProjectileTargetTracing {

    private static final Predicate<Entity> TARGET_ANY = entity -> true;

    public static void moveTowardsTarget(final Entity entity) {
        if (null == entity)
            return;

        Entity owner;
        Predicate<Entity> targetPredicate;

        if (entity instanceof Projectile projectile){
            owner = projectile.getOwner();
            Predicate<Entity> targetMode = projectile instanceof TargetTracker mode ? mode.dreamtinker$getMode() : null;
            targetPredicate = targetMode != null ? targetMode : TARGET_ANY;
        }else {
            owner = null;
            targetPredicate = TARGET_ANY;
        }

        List<LivingEntity> entities = entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(12),
                                                                        target -> targetPredicate.test(target) && target != owner && target.isAlive() &&
                                                                                  !(owner != null && target.isAlliedTo(owner)) &&
                                                                                  (!entity.level().isClientSide() || target != Minecraft.getInstance().player));

        LivingEntity nearest = entities.stream().min(Comparator.comparingDouble((e) -> e.distanceToSqr(entity))).orElse(null);
        if (nearest != null){
            Vec3 diff = nearest.position().add(0, nearest.getBbHeight() / 2, 0).subtract(entity.position());
            Vec3 newMotion = entity.getDeltaMovement().add(diff.normalize()).scale(0.75);
            entity.setDeltaMovement(newMotion);
        }
    }
}
