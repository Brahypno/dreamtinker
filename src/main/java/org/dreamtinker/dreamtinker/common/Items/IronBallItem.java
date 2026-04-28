package org.dreamtinker.dreamtinker.common.Items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.dreamtinker.dreamtinker.Entity.DreamtinkerEntityTypes;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.jetbrains.annotations.NotNull;

public class IronBallItem extends Item {
    public IronBallItem(Properties p_41383_) {
        super(p_41383_);
    }

    public InteractionResultHolder<ItemStack> use(Level p_41190_, Player p_41191_, InteractionHand p_41192_) {
        ItemStack itemstack = p_41191_.getItemInHand(p_41192_);
        p_41190_.playSound((Player) null, p_41191_.getX(), p_41191_.getY(), p_41191_.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5F,
                           0.4F / (p_41190_.getRandom().nextFloat() * 0.4F + 0.8F));
        p_41191_.getCooldowns().addCooldown(this, 20);
        if (!p_41190_.isClientSide){
            ThrownIronBall thrownIronBall = new ThrownIronBall(p_41190_, p_41191_);
            thrownIronBall.setItem(itemstack);
            thrownIronBall.shootFromRotation(p_41191_, p_41191_.getXRot(), p_41191_.getYRot(), 0.0F, 1.5F, 1.0F);
            p_41190_.addFreshEntity(thrownIronBall);
        }

        p_41191_.awardStat(Stats.ITEM_USED.get(this));
        if (!p_41191_.getAbilities().instabuild){
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, p_41190_.isClientSide());
    }

    public static class ThrownIronBall extends ThrowableItemProjectile {

        public ThrownIronBall(EntityType<? extends ThrowableItemProjectile> p_37442_, Level p_37443_) {
            super(p_37442_, p_37443_);
        }

        public ThrownIronBall(Level p_37499_, LivingEntity p_37500_) {
            super(DreamtinkerEntityTypes.ThrownIronBall.get(), p_37500_, p_37499_);
        }

        public ThrownIronBall(Level p_37394_, double p_37395_, double p_37396_, double p_37397_) {
            super(DreamtinkerEntityTypes.ThrownIronBall.get(), p_37395_, p_37396_, p_37397_, p_37394_);
        }


        @Override
        protected @NotNull Item getDefaultItem() {
            return DreamtinkerCommon.spiral_spin.get();
        }

        protected void onHitEntity(EntityHitResult p_37404_) {
            super.onHitEntity(p_37404_);
            Entity entity = p_37404_.getEntity();
            entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float) 6);
        }

        protected void onHit(HitResult p_37406_) {
            super.onHit(p_37406_);
            if (!this.level().isClientSide){
                this.level().broadcastEntityEvent(this, (byte) 3);
                this.discard();
            }

        }
    }
}
