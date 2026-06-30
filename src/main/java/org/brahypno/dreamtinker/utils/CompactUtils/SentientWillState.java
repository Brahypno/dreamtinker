package org.brahypno.dreamtinker.utils.CompactUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Locale;

public final class SentientWillState {
    public static final ResourceLocation DATA_KEY = new ResourceLocation("dreamtinker", "sentient_will");

    private static final String TYPE = "type";
    private static final String LEVEL = "level";
    private static final String DAMAGE_DELTA = "damage_delta";
    private static final String ATTACK_SPEED_DELTA = "attack_speed_delta";
    private static final String DIG_SPEED_DELTA = "dig_speed_delta";
    private static final String MOVEMENT_SPEED_DELTA = "movement_speed_delta";
    private static final String HEALTH_BONUS_DELTA = "health_bonus_delta";
    private static final String DRAIN = "drain";
    private static final String SOUL_DROP = "soul_drop";
    private static final String STATIC_DROP = "static_drop";

    private static final int[] SOUL_BRACKET = {16, 60, 200, 400, 1000, 2000, 4000};

    private static final float[] DEFAULT_DAMAGE = {1, 2, 3, 3.5f, 4, 4.5f, 5};
    private static final float[] DESTRUCTIVE_DAMAGE = {2, 3, 4, 5, 6, 7, 8};
    private static final float[] VENGEFUL_DAMAGE = {0, 0.5f, 1, 1.5f, 2, 2.5f, 3};
    private static final float[] STEADFAST_DAMAGE = {0, 0.5f, 1, 1.5f, 2, 2.5f, 3};

    private static final float[] DIG_SPEED = {1, 1.5f, 2, 3, 4, 5, 6};
    private static final float[] DRAIN_PER_SWING = {0.05f, 0.1f, 0.2f, 0.4f, 0.75f, 1, 1.25f};
    private static final float[] SOUL_DROP_VALUE = {2, 4, 7, 10, 13, 15, 18};
    private static final float[] STATIC_DROP_VALUE = {1, 1, 2, 3, 3, 4, 4};

    private static final float[] HEALTH_BONUS = {0, 0, 0, 0, 0, 0, 0};
    private static final float[] MOVEMENT_SPEED = {0.05f, 0.1f, 0.15f, 0.2f, 0.25f, 0.3f, 0.4f};

    private static final float[] VENGEFUL_ATTACK_SPEED = {-3, -2.8f, -2.7f, -2.6f, -2.5f, -2.4f, -2.3f};
    private static final float[] DESTRUCTIVE_ATTACK_SPEED = {-3.1f, -3.1f, -3.2f, -3.3f, -3.3f, -3.3f, -3.3f};

    private static final float BASE_SENTIENT_SWORD_ATTACK_SPEED = -2.4f;
    private static final float DEFAULT_SENTIENT_ATTACK_SPEED = -2.9f;

    private static final int[] ABSORPTION_TIME = {200, 300, 400, 500, 600, 700, 800};
    private static final int[] POISON_TIME = {25, 50, 60, 80, 100, 120, 150};
    private static final int[] POISON_LEVEL = {0, 0, 0, 1, 1, 1, 1};
    private static final float MAX_ABSORPTION_HEARTS = 10;

    public static float movementSpeedDelta(IToolStackView tool) {
        return state(tool.getPersistentData()).getFloat(MOVEMENT_SPEED_DELTA);
    }

    public static int corrosiveTime(int level) {
        return POISON_TIME[Math.max(0, Math.min(level, POISON_TIME.length - 1))];
    }

    public static int corrosiveLevel(int level) {
        return POISON_LEVEL[Math.max(0, Math.min(level, POISON_LEVEL.length - 1))];
    }

    public static int absorptionTime(int level) {
        return ABSORPTION_TIME[Math.max(0, Math.min(level, ABSORPTION_TIME.length - 1))];
    }

    /**
     * 只在 BLM 原版也会刷新的路径调用：
     * use / left click entity / hurt enemy / before melee hit。
     * <p>
     * 不要在 mining speed / after block break 调用。
     */
    public static boolean refreshFromPlayer(IToolStackView tool, Player player) {
        boolean hadState = hasState(tool);
        CompoundTag oldTag = state(tool.getPersistentData());
        String oldType = oldTag.getString(TYPE);

        Object type = BloodMagicWillReflect.getLargestWillType(player);
        double will = BloodMagicWillReflect.getTotalDemonWill(type, player);

        String typeName = will > 0 ? normalizeType(type) : "default";
        int level = getLevel(will);

        CompoundTag tag = new CompoundTag();
        tag.putString(TYPE, typeName);
        tag.putInt(LEVEL, level);
        tag.putFloat(DAMAGE_DELTA, level >= 0 ? damageDelta(typeName, level) : 0);
        tag.putFloat(ATTACK_SPEED_DELTA, level >= 0 ? attackSpeedDelta(typeName, level) : 0);
        tag.putFloat(DIG_SPEED_DELTA, level >= 0 ? digSpeedDelta(typeName, level) : 0);
        tag.putFloat(MOVEMENT_SPEED_DELTA, level >= 0 ? movementSpeedDelta(typeName, level) : 0);
        tag.putFloat(HEALTH_BONUS_DELTA, level >= 0 ? healthBonusDelta(typeName, level) : 0);
        tag.putFloat(DRAIN, level >= 0 ? DRAIN_PER_SWING[level] : 0);
        tag.putFloat(SOUL_DROP, level >= 0 ? SOUL_DROP_VALUE[level] : 0);
        tag.putFloat(STATIC_DROP, level >= 0 ? STATIC_DROP_VALUE[level] : 1);

        tool.getPersistentData().put(DATA_KEY, tag);
        return !hadState || !oldType.equals(typeName);
    }

