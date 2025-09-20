package org.dreamtinker.dreamtinker.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

public class NarcissusFluidProjectile extends AbstractArrow {
    private IToolStackView toolStackView;
    private static final EntityDataAccessor<FluidStack> FLUID;
    private float power;
    private int knockback;


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
        this.setNoPhysics(true);
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
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        if (this.getKnockback() > 0){
            Vec3 vec3 = this.getDeltaMovement().multiply((double) 1.0F, (double) 0.0F, (double) 1.0F).normalize().scale((double) this.knockback * 0.6);
            if (vec3.lengthSqr() > (double) 0.0F){
                target.push(vec3.x, 0.1, vec3.z);
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

                    fluid.shrink(consumed);
                    if (fluid.isEmpty()){
                        this.discard();
                    }else {
                        this.setFluid(fluid);
                    }
                }
            }
            ToolAttackUtil.attackEntity(toolStackView, (LivingEntity) this.getOwner(), InteractionHand.MAIN_HAND, target, NO_COOLDOWN, false,
                                        Util.getSlotType(InteractionHand.MAIN_HAND));
        }

    }

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
        super.defineSynchedData();
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

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return Items.AIR.getDefaultInstance();
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

    public void setKnockback(int knockback) {
        this.knockback = knockback;
    }

    public int getKnockback() {
        return this.knockback;
    }

    protected @NotNull Component getTypeName() {
        return this.getFluid().getDisplayName();
    }

    public void setTool(IToolStackView tool) {
        this.toolStackView = tool;
    }

    public IToolStackView getTool() {
        return this.toolStackView;
    }

    static {
        FLUID = SynchedEntityData.defineId(NarcissusFluidProjectile.class, TinkerFluids.FLUID_DATA_SERIALIZER);
    }
}
