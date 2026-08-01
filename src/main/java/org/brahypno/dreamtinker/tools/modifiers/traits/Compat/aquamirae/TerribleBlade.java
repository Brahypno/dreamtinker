package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.aquamirae;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/**
 * Melee behavior modeled after Aquamirae's terrible blade.
 */
public class TerribleBlade extends Modifier implements MeleeHitModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity attacker = context.getAttacker();
        if (!(context.getTarget() instanceof LivingEntity target)
            || attacker.level().isClientSide){
            return;
        }

        target.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 0));
        if (target.isDeadOrDying() && target.hasEffect(MobEffects.POISON)
            && TerribleArmor.getEquippedLevel(attacker) >= 4){
            attacker.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 1));
        }
    }

    @Override
    public @NotNull Component getDisplayName(int level) {
        return super.getDisplayName();
    }
}
