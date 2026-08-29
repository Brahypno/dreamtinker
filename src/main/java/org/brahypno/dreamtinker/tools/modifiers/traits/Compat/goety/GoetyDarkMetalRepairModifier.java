package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.goety;

import com.Polarice3.Goety.utils.ItemHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Independent soul-powered repair trait for dark-metal equipment.
 */
public class GoetyDarkMetalRepairModifier extends Modifier implements InventoryTickModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onInventoryTick(
            IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder,
            int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!world.isClientSide && (isCorrectSlot || isSelected)){
            // Delegate to Goety so SoulRepair, enchantment cost scaling, and ItemsRepairAmount stay config-correct.
            ItemHelper.repairTick(stack, holder, isSelected);
        }
    }
}
