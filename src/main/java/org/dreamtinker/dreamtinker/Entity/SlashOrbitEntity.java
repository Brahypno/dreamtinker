package org.dreamtinker.dreamtinker.Entity;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SlashOrbitEntity extends Entity {

    @Nullable
    private LivingEntity owner;

    // 可调参数
    public int blades = 6;             // 刀片数量（围成一圈）
    public float bladeHalfAngle = 10f;   // 每片扇形的半角（度）
    public float damage = 0f;            // =0 只渲染；>0 则造成伤害
    public int hitCooldown = 4;        // 同一目标命中间隔（刻）
    private static final EntityDataAccessor<Float> RADIUS =
            SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> THICKNESS =
            SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> LIFE_TICKS =
            SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> OMEGA =
            SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.FLOAT);

    public enum GradMode {SOLID, ANGULAR, RADIAL, LENGTH, TIME_RAINBOW}

    private static final EntityDataAccessor<Integer> COL_A =
            SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> COL_B =
            SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MODE =
            SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> USE_HSV =
            SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> HUE_SHIFT_SPD =
            SynchedEntityData.defineId(SlashOrbitEntity.class, EntityDataSerializers.FLOAT);

    private final Int2IntOpenHashMap lastHitAge = new Int2IntOpenHashMap(); // entityId -> age

    public SlashOrbitEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    public SlashOrbitEntity(
            ServerLevel s, LivingEntity owner,
            float radius, int life, float omega,
            int blades, float damage) {
        this(s, owner, radius, life, omega, blades, damage, 0);
    }

    public SlashOrbitEntity(
            ServerLevel s, LivingEntity owner,
            float radius, int life, float omega,
            int blades, float damage, float thickness) {
        this(DreamtinkerModifiers.SLASH_ORBIT.get(), s);
        this.owner = owner;
        setRadius(radius);
        setLife(life);
        setOmega(omega);
        this.blades = blades;
        this.damage = damage;
        setThickness(thickness);
        this.setPos(owner.getX(), owner.getY() + owner.getBbHeight() * 0.5, owner.getZ());
    }

    @Override
    public void tick() {
        if (!level().isClientSide && this.tickCount >= life()){
            discard();
            return; // 别再往下跑 super.tick() 之类的
        }
        super.tick();
        if (!level().isClientSide && owner != null && owner.isAlive()){
            setPos(owner.getX(), owner.getY() + owner.getBbHeight() * 0.5, owner.getZ());
        }
        if (!level().isClientSide && damage > 0f){
            doHits();
        }
    }

    private void doHits() {
        double y0 = getY() - 0.6, y1 = getY() + 0.6;
        AABB box = new AABB(getX() - radius() - 0.8, y0, getZ() - radius() - 0.8,
                            getX() + radius() + 0.8, y1, getZ() + radius() + 0.8);
        float phase = omega() * tickCount;                 // 当前旋转角
        float step = (float) (2 * Math.PI / blades); // 每片中心间隔
        float half = (float) Math.toRadians(bladeHalfAngle);

        for (LivingEntity t : level().getEntitiesOfClass(LivingEntity.class, box,
                                                         e -> e != owner && e.isAlive() && !e.isSpectator())) {
            double dx = t.getX() - getX(), dz = t.getZ() - getZ();
            double d = Math.sqrt(dx * dx + dz * dz);
            if (d < radius() - 0.5 || d > radius() + 0.5)
                continue;    // 只打环带内

            float ang = (float) Math.atan2(dz, dx);             // 目标极角
            // 将角度对齐到某一“刀片扇形”中心
            float local = wrapToPi(ang - phase);               // 与相位的差
            float mod = (float) Math.IEEEremainder(local, step);
            float distToBladeCenter = Math.abs(mod);
            if (distToBladeCenter <= half){
                int id = t.getId();
                int last = lastHitAge.getOrDefault(id, -999);
                if (tickCount - last >= hitCooldown && 0 < damage){
                    t.hurt(level().damageSources().indirectMagic(this, owner), damage);
                    lastHitAge.put(id, tickCount);
                }
            }
        }
    }

    private static float wrapToPi(float a) {
        while (a < -Math.PI)
            a += (float) (2 * Math.PI);
        while (a > Math.PI)
            a -= (float) (2 * Math.PI);
        return a;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(COL_A, 0xFFFFFFFF);   // ARGB：默认白
        this.entityData.define(COL_B, 0xFFFFFFFF);
        this.entityData.define(MODE, GradMode.SOLID.ordinal());
        this.entityData.define(USE_HSV, true);
        this.entityData.define(HUE_SHIFT_SPD, 0.0f); // >0 则随时间彩虹滚动
        this.entityData.define(RADIUS, 2.60f);
        this.entityData.define(THICKNESS, 0.35f);
        this.entityData.define(LIFE_TICKS, 20);     // 1 秒 = 20 tick
        this.entityData.define(OMEGA, .35f);
    }

    public SlashOrbitEntity setSolidColor(int argb) {
        this.entityData.set(COL_A, argb);
        this.entityData.set(COL_B, argb);
        this.entityData.set(MODE, GradMode.SOLID.ordinal());
        return this;
    }

    public SlashOrbitEntity setGradient(int colA, int colB, GradMode m, boolean hsv) {
        this.entityData.set(COL_A, colA);
        this.entityData.set(COL_B, colB);
        this.entityData.set(MODE, m.ordinal());
        this.entityData.set(USE_HSV, hsv);
        return this;
    }

    public void setHueShift(float speed) { // 例如 0.15f
        this.entityData.set(HUE_SHIFT_SPD, speed);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag nbt) {}

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag nbt) {}

    // 读取（渲染器会用）
    public int colorA() {return this.entityData.get(COL_A);}

    public int colorB() {return this.entityData.get(COL_B);}

    public GradMode gradMode() {return GradMode.values()[Mth.clamp(this.entityData.get(MODE), 0, GradMode.values().length - 1)];}

    public boolean useHSV() {return this.entityData.get(USE_HSV);}

    public float hueShiftSpd() {return this.entityData.get(HUE_SHIFT_SPD);}

    public float radius() {return this.entityData.get(RADIUS);}

    public float thickness() {return this.entityData.get(THICKNESS);}

    public int life() {return this.entityData.get(LIFE_TICKS);}

    public float omega() {return this.entityData.get(OMEGA);}

    public void setRadius(float v) {
        this.entityData.set(RADIUS, v);
    }

    public void setThickness(float v) {
        this.entityData.set(THICKNESS, v);
    }

    public void setLife(int t) {
        this.entityData.set(LIFE_TICKS, t);
    }

    public void setOmega(float v) {
        this.entityData.set(OMEGA, v);
    }

}