    public static boolean hasState(IToolStackView tool) {
        return hasState(tool.getPersistentData());
    }

    public static boolean hasState(IToolContext context) {
        return hasState(context.getPersistentData());
    }

    public static String willType(IToolStackView tool) {
        return willType(tool.getPersistentData());
    }


    public static int willLevel(IToolStackView tool) {
        return state(tool.getPersistentData()).getInt(LEVEL);
    }

    public static float damageDelta(IToolContext context) {
        return state(context.getPersistentData()).getFloat(DAMAGE_DELTA);
    }

    public static float attackSpeedDelta(IToolContext context) {
        return state(context.getPersistentData()).getFloat(ATTACK_SPEED_DELTA);
    }

    public static float digSpeedDelta(IToolContext context) {
        return state(context.getPersistentData()).getFloat(DIG_SPEED_DELTA);
    }

    public static float healthBonusDelta(IToolContext context) {
        return state(context.getPersistentData()).getFloat(HEALTH_BONUS_DELTA);
    }

    public static double drain(IToolStackView tool) {
        return state(tool.getPersistentData()).getFloat(DRAIN);
    }

    public static double soulDrop(IToolStackView tool) {
        return state(tool.getPersistentData()).getFloat(SOUL_DROP);
    }

    public static double staticDrop(IToolStackView tool) {
        return state(tool.getPersistentData()).getFloat(STATIC_DROP);
    }

    public static void clear(IToolStackView tool) {
        tool.getPersistentData().remove(DATA_KEY);
    }

    public static double consumeSwingDrain(IToolStackView tool, Player player) {
        double drain = drain(tool);
        if (drain <= 0){
            return 0;
        }

        Object type = BloodMagicWillReflect.getLargestWillType(player);
        return BloodMagicWillReflect.consumeDemonWill(type, player, drain);
    }

    private static boolean hasState(IModDataView data) {
        return data.contains(DATA_KEY);
    }

    private static String willType(IModDataView data) {
        CompoundTag tag = state(data);
        String value = tag.getString(TYPE);
        return value.isBlank() ? "default" : value;
    }

    private static CompoundTag state(IModDataView data) {
        return data.get(DATA_KEY, CompoundTag::getCompound);
    }

    private static String normalizeType(Object type) {
        return BloodMagicWillReflect.typeName(type).toLowerCase(Locale.ROOT);
    }

    private static int getLevel(double will) {
        int level = -1;
        for (int i = 0; i < SOUL_BRACKET.length; i++) {
            if (will >= SOUL_BRACKET[i]){
                level = i;
            }
        }
        return level;
    }

    private static float damageDelta(String type, int level) {
        return switch (type) {
            case "destructive" -> DESTRUCTIVE_DAMAGE[level];
            case "vengeful" -> VENGEFUL_DAMAGE[level];
            case "steadfast" -> STEADFAST_DAMAGE[level];
            case "corrosive", "default" -> DEFAULT_DAMAGE[level];
            default -> DEFAULT_DAMAGE[level];
        };
    }

    private static float attackSpeedDelta(String type, int level) {
        float target = switch (type) {
            case "vengeful" -> VENGEFUL_ATTACK_SPEED[level];
            case "destructive" -> DESTRUCTIVE_ATTACK_SPEED[level];
            default -> DEFAULT_SENTIENT_ATTACK_SPEED;
        };
        return target - BASE_SENTIENT_SWORD_ATTACK_SPEED;
    }

    private static float digSpeedDelta(String type, int level) {
        return DIG_SPEED[level];
    }

    private static float movementSpeedDelta(String type, int level) {
        return "vengeful".equals(type) ? MOVEMENT_SPEED[level] : 0;
    }

    private static float healthBonusDelta(String type, int level) {
        return "steadfast".equals(type) ? HEALTH_BONUS[level] : 0;
    }

    public static ChatFormatting colorFor(String type) {
        return switch (type) {
            case "corrosive" -> ChatFormatting.GREEN;
            case "destructive" -> ChatFormatting.GOLD;
            case "vengeful" -> ChatFormatting.RED;
            case "steadfast" -> ChatFormatting.LIGHT_PURPLE;
            case "default" -> ChatFormatting.DARK_AQUA;
            default -> ChatFormatting.GRAY;
        };
    }


    public static float maxAbsorptionHearts() {
        return MAX_ABSORPTION_HEARTS;
    }
}