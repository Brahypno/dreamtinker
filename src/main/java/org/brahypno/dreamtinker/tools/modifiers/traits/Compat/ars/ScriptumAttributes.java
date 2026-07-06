package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.ars;

import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.UUID;
import java.util.function.BiConsumer;

import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.ArtsMaxManaBonus;
import static org.brahypno.dreamtinker.config.DreamtinkerCachedConfig.ArtsRegenBonus;

public class ScriptumAttributes extends NoLevelsModifier implements AttributesModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ATTRIBUTES);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (!tool.isBroken()){
            Attribute attribute = PerkAttributes.MANA_REGEN_BONUS.get();
            consumer.accept(attribute,
                            new AttributeModifier(UUID.nameUUIDFromBytes((slot.getName() + "." + getId() + "." + attribute.getDescriptionId()).getBytes()),
                                                  this.getTranslationKey(),
                                                  Math.min(ArtsRegenBonus.get(),
                                                           tool.getStats().get(ToolStats.ATTACK_SPEED) + tool.getStats().get(ToolStats.VELOCITY)),
                                                  AttributeModifier.Operation.ADDITION));
            attribute = PerkAttributes.MAX_MANA.get();
            consumer.accept(attribute,
                            new AttributeModifier(UUID.nameUUIDFromBytes((slot.getName() + "." + getId() + "." + attribute.getDescriptionId()).getBytes()),
                                                  this.getTranslationKey(),
                                                  Math.min(ArtsMaxManaBonus.get(),
                                                           3 * tool.getStats().get(ToolStats.MINING_SPEED) * tool.getStats().get(ToolStats.DRAW_SPEED)),
                                                  AttributeModifier.Operation.ADDITION));
        }
    }
}
