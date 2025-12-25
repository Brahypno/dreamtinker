package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.lupus_antimony;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

public class TheWolfAnswer extends BattleModifier {

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        LivingEntity target = context.getLivingTarget();
        if (target == null)
            return;
        float curHP = target.getHealth();
        if (damageAttempted < curHP){
            target.setHealth(curHP - damageAttempted);
            if (target.getHealth() < curHP){
                if (context.getAttacker() instanceof Player player)
                    target.setLastHurtByPlayer(player);
                else
                    target.setLastHurtByMob(context.getAttacker());
            }
        }else {
            DamageSource dam;
            if (context.getAttacker() instanceof Player player)
                dam = context.getAttacker().level()
                             .damageSources()
                             .playerAttack(player);
            else
                dam = context.getAttacker().level()
                             .damageSources()
                             .mobAttack(context.getAttacker());
            target.setHealth(0);
            target.die(dam);
            target.dropAllDeathLoot(dam);
        }
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        int types = 0;
        if (null != context.getLivingTarget())
            types += context.getLivingTarget().getActiveEffects().size();
        return damage * (1 + types * 0.1f);
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (null != target && !target.level().isClientSide){
            int types = target.getActiveEffects().size();
            projectile.setDeltaMovement(projectile.getDeltaMovement().scale((1 + types * 0.1f)));
            target.invulnerableTime = 0;
        }
        return false;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (null != context.getLivingTarget() && !context.getLivingTarget().level().isClientSide){
            context.getLivingTarget().invulnerableTime = 0;
        }
    }

}
