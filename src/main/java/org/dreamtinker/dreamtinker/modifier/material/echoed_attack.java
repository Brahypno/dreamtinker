package org.dreamtinker.dreamtinker.modifier.material;

import net.minecraft.world.InteractionHand;
import org.dreamtinker.dreamtinker.modifier.base.BaseModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class echoed_attack extends BaseModifier {
    @Override
    public void  afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if(null!=context.getLivingTarget()&& 0.5<Math.random()){
            ToolAttackUtil.attackEntity(tool, context.getAttacker(), tool.getItem().equals(context.getAttacker().getMainHandItem().getItem())? InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND,context.getLivingTarget(),() -> 10, true);
        }
    }
}
