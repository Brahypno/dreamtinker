package org.brahypno.dreamtinker.common.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerDamageTypes;
import org.brahypno.dreamtinker.common.DreamtinkerEffects;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Dreamtinker.MODID)
public final class DaylostJudgmentEvents {
    public static final String TAG_OVER_SUN_UNTIL = "dreamtinker:over_sun_until";
    public static final String TAG_HANGING_SUN = "dreamtinker:hanging_sun";
    private static final String TAG_JUDGMENT_SEQUENCE = "dreamtinker:daylost_judgment_sequence";
    private static final String TAG_CORONA_SCORCH = "dreamtinker:sunless_corona_scorch";
    private static final String TAG_JUDGMENT_OWNER = "dreamtinker:daylost_judgment_owner";
    private static final int REFRESH_DURATION = 20 * 30;
    private static final int DELAY_TICKS = 10;
    private static final float REDUCED_DAMAGE_FACTOR = 0.50F;
    private static final int HANGING_SUN_THRESHOLD = 100;
    private static final float SOLAR_BASE_MULTIPLIER = 0.20F;
    private static final float SOLAR_LEVEL_MULTIPLIER = 0.10F;
    private static final float SOLAR_DAYLOST_MULTIPLIER = 0.10F;
    private static final float SOLAR_SCORCH_MULTIPLIER_PER_POINT = 0.01F;
    private static final float OVER_SUN_SOLAR_REDUCTION = 0.50F;
    private static final int PASSIVE_SCORCH_PER_SECOND = 1;
    private static final int HANGING_SUN_SCORCH_PER_SECOND = 5;
    private static final int LOW_HEALTH_COMBAT_SCORCH = 2;
    private static final int FAILED_DAYLOST_REMOVAL_SCORCH = 3;
    private static final int DEATH_REFUSAL_SCORCH = 25;
    private static final int STRONG_ENEMY_KILL_SCORCH = 6;
    private static final int BOSS_KILL_SCORCH = 12;
    private static final int ALLY_DEATH_SCORCH = 5;
    private static final float HANGING_SUN_MELTDOWN_BASE_DAMAGE = 0.25F;
    private static final float HANGING_SUN_MELTDOWN_DAMAGE_PER_SCORCH = 0.01F;
    private static final double HANGING_SUN_RANGE = 12.0D;
    private static final double TARGETING_RANGE = 32.0D;
    private static final double ALLY_DEATH_RANGE = 24.0D;
    private static final double JUDGMENT_OWNER_SEARCH_RANGE = 32.0D;
    private static final ThreadLocal<Boolean> REPLAYING_DELAYED_ATTACK = ThreadLocal.withInitial(() -> false);
    private static final List<DelayedAttack> DELAYED_ATTACKS = new ArrayList<>();
    private static final Map<UUID, ReducedAttack> REDUCED_ATTACKS = new ConcurrentHashMap<>();

    private DaylostJudgmentEvents() {}

