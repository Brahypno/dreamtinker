package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.malum;

import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import team.lodestar.lodestone.registry.common.LodestoneAttributeRegistry;

public class malum_magic_attack extends BattleModifier {
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        if (null != context.getLivingTarget()){
            var magicResistance = context.getLivingTarget().getAttribute(LodestoneAttributeRegistry.MAGIC_RESISTANCE.get());
            if (magicResistance != null)
                damage /= (float) Math.max(magicResistance.getValue(), 0.01f);
        }
        var magicProficiency = context.getAttacker().getAttribute(LodestoneAttributeRegistry.MAGIC_PROFICIENCY.get());
        if (magicProficiency != null)
            damage *= (float) ((magicProficiency.getValue() - 1) * modifier.getLevel() + 1);
        return damage;
    }

    @Override
    public boolean isNoLevels() {return false;}

    @Override
    public int getPriority() {
        return 200;
    }
}
