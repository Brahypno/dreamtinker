package org.brahypno.dreamtinker.Entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
import net.minecraft.world.item.ItemStack;
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
import org.brahypno.esotericismtinker.utils.ETHelper;
import org.brahypno.esotericismtinker.utils.damage.DamageProbe;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Comparator;
import java.util.List;

public class NarcissusFluidProjectile extends Projectile implements ProjectileWithPower {
    private static final EntityDataAccessor<FluidStack> FLUID = SynchedEntityData.defineId(
            NarcissusFluidProjectile.class, TinkerFluids.FLUID_DATA_SERIALIZER
    );
    private static final EntityDataAccessor<Integer> CHASE_LIVING = SynchedEntityData.defineId(
            NarcissusFluidProjectile.class, EntityDataSerializers.INT
    );
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(
            NarcissusFluidProjectile.class, EntityDataSerializers.INT
    );
    private static final EntityDataAccessor<ItemStack> TOOL = SynchedEntityData.defineId(
            NarcissusFluidProjectile.class, EntityDataSerializers.ITEM_STACK
    );

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

    public NarcissusFluidProjectile(Level level, LivingEntity owner, FluidStack fluid, float power, ItemStack tool) {
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

    @Override
    protected void defineSynchedData() {
        this.entityData.define(FLUID, FluidStack.EMPTY);
        this.entityData.define(CHASE_LIVING, 0);
        this.entityData.define(COLOR, 0);
        this.entityData.define(TOOL, ItemStack.EMPTY);
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity entity) {
        return (
                       this.getChaseLiving() < 1
                       || entity instanceof EndCrystal
                       || entity instanceof LivingEntity living
                          && living.isAlive()
                          && !(entity instanceof ArmorStand)
               ) && (
                       super.canHitEntity(entity)
                       || !entity.isSpectator()
                          && !entity.canBeHitByProjectile()
               );
    }

    public float getInitialFluid() {
        return this.initialFluid;
    }

    private record ResultTOI(EntityHitResult hit, double t) {}

    private ResultTOI sweepToFirstEntityHit(Vec3 from, Vec3 velocity) {
        final int samples = 6;
        final double inflate = 1.0;
        final double epsilon = 1.0E-4;
        final int maxBisect = 12;

        double lowerTime = 0.0;

        for (int i = 1; i <= samples; i++) {
            double upperTime = (double) i / samples;
            Vec3 segmentStart = from.add(velocity.scale(lowerTime));
            Vec3 segmentEnd = from.add(velocity.scale(upperTime));
            Vec3 segment = segmentEnd.subtract(segmentStart);

            AABB boxAtSegmentStart = this.getBoundingBox().move(
                    segmentStart.x - this.getX(),
                    segmentStart.y - this.getY(),
                    segmentStart.z - this.getZ()
            );
            AABB sweepBox = boxAtSegmentStart.expandTowards(segment).inflate(inflate);

            EntityHitResult hit = ProjectileUtil.getEntityHitResult(
                    this.level(),
                    this,
                    segmentStart,
                    segmentEnd,
                    sweepBox,
                    this::canHitEntity
            );

            if (hit != null && hit.getType() != HitResult.Type.MISS){
                double low = lowerTime;
                double high = upperTime;
                EntityHitResult best = hit;

                for (int j = 0; j < maxBisect && high - low > epsilon; j++) {
                    double middleTime = (low + high) * 0.5;
                    Vec3 start = from.add(velocity.scale(low));
                    Vec3 end = from.add(velocity.scale(middleTime));
                    Vec3 delta = end.subtract(start);

                    AABB boxAtStart = this.getBoundingBox().move(
                            start.x - this.getX(),
                            start.y - this.getY(),
                            start.z - this.getZ()
                    );
                    AABB bisectSweepBox = boxAtStart.expandTowards(delta).inflate(inflate);

                    EntityHitResult test = ProjectileUtil.getEntityHitResult(
                            this.level(),
                            this,
                            start,
                            end,
                            bisectSweepBox,
                            this::canHitEntity
                    );

                    if (test != null && test.getType() != HitResult.Type.MISS){
                        best = test;
                        high = middleTime;
                    }else {
                        low = middleTime;
                    }
                }

                return new ResultTOI(best, high);
            }

            lowerTime = upperTime;
        }

        return new ResultTOI(null, 1.0);
    }

    private DamageSource dmg(Entity target, Entity owner) {
        Entity causingEntity = target.level() == owner.level() ? owner : null;
        Entity directEntity = causingEntity == null ? this : null;

        ItemStack tool = this.getStoredTool();
        int icyLevel = ModifierUtil.getModifierLevel(tool, DreamtinkerModifiers.Ids.icy_memory);

        ResourceKey<DamageType> type =
                icyLevel > 2 ? DreamtinkerDamageTypes.NULL_VOID :
                icyLevel > 1 ? DamageTypes.SONIC_BOOM :
                icyLevel > 0 ? TinkerDamageTypes.FLUID_COLD.melee() :
                DamageTypes.MOB_PROJECTILE;

        return DreamtinkerDamageTypes.source(
                target.level().registryAccess(),
                type,
                directEntity,
                causingEntity
        );
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isRemoved()){
            return;
        }

        this.updateRotation();

        Vec3 velocity = this.getDeltaMovement();

        if (this.getChaseLiving() > 0 && !this.onGround()){
            List<Entity> candidates = this.level().getEntitiesOfClass(
                    Entity.class,
                    this.getBoundingBox().inflate(12),
                    target -> target instanceof EndCrystal
                              || target instanceof LivingEntity living
                                 && living.isAlive()
                                 && target != this.getOwner()
                                 && !(this.getOwner() != null && target.isAlliedTo(this.getOwner()))
                                 && !(target instanceof ArmorStand)
            );

            if (!candidates.isEmpty()){
                Entity nearest = candidates.stream()
                                           .min(Comparator.comparingDouble(target -> target.distanceToSqr(this)))
                                           .orElse(null);

                if (nearest != null){
                    Vec3 aim = nearest.position()
                                      .add(0, nearest.getBbHeight() * 0.5, 0)
                                      .subtract(this.position());

                    if (aim.lengthSqr() > 1.0E-7){
                        velocity = this.getDeltaMovement()
                                       .add(aim.normalize())
                                       .scale(0.75 * this.getChaseLiving());
                    }
                }
            }
        }

        Vec3 from = this.position();
        ResultTOI timeOfImpact = this.sweepToFirstEntityHit(from, velocity);
        boolean impacted = false;

        if (timeOfImpact.hit() != null && timeOfImpact.hit().getType() != HitResult.Type.MISS){
            double time = Mth.clamp(timeOfImpact.t(), 0.0, 1.0);
            Vec3 hitPosition = from.add(velocity.scale(time));
            this.setPos(hitPosition.x, hitPosition.y, hitPosition.z);

            if (!ForgeEventFactory.onProjectileImpact(this, timeOfImpact.hit())){
                this.onHit(timeOfImpact.hit());
            }

            impacted = true;
        }

        if (!this.isRemoved() && !impacted){
            Vec3 destination = from.add(velocity);
            this.setPos(destination.x, destination.y, destination.z);
        }

        if (!this.isRemoved()){
            Vec3 newVelocity = this.getDeltaMovement();

            if (!impacted){
                newVelocity = velocity;
            }

            newVelocity = newVelocity.scale(0.99F);

            if (!this.isNoGravity()){
                FluidStack fluid = this.getFluid();
                double gravity = !fluid.isEmpty() && fluid.getFluid().getFluidType().isLighterThanAir()
                                 ? 0.06
                                 : -0.06;
                newVelocity = newVelocity.add(0.0, gravity, 0.0);
            }

            this.setDeltaMovement(newVelocity);
            --this.life;

            if (this.level().isClientSide){
                Vec3 position = this.getTrailRenderPosition();
                this.shortTrail.tick(position, newVelocity, 5, 0.055D, 0.10D, 0.72F);
                this.trail.tick(position, newVelocity, 12, 0.095D, 0.15D, 0.96F);
            }
        }

        if (
                this.getY() > this.level().getMaxBuildHeight() + 64
                || this.getY() < this.level().getMinBuildHeight() - 64
                || this.life <= 0
        ){
            this.discard();
        }
    }

