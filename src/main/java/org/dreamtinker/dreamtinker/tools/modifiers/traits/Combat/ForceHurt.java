package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.utils.DamageProbe;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ForceHurt extends Modifier implements MeleeHitModifierHook, MonsterMeleeHitModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        DamageProbe.Result result = DamageProbe.finalDamageMethod(context.getTarget(), context.makeDamageSource(), 400);
        Dreamtinker.LOGGER.debug(result.debugText());
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        failedMeleeHit(tool, modifier, context, damage);
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        failedMeleeHit(tool, modifier, context, damage);
    }
}