    @SubscribeEvent
    public static void preventBlindness(MobEffectEvent.Applicable event) {
        if (event.getEffectInstance().getEffect() == MobEffects.BLINDNESS
            && event.getEntity().hasEffect(DreamtinkerEffects.Daylost.get())){
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void resistDaylostRemoval(MobEffectEvent.Remove event) {
        if (event.getEffect() != DreamtinkerEffects.Daylost.get()
            || hasOverSun(event.getEntity())){
            return;
        }

        MobEffectInstance active = event.getEffectInstance();
        if (active == null || active.getDuration() <= 0){
            return;
        }

        event.setCanceled(true);
        int amplifier = Math.min(255, active.getAmplifier() + 1);
        int duration = Math.max(active.getDuration(), REFRESH_DURATION);
        event.getEntity().addEffect(new MobEffectInstance(
                DreamtinkerEffects.Daylost.get(),
                duration,
                amplifier,
                active.isAmbient(),
                active.isVisible(),
                active.showIcon()
        ));
        addScorchForFailedDaylostRemoval(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void adjudicateAttack(LivingAttackEvent event) {
        if (event.isCanceled() || REPLAYING_DELAYED_ATTACK.get() || event.getEntity().level().isClientSide()){
            return;
        }
        if (!event.getSource().is(DreamtinkerDamageTypes.solar_judgment)){
            addLowHealthCombatScorch(event);
        }
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)){
            return;
        }

        LivingEntity defender = event.getEntity();
        MobEffectInstance daylost = attacker.getEffect(DreamtinkerEffects.Daylost.get());
        int sunlessLevel = getSunlessLevel(defender);
        if (daylost == null || sunlessLevel <= 0 || attacker == defender){
            return;
        }

        bindJudgmentOwner(attacker, defender);
        if (hasOverSun(attacker)){
            addCoronaScorch(defender, 1);
            return;
        }

        removeBeneficialEffects(attacker, daylost.getAmplifier() + 1);
        addCoronaScorch(defender, 1);

        if (event.getSource().getDirectEntity() != attacker){
            event.setCanceled(true);
            return;
        }

        if (event.getAmount() <= deniedAttackThreshold(defender, sunlessLevel)){
            event.setCanceled(true);
            applyDaylostFromSunless(attacker, defender, sunlessLevel);
            return;
        }

        int sequence = attacker.getPersistentData().getInt(TAG_JUDGMENT_SEQUENCE);
        attacker.getPersistentData().putInt(TAG_JUDGMENT_SEQUENCE, sequence == Integer.MAX_VALUE ? 0 : sequence + 1);

        switch (sequence % 3) {
            case 0 -> REDUCED_ATTACKS.put(defender.getUUID(), new ReducedAttack(attacker.getUUID(), defender.level().getGameTime()));
            case 1 -> {
                event.setCanceled(true);
                DELAYED_ATTACKS.add(new DelayedAttack(
                        (ServerLevel) defender.level(),
                        defender,
                        attacker,
                        event.getSource(),
                        event.getAmount(),
                        defender.level().getGameTime() + DELAY_TICKS
                ));
            }
            default -> {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void reduceAdjudicatedAttack(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)){
            return;
        }
        ReducedAttack reduced = REDUCED_ATTACKS.remove(event.getEntity().getUUID());
        if (reduced != null
            && reduced.attackerId().equals(attacker.getUUID())
            && reduced.tick() == event.getEntity().level().getGameTime()){
            event.setAmount(event.getAmount() * REDUCED_DAMAGE_FACTOR);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void applySolarDamage(LivingDamageEvent event) {
        if (event.getAmount() <= 0.0F
            || event.getSource().is(DreamtinkerDamageTypes.solar_judgment)
            || !(event.getSource().getEntity() instanceof LivingEntity attacker)){
            return;
        }

        LivingEntity daylostEntity = event.getEntity();
        MobEffectInstance daylost = daylostEntity.getEffect(DreamtinkerEffects.Daylost.get());
        int sunlessLevel = getSunlessLevel(attacker);
        if (attacker == daylostEntity || daylost == null || sunlessLevel <= 0){
            return;
        }

        bindJudgmentOwner(daylostEntity, attacker);
        float multiplier = SOLAR_BASE_MULTIPLIER
                           + SOLAR_LEVEL_MULTIPLIER * sunlessLevel
                           + SOLAR_DAYLOST_MULTIPLIER * (daylost.getAmplifier() + 1)
                           + SOLAR_SCORCH_MULTIPLIER_PER_POINT * getCoronaScorch(attacker);
        if (hasOverSun(daylostEntity)){
            multiplier *= OVER_SUN_SOLAR_REDUCTION;
        }
        float solarDamage = event.getAmount() * multiplier;
        daylostEntity.hurt(
                DreamtinkerDamageTypes.source(daylostEntity.level().registryAccess(), DreamtinkerDamageTypes.solar_judgment, event.getSource()),
                solarDamage
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void limitSunlessHealing(LivingHealEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide()
            || getSunlessLevel(player) <= 0 || event.getAmount() <= 0.0F){
            return;
        }

        float healingCap = player.getMaxHealth() * getHealingCapRatio(player);
        event.setAmount(Math.max(0.0F, Math.min(event.getAmount(), healingCap - player.getHealth())));
    }

    @SubscribeEvent
    public static void buildSunlessPressure(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()
            || event.player.level().getGameTime() % 20 != 0){
            return;
        }

        int sunlessLevel = getSunlessLevel(event.player);
        if (sunlessLevel <= 0){
            return;
        }

        ServerPlayer player = (ServerPlayer) event.player;
        if (getCoronaScorch(player) >= HANGING_SUN_THRESHOLD){
            player.getPersistentData().putBoolean(TAG_HANGING_SUN, true);
        }
        if (hasHangingSun(player)){
            addCoronaScorch(player, HANGING_SUN_SCORCH_PER_SECOND);
            applyHangingSunPressure(player, sunlessLevel);
        }else {
            addCoronaScorch(player, PASSIVE_SCORCH_PER_SECOND);
        }
        applyTargetingDaylost(player, sunlessLevel);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void refuseSunlessDeath(LivingDeathEvent event) {
        if (event.isCanceled() || !(event.getEntity() instanceof ServerPlayer player)
            || getSunlessLevel(player) <= 0){
            return;
        }

        event.setCanceled(true);
        player.deathTime = 0;
        player.setHealth(1.0F);
        player.hurtMarked = true;
        addCoronaScorch(player, DEATH_REFUSAL_SCORCH);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void accumulateScorchFromDeath(LivingDeathEvent event) {
        if (event.isCanceled() || event.getEntity().level().isClientSide()){
            return;
        }

        LivingEntity dead = event.getEntity();
        if (event.getSource().getEntity() instanceof ServerPlayer killer && killer != dead && getSunlessLevel(killer) > 0){
            if (dead instanceof EnderDragon || dead instanceof WitherBoss){
                addCoronaScorch(killer, BOSS_KILL_SCORCH);
            }else if (dead instanceof Enemy && dead.getMaxHealth() >= 40.0F){
                addCoronaScorch(killer, STRONG_ENEMY_KILL_SCORCH);
            }
        }

        if (!(dead.level() instanceof ServerLevel level)){
            return;
        }
        level.getEntitiesOfClass(
                ServerPlayer.class,
                dead.getBoundingBox().inflate(ALLY_DEATH_RANGE),
                player -> player != dead && player.isAlliedTo(dead) && getSunlessLevel(player) > 0
        ).forEach(player -> addCoronaScorch(player, ALLY_DEATH_SCORCH));
    }

    @SubscribeEvent
    public static void processDelayedAttacks(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide() || !(event.level instanceof ServerLevel level)){
            return;
        }

        long now = level.getGameTime();
        REDUCED_ATTACKS.entrySet().removeIf(entry -> entry.getValue().tick() < now);

        Iterator<DelayedAttack> iterator = DELAYED_ATTACKS.iterator();
        while (iterator.hasNext()) {
            DelayedAttack attack = iterator.next();
            if (attack.level() != level || attack.executeAt() > now){
                continue;
            }
            iterator.remove();
            if (!attack.attacker().isAlive() || !attack.target().isAlive()){
                continue;
            }
            REPLAYING_DELAYED_ATTACK.set(true);
            try {
                attack.target().hurt(attack.source(), attack.amount());
            }
            finally {
                REPLAYING_DELAYED_ATTACK.set(false);
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        DELAYED_ATTACKS.clear();
        REDUCED_ATTACKS.clear();
        REPLAYING_DELAYED_ATTACK.remove();
    }

    private static void removeBeneficialEffects(LivingEntity attacker, int amount) {
        List<MobEffect> candidates = attacker.getActiveEffects().stream()
                                             .map(MobEffectInstance::getEffect)
                                             .filter(effect -> effect.getCategory() == MobEffectCategory.BENEFICIAL)
                                             .filter(effect -> effect != DreamtinkerEffects.Daylost.get())
                                             .limit(amount)
                                             .toList();
        for (MobEffect effect : candidates) {
            attacker.removeEffect(effect);
        }
    }

    private static int getSunlessLevel(LivingEntity entity) {
        int level = 0;
        for (ItemStack stack : entity.getAllSlots()) {
            if (!stack.isEmpty() && stack.getItem() instanceof IModifiable){
                level += ToolStack.from(stack).getModifierLevel(DreamtinkerModifiers.sunless.getId());
            }
        }
        return level;
    }

    public static void applyDaylostFromSunless(@javax.annotation.Nullable LivingEntity target, LivingEntity owner, int level) {
        if (target == null || target == owner || target.isAlliedTo(owner) || hasOverSun(target)){
            return;
        }
        MobEffectInstance current = target.getEffect(DreamtinkerEffects.Daylost.get());
        int amplifier = Math.max(0, level - 1);
        int duration = REFRESH_DURATION;
        if (current != null){
            amplifier = Math.max(amplifier, current.getAmplifier());
            duration = Math.max(duration, current.getDuration());
        }
        target.addEffect(new MobEffectInstance(DreamtinkerEffects.Daylost.get(), duration, amplifier));
        bindJudgmentOwner(target, owner);
    }

    private static void addLowHealthCombatScorch(LivingAttackEvent event) {
        if (event.getEntity() instanceof ServerPlayer defender && isLowHealthSunless(defender)){
            addCoronaScorch(defender, LOW_HEALTH_COMBAT_SCORCH);
        }
        if (event.getSource().getEntity() instanceof ServerPlayer attacker
            && attacker != event.getEntity() && isLowHealthSunless(attacker)){
            addCoronaScorch(attacker, LOW_HEALTH_COMBAT_SCORCH);
        }
    }

    private static boolean isLowHealthSunless(ServerPlayer player) {
        return player.getHealth() < player.getMaxHealth() * 0.5F && getSunlessLevel(player) > 0;
    }

    private static void bindJudgmentOwner(LivingEntity daylostEntity, LivingEntity sunlessEntity) {
        if (sunlessEntity instanceof ServerPlayer player){
            daylostEntity.getPersistentData().putUUID(TAG_JUDGMENT_OWNER, player.getUUID());
        }
    }

    private static void addScorchForFailedDaylostRemoval(LivingEntity daylostEntity) {
        if (!(daylostEntity.level() instanceof ServerLevel level)){
            return;
        }
        CompoundTag data = daylostEntity.getPersistentData();
        if (data.hasUUID(TAG_JUDGMENT_OWNER)){
            ServerPlayer owner = level.getServer().getPlayerList().getPlayer(data.getUUID(TAG_JUDGMENT_OWNER));
            if (owner != null && getSunlessLevel(owner) > 0){
                addCoronaScorch(owner, FAILED_DAYLOST_REMOVAL_SCORCH);
                return;
            }
        }
        level.getEntitiesOfClass(
                     ServerPlayer.class,
                     daylostEntity.getBoundingBox().inflate(JUDGMENT_OWNER_SEARCH_RANGE),
                     player -> getSunlessLevel(player) > 0
             ).stream().min(Comparator.comparingDouble(daylostEntity::distanceToSqr))
             .ifPresent(player -> addCoronaScorch(player, FAILED_DAYLOST_REMOVAL_SCORCH));
    }

    private static void addCoronaScorch(LivingEntity entity, int amount) {
        CompoundTag data = entity.getPersistentData();
        long total = (long) data.getInt(TAG_CORONA_SCORCH) + amount;
        data.putInt(TAG_CORONA_SCORCH, (int) Math.min(Integer.MAX_VALUE - 1L, total));
    }

    private static boolean hasOverSun(LivingEntity entity) {
        return entity.getPersistentData().getLong(TAG_OVER_SUN_UNTIL) > entity.level().getGameTime();
    }

    private static boolean hasHangingSun(LivingEntity entity) {
        return entity.getPersistentData().getBoolean(TAG_HANGING_SUN);
    }

    public static void grantOverSun(LivingEntity entity, int duration) {
        int ticks = Mth.clamp(duration, 80, 160);
        long until = entity.level().getGameTime() + ticks;
        CompoundTag data = entity.getPersistentData();
        data.putLong(TAG_OVER_SUN_UNTIL, Math.max(until, data.getLong(TAG_OVER_SUN_UNTIL)));
    }

    private static int getCoronaScorch(LivingEntity entity) {
        return Math.max(0, entity.getPersistentData().getInt(TAG_CORONA_SCORCH));
    }

    private static float deniedAttackThreshold(LivingEntity defender, int sunlessLevel) {
        return 1.0F + sunlessLevel * 0.50F + getCoronaScorch(defender) * 0.04F;
    }

    private static float getHealingCapRatio(LivingEntity entity) {
        return Math.max(0.40F, 0.75F - getCoronaScorch(entity) * 0.0035F);
    }

    private static void applyHangingSunPressure(ServerPlayer player, int sunlessLevel) {
        if (!(player.level() instanceof ServerLevel level)){
            return;
        }
        level.getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(HANGING_SUN_RANGE),
                target -> target != player && target.isAlive() && !target.isAlliedTo(player)
        ).forEach(target -> applyDaylostFromSunless(target, player, sunlessLevel));

        float recoil = HANGING_SUN_MELTDOWN_BASE_DAMAGE
                       + getCoronaScorch(player) * HANGING_SUN_MELTDOWN_DAMAGE_PER_SCORCH;
        player.hurt(
                DreamtinkerDamageTypes.source(player.level().registryAccess(), DreamtinkerDamageTypes.solar_judgment, null, null),
                recoil
        );
    }

    private static void applyTargetingDaylost(ServerPlayer player, int sunlessLevel) {
        if (!(player.level() instanceof ServerLevel level)){
            return;
        }
        level.getEntitiesOfClass(
                Mob.class,
                player.getBoundingBox().inflate(TARGETING_RANGE),
                mob -> mob.isAlive() && mob.getTarget() == player
        ).forEach(mob -> applyDaylostFromSunless(mob, player, sunlessLevel));
    }

    private record ReducedAttack(UUID attackerId, long tick) {}

    private record DelayedAttack(ServerLevel level, LivingEntity target, LivingEntity attacker, DamageSource source, float amount, long executeAt) {}
}
