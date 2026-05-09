package org.dreamtinker.dreamtinker.tools.modifiers.traits.harvest;

import org.dreamtinker.dreamtinker.library.modifiers.base.baseinterface.MeleeInterface;
import org.dreamtinker.dreamtinker.utils.DTDeathLoots;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ForceDrop extends Modifier implements MeleeInterface {
    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (null != context.getLivingTarget()){
            DTDeathLoots.invokeDropAllDeathLoot(context.getLivingTarget(), context.makeDamageSource());
        }
    }

}
