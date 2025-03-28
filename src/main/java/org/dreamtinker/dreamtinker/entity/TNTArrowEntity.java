package org.dreamtinker.dreamtinker.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.dreamtinker.dreamtinker.register.DreamtinkerEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;
import java.util.Objects;

import static slimeknights.tconstruct.library.tools.helper.ToolAttackUtil.NO_COOLDOWN;

public class TNTArrowEntity extends AbstractArrow {
    public TNTArrowEntity(EntityType<? extends AbstractArrow> type, Level world) {
        super(type, world);
    }
    private ItemStack tntarrow = new ItemStack(Items.ARROW);

    public TNTArrowEntity(Level world, LivingEntity shooter, ItemStack stack) {
        super(DreamtinkerEntity.TNTARROW.get(), shooter, world);
        this.tntarrow = stack.copy();
    }

    protected void hitEntity(Entity entity){
        if(null !=this.getOwner() && this.getOwner() instanceof LivingEntity && entity.getUUID() != this.getOwner().getUUID()){
            ToolAttackUtil.attackEntity(ToolStack.from(this.tntarrow), (LivingEntity)this.getOwner(), InteractionHand.OFF_HAND,entity,NO_COOLDOWN, false,Util.getSlotType(InteractionHand.OFF_HAND));

        }
        else{
            try {
                ServerLevel serverLevel = (ServerLevel) this.level;
                FakePlayer fakeAttacker = FakePlayerFactory.getMinecraft(serverLevel);
                ToolAttackUtil.attackEntity(ToolStack.from(this.tntarrow), fakeAttacker, InteractionHand.MAIN_HAND,entity,NO_COOLDOWN, false, Util.getSlotType(InteractionHand.OFF_HAND));
                fakeAttacker = null;
            } catch (SecurityException e) {
                // 捕获异常，说明 FakePlayer 被禁用
                ToolAttackUtil.attackEntity(ToolStack.from(this.tntarrow), (LivingEntity)this.getOwner(), InteractionHand.MAIN_HAND,entity,NO_COOLDOWN, false, Util.getSlotType(InteractionHand.OFF_HAND));
            } catch (Exception ignored) {
            }

        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!this.level.isClientSide) {
            float sound=2.0F;
            Vec3 hitPos = result.getLocation();
            // 查找半径内的实体
            int hitRadius = 5;
            List<Entity> nearbyEntities = this.level.getEntities(null,
                    new AABB(hitPos.subtract(hitRadius, hitRadius, hitRadius), hitPos.add(hitRadius, hitRadius, hitRadius)));

            // 遍历实体列表
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity livingEntity) {
                    // 你可以根据需要对实体进行处理
                    hitEntity(livingEntity);
                    sound++;
                }
            }
            if(Objects.requireNonNull(this.getOwner()).position().distanceTo(hitPos)<= hitRadius){
                hitEntity(this.getOwner());
                sound++;
            }
            this.playSound(SoundEvents.GENERIC_EXPLODE, sound, (1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F) * 0.7F);
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY; // 让箭矢无法被回收
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -5, 0.0));
        }
    }
}

