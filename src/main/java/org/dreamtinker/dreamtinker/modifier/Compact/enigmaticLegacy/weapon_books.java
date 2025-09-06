package org.dreamtinker.dreamtinker.modifier.Compact.enigmaticLegacy;

import com.aizistral.enigmaticlegacy.config.OmniconfigHandler;
import com.aizistral.enigmaticlegacy.effects.GrowingBloodlustEffect;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.items.TheInfinitum;
import com.aizistral.enigmaticlegacy.items.TheTwist;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.dreamtinker.dreamtinker.modifier.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.List;

import static org.dreamtinker.dreamtinker.modifier.Compact.enigmaticLegacy.eldritch_pan.getBloodlust;

public class weapon_books extends BattleModifier {

    @Override
    public @NotNull Component getDisplayName(int level) {
        String book_key_1 = "item.enigmaticlegacy.the_acknowledgment";
        String book_key_2 = "item.enigmaticlegacy.the_twist";
        String book_key_3 = "item.enigmaticlegacy.the_infinitum";
        if (1 == level)
            return Component.translatable(book_key_1);
        else if (2 == level)
            return Component.translatable(book_key_2);
        else
            return Component.translatable(book_key_3);
    }

    @Override
    public @NotNull List<Component> getDescriptionList(int level) {
        if (1 == level)
            return Arrays.asList(Component.translatable("tooltip.enigmaticlegacy.theAcknowledgment1").withStyle(ChatFormatting.ITALIC),
                                 Component.translatable("tooltip.enigmaticlegacy.theAcknowledgment2").withStyle(ChatFormatting.GRAY));
        else if (2 == level)
            return Arrays.asList(Component.translatable("tooltip.enigmaticlegacy.theTwist1").withStyle(ChatFormatting.ITALIC),
                                 Component.translatable("tooltip.enigmaticlegacy.theTwist2").withStyle(ChatFormatting.GRAY));
        else
            return Arrays.asList(Component.translatable("tooltip.enigmaticlegacy.theInfinitum1").withStyle(ChatFormatting.ITALIC),
                                 Component.translatable("tooltip.enigmaticlegacy.worthyOnesOnly2").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void addTraits(IToolContext var1, ModifierEntry var2, TraitBuilder var3, boolean var4) {
        if (var4 && var2.getLevel() < 3 && 0 == var1.getModifierLevel(DreamtinkerModifers.cursed_ring_bound.getId()))
            var3.add(DreamtinkerModifers.cursed_ring_bound.getId(), 1);
        if (var4 && 3 == var2.getLevel() && var1.getModifierLevel(DreamtinkerModifers.cursed_ring_bound.getId()) < 20)
            var3.add(DreamtinkerModifers.cursed_ring_bound.getId(), 20);
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        float knockbackPower = 1F;
        if (context.getAttacker() instanceof Player player && !player.level().isClientSide){
            if (tool.getModifierLevel(this.getId()) <= 2 && SuperpositionHandler.isTheCursedOne(player)){
                if (null != context.getLivingTarget())
                    context.getLivingTarget().setSecondsOnFire(20);
            }
            if (2 == tool.getModifierLevel(this.getId()) && SuperpositionHandler.isTheCursedOne(player)){
                knockbackPower += TheTwist.knockbackBonus.getValue().asModifier(false);
            }else if (3 <= tool.getModifierLevel(this.getId()) && SuperpositionHandler.isTheWorthyOne(player)){
                knockbackPower += TheInfinitum.knockbackBonus.getValue().asModifier(false);
            }
        }
        return knockback * knockbackPower;
    }

    @Override
    public float onGetMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        if (context.getAttacker() instanceof Player player)
            if (2 == tool.getModifierLevel(this.getId())){
                if (SuperpositionHandler.isTheCursedOne(player)){
                    if (null != context.getLivingTarget() && OmniconfigHandler.isBossOrPlayer(context.getLivingTarget()))
                        damage += damage * TheTwist.bossDamageBonus.getValue().asModifier(false);

                }else
                    damage = 0;
            }else if (3 <= tool.getModifierLevel(this.getId())){
                if (SuperpositionHandler.isTheWorthyOne(player)){
                    if (null != context.getLivingTarget() && OmniconfigHandler.isBossOrPlayer(context.getLivingTarget()))
                        damage += damage * TheInfinitum.bossDamageBonus.getValue().asModifier(false);
                }else {
                    damage = 0;
                    player.addEffect(new MobEffectInstance(MobEffects.WITHER, 160, 3, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 500, 3, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, 3, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 3, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 300, 3, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 3, false, true));
                }
            }
        return damage;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (3 <= tool.getModifierLevel(this.getId()))
            if (context.getAttacker() instanceof Player player && !player.level().isClientSide){
                float lifesteal = SuperpositionHandler.isTheWorthyOne(player) ? damageDealt * 0.1F : 0;

                if (null != getBloodlust() && player.hasEffect(getBloodlust())){
                    int amplifier = 1 + player.getEffect(getBloodlust()).getAmplifier();
                    lifesteal += (float) (damageDealt * (GrowingBloodlustEffect.lifestealBoost.getValue() * amplifier));
                }

                player.heal(lifesteal);
            }
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }
}
