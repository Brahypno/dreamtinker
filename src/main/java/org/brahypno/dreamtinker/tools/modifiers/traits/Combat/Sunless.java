package org.brahypno.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.brahypno.dreamtinker.common.event.DaylostJudgmentEvents;
import org.brahypno.esotericismtinker.utils.ETHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.DamageDealtModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

public class Sunless extends Modifier implements ModifierRemovalHook, OnAttackedModifierHook, DamageDealtModifierHook,
        MeleeHitModifierHook, MonsterMeleeHitModifierHook, ProjectileHitModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.REMOVE, ModifierHooks.ON_ATTACKED, ModifierHooks.DAMAGE_DEALT,
                            ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.PROJECTILE_HIT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public Component onRemoved(IToolStackView tool, Modifier modifier) {
        return Component.translatable(this.getTranslationKey() + ".salvage");
    }

    @Override
    public void onAttacked(
            IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType,
            DamageSource source, float amount, boolean isDirectDamage) {
        if (source.getEntity() instanceof LivingEntity attacker){
            DaylostJudgmentEvents.applyDaylostFromSunless(attacker, context.getEntity(), modifier.getLevel());
        }
    }

    @Override
    public void onDamageDealt(
            IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType,
            LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
        DaylostJudgmentEvents.applyDaylostFromSunless(target, context.getEntity(), modifier.getLevel());
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        DaylostJudgmentEvents.applyDaylostFromSunless(
                ETHelper.getLivingTarget(context.getTarget()), context.getAttacker(), modifier.getLevel());
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        DaylostJudgmentEvents.applyDaylostFromSunless(
                ETHelper.getLivingTarget(context.getTarget()), context.getAttacker(), modifier.getLevel());
    }

    @Override
    public boolean onProjectileHitEntity(
            ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier,
            Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker,
            @Nullable LivingEntity target, boolean notBlocked) {
        if (notBlocked && attacker != null){
            DaylostJudgmentEvents.applyDaylostFromSunless(target, attacker, modifier.getLevel());
        }
        return false;
    }
}
