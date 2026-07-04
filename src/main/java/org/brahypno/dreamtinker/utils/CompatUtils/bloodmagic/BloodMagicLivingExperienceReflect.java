package org.brahypno.dreamtinker.utils.CompatUtils.bloodmagic;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;

public final class BloodMagicLivingExperienceReflect {
    private static final String BLOOD_MAGIC_MODID = "bloodmagic";

    private static final String LIVING_UTIL = "wayoftime.bloodmagic.core.living.LivingUtil";
    private static final String LIVING_UPGRADE = "wayoftime.bloodmagic.core.living.LivingUpgrade";
    private static final String LIVING_ARMOR_REGISTRAR = "wayoftime.bloodmagic.core.LivingArmorRegistrar";
    private static final String APACHE_PAIR = "org.apache.commons.lang3.tuple.Pair";

    public static final ResourceLocation ARROW_PROTECT = bm("arrow_protect");
    public static final ResourceLocation FALL_PROTECT = bm("fall_protect");
    public static final ResourceLocation PHYSICAL_PROTECT = bm("physical_protect");
    public static final ResourceLocation JUMP = bm("jump");
    public static final ResourceLocation HEALTH = bm("health");
    public static final ResourceLocation EXPERIENCED = bm("experienced");
    public static final ResourceLocation SPRINT_ATTACK = bm("sprint_attack");
    public static final ResourceLocation SELF_SACRIFICE = bm("self_sacrifice");
    public static final ResourceLocation SPEED = bm("speed");
    public static final ResourceLocation POISON_RESIST = bm("poison_resist");
    public static final ResourceLocation FIRE_RESIST = bm("fire_resist");
    public static final ResourceLocation DIGGING = bm("digging");
    public static final ResourceLocation KNOCKBACK_RESIST = bm("knockback_resist");
    public static final ResourceLocation DIAMOND_PROTECT = bm("diamond_protect");
    public static final ResourceLocation ELYTRA = bm("elytra");
    public static final ResourceLocation CURIOS_SOCKET = bm("curios_socket");
    public static final ResourceLocation MELEE_DAMAGE = bm("melee_damage");
    public static final ResourceLocation REPAIR = bm("repair");
    public static final ResourceLocation GILDED = bm("gilded");

    public static final ResourceLocation DOWNGRADE_QUENCHED = bm("downgrade/quenched");
    public static final ResourceLocation DOWNGRADE_STORM_TROOPER = bm("downgrade/storm_trooper");
    public static final ResourceLocation DOWNGRADE_BATTLE_HUNGRY = bm("downgrade/battle_hungry");
    public static final ResourceLocation DOWNGRADE_MELEE_DECREASE = bm("downgrade/melee_decrease");
    public static final ResourceLocation DOWNGRADE_DIG_SLOWDOWN = bm("downgrade/dig_slowdown");
    public static final ResourceLocation DOWNGRADE_SLOW_HEAL = bm("downgrade/slow_heal");
    public static final ResourceLocation DOWNGRADE_CRIPPLED_ARM = bm("downgrade/crippled_arm");
    public static final ResourceLocation DOWNGRADE_SWIM_DECREASE = bm("downgrade/swim_decrease");
    public static final ResourceLocation DOWNGRADE_SPEED_DECREASE = bm("downgrade/speed_decrease");

    private static volatile boolean initialized;
    private static volatile boolean available;

    private static Class<?> livingUpgradeClass;
    private static MethodHandle upgradeMapGetter;
    private static MethodHandle applyNewExperience;
    private static MethodHandle applyExperienceToUpgradeCap;
    private static MethodHandle pairGetLeft;
    private static MethodHandle pairGetRight;

    private BloodMagicLivingExperienceReflect() {}

    public static boolean isLoaded() {
        return ModList.get().isLoaded(BLOOD_MAGIC_MODID);
    }

