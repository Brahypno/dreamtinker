package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.EnderSlayer;
import com.aizistral.enigmaticlegacy.objects.RegisteredMeleeAttack;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.List;

public class ELEnderSlayer extends BattleModifier {

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        float knockbackPower = 1F;
        if (context.getAttacker() instanceof Player player && SuperpositionHandler.isTheCursedOne(player))
            if (EnigmaticItems.ENDER_SLAYER.isEndDweller(context.getLivingTarget())){
                knockbackPower += EnderSlayer.endKnockbackBonus.getValue().asModifier(false) * modifier.getEffectiveLevel();
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
                damage += damage * EnderSlayer.endDamageBonus.getValue().asModifier(false) * modifier.getEffectiveLevel();
            }
        }
        return damage;
    }

    @Override
    public void addTraits(IToolContext var1, ModifierEntry var2, TraitBuilder var3, boolean var4) {
        if (var4 && 0 == var1.getModifierLevel(DreamtinkerModifiers.cursed_ring_bound.getId()))
            var3.add(DreamtinkerModifiers.cursed_ring_bound.getId(), 1);
    }

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        final String fulfill = this.getTranslationKey() + "_fulfill";
        return Arrays.asList(Component.translatable(this.getTranslationKey() + ".flavor").withStyle(ChatFormatting.ITALIC),
                             Component.translatable(fulfill + ".description").withStyle(ChatFormatting.GRAY));
    }
}
