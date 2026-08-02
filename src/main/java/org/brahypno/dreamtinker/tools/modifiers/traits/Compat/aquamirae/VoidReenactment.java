package org.brahypno.dreamtinker.tools.modifiers.traits.Compat.aquamirae;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
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
import java.util.*;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VoidReenactment extends Modifier implements MeleeHitModifierHook, ProjectileHitModifierHook {
    /**
     * Dominant cyan from Aquamirae's ship graveyard echo texture. Common-side particle data is dedicated-server safe.
     */
    private static final DustParticleOptions ECHO_DUST =
            new DustParticleOptions(new Vector3f(0x4D / 255.0F, 0xCA / 255.0F, 0xDE / 255.0F), 0.8F);
    /**
     * Only levels that currently contain a scheduled echo are present here.
     */
    private static final Map<ServerLevel, EchoQueue> PENDING_ECHOES = new IdentityHashMap<>();

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

    private static void schedule(LivingEntity target, @Nullable LivingEntity attacker, float damage, int modifierLevel) {
        if (!(target.level() instanceof ServerLevel serverLevel) || damage <= 0){
            return;
        }
        EchoQueue queue = PENDING_ECHOES.computeIfAbsent(serverLevel, ignored -> new EchoQueue());
        queue.schedule(new PendingEcho(
                serverLevel.getGameTime() + 10L,
                target.getUUID(),
                attacker == null ? null : attacker.getUUID(),
                damage * echoRatio(modifierLevel)
        ));
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
    public static void applyPendingEcho(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)){
            return;
        }
        EchoQueue queue = PENDING_ECHOES.get(level);
        if (queue == null){
            return;
        }

        long now = level.getGameTime();
        PendingEcho echo;
        while ((echo = queue.pollDue(now)) != null) {
            executeEcho(level, echo);
        }
        if (queue.isEmpty()){
            PENDING_ECHOES.remove(level);
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level){
            PENDING_ECHOES.remove(level);
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        PENDING_ECHOES.clear();
    }

    private static void executeEcho(ServerLevel level, PendingEcho echo) {
        Entity targetEntity = level.getEntity(echo.targetId());
        if (!(targetEntity instanceof LivingEntity target) || !target.isAlive()){
            return;
        }

        Entity attacker = echo.attackerId() == null ? null : level.getEntity(echo.attackerId());
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
            if (target.hurt(source, echo.damage())){
                playEchoEffect(level, target);
            }
        }
        finally {
            target.invulnerableTime = invulnerableTime;
        }
    }

    private record PendingEcho(long dueTime, UUID targetId, @Nullable UUID attackerId, float damage) {}

    /**
     * Priority queue makes a level tick proportional to echoes that are due, not to all living entities.
     */
    private static final class EchoQueue {
        private final PriorityQueue<PendingEcho> byDueTime =
                new PriorityQueue<>(Comparator.comparingLong(PendingEcho::dueTime));
        private final Map<UUID, PendingEcho> byTarget = new HashMap<>();

        private void schedule(PendingEcho echo) {
            // Preserve the old PENDING flag semantics: one outstanding echo per target.
            if (byTarget.putIfAbsent(echo.targetId(), echo) == null){
                byDueTime.add(echo);
            }
        }

        @Nullable
        private PendingEcho pollDue(long now) {
            PendingEcho next = byDueTime.peek();
            if (next == null || next.dueTime() > now){
                return null;
            }
            byDueTime.remove();
            byTarget.remove(next.targetId(), next);
            return next;
        }

        private boolean isEmpty() {
            return byDueTime.isEmpty();
        }
    }
}
