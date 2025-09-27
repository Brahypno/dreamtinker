package org.dreamtinker.dreamtinker.tools.modifiers.traits.Compact.enigmaticLegacy.material.soul_aether;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import org.dreamtinker.dreamtinker.common.DreamtinkerEffects;
import org.dreamtinker.dreamtinker.library.modifiers.base.baseclass.BattleModifier;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.ExilesFaultyAbsorbHPPercentage;
import static org.dreamtinker.dreamtinker.config.DreamtinkerConfig.ExilesFaultyCurseHPPercentage;

public class exiles_faulty extends BattleModifier {
    @Override
    public void addTraits(IToolContext var1, ModifierEntry var2, TraitBuilder var3, boolean var4) {
        if (var4 && var1.getModifierLevel(DreamtinkerModifiers.cursed_ring_bound.getId()) < 20)
            var3.add(DreamtinkerModifiers.cursed_ring_bound.getId(), 20);
    }

    @Override
    public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
        LivingEntity target = context.getLivingTarget();
        LivingEntity attacker = context.getAttacker();
        if (null != target && !target.level().isClientSide){
            target.removeEffect(DreamtinkerEffects.cursed.get());
            target.addEffect(new MobEffectInstance(DreamtinkerEffects.cursed.get(), 100, 0, false, false, false));
            if (attacker instanceof Player player){
                float cursesPercentage = (float) (SuperpositionHandler.getCurseAmount(player) * ExilesFaultyCurseHPPercentage.get());
                float targetHealth = 1 < cursesPercentage ? 1 : target.getMaxHealth() * (1 - cursesPercentage);
                if (targetHealth < target.getHealth())
                    target.setHealth(targetHealth);
                float attackerHealth = 1 < cursesPercentage ? 1 : attacker.getMaxHealth() * (1 - cursesPercentage);
                if (attackerHealth < attacker.getHealth())
                    attacker.setHealth(attackerHealth);
            }
        }
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
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
        if (null != attacker && !attacker.level().isClientSide){
            float data = (float) (projectile.getDeltaMovement().length() * (projectile instanceof AbstractArrow arrow ? arrow.getBaseDamage() : 1));
            float regain = (float) (data * ExilesFaultyAbsorbHPPercentage.get());
            if (attacker.getAbsorptionAmount() < attacker.getMaxHealth())
                attacker.setAbsorptionAmount(
                        Math.min(attacker.getAbsorptionAmount() + regain, attacker.getMaxHealth()));
            if (attacker instanceof Player player){
                float cursesPercentage = (float) (SuperpositionHandler.getCurseAmount(player) * ExilesFaultyCurseHPPercentage.get());
                if (target != null){
                    float targetHealth = 1 < cursesPercentage ? 1 : target.getMaxHealth() * (1 - cursesPercentage);
                    if (targetHealth < target.getHealth())
                        target.setHealth(targetHealth);
                }
                float attackerHealth = 1 < cursesPercentage ? 1 : attacker.getMaxHealth() * (1 - cursesPercentage);
                if (attackerHealth < attacker.getHealth())
                    attacker.setHealth(attackerHealth);
            }
        }
        if (null != target && !target.level().isClientSide){
            target.removeEffect(DreamtinkerEffects.cursed.get());
            target.addEffect(new MobEffectInstance(DreamtinkerEffects.cursed.get(), 100, 0, false, false, false));

        }
        return false;
    }


}
