package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class acheron extends BattleModifier {
    @Override
    public Component onModifierRemoved(IToolStackView tool, Modifier modifier) {
        return Component.translatable(this.getTranslationKey() + ".salvage");
    }

    @Override
    public Component validate(IToolStackView tool, ModifierEntry modifier) {
        return Component.translatable(this.getTranslationKey() + ".salvage");
    }
    
    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        Player attacker = context.getPlayerAttacker();
        if (attacker != null && !attacker.level().isClientSide){
            attacker.setAbsorptionAmount(Math.min(attacker.getAbsorptionAmount() + damageDealt, attacker.getMaxHealth() * 5));
        }
    }
}
