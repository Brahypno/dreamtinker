package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.ArrowInterface;
import org.dreamtinker.dreamtinker.utils.ProjectileHitMemory;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.rangedHit;

public class RangedShoot extends NoLevelsModifier implements ArrowInterface {
    private static final String mark = "dreamtinker_ranged_shot";

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.ArrowInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (null != attacker && null != target && !ProjectileHitMemory.hasTriggered(mark, projectile, target.getUUID())){
            if (rangedHit.get() <= 1e-6)
                return false;
            double dis = attacker.position().distanceTo(target.position());
            double ratio = Math.max(dis / rangedHit.get(), 0.25);
            Vec3 vel = projectile.getDeltaMovement().scale(ratio);
            projectile.setDeltaMovement(vel);
            if (projectile instanceof AbstractArrow arrow){
                arrow.setCritArrow(rangedHit.get() < dis);
                arrow.setBaseDamage(arrow.getBaseDamage() * ratio);
            }
            ProjectileHitMemory.markTriggered(mark, projectile, target.getUUID());
            Vec3 dir = vel.lengthSqr() > 1e-6 ? vel : target.position().subtract(projectile.position());
            dir = dir.normalize();
            projectile.setPos(projectile.getX() + dir.x * 0.08,
                              projectile.getY() + dir.y * 0.08,
                              projectile.getZ() + dir.z * 0.08);
        }
        return false;
    }

    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (null != arrow)
            arrow.setPierceLevel((byte) ((arrow.getPierceLevel() + 1) * 2));
    }
}
