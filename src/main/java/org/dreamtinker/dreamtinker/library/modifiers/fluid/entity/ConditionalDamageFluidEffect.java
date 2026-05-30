package org.dreamtinker.dreamtinker.library.modifiers.fluid.entity;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.dreamtinker.dreamtinker.library.modifiers.fluid.FluidEffectOwnerHelper;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect.DamageTypePair;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;

import javax.annotation.Nullable;

/**
 * Deals conditional damage to a living target.
 * <p>
 * - target: Mantle LivingEntity predicate.
 * - damage: damage dealt when the predicate matches.
 * - fallback_damage: damage dealt when the predicate does not match.
 * - damage_type: optional melee/ranged damage type pair.
 * <p>
 * If damage_type is null, this falls back to context.createDamageSource().
 */
public record ConditionalDamageFluidEffect(
        IJsonPredicate<LivingEntity> target,
        float damage,
        float fallbackDamage,
        @Nullable DamageTypePair damageType
) implements FluidEffect<FluidEffectContext.Entity> {

    public static final RecordLoadable<ConditionalDamageFluidEffect> LOADER = RecordLoadable.create(
            LivingEntityPredicate.LOADER.requiredField("target", ConditionalDamageFluidEffect::target),
            FloatLoadable.FROM_ZERO.requiredField("damage", ConditionalDamageFluidEffect::damage),
            FloatLoadable.FROM_ZERO.defaultField("fallback_damage", 0.0f, ConditionalDamageFluidEffect::fallbackDamage),
            DamageTypePair.LOADER.nullableField("damage_type", ConditionalDamageFluidEffect::damageType),
            ConditionalDamageFluidEffect::new
    );

    public ConditionalDamageFluidEffect(
            IJsonPredicate<LivingEntity> target,
            float damage,
            float fallbackDamage,
            ResourceKey<DamageType> damageType
    ) {
        this(target, damage, fallbackDamage, new DamageTypePair(damageType, damageType));
    }

    public ConditionalDamageFluidEffect(
            IJsonPredicate<LivingEntity> target,
            float damage,
            float fallbackDamage,
            ResourceKey<DamageType> meleeDamageType,
            ResourceKey<DamageType> rangedDamageType
    ) {
        this(target, damage, fallbackDamage, new DamageTypePair(meleeDamageType, rangedDamageType));
    }

    @Override
    public RecordLoadable<ConditionalDamageFluidEffect> getLoader() {
        return LOADER;
    }

    @Override
    public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext.Entity context, FluidAction action) {
        LivingEntity livingTarget = context.getLivingTarget();
        if (livingTarget == null || !livingTarget.isAlive()){
            return 0;
        }

        float value = level.value();
        float chosenDamage = target.matches(livingTarget) ? damage : fallbackDamage;
        if (chosenDamage <= 0){
            return 0;
        }

        if (action.simulate()){
            return value;
        }

        DamageSource source = createDamageSource(context);

        boolean success = ToolAttackUtil.attackEntitySecondary(
                source,
                chosenDamage * value,
                context.getTarget(),
                livingTarget,
                true
        );

        return success ? value : 0;
    }

    private DamageSource createDamageSource(FluidEffectContext.Entity context) {
        DamageSource originalSource = context.createDamageSource();

        if (damageType == null){
            return originalSource;
        }

        LivingEntity entity = FluidEffectOwnerHelper.getLivingOwner(originalSource);
        Projectile projectile = context.getProjectile();

        if (projectile != null){
            return TinkerDamageTypes.source(
                    context.getLevel().registryAccess(),
                    damageType.ranged(),
                    projectile,
                    entity
            );
        }

        return TinkerDamageTypes.source(
                context.getLevel().registryAccess(),
                damageType.melee(),
                entity
        );
    }

    @Override
    public Component getDescription(RegistryAccess registryAccess) {
        String key = FluidEffect.getTranslationKey(getLoader());

        if (damageType != null){
            DamageType type = registryAccess
                    .registryOrThrow(Registries.DAMAGE_TYPE)
                    .get(damageType.melee());

            if (type != null){
                key += "." + type.msgId();
            }
        }

        return Component.translatable(key, damage, fallbackDamage);
    }
}