package org.brahypno.dreamtinker.library.modifiers.modules.combat;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.Entity.NarcissusFluidProjectile;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;

import java.util.*;

public final class NarcissusFluidFeedbacks {
    public static final String ROOT = "dreamtinker:narcissus_fluid_feedback";
    public static final String NEXT_SHOT = "next_shot";
    public static final String GUARD = "guard";
    public static final String MELEE = "melee";
    public static final String CATALYST = "catalyst";
    public static final String PROJECTILE_CATALYST = "dreamtinker:narcissus_fluid_catalyst";

    private static final Map<ResourceLocation, ResolvedFluidFeedback> OVERRIDES = new HashMap<>();
    private static final Map<Fluid, ResolvedFluidFeedback> CACHE = new IdentityHashMap<>();

    private static final TagKey<Fluid> DROWNED_SWASHER = tconstructFluidTag("swasher/drowned");
    private static final TagKey<Fluid> WITHER_SKELETON_SWASHER = tconstructFluidTag("swasher/wither_skeleton");

    private static final String CATEGORY = "category";
    private static final String MODE = "mode";
    private static final String STRENGTH = "strength";
    private static final String EXPIRES = "expires";
    private static final String CHARGES = "charges";

    private static final Category[] CATEGORIES = {
            new Category("slime", new TagKey[]{TinkerTags.Fluids.SLIME, TinkerTags.Fluids.SLIME_TOOLTIPS},
                         Mode.SLIME_BOUNCE, Mode.SLIME_CUSHION, Mode.SLIME_RICOCHET, Mode.SLIME_STICK,
                         Mode.SLIME_SPLASH, Mode.SLIME_REBOUND_GUARD, Mode.SLIME_ELASTIC_STRIKE, Mode.SLIME_DRIFT, Mode.WATER_FLOW),
            new Category("metal", new TagKey[]{TinkerTags.Fluids.METAL_TOOLTIPS},
                         Mode.FORGE_BLADE, Mode.FORGE_PLATE, Mode.FORGE_BARREL, Mode.FORGE_RING,
                         Mode.FORGE_RIVET, Mode.FORGE_EDGE, Mode.FORGE_BULWARK, Mode.FORGE_TEMPER),
            new Category("large_gem", new TagKey[]{TinkerTags.Fluids.LARGE_GEM_TOOLTIPS},
                         Mode.PRISM_AMPLIFY, Mode.PRISM_DUPLICATE, Mode.PRISM_FOCUS, Mode.PRISM_LOCK,
                         Mode.PRISM_CRIT, Mode.PRISM_AEGIS, Mode.PRISM_ECHO, Mode.PRISM_CONDENSE),
            new Category("small_gem", new TagKey[]{TinkerTags.Fluids.SMALL_GEM_TOOLTIPS},
                         Mode.SHARD_QUICKEN, Mode.SHARD_REFUND, Mode.SHARD_CHAIN, Mode.SHARD_SPARK,
                         Mode.SHARD_MARK, Mode.SHARD_EDGE, Mode.SHARD_STEP, Mode.SHARD_RELOAD, Mode.WATER_REFRESH),
            new Category("clay", new TagKey[]{TinkerTags.Fluids.CLAY_TOOLTIPS},
                         Mode.MASON_GUARD, Mode.MASON_WALL, Mode.MASON_WEIGHT, Mode.MASON_SETTLE,
                         Mode.MASON_ROOT, Mode.MASON_COUNTER, Mode.MASON_PLASTER, Mode.MASON_REINFORCE, Mode.WATER_SOAK_GUARD),
            new Category("glass", new TagKey[]{TinkerTags.Fluids.GLASS_TOOLTIPS},
                         Mode.GLASS_REFRACT, Mode.GLASS_MIRROR, Mode.GLASS_LENS, Mode.GLASS_SHATTER,
                         Mode.GLASS_ECHO, Mode.GLASS_PRISM_STEP, Mode.GLASS_NEEDLE, Mode.GLASS_WARD),
            new Category("bottle", new TagKey[]{TinkerTags.Fluids.BOTTLE_TOOLTIPS},
                         Mode.REAGENT_PURGE, Mode.REAGENT_CATALYZE, Mode.REAGENT_DISTILL, Mode.REAGENT_ADAPT,
                         Mode.REAGENT_OVERDOSE, Mode.REAGENT_STIMULATE, Mode.REAGENT_SEAL, Mode.REAGENT_SPLASHBACK, Mode.WATER_WASH),
            new Category("water", new TagKey[]{FluidTags.WATER},
                         Mode.WATER_EXTINGUISH, Mode.WATER_CLEANSE, Mode.WATER_COOL),
            new Category("drowned", new TagKey[]{DROWNED_SWASHER},
                         Mode.DROWNED_TIDE_PULL, Mode.DROWNED_DRENCHED_STRIKE, Mode.DROWNED_UNDERCURRENT, Mode.DROWNED_BREATH,
                         Mode.DROWNED_TRIDENT_MEMORY, Mode.DROWNED_SINKING_GUARD, Mode.DROWNED_WET_RELOAD, Mode.DROWNED_GRAVE_WATER, Mode.WATER_RIPTIDE),
            new Category("wither_skeleton", new TagKey[]{WITHER_SKELETON_SWASHER},
                         Mode.WITHER_BONE_EDGE, Mode.WITHER_ASH_GUARD, Mode.WITHER_BLACKENED_STRIKE, Mode.WITHER_DECAY_REFUND,
                         Mode.WITHER_SKULL_MEMORY, Mode.WITHER_SMOKE_STEP, Mode.WITHER_FATAL_MARK, Mode.WITHER_CINDER_PLATE),
            new Category("fallback", null,
                         Mode.FALLBACK_HEAL, Mode.FALLBACK_COOLDOWN, Mode.FALLBACK_NEXT_DAMAGE, Mode.FALLBACK_NEXT_GUARD,
                         Mode.FALLBACK_COST_REDUCE, Mode.FALLBACK_PROJECTILE_BOOST, Mode.FALLBACK_CLEANSE, Mode.FALLBACK_ABSORPTION)
    };

