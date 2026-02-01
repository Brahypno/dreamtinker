package org.dreamtinker.dreamtinker.tools.modifiers.tools.narcissus_wing;

import net.minecraft.network.chat.Component;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierRequirementsModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.modules.combat.FieryAttackModule;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.flamingMemoryStatusBoost;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.flamingMemoryLifeDrain;
import static org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers.memory_base;

public class FlamingMemory extends BattleModifier {
    private final Component errorMessage =
            Component.translatable("modifier.dreamtinker.flaming_memory.requirements");

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addModule(new FieryAttackModule(LevelingValue.eachLevel(5)));
        hookBuilder.addModule(ModifierRequirementsModule.builder().requireModifier(memory_base.getId(), 1)
                                                        .modifierKey(DreamtinkerModifiers.flaming_memory.getId()).build());
        super.registerHooks(hookBuilder);
    }

    private int levels(IToolContext tool) {
        return tool.getModifierLevel(this.getId()) + tool.getModifierLevel(DreamtinkerModifiers.memory_base.getId()) +
               tool.getModifierLevel(DreamtinkerModifiers.Ids.icy_memory) + tool.getModifierLevel(DreamtinkerModifiers.Ids.hate_memory) +
               tool.getModifierLevel(DreamtinkerModifiers.Ids.soul_core);
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        context.getAttacker().setHealth(Math.max(context.getAttacker().getHealth() - 2, 1));
        return knockback;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        context.getAttacker().heal((float) (damageDealt * levels(tool) * flamingMemoryLifeDrain.get()));
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        float proj_damage = builder.getStat(ToolStats.PROJECTILE_DAMAGE) * 100;// changed to displayed one
        float velocity = builder.getStat(ToolStats.VELOCITY);
        float accuracy = builder.getStat(ToolStats.ACCURACY);
        float draw_speed = builder.getStat(ToolStats.DRAW_SPEED);
        float attack_damage = builder.getStat(ToolStats.ATTACK_DAMAGE);
        float attack_speed = builder.getStat(ToolStats.ATTACK_SPEED);

        if (attack_damage < proj_damage * velocity)
            ToolStats.ATTACK_DAMAGE.add(builder, proj_damage * velocity - attack_damage);
        else
            ToolStats.ATTACK_DAMAGE.multiply(builder, proj_damage * velocity * levels(context) * flamingMemoryStatusBoost.get());
        if (attack_speed < draw_speed * accuracy)
            ToolStats.ATTACK_SPEED.add(builder, draw_speed * accuracy - attack_speed);
        else
            ToolStats.ATTACK_SPEED.multiply(builder, draw_speed * accuracy * levels(context) * flamingMemoryStatusBoost.get());
    }
}
