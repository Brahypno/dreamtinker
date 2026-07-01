package org.brahypno.dreamtinker.utils.CompactUtils.bloodmagic;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.Locale;

public final class BloodMagicWillReflect {
    private static final String BLOOD_MAGIC_MODID = "bloodmagic";
    private static final String WILL_HANDLER = "wayoftime.bloodmagic.will.PlayerDemonWillHandler";
    private static final String WILL_TYPE = "wayoftime.bloodmagic.api.compat.EnumDemonWillType";

    private static final String BLOOD_MAGIC_ITEMS = "wayoftime.bloodmagic.common.item.BloodMagicItems";
    private static final String I_DEMON_WILL = "wayoftime.bloodmagic.api.compat.IDemonWill";

    private static volatile MethodHandle registryObjectGet;
    private static volatile MethodHandle demonWillCreateWill;
    private static volatile Class<?> willTypeClass;
    private static volatile MethodHandle getLargestWillType;
    private static volatile MethodHandle getTotalDemonWill;
    private static volatile MethodHandle consumeDemonWill;
    private static MethodHandle BM_ADD_DEMON_WILL_PLAYER_STACK;

    private BloodMagicWillReflect() {}

    public static boolean isLoaded() {
        return ModList.get().isLoaded(BLOOD_MAGIC_MODID);
    }

    public static Object getLargestWillType(Player player) {
        if (!isLoaded()){
            return null;
        }

        try {
            return largestWillType().invoke(player);
        }
        catch (Throwable throwable) {
            throw new RuntimeException("Failed to call Blood Magic PlayerDemonWillHandler#getLargestWillType", throwable);
        }
    }

    public static double getTotalDemonWill(Object type, Player player) {
        if (!isLoaded() || type == null){
            return 0;
        }

        try {
            return (double) totalDemonWill().invoke(type, player);
        }
        catch (Throwable throwable) {
            throw new RuntimeException("Failed to call Blood Magic PlayerDemonWillHandler#getTotalDemonWill", throwable);
        }
    }

    public static double consumeDemonWill(Object type, Player player, double amount) {
        if (!isLoaded() || type == null || amount <= 0){
            return 0;
        }

        try {
            return (double) consumeWill().invoke(type, player, amount);
        }
        catch (Throwable throwable) {
            throw new RuntimeException("Failed to call Blood Magic PlayerDemonWillHandler#consumeDemonWill", throwable);
        }
    }


    public static void addWillToEntityGemsOrLoot(
            LivingEntity attacker,
            ItemStack willStack,
            List<ItemStack> generatedLoot
    ) {
        if (willStack.isEmpty()){
            return;
        }

        if (attacker instanceof Player player){
            addWillToPlayerGemsOrLoot(player, willStack, generatedLoot);
            return;
        }

        generatedLoot.add(willStack);
    }

    public static void addWillToPlayerGemsOrLoot(
            Player player,
            ItemStack willStack,
            List<ItemStack> generatedLoot
    ) {
        if (willStack.isEmpty()){
            return;
        }

        ItemStack remainder = insertWillIntoSoulGems(player, willStack);
        if (!remainder.isEmpty()){
            generatedLoot.add(remainder);
        }
    }


    public static String typeName(Object type) {
        return type == null ? "DEFAULT" : String.valueOf(type);
    }

    private static Class<?> willTypeClass() throws ClassNotFoundException {
        Class<?> local = willTypeClass;
        if (local == null){
            local = Class.forName(WILL_TYPE);
            willTypeClass = local;
        }
        return local;
    }

    private static MethodHandle largestWillType() throws ReflectiveOperationException {
        MethodHandle local = getLargestWillType;
        if (local == null){
            Class<?> handler = Class.forName(WILL_HANDLER);
            local = MethodHandles.publicLookup().findStatic(
                    handler,
                    "getLargestWillType",
                    MethodType.methodType(willTypeClass(), Player.class)
            );
            getLargestWillType = local;
        }
        return local;
    }

    private static MethodHandle totalDemonWill() throws ReflectiveOperationException {
        MethodHandle local = getTotalDemonWill;
        if (local == null){
            Class<?> handler = Class.forName(WILL_HANDLER);
            local = MethodHandles.publicLookup().findStatic(
                    handler,
                    "getTotalDemonWill",
                    MethodType.methodType(double.class, willTypeClass(), Player.class)
            );
            getTotalDemonWill = local;
        }
        return local;
    }

