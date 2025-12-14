package org.dreamtinker.dreamtinker.utils;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public interface TargetTracker {
    @Nullable
    Predicate<Entity> dreamtinker$getMode();

    void dreamtinker$setMode(final Predicate<Entity> targetMode);
}
