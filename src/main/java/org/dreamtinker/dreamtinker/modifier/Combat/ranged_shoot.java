package org.dreamtinker.dreamtinker.modifier.Combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.rangedhit;

public class ranged_shoot extends BattleModifier {
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (null != attacker){
            double dis = attacker.position().distanceTo(hit.getEntity().position());
            projectile.setDeltaMovement(projectile.getDeltaMovement().scale(dis / rangedhit.get()));
            if (projectile instanceof AbstractHurtingProjectile ahp){
                ahp.xPower *= dis / rangedhit.get();
                ahp.yPower *= dis / rangedhit.get();
                ahp.zPower *= dis / rangedhit.get();
            }
            if (projectile instanceof AbstractArrow arrow){
                arrow.setCritArrow(rangedhit.get() < dis);
                arrow.setBaseDamage(arrow.getBaseDamage() * dis / rangedhit.get());
            }
        }
        return false;
    }

    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (null != arrow)
            arrow.setPierceLevel((byte) ((arrow.getPierceLevel() + 1) * 2));
    }
}
