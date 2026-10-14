package org.brahypno.dreamtinker.library.modifiers.modules.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import org.brahypno.dreamtinker.utils.TargetTracker;
import org.brahypno.dreamtinker.utils.ProjectileTargetTracing;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Enables the existing projectile tracking using serializable living entity predicates.
 */
public record ProjectileTrackingModule(
        IJsonPredicate<LivingEntity> target,
        IJsonPredicate<LivingEntity> holder,
        boolean lookTarget,
        float lookRange,
        ModifierCondition<IToolStackView> condition
) implements ModifierModule, ProjectileLaunchModifierHook, ModifierCondition.ConditionalModule<IToolStackView> {
    public static final RecordLoadable<ProjectileTrackingModule> LOADER = RecordLoadable.create(
            LivingEntityPredicate.LOADER.defaultField("target", ProjectileTrackingModule::target),
            LivingEntityPredicate.LOADER.defaultField("holder", ProjectileTrackingModule::holder),
            BooleanLoadable.INSTANCE.defaultField("look_target", false, ProjectileTrackingModule::lookTarget),
            FloatLoadable.FROM_ZERO.defaultField("look_range", 16.0f, ProjectileTrackingModule::lookRange),
            ModifierCondition.TOOL_FIELD,
            ProjectileTrackingModule::new);
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.defaultHooks(ModifierHooks.PROJECTILE_LAUNCH);

    @Override
    public void onProjectileLaunch(
            IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile,
            @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (condition.matches(tool, modifier) && holder.matches(shooter) && projectile instanceof TargetTracker tracker){
            // Apply to secondary projectiles too, preserving multishot tracking.
            if (lookTarget){
                Entity selected = ProjectileTargetTracing.getLookedEntity(shooter, lookRange);
                tracker.dreamtinker$lockTarget(selected instanceof LivingEntity living && target.matches(living) ? selected : null);
            }else {
                tracker.dreamtinker$setTargetPredicate(target);
            }
        }
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<ProjectileTrackingModule> getLoader() {
        return LOADER;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends ModuleBuilder.Stack<Builder> {
        private IJsonPredicate<LivingEntity> target = LivingEntityPredicate.ANY;
        private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;
        private boolean lookTarget;
        private float lookRange = 16.0f;

        public Builder target(IJsonPredicate<LivingEntity> target) {
            this.target = target;
            return this;
        }

        public Builder holder(IJsonPredicate<LivingEntity> holder) {
            this.holder = holder;
            return this;
        }

        /**
         * Locks to the entity under the shooter's crosshair at launch instead of searching nearby entities.
         */
        public Builder lookTarget(float range) {
            this.lookTarget = true;
            this.lookRange = range;
            return this;
        }

        public ProjectileTrackingModule build() {
            return new ProjectileTrackingModule(target, holder, lookTarget, lookRange, condition);
        }
    }
}
