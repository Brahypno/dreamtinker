package org.dreamtinker.dreamtinker.tools.modifiers.traits.common;

import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.ArmorModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class fly extends ArmorModifier {
    @Override
    public void modifierOnInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
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
