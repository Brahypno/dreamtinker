package org.dreamtinker.dreamtinker.modifier.base.baseinterface;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.hook.LeftClickHook;
import org.dreamtinker.dreamtinker.register.DreamtinkerHook;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public interface InteractionInterface extends LeftClickHook, EntityInteractionModifierHook, InventoryTickModifierHook, UsingToolModifierHook {
    default void InteractionInterfaceInit(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, DreamtinkerHook.LEFT_CLICK, ModifierHooks.ENTITY_INTERACT, ModifierHooks.INVENTORY_TICK, ModifierHooks.TOOL_USING);
    }

    default void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        this.modifierOnInventoryTick(tool, modifier, world, holder, itemSlot, isSelected, isCorrectSlot, stack);
    }

    default void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {

    }

    default void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    }

    default void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    }

    default void afterStopUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    }
}
