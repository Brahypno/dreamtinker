package org.dreamtinker.dreamtinker.tools.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DirectionalResistanceExplosionDamageCalculator;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.List;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.*;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.ContinuousExplodeTimes;

public class TNTArrow extends ModifiableItem {
    public static final ResourceLocation TAG_CONTINUOUS = Dreamtinker.getLocation("continuous_explode");
    public static final ResourceLocation TAG_EXPLODE_ALREADY = Dreamtinker.getLocation("already_explode");

    public void appendHoverText(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        int timeAllows = ModifierUtil.getModifierLevel(stack, DreamtinkerModifiers.Ids.continuous_explode) * ContinuousExplodeTimes.get();
        int currentTime = ModifierUtil.getPersistentInt(stack, TAG_CONTINUOUS, 0);
        if (currentTime < timeAllows)
            tooltip.add(Component.translatable("modifier.dreamtinker.tooltip.continuous_explode").append(String.valueOf(timeAllows - currentTime))
                                 .withStyle(ChatFormatting.DARK_RED));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide){
            ToolStack ts = ToolStack.from(stack);
            if (ts.getPersistentData().contains(TAG_EXPLODE_ALREADY)){
                ts.getPersistentData().remove(TAG_EXPLODE_ALREADY);
                ts.updateStack(stack);
            }
        }
        InventoryTickModifierHook.heldInventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity target) {
        return false;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        return false;
    }

    public TNTArrow(Properties properties, ToolDefinition toolDefinition, int maxStackSize) {
        super(properties, toolDefinition, maxStackSize);
    }

    public @NotNull AbstractArrow createArrow(Level world, ItemStack stack, LivingEntity shooter) {
        return new TNTArrowEntity(world, shooter, stack);
    }

    public static class TNTArrowEntity extends AbstractArrow implements IEntityAdditionalSpawnData {


        public TNTArrowEntity(EntityType<? extends AbstractArrow> type, Level world) {
            super(type, world);
        }

        public ItemStack tntarrow = ItemStack.EMPTY;
        private static final EntityDataAccessor<ItemStack> DATA_TOOL =
                SynchedEntityData.defineId(TNTArrowEntity.class, EntityDataSerializers.ITEM_STACK);

        public TNTArrowEntity(Level world, LivingEntity shooter, ItemStack stack) {
            super(DreamtinkerModifiers.TNTARROW.get(), shooter, world);
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
        public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
            return NetworkHooks.getEntitySpawningPacket(this);
        }

        protected void hitEntity(Entity entity) {
            if (null == this.getOwner() || !(this.getOwner() instanceof LivingEntity) || null == entity)
                return;
            entity.setInvulnerable(false);
            entity.invulnerableTime = 0;
            ToolStack toolStack = ToolStack.from(this.tntarrow);
            if (entity.getUUID() != this.getOwner().getUUID()){
                ToolAttackUtil.performAttack(toolStack,
                                             ToolAttackContext.attacker((LivingEntity) this.getOwner()).target(entity).cooldown(1).toolAttributes(toolStack)
                                                              .build());
                //ToolAttackUtil.attackEntity(toolStack, (LivingEntity) this.getOwner(), InteractionHand.OFF_HAND, entity, NO_COOLDOWN, false);

            }else {
                try {
                    ServerLevel serverLevel = (ServerLevel) this.level();
                    FakePlayer fakeAttacker = FakePlayerFactory.getMinecraft(serverLevel);
                    ToolAttackUtil.performAttack(toolStack,
                                                 ToolAttackContext.attacker(fakeAttacker).target(entity).cooldown(1).toolAttributes(toolStack)
                                                                  .build());
                }
                catch (SecurityException e) {
                    // 捕获异常，说明 FakePlayer 被禁用
                    ToolAttackUtil.performAttack(toolStack,
                                                 ToolAttackContext.attacker((LivingEntity) this.getOwner()).target(entity).cooldown(1).toolAttributes(toolStack)
                                                                  .build());
                }
                catch (Exception ignored) {
                }

            }
        }

        @Override
        protected void onHit(@NotNull HitResult result) {
            Vec3 impactVel = this.getDeltaMovement();
            super.onHit(result);
            if (!this.level().isClientSide){
                Vec3 hitPos = result.getLocation();
                if (this.getOwner() instanceof LivingEntity owner){//Nothing happened if no owner or not alive
                    boolean explosion = 0 < ModifierUtil.getModifierLevel(tntarrow, DreamtinkerModifiers.Ids.force_to_explosion);
                    if (explosion){
                        ToolStack ts = ToolStack.from(tntarrow);
                        if (ts.getPersistentData().contains(TAG_EXPLODE_ALREADY))
                            return;
                        ToolAttackContext context = ToolAttackContext.attacker(owner).target(owner).cooldown(0).toolAttributes(ts).build();

                        float baseDamage = context.getBaseDamage();
                        float damage = baseDamage;
                        List<ModifierEntry> modifiers = ts.getModifierList();

                        for (ModifierEntry entry : modifiers) {
                            damage = ((MeleeDamageModifierHook) entry.getHook(ModifierHooks.MELEE_DAMAGE)).getMeleeDamage(ts, entry, context, baseDamage,
                                                                                                                          damage);
                        }
                        double explosionPower =
                                Math.min(Math.sqrt(damage) * 2, ForceExplosionPower.get());
                        ExplosionDamageCalculator calc =
                                new DirectionalResistanceExplosionDamageCalculator(hitPos, impactVel, 35, 0.65f, 2.2f);
                        this.level().explode(
                                this.getOwner(),
                                DreamtinkerDamageTypes.source(this.level().registryAccess(), DreamtinkerDamageTypes.force_to_explosion, this, this.getOwner()),
                                calc,
                                hitPos.x, hitPos.y, hitPos.z,
                                (float) explosionPower, false,
                                Level.ExplosionInteraction.TNT
                        );
                        ts.getPersistentData().putBoolean(TAG_EXPLODE_ALREADY, true);
                        ts.updateStack(tntarrow);
                    }else {
                        float sound = 2.0F;
                        // 查找半径内的实体
                        int hitRadius = TNT_ARROW_RADIUS.get();
                        List<Entity> nearbyEntities =
                                this.level()
                                    .getEntities(null, new AABB(hitPos.subtract(hitRadius, hitRadius, hitRadius), hitPos.add(hitRadius, hitRadius, hitRadius)));
                        // 遍历实体列表
                        for (Entity entity : nearbyEntities) {
                            if (entity instanceof LivingEntity livingEntity){
                                hitEntity(livingEntity);
                                sound++;
                            }
                        }
                        if (owner.position().distanceTo(hitPos) <= hitRadius){
                            hitEntity(owner);
                            sound++;
                        }
                        this.playSound(SoundEvents.GENERIC_EXPLODE, sound, (1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F) * 0.7F);
                    }
                    int timeAllows = ModifierUtil.getModifierLevel(tntarrow, DreamtinkerModifiers.Ids.continuous_explode) * ContinuousExplodeTimes.get();
                    int currentTime = ModifierUtil.getPersistentInt(tntarrow, TAG_CONTINUOUS, 0);
                    if (currentTime < timeAllows){
                        ToolStack ts = ToolStack.from(tntarrow);
                        ts.getPersistentData().putInt(TAG_CONTINUOUS, ++currentTime);
                        ts.updateStack(tntarrow);
                    }else
                        this.discard();
                }
            }
        }

        @Override
        public @NotNull ItemStack getPickupItem() {
            return tntarrow;
        }

        @Override
        public void tick() {
            super.tick();
            if (!this.isNoGravity() && ModifierUtil.getModifierLevel(tntarrow, DreamtinkerModifiers.Ids.force_to_explosion) <= 0){
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, TNT_ARROW_GRAVITY.get(), 0.0));
            }
        }
    }
}
