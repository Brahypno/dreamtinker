package org.dreamtinker.dreamtinker.modifier.Combat;

import net.minecraft.network.chat.Component;
import org.dreamtinker.dreamtinker.modifier.base.BaseModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import static slimeknights.tconstruct.library.tools.SlotType.UPGRADE;

public class strong_explode extends BaseModifier {
    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return damage*(tool.getModifierLevel(this)+1);
    }
    @Override
    public Component onModifierRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().addSlots(UPGRADE,1);
        return null;
    }
    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }
}
