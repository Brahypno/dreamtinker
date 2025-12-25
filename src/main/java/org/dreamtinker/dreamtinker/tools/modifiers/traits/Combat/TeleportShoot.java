package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.ArrowInterface;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

public class TeleportShoot extends Modifier implements ArrowInterface {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        this.ArrowInterfaceInit(hookBuilder);
        super.registerHooks(hookBuilder);
    }

    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (shooter.level().isClientSide)
            return;
        CrimsonPort(projectile, modifier.getLevel());

    }

    public void onProjectileShoot(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (projectile.level().isClientSide)
            return;
        CrimsonPort(projectile, modifier.getLevel());
    }

    private void CrimsonPort(Projectile projectile, int level) {
        Vec3 v = projectile.getDeltaMovement();          // blocks / tick
        double tTicks = level;// * 20.0 * .5;
        Vec3 futurePos = projectile.position().add(v.scale(tTicks));

        projectile.setPos(futurePos.x, futurePos.y, futurePos.z);
        projectile.hasImpulse = true;
    }
}
