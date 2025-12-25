package org.dreamtinker.dreamtinker.tools.data.material;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Tiers;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.dreamtinker.dreamtinker.tools.items.DtTiers;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.material.AbstractMaterialStatsDataProvider;
import slimeknights.tconstruct.tools.stats.*;

public class DreamtinkerMaterialStatProvider extends AbstractMaterialStatsDataProvider {
    public DreamtinkerMaterialStatProvider(PackOutput packOutput) {
        super(packOutput, new DreamtinkerMaterialDataProvider(packOutput));
    }

    @Override
    protected void addMaterialStats() {
        addMeleeHarvest();
        addRanged();
        addArmor();
        addMisc();
    }

    private void addMeleeHarvest() {
        addMaterialStats(DreamtinkerMaterialIds.echo_alloy,
                         new HeadMaterialStats(400, 3.5f, Tiers.DIAMOND, 4.3f),
                         HandleMaterialStats.multipliers().durability(0.5f).miningSpeed(0.5f).attackDamage(1.5f).attackSpeed(0.5f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.moonlight_ice,
                         new HeadMaterialStats(100, 0.1f, Tiers.NETHERITE, 4.0f));
        addMaterialStats(DreamtinkerMaterialIds.valentinite,
                         HandleMaterialStats.multipliers().durability(0.9f).miningSpeed(1.1f).attackDamage(1.1f).attackSpeed(1.1f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.nigrescence_antimony,
                         new HeadMaterialStats(3355, 3.36f, Tiers.DIAMOND, 1.13f),
                         HandleMaterialStats.multipliers().durability(1.0f).miningSpeed(1f).attackDamage(1f).attackSpeed(1f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.metallivorous_stibium_lupus,
                         new HeadMaterialStats(1600, 7.6f, DtTiers.WOLF_TIER, 7.6f),
                         HandleMaterialStats.multipliers().durability(0.67f).miningSpeed(0.34f).attackDamage(0.76f).attackSpeed(1.76f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.crying_obsidian,
                         new HeadMaterialStats(700, 6f, Tiers.DIAMOND, 1.5f),
                         HandleMaterialStats.multipliers().durability(1.1f).miningSpeed(1.05f).attackDamage(1.1f).attackSpeed(1.1f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.larimar,
                         new HeadMaterialStats(600, 4f, Tiers.DIAMOND, 0.5f),
                         HandleMaterialStats.multipliers().durability(1.1f).miningSpeed(1.3f).attackDamage(0.5f).attackSpeed(1.1f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.amber,
                         new HeadMaterialStats(1800, 1f, Tiers.IRON, 0.5f),
                         HandleMaterialStats.multipliers().durability(1.2f).miningSpeed(.9f).attackDamage(.9f).attackSpeed(.9f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.half_rotten_homunculus,
                         HandleMaterialStats.multipliers().durability(.9f).miningSpeed(1f).attackDamage(1f).attackSpeed(1.2f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.desire_gem,
                         new HeadMaterialStats(600, 2f, Tiers.DIAMOND, 6f),
                         HandleMaterialStats.multipliers().durability(1f).miningSpeed(.9f).attackDamage(.8f).attackSpeed(1.4f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.despair_gem,
                         new HeadMaterialStats(300, 4f, DtTiers.WOLF_TIER, 9f),
                         HandleMaterialStats.multipliers().durability(.1f).miningSpeed(1.8f).attackDamage(1.8f).attackSpeed(1.8f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.soul_steel,
                         new HeadMaterialStats(600, 8f, Tiers.DIAMOND, 2.50f),
                         HandleMaterialStats.multipliers().durability(.95f).miningSpeed(1.05f).attackDamage(1.05f).attackSpeed(1.05f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.rainbow_honey_crystal,
                         new HeadMaterialStats(700, 7f, Tiers.DIAMOND, 2.75f),
                         HandleMaterialStats.multipliers().durability(1.1f).miningSpeed(0.8f).attackDamage(1.2f).attackSpeed(1.1f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.black_sapphire,
                         new HeadMaterialStats(2000, 50f, DtTiers.Netheritte, 10f),
                         HandleMaterialStats.multipliers().durability(2.0f).miningSpeed(11.0f).attackDamage(11.0f).attackSpeed(11.0f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.scolecite,
                         new HeadMaterialStats(600, 5f, Tiers.DIAMOND, 1.5f),
                         HandleMaterialStats.multipliers().durability(0.8f).miningSpeed(1.0f).attackDamage(1.05f).attackSpeed(1.05f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.shiningFlint,
                         new HeadMaterialStats(300, 2f, Tiers.IRON, 1.50f),
                         HandleMaterialStats.multipliers().durability(0.7f).miningSpeed(1.0f).attackDamage(1.1f).attackSpeed(1.05f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.orichalcum,
                         new HeadMaterialStats(420, 7f, Tiers.IRON, 0.75f),
                         HandleMaterialStats.multipliers().durability(0.8f).miningSpeed(1.2f).attackDamage(1.1f).attackSpeed(1.05f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.cold_iron,
                         new HeadMaterialStats(400, 5f, Tiers.IRON, 1.5f),
                         HandleMaterialStats.multipliers().durability(1.05f).miningSpeed(1.05f).attackDamage(1.05f).attackSpeed(0.90f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.shadowSilver,
                         new HeadMaterialStats(360, 6f, Tiers.IRON, 2.5f),
                         HandleMaterialStats.multipliers().durability(0.8f).miningSpeed(1.15f).attackDamage(1.0f).attackSpeed(1.2f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.TransmutationGold,
                         new HeadMaterialStats(120, 12f, DtTiers.TransmutationGold, 1f),
                         HandleMaterialStats.multipliers().durability(0.6f).miningSpeed(1.10f).attackDamage(1.10f).attackSpeed(1.10f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.ArcaneGold,
                         new HeadMaterialStats(66, 9f, Tiers.GOLD, 0.75f),
                         HandleMaterialStats.multipliers().durability(0.7f).attackDamage(0.90f).miningSpeed(1.25f).attackSpeed(1.20f).build(),
                         StatlessMaterialStats.BINDING);

        addELMeleeHarvest();
        addMalumMeleeHarvest();
        addEidolonMeleeHarvest();
        addBICMeleeHarvest();
    }

    private void addELMeleeHarvest() {
        addMaterialStats(DreamtinkerMaterialIds.etherium,
                         new HeadMaterialStats(3000, 8f, Tiers.DIAMOND, 4f),
                         HandleMaterialStats.multipliers().durability(1.0f).miningSpeed(1.2f).attackDamage(1.3f).attackSpeed(1.4f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.nefarious,
                         new HeadMaterialStats(3000, 4f, Tiers.NETHERITE, 7f),
                         HandleMaterialStats.multipliers().durability(1.2f).miningSpeed(1.4f).attackDamage(0.6f).attackSpeed(1.2f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.soul_etherium,
                         new HeadMaterialStats(10, 6f, Tiers.NETHERITE, 6f),
                         HandleMaterialStats.multipliers().durability(0.7f).miningSpeed(1.4f).attackDamage(1.6f).attackSpeed(0.7f).build(),
                         StatlessMaterialStats.BINDING);
    }

    private void addMalumMeleeHarvest() {
        addMaterialStats(DreamtinkerMaterialIds.spirit_fabric,
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.hallowed_gold,
                         new HeadMaterialStats(32, 15f, Tiers.GOLD, 0f));
        addMaterialStats(DreamtinkerMaterialIds.mnemonic_auric,
                         new HeadMaterialStats(1000, 1f, Tiers.IRON, 0f),
                         HandleMaterialStats.multipliers().durability(0.8f).miningSpeed(1.05f).attackDamage(1.1f).attackSpeed(1.2f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.soul_stained_steel,
                         new HeadMaterialStats(1450, 1.5f, Tiers.DIAMOND, 0.5f),
                         HandleMaterialStats.multipliers().durability(0.4f).miningSpeed(1.05f).attackDamage(0.7f).attackSpeed(0.8f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.malignant_lead,
                         HandleMaterialStats.multipliers().durability(0.8f).miningSpeed(0.9f).attackDamage(1.2f).attackSpeed(1.2f).build());
        addMaterialStats(DreamtinkerMaterialIds.malignant_pewter,
                         new HeadMaterialStats(1800, 2.0f, Tiers.NETHERITE, 7f),
                         HandleMaterialStats.multipliers().durability(1.2f).miningSpeed(1.3f).attackDamage(1.2f).attackSpeed(0.4f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.malignant_gluttony,
                         new HeadMaterialStats(1200, 3.0f, Tiers.NETHERITE, 0f),
                         HandleMaterialStats.multipliers().durability(0.5f).miningSpeed(1.3f).attackDamage(1.2f).attackSpeed(1.2f).build(),
                         StatlessMaterialStats.BINDING);
    }

    private void addEidolonMeleeHarvest() {
        addMaterialStats(DreamtinkerMaterialIds.TatteredCloth, StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.WickedWeave, StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.PaladinBoneTool,
                         new HeadMaterialStats(200, 3.0f, Tiers.STONE, 1.5f),
                         HandleMaterialStats.multipliers().durability(0.75f).miningSpeed(0.95f).attackDamage(1.05f).attackSpeed(1.10f).build(),
                         StatlessMaterialStats.BINDING);

    }

    private void addBICMeleeHarvest() {
        addMaterialStats(DreamtinkerMaterialIds.MonsterSkin, StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.DarkMetal,
                         new HeadMaterialStats(1100, 2f, Tiers.NETHERITE, 3f),
                         HandleMaterialStats.multipliers().durability(1.15f).miningSpeed(0.95f).attackDamage(1.15f).attackSpeed(1.05f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.SpinyShell,
                         new HeadMaterialStats(400, 2f, Tiers.IRON, 1.5f),
                         HandleMaterialStats.multipliers().durability(0.85f).attackDamage(1.10f).attackSpeed(0.85f).build(),
                         StatlessMaterialStats.BINDING);

    }

    private void addRanged() {
        addMaterialStats(DreamtinkerMaterialIds.echo_alloy,
                         new LimbMaterialStats(400, -1f, 2.0f, 1.0f),
                         new GripMaterialStats(-0.5f, 1.0f, 4.0f));
        addMaterialStats(DreamtinkerMaterialIds.valentinite,
                         new LimbMaterialStats(200, 0.1f, 0.1f, -.1f));
        addMaterialStats(DreamtinkerMaterialIds.nigrescence_string, StatlessMaterialStats.BOWSTRING);
        addMaterialStats(DreamtinkerMaterialIds.metallivorous_stibium_lupus,
                         new GripMaterialStats(-0.33f, 0.5f, 7.6f));
        addMaterialStats(DreamtinkerMaterialIds.star_regulus,
                         new LimbMaterialStats(777, 7.7f, 0.7f, .7f));
        addMaterialStats(DreamtinkerMaterialIds.crying_obsidian,
                         new LimbMaterialStats(800, 0.3f, -0.2f, 0.3f),
                         new GripMaterialStats(0.8f, -0.2f, 1.0f));
        addMaterialStats(DreamtinkerMaterialIds.larimar,
                         new LimbMaterialStats(900, -0.2f, 0.2f, 0.2f),
                         new GripMaterialStats(0.5f, 0.2f, 1.0f));
        addMaterialStats(DreamtinkerMaterialIds.amber,
                         new LimbMaterialStats(1200, -0.2f, 0.1f, 0.1f),
                         new GripMaterialStats(1.0f, 0.15f, 2.0f));
        addMaterialStats(DreamtinkerMaterialIds.half_rotten_string, StatlessMaterialStats.BOWSTRING);
        addMaterialStats(DreamtinkerMaterialIds.desire_gem,
                         new LimbMaterialStats(600, 0.7f, -0.3f, 0.3f),
                         new GripMaterialStats(0.8f, 0.3f, 3.5f));
        addMaterialStats(DreamtinkerMaterialIds.soul_steel,
                         new LimbMaterialStats(600, 0.10f, 0.05f, 0f),
                         new GripMaterialStats(1.05f, 0.05f, 2.75f));
        addMaterialStats(DreamtinkerMaterialIds.black_sapphire,
                         new LimbMaterialStats(2000, 20f, 30f, 20f),
                         new GripMaterialStats(1.0f, 1f, 10f));
        addMaterialStats(DreamtinkerMaterialIds.scolecite,
                         new LimbMaterialStats(600, 0.05f, 0.05f, 0.05f),
                         new GripMaterialStats(0.05f, 0.05f, 3f));
        addMaterialStats(DreamtinkerMaterialIds.shiningFlint,
                         new LimbMaterialStats(300, -0.3f, 0.25f, -0.05f),
                         new GripMaterialStats(-0.2f, -0.05f, 1.75f));
        addMaterialStats(DreamtinkerMaterialIds.orichalcum,
                         new LimbMaterialStats(400, -0.1f, 0.15f, 0f),
                         new GripMaterialStats(-0.2f, 0.05f, 1.05f));
        addMaterialStats(DreamtinkerMaterialIds.cold_iron,
                         new LimbMaterialStats(400, -0.4f, 0.2f, 0f),
                         new GripMaterialStats(0.2f, 0f, 1.5f));
        addMaterialStats(DreamtinkerMaterialIds.shadowSilver,
                         new LimbMaterialStats(360, -0.1f, 0.05f, 0.1f),
                         new GripMaterialStats(-0.2f, -0.05f, 2.5f));
        addMaterialStats(DreamtinkerMaterialIds.TransmutationGold,
                         new LimbMaterialStats(120, 0.25f, -0.3f, 0.2f),
                         new GripMaterialStats(-0.2f, 0.2f, 1.0f));
        addMaterialStats(DreamtinkerMaterialIds.ArcaneGold,
                         new LimbMaterialStats(32, 0.1f, 0.15f, -0.2f),
                         new GripMaterialStats(-0.2f, 0.15f, 0f));

        addCompactRanged();
    }

    private void addCompactRanged() {
        addMaterialStats(DreamtinkerMaterialIds.etherium,
                         new LimbMaterialStats(3000, 0.7f, 0.7f, 0.6f),
                         new GripMaterialStats(0.0f, 0.6f, 1.0f),
                         StatlessMaterialStats.BOWSTRING);
        addMaterialStats(DreamtinkerMaterialIds.nefarious,
                         new LimbMaterialStats(2000, 0.9f, 0.9f, -0.6f),
                         new GripMaterialStats(0.2f, 0.6f, 1.0f),
                         StatlessMaterialStats.BOWSTRING);
        addMaterialStats(DreamtinkerMaterialIds.soul_etherium,
                         new LimbMaterialStats(10, 1.6f, -0.1f, 0.6f),
                         new GripMaterialStats(0.2f, -0.6f, 7.0f),
                         StatlessMaterialStats.BOWSTRING);

        addMaterialStats(DreamtinkerMaterialIds.spirit_fabric,
                         StatlessMaterialStats.BOWSTRING);
        addMaterialStats(DreamtinkerMaterialIds.hallowed_gold,
                         new LimbMaterialStats(32, 0.3f, -0.15f, 0.1f),
                         new GripMaterialStats(-0.2f, 0.15f, 0f),
                         StatlessMaterialStats.BOWSTRING);
        addMaterialStats(DreamtinkerMaterialIds.soul_stained_steel,
                         new LimbMaterialStats(1200, 0.1f, -0.2f, 0.5f),
                         new GripMaterialStats(-0.3f, 0.35f, 0.1f));

        addMaterialStats(DreamtinkerMaterialIds.TatteredCloth,
                         StatlessMaterialStats.BOWSTRING);
        addMaterialStats(DreamtinkerMaterialIds.WickedWeave,
                         StatlessMaterialStats.BOWSTRING);
        addMaterialStats(DreamtinkerMaterialIds.PaladinBoneTool,
                         new LimbMaterialStats(150, 0.05f, -0.1f, 0.1f),
                         new GripMaterialStats(-0.15f, 0.10f, 1.5f));

        addMaterialStats(DreamtinkerMaterialIds.MonsterSkin,
                         StatlessMaterialStats.BOWSTRING);
    }

    private void addArmor() {
        addArmorShieldStats(DreamtinkerMaterialIds.echo_alloy,
                            PlatingMaterialStats.builder().durabilityFactor(65).armor(3, 4, 6.66f, 2).toughness(2).knockbackResistance(2.5f),
                            StatlessMaterialStats.MAILLE);
        addMaterialStats(DreamtinkerMaterialIds.valentinite, StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.nigrescence_antimony,
                            PlatingMaterialStats.builder().durabilityFactor(40).armor(1.08f, 2.46f, 4.31f, 2).toughness(3).knockbackResistance(6330f));
        addArmorShieldStats(DreamtinkerMaterialIds.star_regulus,
                            PlatingMaterialStats.builder().durabilityFactor(73).armor(4.38f, 7.52f, 10.16f, 5.18f).toughness(7).knockbackResistance(0.65f)
                                                .shieldDurability(7777),
                            StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.crying_obsidian,
                            PlatingMaterialStats.builder().durabilityFactor(12).armor(3f, 4f, 5f, 3f).toughness(0).knockbackResistance(0.65f),
                            StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.amber,
                            PlatingMaterialStats.builder().durabilityFactor(15).armor(2f, 3f, 3f, 2f).toughness(0).knockbackResistance(0.2f),
                            StatlessMaterialStats.MAILLE);
        addMaterialStats(DreamtinkerMaterialIds.half_rotten_homunculus,
                         StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.desire_gem,
                            PlatingMaterialStats.builder().durabilityFactor(15).armor(4f, 6f, 4f, 3f).toughness(8).knockbackResistance(2f),
                            StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.despair_gem,
                            PlatingMaterialStats.builder().durabilityFactor(200).armor(9f, 9f, 9f, 9f).toughness(9).knockbackResistance(7f),
                            StatlessMaterialStats.MAILLE);
        addMaterialStats(DreamtinkerMaterialIds.shadowskin, StatlessMaterialStats.MAILLE, StatlessMaterialStats.CUIRASS);
        addArmorShieldStats(DreamtinkerMaterialIds.soul_steel,
                            PlatingMaterialStats.builder().durabilityFactor(29).armor(1, 4, 6, 1).toughness(1),
                            StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.rainbow_honey_crystal,
                            PlatingMaterialStats.builder().durabilityFactor(30).armor(2, 4, 3, 2).toughness(2),
                            StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.black_sapphire,
                            PlatingMaterialStats.builder().durabilityFactor(100).armor(30, 40, 30, 30).toughness(30),
                            StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.scolecite,
                            PlatingMaterialStats.builder().durabilityFactor(24).armor(1, 4, 6, 1).toughness(1.5f),
                            StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.orichalcum,
                            PlatingMaterialStats.builder().durabilityFactor(26).armor(2, 4, 6, 2).toughness(1f).knockbackResistance(2f),
                            StatlessMaterialStats.MAILLE, StatlessMaterialStats.SHIELD_CORE);
        addArmorShieldStats(DreamtinkerMaterialIds.cold_iron,
                            PlatingMaterialStats.builder().durabilityFactor(17).armor(3, 5, 6, 2).toughness(1f).knockbackResistance(2f),
                            StatlessMaterialStats.MAILLE, StatlessMaterialStats.SHIELD_CORE);
        addArmorShieldStats(DreamtinkerMaterialIds.shadowSilver,
                            PlatingMaterialStats.builder().durabilityFactor(22).armor(2, 4, 5, 3).toughness(1f).knockbackResistance(2f),
                            StatlessMaterialStats.MAILLE, StatlessMaterialStats.SHIELD_CORE);
        addArmorShieldStats(DreamtinkerMaterialIds.TransmutationGold,
                            PlatingMaterialStats.builder().durabilityFactor(12).armor(2, 4, 5, 2).toughness(1f),
                            StatlessMaterialStats.MAILLE, StatlessMaterialStats.SHIELD_CORE);
        addArmorShieldStats(DreamtinkerMaterialIds.ArcaneGold,
                            PlatingMaterialStats.builder().durabilityFactor(6).armor(1, 3, 4, 1), StatlessMaterialStats.MAILLE);


        addArmorShieldStats(DreamtinkerMaterialIds.etherium,
                            PlatingMaterialStats.builder().durabilityFactor(132).armor(4f, 9f, 7f, 4f).toughness(4).knockbackResistance(0f),
                            StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.nefarious,
                            PlatingMaterialStats.builder().durabilityFactor(126).armor(6f, 9f, 4f, 7f).toughness(6).knockbackResistance(5f),
                            StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.soul_etherium,
                            PlatingMaterialStats.builder().durabilityFactor(150).armor(10f, 5f, 17f, 6f).toughness(10).knockbackResistance(5f),
                            StatlessMaterialStats.MAILLE);

        addArmorShieldStats(DreamtinkerMaterialIds.spirit_fabric,
                            PlatingMaterialStats.builder().durabilityFactor(16).armor(1f, 3f, 4f, 2f).toughness(0).knockbackResistance(0f),
                            StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.soul_stained_steel,
                            PlatingMaterialStats.builder().durabilityFactor(24).armor(2f, 6f, 7f, 3f).toughness(2).knockbackResistance(0f),
                            StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.malignant_pewter,
                            PlatingMaterialStats.builder().durabilityFactor(32).armor(3f, 6f, 8f, 3f).toughness(2).knockbackResistance(0f),
                            StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.malignant_gluttony,
                            PlatingMaterialStats.builder().durabilityFactor(27).armor(3f, 5f, 6f, 3f).toughness(3).knockbackResistance(2f),
                            StatlessMaterialStats.MAILLE);
        addMaterialStats(DreamtinkerMaterialIds.soul_rock, StatlessMaterialStats.SHIELD_CORE);


        addArmorShieldStats(DreamtinkerMaterialIds.WickedWeave,
                            PlatingMaterialStats.builder().durabilityFactor(21).armor(1f, 1f, 4f, 1f).toughness(0).knockbackResistance(0f),
                            StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.PaladinBone,
                            PlatingMaterialStats.builder().durabilityFactor(38).armor(2f, 3.5f, 7f, 2f).toughness(2).knockbackResistance(0f),
                            StatlessMaterialStats.MAILLE);

        addArmorShieldStats(DreamtinkerMaterialIds.DarkMetal,
                            PlatingMaterialStats.builder().durabilityFactor(40).armor(2f, 4f, 6f, 2f).toughness(2).knockbackResistance(0f),
                            StatlessMaterialStats.MAILLE, StatlessMaterialStats.SHIELD_CORE);
        addArmorShieldStats(DreamtinkerMaterialIds.MonsterSkin,
                            PlatingMaterialStats.builder().durabilityFactor(20).armor(2f, 2f, 3f, 1f).toughness(1).knockbackResistance(0f),
                            StatlessMaterialStats.MAILLE, StatlessMaterialStats.CUIRASS);
        addArmorShieldStats(DreamtinkerMaterialIds.SpinyShell,
                            PlatingMaterialStats.builder().durabilityFactor(20).armor(2f, 4f, 7f, 3f).toughness(0).knockbackResistance(0f),
                            StatlessMaterialStats.MAILLE, StatlessMaterialStats.SHIELD_CORE);
    }

    private void addMisc() {
        addMaterialStats(DreamtinkerMaterialIds.shadowskin, StatlessMaterialStats.REPAIR_KIT);
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Material Stats Data Provider";
    }
}
