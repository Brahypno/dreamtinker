package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalMeleeDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalPowerModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.AbsorptionHitRate;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.*;

public class AbsorptionHit extends BattleModifier {
    private static final IJsonPredicate<LivingEntity> absorption = LivingEntityPredicate.simple(entity -> 0 < entity.getAbsorptionAmount());

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(ConditionalMeleeDamageModule.builder().attacker(absorption).percent()
                                                          .formula()
                                                          .variable(LEVEL).constant(AbsorptionHitRate.get().floatValue()).multiply()
                                                          .constant(1).add()
                                                          .variable(MULTIPLIER).multiply()
                                                          .variable(VALUE).multiply().build());
        hookBuilder.addModule(ConditionalPowerModule.builder().holder(absorption).percent()
                                                    .formula()
                                                    .variable(LEVEL).constant(AbsorptionHitRate.get().floatValue()).multiply()
                                                    .constant(1).add()
                                                    .variable(MULTIPLIER).multiply()
                                                    .variable(VALUE).multiply().build());
        super.registerHooks(hookBuilder);
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        if (absorption.matches(context.getAttacker()) && null != context.getLivingTarget())
            context.getLivingTarget().addEffect(new MobEffectInstance(MobEffects.WEAKNESS, modifier.getLevel() * 20, modifier.getLevel()));
        knockback *=
                (1 + absorption_buff(modifier.getLevel() * tool.getMultiplier(ToolStats.ATTACK_DAMAGE), 0 < context.getAttacker().getAbsorptionAmount()));
        return knockback;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (context.isCritical() && !context.getAttacker().level().isClientSide)
            onMonsterMeleeHit(tool, modifier, context, damageDealt);
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        float absorption = context.getAttacker().getAbsorptionAmount();
        float max_absorption = context.getAttacker().getMaxHealth() * 2;
        if (absorption < max_absorption)
            context.getAttacker()
                   .setAbsorptionAmount(
                           Math.min(absorption + damage * absorption_buff(modifier.getLevel() * tool.getMultiplier(ToolStats.ATTACK_DAMAGE), true),
                                    max_absorption));
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (shooter.level().isClientSide)
            return;
        float power = Math.max(ProjectileWithPower.getDamage(projectile), 1);
        float absorption = shooter.getAbsorptionAmount();
        if (power > 0 && absorption < shooter.getMaxHealth() * 2){
            shooter.setAbsorptionAmount(
                    (float) Math.min(absorption + projectile.getDeltaMovement().length() *
                                                  absorption_buff(modifier.getLevel() * tool.getMultiplier(ToolStats.VELOCITY), true),
                                     shooter.getMaxHealth() * 2));
        }
    }

    private float absorption_buff(float level, boolean addition) {
        return Math.max(-0.9f, (addition ? 1 : -1) * level * AbsorptionHitRate.get().floatValue());
    }

    @Override
    public boolean isNoLevels() {return false;}

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        return Arrays.asList(Component.translatable(this.getTranslationKey() + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(this.getTranslationKey() + ".description",
                                                    String.format("%.0f%%", AbsorptionHitRate.get() * 100))
                                      .withStyle(ChatFormatting.GRAY));
    }
}
