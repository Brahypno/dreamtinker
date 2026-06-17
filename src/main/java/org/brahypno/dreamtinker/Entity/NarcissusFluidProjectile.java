package org.brahypno.dreamtinker.Entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.brahypno.dreamtinker.common.DreamtinkerDamageTypes;
import org.brahypno.dreamtinker.library.client.trail.DTClientTrail;
import org.brahypno.dreamtinker.library.modifiers.modules.combat.NarcissusFluidFeedbacks;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.dreamtinker.tools.modifiers.tools.narcissus_wing.MemoryBase;
import org.brahypno.dreamtinker.utils.DTHelper;
import org.brahypno.dreamtinker.utils.DamageProbe;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Comparator;
import java.util.List;

public class NarcissusFluidProjectile extends Projectile implements ProjectileWithPower {
    private IToolStackView toolStackView;
    private static final EntityDataAccessor<FluidStack> FLUID;
    private static final EntityDataAccessor<Integer> CHASE_LIVING;
    private static final EntityDataAccessor<Integer> COLOR;
    private int initialFluid;
    private float power;
    private int life = 30 * 20;
    private boolean crit;
    public final DTClientTrail shortTrail = new DTClientTrail(8, 0.0004D);
    public final DTClientTrail trail = new DTClientTrail(24, 0.0004D);


    public NarcissusFluidProjectile(EntityType<? extends NarcissusFluidProjectile> type, Level level) {
        super(type, level);
        this.power = 2.0F;
    }


    public NarcissusFluidProjectile(Level level) {
        this(DreamtinkerEntityTypes.NarcissusSpitEntity.get(), level);
    }

