package org.brahypno.dreamtinker.tools.modifiers.traits.material.ruin_wheel;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.Tags;
import org.brahypno.dreamtinker.common.DreamtinkerDamageTypes;
import org.brahypno.dreamtinker.tools.modifiers.traits.Combat.GoliathDamage;
import org.brahypno.dreamtinker.utils.DamageProbe;
import org.brahypno.esotericismtinker.utils.ETHelper;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.RarityModule;
import slimeknights.tconstruct.library.modifiers.modules.build.VolatileFlagModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.IndestructibleItemEntity;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;


public class DoomTrack extends Modifier implements ProjectileHitModifierHook, MeleeHitModifierHook, MonsterMeleeHitModifierHook {
    private static final DustParticleOptions RUIN_GOLD_DUST =
            new DustParticleOptions(new Vector3f(0.95F, 0.58F, 0.16F), 0.85F);
    private static final DustParticleOptions RUIN_RED_DUST =
            new DustParticleOptions(new Vector3f(0.72F, 0.16F, 0.05F), 0.75F);

    public static float proofByResistanceMultiplier(
            LivingEntity attacker,
            LivingEntity target,
            DamageSource source,
            int level,
            boolean isRanged
    ) {
        level = Math.max(1, level);

        float armorProof = armorProof(target, level);
        float toughnessProof = toughnessProof(target, level);
        float sizeProof = sizeProof(attacker, target, level);
        float resistanceProof = resistanceProof(target, level);
        float shieldProof = shieldProof(target, level, isRanged);
        float bossProof = bossProof(target, level);

        return 1.0F + armorProof
               + toughnessProof
               + sizeProof
               + resistanceProof
               + shieldProof
               + bossProof;
    }

    private static float armorProof(LivingEntity target, int level) {
        return (float) (Math.sqrt(ETHelper.getPositiveAttributeBonus(target, Attributes.ARMOR)) * (0.70F + 0.15F * level));
    }

    private static float toughnessProof(LivingEntity target, int level) {
        return (float) (Math.sqrt(ETHelper.getPositiveAttributeBonus(target, Attributes.ARMOR_TOUGHNESS)) * (0.45F + 0.15F * level));
    }

    private static float sizeProof(LivingEntity attacker, LivingEntity target, int level) {
        float boost = GoliathDamage.goliathPercentage(attacker, target);

        return boost * (1.00F + 0.20F * level);
    }

    private static float resistanceProof(LivingEntity target, int level) {
        MobEffectInstance resistance = target.getEffect(MobEffects.DAMAGE_RESISTANCE);
        if (resistance == null){
            return 0.0F;
        }

        int amp = resistance.getAmplifier(); // Resistance I = 0
        return 0.65F + 0.30F * (amp + 1) + 0.10F * level;
    }

    private static float shieldProof(LivingEntity target, int level, boolean isRanged) {
        if (!target.isBlocking()){
            return 0.0F;
        }
        float proof = 0.65F + 0.15F * level;

        // 远程膛灭更适合惩罚盾牌；近战减半
        return isRanged ? proof : proof * 0.5F;
    }

    private static float bossProof(LivingEntity target, int level) {
        return target.getType().is(Tags.EntityTypes.BOSSES) ? (0.15F + 0.05F * level) : 0.0F;
    }

    public static void spawnOrdainedRuinFx(ServerLevel level, Entity target, int power) {
        double x = target.getX();
        double y = target.getY() + target.getBbHeight() * 0.55D;
        double z = target.getZ();

        int goldCount = 10 + power * 2;
        int redCount = 6 + power;

        level.sendParticles(
                RUIN_GOLD_DUST,
                x, y, z,
                goldCount,
                target.getBbWidth() * 0.35D,
                target.getBbHeight() * 0.25D,
                target.getBbWidth() * 0.35D,
                0.015D
        );

        level.sendParticles(
                RUIN_RED_DUST,
                x, y, z,
                redCount,
                target.getBbWidth() * 0.25D,
                target.getBbHeight() * 0.18D,
                target.getBbWidth() * 0.25D,
                0.01D
        );

        level.sendParticles(
                ParticleTypes.SMOKE,
                x, y, z,
                4 + power,
                target.getBbWidth() * 0.3D,
                target.getBbHeight() * 0.2D,
                target.getBbWidth() * 0.3D,
                0.015D
        );

        level.playSound(
                null,
                target.blockPosition(),
                SoundEvents.BLAZE_SHOOT,
                SoundSource.PLAYERS,
                0.45F,
                0.55F + level.random.nextFloat() * 0.08F
        );

        level.playSound(
                null,
                target.blockPosition(),
                SoundEvents.ANVIL_LAND,
                SoundSource.PLAYERS,
                0.22F,
                0.55F
        );
    }

    public boolean isNoLevels() {return false;}

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT);
        hookBuilder.addModule(new VolatileFlagModule(IndestructibleItemEntity.INDESTRUCTIBLE_ENTITY));
        hookBuilder.addModule(new RarityModule(Rarity.RARE));
        super.registerHooks(hookBuilder);
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        deal_damage(tool, modifier, context.getTarget(), context.getAttacker(), context.makeDamageSource(), damageDealt, null);

    }

    @Override
    public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
        afterMeleeHit(tool, modifier, context, damageAttempted);
    }

    @Override
    public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
        deal_damage(null, modifier, target, attacker, projectile.damageSources().mobProjectile(projectile, attacker), 15 * modifier.getLevel(), projectile);
        return false;
    }


    private void deal_damage(IToolStackView tool, ModifierEntry modifier, @Nullable Entity target, @Nullable LivingEntity attacker, DamageSource source, float damageDealt, @Nullable Projectile projectile) {
        if (target == null || attacker == null){
            return;
        }

        LivingEntity victim = ETHelper.getLivingTarget(target);
        if (victim == null || !victim.isAlive()){
            return;
        }

        DamageSource dmg = DreamtinkerDamageTypes.source(victim.level().registryAccess(), DreamtinkerDamageTypes.ruin_wheel, source);
        if (!victim.isInvulnerableTo(dmg)){
            float theoreticalDamage = projectile != null ? ETModifierCheck.getDamage(projectile) :
                                      Math.max(0.5f, ETModifierCheck.getMeleeDamage(attacker, target, tool, true));
            theoreticalDamage = Math.max(theoreticalDamage, damageDealt);
            theoreticalDamage *=
                    proofByResistanceMultiplier(attacker, victim, dmg, modifier.getLevel(), projectile != null);
            victim.invulnerableTime = 0;
            DamageProbe.damageHandler(target, dmg, theoreticalDamage);
            spawnOrdainedRuinFx((ServerLevel) victim.level(), victim, modifier.getLevel());
        }
    }

    @Override
    public void onMonsterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage) {
        afterMeleeHit(tool, modifier, context, damage);
    }
}
