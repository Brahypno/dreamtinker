package org.dreamtinker.dreamtinker.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.dreamtinker.dreamtinker.register.DreamtinkerEntity;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import java.util.Comparator;
import java.util.List;

import static slimeknights.tconstruct.library.tools.helper.ToolAttackUtil.NO_COOLDOWN;

public class NarcissusFluidProjectile extends Projectile {
    private IToolStackView toolStackView;
    private static final EntityDataAccessor<FluidStack> FLUID;
    private static final EntityDataAccessor<Integer> CHASE_LIVING;
    private float power;
    private int life = 30 * 20;
    private int knock_back;


    public NarcissusFluidProjectile(EntityType<? extends NarcissusFluidProjectile> type, Level level) {
        super(type, level);
        this.power = 2.0F;
        this.knock_back = 1;
    }


    public NarcissusFluidProjectile(Level level) {
        this(DreamtinkerEntity.NarcissusSpitEntity.get(), level);
    }

    public NarcissusFluidProjectile(Level level, LivingEntity owner, FluidStack fluid, float power, IToolStackView tool) {
        this(level);
        this.setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        this.setOwner(owner);
        this.setFluid(fluid);
        this.setPower(power);
        this.setTool(tool);
    }

    private FluidEffectContext.Builder buildContext() {
        Level level = this.level();
        FluidEffectContext.Builder builder = FluidEffectContext.builder(level).projectile(this);
        Entity owner = this.getOwner();
        if (owner != null){
            builder.user(owner);
        }

        return builder;
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity p_36743_) {
        return (getChaseLiving() < 1 ||
                (p_36743_ instanceof LivingEntity entity && entity.isAlive() && !(p_36743_ instanceof ArmorStand))) &&
               (super.canHitEntity(p_36743_) || !p_36743_.canBeHitByProjectile());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isRemoved())
            return;

        // 旋转 & 粒子（粒子只在客户端）
        this.updateRotation();


        // --- 追踪逻辑（不变，写回 vel） ---
        Vec3 vel = this.getDeltaMovement();
        if (getChaseLiving() > 1 && !this.onGround()){
            List<LivingEntity> candidates = this.level().getEntitiesOfClass(
                    LivingEntity.class,
                    this.getBoundingBox().inflate(12),
                    t -> t.isAlive()
                         && t != this.getOwner()
                         && !(this.getOwner() != null && t.isAlliedTo(this.getOwner()))
                         && !(t instanceof ArmorStand)
            );
            if (!candidates.isEmpty()){
                LivingEntity nearest = candidates.stream()
                                                 .min(Comparator.comparingDouble(e -> e.distanceToSqr(this)))
                                                 .get();
                Vec3 aim = nearest.position().add(0, nearest.getBbHeight() * 0.5, 0).subtract(this.position());
                vel = this.getDeltaMovement().add(aim.normalize()).scale(0.75 * Math.max(1, getChaseLiving() - 1));
            }
        }

        // --- 连续实体碰撞：求最早命中时刻 t* ---
        Vec3 from = this.position();
        ResultTOI toi = sweepToFirstEntityHit(from, vel);
        boolean impacted = false;