    public static ExperienceResult applyNewExperience(Player player, ResourceLocation upgradeId, double experience) {
        if (player == null || upgradeId == null || experience <= 0 || !ensureReady()){
            return ExperienceResult.empty();
        }

        try {
            Object upgrade = getUpgrade(upgradeId);
            if (upgrade == null){
                return ExperienceResult.empty();
            }

            Object pair = applyNewExperience.invoke(player, upgrade, experience);
            Object stats = pairGetLeft.invoke(pair);
            Object didUpgrade = pairGetRight.invoke(pair);

            return new ExperienceResult(stats, didUpgrade instanceof Boolean value && value, experience);
        }
        catch (Throwable ignored) {
            return ExperienceResult.empty();
        }
    }

    public static CapResult applyExperienceToUpgradeCap(Player player, ResourceLocation upgradeId, double experience) {
        if (player == null || upgradeId == null || experience <= 0 || !ensureReady()){
            return CapResult.empty();
        }

        try {
            Object upgrade = getUpgrade(upgradeId);
            if (upgrade == null){
                return CapResult.empty();
            }

            Object pair = applyExperienceToUpgradeCap.invoke(player, upgrade, experience);
            Object stats = pairGetLeft.invoke(pair);
            Object applied = pairGetRight.invoke(pair);

            double appliedExperience = applied instanceof Number number ? number.doubleValue() : 0;
            return new CapResult(stats, appliedExperience);
        }
        catch (Throwable ignored) {
            return CapResult.empty();
        }
    }

    public static Object getUpgrade(ResourceLocation upgradeId) {
        if (upgradeId == null || !ensureReady()){
            return null;
        }

        try {
            Object raw = upgradeMapGetter.invoke();
            if (!(raw instanceof Map<?, ?> map)){
                return null;
            }

            Object upgrade = map.get(upgradeId);
            return livingUpgradeClass.isInstance(upgrade) ? upgrade : null;
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean ensureReady() {
        if (initialized){
            return available;
        }

        synchronized(BloodMagicLivingExperienceReflect.class) {
            if (initialized){
                return available;
            }

            available = initHandles();
            initialized = true;
            return available;
        }
    }

    private static boolean initHandles() {
        if (!isLoaded()){
            return false;
        }

        try {
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();

            Class<?> livingUtilClass = Class.forName(LIVING_UTIL);
            livingUpgradeClass = Class.forName(LIVING_UPGRADE);
            Class<?> livingArmorRegistrarClass = Class.forName(LIVING_ARMOR_REGISTRAR);
            Class<?> pairClass = Class.forName(APACHE_PAIR);

            upgradeMapGetter = lookup.findStaticGetter(livingArmorRegistrarClass, "UPGRADE_MAP", Map.class);

            applyNewExperience = lookup.findStatic(
                    livingUtilClass,
                    "applyNewExperience",
                    MethodType.methodType(pairClass, Player.class, livingUpgradeClass, double.class)
            );

            applyExperienceToUpgradeCap = lookup.findStatic(
                    livingUtilClass,
                    "applyExperienceToUpgradeCap",
                    MethodType.methodType(pairClass, Player.class, livingUpgradeClass, double.class)
            );

            pairGetLeft = lookup.findVirtual(pairClass, "getLeft", MethodType.methodType(Object.class));
            pairGetRight = lookup.findVirtual(pairClass, "getRight", MethodType.methodType(Object.class));

            return true;
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    private static ResourceLocation bm(String path) {
        return new ResourceLocation(BLOOD_MAGIC_MODID, path);
    }

    public record ExperienceResult(Object stats, boolean didUpgrade, double attemptedExperience) {
        public static ExperienceResult empty() {
            return new ExperienceResult(null, false, 0);
        }

        public boolean applied() {
            return stats != null;
        }
    }

    public record CapResult(Object stats, double appliedExperience) {
        public static CapResult empty() {
            return new CapResult(null, 0);
        }

        public boolean applied() {
            return stats != null && appliedExperience > 0;
        }
    }
}