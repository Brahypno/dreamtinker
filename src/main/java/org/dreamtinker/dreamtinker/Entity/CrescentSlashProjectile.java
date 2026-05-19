package org.dreamtinker.dreamtinker.Entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.tools.modifiers.events.VisionaryDrops;
import org.dreamtinker.dreamtinker.utils.DTDamageUtils;
import org.jetbrains.annotations.NotNull;

import static org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes.many_wishes;

public class CrescentSlashProjectile extends AbstractSlashProjectile {
    private static final EntityDataAccessor<Boolean> OVERRIDE_DAMAGE_AND_PIERCE =
            SynchedEntityData.defineId(CrescentSlashProjectile.class, EntityDataSerializers.BOOLEAN);

    public CrescentSlashProjectile(EntityType<? extends CrescentSlashProjectile> type, Level level) {
        super(type, level);
    }

    public CrescentSlashProjectile(Level level) {
        this(DreamtinkerEntityTypes.CRESCENT_SLASH.get(), level);
    }

    public CrescentSlashProjectile(EntityType<? extends CrescentSlashProjectile> type, Level level, LivingEntity owner) {
        super(type, level, owner);
    }

    public static void shootDangerousFrom(
            Level level,
            LivingEntity owner,
            float power,
            int maxLife,
            double speed
    ) {
        shootFrom(level, owner, owner.getLookAngle(), power, maxLife, speed, 0xB86BFF, 230, 1.35F, 2.80F, true);
    }

    public static CrescentSlashProjectile shootFrom(
            Level level,
            LivingEntity owner,
            Vec3 direction,
            float power,
            int maxLife,
            double speed,
            int rgb,
            int alpha,
            float lengthScale,
            float widthScale,
            boolean overrideDamageAndPierce
    ) {
        CrescentSlashProjectile projectile = new CrescentSlashProjectile(
                DreamtinkerEntityTypes.CRESCENT_SLASH.get(),
                level,
                owner
        );

        projectile.setOverrideDamageAndPierce(overrideDamageAndPierce);

        return shootProjectile(
                projectile,
                level,
                owner,
                direction,
                power,
                maxLife,
                overrideDamageAndPierce ? (byte) -1 : (byte) 0,
                speed,
                rgb,
                alpha,
                lengthScale,
                widthScale,
                0.0F
        );
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OVERRIDE_DAMAGE_AND_PIERCE, false);
    }

    public boolean isOverrideDamageAndPierce() {
        return this.entityData.get(OVERRIDE_DAMAGE_AND_PIERCE);
    }

    public void setOverrideDamageAndPierce(boolean value) {
        this.entityData.set(OVERRIDE_DAMAGE_AND_PIERCE, value);
    }

    @Override
    protected double getEntityHitHalfWidth() {
        return 1.75D * this.getWidthScale();
    }

    @Override
    protected double getEntityHitHalfHeight() {
        return 0.8D;
    }

    @Override
    protected boolean canPierceEntities() {
        return this.isOverrideDamageAndPierce() || this.getPierceLevel() > 0;
    }

    @Override
    protected boolean hasInfiniteEntityPiercing() {
        return this.isOverrideDamageAndPierce();
    }

    @Override
    protected int getMaxEntityHits() {
        if (this.isOverrideDamageAndPierce()){
            return Integer.MAX_VALUE;
        }

        return Math.max(1, this.getPierceLevel() + 1);
    }

    @Override
    protected boolean doHurt(Entity target, float amount) {
        if (this.isOverrideDamageAndPierce()){
            DamageSource source = DreamtinkerDamageTypes.source(target.level().registryAccess(), many_wishes, this.getOwner(), this);
            target.getPersistentData().putBoolean(VisionaryDrops.Visionary, true);
            return DTDamageUtils.damageHandler(target, source, amount);
        }

        return super.doHurt(target, amount);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("OverrideDamageAndPierce", this.isOverrideDamageAndPierce());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("OverrideDamageAndPierce")){
            this.setOverrideDamageAndPierce(tag.getBoolean("OverrideDamageAndPierce"));
        }
    }

}