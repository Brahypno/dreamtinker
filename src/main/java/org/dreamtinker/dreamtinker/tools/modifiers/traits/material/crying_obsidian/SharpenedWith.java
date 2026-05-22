package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.crying_obsidian;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalMeleeDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.mining.ConditionalMiningSpeedModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static slimeknights.tconstruct.library.json.math.ModifierFormula.*;

public class SharpenedWith extends Modifier implements MeleeHitModifierHook, MonsterMeleeHitModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        hookBuilder.addModule(ConditionalMeleeDamageModule.builder().target(LivingEntityPredicate.RAINING).percent()
                                                          .formula()
                                                          .constant(0.16f).variable(LEVEL).multiply()
                                                          .variable(MULTIPLIER).multiply()
                                                          .constant(1f).add()
                                                          .variable(VALUE).multiply().build())
                   .addModule(ConditionalMiningSpeedModule.builder()
                                                          .holder(LivingEntityPredicate.RAINING)
                                                          .formula()
                                                          .variable(MULTIPLIER).constant(16).multiply()
                                                          .variable(LEVEL).multiply()
                                                          .variable(VALUE).add().build());
        ;
        super.registerHooks(hookBuilder);
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        onMonsterMeleeHit(tool, modifier, context, damage);
        return knockback;
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        LivingEntity attacker = context.getAttacker();
        ServerLevel level = (ServerLevel) attacker.level();
        BlockPos pos = null == context.getLivingTarget() ? attacker.getOnPos() : context.getLivingTarget().getOnPos();
        if (canRainAt(level, pos)){
            if (!level.isRaining())
                level.setWeatherParameters(0, 6000, true, level.random.nextBoolean());
        }
    }

    private static boolean canRainAt(ServerLevel level, BlockPos pos) {
        if (!level.dimensionType().hasSkyLight())
            return false;                                // 头顶无遮挡
        var biome = level.getBiome(pos).value();
        return biome.getPrecipitationAt(pos) != Biome.Precipitation.NONE;
    }
}