        if (toi.hit != null && toi.hit.getType() != HitResult.Type.MISS){
            // 把位置推进到命中点（略微回退 epsilon，避免下一帧重叠)
            double t = Math.max(0.0, Math.min(1.0, toi.t));
            Vec3 hitPos = from.add(vel.scale(t));
            this.setPos(hitPos.x, hitPos.y, hitPos.z);

            if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, toi.hit)){
                this.onHit(toi.hit); // 只实体命中
            }
            impacted = true;
        }

        if (!this.isRemoved() && !impacted){
            // 未命中：推进到完整终点
            Vec3 to = from.add(vel);
            this.setPos(to.x, to.y, to.z);
            if (level().isClientSide){
                level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY(), getZ(), 0, 0, 0);
            }
        }

        if (!this.isRemoved()){
            // 阻尼 & 重力
            Vec3 v2 = this.getDeltaMovement();
            if (!impacted)
                v2 = vel; // onHit 可能修改了速度，命中过则尊重修改
            v2 = v2.scale(0.99F);
            if (!this.isNoGravity()){
                FluidStack fluid = this.getFluid();
                v2 = v2.add(0.0F, fluid.getFluid().getFluidType().isLighterThanAir() ? 0.06 : -0.06, 0.0F);
            }
            this.setDeltaMovement(v2);
            --life;
        }

        if (this.getY() > this.level().getMaxBuildHeight() + 64
            || this.getY() < this.level().getMinBuildHeight() - 64
            || this.life <= 0){
            this.discard();
        }
    }

    /**
     * @param hit 可能为 null
     * @param t   命中最早时刻 ∈[0,1]；未命中时无意义
     */ // --- 辅助结构 ---
    private record ResultTOI(EntityHitResult hit, double t) {
    }

    // 连续扫描 + 二分逼近，只做“实体碰撞”检测
    private ResultTOI sweepToFirstEntityHit(Vec3 from, Vec3 vel) {
        // 快速粗采样：4~6次通常够定位
        final int SAMPLES = 6;
        final double inflate = 1.0;          // 与你原来保持一致
        final double EPS = 1e-4;             // 二分收敛阈值
        final int MAX_BISECT = 12;           // 二分次数上限

        double tLo = 0.0;
        EntityHitResult hitAt = null;

        // 先做均匀采样找到第一次命中出现的采样点
        for (int i = 1; i <= SAMPLES; i++) {
            double t = (double) i / (double) SAMPLES;
            Vec3 midFrom = from.add(vel.scale(tLo));
            Vec3 midTo = from.add(vel.scale(t));
            Vec3 seg = midTo.subtract(midFrom);

            EntityHitResult hr = ProjectileUtil.getEntityHitResult(
                    this.level(), this, midFrom, midTo,
                    this.getBoundingBox().expandTowards(seg).inflate(inflate),
                    this::canHitEntity
            );

            if (hr != null && hr.getType() != HitResult.Type.MISS){
                // 命中出现在(tLo, t] 区间，进入二分
                double lo = tLo, hi = t;
                EntityHitResult best = hr;
                for (int k = 0; k < MAX_BISECT && hi - lo > EPS; k++) {
                    double midT = (lo + hi) * 0.5;
                    Vec3 a = from.add(vel.scale(lo));
                    Vec3 b = from.add(vel.scale(midT));
                    Vec3 d = b.subtract(a);

                    EntityHitResult test = ProjectileUtil.getEntityHitResult(
                            this.level(), this, a, b,
                            this.getBoundingBox().expandTowards(d).inflate(inflate),
                            this::canHitEntity
                    );

                    if (test != null && test.getType() != HitResult.Type.MISS){
                        best = test;
                        hi = midT;     // 命中在前半段，收缩到更早
                    }else {
                        lo = midT;     // 未命中，向后探
                    }
                }
                hitAt = best;
                return new ResultTOI(hitAt, hi);
            }
            tLo = t;
        }
        // 完全未命中
        return new ResultTOI(null, 1.0);
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        Entity target = result.getEntity();
        float f = (float) this.getDeltaMovement().length();
        int i = Mth.ceil(Mth.clamp((double) f * this.power, 0.0F, Integer.MAX_VALUE));

        if (this.isCritArrow()){
            long j = this.random.nextInt(i / 2 + 2);
            i = (int) Math.min(j + (long) i, 2147483647L);
        }

        Entity entity1 = this.getOwner();
        if (null != entity1){
            DamageSource damagesource = null != toolStackView && 1 < toolStackView.getModifierLevel(DreamtinkerModifers.Ids.icy_memory) ?
                                        this.damageSources().sonicBoom(entity1) :
                                        null != toolStackView && 0 < toolStackView.getModifierLevel(DreamtinkerModifers.Ids.icy_memory) ?
                                        this.damageSources().indirectMagic(this, entity1) :
                                        this.damageSources().mobProjectile(this, (LivingEntity) entity1);
            if (entity1 instanceof LivingEntity){
                ((LivingEntity) entity1).setLastHurtMob(target);
            }
            if (target instanceof LivingEntity livingentity && target.hurt(damagesource, (float) i)){
                if (this.knock_back > 0){
                    double d0 = Math.max(0.0F, (double) 1.0F - livingentity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                    Vec3 vec3 =
                            this.getDeltaMovement().multiply(1.0F, 0.0F, 1.0F).normalize()
                                .scale((double) this.knock_back * 0.6 * d0);
                    if (vec3.lengthSqr() > (double) 0.0F){
                        livingentity.push(vec3.x, 0.1, vec3.z);
                    }
                }

                if (!this.level().isClientSide && entity1 instanceof LivingEntity){
                    EnchantmentHelper.doPostHurtEffects(livingentity, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) entity1, livingentity);
                }
                if (!this.level().isClientSide)
                    if (this.isOnFire()){
                        target.setSecondsOnFire(5 * Math.max(1, this.level().random.nextInt(3)));
                    }

            }
        }

        FluidStack fluid = this.getFluid();
        Level level = this.level();
        if (!level.isClientSide && !fluid.isEmpty()){
            FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
            if (null != this.getOwner()){
                if (recipe.hasEntityEffects()){
                    FluidEffectContext.Entity context = this.buildContext().location(result.getLocation()).target(this.getOwner());
                    int consumed = recipe.applyToEntity(fluid, this.power, context, IFluidHandler.FluidAction.EXECUTE);
                    int hate = 1;
                    if (null != toolStackView)
                        hate = Math.max(1, toolStackView.getModifierLevel(DreamtinkerModifers.Ids.hate_memory) + 1);
                    fluid.shrink(consumed / hate);
                    if (fluid.isEmpty()){
                        this.discard();
                    }else {
                        this.setFluid(fluid);
                    }
                    target.invulnerableTime = 0;
                    ToolAttackUtil.attackEntity(toolStackView, (LivingEntity) this.getOwner(), InteractionHand.MAIN_HAND, target, NO_COOLDOWN, false,
                                                Util.getSlotType(InteractionHand.MAIN_HAND));


                }
            }


        }


    }

    private boolean isCritArrow() {return false;}

    @Override
    protected void onHit(HitResult p_37260_) {
        HitResult.Type hitresult$type = p_37260_.getType();
        if (hitresult$type == HitResult.Type.ENTITY){
            this.onHitEntity((EntityHitResult) p_37260_);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, p_37260_.getLocation(), GameEvent.Context.of(this, null));
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(FLUID, FluidStack.EMPTY);
        this.entityData.define(CHASE_LIVING, 0);
    }

    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        double x = packet.getXa();
        double y = packet.getYa();
        double z = packet.getZa();

        for (int i = 0; i < 7; ++i) {
            double offset = 0.4 + 0.1 * (double) i;
            this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() * .4 - 1, this.getZ(), x * offset, y, z * offset);
        }

        this.setDeltaMovement(x, y, z);
    }

    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putFloat("power", this.power);
        nbt.putInt("knock_back", this.knock_back);

        FluidStack fluid = this.getFluid();
        if (!fluid.isEmpty()){
            nbt.put("fluid", fluid.writeToNBT(new CompoundTag()));
        }

    }

    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.power = nbt.getFloat("power");
        this.knock_back = nbt.getInt("knock_back");

        this.setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid")));
    }

    public FluidStack getFluid() {
        return this.entityData.get(FLUID);
    }

    public int getChaseLiving() {
        return this.entityData.get(CHASE_LIVING);
    }

    public void setFluid(FluidStack fluid) {
        this.entityData.set(FLUID, fluid);
    }

    public void setPower(float power) {
        this.power = power;
    }

    protected @NotNull Component getTypeName() {
        return this.getFluid().getDisplayName();
    }

    public void setTool(@NotNull IToolStackView tool) {
        this.toolStackView = tool;
        this.entityData.set(CHASE_LIVING, tool.getModifierLevel(DreamtinkerModifers.Ids.soul_core));
    }

    public IToolStackView getTool() {
        return this.toolStackView;
    }

    static {
        FLUID = SynchedEntityData.defineId(NarcissusFluidProjectile.class, TinkerFluids.FLUID_DATA_SERIALIZER);
        CHASE_LIVING = SynchedEntityData.defineId(NarcissusFluidProjectile.class, EntityDataSerializers.INT);
    }
}
