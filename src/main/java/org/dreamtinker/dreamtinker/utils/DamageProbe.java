package org.dreamtinker.dreamtinker.utils;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.dreamtinker.dreamtinker.utils.LootHelper.DTLoots;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public final class DamageProbe {
    private static final int MAX_RETRY_TOTAL = 12;
    private static final int MIN_ENTITY_DATA_SCORE = 55;
    private static final int MIN_FIELD_SCORE = 60;
    private static final float DAMAGE_EPS = 0.01F;
    private static final float DAMAGE_TOLERANCE = 0.999F;

    public static boolean damageHandler(@Nullable Entity entity, DamageSource damageSource, float damageAmount) {
        if (entity == null)
            return false;

        LivingEntity victim = DTHelper.getLivingTarget(entity);
        if (victim != null && !victim.level().isClientSide)
            return DTMethodHandler.invokeLivingHurt(victim, damageSource, damageAmount);

        return entity.hurt(damageSource, damageAmount);
    }

    public static Result finalDamageMethod(@Nullable Entity entity, DamageSource source, float amount) {
        LivingEntity victim = DTHelper.getLivingTarget(entity);
        Result result = new Result(entity, victim, source, amount);

        if (entity == null || source == null || amount <= 0.0F)
            return result.fail("invalid args");
        if (victim == null)
            return result.fail("no living target");
        if (victim.level().isClientSide)
            return result.fail("client side");
        if (!victim.isAlive())
            return result.serverDead("victim already not alive, health=" + victim.getHealth());

        List<PlanStep> steps = buildSteps(entity, victim, source);
        List<RetryCandidate> candidates = new ArrayList<>();

        for (PlanStep step : steps) {
            if (result.reachedExpectedDamage())
                return result.success(step.name());

            StepResult attempt = runStepOnce(step, result);
            if (attempt.progress())
                candidates.add(new RetryCandidate(step, attempt.dealt()));

            if (result.reachedExpectedDamage())
                return result.success(step.name());
        }

        if (result.reachedExpectedDamage())
            return result.success("first_pass_accumulated");
        if (candidates.isEmpty())
            return result.fail("all strategies failed");

        candidates.sort(Comparator.comparingInt(c -> c.estimatedOps(result.remainingAmount())));
        result.add("retry_candidates=" +
                   candidates.stream().map(c -> c.step().name() + "/dealt=" + c.dealtPerOp() + "/estimatedOps=" + c.estimatedOps(result.remainingAmount()))
                             .toList());

        for (RetryCandidate candidate : candidates) {
            int limit = Math.min(MAX_RETRY_TOTAL, Math.max(1, candidate.estimatedOps(result.remainingAmount()) + 2));
            result.add("retry_select: " + candidate.step().name() + ", limit=" + limit);

            for (int i = 0; i < limit && !result.reachedExpectedDamage(); ++i) {
                StepResult attempt = runStepOnce(candidate.step(), result);
                result.add("retry " + candidate.step().name() + " #" + i + ": progress=" + attempt.progress() + ", dealt=" + attempt.dealt() + ", remaining=" +
                           result.remainingAmount());
                if (!attempt.progress())
                    break;
            }

            if (result.reachedExpectedDamage())
                return result.success("retry_" + candidate.step().name());
        }

        return result.fail("all strategies exhausted");
    }

    public static boolean damageBoolean(@Nullable Entity entity, DamageSource source, float amount) {
        return finalDamageMethod(entity, source, amount).success();
    }


    private static List<PlanStep> buildSteps(Entity entity, LivingEntity victim, DamageSource source) {
        List<PlanStep> steps = new ArrayList<>();

        if (entity != victim)
            steps.add(new PlanStep("direct_entity_hurt", (amount, result) -> tryDirectEntityHurt(entity, source, amount, result)));

        steps.add(new PlanStep("dreamtinker_damage_handler", (amount, result) -> tryDreamtinkerDamageHandler(entity, victim, source, amount, result)));
        steps.add(new PlanStep("raw_set_health", (amount, result) -> tryRawSetHealth(victim, source, amount, result)));
        steps.add(new PlanStep("entity_data_unsafe", (amount, result) -> tryEntityDataUnsafe(victim, source, amount, result)));
        steps.add(new PlanStep("private_field_unsafe", (amount, result) -> tryPrivateFieldUnsafe(victim, source, amount, result)));

        return steps;
    }

    private static StepResult runStepOnce(PlanStep step, Result result) {
        float amount = result.remainingAmount();
        if (amount <= DAMAGE_EPS)
            return StepResult.noProgress();

        float before = result.totalDealt();
        boolean rawProgress;

        try {
            rawProgress = step.runner().run(amount, result);
        }
        catch (Throwable e) {
            result.add(step.name() + " fatal error: " + e.getClass().getSimpleName());
            return StepResult.noProgress();
        }

        float dealt = result.totalDealt() - before;
        boolean progress = rawProgress && dealt > DAMAGE_EPS;
        result.add(step.name() + " once: input=" + amount + ", rawProgress=" + rawProgress + ", dealt=" + dealt + ", totalDealt=" + result.totalDealt() +
                   ", remaining=" + result.remainingAmount());
        return new StepResult(progress, Math.max(0.0F, dealt));
    }

    private static boolean tryDirectEntityHurt(Entity entity, DamageSource source, float amount, Result result) {
        if (amount <= DAMAGE_EPS)
            return false;

        clearInvulnerability(entity, result.victim);
        float before = result.health();
        boolean damaged;

        try {
            damaged = entity.hurt(source, amount);
        }
        catch (Throwable e) {
            result.add("direct_entity_hurt error: " + e.getClass().getSimpleName());
            return false;
        }

        float after = result.health();
        result.recordHealthChange(before, after);
        result.add("direct_entity_hurt: returned=" + damaged + ", health " + before + " -> " + after);
        result.recordFieldChanges("direct_entity_hurt");
        handleServerDeath(result.victim, source, result, "direct_entity_hurt", false);
        return before > after || result.serverDead();
    }

    private static boolean tryDreamtinkerDamageHandler(Entity entity, LivingEntity victim, DamageSource source, float amount, Result result) {
        if (amount <= DAMAGE_EPS)
            return false;

        clearInvulnerability(entity, victim);
        float before = victim.getHealth();
        boolean damaged;

        try {
            damaged = damageHandler(entity, source, amount);
        }
        catch (Throwable e) {
            result.add("dreamtinker_damage_handler error: " + e.getClass().getSimpleName());
            return false;
        }

        float after = victim.getHealth();
        result.recordHealthChange(before, after);
        result.add("dreamtinker_damage_handler: returned=" + damaged + ", health " + before + " -> " + after);
        result.recordFieldChanges("dreamtinker_damage_handler");
        handleServerDeath(victim, source, result, "dreamtinker_damage_handler", false);
        return before > after || result.serverDead();
    }

    private static boolean tryRawSetHealth(LivingEntity victim, DamageSource source, float amount, Result result) {
        if (amount <= DAMAGE_EPS)
            return false;

        clearInvulnerability(victim, victim);
        float before = victim.getHealth();
        if (before <= 0.0F)
            return false;

        try {
            victim.setHealth(Math.max(0.0F, before - amount));
            float after = victim.getHealth();
            result.recordHealthChange(before, after);
            result.add("raw_set_health: health " + before + " -> " + after);
            result.recordFieldChanges("raw_set_health");
            handleServerDeath(victim, source, result, "raw_set_health", true);
            return before > after || result.serverDead();
        }
        catch (Throwable e) {
            result.add("raw_set_health error: " + e.getClass().getSimpleName());
            return false;
        }
    }

    private static boolean tryEntityDataUnsafe(LivingEntity victim, DamageSource source, float amount, Result result) {
        if (amount <= DAMAGE_EPS)
            return false;

        List<DataCandidate> candidates = collectEntityDataCandidates(victim);
        result.add("entity_data candidates=" + candidates.size() + ", input=" + amount);

        for (DataCandidate candidate : candidates) {
            EntityDataMove move = tryMoveEntityData(victim, candidate, amount, result);
            if (!move.moved())
                continue;

            result.recordSyntheticDamage(move.dealtEquivalent());
            handleServerDeath(victim, source, result, "entity_data", true);
            return true;
        }

        return false;
    }

    private static boolean tryPrivateFieldUnsafe(LivingEntity victim, DamageSource source, float amount, Result result) {
        if (amount <= DAMAGE_EPS)
            return false;

        List<FieldCandidate> candidates = collectFieldCandidates(victim, result);
        result.add("private_field candidates=" + candidates.size() + ", input=" + amount);

        for (FieldCandidate candidate : candidates) {
            FieldMove move = tryMoveField(victim, candidate, amount, result);
            if (!move.moved())
                continue;

            result.recordSyntheticDamage(move.dealtEquivalent());
            handleServerDeath(victim, source, result, "private_field", true);
            return true;
        }

        return false;
    }

    private static void handleServerDeath(LivingEntity victim, DamageSource source, Result result, String reason, boolean prepareLootFallback) {
        if (victim == null)
            return;
        if (victim.getHealth() > 0.0F)
            return;

        result.serverDead(reason + ", health=" + victim.getHealth() + ", isAlive=" + victim.isAlive()
                          + ", removed=" + victim.isRemoved() + ", deathTime=" + victim.deathTime);

        syncVanillaHealthZero(victim, result);

        try {
            victim.invulnerableTime = 0;
            victim.hurtTime = 0;
            victim.hurtDuration = 0;
            result.add("death_sync flags: invulnerableTime=0, hurtTime=0, hurtDuration=0, deathTime=" + victim.deathTime);
        }
        catch (Throwable e) {
            result.add("death_sync flags error: " + e.getClass().getSimpleName());
        }

        if (prepareLootFallback)
            result.prepareLootFallback(victim, source, reason);

        try {
            victim.die(source);
            result.markDeathHandled(reason + ", die(source)");
            DTLoots.dropAllDeathLootVanilla(victim, source);
            result.add("after_die: health=" + victim.getHealth()
                       + ", isAlive=" + victim.isAlive()
                       + ", removed=" + victim.isRemoved()
                       + ", deathTime=" + victim.deathTime);
        }
        catch (Throwable e) {
            result.add("die(source) error: " + e.getClass().getSimpleName());
        }
    }

    private static void syncVanillaHealthZero(LivingEntity victim, Result result) {
        try {
            EntityDataAccessor<Float> healthAccessor = findLivingHealthAccessor(victim, result);
            if (healthAccessor == null){
                result.add("death_sync: failed to find LivingEntity health accessor");
                return;
            }

            Float before = victim.getEntityData().get(healthAccessor);
            victim.getEntityData().set(healthAccessor, 0.0F);
            Float after = victim.getEntityData().get(healthAccessor);

            result.add("death_sync: vanilla health data " + before + " -> " + after);
        }
        catch (Throwable e) {
            result.add("death_sync health data error: " + e.getClass().getSimpleName());
        }
    }

    private static void clearInvulnerability(Entity entity, LivingEntity victim) {
        if (entity != null)
            entity.invulnerableTime = 0;

        if (victim != null){
            victim.invulnerableTime = 0;
            victim.hurtTime = 0;
            victim.hurtDuration = 0;
        }
    }

    private static List<DataCandidate> collectEntityDataCandidates(LivingEntity victim) {
        List<DataCandidate> result = new ArrayList<>();
        Class<?> cls = victim.getClass();

        while (cls != null && cls != LivingEntity.class && cls != Entity.class && cls != Object.class) {
            collectEntityDataFromClass(victim, cls, result);
            cls = cls.getSuperclass();
        }

        result.sort(Comparator.comparingInt(DataCandidate::score).reversed());
        return result;
    }

    @SuppressWarnings("rawtypes")
    private static void collectEntityDataFromClass(LivingEntity victim, Class<?> cls, List<DataCandidate> result) {
        for (Field field : cls.getDeclaredFields()) {
            try {
                if (!Modifier.isStatic(field.getModifiers()) || !EntityDataAccessor.class.isAssignableFrom(field.getType()))
                    continue;

                field.setAccessible(true);
                EntityDataAccessor accessor = (EntityDataAccessor) field.get(null);
                Object value = getData(victim, accessor);
                if (!(value instanceof Float) && !(value instanceof Integer))
                    continue;

                DataMode mode = guessMode(field.getName());
                int score = scoreName(field.getName(), value, mode) + 10;
                if (score >= MIN_ENTITY_DATA_SCORE)
                    result.add(new DataCandidate(field.getName(), accessor, value, score, mode));
            }
            catch (Throwable ignored) {}
        }
    }

    private static List<FieldCandidate> collectFieldCandidates(LivingEntity victim, Result probe) {
        List<FieldCandidate> result = new ArrayList<>();
        Class<?> cls = victim.getClass();

        while (cls != null && cls != LivingEntity.class && cls != Entity.class && cls != Object.class) {
            collectFieldsFromClass(victim, cls, probe, result);
            cls = cls.getSuperclass();
        }

        result.sort(Comparator.comparingInt(FieldCandidate::score).reversed());
        return result;
    }

    private static void collectFieldsFromClass(LivingEntity victim, Class<?> cls, Result probe, List<FieldCandidate> result) {
        for (Field field : cls.getDeclaredFields()) {
            try {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()))
                    continue;
                if (!isNumericField(field))
                    continue;

                field.setAccessible(true);
                Object value = field.get(victim);
                DataMode mode = guessMode(field.getName());
                int score = scoreName(field.getName(), value, mode) + probe.scoreObservedField(field) + scoreGetHealthDependency(victim, field, mode);
                if (score >= MIN_FIELD_SCORE)
                    result.add(new FieldCandidate(field, field.getName(), value, score, mode));
            }
            catch (Throwable ignored) {}
        }
    }

    private static int scoreGetHealthDependency(LivingEntity victim, Field field, DataMode mode) {
        try {
            Object beforeValue = field.get(victim);
            float beforeHealth = victim.getHealth();
            Object nudged = nudgeValue(beforeValue, mode == DataMode.REMAINING ? -1.0F : 1.0F);
            if (nudged == null)
                return 0;

            field.set(victim, nudged);
            float afterHealth = victim.getHealth();
            field.set(victim, beforeValue);

            if (Math.abs(afterHealth - beforeHealth) > DAMAGE_EPS)
                return 45;
        }
        catch (Throwable ignored) {}

        return 0;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static EntityDataMove tryMoveEntityData(LivingEntity victim, DataCandidate candidate, float amount, Result result) {
        try {
            Object before = getData(victim, candidate.accessor);
            Object after = nextValue(before, amount, candidate.mode);
            if (after == null)
                return EntityDataMove.failed();

            victim.getEntityData().set((EntityDataAccessor) candidate.accessor, after);
            Object confirmed = getData(victim, candidate.accessor);
            boolean moved = movedAsDamage(before, confirmed, candidate.mode);
            float dealt = moved ? dealtEquivalent(before, confirmed, candidate.mode) : 0.0F;

            result.add("entity_data " + candidate.mode + " " + candidate.name + ": " + before + " -> " + confirmed + ", score=" + candidate.score + ", dealt=" +
                       dealt);
            return new EntityDataMove(moved, dealt);
        }
        catch (Throwable e) {
            result.add("entity_data " + candidate.name + " error: " + e.getClass().getSimpleName());
            return EntityDataMove.failed();
        }
    }

    private static FieldMove tryMoveField(LivingEntity victim, FieldCandidate candidate, float amount, Result result) {
        try {
            Object before = candidate.field.get(victim);
            Object after = nextValue(before, amount, candidate.mode);
            if (after == null)
                return FieldMove.failed();

            candidate.field.set(victim, after);
            Object confirmed = candidate.field.get(victim);
            boolean moved = movedAsDamage(before, confirmed, candidate.mode);
            float dealt = moved ? dealtEquivalent(before, confirmed, candidate.mode) : 0.0F;

            result.add(
                    "private_field " + candidate.mode + " " + candidate.name + ": " + before + " -> " + confirmed + ", score=" + candidate.score + ", dealt=" +
                    dealt);
            return new FieldMove(moved, dealt);
        }
        catch (Throwable e) {
            result.add("private_field " + candidate.name + " error: " + e.getClass().getSimpleName());
            return FieldMove.failed();
        }
    }

    private static Object getData(LivingEntity victim, EntityDataAccessor<?> accessor) {
        try {
            return victim.getEntityData().get(accessor);
        }
        catch (Throwable e) {
            return null;
        }
    }

    private static Object nextValue(Object before, float amount, DataMode mode) {
        if (before instanceof Float f)
            return Float.isFinite(f) ? (mode == DataMode.REMAINING ? Math.max(0.0F, f - amount) : Math.min(100000.0F, f + amount)) : null;
        if (before instanceof Double d)
            return Double.isFinite(d) ? (mode == DataMode.REMAINING ? Math.max(0.0D, d - amount) : Math.min(100000.0D, d + amount)) : null;
        if (before instanceof Integer i)
            return mode == DataMode.REMAINING ? Math.max(0, i - Math.max(1, (int) Math.ceil(amount))) :
                   Math.min(100000, i + Math.max(1, (int) Math.ceil(amount)));
        if (before instanceof Long l)
            return mode == DataMode.REMAINING ? Math.max(0L, l - Math.max(1L, (long) Math.ceil(amount))) :
                   Math.min(100000L, l + Math.max(1L, (long) Math.ceil(amount)));
        return null;
    }

    private static Object nudgeValue(Object before, float delta) {
        if (before instanceof Float f)
            return Float.isFinite(f) ? f + delta : null;
        if (before instanceof Double d)
            return Double.isFinite(d) ? d + delta : null;
        if (before instanceof Integer i)
            return i + (delta < 0 ? -1 : 1);
        if (before instanceof Long l)
            return l + (delta < 0 ? -1L : 1L);
        return null;
    }

    private static boolean movedAsDamage(Object before, Object after, DataMode mode) {
        double b = asDouble(before);
        double a = asDouble(after);
        if (!Double.isFinite(b) || !Double.isFinite(a))
            return false;
        return mode == DataMode.REMAINING ? a < b : a > b;
    }

    private static float dealtEquivalent(Object before, Object after, DataMode mode) {
        double b = asDouble(before);
        double a = asDouble(after);
        if (!Double.isFinite(b) || !Double.isFinite(a))
            return 0.0F;
        return (float) Math.max(0.0D, mode == DataMode.REMAINING ? b - a : a - b);
    }

    private static double asDouble(Object value) {
        if (value instanceof Number n)
            return n.doubleValue();
        return Double.NaN;
    }

    private static boolean isNumericField(Field field) {
        Class<?> type = field.getType();
        return type == float.class || type == Float.class || type == double.class || type == Double.class || type == int.class || type == Integer.class ||
               type == long.class || type == Long.class;
    }

    private static DataMode guessMode(String rawName) {
        String name = rawName.toUpperCase(Locale.ROOT);
        if (containsAny(name, "TOTALDAMAGETAKEN", "TOTAL_DAMAGE_TAKEN", "DAMAGETAKEN", "DAMAGE_TAKEN", "TAKENDAMAGE", "TAKEN_DAMAGE", "HURT_TAKEN",
                        "WOUND_TAKEN", "INJURY_TAKEN", "DAMAGE", "HURT", "WOUND", "INJURY", "DMG"))
            return DataMode.TAKEN;
        return DataMode.REMAINING;
    }

    private static int scoreName(String rawName, Object value, DataMode mode) {
        String name = rawName.toUpperCase(Locale.ROOT);
        int score = value instanceof Float || value instanceof Double ? 20 : 12;

        if (mode == DataMode.TAKEN && containsAny(name, "TOTALDAMAGETAKEN", "TOTAL_DAMAGE_TAKEN", "DAMAGETAKEN", "DAMAGE_TAKEN", "TAKENDAMAGE", "TAKEN_DAMAGE"))
            score += 120;
        if (mode == DataMode.REMAINING && containsAny(name, "HEALTH", "HP", "LIFE", "VITAL"))
            score += 75;
        if (mode == DataMode.REMAINING && containsAny(name, "SHIELD", "BARRIER", "CORE", "PART"))
            score += 35;
        if (mode == DataMode.TAKEN && containsAny(name, "HURT_TAKEN", "WOUND_TAKEN", "INJURY_TAKEN"))
            score += 75;
        if (mode == DataMode.TAKEN && containsAny(name, "DAMAGE", "HURT", "WOUND", "INJURY", "DMG"))
            score += 35;

        if (containsAny(name, "ANIM", "TIMER", "TIME", "COOLDOWN", "POSE", "FLAGS", "FLAG", "STATE", "MODE", "VARIANT", "TYPE", "ID", "PROGRESS", "LAST",
                        "CLIENT", "TARGET", "ATTACK", "LOCK"))
            score -= 60;
        if (containsAny(name, "MAX", "CAP", "LIMIT", "MULTIPLIER"))
            score -= 35;

        double d = asDouble(value);
        if (Double.isFinite(d) && d >= 0.0D && d <= 100000.0D)
            score += 8;
        else
            score -= 30;

        return score;
    }

    private static boolean containsAny(String s, String... keys) {
        for (String key : keys)
            if (s.contains(key))
                return true;
        return false;
    }

    private static EntityDataAccessor<Float> findLivingHealthAccessor(LivingEntity victim, Result result) {
        EntityDataAccessor<Float> byName = findLivingHealthAccessorByName(victim, result);
        if (byName != null)
            return byName;

        return findLivingHealthAccessorByFloatType(victim, result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static EntityDataAccessor<Float> findLivingHealthAccessorByName(LivingEntity victim, Result result) {
        Class<?> cls = LivingEntity.class;

        for (Field field : cls.getDeclaredFields()) {
            try {
                if (!Modifier.isStatic(field.getModifiers()))
                    continue;
                if (!EntityDataAccessor.class.isAssignableFrom(field.getType()))
                    continue;

                String name = field.getName();
                if (!name.equals("DATA_HEALTH_ID") && !name.equals("f_20961_"))
                    continue;

                field.setAccessible(true);
                EntityDataAccessor accessor = (EntityDataAccessor) field.get(null);
                Object value = victim.getEntityData().get(accessor);

                if (value instanceof Float){
                    result.add("death_sync: found health accessor by name=" + name + ", value=" + value);
                    return (EntityDataAccessor<Float>) accessor;
                }

                result.add("death_sync: named health accessor " + name + " had non-float value=" + value);
            }
            catch (Throwable e) {
                result.add("death_sync: named health accessor error field=" + field.getName() + ", error=" + e.getClass().getSimpleName());
            }
        }

        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static EntityDataAccessor<Float> findLivingHealthAccessorByFloatType(LivingEntity victim, Result result) {
        Class<?> cls = LivingEntity.class;

        for (Field field : cls.getDeclaredFields()) {
            try {
                if (!Modifier.isStatic(field.getModifiers()))
                    continue;
                if (!EntityDataAccessor.class.isAssignableFrom(field.getType()))
                    continue;

                field.setAccessible(true);
                EntityDataAccessor accessor = (EntityDataAccessor) field.get(null);
                Object value;

                try {
                    value = victim.getEntityData().get(accessor);
                }
                catch (Throwable ignored) {
                    continue;
                }

                if (value instanceof Float f && Float.isFinite(f)){
                    result.add("death_sync: fallback float EntityDataAccessor field=" + field.getName() + ", value=" + f);
                    return (EntityDataAccessor<Float>) accessor;
                }
            }
            catch (Throwable e) {
                result.add("death_sync: fallback accessor error field=" + field.getName() + ", error=" + e.getClass().getSimpleName());
            }
        }
        return null;
    }

    private enum DataMode {
        REMAINING,
        TAKEN
    }

    @FunctionalInterface
    private interface StepRunner {
        boolean run(float amount, Result result);
    }

    private record PlanStep(String name, StepRunner runner) {}

    private record StepResult(boolean progress, float dealt) {
        private static StepResult noProgress() {
            return new StepResult(false, 0.0F);
        }
    }

    private record RetryCandidate(PlanStep step, float dealtPerOp) {
        private int estimatedOps(float remaining) {
            if (dealtPerOp <= DAMAGE_EPS)
                return Integer.MAX_VALUE;
            return Math.max(1, (int) Math.ceil(remaining / dealtPerOp));
        }
    }

    private record DataCandidate(String name, EntityDataAccessor<?> accessor, Object value, int score, DataMode mode) {}

    private record FieldCandidate(Field field, String name, Object value, int score, DataMode mode) {}

    private record EntityDataMove(boolean moved, float dealtEquivalent) {
        private static EntityDataMove failed() {
            return new EntityDataMove(false, 0.0F);
        }
    }

    private record FieldMove(boolean moved, float dealtEquivalent) {
        private static FieldMove failed() {
            return new FieldMove(false, 0.0F);
        }
    }

    public static final class Result {
        private final Entity entity;
        private final LivingEntity victim;
        private final DamageSource source;
        private final float amount;
        private final List<String> lines = new ArrayList<>();
        private final Map<String, FieldSnapshot> fieldSnapshot = new HashMap<>();
        private final Map<String, Integer> observedFieldScores = new HashMap<>();
        private boolean success;
        private boolean serverDead;
        private boolean deathHandled;
        private boolean lootFallbackPrepared;
        private LivingEntity lootFallbackEntity;
        private DamageSource lootFallbackSource;
        private String lootFallbackReason = "";
        private String strategy = "none";
        private float totalDealt;

        private Result(Entity entity, LivingEntity victim, DamageSource source, float amount) {
            this.entity = entity;
            this.victim = victim;
            this.source = source;
            this.amount = amount;
            snapshotFields();
        }

        private static String fieldKey(Field field) {
            return field.getDeclaringClass().getName() + "#" + field.getName();
        }

        private Result success(String strategy) {
            this.success = true;
            this.strategy = strategy;
            add("success: " + strategy);
            return this;
        }

        private Result fail(String reason) {
            add("failed: " + reason);
            return this;
        }

        private Result serverDead(String reason) {
            this.serverDead = true;
            add("server_dead: " + reason);
            return this;
        }

        private void markDeathHandled(String reason) {
            this.serverDead = true;
            this.deathHandled = true;
            add("death_handled: " + reason);
        }

        private void prepareLootFallback(LivingEntity victim, DamageSource source, String reason) {
            if (lootFallbackPrepared)
                return;

            this.lootFallbackPrepared = true;
            this.lootFallbackEntity = victim;
            this.lootFallbackSource = source;
            this.lootFallbackReason = reason;
            add("loot_fallback_prepared: entity=" + victim.getType() + ", reason=" + reason);
        }

        private void add(String line) {
            lines.add(line);
        }

        private float health() {
            return victim == null ? -1.0F : victim.getHealth();
        }

        private void recordHealthChange(float before, float after) {
            if (before > after)
                totalDealt += before - after;
        }

        private void recordSyntheticDamage(float amount) {
            if (amount > 0.0F)
                totalDealt += amount;
        }

        private void snapshotFields() {
            fieldSnapshot.clear();
            if (victim == null)
                return;

            Class<?> cls = victim.getClass();
            while (cls != null && cls != LivingEntity.class && cls != Entity.class && cls != Object.class) {
                for (Field field : cls.getDeclaredFields())
                    snapshotField(field);
                cls = cls.getSuperclass();
            }
        }

        private void snapshotField(Field field) {
            try {
                if (Modifier.isStatic(field.getModifiers()) || !isNumericField(field))
                    return;

                field.setAccessible(true);
                Object value = field.get(victim);
                fieldSnapshot.put(fieldKey(field), new FieldSnapshot(field, value));
            }
            catch (Throwable ignored) {}
        }

        private void recordFieldChanges(String sourceName) {
            if (victim == null)
                return;

            float health = health();
            for (FieldSnapshot snapshot : fieldSnapshot.values()) {
                try {
                    Object now = snapshot.field.get(victim);
                    double before = asDouble(snapshot.value);
                    double after = asDouble(now);
                    if (!Double.isFinite(before) || !Double.isFinite(after) || Math.abs(after - before) <= DAMAGE_EPS)
                        continue;

                    int score = after > before ? 30 : 20;
                    observedFieldScores.merge(fieldKey(snapshot.field), score, Integer::sum);
                    add("observed_field_change by " + sourceName + ": " + snapshot.field.getName() + " " + before + " -> " + after + ", healthNow=" + health);
                }
                catch (Throwable ignored) {}
            }

            snapshotFields();
        }

        private int scoreObservedField(Field field) {
            return observedFieldScores.getOrDefault(fieldKey(field), 0);
        }

        public boolean reachedExpectedDamage() {
            return serverDead || totalDealt + DAMAGE_EPS >= amount * DAMAGE_TOLERANCE || victim != null && !victim.isAlive();
        }

        public float remainingAmount() {
            return reachedExpectedDamage() ? 0.0F : Math.max(0.0F, amount - totalDealt);
        }

        public float totalDealt() {
            return totalDealt;
        }

        public boolean success() {
            return success;
        }

        public boolean serverDead() {
            return serverDead;
        }

        public boolean deathHandled() {
            return deathHandled;
        }

        public boolean lootFallbackPrepared() {
            return lootFallbackPrepared;
        }

        public LivingEntity lootFallbackEntity() {
            return lootFallbackEntity;
        }

        public DamageSource lootFallbackSource() {
            return lootFallbackSource;
        }

        public String lootFallbackReason() {
            return lootFallbackReason;
        }

        public DamageSource source() {
            return source;
        }

        public String strategy() {
            return strategy;
        }

        public List<String> lines() {
            return List.copyOf(lines);
        }

        public String debugText() {
            String entityName = entity == null ? "null" : String.valueOf(entity.getType());
            String victimName = victim == null ? "null" : String.valueOf(victim.getType());
            return "DamageProbe{entity=" + entityName
                   + ", victim=" + victimName
                   + ", amount=" + amount
                   + ", totalDealt=" + totalDealt
                   + ", remaining=" + remainingAmount()
                   + ", success=" + success
                   + ", serverDead=" + serverDead
                   + ", deathHandled=" + deathHandled
                   + ", lootFallbackPrepared=" + lootFallbackPrepared
                   + ", lootFallbackReason=" + lootFallbackReason
                   + ", strategy=" + strategy
                   + ", steps=" + lines
                   + "}";
        }

        private record FieldSnapshot(Field field, Object value) {}
    }
}