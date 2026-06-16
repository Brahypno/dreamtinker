package org.brahypno.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.brahypno.dreamtinker.common.DreamtinkerDamageTypes;
import org.brahypno.esotericismtinker.library.modifiers.EsotericismTinkerHook;
import org.brahypno.esotericismtinker.library.modifiers.hook.ProjectileHurtHook;
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
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

import static net.minecraft.tags.DamageTypeTags.BYPASSES_ENCHANTMENTS;

public class ArcaneHit extends Modifier implements MeleeDamageModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook, ProjectileHurtHook {
    private static final int ALLOWED_EXTRA_TIMES = 1;
    private static final ThreadLocal<Integer> EXTRA_ATTACK_DEPTH = ThreadLocal.withInitial(() -> 0);

    private static float conversionRatio(ModifierEntry modifier) {
        return 0.1F * modifier.getLevel();
    }

    private static boolean dealConvertedDamage(Entity target, DamageSource parentSource, float extra) {
        int depth = EXTRA_ATTACK_DEPTH.get();
        if (target.level().isClientSide || extra < 0.1F || depth >= ALLOWED_EXTRA_TIMES){
            return false;
        }
        int invulnerableTime = target.invulnerableTime;
        try {
            EXTRA_ATTACK_DEPTH.set(depth + 1);
            target.invulnerableTime = 0;
            target.hurt(DreamtinkerDamageTypes.source(target.level().registryAccess(), DreamtinkerDamageTypes.arcane_damage, parentSource), extra);
            return true;
        }
        finally {
            target.invulnerableTime = invulnerableTime;
            EXTRA_ATTACK_DEPTH.set(depth);
        }
    }

    public int getPriority() {
        return -10;
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, ModifierHooks.MELEE_HIT,
                            ModifierHooks.MONSTER_MELEE_HIT, EsotericismTinkerHook.PROJECTILE_HURT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        return Math.max(1.0F, damage - damage * conversionRatio(modifier));
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        Entity target = context.getTarget();
        float ratio = conversionRatio(modifier);
        if (ratio > 0 && ratio < 1.0F){
            dealConvertedDamage(target, context.makeDamageSource(), damageDealt * ratio / (1.0F - ratio));
        }
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        afterMeleeHit(tool, modifier, context, damage);
    }

    @Override
    public float modifyProjectileHurt(
            ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile,
            DamageSource source, @Nullable LivingEntity attacker, LivingEntity target, float amount) {
        if (source.is(BYPASSES_ENCHANTMENTS)){
            return amount;
        }
        float extra = amount * conversionRatio(modifier);
        return dealConvertedDamage(target, source, extra) ? Math.max(1.0F, amount - extra) : amount;
    }
}
