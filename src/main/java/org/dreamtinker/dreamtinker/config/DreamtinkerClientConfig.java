package org.dreamtinker.dreamtinker.config;

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
    public static final ForgeConfigSpec specs = builder.pop().build();

}
