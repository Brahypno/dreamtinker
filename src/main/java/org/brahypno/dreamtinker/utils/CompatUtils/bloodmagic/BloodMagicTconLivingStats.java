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
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.brahypno.dreamtinker.tools.DreamtinkerModifiers.living_armor;

public final class BloodMagicTconLivingStats {
    private static final String BLOOD_MAGIC_MODID = "bloodmagic";
    private static final String LIVING_STATS_CLASS = "wayoftime.bloodmagic.core.living.LivingStats";
    private static final String LIVING_CONTAINER_CLASS = "wayoftime.bloodmagic.core.living.ILivingContainer";

    /**
     * 存在 TCon ToolStack persistentData 里的 key。
     * 不建议直接叫 livingStats，避免和 Blood Magic 原 ItemStack 根 NBT key 混淆。
     */
    public static final ResourceLocation LIVING_STATS_KEY =
            new ResourceLocation("dreamtinker", "blood_magic_living_stats");

    private static volatile boolean failed;
    private static volatile Class<?> livingStatsClass;
    private static volatile Class<?> livingContainerClass;
    private static volatile MethodHandle livingStatsFromNbt;
    private static volatile MethodHandle livingStatsSerialize;
    private static volatile MethodHandle livingStatsConstructor;
    private static volatile MethodHandle appendLivingTooltip;
    private static final String LIVING_UPGRADE_CLASS =
            "wayoftime.bloodmagic.core.living.LivingUpgrade";
    private static final String ATTRIBUTE_PROVIDER_CLASS =
            "wayoftime.bloodmagic.core.living.LivingUpgrade$IAttributeProvider";

    private static volatile Class<?> livingUpgradeClass;
    private static volatile Class<?> attributeProviderClass;

    private static volatile MethodHandle livingStatsGetUpgrades;
    private static volatile MethodHandle livingUpgradeGetAttributeProvider;
    private static volatile MethodHandle livingUpgradeGetKey;
    private static volatile MethodHandle livingUpgradeGetLevel;
    private static volatile MethodHandle attributeProviderHandleAttributes;

    private BloodMagicTconLivingStats() {}

    public static boolean isBloodMagicLoaded() {
        return ModList.get().isLoaded(BLOOD_MAGIC_MODID);
    }

    public static boolean hasTconLivingSet(Player player) {
        if (player == null){
            return false;
        }

        return isLivingTconArmor(player.getItemBySlot(EquipmentSlot.CHEST), EquipmentSlot.CHEST);
        //&& isLivingTconArmor(player.getItemBySlot(EquipmentSlot.HEAD), EquipmentSlot.HEAD)
        //&& isLivingTconArmor(player.getItemBySlot(EquipmentSlot.LEGS), EquipmentSlot.LEGS)
        //&& isLivingTconArmor(player.getItemBySlot(EquipmentSlot.FEET), EquipmentSlot.FEET);
    }

    /**
     * 返回 Object，真实类型是 Blood Magic 的 LivingStats。
     */
    public static Object readStats(Player player) {
        if (!isBloodMagicLoaded() || failed || !hasTconLivingSet(player)){
            return null;
        }

        return readStats(player.getItemBySlot(EquipmentSlot.CHEST));
    }

    /**
     * 返回 Object，真实类型是 Blood Magic 的 LivingStats。
     */
    public static Object readStats(ItemStack stack) {
        if (!isBloodMagicLoaded() || failed || !isLivingTconChest(stack)){
            return null;
        }

        try {
            initLivingStatsReflection();

            ToolStack tool = ToolStack.from(stack);
            CompoundTag statsTag = tool.getPersistentData().getCompound(LIVING_STATS_KEY);

            if (statsTag == null || statsTag.isEmpty()){
                return null;
            }

            return livingStatsFromNbt.invoke(statsTag);
        }
        catch (Throwable ignored) {
            failed = true;
            return null;
        }
    }

