package org.brahypno.dreamtinker.utils.CompatUtils.bloodmagic;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.brahypno.dreamtinker.Dreamtinker;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import wayoftime.bloodmagic.core.living.ILivingContainer;
import wayoftime.bloodmagic.core.living.LivingStats;
import wayoftime.bloodmagic.core.living.LivingUpgrade;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import static org.brahypno.dreamtinker.tools.DreamtinkerModifiers.living_armor;

/** Keeps optional Blood Magic types behind a mod-presence check and a separate implementation class. */
public final class BloodMagicTconLivingStats {
    private static final String BLOOD_MAGIC_MODID = "bloodmagic";
    public static final ResourceLocation LIVING_STATS_KEY = new ResourceLocation("dreamtinker", "blood_magic_living_stats");
    private static final AtomicBoolean FAILURE_REPORTED = new AtomicBoolean();

    private BloodMagicTconLivingStats() {}

    public static boolean isBloodMagicLoaded() {
        return ModList.get().isLoaded(BLOOD_MAGIC_MODID);
    }

    public static boolean hasTconLivingSet(Player player) {
        return player != null && isLivingTconArmor(player.getItemBySlot(EquipmentSlot.CHEST), EquipmentSlot.CHEST);
    }

    public static Object readStats(Player player) {
        return isBloodMagicLoaded() && hasTconLivingSet(player) ? readStats(player.getItemBySlot(EquipmentSlot.CHEST)) : null;
    }

    public static Object readStats(ItemStack stack) {
        if (!isBloodMagicLoaded() || !isLivingTconChest(stack))
            return null;
        try {
            return readStats(ToolStack.from(stack).getPersistentData());
        } catch (RuntimeException exception) {
            reportFailure("read item stats", exception);
            return null;
        }
    }

    public static Object readStats(IToolStackView tool) {
        return isBloodMagicLoaded() && tool != null ? readStats(tool.getPersistentData()) : null;
    }

    public static Object readStats(IModDataView data) {
        if (!isBloodMagicLoaded() || data == null)
            return null;
        try {
            CompoundTag statsTag = data.getCompound(LIVING_STATS_KEY);
            return statsTag == null || statsTag.isEmpty() ? null : Loaded.readStats(statsTag);
        } catch (RuntimeException exception) {
            reportFailure("read persistent stats", exception);
            return null;
        }
    }

    public static void appendLivingTooltip(IToolStackView tool, List<Component> tooltip, boolean trainable) {
        if (!isBloodMagicLoaded() || tool == null || tooltip == null)
            return;
        try {
            Object stats = readStats(tool);
            Loaded.appendTooltip(stats == null ? Loaded.createDefaultStats() : stats, tooltip, trainable);
        } catch (RuntimeException exception) {
            reportFailure("build living tooltip", exception);
        }
    }

    public static Object createDefaultStats() {
        return isBloodMagicLoaded() ? Loaded.createDefaultStats() : null;
    }

    public static void writeStats(Player player, Object stats) {
        if (isBloodMagicLoaded() && hasTconLivingSet(player))
            writeStats(player.getItemBySlot(EquipmentSlot.CHEST), stats);
    }

    public static void writeStats(ItemStack stack, Object stats) {
        if (!isBloodMagicLoaded() || !isLivingTconChest(stack))
            return;
        try {
            // Serialize before modifying persistent data, so a bad value leaves the previous stats intact.
            CompoundTag serialized = stats == null ? null : Loaded.serialize(stats);
            ToolStack tool = ToolStack.from(stack);
            if (serialized == null)
                tool.getPersistentData().remove(LIVING_STATS_KEY);
            else
                tool.getPersistentData().put(LIVING_STATS_KEY, serialized);
            tool.updateStack(stack);
        } catch (RuntimeException exception) {
            reportFailure("write item stats", exception);
        }
    }

    public static boolean isLivingTconChest(ItemStack stack) {
        return isLivingTconArmor(stack, EquipmentSlot.CHEST);
    }

    public static boolean isLivingTconArmor(ItemStack stack, EquipmentSlot slot) {
        return stack != null && !stack.isEmpty() && isExpectedArmorSlot(stack, slot)
               && ModifierUtil.getModifierLevel(stack, living_armor.getId()) > 0;
    }

