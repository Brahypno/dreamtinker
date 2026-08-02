package org.brahypno.dreamtinker.config;

import net.minecraftforge.common.ForgeConfigSpec;


public class DreamtinkerClientConfig {
    public static final ForgeConfigSpec.Builder builder =
            new ForgeConfigSpec.Builder().comment("This is client config!!!");

    static {
        builder.push("UI Configuration");
    }

    public static final ForgeConfigSpec.BooleanValue SHELL_HEART_DISPLAYED =
            builder.comment("Enable/disable shell heart render").define("shellHeartDisplayedEnable", true);
    public static final ForgeConfigSpec.IntValue SHELL_HEART_RESERVED_ROWS =
            builder.comment("Extra heart rows reserved for other mods that do not update ForgeGui.leftHeight.")
                   .defineInRange("shellHeartReservedRows", 0, 0, 10);
    public static final ForgeConfigSpec.IntValue SHELL_HEART_X_OFFSET =
            builder.comment("Manual X offset for shell hearts.")
                   .defineInRange("shellHeartXOffset", 0, -200, 200);
    public static final ForgeConfigSpec.IntValue SHELL_HEART_Y_OFFSET =
            builder.comment("Manual Y offset for shell hearts.")
                   .defineInRange("shellHeartYOffset", 0, -200, 200);

    static {
        builder.pop();
        builder.push("Wall Vision Performance");
    }

    public static final ForgeConfigSpec.IntValue WALL_VISION_SCAN_BUDGET =
            builder.comment("Maximum block positions checked per client tick while rebuilding Wall Vision.")
                   .defineInRange("scanBudgetPerTick", 8192, 512, 32768);
    public static final ForgeConfigSpec.IntValue WALL_VISION_MAX_HIGHLIGHTS =
            builder.comment("Maximum matching blocks cached and rendered by Wall Vision.")
                   .defineInRange("maxHighlights", 16384, 256, 65536);
    public static final ForgeConfigSpec.IntValue WALL_VISION_STATIONARY_REFRESH_TICKS =
            builder.comment("Refresh interval while the player remains in the same block. Movement refreshes immediately.")
                   .defineInRange("stationaryRefreshTicks", 40, 5, 200);

    public static final ForgeConfigSpec specs = builder.pop().build();

}
