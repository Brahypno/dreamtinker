package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileShootModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.UUID;

public class ReturningArrow extends NoLevelsModifier implements ProjectileLaunchModifierHook, ProjectileShootModifierHook, ProjectileHitModifierHook {
    private static final String ORIGIN_X = "dreamtinker:return_origin_x";
    private static final String ORIGIN_Y = "dreamtinker:return_origin_y";
    private static final String ORIGIN_Z = "dreamtinker:return_origin_z";
    private static final String RETURNING = "dreamtinker:returning_projectile";

    private static void rememberOrigin(Projectile projectile) {
        if (projectile.level().isClientSide()){
            return;
        }

        CompoundTag data = projectile.getPersistentData();
        Vec3 pos = projectile.position();

        data.putDouble(ORIGIN_X, pos.x);
        data.putDouble(ORIGIN_Y, pos.y);
        data.putDouble(ORIGIN_Z, pos.z);
        data.putFloat("dreamtinker:return_velocity", (float) projectile.getDeltaMovement().length());
    }

    public static boolean hasOrigin(Projectile projectile) {
        CompoundTag data = projectile.getPersistentData();
        return data.contains(ORIGIN_X)
               && data.contains(ORIGIN_Y)
               && data.contains(ORIGIN_Z);
    }

    public static boolean isReturning(Projectile projectile) {
        return projectile.getPersistentData().getBoolean(RETURNING);
    }

    public static void markReturning(Projectile projectile) {
        projectile.getPersistentData().putBoolean(RETURNING, true);
    }

    public static boolean spawnReturningCopy(Projectile projectile, HitResult hitResult, double arcScale) {
        if (!(projectile.level() instanceof ServerLevel level)){
            return false;
        }

        if (isReturning(projectile) || !hasOrigin(projectile)){
            return false;
        }

        // 有 pierce 的箭命中实体时，让它先正常穿透，不触发回归
        if (projectile instanceof AbstractArrow arrow
            && hitResult.getType() == HitResult.Type.ENTITY
            && arrow.getPierceLevel() > 0){
            return false;
        }

        Vec3 hitPos = projectile.position();
        CompoundTag data = projectile.getPersistentData();

        Vec3 origin = new Vec3(
                data.getDouble(ORIGIN_X),
                data.getDouble(ORIGIN_Y),
                data.getDouble(ORIGIN_Z)
        );

        double speed = data.getFloat("dreamtinker:return_velocity");
        if (speed <= 0.0D){
            speed = projectile.getDeltaMovement().length();
        }

        double distance = hitPos.distanceTo(origin);
        double lift = Math.min(8.0D, distance * arcScale);

        Vec3 aimPos = origin.add(0.0D, lift, 0.0D);
        Vec3 movement = aimPos.subtract(hitPos);

        if (movement.lengthSqr() < 1.0E-6D){
            return false;
        }

        CompoundTag copyTag = new CompoundTag();
        projectile.saveWithoutId(copyTag);

        Entity entity = projectile.getType().create(level);
        if (!(entity instanceof Projectile copy)){
            return false;
        }

        copy.load(copyTag);
        copy.setUUID(UUID.randomUUID());

        Vec3 velocity = movement.normalize().scale(speed);

        copy.setPos(hitPos.x, hitPos.y, hitPos.z);
        copy.setDeltaMovement(velocity);
        faceVelocity(copy, velocity);

        copy.hurtMarked = true;
        markReturning(copy);

        if (copy instanceof AbstractArrow copyArrow){
            copyArrow.pickup = AbstractArrow.Pickup.DISALLOWED;
            copyArrow.shakeTime = 0;
            copyArrow.setNoPhysics(false);
        }

        level.addFreshEntity(copy);
        return true;
    }

    public static void faceVelocity(Entity entity, Vec3 velocity) {
        double horizontal = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);

        float yRot = (float) (Mth.atan2(velocity.x, velocity.z) * Mth.RAD_TO_DEG);
        float xRot = (float) (Mth.atan2(velocity.y, horizontal) * Mth.RAD_TO_DEG);

        entity.setYRot(yRot);
        entity.setXRot(xRot);

        entity.yRotO = yRot;
        entity.xRotO = xRot;
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_SHOT, ModifierHooks.PROJECTILE_HIT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        rememberOrigin(projectile);
    }

    @Override
    public void onProjectileShoot(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        rememberOrigin(projectile);
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        ResourceLocation key = modifier.getId();
        // if we already boosted power from an entity, don't boost again
        // minimizes issues with projectile bounces and piercing
        if (!persistentData.getBoolean(key)){
            // as soon as we attempt to boost, mark this modifier as having run
            // means the second entity will not get to apply its boost if the first did not apply it
            if (spawnReturningCopy(projectile, hit, 0.12D))
                persistentData.putBoolean(key, true);
        }
        return false;
    }

    @Override
    public boolean onProjectileHitsBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity owner) {
        ResourceLocation key = modifier.getId();
        // if we already boosted power from an entity, don't boost again
        // minimizes issues with projectile bounces and piercing
        if (!persistentData.getBoolean(key)){
            // as soon as we attempt to boost, mark this modifier as having run
            // means the second entity will not get to apply its boost if the first did not apply it
            if (spawnReturningCopy(projectile, hit, 0.12D))
                persistentData.putBoolean(key, true);
        }
        return false;
    }

}
