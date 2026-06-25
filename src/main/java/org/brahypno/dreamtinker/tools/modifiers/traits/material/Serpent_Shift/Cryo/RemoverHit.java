package org.brahypno.dreamtinker.tools.modifiers.traits.material.Serpent_Shift.Cryo;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.brahypno.dreamtinker.utils.LootHelper.DTLoots;
import org.brahypno.esotericismtinker.utils.ETHelper;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

public class RemoverHit extends Modifier implements MeleeHitModifierHook, ProjectileHitModifierHook {
    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT);
        super.registerHooks(hookBuilder);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        failedMeleeHit(tool, modifier, context, damageDealt);
    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        if (!context.getTarget().isRemoved()){
            LivingEntity target = ETHelper.getLivingTarget(context.getTarget());
            if (null != target && target.isAlive() && !(target instanceof Player)){
                LivingEntity attacker = context.getAttacker();
                double attack = Math.max(attacker.getMaxHealth(), attacker.getAttributeValue(Attributes.ATTACK_DAMAGE));
                if (target.getHealth() <= attack){
                    DTLoots.dropAllDeathLootVanilla(target, context.makeDamageSource());
                    target.remove(Entity.RemovalReason.DISCARDED);
                }else
                    target.setHealth((float) (target.getHealth() - attack));
            }
        }
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (null != attacker && null != target){
            if (target.isAlive() && !(target instanceof Player)){
                double attack = Math.max(attacker.getMaxHealth(), attacker.getAttributeValue(Attributes.ATTACK_DAMAGE));
                attack = Math.max(attack, ETModifierCheck.getDamage(projectile));
                if (target.getHealth() <= attack){
                    DTLoots.dropAllDeathLootVanilla(target, target.level().damageSources().mobProjectile(projectile, attacker));
                    target.remove(Entity.RemovalReason.DISCARDED);
                }else
                    target.setHealth((float) (target.getHealth() - attack));
            }
        }
        return false;
    }

}
