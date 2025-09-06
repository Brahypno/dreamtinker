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
import org.dreamtinker.dreamtinker.register.DreamtinkerModifers;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ender_slayer extends BattleModifier {

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        float knockbackPower = 1F;
        if (context.getAttacker() instanceof Player player && SuperpositionHandler.isTheCursedOne(player))
            if (EnigmaticItems.ENDER_SLAYER.isEndDweller(context.getLivingTarget())){
                knockbackPower += EnderSlayer.endKnockbackBonus.getValue().asModifier(false);
            }
        return knockback * knockbackPower;
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

    @Override
    public void addTraits(IToolContext var1, ModifierEntry var2, TraitBuilder var3, boolean var4) {
        if (var4 && 0 == var1.getModifierLevel(DreamtinkerModifers.cursed_ring_bound.getId()))
            var3.add(DreamtinkerModifers.cursed_ring_bound.getId(), 1);
    }
}