    public boolean isCritArrow() {
        return this.crit;
    }

    public void setCrit(boolean crit) {
        this.crit = crit;
    }

    @Override
    protected void onHit(HitResult result) {
        if (result.getType() == HitResult.Type.ENTITY){
            this.onHitEntity((EntityHitResult) result);
            this.level().gameEvent(
                    GameEvent.PROJECTILE_LAND,
                    result.getLocation(),
                    GameEvent.Context.of(this, null)
            );
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        ItemStack toolStack = this.getStoredTool();

        int damage = (int) this.getDamage();

        if (this.isCritArrow()){
            long bonus = this.random.nextInt(damage / 2 + 2);
            damage = (int) Math.min(bonus + (long) damage, Integer.MAX_VALUE);
        }

        if (owner != null){
            if (owner instanceof LivingEntity livingOwner){
                livingOwner.setLastHurtMob(target);
            }

            target.invulnerableTime = 0;

            int icyLevel = ModifierUtil.getModifierLevel(toolStack, DreamtinkerModifiers.Ids.icy_memory);
            boolean damaged = DamageProbe.damageHandler(
                    target,
                    this.dmg(target, owner),
                    (icyLevel + 1) * damage
            );

            if (damaged){
                if (!this.level().isClientSide && owner instanceof LivingEntity livingOwner){
                    LivingEntity livingTarget = ETHelper.getLivingTarget(target);

                    if (livingTarget != null){
                        EnchantmentHelper.doPostHurtEffects(livingTarget, livingOwner);
                    }

                    EnchantmentHelper.doPostDamageEffects(livingOwner, target);
                }

                if (!this.level().isClientSide && this.isOnFire()){
                    target.setSecondsOnFire(5 * Math.max(1, this.level().random.nextInt(3)));
                }
            }
        }

        FluidStack fluid = this.getFluid();
        Level level = this.level();

        if (level.isClientSide || fluid.isEmpty()){
            return;
        }

        FluidStack feedbackFluid = fluid.copy();
        FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());

        if (owner != null){
            if (recipe.hasEntityEffects()){
                this.applyEntityEffects(result, target, owner, toolStack, fluid, recipe, damage);
            }

            if (owner instanceof LivingEntity livingOwner){
                NarcissusFluidFeedbacks.onProjectileHit(
                        this,
                        livingOwner,
                        target,
                        feedbackFluid
                );
            }
        }
    }

