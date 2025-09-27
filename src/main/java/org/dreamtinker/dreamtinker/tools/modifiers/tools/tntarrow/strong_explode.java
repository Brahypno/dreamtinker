package org.dreamtinker.dreamtinker.tools.modifiers.tools.tntarrow;

import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.StrongExplodeDamageBoost;

public class strong_explode extends BattleModifier {
    @Override
    public float onGetMeleeDamage(@NotNull IToolStackView tool, @NotNull ModifierEntry modifier, @NotNull ToolAttackContext context, float baseDamage, float damage) {
        return (float) Math.pow(damage, (tool.getModifierLevel(this) + 1) * StrongExplodeDamageBoost.get());
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE / 2;
    }

    @Override
    public boolean isNoLevels() {return false;}
}
