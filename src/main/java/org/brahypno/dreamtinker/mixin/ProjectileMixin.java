package org.brahypno.dreamtinker.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.brahypno.dreamtinker.utils.ProjectileTargetTracing;
import org.brahypno.dreamtinker.utils.TargetTracker;
import org.brahypno.dreamtinker.utils.DTHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@Mixin(Projectile.class)
public abstract class ProjectileMixin extends Entity implements TargetTracker {
    public ProjectileMixin(final EntityType<?> type, final Level level) {
        super(type, level);
    }

    @Unique
    private Predicate<Entity> dreamtinker$targetMode = null;

    @Unique
    private int dreamtinker$trackedTargetId = -1;

    @Unique
    private boolean dreamtinker$targetLocked;

    @Unique
    private int dreamtinker$nextTargetScanTick;

    @Unique
    @Override
    public void dreamtinker$setMode(final Predicate<Entity> targetMode) {
        this.dreamtinker$targetMode = targetMode;
        this.dreamtinker$trackedTargetId = -1;
        this.dreamtinker$targetLocked = false;
        this.dreamtinker$nextTargetScanTick = 0;
    }

    @Unique
    @Override
    public @Nullable Predicate<Entity> dreamtinker$getMode() {
        return dreamtinker$targetMode;
    }

    @Unique
    @Override
    public @Nullable Entity dreamtinker$getTrackedTarget() {
        return dreamtinker$trackedTargetId < 0 ? null : level().getEntity(dreamtinker$trackedTargetId);
    }

    @Unique
    @Override
    public void dreamtinker$setTrackedTarget(@Nullable Entity target) {
        dreamtinker$trackedTargetId = target == null ? -1 : target.getId();
    }

    @Unique
    @Override
    public void dreamtinker$lockTarget(@Nullable Entity target) {
        dreamtinker$trackedTargetId = target == null ? -1 : target.getId();
        dreamtinker$targetLocked = target != null;
        dreamtinker$targetMode = null;
    }

    @Unique
    @Override
    public boolean dreamtinker$isTargetLocked() {
        return dreamtinker$targetLocked;
    }

    @Unique
    @Override
    public int dreamtinker$getNextTargetScanTick() {
        return dreamtinker$nextTargetScanTick;
    }

    @Unique
    @Override
    public void dreamtinker$setNextTargetScanTick(int tick) {
        dreamtinker$nextTargetScanTick = tick;
    }

    @Unique
    @Override
    public void dreamtinker$clearTargetTracking() {
        dreamtinker$targetMode = null;
        dreamtinker$trackedTargetId = -1;
        dreamtinker$targetLocked = false;
        dreamtinker$nextTargetScanTick = 0;
    }

    //set target mode to null after the entity is hit
    @Inject(method = "onHit", at = @At("TAIL"))
    private void dreamtinker$onHit(HitResult pResult, CallbackInfo ci) {
        if (pResult.getType() != HitResult.Type.MISS)
            dreamtinker$clearTargetTracking();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void dreamtinker$moveTowardsTarget(CallbackInfo ci) {
        DTHelper.trackProjectileTick((Projectile) (Object) this);
        if ((dreamtinker$targetMode != null || dreamtinker$trackedTargetId >= 0) && !onGround()){
            ProjectileTargetTracing.moveTowardsTarget(this);
        }
    }
}
