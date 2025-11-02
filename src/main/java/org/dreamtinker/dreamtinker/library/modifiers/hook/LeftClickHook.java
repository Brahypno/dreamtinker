package org.dreamtinker.dreamtinker.library.modifiers.hook;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.dreamtinker.dreamtinker.library.modifiers.DreamtinkerHook;
import org.dreamtinker.dreamtinker.network.Dnetwork;
import org.dreamtinker.dreamtinker.network.LeftClickEmptyPacket;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Collection;


public interface LeftClickHook {
    default void onLeftClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {}

    default void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, BlockState state, BlockPos pos) {}

    default void onLeftClickEntity(AttackEntityEvent event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, Entity target) {}

    static void handleLeftClick(ItemStack stack, Player player, EquipmentSlot slot) {
        Level level = player.level();
        IToolStackView tool = ToolStack.from(stack);
        for (ModifierEntry entry : tool.getModifierList()) {
            entry.getHook(DreamtinkerHook.LEFT_CLICK).onLeftClickEmpty(tool, entry, player, level, slot);
        }
        if (level.isClientSide){
            Dnetwork.CHANNEL.sendToServer(new LeftClickEmptyPacket());
        }
    }

    static void handleLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, ItemStack stack, Player player, EquipmentSlot slot, BlockState state, BlockPos pos) {
        Level level = player.level();
        IToolStackView tool = ToolStack.from(stack);
        for (ModifierEntry entry : tool.getModifierList()) {
            entry.getHook(DreamtinkerHook.LEFT_CLICK).onLeftClickBlock(event, tool, entry, player, level, slot, state, pos);
        }
    }

    static void handleLeftClickEntity(AttackEntityEvent event, ItemStack stack, Player player, EquipmentSlot slot, Entity target) {
        Level level = player.level();
        IToolStackView tool = ToolStack.from(stack);
        for (ModifierEntry entry : tool.getModifierList()) {
            entry.getHook(DreamtinkerHook.LEFT_CLICK).onLeftClickEntity(event, tool, entry, player, level, slot, target);
        }
    }

    record AllMerger(Collection<LeftClickHook> modules) implements LeftClickHook {
        @Override
        public void onLeftClickEmpty(IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot) {
            for (LeftClickHook module : this.modules) {
                module.onLeftClickEmpty(tool, entry, player, level, equipmentSlot);
            }
        }

        @Override
        public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, BlockState state, BlockPos pos) {
            for (LeftClickHook module : this.modules) {
                module.onLeftClickBlock(event, tool, entry, player, level, equipmentSlot, state, pos);
            }
        }

        @Override
        public void onLeftClickEntity(AttackEntityEvent event, IToolStackView tool, ModifierEntry entry, Player player, Level level, EquipmentSlot equipmentSlot, Entity target) {
            for (LeftClickHook module : this.modules) {
                module.onLeftClickEntity(event, tool, entry, player, level, equipmentSlot, target);
            }
        }
    }
}
