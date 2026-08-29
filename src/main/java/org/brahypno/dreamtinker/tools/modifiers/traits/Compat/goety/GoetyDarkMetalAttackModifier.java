package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.goety;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.SEHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Dark-metal behavior for melee and harvest tools.
 */
public class GoetyDarkMetalAttackModifier extends Modifier implements MeleeHitModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void afterMeleeHit(
            IToolStackView tool, ModifierEntry modifier, ToolAttackContext context,
            float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target == null || player == null || player.level().isClientSide){
            return;
        }

        target.addEffect(new MobEffectInstance(GoetyEffects.WANE.get(), 40 + player.getRandom().nextInt(21)));

        int soulMultiplier = SEHelper.SoulMultiply(player, context.makeDamageSource());
        if (tool.hasTag(TinkerTags.Items.SCYTHES)){
            if (SEHelper.getSoulGiven(target) > 0){
                SEHelper.increaseSouls(player, ItemConfig.DarkScytheSouls.get() * soulMultiplier);
            }
            return;
        }

        if (tool.hasTag(TinkerTags.Items.SMALL_TOOLS) && tool.hasTag(TinkerTags.Items.SWORD)){
            player.heal(damageDealt * 0.05f * soulMultiplier);
            return;
        }

        // Goety has already granted the normal kill reward; add only the difference needed to reach 1.5x.
        if (target.isDeadOrDying()){
            int baseSouls = SEHelper.getSoulGiven(target);
            int normal = Mth.floor(baseSouls * (float) soulMultiplier);
            int boosted = Mth.floor(baseSouls * soulMultiplier * 1.5f);
            int configMultiplier = Mth.clamp(MainConfig.SoulTakenMultiplier.get(), 1, Integer.MAX_VALUE);
            int extra = (boosted - normal) * configMultiplier;
            if (extra > 0){
                SEHelper.increaseSouls(player, extra);
            }
        }
    }
}