    private void applyEntityEffects(
            EntityHitResult result, Entity target, Entity owner, ItemStack toolStack,
            FluidStack fluid, FluidEffects recipe, int damage) {
        IToolStackView tool = toolStack.isEmpty() ? null : ToolStack.from(toolStack);
        int times = tool != null ? Math.max(1, MemoryBase.getLevel(tool) / 3) : 1;
        int icyLevel = ModifierUtil.getModifierLevel(toolStack, DreamtinkerModifiers.Ids.icy_memory);
        DamageSource damageSource = this.dmg(target, owner);

        ToolAttackContext attackContext = null;

        if (tool != null && owner instanceof LivingEntity livingOwner){
            ToolAttackContext.Builder builder = ToolAttackContext.attacker(livingOwner)
                                                                 .target(target)
                                                                 .cooldown(1)
                                                                 .applyAttributes();

            if (ModifierUtil.getModifierLevel(toolStack, DreamtinkerModifiers.flaming_memory.getId()) > 0){
                builder.projectile(this);
            }

            attackContext = builder.build();
        }

        for (int i = 0; i < times; i++) {
            target.invulnerableTime = 0;

            if (tool != null && attackContext != null && owner.level() == target.level()){
                ToolAttackUtil.performAttack(tool, attackContext);
            }else {
                DamageProbe.damageHandler(
                        target,
                        damageSource,
                        (icyLevel + 1) * damage
                );
            }
        }

        FluidEffectContext.Entity context = this.buildContext()
                                                .location(result.getLocation())
                                                .target(target);

        int hateLevel = ModifierUtil.getModifierLevel(toolStack, DreamtinkerModifiers.Ids.hate_memory);
        int hate = Math.max(1, hateLevel + 1);
        int consumed = recipe.applyToEntity(
                fluid,
                this.power,
                context,
                IFluidHandler.FluidAction.EXECUTE
        );

        if (consumed == 0){
            consumed = Math.max(100, this.initialFluid) / hate;
        }

        fluid.shrink(consumed);

        if (fluid.isEmpty()){
            this.discard();
        }else {
            this.setFluid(fluid);
        }
    }

