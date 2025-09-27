package org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.library.modifiers.DreamtinkerHook;
import org.dreamtinker.dreamtinker.library.modifiers.hook.LeftClickHook;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.*;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public interface InteractionInterface extends LeftClickHook, EntityInteractionModifierHook, InventoryTickModifierHook, UsingToolModifierHook, GeneralInteractionModifierHook {
    default void InteractionInterfaceInit(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, DreamtinkerHook.LEFT_CLICK, ModifierHooks.ENTITY_INTERACT, ModifierHooks.INVENTORY_TICK, ModifierHooks.TOOL_USING,
                            ModifierHooks.GENERAL_INTERACT);
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

    default InteractionResult onToolUse(IToolStackView var1, ModifierEntry var2, Player var3, InteractionHand var4, InteractionSource var5) {return InteractionResult.PASS;}

}
