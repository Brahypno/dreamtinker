package org.dreamtinker.dreamtinker.tools.TNTarrow;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.dreamtinker.dreamtinker.register.DreamtinkerEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;

public class TNTArrowEntity extends AbstractArrow {
    public TNTArrowEntity(EntityType<? extends AbstractArrow> type, Level world) {
        super(type, world);
    }
    private ItemStack tntarrow = new ItemStack(Items.ARROW);
    private  int hitradius=5;

    public TNTArrowEntity(Level world, LivingEntity shooter, ItemStack stack) {
        super(DreamtinkerEntity.TNTARROW.get(), shooter, world);
        this.tntarrow = stack.copy();
    }

    protected void hitEntity(Entity entity){
        if(null !=this.getOwner() && this.getOwner() instanceof LivingEntity && entity.getUUID() != this.getOwner().getUUID()){
            ToolAttackUtil.attackEntity(ToolStack.from(this.tntarrow), (LivingEntity)this.getOwner(), InteractionHand.MAIN_HAND,entity,() -> 10, false);
        }
        else{
            try {
                ServerLevel serverLevel = (ServerLevel) this.level;
                FakePlayer fakeAttacker = FakePlayerFactory.getMinecraft(serverLevel);
                ToolAttackUtil.attackEntity(ToolStack.from(this.tntarrow), fakeAttacker, InteractionHand.MAIN_HAND,entity,() -> 10, false);
            } catch (SecurityException e) {
                // 捕获异常，说明 FakePlayer 被禁用
                ToolAttackUtil.attackEntity(ToolStack.from(this.tntarrow), (LivingEntity)this.getOwner(), InteractionHand.MAIN_HAND,entity,() -> 10, false);
            } catch (Exception ignored) {
            }

        }
    }
    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!this.level.isClientSide) {
            Entity entity = hitResult.getEntity();
            hitEntity(entity);
        }

    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!this.level.isClientSide) {
            Vec3 hitPos = result.getLocation();
            // 查找半径内的实体
            List<Entity> nearbyEntities = this.level.getEntities(null,
                    new AABB(hitPos.subtract(this.hitradius, this.hitradius, this.hitradius), hitPos.add(this.hitradius, this.hitradius, this.hitradius)));

            // 遍历实体列表
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity livingEntity) {
                    // 你可以根据需要对实体进行处理
                    hitEntity(livingEntity);
                }
            }
            hitEntity(this.getOwner());
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