    private static MethodHandle consumeWill() throws ReflectiveOperationException {
        MethodHandle local = consumeDemonWill;
        if (local == null){
            Class<?> handler = Class.forName(WILL_HANDLER);
            local = MethodHandles.publicLookup().findStatic(
                    handler,
                    "consumeDemonWill",
                    MethodType.methodType(double.class, willTypeClass(), Player.class, double.class)
            );
            consumeDemonWill = local;
        }
        return local;
    }

    public static ItemStack insertWillIntoSoulGems(Player player, ItemStack willStack) {
        if (player == null || willStack.isEmpty() || !ModList.get().isLoaded(BLOOD_MAGIC_MODID)){
            return willStack;
        }

        try {
            MethodHandle mh = BM_ADD_DEMON_WILL_PLAYER_STACK;
            if (mh == null){
                mh = findBloodMagicAddDemonWill();
                BM_ADD_DEMON_WILL_PLAYER_STACK = mh;
            }

            Object result = mh.invoke(player, willStack);
            return result instanceof ItemStack stack ? stack : willStack;
        }
        catch (Throwable ignored) {
            return willStack;
        }
    }

    public static ItemStack createWillStack(String typeName, double amount) {
        if (!isLoaded() || amount <= 0){
            return ItemStack.EMPTY;
        }

        try {
            Object soulItem = soulItem(typeName);
            if (soulItem == null){
                return ItemStack.EMPTY;
            }

            return (ItemStack) demonWillCreateWill().invoke(soulItem, amount);
        }
        catch (Throwable throwable) {
            return ItemStack.EMPTY;
        }
    }

    private static MethodHandle findBloodMagicAddDemonWill() throws ReflectiveOperationException {
        Class<?> handler = Class.forName("wayoftime.bloodmagic.will.PlayerDemonWillHandler");

        return MethodHandles.publicLookup().findStatic(
                handler,
                "addDemonWill",
                MethodType.methodType(ItemStack.class, Player.class, ItemStack.class)
        );
    }

    private static String normalizeWillTypeName(String typeName) {
        if (typeName == null || typeName.isBlank()){
            return "DEFAULT";
        }

        return switch (typeName.toLowerCase(Locale.ROOT)) {
            case "corrosive" -> "CORROSIVE";
            case "destructive" -> "DESTRUCTIVE";
            case "vengeful" -> "VENGEFUL";
            case "steadfast" -> "STEADFAST";
            case "raw", "default" -> "DEFAULT";
            default -> "DEFAULT";
        };
    }

    private static Object soulItem(String typeName) throws Throwable {
        Class<?> items = Class.forName(BLOOD_MAGIC_ITEMS);

        String fieldName = switch (normalizeWillTypeName(typeName)) {
            case "CORROSIVE" -> "MONSTER_SOUL_CORROSIVE";
            case "DESTRUCTIVE" -> "MONSTER_SOUL_DESTRUCTIVE";
            case "VENGEFUL" -> "MONSTER_SOUL_VENGEFUL";
            case "STEADFAST" -> "MONSTER_SOUL_STEADFAST";
            default -> "MONSTER_SOUL_RAW";
        };

        Object registryObject = items.getField(fieldName).get(null);
        return registryObjectGet(registryObject.getClass()).invoke(registryObject);
    }

    private static MethodHandle registryObjectGet(Class<?> registryObjectClass) throws ReflectiveOperationException {
        MethodHandle local = registryObjectGet;
        if (local == null){
            local = MethodHandles.publicLookup().findVirtual(
                    registryObjectClass,
                    "get",
                    MethodType.methodType(Object.class)
            );
            registryObjectGet = local;
        }
        return local;
    }

    private static MethodHandle demonWillCreateWill() throws ReflectiveOperationException {
        MethodHandle local = demonWillCreateWill;
        if (local == null){
            Class<?> demonWill = Class.forName(I_DEMON_WILL);
            local = MethodHandles.publicLookup().findVirtual(
                    demonWill,
                    "createWill",
                    MethodType.methodType(ItemStack.class, double.class)
            );
            demonWillCreateWill = local;
        }
        return local;
    }

}