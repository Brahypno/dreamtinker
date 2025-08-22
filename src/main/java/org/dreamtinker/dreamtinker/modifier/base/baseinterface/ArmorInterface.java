package org.dreamtinker.dreamtinker.modifier.base.baseinterface;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.*;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public interface ArmorInterface extends OnAttackedModifierHook, DamageBlockModifierHook, EquipmentChangeModifierHook, ModifyDamageModifierHook, ProtectionModifierHook, ArmorWalkModifierHook {
    default void ArmorInterfaceInit(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED, ModifierHooks.DAMAGE_BLOCK, ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.MODIFY_DAMAGE, ModifierHooks.PROTECTION, ModifierHooks.BOOT_WALK);
    }

    default void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {}

    default boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
        return false;
    }

    default float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {return amount;}

    default void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {}

    default void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {}

    default void onEquipmentChange(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context, EquipmentSlot slotType) {}

    default float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {return modifierValue;}

    default void onWalk(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {}

}
