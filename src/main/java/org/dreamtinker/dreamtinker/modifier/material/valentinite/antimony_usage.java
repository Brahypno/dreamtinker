package org.dreamtinker.dreamtinker.modifier.material.valentinite;

import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.*;

public class antimony_usage extends Modifier implements ToolStatsModifierHook {
    private static final Double ATTACK_MULTIPLIER = AntimonyUsageAttack.get();
    private static final Double RANGE_MULTIPLIER = AntimonyUsageProj.get();
    private static final Double ARMOR_MULTIPLIER = AntimonyUsageArmor.get();
    private static final Double DUR_MULTIPLIER = AntimonyUsageDur.get();

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.TOOL_STATS);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        ToolStats.DURABILITY.multiply(builder, 1 + DUR_MULTIPLIER);

        ToolStats.ATTACK_DAMAGE.multiply(builder, 1 + ATTACK_MULTIPLIER);
        ToolStats.ATTACK_SPEED.multiply(builder, 1 + ATTACK_MULTIPLIER);
        ToolStats.MINING_SPEED.multiply(builder, 1 + ATTACK_MULTIPLIER);

        ToolStats.PROJECTILE_DAMAGE.multiply(builder, 1 + RANGE_MULTIPLIER);
        ToolStats.DRAW_SPEED.multiply(builder, 1 - RANGE_MULTIPLIER);

        ToolStats.ARMOR_TOUGHNESS.multiply(builder, 1 + ARMOR_MULTIPLIER);
        ToolStats.ARMOR.multiply(builder, 1 + ARMOR_MULTIPLIER);
        ToolStats.KNOCKBACK_RESISTANCE.multiply(builder, 1 + ARMOR_MULTIPLIER);
        ToolStats.BLOCK_AMOUNT.multiply(builder, 1 + ARMOR_MULTIPLIER);
    }
}
