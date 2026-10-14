package org.brahypno.dreamtinker.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.predicate.IJsonPredicate;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public interface TargetTracker {
    @Nullable
    Predicate<Entity> dreamtinker$getMode();

    void dreamtinker$setMode(final Predicate<Entity> targetMode);

    /**
     * Adapts a Mantle/Tinkers' Construct target predicate to the existing tracking mode.
     * The predicate is evaluated again for cached targets, so changing entity conditions
     * can invalidate a target and allow the projectile to select another one.
     */
    default void dreamtinker$setTargetPredicate(IJsonPredicate<LivingEntity> target) {
        dreamtinker$setMode(entity -> entity instanceof LivingEntity living && target.matches(living));
    }

    @Nullable
    Entity dreamtinker$getTrackedTarget();

    /**
     * Updates the reusable target selected by a general predicate without turning it into a hard lock.
     */
    void dreamtinker$setTrackedTarget(@Nullable Entity target);

    /**
     * Locks a projectile to the already-resolved target, avoiding all subsequent area searches.
     */
    void dreamtinker$lockTarget(@Nullable Entity target);

    boolean dreamtinker$isTargetLocked();

    int dreamtinker$getNextTargetScanTick();

    void dreamtinker$setNextTargetScanTick(int tick);

    void dreamtinker$clearTargetTracking();
}
