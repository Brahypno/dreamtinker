package org.dreamtinker.dreamtinker.tools.modifiers.traits.common;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.ArmorLootingModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.LootingModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.HarvestEnchantmentsModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.LootingContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Map;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.LifeLootingBonus;

public class life_looting extends Modifier implements LootingModifierHook, ArmorLootingModifierHook, HarvestEnchantmentsModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.WEAPON_LOOTING, ModifierHooks.ARMOR_LOOTING, ModifierHooks.HARVEST_ENCHANTMENTS);
    }

    @Override
    public void updateHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, EquipmentContext equipment, EquipmentSlot slot, Map<Enchantment, Integer> map) {
        int cur = map.getOrDefault(Enchantments.BLOCK_FORTUNE, 0);
        int next = life_looting_bonus(context.getLiving(), cur);
        if (next > 0)
            map.put(Enchantments.BLOCK_FORTUNE, next);
    }

    @Override
    public int updateLooting(IToolStackView iToolStackView, ModifierEntry modifierEntry, LootingContext lootingContext, int i) {
        return life_looting_bonus(lootingContext.getHolder(), i);
    }

    @Override
    public int updateArmorLooting(IToolStackView iToolStackView, ModifierEntry modifierEntry, LootingContext lootingContext, EquipmentContext equipmentContext, EquipmentSlot equipmentSlot, int i) {
        return life_looting_bonus(lootingContext.getHolder(), i);
    }

    private int life_looting_bonus(LivingEntity entity, int i) {
        i += (int) ((20 - entity.getMaxHealth()) * LifeLootingBonus.get());
        return i;
    }

    public @NotNull Component getDisplayName(int level) {
        return this.isNoLevels() ? super.getDisplayName() : super.getDisplayName(level);
    }

    public boolean isNoLevels() {return true;}
}
