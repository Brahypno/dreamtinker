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
                         new HeadMaterialStats(200, 0.1f, Tiers.NETHERITE, 8.0f));
        addMaterialStats(DreamtinkerMaterialIds.valentinite,
                         HandleMaterialStats.multipliers().durability(0.9f).miningSpeed(1.1f).attackDamage(1.1f).attackSpeed(1.1f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.nigrescence_antimony,
                         new HeadMaterialStats(3355, 3.36f, Tiers.NETHERITE, 1.13f),
                         HandleMaterialStats.multipliers().durability(0.5f).miningSpeed(1f).attackDamage(1f).attackSpeed(1f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.metallivorous_stibium_lupus,
                         new HeadMaterialStats(1600, 7.6f, DtTiers.WOLF_TIER, 7.6f),
                         HandleMaterialStats.multipliers().durability(0.67f).miningSpeed(0.34f).attackDamage(1.33f).attackSpeed(1.76f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.crying_obsidian,
                         new HeadMaterialStats(700, 6f, Tiers.DIAMOND, 2f),
                         HandleMaterialStats.multipliers().durability(1.1f).miningSpeed(1.05f).attackDamage(1.1f).attackSpeed(1.1f).build(),
                         StatlessMaterialStats.BINDING);

        addMaterialStats(DreamtinkerMaterialIds.etherium,
                         new HeadMaterialStats(3000, 8f, Tiers.NETHERITE, 5f),
                         HandleMaterialStats.multipliers().durability(1.0f).miningSpeed(1.2f).attackDamage(1.3f).attackSpeed(1.4f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.nefarious,
                         new HeadMaterialStats(3000, 4f, Tiers.NETHERITE, 8f),
                         HandleMaterialStats.multipliers().durability(1.2f).miningSpeed(1.4f).attackDamage(0.6f).attackSpeed(1.2f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.soul_etherium,
                         new HeadMaterialStats(10, 6f, Tiers.NETHERITE, 6f),
                         HandleMaterialStats.multipliers().durability(0.7f).miningSpeed(1.4f).attackDamage(1.6f).attackSpeed(0.7f).build(),
                         StatlessMaterialStats.BINDING);

        addMaterialStats(DreamtinkerMaterialIds.spirit_fabric,
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.hallowed_gold,
                         new HeadMaterialStats(32, 15f, Tiers.GOLD, 0f));
        addMaterialStats(DreamtinkerMaterialIds.mnemonic_fragment,
                         new HeadMaterialStats(1000, 1f, Tiers.IRON, 0f),
                         HandleMaterialStats.multipliers().durability(0.8f).miningSpeed(1.05f).attackDamage(1.1f).attackSpeed(1.2f).build(),
                         StatlessMaterialStats.BINDING);
        addMaterialStats(DreamtinkerMaterialIds.soul_stained_steel,
                         new HeadMaterialStats(1450, 1.5f, Tiers.DIAMOND, 0.5f),
                         HandleMaterialStats.multipliers().durability(1.2f).miningSpeed(1.05f).attackDamage(1.3f).attackSpeed(0.8f).build(),
                         StatlessMaterialStats.BINDING);
    }

    private void addRanged() {
        addMaterialStats(DreamtinkerMaterialIds.echo_alloy,
                         new LimbMaterialStats(400, -1f, 3.0f, 3.0f),
                         new GripMaterialStats(-0.5f, 3.0f, 4.0f));
        addMaterialStats(DreamtinkerMaterialIds.valentinite,
                         new LimbMaterialStats(400, 0.1f, 0.1f, .15f));
        addMaterialStats(DreamtinkerMaterialIds.nigrescence_antimony,
                         StatlessMaterialStats.BOWSTRING);
        addMaterialStats(DreamtinkerMaterialIds.metallivorous_stibium_lupus,
                         new GripMaterialStats(-0.33f, 3.0f, 0.76f));
        addMaterialStats(DreamtinkerMaterialIds.star_regulus,
                         new LimbMaterialStats(777, 7.7f, 0.7f, .7f));
        addMaterialStats(DreamtinkerMaterialIds.crying_obsidian,
                         new LimbMaterialStats(800, 0.3f, -0.2f, 0.4f),
                         new GripMaterialStats(0.5f, -0.4f, 1.0f));

        addMaterialStats(DreamtinkerMaterialIds.etherium,
                         new LimbMaterialStats(3000, 0.7f, 0.7f, 0.6f),
                         new GripMaterialStats(0.0f, 0.6f, 1.0f),
                         StatlessMaterialStats.BOWSTRING);
        addMaterialStats(DreamtinkerMaterialIds.nefarious,
                         new LimbMaterialStats(2000, 0.9f, 1.7f, -0.6f),
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
                         new LimbMaterialStats(1200, 0.3f, -0.2f, 0.5f),
                         new GripMaterialStats(-0.3f, 0.5f, 0.1f));
    }

    private void addArmor() {
        addArmorShieldStats(DreamtinkerMaterialIds.echo_alloy,
                            PlatingMaterialStats.builder().durabilityFactor(65).armor(3, 4, 6.66f, 2).toughness(2).knockbackResistance(2.5f),
                            StatlessMaterialStats.MAILLE);
        addMaterialStats(DreamtinkerMaterialIds.valentinite,
                         StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.nigrescence_antimony,
                            PlatingMaterialStats.builder().durabilityFactor(40).armor(1.08f, 2.46f, 4.31f, 2).toughness(3).knockbackResistance(6330f));
        addArmorShieldStats(DreamtinkerMaterialIds.star_regulus,
                            PlatingMaterialStats.builder().durabilityFactor(73).armor(4.38f, 7.52f, 10.16f, 5.18f).toughness(7).knockbackResistance(0.65f)
                                                .shieldDurability(7777),
                            StatlessMaterialStats.MAILLE);
        addArmorShieldStats(DreamtinkerMaterialIds.crying_obsidian,
                            PlatingMaterialStats.builder().durabilityFactor(12).armor(3f, 4f, 5f, 3f).toughness(0).knockbackResistance(0.65f),
                            StatlessMaterialStats.MAILLE);

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
    }

    private void addMisc() {}

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Material Stats Data Provider";
    }
}
