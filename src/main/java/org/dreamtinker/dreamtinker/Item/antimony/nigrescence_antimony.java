package org.dreamtinker.dreamtinker.Item.antimony;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class nigrescence_antimony extends Item {
    public nigrescence_antimony(Properties p_41383_) {
        super(p_41383_);
    }
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.nigrescence_antimony.desc1").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.nigrescence_antimony.desc2").withStyle(ChatFormatting.BLACK));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