    /**
     * 从 TCon tool view 直接读取 Blood Magic LivingStats。
     * 返回 Object，真实类型是 Blood Magic 的 LivingStats。
     */
    public static Object readStats(IToolStackView tool) {
        if (!isBloodMagicLoaded() || failed || tool == null){
            return null;
        }

        try {
            initLivingStatsReflection();

            CompoundTag statsTag = tool.getPersistentData().getCompound(LIVING_STATS_KEY);
            if (statsTag == null || statsTag.isEmpty()){
                return null;
            }

            return livingStatsFromNbt.invoke(statsTag);
        }
        catch (Throwable ignored) {
            failed = true;
            return null;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void appendLivingTooltip(IToolStackView tool, List<Component> tooltip, boolean trainable) {
        if (!isBloodMagicLoaded() || failed || tool == null || tooltip == null){
            return;
        }

        try {
            initLivingStatsReflection();
            initLivingContainerReflection();

            Object stats = readStats(tool);
            if (stats == null){
                stats = createDefaultStats();
            }

            if (stats != null){
                appendLivingTooltip.invoke(ItemStack.EMPTY, stats, tooltip, trainable);
            }
        }
        catch (Throwable ignored) {
            failed = true;
        }
    }

    /**
     * 创建 Blood Magic 的 LivingStats。
     * 返回 Object，真实类型是 Blood Magic 的 LivingStats。
     */
    public static Object createDefaultStats() {
        if (!isBloodMagicLoaded() || failed){
            return null;
        }

        try {
            initLivingStatsReflection();
            return livingStatsConstructor.invoke();
        }
        catch (Throwable ignored) {
            failed = true;
            return null;
        }
    }

    /**
     * stats 真实类型必须是 Blood Magic 的 LivingStats。
     */
    public static void writeStats(Player player, Object stats) {
        if (!isBloodMagicLoaded() || failed || !hasTconLivingSet(player)){
            return;
        }

        writeStats(player.getItemBySlot(EquipmentSlot.CHEST), stats);
    }

    /**
     * stats 真实类型必须是 Blood Magic 的 LivingStats。
     */
    public static void writeStats(ItemStack stack, Object stats) {
        if (!isBloodMagicLoaded() || failed || !isLivingTconChest(stack)){
            return;
        }

        try {
            initLivingStatsReflection();

            ToolStack tool = ToolStack.from(stack);

            if (stats == null){
                tool.getPersistentData().remove(LIVING_STATS_KEY);
            }else {
                CompoundTag serialized = (CompoundTag) livingStatsSerialize.invoke(stats);
                tool.getPersistentData().put(LIVING_STATS_KEY, serialized);
            }

            tool.updateStack(stack);
        }
        catch (Throwable ignored) {
            failed = true;
        }
    }

    public static boolean isLivingTconChest(ItemStack stack) {
        return isLivingTconArmor(stack, EquipmentSlot.CHEST);
    }

    public static boolean isLivingTconArmor(ItemStack stack, EquipmentSlot slot) {
        if (stack == null || stack.isEmpty()){
            return false;
        }

        if (!isExpectedArmorSlot(stack, slot)){
            return false;
        }

        return 0 < ModifierUtil.getModifierLevel(stack, living_armor.getId());
    }

    public static boolean isBloodMagicLivingContainer(ItemStack stack) {
        if (!isBloodMagicLoaded() || stack == null || stack.isEmpty()){
            return false;
        }

        try {
            initLivingContainerReflection();
            return livingContainerClass.isInstance(stack.getItem());
        }
        catch (Throwable ignored) {
            return false;
        }
    }


    private static boolean isExpectedArmorSlot(ItemStack stack, EquipmentSlot slot) {
        if (!(stack.getItem() instanceof ArmorItem armorItem)){
            return false;
        }

        return switch (armorItem.getType()) {
            case HELMET -> slot == EquipmentSlot.HEAD;
            case CHESTPLATE -> slot == EquipmentSlot.CHEST;
            case LEGGINGS -> slot == EquipmentSlot.LEGS;
            case BOOTS -> slot == EquipmentSlot.FEET;
        };
    }

    private static void initLivingStatsReflection() throws ReflectiveOperationException {
        if (livingStatsClass != null
            && livingStatsFromNbt != null
            && livingStatsSerialize != null
            && livingStatsConstructor != null){
            return;
        }

        synchronized(BloodMagicTconLivingStats.class) {
            if (livingStatsClass != null
                && livingStatsFromNbt != null
                && livingStatsSerialize != null
                && livingStatsConstructor != null){
                return;
            }

            MethodHandles.Lookup lookup = MethodHandles.publicLookup();

            livingStatsClass = Class.forName(LIVING_STATS_CLASS);

            livingStatsFromNbt = lookup.findStatic(
                    livingStatsClass,
                    "fromNBT",
                    MethodType.methodType(livingStatsClass, CompoundTag.class)
            );

            livingStatsSerialize = lookup.findVirtual(
                    livingStatsClass,
                    "serialize",
                    MethodType.methodType(CompoundTag.class)
            );

            livingStatsConstructor = lookup.findConstructor(
                    livingStatsClass,
                    MethodType.methodType(void.class)
            );
        }
    }

    /**
     * 从 TCon persistentData view 直接读取 Blood Magic LivingStats。
     * 返回 Object，真实类型是 Blood Magic 的 LivingStats。
     */
    public static Object readStats(IModDataView data) {
        if (!isBloodMagicLoaded() || failed || data == null){
            return null;
        }

        try {
            initLivingStatsReflection();

            CompoundTag statsTag = data.getCompound(LIVING_STATS_KEY);
            if (statsTag == null || statsTag.isEmpty()){
                return null;
            }

            return livingStatsFromNbt.invoke(statsTag);
        }
        catch (Throwable ignored) {
            failed = true;
            return null;
        }
    }

    public static boolean hasElytraUpgrade(IModDataView data) {
        return hasUpgradeLevel(data, new ResourceLocation(BLOOD_MAGIC_MODID, "elytra"), 1);
    }

    public static boolean hasGildedUpgrade(IModDataView data) {
        return hasUpgradeLevel(data, new ResourceLocation(BLOOD_MAGIC_MODID, "gilded"), 1);
    }

    public static boolean hasUpgradeLevel(IModDataView data, ResourceLocation upgradeId, int minLevel) {
        if (!isBloodMagicLoaded() || failed || data == null || upgradeId == null){
            return false;
        }

        try {
            initLivingAttributeReflection();

            Object stats = readStats(data);
            if (stats == null){
                return false;
            }

            Object rawUpgrades = livingStatsGetUpgrades.invoke(stats);
            if (!(rawUpgrades instanceof Map<?, ?> upgrades) || upgrades.isEmpty()){
                return false;
            }

            for (Map.Entry<?, ?> entry : upgrades.entrySet()) {
                Object upgrade = entry.getKey();
                Object experience = entry.getValue();

                if (!livingUpgradeClass.isInstance(upgrade) || !(experience instanceof Number number)){
                    continue;
                }

                ResourceLocation key = (ResourceLocation) livingUpgradeGetKey.invoke(upgrade);
                if (!upgradeId.equals(key)){
                    continue;
                }

                int level = (int) livingUpgradeGetLevel.invoke(upgrade, number.intValue());
                return level >= minLevel;
            }

            return false;
        }
        catch (Throwable ignored) {
            failed = true;
            return false;
        }
    }

    private static void initLivingContainerReflection() throws ReflectiveOperationException {
        if (livingContainerClass != null && appendLivingTooltip != null){
            return;
        }

        synchronized(BloodMagicTconLivingStats.class) {
            if (livingContainerClass != null && appendLivingTooltip != null){
                return;
            }

            initLivingStatsReflection();

            MethodHandles.Lookup lookup = MethodHandles.publicLookup();

            livingContainerClass = Class.forName(LIVING_CONTAINER_CLASS);

            appendLivingTooltip = lookup.findStatic(
                    livingContainerClass,
                    "appendLivingTooltip",
                    MethodType.methodType(void.class, ItemStack.class, livingStatsClass, List.class, boolean.class)
            );
        }
    }

    public static void addLivingAttributes(
            IToolStackView tool,
            EquipmentSlot slot,
            BiConsumer<Attribute, AttributeModifier> consumer
    ) {
        if (!isBloodMagicLoaded() || failed || tool == null || consumer == null){
            return;
        }

        // 对齐 Blood Magic 原胸甲逻辑：attribute provider 只在 CHEST slot 生效。
        if (slot != EquipmentSlot.CHEST){
            return;
        }

        try {
            initLivingAttributeReflection();

            Object stats = readStats(tool);
            if (stats == null){
                return;
            }

            Object rawUpgrades = livingStatsGetUpgrades.invoke(stats);
            if (!(rawUpgrades instanceof Map<?, ?> upgrades) || upgrades.isEmpty()){
                return;
            }

            Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();

            for (Map.Entry<?, ?> entry : upgrades.entrySet()) {
                Object upgrade = entry.getKey();
                Object experience = entry.getValue();

                if (!livingUpgradeClass.isInstance(upgrade) || !(experience instanceof Number number)){
                    continue;
                }

                Object provider = livingUpgradeGetAttributeProvider.invoke(upgrade);
                if (!attributeProviderClass.isInstance(provider)){
                    continue;
                }

                int level = (int) livingUpgradeGetLevel.invoke(upgrade, number.intValue());
                if (level <= 0){
                    continue;
                }

                ResourceLocation key = (ResourceLocation) livingUpgradeGetKey.invoke(upgrade);
                UUID uuid = UUID.nameUUIDFromBytes(key.toString().getBytes(StandardCharsets.UTF_8));

                attributeProviderHandleAttributes.invoke(provider, stats, modifiers, uuid, upgrade, level);
            }

            modifiers.forEach(consumer);
        }
        catch (Throwable ignored) {
            failed = true;
        }
    }

    private static void initLivingAttributeReflection() throws ReflectiveOperationException {
        if (livingUpgradeClass != null
            && attributeProviderClass != null
            && livingStatsGetUpgrades != null
            && livingUpgradeGetAttributeProvider != null
            && livingUpgradeGetKey != null
            && livingUpgradeGetLevel != null
            && attributeProviderHandleAttributes != null){
            return;
        }

        synchronized(BloodMagicTconLivingStats.class) {
            if (livingUpgradeClass != null
                && attributeProviderClass != null
                && livingStatsGetUpgrades != null
                && livingUpgradeGetAttributeProvider != null
                && livingUpgradeGetKey != null
                && livingUpgradeGetLevel != null
                && attributeProviderHandleAttributes != null){
                return;
            }

            initLivingStatsReflection();

            MethodHandles.Lookup lookup = MethodHandles.publicLookup();

            livingUpgradeClass = Class.forName(LIVING_UPGRADE_CLASS);
            attributeProviderClass = Class.forName(ATTRIBUTE_PROVIDER_CLASS);

            livingStatsGetUpgrades = lookup.findVirtual(
                    livingStatsClass,
                    "getUpgrades",
                    MethodType.methodType(Map.class)
            );

            livingUpgradeGetAttributeProvider = lookup.findVirtual(
                    livingUpgradeClass,
                    "getAttributeProvider",
                    MethodType.methodType(attributeProviderClass)
            );

            livingUpgradeGetKey = lookup.findVirtual(
                    livingUpgradeClass,
                    "getKey",
                    MethodType.methodType(ResourceLocation.class)
            );

            livingUpgradeGetLevel = lookup.findVirtual(
                    livingUpgradeClass,
                    "getLevel",
                    MethodType.methodType(int.class, int.class)
            );

            attributeProviderHandleAttributes = lookup.findVirtual(
                    attributeProviderClass,
                    "handleAttributes",
                    MethodType.methodType(
                            void.class,
                            livingStatsClass,
                            Multimap.class,
                            UUID.class,
                            livingUpgradeClass,
                            int.class
                    )
            );
        }
    }
}