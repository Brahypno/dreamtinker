package org.dreamtinker.dreamtinker.tools.modifiers.traits.armors;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.dreamtinker.dreamtinker.library.modifiers.DreamtinkerHook;
import org.dreamtinker.dreamtinker.library.modifiers.hook.ProjectileHurtHook;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

import static org.dreamtinker.dreamtinker.config.DreamtinkerCachedConfig.homunculusLifeCurseMaxEffectLevel;

public class HomunculusLifeCurse extends Modifier implements MeleeDamageModifierHook, ProjectileHurtHook {
    private static float multiplyDamage(LivingEntity attacker, float amount, int level) {
        float multiplier = (1 - attacker.getHealth() / attacker.getMaxHealth())
                           * Math.min(homunculusLifeCurseMaxEffectLevel.get() + 1, level + 1);
        return amount * multiplier;
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, DreamtinkerHook.PROJECTILE_HURT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return multiplyDamage(context.getAttacker(), damage, modifier.getLevel());
    }

    @Override
    public float modifyProjectileHurt(
            ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile,
            DamageSource source, @Nullable LivingEntity attacker, LivingEntity target, float amount) {
        return attacker == null ? amount : multiplyDamage(attacker, amount, modifier.getLevel());
    }
}
