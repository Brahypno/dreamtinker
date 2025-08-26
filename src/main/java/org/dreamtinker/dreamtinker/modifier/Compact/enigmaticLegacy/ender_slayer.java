package org.dreamtinker.dreamtinker.modifier.Compact.enigmaticLegacy;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.EnderSlayer;
import com.aizistral.enigmaticlegacy.objects.RegisteredMeleeAttack;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ender_slayer extends BattleModifier {

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        if (context.getAttacker() instanceof Player player && SuperpositionHandler.isTheCursedOne(player))
            if (EnigmaticItems.ENDER_SLAYER.isEndDweller(context.getLivingTarget())){
                knockback += EnderSlayer.endKnockbackBonus.getValue().asModifier(false);
            }
        return knockback;
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        if (context.getAttacker() instanceof Player player && SuperpositionHandler.isTheCursedOne(player)){
            if (EnigmaticItems.ENDER_SLAYER.isEndDweller((LivingEntity) context.getTarget())){
                if (player.level().dimension().equals(EnigmaticLegacy.PROXY.getEndKey())){
                    if (context.getTarget() instanceof EnderMan
                        && RegisteredMeleeAttack.getRegisteredAttackStregth(player) >= 1F){
                        damage = (damage + 100F) * 10F;
                    }
                    context.getTarget().getPersistentData().putBoolean("EnderSlayerVictim", true);
                }
                damage += damage * EnderSlayer.endDamageBonus.getValue().asModifier(false);
            }
        }
        return damage;
    }
}
