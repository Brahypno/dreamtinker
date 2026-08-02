package org.brahypno.dreamtinker.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public class ProjectileTargetTracing {

    private static final Predicate<Entity> TARGET_ANY = entity -> true;
    private static final double TARGET_RANGE = 12.0D;
    private static final double LOCKED_KEEP_RANGE_SQR = 20.0D * 20.0D;
    private static final int RESCAN_INTERVAL_TICKS = 4;

    public static void moveTowardsTarget(final Entity entity) {
        if (null == entity)
            return;

        Entity owner;
        Predicate<Entity> targetPredicate;

        TargetTracker tracker = null;
        if (entity instanceof Projectile projectile){
            owner = projectile.getOwner();
            tracker = projectile instanceof TargetTracker mode ? mode : null;
            Predicate<Entity> targetMode = tracker == null ? null : tracker.dreamtinker$getMode();
            targetPredicate = targetMode != null ? targetMode : TARGET_ANY;
        }else {
            owner = null;
            targetPredicate = TARGET_ANY;
        }

        LivingEntity nearest = tracker == null
                               ? null
                               : validTarget(tracker.dreamtinker$getTrackedTarget(), entity, owner, targetPredicate);
        if (nearest != null && tracker.dreamtinker$isTargetLocked()
            && nearest.distanceToSqr(entity) > LOCKED_KEEP_RANGE_SQR){
            nearest = null;
        }

        if (nearest == null && tracker != null){
            tracker.dreamtinker$setTrackedTarget(null);
            // View Tracing resolves exactly one entity at launch. Once locked, never scan the area.
            if (tracker.dreamtinker$isTargetLocked()){
                return;
            }
            if (entity.tickCount < tracker.dreamtinker$getNextTargetScanTick()){
                return;
            }
            tracker.dreamtinker$setNextTargetScanTick(entity.tickCount + RESCAN_INTERVAL_TICKS);
        }

        if (nearest == null){
            List<LivingEntity> entities = entity.level().getEntitiesOfClass(
                    LivingEntity.class,
                    entity.getBoundingBox().inflate(TARGET_RANGE),
                    target -> validTarget(target, entity, owner, targetPredicate) != null);

            double nearestDistanceSqr = Double.MAX_VALUE;
            for (LivingEntity candidate : entities) {
                double distanceSqr = candidate.distanceToSqr(entity);
                if (distanceSqr < nearestDistanceSqr){
                    nearest = candidate;
                    nearestDistanceSqr = distanceSqr;
                }
            }
            if (tracker != null){
                tracker.dreamtinker$setTrackedTarget(nearest);
            }
        }

        if (nearest != null){
            Vec3 diff = nearest.position().add(0, nearest.getBbHeight() / 2, 0).subtract(entity.position());
            Vec3 newMotion = entity.getDeltaMovement().add(diff.normalize()).scale(0.75);
            entity.setDeltaMovement(newMotion);
        }
    }

    private static LivingEntity validTarget(
            Entity candidate, Entity projectile, Entity owner, Predicate<Entity> targetPredicate) {
        if (!(candidate instanceof LivingEntity living)
            || !living.isAlive()
            || candidate == owner
            || !targetPredicate.test(candidate)
            || owner != null && candidate.isAlliedTo(owner)
            || projectile.level().isClientSide() && candidate == Minecraft.getInstance().player){
            return null;
        }
        return living;
    }
}
