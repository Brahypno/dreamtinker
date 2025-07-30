package org.dreamtinker.dreamtinker.modifier.base.baseinterface;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.List;
import java.util.function.BiConsumer;

public interface BasicInterface extends ToolDamageModifierHook, ModifierRemovalHook, TooltipModifierHook, ToolStatsModifierHook, AttributesModifierHook {
    default void BasicInterfaceInit(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.REMOVE, ModifierHooks.TOOLTIP,ModifierHooks.TOOL_DAMAGE,ModifierHooks.TOOL_STATS,ModifierHooks.ATTRIBUTES);
    }

    default int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @javax.annotation.Nullable LivingEntity holder) {
        return this.modifierDamageTool(tool,modifier,amount,holder);
    }

    default Component onRemoved(@NotNull IToolStackView tool, @NotNull Modifier modifier) {
        return this.onModifierRemoved(tool,modifier);
    }

    default int modifierDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
        return amount;
    }

    default Component onModifierRemoved(IToolStackView tool, Modifier modifier) {
        return null;
    }

    default void addTooltip(IToolStackView tool, @NotNull ModifierEntry modifier, @javax.annotation.Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {}

    default void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {}
    default void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer){}
}
