package org.brahypno.dreamtinker.tools.modifiers.traits.Combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.brahypno.dreamtinker.library.modifiers.DreamtinkerHook;
import org.brahypno.dreamtinker.library.modifiers.hook.ProjectileHurtHook;
import org.brahypno.dreamtinker.utils.CompactUtils.CuriosCompact;
import org.brahypno.dreamtinker.utils.DTHelper;
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

public class AsSand extends Modifier implements MeleeHitModifierHook, MonsterMeleeHitModifierHook, ProjectileHurtHook {
    private static void applyWearToTarget(LivingEntity target, int amount) {
        if (target.isDeadOrDying() || amount <= 0){
            return;
        }
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET,
                EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}) {
            ItemStack stack = target.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.isDamageableItem()){
                stack.hurtAndBreak(amount, target, entity -> entity.broadcastBreakEvent(slot));
            }
        }
        CuriosCompact.damageAllCurios(target, amount, stack -> true);
    }

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, DreamtinkerHook.PROJECTILE_HURT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = DTHelper.getLivingTarget(context.getTarget());
        if (target != null && !target.level().isClientSide){
            applyWearToTarget(target, (int) (damageDealt / 10 * modifier.getLevel()));
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
        if (!target.level().isClientSide){
            applyWearToTarget(target, (int) (amount / 10 * modifier.getLevel()));
        }
        return amount;
    }
}
