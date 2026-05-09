package org.dreamtinker.dreamtinker.Entity;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;

public class WingSlashProjectile extends Projectile implements ProjectileWithPower {
    private static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(WingSlashProjectile.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> ALPHA =
            SynchedEntityData.defineId(WingSlashProjectile.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> LENGTH_SCALE =
            SynchedEntityData.defineId(WingSlashProjectile.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> WIDTH_SCALE =
            SynchedEntityData.defineId(WingSlashProjectile.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> SPIN_SPEED =
            SynchedEntityData.defineId(WingSlashProjectile.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> POWER =
            SynchedEntityData.defineId(WingSlashProjectile.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Integer> MAX_LIFE =
            SynchedEntityData.defineId(WingSlashProjectile.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> MAX_DISTANCE =
            SynchedEntityData.defineId(WingSlashProjectile.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Byte> PIERCE_LEVEL =
            SynchedEntityData.defineId(WingSlashProjectile.class, EntityDataSerializers.BYTE);
    private final IntOpenHashSet ignoredEntities = new IntOpenHashSet();
    private int life;
    private double traveledDistance;
    /**
     * 已经击中过的实体 ID。
     * 用于防止穿透时同一个实体被一发剑气重复命中。
     */
    @Nullable
    private IntOpenHashSet piercedEntities;

    /**
     * 还剩多少次实体穿透。
     * <p>
     * 例如：
     * pierceLevel = 0：命中第一个实体后消失。
     * pierceLevel = 1：可以命中 2 个实体。
     * pierceLevel = 2：可以命中 3 个实体。
     */

    public WingSlashProjectile(EntityType<? extends WingSlashProjectile> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    public WingSlashProjectile(Level level) {
        this(DreamtinkerEntityTypes.WING_SLASH.get(), level);
    }

    public WingSlashProjectile(
            EntityType<? extends WingSlashProjectile> type,
            Level level,
            LivingEntity owner
    ) {
        this(type, level);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY() - 0.1D, owner.getZ());
    }

    /**
     * 生成并发射剑气的便捷方法。
     */
    public static WingSlashProjectile shootFrom(Level level, LivingEntity owner, float power, int maxLife, byte pierceLevel, double speed
    ) {
        return shootFrom(
                level,
                owner,
                power,
                maxLife,
                pierceLevel,
                speed,
                0xEDE9DD,
                220,
                1.0F,
                1.0F,
                18.0F
        );
    }

    public static WingSlashProjectile shootFrom(Level level, LivingEntity owner, float power, int maxLife, byte pierceLevel, double speed, int rgb, int alpha, float lengthScale, float widthScale, float spinSpeed) {
        WingSlashProjectile projectile = new WingSlashProjectile(
                DreamtinkerEntityTypes.WING_SLASH.get(), level, owner);

        Vec3 look = owner.getLookAngle().normalize();
        Vec3 spawnPos = owner.getEyePosition().add(look.scale(0.65D));

        projectile.setPos(spawnPos.x, spawnPos.y - 0.1D, spawnPos.z);
        projectile.setOwner(owner);

        projectile.setPower(power);
        projectile.setMaxLife(maxLife);
        projectile.setPierceLevel(pierceLevel);

        projectile.setRenderColor(rgb, alpha);
        projectile.setRenderScale(lengthScale, widthScale);
        projectile.setSpinSpeed(spinSpeed);

        projectile.shoot(look.x, look.y, look.z, (float) speed, 0.0F);

        level.addFreshEntity(projectile);
        return projectile;
    }

    public static WingSlashProjectile shootFrom(Level level, LivingEntity owner, Vec3 direction, float power, int maxLife, byte pierceLevel, double speed, int rgb, int alpha, float lengthScale, float widthScale, float spinSpeed) {
        WingSlashProjectile projectile = new WingSlashProjectile(DreamtinkerEntityTypes.WING_SLASH.get(), level, owner);

        Vec3 look = direction.normalize();
        Vec3 spawnPos = owner.getEyePosition().add(look.scale(0.65D));

        projectile.setPos(spawnPos.x, spawnPos.y - 0.1D, spawnPos.z);
        projectile.setOwner(owner);

        projectile.setPower(power);
        projectile.setMaxLife(maxLife);
        projectile.setPierceLevel(pierceLevel);

        projectile.setRenderColor(rgb, alpha);
        projectile.setRenderScale(lengthScale, widthScale);
        projectile.setSpinSpeed(spinSpeed);

        projectile.shoot(look.x, look.y, look.z, (float) speed, 0.0F);

        level.addFreshEntity(projectile);
        return projectile;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(POWER, 4.0F);
        this.entityData.define(MAX_LIFE, 40);
        this.entityData.define(MAX_DISTANCE, 40.0F);
        this.entityData.define(PIERCE_LEVEL, (byte) 0);

        // 外观参数
        this.entityData.define(COLOR, 0xEDE9DD); // 玉白
        this.entityData.define(ALPHA, 220);      // 0~255
        this.entityData.define(LENGTH_SCALE, 1.0F);
        this.entityData.define(WIDTH_SCALE, 1.0F);
        this.entityData.define(SPIN_SPEED, 18.0F);
    }

    @Override
    public float getPower() {
        return this.entityData.get(POWER);
    }

    @Override
    public void setPower(float power) {
        this.entityData.set(POWER, Math.max(0.0F, power));
    }

    public int getMaxLife() {
        return this.entityData.get(MAX_LIFE);
    }

    public void setMaxLife(int maxLife) {
        this.entityData.set(MAX_LIFE, Math.max(1, maxLife));
    }

    public float getMaxDistance() {
        return this.entityData.get(MAX_DISTANCE);
    }

    public void setMaxDistance(float maxDistance) {
        this.entityData.set(MAX_DISTANCE, Math.max(0.5F, maxDistance));
    }

    public byte getPierceLevel() {
        return this.entityData.get(PIERCE_LEVEL);
    }

    public void setPierceLevel(byte pierceLevel) {
        byte value = (byte) Math.max(0, pierceLevel);
        this.entityData.set(PIERCE_LEVEL, value);
        if (value > 0 && this.piercedEntities == null){
            this.piercedEntities = new IntOpenHashSet();
        }
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double size = this.getBoundingBox().getSize() * 10.0D;
        if (Double.isNaN(size)){
            size = 1.0D;
        }
        size *= 64.0D;
        return distance < size * size;
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 motion = this.getDeltaMovement();

        if (motion.lengthSqr() < 1.0E-7D){
            if (!this.level().isClientSide){
                this.discard();
            }
            return;
        }

        // 客户端：只做视觉移动，不做命中循环
        if (this.level().isClientSide){
            Vec3 next = this.position().add(motion);
            this.setPos(next.x, next.y, next.z);
            this.updateRotation();
            this.spawnClientParticles();
            return;
        }

        // 服务端：寿命、距离、碰撞、伤害
        this.life++;
        if (this.life >= this.getMaxLife()){
            this.discard();
            return;
        }

        this.traveledDistance += motion.length();
        if (this.traveledDistance >= this.getMaxDistance()){
            this.discard();
            return;
        }

        Vec3 from = this.position();
        Vec3 to = from.add(motion);

        HitResult hitResult = this.level().clip(new ClipContext(
                from,
                to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
        ));

        if (hitResult.getType() != HitResult.Type.MISS){
            to = hitResult.getLocation();
        }

        this.processHitsAlongPath(from, to, hitResult);

        if (this.isRemoved()){
            return;
        }

        this.setPos(to.x, to.y, to.z);
        this.updateRotation();
        this.checkInsideBlocks();
    }

    protected void processHitsAlongPath(Vec3 from, Vec3 to, HitResult blockHitResult) {
        HitResult hitResult = blockHitResult;

        this.ignoredEntities.clear();

        while (!this.isRemoved()) {
            EntityHitResult entityHitResult = this.findHitEntity(from, to);

            if (entityHitResult != null){
                hitResult = entityHitResult;
            }

            if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY){
                Entity hitEntity = ((EntityHitResult) hitResult).getEntity();
                Entity owner = this.getOwner();

                if (hitEntity instanceof Player targetPlayer
                    && owner instanceof Player ownerPlayer
                    && !ownerPlayer.canHarmPlayer(targetPlayer)){
                    hitResult = null;
                    entityHitResult = null;
                }
            }

            if (hitResult != null && hitResult.getType() != HitResult.Type.MISS){
                ProjectileImpactEvent.ImpactResult impactResult =
                        ForgeEventFactory.onProjectileImpactResult(this, hitResult);

                switch (impactResult) {
                    case SKIP_ENTITY -> {
                        if (hitResult.getType() != HitResult.Type.ENTITY){
                            this.onHit(hitResult);
                            this.hasImpulse = true;
                            break;
                        }

                        Entity skipped = ((EntityHitResult) hitResult).getEntity();
                        this.ignoredEntities.add(skipped.getId());

                        hitResult = null;
                        entityHitResult = null;
                    }

                    case STOP_AT_CURRENT_NO_DAMAGE -> {
                        this.discard();
                        entityHitResult = null;
                    }

                    case STOP_AT_CURRENT -> {
                        this.setPierceLevel((byte) 0);
                        this.onHit(hitResult);
                        this.hasImpulse = true;
                    }

                    case DEFAULT -> {
                        this.onHit(hitResult);
                        this.hasImpulse = true;
                    }
                }
            }

            if (this.isRemoved()){
                break;
            }

            if (entityHitResult == null || this.getPierceLevel() <= 0){
                break;
            }

            hitResult = null;
        }
    }

    @javax.annotation.Nullable
    protected EntityHitResult findHitEntity(Vec3 p_36758_, Vec3 p_36759_) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, p_36758_, p_36759_,
                                                 this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }

    protected boolean canHitEntity(Entity p_36743_) {
        return super.canHitEntity(p_36743_) && (this.piercedEntities == null || !this.piercedEntities.contains(p_36743_.getId())) &&
               !this.ignoredEntities.contains(p_36743_.getId());
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        HitResult.Type type = result.getType();

        if (type == HitResult.Type.ENTITY){
            this.onHitEntity((EntityHitResult) result);
        }else if (type == HitResult.Type.BLOCK){
            this.onHitBlock((BlockHitResult) result);
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        // 不能穿墙：命中方块直接消失。
        if (!this.level().isClientSide){
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        // 理论上现在只会服务端进来；保留防御式判断
        if (this.level().isClientSide){
            return;
        }

        Entity target = result.getEntity();
        Entity owner = this.getOwner();

        if (target == owner){
            return;
        }

        // 先记录穿透实体，避免本 tick 后续 while 再找到同一个目标
        if (this.getPierceLevel() > 0){
            if (this.piercedEntities == null){
                this.piercedEntities = new IntOpenHashSet();
            }

            // 已经达到可命中上限，直接移除
            // pierceLevel = 0 -> 1 个目标
            // pierceLevel = 1 -> 2 个目标
            // pierceLevel = 2 -> 3 个目标
            if (this.piercedEntities.size() >= this.getPierceLevel() + 1){
                this.discard();
                return;
            }

            this.piercedEntities.add(target.getId());
        }

        float damage = this.getDamage();

        // 伤害 <= 0 时不要继续穿透循环找同一个目标
        if (damage <= 0.0F){
            if (this.getPierceLevel() <= 0){
                this.discard();
            }
            return;
        }

        DamageSource source = this.makeDamageSource(owner);

        if (owner instanceof LivingEntity livingOwner){
            livingOwner.setLastHurtMob(target);
        }

        boolean hurt = target.hurt(source, damage);

        if (hurt){
            this.doPostHurtEffects(target, owner);
        }

        if (this.getPierceLevel() <= 0){
            this.discard();
        }
    }

    protected DamageSource makeDamageSource(@Nullable Entity owner) {
        // 剑气偏“术式/剑意”，这里用 indirectMagic 比较稳。
        // 如果你之后做了自定义 DamageType，可以在这里替换。
        return this.damageSources().indirectMagic(this, owner);
    }

    protected void doPostHurtEffects(Entity target, @Nullable Entity owner) {
        // 命中反馈。可以删掉，或者替换成你的羽裂粒子包。
        if (this.level() instanceof ServerLevel serverLevel){
            serverLevel.playSound(
                    null,
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP,
                    SoundSource.PLAYERS,
                    0.6F,
                    1.35F
            );
        }

        // 这里可以扩展：
        // 1. 给目标小击退
        // 2. 生成羽片粒子
        // 3. 标记“羽裂”
    }

    protected void spawnClientParticles() {
        // 留空给客户端粒子。
        // 例如你之后可以在这里生成淡白/青白/玉色 dust 粒子。
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putFloat("Power", this.getPower());
        tag.putInt("Life", this.life);
        tag.putInt("MaxLife", this.getMaxLife());
        tag.putFloat("MaxDistance", this.getMaxDistance());
        tag.putDouble("TraveledDistance", this.traveledDistance);
        tag.putByte("PierceLevel", this.getPierceLevel());

        tag.putInt("Color", this.getColor());
        tag.putInt("Alpha", this.getAlpha());
        tag.putFloat("LengthScale", this.getLengthScale());
        tag.putFloat("WidthScale", this.getWidthScale());
        tag.putFloat("SpinSpeed", this.getSpinSpeed());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.setPower(tag.getFloat("Power"));
        this.life = tag.getInt("Life");

        if (tag.contains("MaxLife")){
            this.setMaxLife(tag.getInt("MaxLife"));
        }

        if (tag.contains("MaxDistance")){
            this.setMaxDistance(tag.getFloat("MaxDistance"));
        }

        this.traveledDistance = tag.getDouble("TraveledDistance");

        if (tag.contains("PierceLevel")){
            this.setPierceLevel(tag.getByte("PierceLevel"));
        }

        if (tag.contains("Color")){
            this.setColor(tag.getInt("Color"));
        }
        if (tag.contains("Alpha")){
            this.setAlpha(tag.getInt("Alpha"));
        }
        if (tag.contains("LengthScale")){
            this.setLengthScale(tag.getFloat("LengthScale"));
        }
        if (tag.contains("WidthScale")){
            this.setWidthScale(tag.getFloat("WidthScale"));
        }
        if (tag.contains("SpinSpeed")){
            this.setSpinSpeed(tag.getFloat("SpinSpeed"));
        }
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    public void setColor(int rgb) {
        this.entityData.set(COLOR, rgb & 0xFFFFFF);
    }

    public int getAlpha() {
        return this.entityData.get(ALPHA);
    }

    public void setAlpha(int alpha) {
        this.entityData.set(ALPHA, Mth.clamp(alpha, 0, 255));
    }

    public float getLengthScale() {
        return this.entityData.get(LENGTH_SCALE);
    }

    public void setLengthScale(float scale) {
        this.entityData.set(LENGTH_SCALE, Mth.clamp(scale, 0.1F, 8.0F));
    }

    public float getWidthScale() {
        return this.entityData.get(WIDTH_SCALE);
    }

    public void setWidthScale(float scale) {
        this.entityData.set(WIDTH_SCALE, Mth.clamp(scale, 0.1F, 8.0F));
    }

    public float getSpinSpeed() {
        return this.entityData.get(SPIN_SPEED);
    }

    public void setSpinSpeed(float spinSpeed) {
        this.entityData.set(SPIN_SPEED, spinSpeed);
    }

    public void setRenderColor(int rgb, int alpha) {
        this.setColor(rgb);
        this.setAlpha(alpha);
    }

    public void setRenderScale(float lengthScale, float widthScale) {
        this.setLengthScale(lengthScale);
        this.setWidthScale(widthScale);
    }

    public int getRenderARGB() {
        return (this.getAlpha() & 255) << 24 | (this.getColor() & 0xFFFFFF);
    }

    public void setRenderARGB(int argb) {
        int alpha = (argb >>> 24) & 255;
        int rgb = argb & 0xFFFFFF;

        this.setColor(rgb);
        this.setAlpha(alpha);
    }
}
