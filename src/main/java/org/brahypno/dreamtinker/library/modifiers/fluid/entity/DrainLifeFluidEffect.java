package org.brahypno.dreamtinker.library.modifiers.fluid.entity;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
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
 * Effect that damages an entity and heals the fluid user.
 *
 * @param damage     Amount of damage to apply before fluid level scaling
 * @param healRatio  Ratio of dealt damage used to heal the source entity
 * @param damageType Damage types to use when hitting
 */
public record DrainLifeFluidEffect(
        float damage,
        float healRatio,
        @Nullable DamageTypePair damageType
) implements FluidEffect<FluidEffectContext.Entity> {

    public static final RecordLoadable<DrainLifeFluidEffect> LOADER = RecordLoadable.create(
            FloatLoadable.FROM_ZERO.requiredField("damage", DrainLifeFluidEffect::damage),
            FloatLoadable.FROM_ZERO.defaultField("heal_ratio", 0.5f, DrainLifeFluidEffect::healRatio),
            DamageTypePair.LOADER.nullableField("damage_type", DrainLifeFluidEffect::damageType),
            DrainLifeFluidEffect::new
    );

    public DrainLifeFluidEffect(float damage, float healRatio) {
        this(damage, healRatio, null);
    }

    @Override
    public RecordLoadable<DrainLifeFluidEffect> getLoader() {
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

        float scaledDamage = this.damage * value;

        boolean success = ToolAttackUtil.attackEntitySecondary(
                source,
                scaledDamage,
                context.getTarget(),
                livingTarget,
                true
        );

        if (success && entity != null && entity.isAlive() && entity != livingTarget && healRatio > 0){
            entity.heal(scaledDamage * healRatio);
        }

        return success ? value : 0;
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

        return Component.translatable(translationKey, damage, healRatio);
    }
}
