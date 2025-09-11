package org.dreamtinker.dreamtinker.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import org.dreamtinker.dreamtinker.register.DreamtinkerEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.Util;

import java.util.List;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.TNTarrowRadius;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.TNTarrowgravity;
import static slimeknights.tconstruct.library.tools.helper.ToolAttackUtil.NO_COOLDOWN;

public class TNTArrowEntity extends AbstractArrow implements IEntityAdditionalSpawnData {
    public TNTArrowEntity(EntityType<? extends AbstractArrow> type, Level world) {
        super(type, world);
    }

    public ItemStack tntarrow = ItemStack.EMPTY;
    private static final EntityDataAccessor<ItemStack> DATA_TOOL =
            SynchedEntityData.defineId(TNTArrowEntity.class, EntityDataSerializers.ITEM_STACK);

    public TNTArrowEntity(Level world, LivingEntity shooter, ItemStack stack) {
        super(DreamtinkerEntity.TNTARROW.get(), shooter, world);
        this.tntarrow = stack;
        setToolStack(stack.copy());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TOOL, ItemStack.EMPTY);
    }

    public void setToolStack(ItemStack stack) {
        this.entityData.set(DATA_TOOL, stack.copy());
    }

    public ItemStack getToolStackSynced() {
        return this.entityData.get(DATA_TOOL);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ItemStack s = getToolStackSynced();
        if (!s.isEmpty())
            tag.put("Tool", s.save(new CompoundTag()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Tool"))
            setToolStack(ItemStack.of(tag.getCompound("Tool")));
    }

    // —— 初次生成时下发到客户端（关键） —— //
    @Override
    public void writeSpawnData(FriendlyByteBuf buf) {buf.writeItem(getToolStackSynced());}

    @Override
    public void readSpawnData(FriendlyByteBuf buf) {setToolStack(buf.readItem());}

    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getAddEntityPacket() {
        return net.minecraftforge.network.NetworkHooks.getEntitySpawningPacket(this);
    }

    protected void hitEntity(Entity entity) {
        if (null == this.getOwner() || !(this.getOwner() instanceof LivingEntity) || null == entity)
            return;
        entity.setInvulnerable(false);
        entity.invulnerableTime = 0;
        if (entity.getUUID() != this.getOwner().getUUID()){
            ToolAttackUtil.attackEntity(ToolStack.from(this.tntarrow), (LivingEntity) this.getOwner(), InteractionHand.OFF_HAND, entity, NO_COOLDOWN, false,
                                        Util.getSlotType(InteractionHand.OFF_HAND));
        }else {
            try {
                ServerLevel serverLevel = (ServerLevel) this.level();
                FakePlayer fakeAttacker = FakePlayerFactory.getMinecraft(serverLevel);
                ToolAttackUtil.attackEntity(ToolStack.from(this.tntarrow), fakeAttacker, InteractionHand.MAIN_HAND, entity, NO_COOLDOWN, false,
                                            Util.getSlotType(InteractionHand.OFF_HAND));
                fakeAttacker = null;
            }
            catch (SecurityException e) {
                // 捕获异常，说明 FakePlayer 被禁用
                ToolAttackUtil.attackEntity(ToolStack.from(this.tntarrow), (LivingEntity) this.getOwner(), InteractionHand.MAIN_HAND, entity, NO_COOLDOWN,
                                            false, Util.getSlotType(InteractionHand.OFF_HAND));
            }
            catch (Exception ignored) {
            }

        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide){
            float sound = 2.0F;
            Vec3 hitPos = result.getLocation();
            // 查找半径内的实体
            int hitRadius = TNTarrowRadius.get();
            List<Entity> nearbyEntities =
                    this.level().getEntities(null, new AABB(hitPos.subtract(hitRadius, hitRadius, hitRadius), hitPos.add(hitRadius, hitRadius, hitRadius)));

            // 遍历实体列表
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity livingEntity){
                    hitEntity(livingEntity);
                    sound++;
                }
            }
            if (this.getOwner() != null && this.getOwner().position().distanceTo(hitPos) <= hitRadius){
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
        if (!this.isNoGravity()){
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, TNTarrowgravity.get(), 0.0));
        }
    }
}

