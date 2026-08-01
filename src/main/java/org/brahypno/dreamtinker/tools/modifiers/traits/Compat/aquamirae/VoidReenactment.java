package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.aquamirae;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.tools.data.ModifierIds;

import javax.annotation.Nullable;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VoidReenactment extends Modifier implements MeleeHitModifierHook, ProjectileHitModifierHook {
    /**
     * Dominant cyan from Aquamirae's ship graveyard echo texture. Common-side particle data is dedicated-server safe.
     */
    private static final DustParticleOptions ECHO_DUST =
            new DustParticleOptions(new Vector3f(0x4D / 255.0F, 0xCA / 255.0F, 0xDE / 255.0F), 0.8F);
    private static final String PENDING = "dreamtinker_void_reenactment_pending";
    private static final String DUE_TIME = "dreamtinker_void_reenactment_due";
    private static final String DAMAGE = "dreamtinker_void_reenactment_damage";
    private static final String ATTACKER = "dreamtinker_void_reenactment_attacker";

    @Override
    protected void registerHooks(ModuleHookMap.@NotNull Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT);
        hookBuilder.addModule(EnchantmentModule.builder(Enchantments.SMITE).level(2).constant());
        hookBuilder.addModule(new ModifierTraitModule(ModifierIds.smite, 2, false));
        super.registerHooks(hookBuilder);
    }

    private static float echoRatio(int level) {
        return Math.min(0.35f, 0.15f + 0.05f * level);
    }

    private static void playEchoEffect(ServerLevel level, LivingEntity target) {
        double y = target.getY() + target.getBbHeight() * 0.5D;
        double horizontalSpread = Math.max(0.15D, target.getBbWidth() * 0.35D);
        double verticalSpread = Math.max(0.2D, target.getBbHeight() * 0.25D);
        level.sendParticles(ECHO_DUST, target.getX(), y, target.getZ(),
                            6, horizontalSpread, verticalSpread, horizontalSpread, 0.015D);
        level.sendParticles(ParticleTypes.SCULK_SOUL, target.getX(), y, target.getZ(),
                            2, horizontalSpread * 0.5D, verticalSpread * 0.5D, horizontalSpread * 0.5D, 0.01D);
        level.playSound(null, target.getX(), y, target.getZ(), SoundEvents.SCULK_CATALYST_BLOOM,
                        SoundSource.PLAYERS, 0.4F, 0.75F + level.random.nextFloat() * 0.1F);
    }

    private static void schedule(LivingEntity target, @Nullable LivingEntity attacker, float damage, int level) {
        if (target.level().isClientSide || damage <= 0){
            return;
        }
        CompoundTag data = target.getPersistentData();
        if (data.getBoolean(PENDING)){
            return;
        }
        data.putBoolean(PENDING, true);
        data.putLong(DUE_TIME, target.level().getGameTime() + 10);
        data.putFloat(DAMAGE, damage * echoRatio(level));
        if (attacker != null){
            data.putUUID(ATTACKER, attacker.getUUID());
        }
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (context.getTarget() instanceof LivingEntity target){
            schedule(target, context.getAttacker(), damageDealt, modifier.getLevel());
        }
    }

    @Override
    public boolean onProjectileHitEntity(
            ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier,
            Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker,
            @Nullable LivingEntity target, boolean notBlocked) {
        if (target != null && notBlocked){
            schedule(target, attacker, ETModifierCheck.getDamage(projectile), modifier.getLevel());
        }
        return false;
    }

    @SubscribeEvent
    public static void applyPendingEcho(LivingEvent.LivingTickEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target.level() instanceof ServerLevel level)){
            return;
        }
        CompoundTag data = target.getPersistentData();
        if (!data.getBoolean(PENDING) || level.getGameTime() < data.getLong(DUE_TIME)){
            return;
        }

        float damage = data.getFloat(DAMAGE);
        UUID attackerId = data.hasUUID(ATTACKER) ? data.getUUID(ATTACKER) : null;
        data.remove(PENDING);
        data.remove(DUE_TIME);
        data.remove(DAMAGE);
        data.remove(ATTACKER);

        Entity attacker = attackerId == null ? null : level.getEntity(attackerId);
        DamageSource source = attacker instanceof Player player
                              ? target.damageSources().playerAttack(player)
                              : attacker instanceof LivingEntity living
                                ? target.damageSources().mobAttack(living)
                                : target.damageSources().generic();
        int invulnerableTime = target.invulnerableTime;
        try {
            // The echo lands inside the original hit's immunity window. Clear it for this damage only,
            // otherwise vanilla rejects the smaller follow-up hit.
            target.invulnerableTime = 0;
            if (target.hurt(source, damage)){
                playEchoEffect(level, target);
            }
        }
        finally {
            target.invulnerableTime = invulnerableTime;
        }
    }
}
