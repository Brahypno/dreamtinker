package org.brahypno.dreamtinker.tools.modifiers.traits.Compact.bloodmagic;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import org.brahypno.dreamtinker.utils.CompactUtils.bloodmagic.BloodMagicTconLivingStats;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierTraitHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.data.ModifierIds;

import java.util.List;
import java.util.function.BiConsumer;

public class livingArmorModifier extends NoLevelsModifier implements TooltipModifierHook, AttributesModifierHook, ModifierTraitHook {
    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MODIFIER_TRAITS, ModifierHooks.TOOLTIP, ModifierHooks.ATTRIBUTES);
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        BloodMagicTconLivingStats.appendLivingTooltip(tool, tooltip, true);
    }

    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        BloodMagicTconLivingStats.addLivingAttributes(tool, slot, consumer);
    }

    @Override
    public void addTraits(IToolContext context, ModifierEntry modifier, TraitBuilder builder, boolean firstEncounter) {
        if (BloodMagicTconLivingStats.hasElytraUpgrade(context.getPersistentData())){
            builder.add(ModifierIds.wings, 1);
        }
        if (BloodMagicTconLivingStats.hasGildedUpgrade(context.getPersistentData())){
            builder.add(ModifierIds.gilded, 1);
        }

    }
}
