package org.brahypno.dreamtinker.utils.CompactUtils.bloodmagic;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class BloodMagicSoulNetworkReflect {
    private static final String BLOOD_MAGIC_MODID = "bloodmagic";

    private static final String NETWORK_HELPER =
            "wayoftime.bloodmagic.util.helper.NetworkHelper";
    private static final String SOUL_NETWORK =
            "wayoftime.bloodmagic.core.data.SoulNetwork";
    private static final String SOUL_TICKET =
            "wayoftime.bloodmagic.core.data.SoulTicket";
    private static final String BOOLEAN_RESULT =
            "wayoftime.bloodmagic.util.BooleanResult";

    private static volatile boolean failed;

    private static volatile Class<?> soulNetworkClass;
    private static volatile Class<?> soulTicketClass;
    private static volatile Class<?> booleanResultClass;

    private static volatile MethodHandle getSoulNetwork;
    private static volatile MethodHandle getMaximumForTier;

    private static volatile MethodHandle getCurrentEssence;
    private static volatile MethodHandle getOrbTier;
    private static volatile MethodHandle add;
    private static volatile MethodHandle syphon;
    private static volatile MethodHandle syphonAndDamage;

    private static volatile MethodHandle soulTicketConstructor;
    private static volatile MethodHandle booleanResultIsSuccess;

    private BloodMagicSoulNetworkReflect() {}

    public static boolean isBloodMagicLoaded() {
        return ModList.get().isLoaded(BLOOD_MAGIC_MODID);
    }

    public static int getOrbTier(Player player) {
        if (!canUse(player)){
            return 0;
        }

        try {
            Object network = getNetwork(player);
            if (network == null){
                return 0;
            }

            return (int) getOrbTier.invoke(network);
        }
        catch (Throwable ignored) {
            failed = true;
            return 0;
        }
    }

    public static int getCurrentLP(Player player) {
        if (!canUse(player)){
            return 0;
        }

        try {
            Object network = getNetwork(player);
            if (network == null){
                return 0;
            }

            return (int) getCurrentEssence.invoke(network);
        }
        catch (Throwable ignored) {
            failed = true;
            return 0;
        }
    }

    public static int getMaxLP(Player player) {
        if (!canUse(player)){
            return 0;
        }

        try {
            Object network = getNetwork(player);
            if (network == null){
                return 0;
            }

            int tier = (int) getOrbTier.invoke(network);
            return (int) getMaximumForTier.invoke(tier);
        }
        catch (Throwable ignored) {
            failed = true;
            return 0;
        }
    }

    public static boolean hasLP(Player player, int amount) {
        if (amount <= 0){
            return true;
        }

        return getCurrentLP(player) >= amount;
    }

    /**
     * 只从 Soul Network 扣 LP。
     * LP 不够时不扣，返回 false。
     * <p>
     * 适合 Hellforged 普通触发。
     */
    public static boolean consumeLP(Player player, int amount) {
        if (amount <= 0){
            return true;
        }

        if (!canUse(player)){
            return false;
        }

        try {
            Object network = getNetwork(player);
            if (network == null){
                return false;
            }

            Object ticket = soulTicketConstructor.invoke(amount);
            int drained = (int) syphon.invoke(network, ticket);

            return drained >= amount;
        }
        catch (Throwable ignored) {
            failed = true;
            return false;
        }
    }

    /**
     * 从 Soul Network 扣 LP；LP 不足时 Blood Magic 会按原逻辑伤害玩家。
     * <p>
     * 不建议高频工具默认使用。
     * 可以用于“过载 / 血债 / 强制触发”类效果。
     */
    public static boolean consumeLPOrDamage(Player player, int amount) {
        if (amount <= 0){
            return true;
        }

        if (!canUse(player)){
            return false;
        }

        try {
            Object network = getNetwork(player);
            if (network == null){
                return false;
            }

            Object ticket = soulTicketConstructor.invoke(amount);
            Object result = syphonAndDamage.invoke(network, player, ticket);

            return isBooleanResultSuccess(result);
        }
        catch (Throwable ignored) {
            failed = true;
            return false;
        }
    }

    /**
     * 往 Soul Network 里加 LP。
     * 上限使用玩家当前 Blood Orb tier 对应的 maximum。
     * <p>
     * 返回实际加入的 LP。
     */
    public static int addLP(Player player, int amount) {
        if (amount <= 0){
            return 0;
        }

        if (!canUse(player)){
            return 0;
        }

        try {
            Object network = getNetwork(player);
            if (network == null){
                return 0;
            }

            int tier = (int) getOrbTier.invoke(network);
            int maximum = (int) getMaximumForTier.invoke(tier);

            Object ticket = soulTicketConstructor.invoke(amount);
            return (int) add.invoke(network, ticket, maximum);
        }
        catch (Throwable ignored) {
            failed = true;
            return 0;
        }
    }

    /**
     * 往 Soul Network 里加 LP。
     * 使用调用方指定的 maximum。
     * <p>
     * 返回实际加入的 LP。
     */
    public static int addLP(Player player, int amount, int maximum) {
        if (amount <= 0 || maximum <= 0){
            return 0;
        }

        if (!canUse(player)){
            return 0;
        }

        try {
            Object network = getNetwork(player);
            if (network == null){
                return 0;
            }

            Object ticket = soulTicketConstructor.invoke(amount);
            return (int) add.invoke(network, ticket, maximum);
        }
        catch (Throwable ignored) {
            failed = true;
            return 0;
        }
    }

    public static boolean tryConsumeLP(Player player, int amount) {
        return consumeLP(player, amount);
    }

    public static boolean tryConsumeLPOrDamage(Player player, int amount) {
        return consumeLPOrDamage(player, amount);
    }

    public static boolean isAvailable() {
        return isBloodMagicLoaded() && !failed && ensureReady();
    }

    private static boolean canUse(Player player) {
        return player != null && isAvailable();
    }

    private static Object getNetwork(Player player) throws Throwable {
        ensureReady();
        return getSoulNetwork.invoke(player);
    }

    private static boolean isBooleanResultSuccess(Object result) throws Throwable {
        if (result == null){
            return false;
        }

        if (booleanResultIsSuccess == null){
            return true;
        }

        Object value = booleanResultIsSuccess.invoke(result);
        return value instanceof Boolean bool && bool;
    }

    private static boolean ensureReady() {
        if (failed){
            return false;
        }

        if (getSoulNetwork != null
            && getMaximumForTier != null
            && getCurrentEssence != null
            && getOrbTier != null
            && add != null
            && syphon != null
            && syphonAndDamage != null
            && soulTicketConstructor != null){
            return true;
        }

        synchronized(BloodMagicSoulNetworkReflect.class) {
            if (getSoulNetwork != null
                && getMaximumForTier != null
                && getCurrentEssence != null
                && getOrbTier != null
                && add != null
                && syphon != null
                && syphonAndDamage != null
                && soulTicketConstructor != null){
                return true;
            }

            try {
                initReflection();
                return true;
            }
            catch (Throwable ignored) {
                failed = true;
                return false;
            }
        }
    }

    private static void initReflection() throws ReflectiveOperationException {
        if (!isBloodMagicLoaded()){
            throw new ClassNotFoundException("Blood Magic is not loaded");
        }

        MethodHandles.Lookup lookup = MethodHandles.publicLookup();

        Class<?> networkHelperClass = Class.forName(NETWORK_HELPER);
        soulNetworkClass = Class.forName(SOUL_NETWORK);
        soulTicketClass = Class.forName(SOUL_TICKET);

        getSoulNetwork = lookup.findStatic(
                networkHelperClass,
                "getSoulNetwork",
                MethodType.methodType(soulNetworkClass, Player.class)
        );

        getMaximumForTier = lookup.findStatic(
                networkHelperClass,
                "getMaximumForTier",
                MethodType.methodType(int.class, int.class)
        );

        getCurrentEssence = lookup.findVirtual(
                soulNetworkClass,
                "getCurrentEssence",
                MethodType.methodType(int.class)
        );

        getOrbTier = lookup.findVirtual(
                soulNetworkClass,
                "getOrbTier",
                MethodType.methodType(int.class)
        );

        soulTicketConstructor = lookup.findConstructor(
                soulTicketClass,
                MethodType.methodType(void.class, int.class)
        );

        add = lookup.findVirtual(
                soulNetworkClass,
                "add",
                MethodType.methodType(int.class, soulTicketClass, int.class)
        );

        syphon = lookup.findVirtual(
                soulNetworkClass,
                "syphon",
                MethodType.methodType(int.class, soulTicketClass)
        );

        syphonAndDamage = lookup.findVirtual(
                soulNetworkClass,
                "syphonAndDamage",
                MethodType.methodType(loadBooleanResultClass(), Player.class, soulTicketClass)
        );

        initBooleanResultReflection(lookup);
    }

    private static Class<?> loadBooleanResultClass() throws ClassNotFoundException {
        if (booleanResultClass == null){
            booleanResultClass = Class.forName(BOOLEAN_RESULT);
        }

        return booleanResultClass;
    }

    private static void initBooleanResultReflection(MethodHandles.Lookup lookup) {
        try {
            booleanResultIsSuccess = lookup.findVirtual(
                    booleanResultClass,
                    "isSuccess",
                    MethodType.methodType(boolean.class)
            );
        }
        catch (Throwable ignored) {
            booleanResultIsSuccess = null;
        }
    }
}