package org.dreamtinker.dreamtinker.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.dreamtinker.dreamtinker.hook.LeftClickHook;
import slimeknights.tconstruct.library.tools.item.IModifiable;

public class PlayerLeftClickEvent {
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        if (player != null && player.level().isClientSide){
            ItemStack stack = player.getItemInHand(player.getUsedItemHand());
            if (stack.getItem() instanceof IModifiable)
                LeftClickHook.handleLeftClick(stack, player,
                                              InteractionHand.MAIN_HAND == player.getUsedItemHand() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
    }

    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        if (player != null){
            BlockState state = player.level().getBlockState(pos);
            ItemStack stack = player.getItemInHand(player.getUsedItemHand());
            if (stack.getItem() instanceof IModifiable)
                LeftClickHook.handleLeftClickBlock(stack, player,
                                                   InteractionHand.MAIN_HAND == player.getUsedItemHand() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND,
                                                   state, pos);

        }
    }

    public static void onLeftClickEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player != null){
            ItemStack stack = player.getItemInHand(player.getUsedItemHand());
            if (stack.getItem() instanceof IModifiable)
                LeftClickHook.handleLeftClickEntity(stack, player,
                                                    InteractionHand.MAIN_HAND == player.getUsedItemHand() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND,
                                                    event.getTarget());
            
        }
    }
}
