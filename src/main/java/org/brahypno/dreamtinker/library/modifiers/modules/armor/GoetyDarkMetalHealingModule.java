package org.brahypno.dreamtinker.library.modifiers.modules.armor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.brahypno.dreamtinker.library.compat.goety.GoetyMaterialUtil;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Heals a hungry player wearing a complete, usable dark-metal set once every two seconds.
 * Only the equipped chestplate performs the set check, so the four pieces cannot multiply the healing.
 */
public enum GoetyDarkMetalHealingModule implements ModifierModule, InventoryTickModifierHook {
    INSTANCE;

    public static final SingletonLoader<GoetyDarkMetalHealingModule> LOADER = new SingletonLoader<>(INSTANCE);
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.defaultHooks(ModifierHooks.INVENTORY_TICK);

    @Override
    public SingletonLoader<GoetyDarkMetalHealingModule> getLoader() {
        return LOADER;
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
        return DEFAULT_HOOKS;
    }

    @Override
    public void onInventoryTick(
            IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder,
            int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!world.isClientSide && isCorrectSlot && holder instanceof Player player
            && player.tickCount % 40 == 0 && stack.is(TinkerTags.Items.CHESTPLATES)
            && player.getFoodData().needsFood() && GoetyMaterialUtil.hasFullDarkMetalSet(player)){
            player.heal(1.0f);
        }
    }
}
