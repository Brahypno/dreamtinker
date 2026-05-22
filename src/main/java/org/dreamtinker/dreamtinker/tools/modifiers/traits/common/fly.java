package org.dreamtinker.dreamtinker.tools.modifiers.traits.common;

import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class fly extends Modifier implements InventoryTickModifierHook, EquipmentChangeModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK, ModifierHooks.EQUIPMENT_CHANGE);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!isCorrectSlot)
            return;
        if (world.isClientSide)
            return;
        if (holder instanceof ServerPlayer player && !player.isCreative() && !player.isSpectator() &&
            !player.getAbilities().mayfly){
            player.getAbilities().mayfly = true;   // 允许飞行
            player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
        }
    }

    @Override
    public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        if (context.getEntity() instanceof ServerPlayer player && !player.isCreative() && !player.isSpectator() &&
            !player.getAbilities().mayfly){
            player.getAbilities().mayfly = true;   // 允许飞行
            player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
        }
    }

    @Override
    public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
        if (context.getEntity() instanceof ServerPlayer player && !player.isCreative() && !player.isSpectator() &&
            player.getAbilities().mayfly){
            player.getAbilities().mayfly = false;   // 拒绝飞行
            player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
        }
    }
}
