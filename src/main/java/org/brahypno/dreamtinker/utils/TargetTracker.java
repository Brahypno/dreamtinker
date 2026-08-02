package org.brahypno.dreamtinker.utils;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public interface TargetTracker {
    @Nullable
    Predicate<Entity> dreamtinker$getMode();

    void dreamtinker$setMode(final Predicate<Entity> targetMode);

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
