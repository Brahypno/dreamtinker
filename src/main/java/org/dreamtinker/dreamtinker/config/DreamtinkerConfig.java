package org.dreamtinker.dreamtinker.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class DreamtinkerConfig {
    public static final ForgeConfigSpec.Builder builder =
            new ForgeConfigSpec.Builder().comment("Configuration to almost all data in this mod. Take your own risk modify it!!!").push("Tool Configuration");

    public static final ForgeConfigSpec.IntValue TNTarrowgravity =
            builder.comment("vertical accelerator or tnt arrow").defineInRange("TNTArrowGravity", -5, Integer.MIN_VALUE, 0);
    public static final ForgeConfigSpec.IntValue TNTarrowRadius = builder.comment("tnt arrow effect range").defineInRange("TNTarrowRadius", 5, 0, 100);
    public static final ForgeConfigSpec.IntValue StrongExplodeDamageBoost =
            builder.comment("Strong Explode Damage Boost").defineInRange("StrongExplodeDamageBoost", 1, 1, 1000);
    public static final ForgeConfigSpec.DoubleValue UnderPlateBoostMutiply =
            builder.comment("how many status you want to boost?").defineInRange("UnderPlateBoostMutiply", 0.1, 0, 100);

    static {
        builder.pop();
        builder.push("Item Configuration");
    }

    public static final ForgeConfigSpec.IntValue StarRegulusMaxHP =
            builder.comment("MAX HP BOOST from star Regulus").defineInRange("StarRegulusMaxHP", 77, 0, 1000);

    static {
        builder.pop();
        builder.push("Advancement Configuration");
    }

    public static final ForgeConfigSpec.IntValue StarRegulusAdvancement =
            builder.comment("1=Enable star regulus advancement effect").defineInRange("StarRegulusAdvancement", 1, 0, 1);

    static {
        builder.pop();
        builder.push("Effect Configuration");
    }

    public static final ForgeConfigSpec.IntValue SilvernamebeeNum =
            builder.comment("This is not very powerful, btw.").defineInRange("SilvernamebeeItemNum", 1, 0, 1000);

    static {
        builder.pop();
        builder.push("LOOT Configuration");
    }

    public static final ForgeConfigSpec.DoubleValue AntimonyLootChance =
            builder.comment("Base chance to get Antimony drop from Ore").defineInRange("AntimonyLootChance", 0.2, 0, 10);
    public static final ForgeConfigSpec.DoubleValue voidpearlDropRate =
            builder.comment("how many times trying to dodge?").defineInRange("voidpearlDropRate", 0.1, 0, 1);
    public static final ForgeConfigSpec.DoubleValue voidpearlDamage =
            builder.comment("how many damage this voidPearDeal?").defineInRange("voidpearlDamage", 2.0, 0.1, 1000);

    static {
        builder.pop();
        builder.push("Modifier Configuration");
    }

    public static final ForgeConfigSpec.IntValue ProjLimit =
            builder.comment(
                           "Numbers of dangling projectile allowed when shooting. This is aim to reduce cases like Dragon fire Ball stay still in air and cause system lagging")
                   .defineInRange("ProjLimit", 9, 0, 1000);

    static {builder.comment("Moonlight Ice: ");}

    public static final ForgeConfigSpec.DoubleValue glaciriverPortion =
            builder.comment("Portion of life that glarical river steal").defineInRange("GlaciralRiverPortion", 0.1, 0, 1000);
    public static final ForgeConfigSpec.IntValue glaciriverRange =
            builder.comment("Range that glarical river effect").defineInRange("GlaciralRiverRange", 5, 1, 1000);
    public static final ForgeConfigSpec.IntValue glaciriverKillPlayer =
            builder.comment("Does Glarical river kill player? 0=no").defineInRange("GlaciralRiverKillPlayer", 0, 0, 1);

    static {builder.comment("\nEcho Shard: ");}

    public static final ForgeConfigSpec.IntValue EchoAttackCharge =
            builder.comment("Numbers of Charge needed to trigger").defineInRange("EchoAttackCharge", 9, 1, 1000);
    public static final ForgeConfigSpec.DoubleValue EchoAttackChargingChance =
            builder.comment("Chance for trigger to update").defineInRange("EchoAttackChargingChance", 0.5, 0, 1);

    public static final ForgeConfigSpec.DoubleValue EchoDefenceRange =
            builder.comment("Entity within this range would be considered as potential target").defineInRange("EchoDefenceRange", 10.0, 1, 1000);
    public static final ForgeConfigSpec.DoubleValue EchoDefenceSpeed = builder.comment("Speed of bounced arrow").defineInRange("EchoDefenceSpeed", 5.0, 3, 100);

    static {builder.comment("\nValentinite: ");}

    public static final ForgeConfigSpec.DoubleValue AntimonyUsageDur =
            builder.comment("Durability improvement from antimony_usage").defineInRange("AntimonyUsageDur", 0.1, 0, 1000);
    public static final ForgeConfigSpec.DoubleValue AntimonyUsageAttack =
            builder.comment("Tool improvement from antimony_usage").defineInRange("AntimonyUsageAttack", 0.1, 0, 1000);
    public static final ForgeConfigSpec.DoubleValue AntimonyUsageArmor =
            builder.comment("Armor improvement from antimony_usage").defineInRange("AntimonyUsageArmor", 0.1, 0, 1000);
    public static final ForgeConfigSpec.DoubleValue AntimonyUsageProj =
            builder.comment("Projectile modifier from antimony_usage").defineInRange("AntimonyUsageProj", 0.1, 0, 1000);

    static {builder.comment("\nNigrescence Antimony: ");}

    public static final ForgeConfigSpec.IntValue BrokenVesselBoost =
            builder.comment("HP boost from BrokenBessel").defineInRange("BrokenVesselBoost", 1, 0, 1000);
    public static final ForgeConfigSpec.DoubleValue OuroboricHourglassMutiply =
            builder.comment("increase damage reducer from OuroboricHourglass").defineInRange("OuroboricHourglassMutiply", 2.0, 0, 1000);

    public static final ForgeConfigSpec.DoubleValue Prometheus =
            builder.comment("status boot everytime for ewige_widerkunft").defineInRange("Prometheus", 0.13, 0, 1);
    public static final ForgeConfigSpec.IntValue CentralFlame =
            builder.comment("How many Spins can ewige_widerkunft do?").defineInRange("CentralFlame", 12, 1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec.IntValue BurninVainRandomProj =
            builder.comment("Allow Burn in Vain Modifier summon random Projectile when shooting").defineInRange("BurninVainRandomProj", 1, 0, 1);
    public static final ForgeConfigSpec.DoubleValue BurninVainInaccuracy =
            builder.comment("Inaccuracy of Projectile sending from this Modifier").defineInRange("BurninVainInaccurity", 5.0, 0, 1000);

    static {builder.comment("\nMetallivorous Stibium Lupus: ");}

    public static final ForgeConfigSpec.IntValue TheWolfWonderEffectNum =
            builder.comment("Number of effective that applied on target").defineInRange("TheWolfWonderEffectNum", 10, 0, 100);
    public static final ForgeConfigSpec.IntValue TheWolfWonderEffectMinTime =
            builder.comment("Min time effective last in second").defineInRange("TheWolfWonderEffectMinTime", 10, 0, 100);
    public static final ForgeConfigSpec.IntValue TheWolfWonderEffectMaxTime =
            builder.comment("Max time effective last in second").defineInRange("TheWolfWonderEffectMaxTime", 10, 0, 100);
    public static final ForgeConfigSpec.IntValue TheWolfWonderEffectAmplifier =
            builder.comment("Max Amplifier of effects").defineInRange("TheWolfWonderEffectAmplifier", 10, 0, 100);
    public static final ForgeConfigSpec.IntValue TheWolfWonderSurpriseNumber =
            builder.comment("This is a suprise!").defineInRange("TheWolfWonderSurpriseNumber", 7, 0, 666);

    public static final ForgeConfigSpec.IntValue TheWolfWasEnable = builder.comment("Enable the Wolf Was modifier").defineInRange("TheWolfWasEnable", 1, 0, 1);
    public static final ForgeConfigSpec.IntValue TheWolfWasDamage = builder.comment(
                                                                                   "In the hidden crucible of our path, there can be no ascendance without renunciation—where loss is absent, growth withers----Modify the damage taken for this Modifier work")
                                                                           .defineInRange("TheWolfWasDamage", 77, 1, 1000);
    public static final ForgeConfigSpec.IntValue TheWolfWasMaxTier =
            builder.comment("Maximum tier the wolf was may get").defineInRange("TheWolfWasMaxTier", 4, 2, 100);

    static {builder.comment("\nstar_regulus: ");}

    public static final ForgeConfigSpec.IntValue AsOneRe = builder.comment("Initial Revive cound of As one").defineInRange("AsOneRe", 2, 0, 10000);
    public static final ForgeConfigSpec.IntValue AsOneT = builder.comment("Time counter for gaining revive counts").defineInRange("AsOneT", 777, 1, 10000);
    public static final ForgeConfigSpec.IntValue AsOneA = builder.comment("Max amp of effect that would be clear").defineInRange("AsOneA", 3, 1, 10000);
    public static final ForgeConfigSpec.DoubleValue AsOneS = builder.comment("Percentage damage taken").defineInRange("AsOneS", 0.33, 0, 1);

    static {builder.comment("\ncrying_obsidian: ");}

    public static final ForgeConfigSpec.IntValue CryingParticles =
            builder.comment("Do you want more particles in Sharpened With?").defineInRange("CryingParticles", 1, 0, 10000);
    public static final ForgeConfigSpec.DoubleValue CryingDamageBoost =
            builder.comment("Damage boost of Sharpened With").defineInRange("CryingDamageBoost", 0.16, 0, 100);
    public static final ForgeConfigSpec.IntValue IsoLdeEaseTime =
            builder.comment("Torrance period for ISOLDE;also the least time allowed").defineInRange("IsoLdeEaseTime", 3, 1, 10);

    public static final ForgeConfigSpec.IntValue RedTime = builder.comment("The seconds used to level up itself ").defineInRange("RedTime", 1200, 1, 1000000);

    static {builder.comment("\netherium: ");}

    public static final ForgeConfigSpec.DoubleValue EnderDodgeChance =
            builder.comment("Can I dance?").defineInRange("EnderDodgeChance", 0.1, 0, 1);
    public static final ForgeConfigSpec.IntValue EnderDodgetimes =
            builder.comment("how many times trying to dodge?").defineInRange("EnderDodgetimes", 1, 0, 64);

    public static final ForgeConfigSpec.BooleanValue ExplodehitFire =
            builder.comment("Do you want fire in explode hit?").define("ExplodehitFire", true);

    public static final ForgeConfigSpec.IntValue rangedhit =
            builder.comment("Distance for ranged hit start positive").defineInRange("rangedhit", 10, 1, 64);

    public static final ForgeConfigSpec.DoubleValue WitherShootDangerPercentage =
            builder.comment("Current health percentage for wither shoot dangerous skull").defineInRange("WitherShootDangerPercentage", 0.5, 0, 1);
    public static final ForgeConfigSpec.DoubleValue StoneheartProjreduce =
            builder.comment("Amount percentage reduced of stone heart").defineInRange("StoneheartProjreduce", 0.2, 0, 100);

    public static final ForgeConfigSpec.DoubleValue Lifelootingbonus =
            builder.comment("Bonus for life looting").defineInRange("Lifelootingbonus", 0.5, 0, 100);

    public static final ForgeConfigSpec.DoubleValue OpenSoulDeathCount =
            builder.comment("Count for Open Soul to reject death").defineInRange("OpenSoulDeathCount", 1000.0, 0, 100000);
    public static final ForgeConfigSpec.DoubleValue OpenSoulRepairCount =
            builder.comment("Count for Open soul to repair tool").defineInRange("OpenSoulRepairCount", 1.0, 0, 100);
    public static final ForgeConfigSpec.DoubleValue OpenSoulRangedGet =
            builder.comment("Count gain from ranged hit").defineInRange("OpenSoulRangedGet", 0.1, 0, 1);

    public static final ForgeConfigSpec.IntValue SoulBoundRange =
            builder.comment("At what range to find the scapegoat").defineInRange("SoulBoundRange", 5, 1, 16);
    public static final ForgeConfigSpec.IntValue SoulBoundCoolDown =
            builder.comment("how many Seconds need to wait before next time").defineInRange("SoulBoundCoolDown", 120, 0, 6400);

    public static final ForgeConfigSpec.DoubleValue ExilesFaultyCurseHPPercentage =
            builder.comment("Percentage of Max HP taken by the curse").defineInRange("ExilesFaultyCurseHPPercentage", 0.01, 0, 100);
    public static final ForgeConfigSpec.DoubleValue ExilesFaultyAbsorbHPPercentage =
            builder.comment("Percentage of Damage turn into absorption").defineInRange("ExilesFaultyAbsorbHPPercentage", 0.1, 0, 100);

    public static final ForgeConfigSpec specs = builder.pop().build();
}

