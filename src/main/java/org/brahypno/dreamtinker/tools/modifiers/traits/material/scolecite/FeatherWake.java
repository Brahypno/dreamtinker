package org.brahypno.dreamtinker.tools.modifiers.traits.material.scolecite;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

public class FeatherWake extends Modifier implements ProjectileHitModifierHook {
    private static boolean isInFeatherFan(AABB box, Vec3 origin, Vec3 dir, double length, double baseWidth, double endWidth) {
        Vec3 center = box.getCenter();
        Vec3 rel = center.subtract(origin);

        double forward = rel.dot(dir);
        if (forward < 0.0D || forward > length){
            return false;
        }

        Vec3 side = new Vec3(-dir.z, 0.0D, dir.x);
        if (side.lengthSqr() < 1.0E-6D){
            side = new Vec3(1.0D, 0.0D, 0.0D);
        }
        side = side.normalize();

        double sideways = Math.abs(rel.dot(side));

        // 距离越远，风刃越宽
        double t = forward / length;
        double allowedWidth = Mth.lerp(t, baseWidth, endWidth);

        // 给实体碰撞箱半径一点容错，否则大体型边缘会漏
        double entityAllowance = Math.max(box.getXsize(), box.getZsize()) * 0.5D;

        return sideways <= allowedWidth + entityAllowance;
    }

    private static void spawnFeatherwakeSlash(Level level, Vec3 hitPos, Vec3 dir, double range) {
        if (!(level instanceof ServerLevel serverLevel)){
            return;
        }

        Vec3 normal = new Vec3(-dir.z, 0.0D, dir.x);
        if (normal.lengthSqr() < 1.0E-6D){
            normal = new Vec3(1.0D, 0.0D, 0.0D);
        }
        normal = normal.normalize();

        for (int i = 0; i < 12; i++) {
            double t = i / 11.0D;
            Vec3 pos = hitPos
                    .add(dir.scale(0.4D + range * t))
                    .add(normal.scale((serverLevel.random.nextDouble() - 0.5D) * 0.45D))
                    .add(0.0D, 0.15D + serverLevel.random.nextDouble() * 0.35D, 0.0D);

            serverLevel.sendParticles(
                    ParticleTypes.END_ROD,
                    pos.x,
                    pos.y,
                    pos.z,
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.015D
            );
        }
    }

    @Override
    public boolean onProjectileHitEntity(
            ModifierNBT modifiers,
            ModDataNBT persistentData,
            ModifierEntry modifier,
            Projectile projectile,
            EntityHitResult hit,
            @Nullable LivingEntity attacker,
            @Nullable LivingEntity target,
            boolean notBlocked
    ) {
        if (!notBlocked || projectile.level().isClientSide){
            return false;
        }

        if (target == null || target.isDeadOrDying()){
            return false;
        }

        Vec3 dir = projectile.getDeltaMovement();
        if (dir.lengthSqr() < 1.0E-6D){
            return false;
        }

        dir = dir.normalize();

        float projectileDamage = ETModifierCheck.getDamage(projectile);
        if (projectileDamage <= 0.0F){
            return false;
        }

        int level = modifier.getLevel();

        float slashDamage = projectileDamage * (0.7F + 0.15F * level);
        double range = Math.min(4.0D, 2.5D + 0.5D * level);
        double radius = 0.45D + 0.05D * level;
        Vec3 hitPos = hit.getLocation();

        // 稍微从目标身后开始，避免重复切到原目标
        Vec3 start = hitPos.add(dir.scale(0.35D));
        Vec3 end = hitPos.add(dir.scale(range));

        AABB box = new AABB(start, end).inflate(radius);

        DamageSource source = projectile.damageSources().indirectMagic(projectile, attacker);
        Vec3 origin = hit.getLocation().add(dir.scale(0.25D));

        double length = 4.0D;
        double baseWidth = 0.45D;
        double endWidth = 2.0D;

        AABB searchBox = new AABB(
                origin,
                origin.add(dir.scale(length))
        ).inflate(endWidth, 1.0D, endWidth);

        for (LivingEntity victim : projectile.level().getEntitiesOfClass(LivingEntity.class, searchBox)) {
            if (victim == target || victim == attacker || victim == projectile.getOwner()){
                continue;
            }

            if (victim.isDeadOrDying()){
                continue;
            }

            if (victim instanceof ArmorStand stand && stand.isMarker()){
                continue;
            }

            if (attacker != null && attacker.isAlliedTo(victim)){
                continue;
            }

            if (!isInFeatherFan(victim.getBoundingBox(), origin, dir, length, baseWidth, endWidth)){
                continue;
            }

            victim.hurt(source, slashDamage);
        }

        spawnFeatherwakeSlash(projectile.level(), hitPos, dir, range);

        return false;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT);
        super.registerHooks(hookBuilder);
    }
}
