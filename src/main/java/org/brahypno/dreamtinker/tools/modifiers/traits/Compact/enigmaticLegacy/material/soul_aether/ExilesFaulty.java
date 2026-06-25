package org.brahypno.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.soul_aether;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.brahypno.dreamtinker.common.DreamtinkerEffects;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.CursedRingBound;
import org.brahypno.dreamtinker.utils.CompactUtils.EnigmaticLegacyCompact;
import org.brahypno.esotericismtinker.utils.ETHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

import static org.brahypno.dreamtinker.config.DreamtinkerConfig.ExilesFaultyAbsorbHPPercentage;
import static org.brahypno.dreamtinker.config.DreamtinkerConfig.ExilesFaultyCurseHPPercentage;

public class ExilesFaulty extends Modifier implements ProjectileHitModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook, ModifierRemovalHook, ValidateModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.REMOVE,
                            ModifierHooks.VALIDATE);
        hookBuilder.addModule(new ModifierTraitModule(DreamtinkerModifiers.cursed_ring_bound.getId(), 1, true));
        super.registerHooks(hookBuilder);
    }

    @Override
    public Component onRemoved(IToolStackView tool, Modifier modifier) {
        tool.getPersistentData().remove(CursedRingBound.TAG_DEEP_CURSE);
        return null;
    }

    @Override
    public Component validate(IToolStackView tool, ModifierEntry modifier) {
        tool.getPersistentData().putBoolean(CursedRingBound.TAG_DEEP_CURSE, true);
        return null;
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = ETHelper.getLivingTarget(context.getTarget());
        LivingEntity attacker = context.getAttacker();
        if (null != target && !target.level().isClientSide)
            curse_faulty(attacker, target, damage);

        return knockback;
    }

    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity attacker = context.getAttacker();
        if (!attacker.level().isClientSide)
            if (attacker.getAbsorptionAmount() < attacker.getMaxHealth())
                attacker.setAbsorptionAmount(
                        (float) Math.min(attacker.getAbsorptionAmount() + damageDealt * ExilesFaultyAbsorbHPPercentage.get(), attacker.getMaxHealth()));
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (null != attacker && !attacker.level().isClientSide){
            float data = (float) (projectile.getDeltaMovement().length() * (projectile instanceof AbstractArrow arrow ? arrow.getBaseDamage() : 1));
            float regain = (float) (data * ExilesFaultyAbsorbHPPercentage.get());
            curse_faulty(attacker, target, regain);
        }
        return false;
    }

    private void curse_faulty(LivingEntity attacker, LivingEntity target, float damage) {
        if (attacker.getAbsorptionAmount() < attacker.getMaxHealth())
            attacker.setAbsorptionAmount(
                    Math.min(attacker.getAbsorptionAmount() + damage, attacker.getMaxHealth()));
        if (attacker instanceof Player player){
            float cursesPercentage = (float) (EnigmaticLegacyCompact.getCurseAmount(player) * ExilesFaultyCurseHPPercentage.get());
            if (target != null){
                float targetHealth = 1 < cursesPercentage ? 1 : target.getMaxHealth() * (1 - cursesPercentage);
                if (targetHealth < target.getHealth())
                    target.setHealth(targetHealth);
                target.removeEffect(DreamtinkerEffects.cursed.get());
                target.addEffect(new MobEffectInstance(DreamtinkerEffects.cursed.get(), 100, 0, false, false, false));
            }
            float attackerHealth = 1 < cursesPercentage ? 1 : attacker.getMaxHealth() * (1 - cursesPercentage);
            if (attackerHealth < attacker.getHealth())
                attacker.setHealth(attackerHealth);
        }
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        beforeMeleeHit(tool, modifier, context, damage, 0, 0);
    }
}
