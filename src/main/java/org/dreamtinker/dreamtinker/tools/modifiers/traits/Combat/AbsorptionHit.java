package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.AbsorptionHitRate;

public class AbsorptionHit extends BattleModifier {
    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        if (0 < context.getAttacker().getAbsorptionAmount() && null != context.getLivingTarget()){
            context.getLivingTarget().addEffect(new MobEffectInstance(MobEffects.WEAKNESS, modifier.getLevel() * 20, modifier.getLevel()));
            knockback *= (1 + absorption_buff(modifier.getLevel(), 0 < context.getAttacker().getAbsorptionAmount()));
        }
        return knockback;
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        damage *= (1 + absorption_buff(modifier.getLevel(), 0 < context.getAttacker().getAbsorptionAmount()));
        return damage;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (context.isCritical() && !context.getAttacker().level().isClientSide){
            float absorption = context.getAttacker().getAbsorptionAmount();
            if (absorption < context.getAttacker().getMaxHealth() * 2)
                context.getAttacker()
                       .setAbsorptionAmount(
                               Math.min(absorption + damageDealt * absorption_buff(modifier.getLevel(), true), context.getAttacker().getMaxHealth() * 2));
        }
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        float absorption = context.getAttacker().getAbsorptionAmount();
        if (absorption < context.getAttacker().getMaxHealth() * 2)
            context.getAttacker()
                   .setAbsorptionAmount(
                           Math.min(absorption + damage * absorption_buff(modifier.getLevel(), true), context.getAttacker().getMaxHealth() * 2));
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (shooter.level().isClientSide)
            return;
        projectile.setDeltaMovement(
                projectile.getDeltaMovement().scale(1 + absorption_buff(modifier.getLevel(), 0 < shooter.getAbsorptionAmount())));
        float percent = 0.05f * modifier.getEffectiveLevel();
        if (percent > 0){
            float power = Math.max(ProjectileWithPower.getDamage(projectile), 1);
            float absorption = shooter.getAbsorptionAmount();
            if (power > 0 && absorption < shooter.getMaxHealth() * 2){
                shooter.setAbsorptionAmount(
                        (float) Math.min(absorption + projectile.getDeltaMovement().length() * absorption_buff(modifier.getLevel(), true),
                                         shooter.getMaxHealth() * 2));
            }
        }
    }

    private float absorption_buff(int level, boolean addition) {
        return addition ? 1 : -1 * level * AbsorptionHitRate.get().floatValue();
    }

    @Override
    public boolean isNoLevels() {return false;}
}