    private NarcissusFluidFeedbacks() {}

    public static void onProjectileHit(NarcissusFluidProjectile projectile, LivingEntity owner, Entity target, FluidStack fluid) {
        if (owner.level().isClientSide || fluid.isEmpty()){
            return;
        }
        ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fluid.getFluid());
        if (id == null){
            return;
        }
        ResolvedFluidFeedback feedback = resolveFeedback(fluid.getFluid(), id);
        Mode mode = feedback.mode();
        float quality = Mth.clamp(projectile.getPower(), 0.5f, 12.0f);
        mode.apply(new Context(owner, target, fluid, id, feedback.category(), quality));
    }

    public static void registerOverride(ResourceLocation fluidId, String category, Mode mode) {
        registerOverride(fluidId, new ResolvedFluidFeedback(category, mode));
    }

    public static void registerOverride(ResourceLocation fluidId, ResolvedFluidFeedback feedback) {
        OVERRIDES.put(Objects.requireNonNull(fluidId), Objects.requireNonNull(feedback));
        CACHE.clear();
    }

    @Nullable
    public static ResolvedFluidFeedback removeOverride(ResourceLocation fluidId) {
        ResolvedFluidFeedback removed = OVERRIDES.remove(fluidId);
        CACHE.clear();
        return removed;
    }

    public static void clearOverrides() {
        OVERRIDES.clear();
        CACHE.clear();
    }

    public static float applyProjectileCatalyst(Projectile projectile, float power) {
        CompoundTag data = projectile.getPersistentData();
        if (!data.contains(PROJECTILE_CATALYST)){
            return power;
        }
        float catalyst = Mth.clamp(data.getFloat(PROJECTILE_CATALYST), 0.0f, 8.0f);
        data.remove(PROJECTILE_CATALYST);
        return power * (1.0f + catalyst * 0.12f);
    }

    public static void consumeInventoryTick(LivingEntity holder, int modifierLevel) {
        clearExpired(holder, NEXT_SHOT);
        clearExpired(holder, GUARD);
        clearExpired(holder, MELEE);
        clearExpired(holder, CATALYST);
    }

    public static void consumeProjectileLaunch(LivingEntity shooter, Projectile projectile, int modifierLevel) {
        if (!(projectile instanceof NarcissusFluidProjectile)){
            return;
        }
        CompoundTag next = takePending(shooter, NEXT_SHOT);
        if (next != null){
            float strength = scale(next, modifierLevel);
            Mode mode = mode(next);
            float damageMul = projectileDamageMultiplier(mode, strength);
            float velocityMul = projectileVelocityMultiplier(mode, strength);
            projectile.setDeltaMovement(projectile.getDeltaMovement().scale(velocityMul));
            if (projectile instanceof ProjectileWithPower powerProjectile){
                powerProjectile.setPower(powerProjectile.getPower() * damageMul);
            }
        }
        CompoundTag catalyst = takePending(shooter, CATALYST);
        if (catalyst != null){
            projectile.getPersistentData().putFloat(PROJECTILE_CATALYST, scale(catalyst, modifierLevel));
        }
    }

    public static void consumeMeleeHit(ToolAttackContext context, int modifierLevel, float damageDealt) {
        LivingEntity attacker = context.getAttacker();
        CompoundTag tag = takePending(attacker, MELEE);
        if (tag == null){
            return;
        }
        Entity target = context.getTarget();
        float strength = scale(tag, modifierLevel);
        Mode mode = mode(tag);
        if (target instanceof LivingEntity livingTarget){
            switch (mode) {
                case FORGE_RIVET, SLIME_STICK, DROWNED_DRENCHED_STRIKE, DROWNED_SINKING_GUARD ->
                        livingTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration(strength, 40, 120), amplifier(strength, 0, 1)));
                case FORGE_EDGE -> attacker.removeEffect(MobEffects.DAMAGE_BOOST);
                case WITHER_BLACKENED_STRIKE, WITHER_FATAL_MARK -> {
                    if (livingTarget.getHealth() <= livingTarget.getMaxHealth() * 0.4f){
                        target.hurt(context.makeDamageSource(), 1.0f + strength * 0.45f);
                    }
                }
                default -> {}
            }
        }
        switch (mode) {
            case DROWNED_TIDE_PULL -> pullTarget(attacker, target, strength);
            case FORGE_RIVET, SLIME_ELASTIC_STRIKE, MASON_WEIGHT -> pushTarget(attacker, target, strength);
            case SHARD_EDGE, GLASS_NEEDLE, WITHER_BONE_EDGE, FALLBACK_NEXT_DAMAGE -> target.hurt(context.makeDamageSource(), 0.75f + strength * 0.35f);
            default -> {}
        }
    }

    public static float consumeGuardForDamage(LivingEntity defender, DamageSource source, float amount, int modifierLevel) {
        CompoundTag tag = takePending(defender, GUARD);
        if (tag == null){
            return amount;
        }
        float strength = scale(tag, modifierLevel);
        Mode mode = mode(tag);
        switch (mode) {
            case MASON_COUNTER -> {
                if (source.getEntity() instanceof LivingEntity attacker){
                    attacker.hurt(defender.damageSources().thorns(defender), 0.5f + strength * 0.25f);
                }
            }
            case MASON_WALL, SLIME_REBOUND_GUARD -> pushNearby(defender, 2.5 + strength * 0.15, 0.35 + strength * 0.03);
            case DROWNED_SINKING_GUARD -> {
                if (source.getEntity() instanceof LivingEntity attacker){
                    attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration(strength, 50, 120), amplifier(strength, 0, 1)));
                }
            }
            case WITHER_ASH_GUARD, MASON_PLASTER, PRISM_AEGIS, FORGE_BULWARK, FALLBACK_ABSORPTION -> addAbsorption(defender, strength);
            default -> {}
        }
        if (mode == Mode.SLIME_CUSHION && source.is(DamageTypes.FALL)){
            return amount * 0.2f;
        }
        if ((mode == Mode.WATER_SOAK_GUARD || mode == Mode.WITHER_CINDER_PLATE) &&
            (source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypeTags.IS_EXPLOSION) || source.getDirectEntity() instanceof Projectile)){
            return amount * guardMultiplier(strength, 0.35f);
        }
        if ((mode == Mode.GLASS_MIRROR || mode == Mode.GLASS_WARD) && source.getDirectEntity() instanceof Projectile){
            return amount * guardMultiplier(strength, 0.25f);
        }
        if ((mode == Mode.MASON_GUARD || mode == Mode.MASON_REINFORCE) && defender.onGround()){
            return amount * guardMultiplier(strength, 0.3f);
        }
        return amount * guardMultiplier(strength, 0.45f);
    }

    private static void instantEffect(LivingEntity owner, MobEffect effect, float strength, int baseDuration, int maxAmplifier) {
        owner.addEffect(new MobEffectInstance(effect, duration(strength, baseDuration, baseDuration * 3), amplifier(strength, 0, maxAmplifier)));
    }

    private static void cleanse(LivingEntity owner, int max) {
        int removed = 0;
        for (MobEffectInstance instance : owner.getActiveEffects().stream().toList()) {
            if (instance.getEffect().getCategory() == MobEffectCategory.HARMFUL){
                owner.removeEffect(instance.getEffect());
                if (++removed >= max){
                    return;
                }
            }
        }
    }

    private static void refundCooldown(LivingEntity owner) {
        if (owner instanceof Player player){
            player.getCooldowns().removeCooldown(player.getMainHandItem().getItem());
        }
    }

    private static void addAbsorption(LivingEntity owner, float strength) {
        owner.setAbsorptionAmount(Math.min(owner.getMaxHealth(), owner.getAbsorptionAmount() + 1.0f + strength * 0.45f));
    }

    private static void storePending(LivingEntity owner, String slot, String category, String mode, float strength, int duration, int charges) {
        CompoundTag root = owner.getPersistentData().getCompound(ROOT);
        CompoundTag tag = new CompoundTag();
        tag.putString(CATEGORY, category);
        tag.putString(MODE, mode);
        tag.putFloat(STRENGTH, strength);
        tag.putLong(EXPIRES, owner.level().getGameTime() + duration);
        tag.putInt(CHARGES, charges);
        root.put(slot, tag);
        owner.getPersistentData().put(ROOT, root);
    }

    @Nullable
    private static CompoundTag takePending(LivingEntity owner, String slot) {
        CompoundTag data = owner.getPersistentData();
        CompoundTag root = data.getCompound(ROOT);
        if (!root.contains(slot)){
            return null;
        }
        CompoundTag tag = root.getCompound(slot);
        if (tag.getLong(EXPIRES) < owner.level().getGameTime()){
            root.remove(slot);
            saveRoot(data, root);
            return null;
        }
        int charges = Math.max(1, tag.getInt(CHARGES));
        if (charges > 1){
            CompoundTag remaining = tag.copy();
            remaining.putInt(CHARGES, charges - 1);
            root.put(slot, remaining);
        }else {
            root.remove(slot);
        }
        saveRoot(data, root);
        return tag;
    }

    private static void clearExpired(LivingEntity owner, String slot) {
        CompoundTag data = owner.getPersistentData();
        CompoundTag root = data.getCompound(ROOT);
        if (root.contains(slot) && root.getCompound(slot).getLong(EXPIRES) < owner.level().getGameTime()){
            root.remove(slot);
            saveRoot(data, root);
        }
    }

    private static void saveRoot(CompoundTag data, CompoundTag root) {
        if (root.isEmpty()){
            data.remove(ROOT);
        }else {
            data.put(ROOT, root);
        }
    }

    private static float scale(CompoundTag tag, int modifierLevel) {
        return Mth.clamp(tag.getFloat(STRENGTH) * (0.75f + Math.max(1, modifierLevel) * 0.25f), 0.25f, 16.0f);
    }

    private static int duration(float strength, int base, int max) {
        return Mth.clamp(Math.round(base + strength * 12), base, max);
    }

    private static int amplifier(float strength, int base, int max) {
        return Mth.clamp(base + (int) (strength / 4.0f), base, max);
    }

    private static float guardMultiplier(float strength, float floor) {
        return Mth.clamp(1.0f - (0.18f + strength * 0.035f), floor, 0.9f);
    }

    private static float projectileDamageMultiplier(Mode mode, float strength) {
        return switch (mode) {
            case PRISM_CONDENSE -> 1.25f + strength * 0.08f;
            case PRISM_CRIT, GLASS_LENS, DROWNED_TRIDENT_MEMORY -> 1.15f + strength * 0.06f;
            case PRISM_DUPLICATE, GLASS_REFRACT, GLASS_ECHO, REAGENT_SPLASHBACK, SHARD_SPARK -> 1.0f + strength * 0.04f;
            default -> 1.0f + strength * 0.05f;
        };
    }

    private static float projectileVelocityMultiplier(Mode mode, float strength) {
        return switch (mode) {
            case GLASS_LENS, DROWNED_TRIDENT_MEMORY -> 1.18f + strength * 0.03f;
            case WITHER_SKULL_MEMORY -> 0.9f;
            case PRISM_CONDENSE -> 0.95f;
            default -> 1.0f + strength * 0.025f;
        };
    }

    private static void pullTarget(LivingEntity owner, Entity target, float strength) {
        Vec3 pull = owner.position().subtract(target.position()).normalize().scale(0.18 + strength * 0.025);
        target.push(pull.x, 0.02, pull.z);
    }

    private static void pushTarget(LivingEntity owner, Entity target, float strength) {
        Vec3 push = target.position().subtract(owner.position()).normalize().scale(0.35 + strength * 0.035);
        target.push(push.x, 0.08, push.z);
    }

    private static void pushNearby(LivingEntity owner, double radius, double strength) {
        AABB bounds = owner.getBoundingBox().inflate(radius);
        for (LivingEntity target : owner.level().getEntitiesOfClass(LivingEntity.class, bounds, entity -> entity != owner && entity.isAlive())) {
            Vec3 push = target.position().subtract(owner.position()).normalize().scale(strength);
            target.push(push.x, 0.08, push.z);
        }
    }

    private static void splash(Context context, float radius, float damage, MobEffect effect) {
        AABB bounds = context.target.getBoundingBox().inflate(radius);
        for (LivingEntity target : context.owner.level()
                                                .getEntitiesOfClass(LivingEntity.class, bounds, entity -> entity != context.owner && entity.isAlive())) {
            if (damage > 0){
                target.hurt(context.owner.damageSources().indirectMagic(context.owner, context.owner), damage);
            }
            target.addEffect(new MobEffectInstance(effect, duration(context.quality, 35, 90), 0));
        }
    }

    private static Mode mode(CompoundTag tag) {
        try {
            return Mode.valueOf(tag.getString(MODE).toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException ignored) {
            return Mode.FALLBACK_NEXT_DAMAGE;
        }
    }

    public static ResolvedFluidFeedback resolveFeedback(FluidStack fluid) {
        if (fluid.isEmpty()){
            return null;
        }
        ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fluid.getFluid());
        return id == null ? null : resolveFeedback(fluid.getFluid(), id);
    }

    public static ResolvedFluidFeedback resolveFeedback(Fluid fluid) {
        ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fluid);
        return id == null ? null : resolveFeedback(fluid, id);
    }

    public static ResolvedFluidFeedback resolveFeedback(Fluid fluid, ResourceLocation id) {
        ResolvedFluidFeedback override = OVERRIDES.get(id);
        if (override != null){
            return override;
        }
        return CACHE.computeIfAbsent(fluid, key -> resolveCategory(key).select(id));
    }

    public static FeedbackDisplay displayFor(Mode mode) {
        return switch (mode) {
            case FORGE_EDGE -> FeedbackDisplay.effect(mode, MobEffects.DAMAGE_BOOST, 20 * 6, 0);
            case FORGE_RIVET, SLIME_STICK, DROWNED_DRENCHED_STRIKE, DROWNED_SINKING_GUARD ->
                    FeedbackDisplay.effect(mode, MobEffects.MOVEMENT_SLOWDOWN, 20 * 4, 0);
            case SLIME_DRIFT -> FeedbackDisplay.effect(mode, MobEffects.SLOW_FALLING, 20 * 6, 0);
            case SHARD_STEP, GLASS_PRISM_STEP, WATER_FLOW, WITHER_SMOKE_STEP -> FeedbackDisplay.effect(mode, MobEffects.MOVEMENT_SPEED, 20 * 6, 0);
            case GLASS_SHATTER -> FeedbackDisplay.effect(mode, MobEffects.WEAKNESS, 20 * 5, 0);
            case REAGENT_OVERDOSE -> FeedbackDisplay.effects(mode,
                                                             new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 6, 0),
                                                             new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 6, 0),
                                                             new MobEffectInstance(MobEffects.DIG_SPEED, 20 * 6, 0));
            case DROWNED_BREATH -> FeedbackDisplay.effect(mode, MobEffects.WATER_BREATHING, 20 * 8, 0);
            case FALLBACK_ABSORPTION, FORGE_BULWARK, MASON_PLASTER, PRISM_AEGIS, WITHER_ASH_GUARD ->
                    FeedbackDisplay.effect(mode, MobEffects.ABSORPTION, 20 * 6, 0);
            default -> FeedbackDisplay.text(mode);
        };
    }

    public record FeedbackDisplay(Mode mode, List<MobEffectInstance> effects, Component text) {
        public static FeedbackDisplay text(Mode mode) {
            return new FeedbackDisplay(mode, List.of(), Component.translatable(modeKey(mode)));
        }

        public static FeedbackDisplay effect(Mode mode, MobEffect effect, int duration, int amplifier) {
            return new FeedbackDisplay(mode, List.of(new MobEffectInstance(effect, duration, amplifier)), Component.translatable(modeKey(mode)));
        }

        public static FeedbackDisplay effects(Mode mode, MobEffectInstance... effects) {
            return new FeedbackDisplay(mode, List.of(effects), Component.translatable(modeKey(mode)));
        }

        private static String modeKey(Mode mode) {
            return "jei.dreamtinker.narcissus_feedback.mode." + mode.name().toLowerCase(Locale.ROOT);
        }
    }

    private static Category resolveCategory(Fluid fluid) {
        return Arrays.stream(CATEGORIES).filter(category -> category.matches(fluid)).findFirst().orElse(CATEGORIES[CATEGORIES.length - 1]);
    }

    private static TagKey<Fluid> tconstructFluidTag(String path) {
        return TagKey.create(Registries.FLUID, new ResourceLocation("tconstruct", path));
    }

    public enum Mode {
        FORGE_BLADE(MELEE),
        FORGE_PLATE(GUARD),
        FORGE_BARREL(NEXT_SHOT),
        FORGE_RING(null),
        FORGE_RIVET(MELEE),
        FORGE_EDGE(MELEE),
        FORGE_BULWARK(null),
        FORGE_TEMPER(CATALYST),
        SLIME_BOUNCE(null),
        SLIME_CUSHION(GUARD),
        SLIME_RICOCHET(NEXT_SHOT),
        SLIME_STICK(MELEE),
        SLIME_SPLASH(null),
        SLIME_REBOUND_GUARD(GUARD),
        SLIME_ELASTIC_STRIKE(MELEE),
        SLIME_DRIFT(null),
        PRISM_AMPLIFY(CATALYST),
        PRISM_DUPLICATE(NEXT_SHOT),
        PRISM_FOCUS(NEXT_SHOT),
        PRISM_LOCK(NEXT_SHOT),
        PRISM_CRIT(NEXT_SHOT),
        PRISM_AEGIS(GUARD),
        PRISM_ECHO(CATALYST),
        PRISM_CONDENSE(NEXT_SHOT),
        SHARD_QUICKEN(null),
        SHARD_REFUND(CATALYST),
        SHARD_CHAIN(CATALYST),
        SHARD_SPARK(NEXT_SHOT),
        SHARD_MARK(NEXT_SHOT),
        SHARD_EDGE(MELEE),
        SHARD_STEP(null),
        SHARD_RELOAD(null),
        MASON_GUARD(GUARD),
        MASON_WALL(GUARD),
        MASON_WEIGHT(MELEE),
        MASON_SETTLE(null),
        MASON_ROOT(GUARD),
        MASON_COUNTER(GUARD),
        MASON_PLASTER(null),
        MASON_REINFORCE(GUARD),
        GLASS_REFRACT(NEXT_SHOT),
        GLASS_MIRROR(GUARD),
        GLASS_LENS(NEXT_SHOT),
        GLASS_SHATTER(null),
        GLASS_ECHO(CATALYST),
        GLASS_PRISM_STEP(null),
        GLASS_NEEDLE(MELEE),
        GLASS_WARD(GUARD),
        REAGENT_PURGE(null),
        REAGENT_CATALYZE(CATALYST),
        REAGENT_DISTILL(CATALYST),
        REAGENT_ADAPT(GUARD),
        REAGENT_OVERDOSE(null),
        REAGENT_STIMULATE(MELEE),
        REAGENT_SEAL(GUARD),
        REAGENT_SPLASHBACK(NEXT_SHOT),
        WATER_EXTINGUISH(null),
        WATER_CLEANSE(null),
        WATER_COOL(null),
        WATER_FLOW(null),
        WATER_WASH(null),
        WATER_RIPTIDE(NEXT_SHOT),
        WATER_SOAK_GUARD(GUARD),
        WATER_REFRESH(CATALYST),
        DROWNED_TIDE_PULL(MELEE),
        DROWNED_DRENCHED_STRIKE(MELEE),
        DROWNED_UNDERCURRENT(NEXT_SHOT),
        DROWNED_BREATH(null),
        DROWNED_TRIDENT_MEMORY(NEXT_SHOT),
        DROWNED_SINKING_GUARD(GUARD),
        DROWNED_WET_RELOAD(null),
        DROWNED_GRAVE_WATER(null),
        WITHER_BONE_EDGE(MELEE),
        WITHER_ASH_GUARD(GUARD),
        WITHER_BLACKENED_STRIKE(MELEE),
        WITHER_DECAY_REFUND(CATALYST),
        WITHER_SKULL_MEMORY(NEXT_SHOT),
        WITHER_SMOKE_STEP(null),
        WITHER_FATAL_MARK(MELEE),
        WITHER_CINDER_PLATE(GUARD),
        FALLBACK_HEAL(null),
        FALLBACK_COOLDOWN(null),
        FALLBACK_NEXT_DAMAGE(MELEE),
        FALLBACK_NEXT_GUARD(GUARD),
        FALLBACK_COST_REDUCE(CATALYST),
        FALLBACK_PROJECTILE_BOOST(NEXT_SHOT),
        FALLBACK_CLEANSE(null),
        FALLBACK_ABSORPTION(null);

        private final String pendingSlot;

        Mode(@Nullable String pendingSlot) {
            this.pendingSlot = pendingSlot;
        }

        void apply(Context context) {
            float strength = context.quality;
            if (pendingSlot != null){
                storePending(context.owner, pendingSlot, context.category, name().toLowerCase(Locale.ROOT), strength, duration(strength, 160, 500), charges());
                if (this == FORGE_EDGE){
                    instantEffect(context.owner, MobEffects.DAMAGE_BOOST, strength, 45, 0);
                }
                return;
            }
            switch (this) {
                case FORGE_RING, SHARD_QUICKEN, SHARD_RELOAD, MASON_SETTLE, WATER_COOL, DROWNED_WET_RELOAD, FALLBACK_COOLDOWN -> refundCooldown(context.owner);
                case FORGE_BULWARK, MASON_PLASTER, FALLBACK_ABSORPTION -> addAbsorption(context.owner, strength);
                case SLIME_BOUNCE -> context.owner.push(0, 0.25 + strength * 0.035, 0);
                case SLIME_SPLASH -> splash(context, 2.0f, 0, MobEffects.MOVEMENT_SLOWDOWN);
                case SLIME_DRIFT -> instantEffect(context.owner, MobEffects.SLOW_FALLING, strength, 45, 0);
                case SHARD_STEP, GLASS_PRISM_STEP, WATER_FLOW, WITHER_SMOKE_STEP -> instantEffect(context.owner, MobEffects.MOVEMENT_SPEED, strength, 45, 1);
                case GLASS_SHATTER -> splash(context, 2.0f, 0.75f + strength * 0.25f, MobEffects.WEAKNESS);
                case REAGENT_PURGE, WATER_CLEANSE, WATER_WASH, FALLBACK_CLEANSE -> cleanse(context.owner, Math.max(1, (int) (strength / 3)));
                case REAGENT_OVERDOSE -> {
                    instantEffect(context.owner, MobEffects.DAMAGE_BOOST, strength, 55, 1);
                    instantEffect(context.owner, MobEffects.MOVEMENT_SPEED, strength, 55, 1);
                    instantEffect(context.owner, MobEffects.DIG_SPEED, strength, 55, 1);
                }
                case WATER_EXTINGUISH -> context.owner.clearFire();
                case DROWNED_BREATH -> instantEffect(context.owner, MobEffects.WATER_BREATHING, strength, 100, 0);
                case DROWNED_GRAVE_WATER -> {
                    if (context.owner.getHealth() <= context.owner.getMaxHealth() * 0.35f){
                        context.owner.heal(1.0f + strength * 0.2f);
                        addAbsorption(context.owner, strength);
                    }
                }
                case FALLBACK_HEAL -> context.owner.heal(0.5f + strength * 0.15f);
                default -> instantEffect(context.owner, MobEffects.DIG_SPEED, strength, 55, 1);
            }
        }

        private int charges() {
            return switch (this) {
                case FORGE_RIVET, FORGE_TEMPER, SLIME_ELASTIC_STRIKE, MASON_REINFORCE, REAGENT_STIMULATE,
                     WATER_REFRESH, DROWNED_DRENCHED_STRIKE, SHARD_EDGE -> 2;
                default -> 1;
            };
        }
    }

    private record Context(LivingEntity owner, Entity target, FluidStack fluid, ResourceLocation fluidId, String category, float quality) {}

    public record ResolvedFluidFeedback(String category, Mode mode) {
        public ResolvedFluidFeedback {
            Objects.requireNonNull(category);
            Objects.requireNonNull(mode);
        }
    }

    private record Category(String name, @Nullable TagKey<Fluid>[] tags, Mode... modes) {
        boolean matches(Fluid fluid) {
            if (tags == null){
                return true;
            }
            return ForgeRegistries.FLUIDS.getHolder(fluid).map(holder -> Arrays.stream(tags).anyMatch(holder::is)).orElse(false);
        }

        ResolvedFluidFeedback select(ResourceLocation id) {
            return new ResolvedFluidFeedback(name, modes[Math.floorMod(id.toString().hashCode(), modes.length)]);
        }
    }
}