    public NarcissusFluidProjectile(Level level, LivingEntity owner, FluidStack fluid, float power, IToolStackView tool) {
        this(level);
        this.setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        this.setOwner(owner);
        this.setFluid(fluid);
        this.setPower(power);
        this.setTool(tool);
        this.initialFluid = fluid.getAmount();
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

    static {
        FLUID = SynchedEntityData.defineId(NarcissusFluidProjectile.class, TinkerFluids.FLUID_DATA_SERIALIZER);
        CHASE_LIVING = SynchedEntityData.defineId(NarcissusFluidProjectile.class, EntityDataSerializers.INT);
        COLOR = SynchedEntityData.defineId(NarcissusFluidProjectile.class, EntityDataSerializers.INT);
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity p_36743_) {
        return (getChaseLiving() < 1 || p_36743_ instanceof EndCrystal ||
                (p_36743_ instanceof LivingEntity entity && entity.isAlive() && !(p_36743_ instanceof ArmorStand))) &&
               (super.canHitEntity(p_36743_) || !p_36743_.isSpectator() && !p_36743_.canBeHitByProjectile());
    }

    public float getInitialFluid() {
        return initialFluid;
    }

    /**
     * @param hit 可能为 null
     * @param t   命中最早时刻 ∈[0,1]；未命中时无意义
     */ // --- 辅助结构 ---
    private record ResultTOI(EntityHitResult hit, double t) {
    }

    private ResultTOI sweepToFirstEntityHit(Vec3 from, Vec3 vel) {
        final int SAMPLES = 6;
        final double inflate = 1.0;
        final double EPS = 1e-4;
        final int MAX_BISECT = 12;

        double tLo = 0.0;

        for (int i = 1; i <= SAMPLES; i++) {
            double t = (double) i / (double) SAMPLES;

            Vec3 midFrom = from.add(vel.scale(tLo));
            Vec3 midTo = from.add(vel.scale(t));
            Vec3 seg = midTo.subtract(midFrom);

            // 关键：把 AABB 移到本段起点 midFrom 再 expandTowards
            AABB boxAtSegStart = this.getBoundingBox().move(
                    midFrom.x - this.getX(),
                    midFrom.y - this.getY(),
                    midFrom.z - this.getZ()
            );
            AABB sweepBox = boxAtSegStart.expandTowards(seg).inflate(inflate);

            EntityHitResult hr = ProjectileUtil.getEntityHitResult(
                    this.level(), this, midFrom, midTo, sweepBox, this::canHitEntity
            );

            if (hr != null && hr.getType() != HitResult.Type.MISS){
                double lo = tLo, hi = t;
                EntityHitResult best = hr;

                for (int k = 0; k < MAX_BISECT && hi - lo > EPS; k++) {
                    double midT = (lo + hi) * 0.5;

                    Vec3 a = from.add(vel.scale(lo));
                    Vec3 b = from.add(vel.scale(midT));
                    Vec3 d = b.subtract(a);

                    // 同样：AABB 移到 a 再 expandTowards
                    AABB boxAtA = this.getBoundingBox().move(
                            a.x - this.getX(),
                            a.y - this.getY(),
                            a.z - this.getZ()
                    );
                    AABB sweepBox2 = boxAtA.expandTowards(d).inflate(inflate);

                    EntityHitResult test = ProjectileUtil.getEntityHitResult(
                            this.level(), this, a, b, sweepBox2, this::canHitEntity
                    );

                    if (test != null && test.getType() != HitResult.Type.MISS){
                        best = test;
                        hi = midT;
                    }else {
                        lo = midT;
                    }
                }

                return new ResultTOI(best, hi);
            }

            tLo = t;
        }

        return new ResultTOI(null, 1.0);
    }

    private DamageSource dmg(Entity target, Entity entity1) {
        Entity causingEntity = target.level() == entity1.level() ? entity1 : null;
        Entity direct = null == causingEntity ? this : null;
        ResourceKey<DamageType> type =
                null != toolStackView && 2 < toolStackView.getModifierLevel(DreamtinkerModifiers.Ids.icy_memory) ?
                DreamtinkerDamageTypes.NULL_VOID :
                null != toolStackView && 1 < toolStackView.getModifierLevel(DreamtinkerModifiers.Ids.icy_memory) ?
                DamageTypes.SONIC_BOOM :
                null != toolStackView && 0 < toolStackView.getModifierLevel(DreamtinkerModifiers.Ids.icy_memory) ?
                TinkerDamageTypes.FLUID_COLD.melee() :
                DamageTypes.MOB_PROJECTILE;
        return DreamtinkerDamageTypes.source(target.level().registryAccess(), type, direct, causingEntity);
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
        if (0 < getChaseLiving() && !this.onGround()){
            List<Entity> candidates = this.level().getEntitiesOfClass(
                    Entity.class,
                    this.getBoundingBox().inflate(12),
                    t -> t instanceof EndCrystal || t instanceof LivingEntity && t.isAlive()
                                                    && t != this.getOwner()
                                                    && !(this.getOwner() != null && t.isAlliedTo(this.getOwner()))
                                                    && !(t instanceof ArmorStand)
            );
            if (!candidates.isEmpty()){
                Entity nearest = candidates.stream()
                                           .min(Comparator.comparingDouble(e -> e.distanceToSqr(this)))
                                           .get();
                Vec3 aim = nearest.position().add(0, nearest.getBbHeight() * 0.5, 0).subtract(this.position());
                vel = this.getDeltaMovement().add(aim.normalize()).scale(0.75 * getChaseLiving());
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

            if (!ForgeEventFactory.onProjectileImpact(this, toi.hit)){
                this.onHit(toi.hit); // 只实体命中
            }
            impacted = true;
        }

        if (!this.isRemoved() && !impacted){
            // 未命中：推进到完整终点
            Vec3 to = from.add(vel);
            this.setPos(to.x, to.y, to.z);
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
            if (this.level().isClientSide){
                Vec3 p = this.getTrailRenderPosition();
                this.shortTrail.tick(p, v2, 5, 0.055D, 0.10D, 0.72F);
                this.trail.tick(p, v2, 12, 0.095D, 0.15D, 0.96F);
            }
        }

        if (this.getY() > this.level().getMaxBuildHeight() + 64
            || this.getY() < this.level().getMinBuildHeight() - 64
            || this.life <= 0){
            this.discard();
        }
    }


    public boolean isCritArrow() {return this.crit;}

    public void setCrit(boolean crit) {this.crit = crit;}

    @Override
    protected void onHit(HitResult p_37260_) {
        HitResult.Type hitresult$type = p_37260_.getType();
        if (hitresult$type == HitResult.Type.ENTITY){
            this.onHitEntity((EntityHitResult) p_37260_);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, p_37260_.getLocation(), GameEvent.Context.of(this, null));
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        Entity target = result.getEntity();
        int dmg = (int) this.getDamage();

        if (this.isCritArrow()){
            long j = this.random.nextInt(dmg / 2 + 2);
            dmg = (int) Math.min(j + (long) dmg, 2147483647L);
        }

        Entity entity1 = this.getOwner();
        if (null != entity1){
            if (entity1 instanceof LivingEntity){
                ((LivingEntity) entity1).setLastHurtMob(target);
            }
            target.invulnerableTime = 0;
            if (DamageProbe.damageHandler(target, dmg(target, entity1),
                                          null != toolStackView ? (toolStackView.getModifierLevel(DreamtinkerModifiers.Ids.icy_memory) + 1) * dmg :
                                          dmg)){

                if (!this.level().isClientSide && entity1 instanceof LivingEntity){
                    LivingEntity living_target = DTHelper.getLivingTarget(target);
                    if (null != living_target)
                        EnchantmentHelper.doPostHurtEffects(living_target, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) entity1, target);
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
            FluidStack feedbackFluid = fluid.copy();
            FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
            if (null != entity1){
                if (recipe.hasEntityEffects()){
                    int times = null != toolStackView ? Math.max(1, MemoryBase.getLevel(toolStackView) / 3) : 1;
                    DamageSource damagesource = dmg(target, entity1);
                    ToolAttackContext attackContext = 0 < toolStackView.getModifierLevel(DreamtinkerModifiers.flaming_memory.getId()) ?
                                                      ToolAttackContext.attacker((LivingEntity) this.getOwner()).target(target).cooldown(1)
                                                                       .applyAttributes().projectile(this)
                                                                       .build() :
                                                      ToolAttackContext.attacker((LivingEntity) this.getOwner()).target(target).cooldown(1)
                                                                       .applyAttributes()
                                                                       .build();
                    for (int i = 0; i < times; i++) {
                        target.invulnerableTime = 0;
                        if (toolStackView != null && entity1.level() == target.level()){
                            ToolAttackUtil.performAttack(toolStackView, attackContext);
                        }else
                            DamageProbe.damageHandler(target, damagesource,
                                                      null != toolStackView ? (toolStackView.getModifierLevel(DreamtinkerModifiers.Ids.icy_memory) + 1) * dmg :
                                                      dmg);

                    }
                    FluidEffectContext.Entity context = this.buildContext().location(result.getLocation()).target(target);
                    int hate = 1;
                    if (null != toolStackView)
                        hate = Math.max(1, toolStackView.getModifierLevel(DreamtinkerModifiers.Ids.hate_memory) + 1);

                    int consumed = recipe.applyToEntity(fluid, this.power, context, IFluidHandler.FluidAction.EXECUTE);
                    if (0 == consumed)
                        consumed = Math.max(100, initialFluid) / hate;
                    fluid.shrink(consumed);
                    if (fluid.isEmpty()){
                        this.discard();
                    }else {
                        this.setFluid(fluid);
                    }
                }
                if (entity1 instanceof LivingEntity livingOwner){
                    NarcissusFluidFeedbacks.onProjectileHit(this, livingOwner, target, feedbackFluid);
                }
            }
        }
    }

    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        double x = packet.getXa();
        double y = packet.getYa();
        double z = packet.getZa();
        Vec3 motion = new Vec3(x, y, z);
        this.setDeltaMovement(motion);
        if (false){
            this.shortTrail.seedLine(this.position(), motion, 5, 0.35D);
            this.trail.seedLine(this.position(), motion, 5, 0.35D);
        }
    }

    public Vec3 getTrailRenderPosition() {
        return this.position();
    }

    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putFloat("power", this.power);
        nbt.putBoolean("nas_crit", this.crit);

        FluidStack fluid = this.getFluid();
        if (!fluid.isEmpty()){
            nbt.put("fluid", fluid.writeToNBT(new CompoundTag()));
        }

    }

    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.power = nbt.getFloat("power");
        this.crit = nbt.getBoolean("nas_crit");

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

    @Override
    public float getPower() {
        return this.power;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(FLUID, FluidStack.EMPTY);
        this.entityData.define(CHASE_LIVING, 0);
        this.entityData.define(COLOR, 0);
    }

    @Override
    public void setPower(float power) {
        this.power = power;
    }

    @Override
    public float getDamage() {
        return Mth.ceil(Mth.clamp(this.getDeltaMovement().length() * this.power, 0.0F, Integer.MAX_VALUE));
    }

    protected @NotNull Component getTypeName() {
        return this.getFluid().getDisplayName();
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    public IToolStackView getTool() {
        return this.toolStackView;
    }

    public void setTool(@NotNull IToolStackView tool) {
        this.toolStackView = tool;
        this.entityData.set(CHASE_LIVING, tool.getModifierLevel(DreamtinkerModifiers.Ids.soul_core) + 1);
        this.entityData.set(COLOR, DTHelper.materialToRender(0, tool.getMaterial(0)));
    }

}
