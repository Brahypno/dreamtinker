package org.dreamtinker.dreamtinker.Item.antimony;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;

public class star_regulus extends Item{

    public star_regulus(Item.Properties p_41383_) {
        super(p_41383_);
    }
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.star_regulus.desc1").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.star_regulus.desc2").withStyle(ChatFormatting.GOLD));
        super.appendHoverText(stack, level, tooltip, flag);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // 仅服务器执行效果
        if (!level.isClientSide) {
            player.startUsingItem(hand);
            /* manual gain advancement
            Advancement advancement = serverPlayer.server.getAdvancements()
                    .getAdvancement(TAG_MO);
            if (advancement != null) {
                AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
                for (String key : progress.getRemainingCriteria()) {
                    progress.grantProgress(key);
                }
            }*/

        }

        // 右键动画反馈
        return InteractionResultHolder.success(stack);
    }
    @Override
    public int getUseDuration(ItemStack stack) {
        return 200; // 任意大于 0 的值即可
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }
}
