package org.dreamtinker.dreamtinker.common.event.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemTooltip {
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent e) {
        if (e.getItemStack().getItem().equals(DreamtinkerCommon.narcissus.get().asItem())){
            e.getToolTip().add(Component.translatable("tooltip.dreamtinker.narcissus_1").withStyle(ChatFormatting.GREEN));
            e.getToolTip().add(Component.translatable("tooltip.dreamtinker.narcissus_2").withStyle(ChatFormatting.GREEN));
        }
    }
}
