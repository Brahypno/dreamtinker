package org.brahypno.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.brahypno.dreamtinker.common.DreamtinkerDamageTypes;
import org.brahypno.dreamtinker.library.client.utils.model.RainbowTextUtil;
import org.brahypno.dreamtinker.utils.DamageProbe;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class RainbowLights extends Modifier implements MeleeDamageModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook {
    Component myName = null;

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return damage * modifier.getEffectiveLevel() / 7.0f;
    }

    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        float Theoretical_damage = Math.max(0.5f, ETModifierCheck.getMeleeDamage(context.getAttacker(), context.getTarget(), tool, false));
        Theoretical_damage = Math.max(Theoretical_damage, damageDealt);
        Entity victim = context.getTarget();
        if (!victim.level().isClientSide){
            int inv = victim.invulnerableTime;
            for (int i = 0; i < 9 && victim.isAlive(); i++) {
                DamageSource source =
                        DreamtinkerDamageTypes.randomSourceNotSame(victim.level().registryAccess(), context.makeDamageSource(), victim.level().random);
                victim.invulnerableTime = 0;
                if (victim instanceof LivingEntity le)
                    le.hurtDuration = 0;
                DamageProbe.damageHandler(victim, source, Theoretical_damage);
            }
            victim.invulnerableTime = inv;
        }
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        afterMeleeHit(tool, modifier, context, damage);
    }

    @Override
    public @NotNull Component getDisplayName(int level) {
        if (myName == null){
            myName = RainbowTextUtil.rainbowKeyBand(getTranslationKey(), 0.05f, 0.70f);
        }
        return myName;
    }
}
