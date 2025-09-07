package org.dreamtinker.dreamtinker.Item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.entity.ThrownVoidpearl;

import java.util.List;

public class void_pearl extends EnderpearlItem {
    public void_pearl(Properties p_41188_) {
        super(p_41188_);
    }

    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.void_pearl.desc1").withStyle(ChatFormatting.DARK_PURPLE));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41190_, Player p_41191_, InteractionHand p_41192_) {
        ItemStack $$3 = p_41191_.getItemInHand(p_41192_);
        p_41190_.playSound((Player) null, p_41191_.getX(), p_41191_.getY(), p_41191_.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5F,
                           0.4F / (p_41190_.getRandom().nextFloat() * 0.4F + 0.8F));
        p_41191_.getCooldowns().addCooldown(this, 20);
        if (!p_41190_.isClientSide){
            ThrownEnderpearl $$4 = new ThrownVoidpearl(p_41190_, p_41191_);
            $$4.setItem($$3);
            $$4.shootFromRotation(p_41191_, p_41191_.getXRot(), p_41191_.getYRot(), 0.0F, 1.5F, 1.0F);
            p_41190_.addFreshEntity($$4);
        }

        p_41191_.awardStat(Stats.ITEM_USED.get(this));
        if (!p_41191_.getAbilities().instabuild){
            $$3.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess($$3, p_41190_.isClientSide());
    }
}
