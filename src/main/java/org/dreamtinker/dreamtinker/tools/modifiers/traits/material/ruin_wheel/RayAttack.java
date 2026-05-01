package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.ruin_wheel;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.*;

public class RayAttack extends BattleModifier {
    private static void triggerBarrelRuin(
            LivingEntity attacker,
            Entity mainTarget,
            DamageSource branchSource,
            float originalDamage,
            int level
    ) {
        Level levelObj = attacker.level();
        if (levelObj.isClientSide || originalDamage <= 0.0F){
            return;
        }

        Vec3 dir = mainTarget.position().subtract(attacker.position());
        if (mainTarget instanceof Projectile projectile){
            // 1. 弹射物仍在飞行时，优先使用当前速度方向
            dir = projectile.getDeltaMovement();

            // 2. 弹射物已落地/插地时，deltaMovement 可能已经被清零；
            //    此时尝试用上一 tick 位置推断飞行方向。
            if (dir.lengthSqr() < 1.0E-4D){
                dir = projectile.position().subtract(
                        new Vec3(projectile.xo, projectile.yo, projectile.zo)
                );
            }

            // 3. 如果 old position 也不可靠，就用攻击者视点到弹射物落点的方向。
            if (dir.lengthSqr() < 1.0E-4D){
                dir = projectile.position().subtract(attacker.getEyePosition());
            }
        }
        if (dir.lengthSqr() < 1.0E-4D){
            dir = attacker.getLookAngle();
        }
        if (dir.lengthSqr() < 1.0E-4D){
            return;
        }
        dir = dir.normalize();

        double distance = 4.5D + 2.75D * level;
        double step = 0.75D;
        double radius = 0.65D;

        float damage = originalDamage * (0.45F + 0.10F * level);
        int maxTargets = 1 + level / 2;

        Set<UUID> hitEntities = new HashSet<>();
        hitEntities.add(attacker.getUUID());
        hitEntities.add(mainTarget.getUUID());

        int hit = 0;

        for (double traveled = step; traveled <= distance && hit < maxTargets; traveled += step) {
            Vec3 center = mainTarget.position().add(dir.scale(traveled));
            AABB box = new AABB(
                    center.x - radius, center.y - radius, center.z - radius,
                    center.x + radius, center.y + radius, center.z + radius
            );

            List<LivingEntity> candidates = levelObj.getEntitiesOfClass(
                    LivingEntity.class,
                    box,
                    entity -> entity.isAlive()
                              && !hitEntities.contains(entity.getUUID())
                              && !attacker.isAlliedTo(entity)
                              && !(entity instanceof ArmorStand stand && stand.isMarker())
            );

            candidates.sort(Comparator.comparingDouble(entity -> entity.distanceToSqr(center)));

            for (LivingEntity pierceTarget : candidates) {
                boolean hurt = pierceTarget.hurt(branchSource, damage);
                if (hurt){
                    hitEntities.add(pierceTarget.getUUID());
                    damage *= 0.70F;
                    hit++;
                    break;
                }
            }
        }
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        if (null != target && !target.level().isClientSide){
            float Theoretical_damage = Math.max(0.5f, DTModifierCheck.getMeleeDamage(context.getAttacker(), context.getTarget(), tool, true));
            Theoretical_damage = Math.max(Theoretical_damage, damageDealt);
            DamageSource dmg = DreamtinkerDamageTypes.source(target.level().registryAccess(), DreamtinkerDamageTypes.ruin_wheel, context.makeDamageSource());
            triggerBarrelRuin(context.getAttacker(), target, dmg, Theoretical_damage, modifier.getLevel());
        }
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        float damage = DTModifierCheck.getDamage(projectile);
        if (null != target && null != attacker && attacker.isAlive() && !attacker.level().isClientSide){
            DamageSource dmg = DreamtinkerDamageTypes.source(target.level().registryAccess(), DreamtinkerDamageTypes.ruin_wheel, projectile, attacker);
            triggerBarrelRuin(attacker, target, dmg, damage, modifier.getLevel());
        }
        return false;
    }

    @Override
    public boolean onProjectileHitsBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity owner) {
        float damage = DTModifierCheck.getDamage(projectile);
        if (null != owner && owner.isAlive() && !owner.level().isClientSide){
            DamageSource dmg = DreamtinkerDamageTypes.source(owner.level().registryAccess(), DreamtinkerDamageTypes.ruin_wheel, projectile, owner);
            triggerBarrelRuin(owner, projectile, dmg, damage, modifier.getLevel());
        }
        return false;
    }
}
