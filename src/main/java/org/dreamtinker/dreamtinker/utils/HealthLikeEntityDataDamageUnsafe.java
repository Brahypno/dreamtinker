package org.dreamtinker.dreamtinker.utils;


import com.mojang.logging.LogUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public final class HealthLikeEntityDataDamageUnsafe {
    /**
     * 低于这个分数的 EntityDataAccessor 不尝试修改。
     * 如果你想更激进，可以降到 30；如果想保守，可以升到 60~80。
     */
    public static final int DEFAULT_MIN_SCORE = 45;
    private static final Logger LOGGER = LogUtils.getLogger();

    private HealthLikeEntityDataDamageUnsafe() {}

    /**
     * 最常用入口：
     * 对 living 的疑似 health-like EntityDataAccessor 按 score 从高到低尝试扣 amount。
     * <p>
     * 成功定义：
     * - 成功写入；
     * - 立刻读回；
     * - 读回值确实比原值小。
     * <p>
     * 如果写到 0 或以下，会尝试 living.die(source)，但 die 本身仍然尊重实体 override。
     */
    public static boolean tryDamageHealthLikeEntityDataUnsafe(LivingEntity living, DamageSource source, float amount) {
        return tryDamageHealthLikeEntityDataUnsafe(living, source, amount, DEFAULT_MIN_SCORE, true).success();
    }

    public static AttemptResult tryDamageHealthLikeEntityDataUnsafe(LivingEntity living, DamageSource source, float amount, int minScore, boolean callDieWhenZero) {
        if (living == null || source == null || amount <= 0.0F || !living.isAlive())
            return AttemptResult.failed();

        List<EntityDataDamageCandidate> candidates = collectCandidates(living, minScore);

        if (candidates.isEmpty()){
            LOGGER.debug("[ForceHit] no health-like EntityDataAccessor candidates: entity={}", living.getType());
            return AttemptResult.failed();
        }

        LOGGER.debug("[ForceHit] health-like EntityDataAccessor candidates for {}: {}",
                     living.getType(),
                     candidates.stream().map(c -> c.fieldName() + "/" + c.kind() + "/score=" + c.score()).toList());

        return tryDamageCandidatesUnsafe(living, source, amount, candidates, callDieWhenZero);
    }

    /**
     * 只扫描当前实体类到 LivingEntity 之间的 static EntityDataAccessor 字段。
     * 不扫描 LivingEntity.DATA_HEALTH_ID；那个应该由你的 vanilla raw health fallback 单独处理。
     */
    public static List<EntityDataDamageCandidate> collectCandidates(LivingEntity living, int minScore) {
        List<EntityDataDamageCandidate> result = new ArrayList<>();
        if (living == null)
            return result;

        Class<?> cls = living.getClass();

        while (cls != null && cls != LivingEntity.class && cls != Entity.class && cls != Object.class) {
            collectCandidatesFromClass(living, cls, result);
            cls = cls.getSuperclass();
        }

        return result.stream()
                     .filter(c -> c.score() >= minScore)
                     .sorted(Comparator.comparingInt(EntityDataDamageCandidate::score).reversed())
                     .toList();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void collectCandidatesFromClass(LivingEntity living, Class<?> cls, List<EntityDataDamageCandidate> result) {
        for (Field field : cls.getDeclaredFields()) {
            try {
                if (!Modifier.isStatic(field.getModifiers()))
                    continue;
                if (!EntityDataAccessor.class.isAssignableFrom(field.getType()))
                    continue;

                field.setAccessible(true);

                EntityDataAccessor accessor = (EntityDataAccessor) field.get(null);
                if (accessor == null)
                    continue;

                Object current;
                try {
                    current = living.getEntityData().get(accessor);
                }
                catch (Throwable ignored) {
                    continue;
                }

                if (!(current instanceof Float) && !(current instanceof Integer))
                    continue;

                ScoreResult score = scoreField(field.getName(), current);
                if (score.score() <= 0)
                    continue;

                result.add(new EntityDataDamageCandidate(
                        field,
                        accessor,
                        cls.getName(),
                        field.getName(),
                        current,
                        score.score(),
                        score.kind(),
                        score.reasons()
                ));
            }
            catch (Throwable e) {
                LOGGER.debug("[ForceHit] failed to inspect EntityDataAccessor field: entity={}, class={}, field={}",
                             living.getType(), cls.getName(), field.getName(), e);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static AttemptResult tryDamageCandidatesUnsafe(LivingEntity living, DamageSource source, float amount, List<EntityDataDamageCandidate> candidates, boolean callDieWhenZero) {
        for (EntityDataDamageCandidate c : candidates) {
            try {
                EntityDataAccessor accessor = (EntityDataAccessor) c.accessor();

                Object before = living.getEntityData().get(accessor);
                Object after = computeReducedValue(before, amount);

                if (after == null || Objects.equals(before, after))
                    continue;

                living.getEntityData().set(accessor, after);

                Object confirmed = living.getEntityData().get(accessor);

                if (!valueReduced(before, confirmed)){
                    LOGGER.debug("[ForceHit] EntityDataAccessor write did not reduce value: entity={}, field={}, before={}, attempted={}, confirmed={}",
                                 living.getType(), c.fieldName(), before, after, confirmed);
                    continue;
                }

                if (callDieWhenZero && zeroOrBelow(confirmed)){
                    try {
                        living.die(source);
                    }
                    catch (Throwable dieError) {
                        LOGGER.debug("[ForceHit] die(source) failed after health-like EntityDataAccessor reached zero: entity={}, field={}",
                                     living.getType(), c.fieldName(), dieError);
                    }
                }

                LOGGER.debug(
                        "[ForceHit] health-like EntityDataAccessor damage success: entity={}, ownerClass={}, field={}, kind={}, score={}, before={}, after={}, reasons={}",
                        living.getType(), c.ownerClass(), c.fieldName(), c.kind(), c.score(), before, confirmed, c.reasons());

                return new AttemptResult(true, c.fieldName(), c.kind(), c.score(), before, confirmed);
            }
            catch (Throwable e) {
                LOGGER.debug("[ForceHit] health-like EntityDataAccessor candidate failed: entity={}, ownerClass={}, field={}, kind={}, score={}",
                             living.getType(), c.ownerClass(), c.fieldName(), c.kind(), c.score(), e);
            }
        }

        return AttemptResult.failed();
    }

    private static Object computeReducedValue(Object before, float amount) {
        if (before instanceof Float f){
            if (!Float.isFinite(f) || f <= 0.0F)
                return null;
            return Math.max(0.0F, f - amount);
        }

        if (before instanceof Integer i){
            if (i <= 0)
                return null;
            int intDamage = Math.max(1, (int) Math.ceil(amount));
            return Math.max(0, i - intDamage);
        }

        return null;
    }

    private static boolean valueReduced(Object before, Object after) {
        if (before instanceof Float b && after instanceof Float a)
            return Float.isFinite(b) && Float.isFinite(a) && a < b;

        if (before instanceof Integer b && after instanceof Integer a)
            return a < b;

        return false;
    }

    private static boolean zeroOrBelow(Object value) {
        if (value instanceof Float f)
            return f <= 0.0F;
        if (value instanceof Integer i)
            return i <= 0;
        return false;
    }

    private static ScoreResult scoreField(String rawName, Object currentValue) {
        String name = rawName == null ? "" : rawName.toUpperCase(Locale.ROOT);
        int score = 0;
        GuessKind kind = GuessKind.UNKNOWN_NUMERIC_STATE;
        List<String> reasons = new ArrayList<>();

        if (currentValue instanceof Float){
            score += 20;
            reasons.add("value type is Float");
        }else if (currentValue instanceof Integer){
            score += 14;
            reasons.add("value type is Integer");
        }

        if (containsAny(name, "HEALTH", "HP", "LIFE", "VITAL")){
            score += 50;
            kind = GuessKind.LIKELY_CUSTOM_HEALTH;
            reasons.add("field name looks health-like");
        }

        if (containsAny(name, "SHIELD", "BARRIER", "ARMOR", "GUARD")){
            score += 30;
            if (kind == GuessKind.UNKNOWN_NUMERIC_STATE)
                kind = GuessKind.LIKELY_SHIELD_HEALTH;
            reasons.add("field name looks shield-like");
        }

        if (containsAny(name, "PHASE", "STAGE", "CORE", "PART")){
            score += 25;
            if (kind == GuessKind.UNKNOWN_NUMERIC_STATE)
                kind = GuessKind.LIKELY_PHASE_HEALTH;
            reasons.add("field name looks phase/core-like");
        }

        if (containsAny(name, "DAMAGE", "HURT", "WOUND")){
            score += 20;
            if (kind == GuessKind.UNKNOWN_NUMERIC_STATE)
                kind = GuessKind.LIKELY_DAMAGE_STATE;
            reasons.add("field name looks damage-related");
        }

        if (containsAny(name, "ANIMATION", "ANIM", "TIMER", "COOLDOWN", "POSE", "FLAGS", "FLAG", "STATE", "MODE", "VARIANT", "TYPE", "ID")){
            score -= 35;
            reasons.add("field name looks non-health state");
        }

        if (currentValue instanceof Float f){
            if (Float.isFinite(f) && f > 0.0F && f <= 4096.0F){
                score += 10;
                reasons.add("float value is positive and plausible");
            }else {
                score -= 30;
                reasons.add("float value is implausible");
            }
        }

        if (currentValue instanceof Integer i){
            if (i > 0 && i <= 100000){
                score += 8;
                reasons.add("integer value is positive and plausible");
            }else {
                score -= 25;
                reasons.add("integer value is implausible");
            }
        }

        return new ScoreResult(score, kind, reasons);
    }

    private static boolean containsAny(String s, String... keys) {
        for (String key : keys)
            if (s.contains(key))
                return true;
        return false;
    }

    public enum GuessKind {
        LIKELY_CUSTOM_HEALTH,
        LIKELY_PHASE_HEALTH,
        LIKELY_SHIELD_HEALTH,
        LIKELY_DAMAGE_STATE,
        UNKNOWN_NUMERIC_STATE
    }

    public record EntityDataDamageCandidate(
            Field field,
            EntityDataAccessor<?> accessor,
            String ownerClass,
            String fieldName,
            Object currentValue,
            int score,
            GuessKind kind,
            List<String> reasons
    ) {}

    public record ScoreResult(int score, GuessKind kind, List<String> reasons) {}

    public record AttemptResult(
            boolean success,
            String fieldName,
            GuessKind kind,
            int score,
            Object before,
            Object after
    ) {
        public static AttemptResult failed() {
            return new AttemptResult(false, "", GuessKind.UNKNOWN_NUMERIC_STATE, 0, null, null);
        }
    }
}