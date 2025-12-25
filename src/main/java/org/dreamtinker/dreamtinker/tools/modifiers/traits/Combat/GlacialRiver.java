package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.*;

public class GlacialRiver extends BattleModifier {

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        if (null == context.getLivingTarget())
            return damage;
        Level level = context.getLivingTarget().level();
        float damageboost = 0;
        for (LivingEntity aoeTarget : level.getEntitiesOfClass(LivingEntity.class,
                                                               context.getAttacker().getBoundingBox().inflate(glacialRiverRange.get(), 0.25D, 5))) {
            if (aoeTarget.isAlliedTo(context.getAttacker()))
                continue;
            float life_hurt = (float) (aoeTarget.getMaxHealth() * glacialRiverPortion.get());
            float life = !glacialRiverKillPlayer.get() && aoeTarget instanceof Player && aoeTarget.getHealth() - life_hurt < 1 ? 1 :
                         (aoeTarget.getHealth()) - life_hurt;
            aoeTarget.setHealth(life);
            damageboost += life_hurt;
        }
        damage += damageboost;
        return damage;
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        //cannot boost damage, so just do range HP set
        if (null == context.getLivingTarget())
            return;
        Level level = context.getLivingTarget().level();
        for (LivingEntity aoeTarget : level.getEntitiesOfClass(LivingEntity.class,
                                                               context.getAttacker().getBoundingBox().inflate(glacialRiverRange.get(), 0.25D, 5))) {
            if (aoeTarget.isAlliedTo(context.getAttacker()))
                continue;
            float life_hurt = (float) (aoeTarget.getMaxHealth() * glacialRiverPortion.get());
            float life = !glacialRiverKillPlayer.get() && aoeTarget instanceof Player && aoeTarget.getHealth() - life_hurt < 1 ? 1 :
                         (aoeTarget.getHealth()) - life_hurt;
            aoeTarget.setHealth(life);
        }
    }
}
