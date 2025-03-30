package org.dreamtinker.dreamtinker.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.dreamtinker.dreamtinker.hook.LeftClickHook;
import slimeknights.tconstruct.library.tools.item.IModifiable;

public class PlayerEvent {
    public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event){
        Player player = event.getEntity();
        if (player!=null&&player.level.isClientSide) {
            ItemStack stack = player.getItemInHand(player.getUsedItemHand());
            if (stack.getItem() instanceof IModifiable) {
                EquipmentSlot slot = stack.getEquipmentSlot();
                LeftClickHook.handleLeftClick(stack,player,slot);
            }
        }
    }
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event){
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        if (player!=null) {
            BlockState state = player.level.getBlockState(pos);
            ItemStack stack = player.getItemInHand(player.getUsedItemHand());
            if (stack.getItem() instanceof IModifiable) {
                EquipmentSlot slot = stack.getEquipmentSlot();
                LeftClickHook.handleLeftClickBlock(stack,player,slot,state,pos);
            }
        }
    }
    public static void onLeftClickEntity(AttackEntityEvent event){
        Player player = event.getEntity();
        if (player!=null) {
            ItemStack stack = player.getItemInHand(player.getUsedItemHand());
            if (stack.getItem() instanceof IModifiable) {
                EquipmentSlot slot = stack.getEquipmentSlot();
                LeftClickHook.handleLeftClickEntity(stack,player,slot,event.getTarget());
            }
        }
    }
}
