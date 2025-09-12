package org.dreamtinker.dreamtinker.Item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.dreamtinker.dreamtinker.utils.DThelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.voidpearlDamage;

public class void_pearl extends EnderpearlItem {
    public void_pearl(Properties p_41188_) {
        super(p_41188_);
    }

    public void appendHoverText(@NotNull ItemStack stack, Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.void_pearl.desc1").withStyle(ChatFormatting.DARK_PURPLE));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level p_41190_, Player p_41191_, @NotNull InteractionHand p_41192_) {
        ItemStack $$3 = p_41191_.getItemInHand(p_41192_);
        p_41190_.playSound(null, p_41191_.getX(), p_41191_.getY(), p_41191_.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5F,
                           0.4F / (p_41190_.getRandom().nextFloat() * 0.4F + 0.8F));
        p_41191_.getCooldowns().addCooldown(this, 20);
        if (!p_41190_.isClientSide){
            ThrownEnderpearl $$4 = new ThrownVoidPearl(p_41190_, p_41191_);
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

    private static class ThrownVoidPearl extends ThrownEnderpearl {
        public ThrownVoidPearl(EntityType<? extends ThrownEnderpearl> p_37491_, Level p_37492_) {
            super(p_37491_, p_37492_);
        }

        public ThrownVoidPearl(Level p_37499_, LivingEntity p_37500_) {
            super(p_37499_, p_37500_);
        }

        protected void onHitEntity(@NotNull EntityHitResult p_37502_) {
            super.onHitEntity(p_37502_);
            if (p_37502_.getEntity() instanceof LivingEntity le)
                for (int i = 0; i < 64; ++i)
                    if (DThelper.teleport(le)){
                        le.hurt(le.level().damageSources().fellOutOfWorld(), voidpearlDamage.get().floatValue());
                        return;
                    }

        }

        protected void onHit(@NotNull HitResult p_37504_) {
            if (p_37504_.getType() == HitResult.Type.ENTITY && ((EntityHitResult) p_37504_).getEntity() instanceof LivingEntity){
                this.onHitEntity((EntityHitResult) p_37504_);
                if (!this.level().isClientSide)
                    this.discard();
                return;
            }
            for (int i = 0; i < 32; ++i) {
                this.level()
                    .addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0F, this.getZ(),
                                 this.random.nextGaussian(),
                                 0.0F, this.random.nextGaussian());
            }
            if (null != this.getOwner() && this.getOwner() instanceof LivingEntity le)
                le.hurt(le.level().damageSources().fellOutOfWorld(), voidpearlDamage.get().floatValue());
            super.onHit(p_37504_);
        }
    }
}
