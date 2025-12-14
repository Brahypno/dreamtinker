package org.dreamtinker.dreamtinker.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.dreamtinker.dreamtinker.utils.TargetTracker;
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
    @Override
    public void dreamtinker$setMode(final Predicate<Entity> targetMode) {
        this.dreamtinker$targetMode = targetMode;
    }

    @Unique
    @Override
    public @Nullable Predicate<Entity> dreamtinker$getMode() {
        return dreamtinker$targetMode;
    }

    //set target mode to null after the entity is hit
    @Inject(method = "onHit", at = @At("TAIL"))
    private void dreamtinker$onHit(HitResult pResult, CallbackInfo ci) {
        if (pResult.getType() != HitResult.Type.MISS)
            dreamtinker$setMode(null);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void dreamtinker$moveTowardsTarget(CallbackInfo ci) {
        if (dreamtinker$targetMode != null && !onGround()){
            DTHelper.moveTowardsTarget(this);
        }
    }
}
