package org.dreamtinker.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.dreamtinker.dreamtinker.common.DreamtinkerDamageTypes;
import org.dreamtinker.dreamtinker.library.modifiers.DreamtinkerHook;
import org.dreamtinker.dreamtinker.library.modifiers.hook.ProjectileHurtHook;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

public class WhyICry extends Modifier implements MeleeHitModifierHook, MonsterMeleeHitModifierHook, ProjectileHurtHook {
    private static final int ALLOWED_EXTRA_TIMES = 1;
    private static final ThreadLocal<Integer> EXTRA_ATTACK_DEPTH = ThreadLocal.withInitial(() -> 0);

    private static void applyExtraDamage(LivingEntity attacker, Entity target, DamageSource parentSource, float amount, int level) {
        int depth = EXTRA_ATTACK_DEPTH.get();
        if (target.level().isClientSide || amount <= 0 || level <= 0 || depth >= ALLOWED_EXTRA_TIMES){
            return;
        }

        float extra = amount * 0.05F * level;
        if (extra < 0.1F){
            return;
        }

        DamageSource source = DreamtinkerDamageTypes.source(target.level().registryAccess(), DreamtinkerDamageTypes.NULL_VOID, parentSource);
        try {
            EXTRA_ATTACK_DEPTH.set(depth + 1);
            target.hurt(source, extra);
            if (target.level().random.nextFloat() < 0.1F){
                attacker.hurt(source, extra);
            }
        }
        finally {
            EXTRA_ATTACK_DEPTH.set(depth);
        }
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, DreamtinkerHook.PROJECTILE_HURT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        applyExtraDamage(context.getAttacker(), context.getTarget(), context.makeDamageSource(), damageDealt, modifier.getLevel());
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        afterMeleeHit(tool, modifier, context, damage);
    }

    @Override
    public float modifyProjectileHurt(
            ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile,
            DamageSource source, @Nullable LivingEntity attacker, LivingEntity target, float amount) {
        if (attacker != null){
            applyExtraDamage(attacker, target, source, amount, modifier.getLevel());
        }
        return amount;
    }
}
