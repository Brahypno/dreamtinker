package org.dreamtinker.dreamtinker.data.providers.tool;

import com.google.common.collect.ImmutableMap;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerItems;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifers;
import org.dreamtinker.dreamtinker.tools.DTtoolsDefinition;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractToolDefinitionDataProvider;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.definition.module.build.*;
import slimeknights.tconstruct.library.tools.definition.module.material.DefaultMaterialsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.PartStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningSpeedModifierModule;
import slimeknights.tconstruct.library.tools.definition.module.weapon.CircleWeaponAttack;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.data.ModifierIds;

public class DreamtinkerToolDefinitionProvider extends AbstractToolDefinitionDataProvider {
    public DreamtinkerToolDefinitionProvider(PackOutput packOutput) {
        super(packOutput, Dreamtinker.MODID);
    }

    RandomMaterial tier1Material = RandomMaterial.random().tier(1).build();
    RandomMaterial randomMaterial = RandomMaterial.random().allowHidden().build();
    DefaultMaterialsModule defaultTwoParts = DefaultMaterialsModule.builder().material(tier1Material, tier1Material).build();
    DefaultMaterialsModule defaultThreeParts = DefaultMaterialsModule.builder().material(tier1Material, tier1Material, tier1Material).build();
    DefaultMaterialsModule defaultFourParts = DefaultMaterialsModule.builder().material(tier1Material, tier1Material, tier1Material, tier1Material).build();
    DefaultMaterialsModule defaultFiveParts =
            DefaultMaterialsModule.builder().material(tier1Material, tier1Material, tier1Material, tier1Material, tier1Material).build();
    DefaultMaterialsModule ancientTwoParts = DefaultMaterialsModule.builder().material(randomMaterial, randomMaterial).build();
    DefaultMaterialsModule ancientThreeParts = DefaultMaterialsModule.builder().material(randomMaterial, randomMaterial, randomMaterial).build();

    @Override
    protected void addToolDefinitions() {
        ToolModule[] swordHarvest = {
                IsEffectiveModule.tag(TinkerTags.Blocks.MINABLE_WITH_SWORD),
                MiningSpeedModifierModule.blocks(7.5f, Blocks.COBWEB)
        };
        define(DTtoolsDefinition.MASU)
                // parts
                .module(PartStatsModule.parts()
                                       .part(TinkerToolParts.broadBlade, 0.75f)
                                       .part(TinkerToolParts.broadBlade, 0.5f)
                                       .part(TinkerToolParts.largePlate)
                                       .part(TinkerToolParts.toughHandle, 0.5f)
                                       .part(TinkerToolParts.toughHandle, 0.5f)
                                       .build())
                .module(defaultFiveParts)
                // stats
                .module(new SetStatsModule(StatsNBT.builder()
                                                   .set(ToolStats.ATTACK_DAMAGE, 3f)
                                                   .set(ToolStats.ATTACK_SPEED, 0.8f).build()))
                .module(new MultiplyStatsModule(MultiplierNBT.builder()
                                                             .set(ToolStats.ATTACK_DAMAGE, 1.5f)
                                                             .set(ToolStats.ATTACK_SPEED, 0.7f)
                                                             .set(ToolStats.MINING_SPEED, 0.25f)
                                                             .set(ToolStats.DURABILITY, 1.1f).build()))
                .module(new ToolSlotsModule(ImmutableMap.of(SlotType.UPGRADE, 4, SlotType.ABILITY, 4)))
                // traits
                .module(ToolTraitsModule.builder()
                                        .trait(ModifierIds.reach, 6)
                                        .trait(DreamtinkerModifers.realsweep, 1)
                                        .trait(DreamtinkerModifers.strong_heavy, 1)
                                        .trait(DreamtinkerModifers.silvernamebee, 1).build())
                // behavior
                .module(ToolActionsModule.of(ToolActions.SWORD_DIG))
                .module(swordHarvest)
                .module(new CircleWeaponAttack(4));
        define(DTtoolsDefinition.TNTARROW)
                // parts
                .module(PartStatsModule.parts()
                                       .part(DreamtinkerItems.explode_core)
                                       .part(TinkerToolParts.toolHandle, 0.5f)
                                       .part(TinkerToolParts.toughHandle, 0.5f)
                                       .build())
                .module(defaultThreeParts)
                // stats
                .module(new SetStatsModule(StatsNBT.builder()
                                                   .set(ToolStats.ATTACK_DAMAGE, 3f)
                                                   .set(ToolStats.ATTACK_SPEED, 0f).build()))
                .module(new MultiplyStatsModule(MultiplierNBT.builder()
                                                             .set(ToolStats.ATTACK_DAMAGE, 1.5f)
                                                             .set(ToolStats.MINING_SPEED, 0f)
                                                             .set(ToolStats.DURABILITY, 1.1f).build()))
                .module(new ToolSlotsModule(ImmutableMap.of(SlotType.UPGRADE, 4, SlotType.ABILITY, 2)))
                // traits
                .module(ToolTraitsModule.builder()
                                        .trait(DreamtinkerModifers.strong_explode, 1).build());

        // plate armor
        RandomMaterial tier2Material = RandomMaterial.random().tier(1, 2).build();
        DefaultMaterialsModule underplateMaterials = DefaultMaterialsModule.builder().material(tier2Material, tier2Material, tier2Material).build();
        ToolModule plateSlots =
                ToolSlotsModule.builder()
                               .slots(SlotType.SOUL, 1)
                               .slots(SlotType.ABILITY, 1)
                               .slots(SlotType.UPGRADE, 2)
                               .slots(SlotType.DEFENSE, 3).build();
        defineArmor(DTtoolsDefinition.UNDER_PLATE)
                .modules(slots -> PartStatsModule.armor(slots)
                                                 .part(TinkerToolParts.plating, 1)
                                                 .part(TinkerToolParts.maille, 1)
                                                 .part(TinkerToolParts.maille, 1))
                .module(underplateMaterials)
                .module(ArmorItem.Type.CHESTPLATE, new MultiplyStatsModule(MultiplierNBT.builder().set(ToolStats.ATTACK_DAMAGE, 0.4f).build()))
                .module(new MultiplyStatsModule(MultiplierNBT.builder()
                                                             .set(ToolStats.DURABILITY, 0.8f).build()))
                .module(plateSlots);
    }

    @Override
    public String getName() {
        return "Dreamtinker Tool Definition Data Generator";
    }
}
