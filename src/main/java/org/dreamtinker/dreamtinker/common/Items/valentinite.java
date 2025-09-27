package org.dreamtinker.dreamtinker.common.Items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class valentinite extends Item {

    public valentinite(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // 仅服务器执行效果
        if (!level.isClientSide){
            player.startUsingItem(hand);
            // 添加中毒和恶心效果（持续时间单位为tick，20 ticks = 1秒）
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 10, 1)); // 10 秒 中毒 II
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20 * 15, 0)); // 15 秒 恶心 I
        }

        // 右键动画反馈
        return InteractionResultHolder.success(stack);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.EAT;
    }
}
