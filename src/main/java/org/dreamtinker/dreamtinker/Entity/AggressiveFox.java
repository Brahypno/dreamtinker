package org.dreamtinker.dreamtinker.Entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class AggressiveFox extends Fox implements NeutralMob {
    private ItemStack storedWeapon;
    @Nullable
    private UUID persistentAngerTarget;
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME;
    private static final UniformInt PERSISTENT_ANGER_TIME;
    private static final Predicate<Entity> AVOID_PLAYERS;

    public AggressiveFox(EntityType<? extends Fox> p_28451_, Level p_28452_) {
        super(p_28451_, p_28452_);
    }

    private long lastStoredWeaponCheckGameTime = 0L;

    private void reRollStoredWeapon() {
        this.storedWeapon = DTHelper.randomTinkerTool(TinkerTags.Items.MELEE_PRIMARY, false, this.level().random, DreamtinkerModifiers.Ids.aggressiveFoxUsage);
    }


    private void tryEquipStoredWeapon() {
        if (!this.level().isClientSide){
            if (this.storedWeapon.isEmpty())
                reRollStoredWeapon();
            this.setItemInHand(InteractionHand.MAIN_HAND, this.storedWeapon);
            this.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
            this.lastStoredWeaponCheckGameTime = this.level().getGameTime();
            // 之后如果别的逻辑又把它 spit 掉，我们就在 1 秒内通过上面的 check 捕捉掉落
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
            @NotNull ServerLevelAccessor level,
            @NotNull DifficultyInstance difficulty,
            @NotNull MobSpawnType reason,
            @Nullable SpawnGroupData spawnData,
            @Nullable CompoundTag dataTag) {

        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);

        tryEquipStoredWeapon();

        return data;
    }

    @Override
    protected void registerGoals() {
        // 先注册原版狐狸 AI
        super.registerGoals();

        this.goalSelector.getAvailableGoals().removeIf(wrapped -> wrapped.getGoal() instanceof AvoidEntityGoal<?>);
        this.goalSelector.getAvailableGoals().removeIf(wrapped -> wrapped.getGoal() instanceof PanicGoal);

        this.goalSelector.addGoal(2, new AggresiveFoxPanicGoal(2.2));
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class, 16.0F, 1.6, 1.4,
                                                           (p_289437_) -> AVOID_PLAYERS.test(p_289437_) && !this.trusts(p_289437_.getUUID()) &&
                                                                          !this.isDefending() && !isAngry()));

        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    protected void pickUpItem(ItemEntity p_28514_) {
        ItemStack itemstack = p_28514_.getItem();
        if (this.canHoldItem(itemstack)){
            int i = itemstack.getCount();
            if (i > 1){
                this.dropItemStack(itemstack.split(i - 1));
            }

            this.spitOutItem(this.getItemBySlot(EquipmentSlot.MAINHAND));
            this.onItemPickup(p_28514_);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.split(1));
            this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            this.take(p_28514_, itemstack.getCount());
            p_28514_.discard();
            this.ticksSinceEaten = 0;
        }

    }

    private void spitOutItem(ItemStack p_28602_) {
        if (!p_28602_.isEmpty() && p_28602_ != storedWeapon && !this.level().isClientSide){
            ItemEntity itementity =
                    new ItemEntity(this.level(), this.getX() + this.getLookAngle().x, this.getY() + (double) 1.0F, this.getZ() + this.getLookAngle().z,
                                   p_28602_);
            itementity.setPickUpDelay(40);
            itementity.setThrower(this.getUUID());
            this.playSound(SoundEvents.FOX_SPIT, 1.0F, 1.0F);
            this.level().addFreshEntity(itementity);
        }

    }

    private void dropItemStack(ItemStack p_28606_) {
        ItemEntity itementity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), p_28606_);
        this.level().addFreshEntity(itementity);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide){
            checkStoredWeaponDropped();
            if (this.isAlive() && this.isEffectiveAi()){
                ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
                if (!this.canEat(itemstack) && !isInternalWeapon(itemstack)){
                    tryEquipStoredWeapon();
                }
                if (this.isAngry() && !isInternalWeapon(this.getMainHandItem())){
                    spitOutItem(this.getMainHandItem());
                    tryEquipStoredWeapon();
                }
            }

            this.updatePersistentAnger((ServerLevel) this.level(), true);
        }
    }


    private void checkStoredWeaponDropped() {
        if (this.storedWeapon.isEmpty())
            return;

        long gameTime = this.level().getGameTime();
        if (gameTime - this.lastStoredWeaponCheckGameTime > 20){
            // 超过 1 秒钟，就认为这次“检查窗口”结束，不再认定是刚刚 spit 出去的那一件
            return;
        }

        // 在狐狸附近一小块区域里找和 storedWeapon 完全相同的 ItemEntity
        AABB box = this.getBoundingBox().inflate(1.5D);
        List<ItemEntity> candidates = this.level().getEntitiesOfClass(
                ItemEntity.class,
                box,
                e -> ItemStack.isSameItemSameTags(e.getItem(), this.storedWeapon)
                     && e.getAge() < 10 // 年龄很小，说明是刚刚掉出来的
        );

        if (!candidates.isEmpty()){
            // 找到了“掉在地上的 storedWeapon”
            for (ItemEntity e : candidates) {
                if (!e.isRemoved())
                    e.discard(); // 删掉掉落物
            }

            ItemStack main = this.getMainHandItem();
            if (!isInternalWeapon(main) && !this.isDeadOrDying()){
                reRollStoredWeapon();
            }
        }
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, (double) 0.3F).add(Attributes.MAX_HEALTH, (double) 20.0F)
                  .add(Attributes.FOLLOW_RANGE, (double) 32.0F).add(Attributes.ATTACK_DAMAGE, (double) 4.0F)
                  .add(Attributes.ATTACK_KNOCKBACK, 1.0f)
                  .add(Attributes.ARMOR, 2.0f).add(Attributes.ARMOR_TOUGHNESS, 2.0f);
    }

    @Override
    public void die(DamageSource p_30384_) {
        if (!this.level().isClientSide){
            checkStoredWeaponDropped();
            emptyMainHand();
        }
        super.die(p_30384_);
    }

    private void emptyMainHand() {
        ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
        if (isInternalWeapon(itemstack))
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

    }

    @Override
    protected void dropEquipment() {
        emptyMainHand();
        super.dropEquipment();
    }

    private boolean canEat(ItemStack p_28598_) {
        return p_28598_.getItem().isEdible() && this.getTarget() == null && this.onGround() && !this.isSleeping();
    }

    public static boolean checkAggressiveFoxSpawnRules(EntityType<AggressiveFox> p_218176_, LevelAccessor p_218177_, MobSpawnType p_218178_, BlockPos p_218179_, RandomSource p_218180_) {
        return p_218177_.getBlockState(p_218179_.below()).is(BlockTags.FOXES_SPAWNABLE_ON) && isBrightEnoughToSpawn(p_218177_, p_218179_);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (!this.storedWeapon.isEmpty()){
            tag.put("StoredWeapon", this.storedWeapon.save(new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("StoredWeapon", Tag.TAG_COMPOUND)){
            this.storedWeapon = ItemStack.of(tag.getCompound("StoredWeapon"));
        }else {
            this.storedWeapon = ItemStack.EMPTY;
        }
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return (Integer) this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int p_30404_) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, p_30404_);
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID p_30400_) {
        this.persistentAngerTarget = p_30400_;
    }

    private boolean isInternalWeapon(ItemStack stack) {
        if (this.level().isClientSide)
            return false;
        return stack.equals(storedWeapon) || ItemStack.isSameItemSameTags(stack, storedWeapon) ||
               0 < DTModifierCheck.getItemModifierNum(stack, DreamtinkerModifiers.Ids.aggressiveFoxUsage);
    }

    static {
        DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(AggressiveFox.class, EntityDataSerializers.INT);
        PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
        AVOID_PLAYERS = (p_28463_) -> !p_28463_.isDiscrete() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(p_28463_);
    }

    class AggresiveFoxPanicGoal extends PanicGoal {
        public AggresiveFoxPanicGoal(double p_28734_) {
            super(AggressiveFox.this, p_28734_);
        }

        public boolean shouldPanic() {
            return !AggressiveFox.this.isDefending() && (this.mob.isFreezing() || this.mob.isOnFire());
        }
    }

    @Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
    public static class WolfAIModifier {

        @SubscribeEvent
        public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
            if (!(event.getEntity() instanceof Wolf wolf))
                return;
            if (wolf.level().isClientSide)
                return;

            // 防止重复添加（读档 / 重新进入区块时事件会再触发）
            CompoundTag data = wolf.getPersistentData();
            String key = "dreamtinker_add_wolf_battle";
            if (data.getBoolean(key)){
                return;
            }
            data.putBoolean(key, true);

            wolf.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(wolf, AggressiveFox.class, 10, true, true, null));
        }
    }

    @Mod.EventBusSubscriber(modid = Dreamtinker.MODID)
    public static class FoxDropFilter {

        @SubscribeEvent
        public static void onLivingDrops(LivingDropsEvent event) {
            if (!(event.getEntity() instanceof AggressiveFox fox))
                return;
            if (event.getEntity().level().isClientSide)
                return;
            fox.checkStoredWeaponDropped();
            event.getDrops().removeIf(drop -> fox.isInternalWeapon(drop.getItem()));
        }
    }

}
