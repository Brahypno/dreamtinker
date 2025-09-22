package org.dreamtinker.dreamtinker.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.dreamtinker.dreamtinker.register.DreamtinkerEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import static slimeknights.tconstruct.library.tools.helper.ToolAttackUtil.NO_COOLDOWN;

public class NarcissusFluidProjectile extends Projectile {
    private IToolStackView toolStackView;
    private static final EntityDataAccessor<FluidStack> FLUID;
    private float power = 1;

    private int knockback = 1;
    private float basedamage = 1;//mock abstract arrow and to improved based on specific modifier


    public NarcissusFluidProjectile(EntityType<? extends NarcissusFluidProjectile> type, Level level) {
        super(type, level);
        this.power = 1.0F;
        this.knockback = 1;
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
        return p_36743_ instanceof LivingEntity entity && entity.isAlive() && (super.canHitEntity(p_36743_) || !p_36743_.canBeHitByProjectile());
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 from = this.position();
        Vec3 vel = this.getDeltaMovement();
        Vec3 to = from.add(vel);
        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
                this.level(), this, from, to,
                this.getBoundingBox().expandTowards(vel).inflate(1.0),
                this::canHitEntity);
        HitResult.Type hitType = null;
        if (hitResult != null){
            hitType = hitResult.getType();
        }
        if (hitType != HitResult.Type.MISS && hitResult != null && !ForgeEventFactory.onProjectileImpact(this, hitResult))
            this.onHit(hitResult);
        if (!this.isRemoved()){
            this.updateRotation();
            level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, getX(), getY(), getZ(), 0, 0, 0);
            Vec3 newLocation = this.position();
            Vec3 velocity = this.getDeltaMovement();
            newLocation = newLocation.add(velocity);

            velocity = velocity.scale((double) 0.99F);
            if (!this.isNoGravity()){
                FluidStack fluid = this.getFluid();
                velocity = velocity.add((double) 0.0F, fluid.getFluid().getFluidType().isLighterThanAir() ? 0.06 : -0.06, (double) 0.0F);
            }

            this.setDeltaMovement(velocity);
            this.setPos(newLocation);
        }
        if (this.getY() > (double) (this.level().getMaxBuildHeight() + 64) || this.getY() < (double) (this.level().getMinBuildHeight() - 64)){
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {

        Entity target = result.getEntity();
        float f = (float) this.getDeltaMovement().length();
        int i = Mth.ceil(Mth.clamp((double) f * this.getBaseDamage(), 0.0F, Integer.MAX_VALUE));

        if (this.isCritArrow()){
            long j = this.random.nextInt(i / 2 + 2);
            i = (int) Math.min(j + (long) i, 2147483647L);
        }

        Entity entity1 = this.getOwner();
        if (null != entity1){
            DamageSource damagesource = this.damageSources().mobProjectile(this, (LivingEntity) entity1);
            if (entity1 instanceof LivingEntity){
                ((LivingEntity) entity1).setLastHurtMob(target);
            }
            if (target instanceof LivingEntity livingentity && target.hurt(damagesource, (float) i)){
                if (this.knockback > 0){
                    double d0 = Math.max(0.0F, (double) 1.0F - livingentity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                    Vec3 vec3 =
                            this.getDeltaMovement().multiply(1.0F, 0.0F, 1.0F).normalize()
                                .scale((double) this.knockback * 0.6 * d0);
                    if (vec3.lengthSqr() > (double) 0.0F){
                        livingentity.push(vec3.x, 0.1, vec3.z);
                    }
                }

                if (!this.level().isClientSide && entity1 instanceof LivingEntity){
                    EnchantmentHelper.doPostHurtEffects(livingentity, entity1);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) entity1, livingentity);
                }

            }
        }

        if (this.isOnFire()){
            target.setSecondsOnFire(5);
        }

        FluidStack fluid = this.getFluid();
        Level level = this.level();
        if (!level.isClientSide && !fluid.isEmpty()){
            FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
            if (null != this.getOwner()){
                if (recipe.hasEntityEffects()){
                    FluidEffectContext.Entity context = this.buildContext().location(result.getLocation()).target(this.getOwner());
                    int consumed = recipe.applyToEntity(fluid, this.power, context, IFluidHandler.FluidAction.EXECUTE);

                    fluid.shrink(consumed);
                    if (fluid.isEmpty()){
                        this.discard();
                    }else {
                        this.setFluid(fluid);
                    }
                    //target.invulnerableTime = 0; potential improvements here
                    ToolAttackUtil.attackEntity(toolStackView, (LivingEntity) this.getOwner(), InteractionHand.MAIN_HAND, target, NO_COOLDOWN, false,
                                                Util.getSlotType(InteractionHand.MAIN_HAND));
                }
            }


        }

    }

    private double getBaseDamage() {return basedamage;}

    private boolean isCritArrow() {return false;}

    @Override
    protected void onHit(HitResult p_37260_) {
        HitResult.Type hitresult$type = p_37260_.getType();
        if (hitresult$type == HitResult.Type.ENTITY){
            this.onHitEntity((EntityHitResult) p_37260_);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, p_37260_.getLocation(), GameEvent.Context.of(this, (BlockState) null));
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(FLUID, FluidStack.EMPTY);
    }

    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        double x = packet.getXa();
        double y = packet.getYa();
        double z = packet.getZa();

        for (int i = 0; i < 7; ++i) {
            double offset = 0.4 + 0.1 * (double) i;
            this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY(), this.getZ(), x * offset, y, z * offset);
        }

        this.setDeltaMovement(x, y, z);
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putFloat("power", this.power);
        nbt.putInt("knockback", this.knockback);

        FluidStack fluid = this.getFluid();
        if (!fluid.isEmpty()){
            nbt.put("fluid", fluid.writeToNBT(new CompoundTag()));
        }

    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.power = nbt.getFloat("power");
        this.knockback = nbt.getInt("knockback");

        this.setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid")));
    }

    public FluidStack getFluid() {
        return this.entityData.get(FLUID);
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

    public void setTool(IToolStackView tool) {
        this.toolStackView = tool;
        //XXX: read modifier and improve status here.
    }

    public IToolStackView getTool() {
        return this.toolStackView;
    }

    static {
        FLUID = SynchedEntityData.defineId(NarcissusFluidProjectile.class, TinkerFluids.FLUID_DATA_SERIALIZER);
    }
}