    @Override
    public void recreateFromPacket(@NotNull ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);

        Vec3 motion = new Vec3(
                packet.getXa(),
                packet.getYa(),
                packet.getZa()
        );
        this.setDeltaMovement(motion);
    }

    public Vec3 getTrailRenderPosition() {
        return this.position();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        nbt.putFloat("power", this.power);
        nbt.putInt("initial_fluid", this.initialFluid);
        nbt.putInt("life", this.life);
        nbt.putBoolean("nas_crit", this.crit);

        FluidStack fluid = this.getFluid();

        if (!fluid.isEmpty()){
            nbt.put("fluid", fluid.writeToNBT(new CompoundTag()));
        }

        ItemStack tool = this.getStoredTool();

        if (!tool.isEmpty()){
            nbt.put("tool", tool.save(new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        this.power = nbt.contains("power", Tag.TAG_FLOAT)
                     ? nbt.getFloat("power")
                     : 2.0F;
        this.crit = nbt.getBoolean("nas_crit");

        if (nbt.contains("life", Tag.TAG_INT)){
            this.life = nbt.getInt("life");
        }

        if (nbt.contains("fluid", Tag.TAG_COMPOUND)){
            this.setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid")));
        }else {
            this.setFluid(FluidStack.EMPTY);
        }

        if (nbt.contains("initial_fluid", Tag.TAG_INT)){
            this.initialFluid = nbt.getInt("initial_fluid");
        }else {
            this.initialFluid = this.getFluid().getAmount();
        }

        if (nbt.contains("tool", Tag.TAG_COMPOUND)){
            this.setTool(ItemStack.of(nbt.getCompound("tool")));
        }else {
            this.setTool(ItemStack.EMPTY);
        }
    }

    public FluidStack getFluid() {
        return this.entityData.get(FLUID);
    }

    public void setFluid(FluidStack fluid) {
        this.entityData.set(FLUID, fluid.copy());
    }

    public int getChaseLiving() {
        return this.entityData.get(CHASE_LIVING);
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }

    private ItemStack getStoredTool() {
        return this.entityData.get(TOOL);
    }

    public ItemStack getTool() {
        return this.getStoredTool().copy();
    }

    public void setTool(ItemStack stack) {
        if (stack.isEmpty()){
            this.entityData.set(TOOL, ItemStack.EMPTY);
            this.entityData.set(CHASE_LIVING, 0);
            this.entityData.set(COLOR, 0);
            return;
        }

        ItemStack copy = stack.copy();
        copy.setCount(1);
        this.entityData.set(TOOL, copy);

        int soulCoreLevel = ModifierUtil.getModifierLevel(copy, DreamtinkerModifiers.Ids.soul_core);
        IToolStackView tool = ToolStack.from(copy);

        this.entityData.set(CHASE_LIVING, soulCoreLevel + 1);
        this.entityData.set(COLOR, ETHelper.materialToRender(0, tool.getMaterial(0)));
    }

    @Override
    public float getPower() {
        return this.power;
    }

    @Override
    public void setPower(float power) {
        this.power = power;
    }

    @Override
    public float getDamage() {
        return Mth.ceil(
                Mth.clamp(
                        this.getDeltaMovement().length() * this.power,
                        0.0F,
                        Integer.MAX_VALUE
                )
        );
    }

    @Override
    protected @NotNull Component getTypeName() {
        return this.getFluid().getDisplayName();
    }
}