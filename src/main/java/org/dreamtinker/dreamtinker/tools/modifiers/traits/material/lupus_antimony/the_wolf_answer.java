package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.lupus_antimony;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

import static org.dreamtinker.dreamtinker.utils.DTModifierCheck.getMainhandModifierlevel;

public class the_wolf_answer extends BattleModifier {

    {
        MinecraftForge.EVENT_BUS.addListener(this::LivingAttackEvent);
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        LivingEntity target = context.getLivingTarget();
        if (target == null)
            return;
        float curHP = target.getHealth();
        if (damageAttempted < curHP){
            target.setHealth(curHP - damageAttempted);
            if (target.getHealth() < curHP){
                target.setLastHurtByMob(context.getAttacker());
                if (context.getAttacker() instanceof Player player)
                    target.setLastHurtByPlayer(player);
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
        }
    }

    public void LivingAttackEvent(LivingAttackEvent event) {
        if (event.getSource()
                 .getEntity() instanceof LivingEntity entity){
            if (0 < getMainhandModifierlevel(entity,
                                             this.getId())){
                event.getEntity().invulnerableTime = 0;
                //event.getSource().bypassArmor().bypassMagic().bypassEnchantments().bypassInvul();
            }
        }
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        int types = 0;
        if (null != context.getLivingTarget())
            types += context.getLivingTarget()
                            .getActiveEffects().size();
        return damage * Math.max(1,
                                 types);
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (null != target && !target.level().isClientSide && projectile instanceof AbstractArrow arrow){
            int types = target.getActiveEffects().size();
            arrow.setBaseDamage(arrow.getBaseDamage() * (1 + types));
        }
        return false;
    }
}
