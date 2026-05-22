package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.OathSteel;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileShootModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;
import java.util.List;

import static org.dreamtinker.dreamtinker.tools.modifiers.events.OathGuardPaleSteelEvents.*;

public class ForOath extends Modifier implements ProjectileHitModifierHook, ProjectileLaunchModifierHook, ProjectileShootModifierHook, MeleeDamageModifierHook, MeleeHitModifierHook {
    public boolean isNoLevels() {return false;}

    private static void grantGuardianAbsorption(
            ServerPlayer player,
            ServerLevel serverLevel,
            float damage,
            int modifierLevel
    ) {
        if (damage <= 0)
            return;
        List<LivingEntity> protectedTargets = serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(16.0D),
                target -> target != player
                          && target.isAlive()
                          && isGuardianProtectedTarget(player, target));
        for (LivingEntity target : protectedTargets) {
            if (target.getAbsorptionAmount() < 2 * target.getMaxHealth())
                target.setAbsorptionAmount(target.getAbsorptionAmount() + damage * 0.1f * modifierLevel);
        }
    }

    private float applyOathEvilDamageBonus(LivingEntity target, ServerPlayer player, int level, float damage) {
        float evil = getOathEvil(target, player);
        if (evil <= 0.0F)
            return damage;
        if (this.getId().equals(DreamtinkerModifiers.for_oath.getId())){

            float scale = 140.0F - 20.0F * level;
            float cap = 0.20F + 0.10F * level;
            float bonusRate = Mth.clamp(evil / scale, 0.0F, cap);
            return damage * (1.0F + bonusRate);
        }else {
            float scale = 120.0F - 15.0F * level;
            float cap = 0.35F + 0.15F * level;
            float bonusRate = Mth.clamp(evil / scale, 0.0F, cap);
            return damage * (1.0F + bonusRate);
        }


    }

    private static void consumeOathEvil(LivingEntity target, ServerPlayer player, int level) {
        float evil = getOathEvil(target, player);
        if (evil <= 0.0F)
            return;

        float consumeRate = Mth.clamp(0.35F - 0.05F * level, 0.15F, 0.35F);
        float consumed = Math.max(1.0F, evil * consumeRate);

        reduceOathEvil(target, player, consumed);
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        if (context.getAttacker() instanceof ServerPlayer player && context.getLevel() instanceof ServerLevel serverLevel){
            grantGuardianAbsorption(player, serverLevel, damage, modifier.getLevel());
        }
        return knockback;
    }

    @Override
    public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
        if (context.getAttacker() instanceof ServerPlayer player && context.getLevel() instanceof ServerLevel && context.getLivingTarget() != null){
            return applyOathEvilDamageBonus(context.getLivingTarget(), player, modifier.getLevel(), damage);
        }
        return damage;
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (context.getAttacker() instanceof ServerPlayer player && context.getLevel() instanceof ServerLevel && context.getLivingTarget() != null &&
            damageDealt > 0.0F){
            consumeOathEvil(context.getLivingTarget(), player, modifier.getLevel());
        }
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        if (notBlocked && attacker instanceof ServerPlayer player && projectile.level() instanceof ServerLevel && target != null){
            float damage = DTModifierCheck.getDamage(projectile);
            if (damage > 0.0F){
                damage = applyOathEvilDamageBonus(target, player, modifier.getLevel(), damage);
                if (projectile instanceof AbstractArrow arrow){
                    arrow.setBaseDamage(damage);
                }else if (projectile instanceof ProjectileWithPower withPower){
                    withPower.setPower(damage);
                }
                consumeOathEvil(target, player, modifier.getLevel());
            }
        }
        return false;
    }

    @Override
    public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (shooter instanceof ServerPlayer player
            && shooter.level() instanceof ServerLevel serverLevel){
            float damage = arrow != null ? (float) arrow.getBaseDamage() : DTModifierCheck.getDamage(projectile);
            grantGuardianAbsorption(player, serverLevel, damage, modifier.getLevel());
        }

    }

    @Override
    public void onProjectileShoot(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
        if (shooter instanceof ServerPlayer player
            && shooter.level() instanceof ServerLevel serverLevel){
            float damage = arrow != null ? (float) arrow.getBaseDamage() : DTModifierCheck.getDamage(projectile);
            grantGuardianAbsorption(player, serverLevel, damage, modifier.getLevel());
        }
    }


    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_SHOT, ModifierHooks.PROJECTILE_THROWN,
                            ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE, ModifierHooks.MELEE_HIT);
        super.registerHooks(hookBuilder);
    }
}
