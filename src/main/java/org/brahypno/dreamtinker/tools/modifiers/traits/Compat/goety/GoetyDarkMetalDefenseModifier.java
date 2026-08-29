package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.goety;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.brahypno.dreamtinker.library.compat.goety.GoetyMaterialUtil;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Dark-metal behavior for armor and shields.
 */
public class GoetyDarkMetalDefenseModifier extends Modifier implements InventoryTickModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onInventoryTick(
            IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder,
            int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!world.isClientSide && isCorrectSlot && stack.is(TinkerTags.Items.CHESTPLATES)
            && holder instanceof Player player && player.tickCount % 40 == 0
            && player.getFoodData().needsFood() && GoetyMaterialUtil.hasFullDarkMetalSet(player)){
            player.heal(1.0f);
        }
    }
}
