package org.dreamtinker.dreamtinker.tools.data;

import com.google.common.collect.ImmutableMap;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.DTtoolsDefinition;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.tools.DreamtinkerToolParts;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractToolDefinitionDataProvider;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.definition.module.aoe.CircleAOEIterator;
import slimeknights.tconstruct.library.tools.definition.module.build.*;
import slimeknights.tconstruct.library.tools.definition.module.material.DefaultMaterialsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.MaterialTraitsModule;
import slimeknights.tconstruct.library.tools.definition.module.material.PartStatsModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.MiningSpeedModifierModule;
import slimeknights.tconstruct.library.tools.definition.module.weapon.CircleWeaponAttack;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerToolActions;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

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
        define(DTtoolsDefinition.MASHOU)
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
                                        .trait(DreamtinkerModifiers.real_sweep, 1)
                                        .trait(DreamtinkerModifiers.strong_heavy, 1)
                                        .trait(DreamtinkerModifiers.silver_name_bee, 1).build())
                // behavior
                .module(ToolActionsModule.of(ToolActions.SWORD_DIG))
                .module(swordHarvest)
                .module(new CircleWeaponAttack(6));
        define(DTtoolsDefinition.TNTARROW)
                // parts
                .module(PartStatsModule.parts()
                                       .part(DreamtinkerToolParts.explode_core)
                                       .part(TinkerToolParts.toolHandle, 0.5f)
                                       .part(TinkerToolParts.toughHandle, 0.5f)
                                       .build())
                .module(defaultThreeParts)
                // stats
                .module(new SetStatsModule(StatsNBT.builder()
                                                   .set(ToolStats.ATTACK_DAMAGE, 5f)
                                                   .set(ToolStats.ATTACK_SPEED, -9999f).build()))
                .module(new MultiplyStatsModule(MultiplierNBT.builder()
                                                             .set(ToolStats.ATTACK_DAMAGE, 1.5f)
                                                             .set(ToolStats.MINING_SPEED, 0f)
                                                             .set(ToolStats.ATTACK_SPEED, 0f)
                                                             .set(ToolStats.DURABILITY, 0.1f).build()))
                .module(new ToolSlotsModule(ImmutableMap.of(SlotType.UPGRADE, 4, SlotType.ABILITY, 2)))
                // traits
                .module(ToolTraitsModule.builder()
                                        .trait(DreamtinkerModifiers.strong_explode, 1).build());

        // plate armor
        RandomMaterial tier2Material = RandomMaterial.random().tier(1, 2).build();
        DefaultMaterialsModule underPlateMaterials = DefaultMaterialsModule.builder().material(tier2Material, tier2Material, tier2Material).build();
        ToolModule plateSlots =
                ToolSlotsModule.builder()
                               .slots(SlotType.SOUL, 4)
                               .slots(SlotType.ABILITY, 2)
                               .slots(SlotType.UPGRADE, 1)
                               .slots(SlotType.DEFENSE, 1).build();
        defineArmor(DTtoolsDefinition.UNDER_PLATE)
                .modules(slots -> PartStatsModule.armor(slots)
                                                 .part(TinkerToolParts.plating, 1)
                                                 .part(TinkerToolParts.maille, 1)
                                                 .part(TinkerToolParts.maille, 1))
                .module(underPlateMaterials)
                .module(new SetStatsModule(StatsNBT.builder()
                                                   .set(ToolStats.ARMOR_TOUGHNESS, 2f).build()))
                .module(ArmorItem.Type.HELMET, ToolTraitsModule.builder().trait(ModifierIds.thorns, 1).build())
                .module(new MultiplyStatsModule(MultiplierNBT.builder()
                                                             .set(ToolStats.DURABILITY, 0.8f)
                                                             .set(ToolStats.ARMOR, 0.5f).build()))
                .module(ToolTraitsModule.builder()
                                        .trait(DreamtinkerModifiers.weapon_transformation, 1).build())
                .module(plateSlots);
        define(DTtoolsDefinition.narcissus_wing)
                // parts
                .module(PartStatsModule.parts()
                                       .part(DreamtinkerToolParts.memoryOrthant, 0.75f)
                                       .part(DreamtinkerToolParts.wishOrthant)
                                       .part(DreamtinkerToolParts.soulOrthant)
                                       .part(DreamtinkerToolParts.personaOrthant)
                                       .part(DreamtinkerToolParts.reasonEmanation, 0.5f)
                                       .build())
                .module(defaultFiveParts)
                // stats
                .module(new SetStatsModule(StatsNBT.builder()
                                                   .set(ToolStats.ATTACK_DAMAGE, 1f)
                                                   .set(ToolStats.ATTACK_SPEED, 2f)
                                                   .set(ToolStats.DRAW_SPEED, 3f).build()))
                .module(new MultiplyStatsModule(MultiplierNBT.builder()
                                                             .set(ToolStats.DRAW_SPEED, 1.5f)
                                                             .set(ToolStats.ATTACK_SPEED, 1.2f)
                                                             .set(ToolStats.DURABILITY, 0.6f).build()))
                .module(new ToolSlotsModule(ImmutableMap.of(SlotType.SOUL, 6, SlotType.ABILITY, 2, SlotType.UPGRADE, 1)))
                // traits
                .module(ToolTraitsModule.builder()
                                        .trait(DreamtinkerModifiers.memory_base, 1)
                                        .trait(DreamtinkerModifiers.foundation_will, 1)
                                        .trait(DreamtinkerModifiers.splendour_heart, 1)
                                        .trait(ModifierIds.soulbound, 1)
                                        .trait(DreamtinkerModifiers.malum_sol_tiferet)
                                        .build())
                // behavior
                .module(ToolActionsModule.of(ToolActions.SWORD_DIG, ToolActions.PICKAXE_DIG, ToolActions.SHOVEL_DIG, TinkerToolActions.SHIELD_DISABLE))
                .module(IsEffectiveModule.tag(BlockTags.MINEABLE_WITH_PICKAXE))
                .module(new MiningSpeedModifierModule(1.5f, BlockPredicate.and(BlockPredicate.tag(BlockTags.MINEABLE_WITH_SHOVEL),
                                                                               BlockPredicate.set(Blocks.COBWEB))),
                        MiningSpeedModifierModule.blocks(0.10f, Blocks.VINE, Blocks.GLOW_LICHEN), MiningSpeedModifierModule.tag(BlockTags.WOOL, 0.3f))
                .module(new CircleAOEIterator(1, true))
                .module(new CircleWeaponAttack(3));
        define(DTtoolsDefinition.silence_glove)
                .module(MaterialStatsModule.stats()
                                           .stat(HeadMaterialStats.ID)
                                           .stat(StatlessMaterialStats.BINDING)
                                           .stat(StatlessMaterialStats.CUIRASS)
                                           .build())
                .module(ancientThreeParts)
                // ancient tools when rebalanced get both heads
                .module(new MaterialTraitsModule(HeadMaterialStats.ID, 2), ToolHooks.REBALANCED_TRAIT)
                // stats
                .module(new SetStatsModule(StatsNBT.builder()
                                                   .set(ToolStats.ATTACK_SPEED, 1.2f).build()))
                .smallToolStartingSlots()
                // traits
                .module(ToolTraitsModule.builder()
                                        .trait(DreamtinkerModifiers.Ids.weapon_slots)
                                        .trait(DreamtinkerModifiers.weapon_dreams)
                                        .build());
        // behavior;
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Tool Definition Data Generator";
    }
}
