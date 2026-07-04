package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.enigmaticLegacy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import org.brahypno.dreamtinker.common.DreamtinkerTagKeys;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.dreamtinker.utils.CompatUtils.EnigmaticLegacyCompat;
import org.brahypno.esotericismtinker.utils.ETHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalMeleeDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.KnockbackModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.List;

import static slimeknights.tconstruct.library.json.math.ModifierFormula.*;

public class ELEnderSlayer extends Modifier implements MeleeDamageModifierHook, MeleeHitModifierHook {
    private static final IJsonPredicate<LivingEntity> ender = LivingEntityPredicate.tag(DreamtinkerTagKeys.EntityTypes.ENDER_ENTITY);

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, ModifierHooks.MELEE_HIT);
        hookBuilder.addModule(new ModifierTraitModule(DreamtinkerModifiers.cursed_ring_bound.getId(), 1, true));
        hookBuilder.addModule(ConditionalMeleeDamageModule.builder().target(ender).percent()
                                                          .formula()
                                                          .variable(LEVEL).constant(EnigmaticLegacyCompat.enderSlayerEndDamageBonusModifier()).multiply()
                                                          .variable(MULTIPLIER).multiply()
                                                          .variable(VALUE).multiply().build());
        hookBuilder.addModule(KnockbackModule.builder().entity(ender)
                                             .formula()
                                             .variable(LEVEL).constant(5f).multiply()
                                             .variable(VALUE).multiply().build());
        super.registerHooks(hookBuilder);
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        float knockbackPower = 1F;
        if (context.getAttacker() instanceof Player player && EnigmaticLegacyCompat.isTheCursedOne(player))
            if (EnigmaticLegacyCompat.isEndDweller(ETHelper.getLivingTarget(context.getTarget()))){
                knockbackPower += EnigmaticLegacyCompat.enderSlayerEndKnockbackBonusModifier() * modifier.getEffectiveLevel();
            }
        return knockback * knockbackPower;
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        if (context.getAttacker() instanceof Player player && EnigmaticLegacyCompat.isTheCursedOne(player)){
            LivingEntity target = ETHelper.getLivingTarget(context.getTarget());
            if (null != target && (EnigmaticLegacyCompat.isEndDweller(target) || target.getType().is(DreamtinkerTagKeys.EntityTypes.ENDER_ENTITY))){
                if (EnigmaticLegacyCompat.isTheEnd(player.level()) && player.level() == target.level()){
                    if (target instanceof EnderMan
                        && EnigmaticLegacyCompat.registeredAttackStrength(player) >= 1F){
                        damage = (damage + 100F) * 10F;
                    }
                    target.getPersistentData().putBoolean("EnderSlayerVictim", true);
                }
            }
        }
        return damage;
    }

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        final String fulfill = this.getTranslationKey() + "_fulfill";
        return Arrays.asList(Component.translatable(this.getTranslationKey() + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(fulfill + ".description").withStyle(ChatFormatting.GRAY));
    }
}
