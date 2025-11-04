package org.dreamtinker.dreamtinker.library.event;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.modifiers.hook.RightClickHook;
import org.dreamtinker.dreamtinker.utils.CuriosCompact;
import slimeknights.tconstruct.library.tools.item.IModifiable;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class PlayerRightClickEvent {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightEmptyClick(PlayerInteractEvent.RightClickEmpty event) {
        Player player = event.getEntity();
        if (player != null && player.level().isClientSide){
            ItemStack stack = player.getItemInHand(player.getUsedItemHand());
            if (stack.isEmpty()){//we only handle real Empty
                stack = CuriosCompact.findPreferredGlove(player);
                if (stack.getItem() instanceof IModifiable)
                    RightClickHook.handleRightClick(stack, player, EquipmentSlot.MAINHAND);
            }
        }
    }
}
