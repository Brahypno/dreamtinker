package org.dreamtinker.dreamtinker.modifier.material.lupus_antimony;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static org.dreamtinker.dreamtinker.utils.modiferCheck.getMainhandModifierlevel;

public class the_wolf_answer extends BattleModifier {

    {
        MinecraftForge.EVENT_BUS.addListener(this::LivingAttackEvent);
    }
    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        LivingEntity target=context.getLivingTarget();
        if(target==null) return;
        float curHP=target.getHealth();
        if(damageAttempted<curHP){
            target.setHealth(curHP-damageAttempted);
            if(target.getHealth()<curHP) {
                target.setLastHurtByMob(context.getAttacker());
                if(context.getAttacker() instanceof Player player)
                    target.setLastHurtByPlayer(player);
            }
        }else {
            if(context.getAttacker() instanceof Player player)
                target.die(DamageSource.playerAttack(player));
            else
                target.die(DamageSource.mobAttack(context.getAttacker()));
        }
    }
    public void LivingAttackEvent(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity entity) {
            if (0<getMainhandModifierlevel(entity,this.getId())) {
                event.getEntity().invulnerableTime = 0;
                event.getSource().bypassArmor().bypassMagic().bypassEnchantments().bypassInvul();
            }
        }
    }
    @Override
    public boolean isNoLevels(){return true;}
}
