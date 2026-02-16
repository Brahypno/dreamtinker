package org.dreamtinker.dreamtinker.tools.modifiers.events;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.UnbuildLimits;
import static org.dreamtinker.dreamtinker.tools.modifiers.traits.common.not_like_was.TAG_CHANGE_TIMES;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
public class GeneralPlayerCraftEvent {
    @SubscribeEvent
    public static void PlayerCraftEvent(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity().level().isClientSide)
            return;
        ItemStack item = event.getCrafting();
        if (item.getItem() instanceof IModifiable){
            ToolStack tool = ToolStack.from(item);
            if (!tool.getModifiers().getEntry(DreamtinkerModifiers.not_like_was.getId()).equals(ModifierEntry.EMPTY)){
                int times = tool.getPersistentData().getInt(TAG_CHANGE_TIMES);
                tool.getPersistentData().putInt(TAG_CHANGE_TIMES, ++times);
                tool.updateStack(item);
                if (UnbuildLimits.get() <= times){
                    event.getEntity().sendSystemMessage(Component.translatable("modifier.dreamtinker.tooltip.not_like_was_1").append(String.valueOf(times))
                                                                 .withStyle(DreamtinkerModifiers.not_like_was.get().getDisplayName().getStyle()));
                }else {
                    event.getEntity().sendSystemMessage(Component.translatable("modifier.dreamtinker.not_like_was.flavor")
                                                                 .withStyle(DreamtinkerModifiers.not_like_was.get().getDisplayName().getStyle()));
                }
            }
        }

    }
}
