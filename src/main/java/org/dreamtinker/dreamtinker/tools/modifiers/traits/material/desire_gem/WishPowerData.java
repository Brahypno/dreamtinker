package org.dreamtinker.dreamtinker.tools.modifiers.traits.material.desire_gem;


import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dreamtinker.dreamtinker.Dreamtinker;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public final class WishPowerData {
    public static final ResourceLocation WISH_KEY = Dreamtinker.getLocation("wish_power");
    public static final ResourceLocation BOOST_UNTIL_KEY = Dreamtinker.getLocation("wish_boost_until");
    public static final ResourceLocation COOLDOWN_UNTIL_KEY = Dreamtinker.getLocation("wish_cooldown_until");
    public static final ResourceLocation LAST_HANDLED_BOOST_END_KEY = Dreamtinker.getLocation("wish_last_handled_boost_end");

    public static final int MAX_WISH = 100;

    private WishPowerData() {}

    public static int get(IToolStackView tool) {
        Tag tag = tool.getPersistentData().get(WISH_KEY);
        return tag instanceof IntTag intTag ? intTag.getAsInt() : 0;
    }

    public static void set(IToolStackView tool, int value) {
        tool.getPersistentData().put(WISH_KEY, IntTag.valueOf(Math.min(MAX_WISH, Math.max(0, value))));
    }

    public static void add(IToolStackView tool, int amount) {
        if (amount > 0)
            set(tool, get(tool) + amount);
    }

    public static void consumeAll(IToolStackView tool) {
        tool.getPersistentData().put(WISH_KEY, IntTag.valueOf(0));
    }

    public static boolean isFull(IToolStackView tool) {
        return get(tool) >= MAX_WISH;
    }

    public static long getLong(IToolStackView tool, ResourceLocation key) {
        Tag tag = tool.getPersistentData().get(key);
        return tag instanceof LongTag longTag ? longTag.getAsLong() : 0L;
    }

    public static void putLong(IToolStackView tool, ResourceLocation key, long value) {
        tool.getPersistentData().put(key, LongTag.valueOf(value));
    }

    public static boolean getBoolean(IToolStackView tool, ResourceLocation key) {
        Tag tag = tool.getPersistentData().get(key);
        return tag instanceof ByteTag byteTag && byteTag.getAsByte() != 0;
    }

    public static void putBoolean(IToolStackView tool, ResourceLocation key, boolean value) {
        tool.getPersistentData().put(key, ByteTag.valueOf(value));
    }

    public static boolean boosted(IToolStackView tool, Level level) {
        return getLong(tool, BOOST_UNTIL_KEY) > level.getGameTime();
    }

    public static boolean inCooldown(IToolStackView tool, Level level) {
        return getLong(tool, COOLDOWN_UNTIL_KEY) > level.getGameTime();
    }

    public static boolean canRelease(IToolStackView tool, Level level) {
        return isFull(tool) && !boosted(tool, level) && !inCooldown(tool, level);
    }

    public static void setBoostUntil(IToolStackView tool, long gameTime) {
        putLong(tool, BOOST_UNTIL_KEY, gameTime);
    }

    public static void setCooldownUntil(IToolStackView tool, long gameTime) {
        putLong(tool, COOLDOWN_UNTIL_KEY, gameTime);
    }

    public static boolean updateState(IToolStackView tool, Level level, int cooldownTicks) {
        long time = level.getGameTime();
        long boostEnd = getLong(tool, BOOST_UNTIL_KEY);
        long handledBoostEnd = getLong(tool, LAST_HANDLED_BOOST_END_KEY);

        if (boostEnd > 0L && time >= boostEnd && handledBoostEnd != boostEnd){
            setCooldownUntil(tool, time + cooldownTicks);
            putLong(tool, LAST_HANDLED_BOOST_END_KEY, boostEnd);
            return true;
        }

        return false;
    }
}
