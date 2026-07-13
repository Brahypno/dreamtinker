package org.brahypno.dreamtinker.Entity;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SlashOrbitEntity extends Entity {
    private static final float TWO_PI = (float) (Math.PI * 2.0);

    @Nullable
    private LivingEntity owner;
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> THICKNESS = SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> LIFE_TICKS = SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.INT);

    public enum GradMode {SOLID, ANGULAR, RADIAL, LENGTH, TIME_RAINBOW}

    private static final EntityDataAccessor<Float> OMEGA = SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> BLADES = SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> BLADE_HALF_ANGLE = SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> MOVE_MODE = SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COL_A = SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COL_B = SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MODE = SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> USE_HSV = SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> HUE_SHIFT_SPD = SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.FLOAT);
    private final Int2IntOpenHashMap lastHitAge = new Int2IntOpenHashMap();
    public float damage = 0f;      // =0 只渲染；>0 服务端造成伤害
    public int hitCooldown = 4;    // 同一目标命中间隔 tick
    public boolean discardOnBlockHit = false;

    public SlashOrbitEntity(ServerLevel level, LivingEntity owner, float radius, int life, float omega, int blades, float damage) {
        this(level, owner, radius, life, omega, blades, damage, 0.55f, 10f);
    }

    public SlashOrbitEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = false;
    }

    public SlashOrbitEntity(ServerLevel level, LivingEntity owner, float radius, int life, float omega, int blades, float damage, float thickness) {
        this(level, owner, radius, life, omega, blades, damage, thickness, 10f);
    }

    public SlashOrbitEntity(ServerLevel level, LivingEntity owner, float radius, int life, float omega, int blades, float damage, float thickness, float bladeHalfAngle) {
        this(DreamtinkerEntityTypes.SLASH_ORBIT.get(), level);
        this.owner = owner;
        this.damage = damage;
        setRadius(radius);
        setLife(life);
        setOmega(omega);
        setBlades(blades);
        setThickness(thickness);
        setBladeHalfAngle(bladeHalfAngle);
        setMoveMode(MoveMode.FOLLOW_OWNER);
        setPos(owner.getX(), owner.getY() + owner.getBbHeight() * 0.5D, owner.getZ());
    }

    public static SlashOrbitEntity projectile(ServerLevel level, LivingEntity owner, Vec3 pos, Vec3 motion, float radius, int life, float omega, int blades, float damage, float thickness) {
        return projectile(level, owner, pos, motion, radius, life, omega, blades, damage, thickness, 10f, false);
    }

    public static SlashOrbitEntity projectile(ServerLevel level, LivingEntity owner, Vec3 pos, Vec3 motion, float radius, int life, float omega, int blades, float damage, float thickness, float bladeHalfAngle, boolean discardOnBlockHit) {
        SlashOrbitEntity e = new SlashOrbitEntity(DreamtinkerEntityTypes.SLASH_ORBIT.get(), level);
        e.owner = owner;
        e.damage = damage;
        e.discardOnBlockHit = discardOnBlockHit;
        e.setRadius(radius);
        e.setLife(life);
        e.setOmega(omega);
        e.setBlades(blades);
        e.setThickness(thickness);
        e.setBladeHalfAngle(bladeHalfAngle);
        e.setMoveMode(MoveMode.PROJECTILE);
        e.setPos(pos.x, pos.y, pos.z);
        e.setDeltaMovement(motion);
        e.hasImpulse = true;
        return e;
    }

    private static float wrapToPi(float a) {
        a %= TWO_PI;
        if (a <= -Math.PI)
            a += TWO_PI;
        if (a > Math.PI)
            a -= TWO_PI;
        return a;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        double horizontal = this.radius() + this.thickness() + 0.5D;
        double vertical = 0.75D + horizontal * 0.12D;
        return new AABB(
                this.getX() - horizontal,
                this.getY() - vertical,
                this.getZ() - horizontal,
                this.getX() + horizontal,
                this.getY() + vertical,
                this.getZ() + horizontal
        );
    }

    @Override
    public void tick() {
        if (!level().isClientSide && tickCount >= life()){
            discard();
            return;
        }

        super.tick();

        if (!level().isClientSide){
            if (moveMode() == MoveMode.FOLLOW_OWNER)
                tickFollowOwner();
            else
                tickProjectile();

            if (damage > 0f)
                doHits();
        }
    }

    private void tickFollowOwner() {
        if (owner == null || !owner.isAlive() || owner.isRemoved()){
            discard();
            return;
        }
        setDeltaMovement(Vec3.ZERO);
        setPos(owner.getX(), owner.getY() + owner.getBbHeight() * 0.5D, owner.getZ());
        hasImpulse = true;
    }

    private void tickProjectile() {
        Vec3 motion = getDeltaMovement();
        if (motion.lengthSqr() <= 1.0E-7D)
            return;

        Vec3 from = position();
        Vec3 to = from.add(motion);

        if (discardOnBlockHit){
            BlockHitResult hit = level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hit.getType() != HitResult.Type.MISS){
                Vec3 p = hit.getLocation();
                setPos(p.x, p.y, p.z);
                discard();
                return;
            }
        }

        setPos(to.x, to.y, to.z);
        hasImpulse = true;
    }

    private void doHits() {
        float radius = this.radius();
        float halfWidth = Math.max(0.05F, this.thickness() * 0.5F);
        float minimum = Math.max(0.0F, radius - halfWidth);
        float maximum = radius + halfWidth;
        double minimumSqr = minimum * minimum;
        double maximumSqr = maximum * maximum;
        double y0 = this.getY() - 0.75D;
        double y1 = this.getY() + 0.75D;
        AABB searchBox = new AABB(
                this.getX() - maximum - 0.5D, y0, this.getZ() - maximum - 0.5D,
                this.getX() + maximum + 0.5D, y1, this.getZ() + maximum + 0.5D
        );

        int bladeCount = this.blades();
        float phase = wrapToPi(this.omega() * this.tickCount);
        float step = TWO_PI / bladeCount;
        float halfAngle = (float) Math.toRadians(this.bladeHalfAngle());

        for (LivingEntity target : this.level().getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity != this.owner && entity.isAlive() && !entity.isSpectator()
        )) {
            int id = target.getId();
            int lastHit = this.lastHitAge.getOrDefault(id, Integer.MIN_VALUE / 2);
            if (this.tickCount - lastHit < this.hitCooldown){
                continue;
            }

            AABB targetBox = target.getBoundingBox();
            double nearestX = Mth.clamp(this.getX(), targetBox.minX, targetBox.maxX);
            double nearestZ = Mth.clamp(this.getZ(), targetBox.minZ, targetBox.maxZ);
            double dx = nearestX - this.getX();
            double dz = nearestZ - this.getZ();
            double distanceSqr = dx * dx + dz * dz;
            if (distanceSqr < minimumSqr || distanceSqr > maximumSqr){
                continue;
            }

            float angle = (float) Math.atan2(dz, dx);
            float local = wrapToPi(angle - phase);
            float modulo = (float) Math.IEEEremainder(local, step);
            if (Math.abs(modulo) > halfAngle){
                continue;
            }

            if (target.hurt(this.level().damageSources().indirectMagic(this, this.owner), this.damage)){
                this.lastHitAge.put(id, this.tickCount);
            }
        }
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(COL_A, 0xFFFFFFFF);
        entityData.define(COL_B, 0xFFFFFFFF);
        entityData.define(MODE, GradMode.SOLID.ordinal());
        entityData.define(USE_HSV, true);
        entityData.define(HUE_SHIFT_SPD, 0.0f);
        entityData.define(RADIUS, 2.60f);
        entityData.define(THICKNESS, 0.55f);
        entityData.define(LIFE_TICKS, 20);
        entityData.define(OMEGA, 0.35f);
        entityData.define(BLADES, 6);
        entityData.define(BLADE_HALF_ANGLE, 10f);
        entityData.define(MOVE_MODE, MoveMode.FOLLOW_OWNER.ordinal());
    }

    public SlashOrbitEntity setSolidColor(int argb) {
        entityData.set(COL_A, argb);
        entityData.set(COL_B, argb);
        entityData.set(MODE, GradMode.SOLID.ordinal());
        return this;
    }

    public SlashOrbitEntity setGradient(int colA, int colB, GradMode mode, boolean hsv) {
        entityData.set(COL_A, colA);
        entityData.set(COL_B, colB);
        entityData.set(MODE, mode.ordinal());
        entityData.set(USE_HSV, hsv);
        return this;
    }

    public SlashOrbitEntity setHueShift(float speed) {
        entityData.set(HUE_SHIFT_SPD, speed);
        return this;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    public int colorA() {return entityData.get(COL_A);}

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag nbt) {}

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag nbt) {}

    public int colorB() {return entityData.get(COL_B);}

    public GradMode gradMode() {return GradMode.values()[Mth.clamp(entityData.get(MODE), 0, GradMode.values().length - 1)];}

    public boolean useHSV() {return entityData.get(USE_HSV);}

    public float hueShiftSpd() {return entityData.get(HUE_SHIFT_SPD);}

    public float radius() {return entityData.get(RADIUS);}

    public float thickness() {return entityData.get(THICKNESS);}

    public int life() {return Math.max(1, entityData.get(LIFE_TICKS));}

    public float omega() {return entityData.get(OMEGA);}

    public int blades() {return Math.max(1, entityData.get(BLADES));}

    public float bladeHalfAngle() {return Mth.clamp(entityData.get(BLADE_HALF_ANGLE), 0.1f, 179f);}

    public MoveMode moveMode() {return MoveMode.values()[Mth.clamp(entityData.get(MOVE_MODE), 0, MoveMode.values().length - 1)];}

    public void setRadius(float v) {entityData.set(RADIUS, Math.max(0.05f, v));}

    public void setThickness(float v) {entityData.set(THICKNESS, Math.max(0.02f, v));}

    public void setLife(int t) {entityData.set(LIFE_TICKS, Math.max(1, t));}

    public void setOmega(float v) {entityData.set(OMEGA, v);}

    public void setBlades(int v) {entityData.set(BLADES, Math.max(1, v));}

    public void setBladeHalfAngle(float v) {entityData.set(BLADE_HALF_ANGLE, Mth.clamp(v, 0.1f, 179f));}

    public void setMoveMode(MoveMode mode) {entityData.set(MOVE_MODE, mode.ordinal());}

    @Nullable
    public LivingEntity getOwnerLiving() {
        return owner;
    }

    public enum MoveMode {FOLLOW_OWNER, PROJECTILE}
}
