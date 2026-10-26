package org.brahypno.dreamtinker.utils.CompatUtils.bloodmagic;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import org.brahypno.dreamtinker.Dreamtinker;
import wayoftime.bloodmagic.core.LivingArmorRegistrar;
import wayoftime.bloodmagic.core.living.LivingUpgrade;
import wayoftime.bloodmagic.core.living.LivingUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/** Optional Blood Magic facade; external types stay in the implementation class. */
public final class BloodMagicLivingExperienceReflect {
    private static final String BLOOD_MAGIC_MODID = "bloodmagic";
    private static final AtomicBoolean FAILURE_REPORTED = new AtomicBoolean();

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

    private BloodMagicLivingExperienceReflect() {}

    public static boolean isLoaded() {
        return ModList.get().isLoaded(BLOOD_MAGIC_MODID);
    }

    public static ExperienceResult applyNewExperience(Player player, ResourceLocation upgradeId, double experience) {
        if (player == null || upgradeId == null || experience <= 0 || !isLoaded())
            return ExperienceResult.empty();
        try {
            return Loaded.applyNewExperience(player, upgradeId, experience);
        } catch (RuntimeException exception) {
            reportFailure(exception);
            return ExperienceResult.empty();
        }
    }

    public static CapResult applyExperienceToUpgradeCap(Player player, ResourceLocation upgradeId, double experience) {
        if (player == null || upgradeId == null || experience <= 0 || !isLoaded())
            return CapResult.empty();
        try {
            return Loaded.applyExperienceToUpgradeCap(player, upgradeId, experience);
        } catch (RuntimeException exception) {
            reportFailure(exception);
            return CapResult.empty();
        }
    }

    public static Object getUpgrade(ResourceLocation upgradeId) {
        return upgradeId != null && isLoaded() ? Loaded.getUpgrade(upgradeId) : null;
    }

    private static void reportFailure(RuntimeException exception) {
        if (FAILURE_REPORTED.compareAndSet(false, true))
            Dreamtinker.LOGGER.warn("Failed to apply Blood Magic living experience; later attempts remain enabled", exception);
    }

    private static ResourceLocation bm(String path) {
        return new ResourceLocation(BLOOD_MAGIC_MODID, path);
    }

    private static final class Loaded {
        private static Object getUpgrade(ResourceLocation upgradeId) {
            return LivingArmorRegistrar.UPGRADE_MAP.get(upgradeId);
        }

        private static ExperienceResult applyNewExperience(Player player, ResourceLocation upgradeId, double experience) {
            LivingUpgrade upgrade = LivingArmorRegistrar.UPGRADE_MAP.get(upgradeId);
            if (upgrade == null)
                return ExperienceResult.empty();
            var result = LivingUtil.applyNewExperience(player, upgrade, experience);
            return new ExperienceResult(result.getLeft(), Boolean.TRUE.equals(result.getRight()), experience);
        }

        private static CapResult applyExperienceToUpgradeCap(Player player, ResourceLocation upgradeId, double experience) {
            LivingUpgrade upgrade = LivingArmorRegistrar.UPGRADE_MAP.get(upgradeId);
            if (upgrade == null)
                return CapResult.empty();
            var result = LivingUtil.applyExperienceToUpgradeCap(player, upgrade, experience);
            Double applied = result.getRight();
            return new CapResult(result.getLeft(), applied != null ? applied : 0);
        }
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