    public static boolean isBloodMagicLivingContainer(ItemStack stack) {
        return isBloodMagicLoaded() && stack != null && !stack.isEmpty() && Loaded.isLivingContainer(stack);
    }

    private static boolean isExpectedArmorSlot(ItemStack stack, EquipmentSlot slot) {
        if (!(stack.getItem() instanceof ArmorItem armorItem))
            return false;
        return switch (armorItem.getType()) {
            case HELMET -> slot == EquipmentSlot.HEAD;
            case CHESTPLATE -> slot == EquipmentSlot.CHEST;
            case LEGGINGS -> slot == EquipmentSlot.LEGS;
            case BOOTS -> slot == EquipmentSlot.FEET;
        };
    }

    public static boolean hasElytraUpgrade(IModDataView data) {
        return hasUpgradeLevel(data, BloodMagicLivingExperienceReflect.ELYTRA, 1);
    }

    public static boolean hasGildedUpgrade(IModDataView data) {
        return hasUpgradeLevel(data, BloodMagicLivingExperienceReflect.GILDED, 1);
    }

    public static boolean hasUpgradeLevel(IModDataView data, ResourceLocation upgradeId, int minLevel) {
        if (!isBloodMagicLoaded() || data == null || upgradeId == null)
            return false;
        try {
            Object stats = readStats(data);
            return stats != null && Loaded.hasUpgradeLevel(stats, upgradeId, minLevel);
        } catch (RuntimeException exception) {
            reportFailure("read living upgrade", exception);
            return false;
        }
    }

    public static void addLivingAttributes(IToolStackView tool, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        if (!isBloodMagicLoaded() || tool == null || consumer == null || slot != EquipmentSlot.CHEST)
            return;
        try {
            Object stats = readStats(tool);
            if (stats != null)
                Loaded.addLivingAttributes(stats, consumer);
        } catch (RuntimeException exception) {
            reportFailure("apply living attributes", exception);
        }
    }

    private static void reportFailure(String operation, RuntimeException exception) {
        // Report the problem once without disabling other items, players, or later attempts.
        if (FAILURE_REPORTED.compareAndSet(false, true))
            Dreamtinker.LOGGER.warn("Failed to {}; later Blood Magic operations remain enabled", operation, exception);
    }

    private static final class Loaded {
        private static Object readStats(CompoundTag tag) {
            return LivingStats.fromNBT(tag);
        }

        private static Object createDefaultStats() {
            return new LivingStats();
        }

        private static CompoundTag serialize(Object stats) {
            return ((LivingStats) stats).serialize();
        }

        private static void appendTooltip(Object stats, List<Component> tooltip, boolean trainable) {
            ILivingContainer.appendLivingTooltip(ItemStack.EMPTY, (LivingStats) stats, tooltip, trainable);
        }

        private static boolean isLivingContainer(ItemStack stack) {
            return stack.getItem() instanceof ILivingContainer;
        }

        private static boolean hasUpgradeLevel(Object value, ResourceLocation upgradeId, int minLevel) {
            LivingStats stats = (LivingStats) value;
            for (Map.Entry<LivingUpgrade, Double> entry : stats.getUpgrades().entrySet()) {
                LivingUpgrade upgrade = entry.getKey();
                Double experience = entry.getValue();
                if (upgrade != null && experience != null && upgradeId.equals(upgrade.getKey()))
                    return upgrade.getLevel(experience.intValue()) >= minLevel;
            }
            return false;
        }

        private static void addLivingAttributes(Object value, BiConsumer<Attribute, AttributeModifier> consumer) {
            LivingStats stats = (LivingStats) value;
            Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
            for (Map.Entry<LivingUpgrade, Double> entry : stats.getUpgrades().entrySet()) {
                LivingUpgrade upgrade = entry.getKey();
                Double experience = entry.getValue();
                if (upgrade == null || experience == null)
                    continue;
                LivingUpgrade.IAttributeProvider provider = upgrade.getAttributeProvider();
                if (provider == null)
                    continue;
                int level = upgrade.getLevel(experience.intValue());
                if (level <= 0)
                    continue;
                UUID uuid = UUID.nameUUIDFromBytes(upgrade.getKey().toString().getBytes(StandardCharsets.UTF_8));
                provider.handleAttributes(stats, modifiers, uuid, upgrade, level);
            }
            modifiers.forEach(consumer);
        }
    }
}
