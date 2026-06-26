package org.brahypno.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.brahypno.esotericismtinker.library.modifiers.modules.combat.AbsorptionGainModule;
import org.brahypno.esotericismtinker.utils.ETHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalMeleeDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalPowerModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.List;

import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.AbsorptionHitRate;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.*;

public class AbsorptionHit extends Modifier implements MeleeHitModifierHook {
    private static final IJsonPredicate<LivingEntity> absorption = LivingEntityPredicate.simple(entity -> 0 < entity.getAbsorptionAmount());

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(ConditionalMeleeDamageModule.builder().attacker(absorption).percent()
                                                          .formula()
                                                          .variable(LEVEL).constant(AbsorptionHitRate.get().floatValue()).multiply()
                                                          .variable(MULTIPLIER).multiply()
                                                          .constant(1).add()
                                                          .variable(VALUE).multiply().build());
        hookBuilder.addModule(ConditionalPowerModule.builder().holder(absorption).percent()
                                                    .formula()
                                                    .variable(LEVEL).constant(AbsorptionHitRate.get().floatValue()).multiply()
                                                    .variable(MULTIPLIER).multiply()
                                                    .constant(1).add()
                                                    .variable(VALUE).multiply().build());
        hookBuilder.addModule(AbsorptionGainModule.builder()
                                                  .ratio(LevelingValue.flat(AbsorptionHitRate.get().floatValue()))
                                                  .maxRatio(LevelingValue.flat(2.0f))
                                                  .buildWeapon());
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = ETHelper.getLivingTarget(context.getTarget());
        if (absorption.matches(context.getAttacker()) && null != target)
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, modifier.getLevel() * 20, modifier.getLevel()));
        return knockback;
    }


    public @NotNull List<Component> getDescriptionList(int level) {
        return Arrays.asList(Component.translatable(this.getTranslationKey() + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(this.getTranslationKey() + ".description",
                                                    String.format("%.0f%%", AbsorptionHitRate.get() * 100))
                                      .withStyle(ChatFormatting.GRAY));
    }
}
