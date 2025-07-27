package org.dreamtinker.dreamtinker.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class DreamtinkerConfig {
    public static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder()
            .comment("Configuration to almost all data in this mod. Take your own risk modify it!!!")
            .push("Tool Configuration");

    public static final ForgeConfigSpec.IntValue TNTarrowgravity = builder.comment("vertical accelerator or tnt arrow")
            .defineInRange("TNTArrowGravity", -5, Integer.MIN_VALUE, 0);

    static {
        builder.pop();
        builder.push("Effect Configuration");
    }
    public static final ForgeConfigSpec.IntValue SilvernamebeeNum = builder.comment("This is not very powerful, btw.")
            .defineInRange("SilvernamebeeItemNum", 1, 0, 1000);

    static {
        builder.pop();
        builder.push("LOOT Configuration");
    }
    public static final ForgeConfigSpec.DoubleValue AntimonyLootChance = builder.comment("Base chance to get Antimony drop from Ore")
            .defineInRange("AntimonyLootChance", 0.001, 0, 1000);

    static {
        builder.pop();
        builder.push("Modifier Configuration");
    }
    public static final ForgeConfigSpec.DoubleValue glaciriverPortion = builder.comment("Portion of life that glarical river steal")
            .defineInRange("GlaciralRiverPortion", 0.1, 0, 1000);

    public static final ForgeConfigSpec.IntValue glaciriverRange = builder.comment("Range that glarical river effect")
            .defineInRange("GlaciralRiverRange", 5, 1, 1000);

    public static final ForgeConfigSpec.IntValue glaciriverKillPlayer = builder.comment("Does Glarical river kill player? 0=no")
            .defineInRange("GlaciralRiverKillPlayer", 0, 0, 1);

    public static final ForgeConfigSpec.IntValue RealSweepRange = builder.comment("Real Sweep range")
            .defineInRange("RealSweepRange", 2, 1, 1000);

    public static final ForgeConfigSpec.IntValue StrongExplodeDamageBoost = builder.comment("Strong Explode Damage Boost")
            .defineInRange("StrongExplodeDamageBoost", 1, 1, 1000);

    public static final ForgeConfigSpec.IntValue EchoAttackCharge = builder.comment("Numbers of Charge needed to trigger")
            .defineInRange("EchoAttackCharge", 9, 1, 1000);

    public static final ForgeConfigSpec.DoubleValue EchoAttackChargingChance = builder.comment("Chance for trigger to update")
            .defineInRange("EchoAttackChargingChance", 0.5, 0, 1);

    public static final ForgeConfigSpec specs = builder.pop().build();
}

