package org.brahypno.dreamtinker.library.modifiers.fluid.entity;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext.Entity;
import slimeknights.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect.DamageTypePair;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;

import javax.annotation.Nullable;

/**
 * Damage effect that scales with target despair.
 * <p>
 * Formula:
 * finalDamage = (damage + min(maxBonus,
 * missingHealth * missingHealthRatio + harmfulEffectCount * negativeEffectBonus
 * )) * level
 *
 * @param damage              Base damage
 * @param missingHealthRatio  Bonus damage per missing health point
 * @param negativeEffectBonus Bonus damage per harmful effect on target
 * @param maxBonus            Max bonus before fluid level multiplier
 * @param damageType          Damage types to use when hitting
 */
public record DespairScalingDamageFluidEffect(
        float damage,
        float missingHealthRatio,
        float negativeEffectBonus,
        float maxBonus,
        @Nullable DamageTypePair damageType
) implements FluidEffect<FluidEffectContext.Entity> {

    public static final RecordLoadable<DespairScalingDamageFluidEffect> LOADER = RecordLoadable.create(
            FloatLoadable.FROM_ZERO.requiredField("damage", DespairScalingDamageFluidEffect::damage),
            FloatLoadable.FROM_ZERO.defaultField("missing_health_ratio", 0.15f, DespairScalingDamageFluidEffect::missingHealthRatio),
            FloatLoadable.FROM_ZERO.defaultField("negative_effect_bonus", 0.5f, DespairScalingDamageFluidEffect::negativeEffectBonus),
            FloatLoadable.FROM_ZERO.defaultField("max_bonus", 8.0f, DespairScalingDamageFluidEffect::maxBonus),
            DamageTypePair.LOADER.nullableField("damage_type", DespairScalingDamageFluidEffect::damageType),
            DespairScalingDamageFluidEffect::new
    );

    @Override
    public RecordLoadable<DespairScalingDamageFluidEffect> getLoader() {
        return LOADER;
    }

    @Override
    public float apply(FluidStack fluid, EffectLevel level, Entity context, FluidAction action) {
        float value = level.value();

        if (action.simulate()){
            return value;
        }

        LivingEntity livingTarget = context.getLivingTarget();
        if (livingTarget == null || !livingTarget.isAlive()){
            return 0;
        }

        Projectile projectile = context.getProjectile();
        LivingEntity entity = context.getEntity();

        DamageSource source;
        if (damageType != null){
            if (projectile != null){
                source = TinkerDamageTypes.source(
                        context.getLevel().registryAccess(),
                        damageType.ranged(),
                        projectile,
                        entity
                );
            }else {
                source = TinkerDamageTypes.source(
                        context.getLevel().registryAccess(),
                        damageType.melee(),
                        entity
                );
            }
        }else {
            source = context.createDamageSource();
        }

        float scaledDamage = computeDamage(livingTarget) * value;

        return ToolAttackUtil.attackEntitySecondary(
                source,
                scaledDamage,
                context.getTarget(),
                livingTarget,
                true
        ) ? value : 0;
    }

    private float computeDamage(LivingEntity target) {
        float missingHealth = Math.max(0.0f, target.getMaxHealth() - target.getHealth());

        int harmfulEffectCount = 0;
        for (MobEffectInstance instance : target.getActiveEffects()) {
            if (!instance.getEffect().isBeneficial()){
                harmfulEffectCount++;
            }
        }

        float bonus = missingHealth * missingHealthRatio + harmfulEffectCount * negativeEffectBonus;
        bonus = Math.min(bonus, maxBonus);

        return damage + bonus;
    }

    @Override
    public Component getDescription(RegistryAccess registryAccess) {
        String translationKey = FluidEffect.getTranslationKey(getLoader());

        if (this.damageType != null){
            DamageType damageType = registryAccess
                    .registryOrThrow(Registries.DAMAGE_TYPE)
                    .get(this.damageType.melee());

            if (damageType != null){
                translationKey += "." + damageType.msgId();
            }
        }

        return Component.translatable(translationKey, damage, maxBonus);
    }
}