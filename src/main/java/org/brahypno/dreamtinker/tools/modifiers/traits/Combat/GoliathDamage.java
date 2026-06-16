package org.brahypno.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.brahypno.dreamtinker.utils.DTHelper;
import org.brahypno.esotericismtinker.library.modifiers.EsotericismTinkerHook;
import org.brahypno.esotericismtinker.library.modifiers.hook.ProjectileHurtHook;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;

public class GoliathDamage extends NoLevelsModifier implements MeleeDamageModifierHook, ProjectileHurtHook {
    public static float goliathPercentage(LivingEntity attacker, Entity target) {
        double attacker_volume = DTHelper.getMultipartVolume(attacker);
        double target_volume = DTHelper.getMultipartVolume(target);
        double multi = 1.0 + 0.35D * Math.log((target_volume / attacker_volume));
        return (float) Math.max(multi, 0.70D);
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, EsotericismTinkerHook.PROJECTILE_HURT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return damage * goliathPercentage(context.getAttacker(), context.getTarget()) * tool.getMultiplier(ToolStats.ATTACK_DAMAGE);
    }

    @Override
    public float modifyProjectileHurt(
            ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile,
            DamageSource source, @Nullable LivingEntity attacker, LivingEntity target, float amount) {
        return attacker == null ? amount : amount * goliathPercentage(attacker, target) * modifier.getLevel();
    }
}
