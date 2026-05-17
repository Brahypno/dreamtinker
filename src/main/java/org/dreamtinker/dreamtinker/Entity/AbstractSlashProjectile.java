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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public abstract class AbstractSlashProjectile extends Projectile implements ProjectileWithPower {
    private static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(AbstractSlashProjectile.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> ALPHA =
            SynchedEntityData.defineId(AbstractSlashProjectile.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> LENGTH_SCALE =
            SynchedEntityData.defineId(AbstractSlashProjectile.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> WIDTH_SCALE =
            SynchedEntityData.defineId(AbstractSlashProjectile.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> SPIN_SPEED =
            SynchedEntityData.defineId(AbstractSlashProjectile.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> POWER =
            SynchedEntityData.defineId(AbstractSlashProjectile.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Integer> MAX_LIFE =
            SynchedEntityData.defineId(AbstractSlashProjectile.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> MAX_DISTANCE =
            SynchedEntityData.defineId(AbstractSlashProjectile.class, EntityDataSerializers.FLOAT);

    /**
     * pierceLevel = 0：命中 1 个实体后消失。
     * pierceLevel = 1：最多命中 2 个实体。
     * pierceLevel = 2：最多命中 3 个实体。
     * pierceLevel < 0：无限穿透，但同一发剑气不会重复命中同一实体。
     */
    private static final EntityDataAccessor<Byte> PIERCE_LEVEL =
            SynchedEntityData.defineId(AbstractSlashProjectile.class, EntityDataSerializers.BYTE);

    private final IntOpenHashSet ignoredEntities = new IntOpenHashSet();

    @Nullable
    private IntOpenHashSet piercedEntities;

    private int life;
    private double traveledDistance;

    protected AbstractSlashProjectile(EntityType<? extends AbstractSlashProjectile> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    protected AbstractSlashProjectile(EntityType<? extends AbstractSlashProjectile> type, Level level, LivingEntity owner) {
        this(type, level);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY() - 0.1D, owner.getZ());
    }

    protected static <T extends AbstractSlashProjectile> T shootProjectile(
            T projectile,
            Level level,
            LivingEntity owner,
            Vec3 direction,
            float power,
            int maxLife,
            float maxDistance,
            byte pierceLevel,
            double speed,
            int rgb,
            int alpha,
            float lengthScale,
            float widthScale,
            float spinSpeed
    ) {
        Vec3 look = direction.normalize();
        Vec3 spawnPos = owner.getEyePosition().add(look.scale(0.65D));

        projectile.setPos(spawnPos.x, spawnPos.y - 0.1D, spawnPos.z);
        projectile.setOwner(owner);

        projectile.setPower(power);
        projectile.setMaxLife(maxLife);
        projectile.setMaxDistance(maxDistance);
        projectile.setPierceLevel(pierceLevel);

        projectile.setRenderColor(rgb, alpha);
        projectile.setRenderScale(lengthScale, widthScale);
        projectile.setSpinSpeed(spinSpeed);

        projectile.shoot(look.x, look.y, look.z, (float) speed, 0.0F);

        level.addFreshEntity(projectile);
        return projectile;
    }

    protected static <T extends AbstractSlashProjectile> T shootProjectile(
            T projectile,
            Level level,
            LivingEntity owner,
            Vec3 direction,
            float power,
            int maxLife,
            byte pierceLevel,
            double speed,
            int rgb,
            int alpha,
            float lengthScale,
            float widthScale,
            float spinSpeed
    ) {
        return shootProjectile(
                projectile,
                level,
                owner,
                direction,
                power,
                maxLife,
                40.0F,
                pierceLevel,
                speed,
                rgb,
                alpha,
                lengthScale,
                widthScale,
                spinSpeed
        );
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(POWER, 4.0F);
        this.entityData.define(MAX_LIFE, 40);
        this.entityData.define(MAX_DISTANCE, 40.0F);
        this.entityData.define(PIERCE_LEVEL, (byte) 0);

        this.entityData.define(COLOR, 0xEDE9DD);
        this.entityData.define(ALPHA, 220);
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

    public float getDamage() {
        return this.getPower();
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
        this.entityData.set(PIERCE_LEVEL, pierceLevel);

        if (pierceLevel != 0 && this.piercedEntities == null){
            this.piercedEntities = new IntOpenHashSet();
        }
    }

    protected boolean canPierceEntities() {
        return this.getPierceLevel() != 0;
    }

    protected boolean hasInfiniteEntityPiercing() {
        return this.getPierceLevel() < 0;
    }

    protected int getMaxEntityHits() {
        return this.hasInfiniteEntityPiercing() ? Integer.MAX_VALUE : this.getPierceLevel() + 1;
    }

    protected boolean canHitMoreEntities() {
        return this.hasInfiniteEntityPiercing()
               || this.piercedEntities == null
               || this.piercedEntities.size() < this.getMaxEntityHits();
    }

    /**
     * 普通细剑气不用覆写。
     * 宽月牙剑气覆写这个值，例如 1.5D ~ 2.5D。
     */
    protected double getEntityHitHalfWidth() {
        return 0.0D;
    }

    /**
     * 普通细剑气不用覆写。
     * 宽月牙剑气可以覆写成 0.6D ~ 1.2D。
     */
    protected double getEntityHitHalfHeight() {
        return 0.0D;
    }

    /**
     * 扫描路径周围实体时的额外保险范围。
     * 一般不用覆写。
     */
    protected double getEntitySearchPadding() {
        return 1.0D;
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

        if (this.level().isClientSide){
            Vec3 next = this.position().add(motion);

            this.setPos(next.x, next.y, next.z);
            this.updateRotation();
            this.spawnClientParticles();
            return;
        }

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

        HitResult blockHitResult = this.level().clip(new ClipContext(
                from,
                to,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                this
        ));

        if (blockHitResult.getType() != HitResult.Type.MISS){
            to = blockHitResult.getLocation();
        }

        this.processHitsAlongPath(from, to, blockHitResult);

        if (this.isRemoved()){
            return;
        }

        this.setPos(to.x, to.y, to.z);
        this.updateRotation();
        this.checkInsideBlocks();
    }

    protected void processHitsAlongPath(Vec3 from, Vec3 to, HitResult blockHitResult) {
        this.ignoredEntities.clear();

        for (EntityHitResult entityHitResult : this.findHitEntities(from, to)) {
            if (this.isRemoved()){
                return;
            }

            Entity hitEntity = entityHitResult.getEntity();

            if (this.isPlayerHitDenied(hitEntity)){
                continue;
            }

            this.handleImpact(entityHitResult);

            if (this.isRemoved() || !this.canPierceEntities()){
                return;
            }
        }

        if (!this.isRemoved() && blockHitResult != null && blockHitResult.getType() != HitResult.Type.MISS){
            this.handleImpact(blockHitResult);
        }
    }

    protected boolean isPlayerHitDenied(Entity target) {
        Entity owner = this.getOwner();

        return target instanceof Player targetPlayer
               && owner instanceof Player ownerPlayer
               && !ownerPlayer.canHarmPlayer(targetPlayer);
    }

    protected void handleImpact(HitResult hitResult) {
        ProjectileImpactEvent.ImpactResult impactResult =
                ForgeEventFactory.onProjectileImpactResult(this, hitResult);

        switch (impactResult) {
            case SKIP_ENTITY -> {
                if (hitResult.getType() == HitResult.Type.ENTITY){
                    Entity skipped = ((EntityHitResult) hitResult).getEntity();
                    this.ignoredEntities.add(skipped.getId());
                }else {
                    this.onHit(hitResult);
                    this.hasImpulse = true;
                }
            }

            case STOP_AT_CURRENT_NO_DAMAGE -> this.discard();

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

    protected List<EntityHitResult> findHitEntities(Vec3 from, Vec3 to) {
        Vec3 movement = to.subtract(from);

        if (movement.lengthSqr() < 1.0E-7D){
            return List.of();
        }

        double halfWidth = Math.max(0.0D, this.getEntityHitHalfWidth());
        double halfHeight = Math.max(0.0D, this.getEntityHitHalfHeight());
        double padding = this.getEntitySearchPadding() + Math.max(halfWidth, halfHeight);

        AABB searchBox = new AABB(from, to).inflate(padding);
        List<EntityHitResult> hits = new ArrayList<>();

        for (Entity entity : this.level().getEntities(this, searchBox, this::canHitEntity)) {
            double pickRadius = entity.getPickRadius();
            AABB hitBox = entity.getBoundingBox().inflate(
                    halfWidth + pickRadius,
                    halfHeight + pickRadius,
                    halfWidth + pickRadius
            );

            Optional<Vec3> location = hitBox.clip(from, to);

            if (location.isPresent()){
                hits.add(new EntityHitResult(entity, location.get()));
            }else if (hitBox.contains(from)){
                hits.add(new EntityHitResult(entity, from));
            }
        }

        hits.sort(Comparator.comparingDouble(hit -> hit.getLocation().distanceToSqr(from)));
        return hits;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        Entity owner = this.getOwner();

        return entity != owner
               && this.canSlashHitEntity(entity)
               && !this.ignoredEntities.contains(entity.getId())
               && (this.piercedEntities == null || !this.piercedEntities.contains(entity.getId()));
    }

    protected boolean canSlashHitEntity(Entity target) {
        return super.canHitEntity(target) || (!target.isSpectator() && !target.canBeHitByProjectile() && target instanceof LivingEntity && target.isAlive());
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

        if (!this.level().isClientSide){
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        if (this.level().isClientSide){
            return;
        }

        Entity target = result.getEntity();
        Entity owner = this.getOwner();

        if (target == owner){
            return;
        }

        if (this.canPierceEntities()){
            if (this.piercedEntities == null){
                this.piercedEntities = new IntOpenHashSet();
            }

            if (!this.canHitMoreEntities()){
                this.discard();
                return;
            }

            this.piercedEntities.add(target.getId());
        }

        float damage = this.getDamage();

        if (damage <= 0.0F){
            if (!this.canPierceEntities()){
                this.discard();
            }

            return;
        }


        if (owner instanceof LivingEntity livingOwner){
            livingOwner.setLastHurtMob(target);
        }

        boolean hurt = doHurt(target, damage);

        if (hurt){
            this.doPostHurtEffects(target, owner);
        }

        if (!this.canPierceEntities() || !this.canHitMoreEntities()){
            this.discard();
        }
    }

    protected boolean doHurt(Entity target, float amount) {
        return target.hurt(this.damageSources().indirectMagic(this, this.getOwner()), amount);
    }

    protected void doPostHurtEffects(Entity target, @Nullable Entity owner) {
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
    }

    protected void spawnClientParticles() {}

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
