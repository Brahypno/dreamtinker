package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.ars;

import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.BasicInterface;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.UUID;
import java.util.function.BiConsumer;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.ArtsMaxManaBonus;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.ArtsRegenBonus;

public class ScriptumAttributes extends NoLevelsModifier implements BasicInterface {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.BasicInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (!tool.isBroken()){
            consumer.accept(PerkAttributes.MANA_REGEN_BONUS.get(),
                            new AttributeModifier(UUID.nameUUIDFromBytes((this.getId() + "." + slot.getName()).getBytes()),
                                                  PerkAttributes.MANA_REGEN_BONUS.get().getDescriptionId(),
                                                  Math.min(ArtsRegenBonus.get(), tool.getStats().get(ToolStats.ATTACK_SPEED)),
                                                  AttributeModifier.Operation.ADDITION));
            consumer.accept(PerkAttributes.MAX_MANA.get(),
                            new AttributeModifier(UUID.nameUUIDFromBytes((this.getId() + "." + slot.getName()).getBytes()),
                                                  PerkAttributes.MAX_MANA.get().getDescriptionId(),
                                                  Math.min(ArtsMaxManaBonus.get(),
                                                           3 * tool.getStats().get(ToolStats.MINING_SPEED) * tool.getStats().get(ToolStats.DRAW_SPEED)),
                                                  AttributeModifier.Operation.ADDITION));
        }
    }
}
