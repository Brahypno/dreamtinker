package org.dreamtinker.dreamtinker.common.Items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.StarRegulusMaxHP;

public class star_regulus extends Item {
    private static final UUID HEALTH_BOOST_ID = UUID.fromString("e7a5d3c2-91f8-4b67-a1e3-cf0a9b8d6e5f");
    private static final String TAG_STAR_REGULUS = "star_regulus";

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
        if (!level.isClientSide){
            player.startUsingItem(hand);
            AttributeInstance attr = player.getAttribute(Attributes.MAX_HEALTH);
            if (attr != null && attr.getModifier(HEALTH_BOOST_ID) == null){
                attr.addPermanentModifier(
                        new AttributeModifier(HEALTH_BOOST_ID, TAG_STAR_REGULUS, StarRegulusMaxHP.get(), AttributeModifier.Operation.ADDITION));
            }
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
