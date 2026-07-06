package org.brahypno.dreamtinker.tools.modifiers.tools.narcissus_wing;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierRequirementsModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modules.combat.FieryAttackModule;

import java.util.UUID;
import java.util.function.BiConsumer;

import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.flamingMemoryStatusBoost;
import static org.brahypno.dreamtinker.tools.DreamtinkerModifiers.memory_base;

public class FlamingMemory extends Modifier implements MeleeHitModifierHook, MonsterMeleeHitModifierHook, ToolStatsModifierHook, AttributesModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.TOOL_STATS, ModifierHooks.ATTRIBUTES);
        hookBuilder.addModule(new FieryAttackModule(LevelingValue.eachLevel(5)));
        hookBuilder.addModule(ModifierRequirementsModule.builder().requireModifier(memory_base.getId(), 1)
                                                        .modifierKey(DreamtinkerModifiers.flaming_memory.getId()).build());
        super.registerHooks(hookBuilder);
    }

    private int levels(IToolContext tool) {
        return tool.getModifierLevel(this.getId()) + tool.getModifierLevel(DreamtinkerModifiers.memory_base.getId()) +
               tool.getModifierLevel(DreamtinkerModifiers.Ids.icy_memory) + tool.getModifierLevel(DreamtinkerModifiers.Ids.hate_memory) +
               tool.getModifierLevel(DreamtinkerModifiers.Ids.soul_core) + tool.getModifierLevel(TinkerModifiers.expanded.get()) * 2;
    }


    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (!tool.isBroken()){
            Attribute attribute = ForgeMod.ENTITY_REACH.get();
            consumer.accept(attribute,
                            new AttributeModifier(UUID.nameUUIDFromBytes((slot.getName() + "." + getId() + "." + attribute.getDescriptionId()).getBytes()),
                                                  this.getTranslationKey(),
                                                  3 + levels(tool),
                                                  AttributeModifier.Operation.ADDITION));
        }
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        float proj_damage = builder.getStat(ToolStats.PROJECTILE_DAMAGE) * 100;// changed to displayed one
        float velocity = builder.getStat(ToolStats.VELOCITY);
        float accuracy = builder.getStat(ToolStats.ACCURACY);
        float draw_speed = builder.getStat(ToolStats.DRAW_SPEED);

        ToolStats.ATTACK_DAMAGE.multiply(builder, proj_damage * velocity * levels(context) * flamingMemoryStatusBoost.get());
        ToolStats.ATTACK_SPEED.multiply(builder, 1 + draw_speed * accuracy * levels(context) * flamingMemoryStatusBoost.get());
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        afterMeleeHit(tool, modifier, context, damage);
    }
}
