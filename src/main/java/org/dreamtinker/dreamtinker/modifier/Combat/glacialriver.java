package org.dreamtinker.dreamtinker.modifier.Combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Objects;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.*;

public class glacialriver extends BattleModifier {
    public glacialriver(){}

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        Level level= Objects.requireNonNull(context.getLivingTarget()).level;
        float damageboost=0;
        for (LivingEntity aoeTarget : level.getEntitiesOfClass(LivingEntity.class, context.getAttacker().getBoundingBox().inflate(glaciriverRange.get(), 0.25D, 5))) {
            float lifehurt=(float) (aoeTarget.getMaxHealth()*glaciriverPortion.get());
            float life= 0==glaciriverKillPlayer.get() && aoeTarget instanceof Player && aoeTarget.getHealth()-lifehurt<1?1:(aoeTarget.getHealth())-lifehurt;
            aoeTarget.setHealth(life);
            damageboost+=lifehurt;
        }
        damage+=damageboost;
        return damage;
    }
    public boolean isNoLevels() {
        return true;
    }

}
