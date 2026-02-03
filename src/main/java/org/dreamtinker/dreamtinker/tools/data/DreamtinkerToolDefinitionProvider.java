package org.dreamtinker.dreamtinker.tools.data;

import com.google.common.collect.ImmutableMap;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaRegistry;
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
import slimeknights.tconstruct.library.tools.definition.module.aoe.*;
import slimeknights.tconstruct.library.tools.definition.module.build.*;
import slimeknights.tconstruct.library.tools.definition.module.display.FixedMaterialToolName;
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
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolActions;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import static slimeknights.tconstruct.tools.TinkerToolParts.smallBlade;
import static slimeknights.tconstruct.tools.TinkerToolParts.toolHandle;

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
    ToolModule large_soul_weapon_slots = ToolSlotsModule.builder()
                                                        .slots(SlotType.SOUL, 1)
                                                        .slots(SlotType.ABILITY, 1)
                                                        .slots(SlotType.UPGRADE, 2).build();

    ToolModule small_soul_weapon_slots = ToolSlotsModule.builder()
                                                        .slots(SlotType.SOUL, 2)
                                                        .slots(SlotType.ABILITY, 1)
                                                        .slots(SlotType.UPGRADE, 3).build();

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
                                                   .set(ToolStats.ATTACK_SPEED, 0.8f)
                                                   .set(ToolStats.BLOCK_AMOUNT, 10).build()))
                .module(new MultiplyStatsModule(MultiplierNBT.builder()
                                                             .set(ToolStats.ATTACK_DAMAGE, 1.5f)
                                                             .set(ToolStats.ATTACK_SPEED, 0.7f)
                                                             .set(ToolStats.MINING_SPEED, 0.25f)
                                                             .set(ToolStats.DURABILITY, 1.1f).build()))
                .module(new ToolSlotsModule(ImmutableMap.of(SlotType.UPGRADE, 4, SlotType.ABILITY, 2)))
                // traits
                .module(ToolTraitsModule.builder()
                                        .trait(DreamtinkerModifiers.Ids.long_tool, 4)
                                        .trait(DreamtinkerModifiers.real_sweep, 1)
                                        .trait(DreamtinkerModifiers.strong_heavy, 1)
                                        .trait(DreamtinkerModifiers.Ids.silver_name_bee, 1).build())
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
                               .slots(SlotType.SOUL, 2)
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
                                        .trait(DreamtinkerModifiers.weapon_transformation, 1)
                                        .trait(NovaRegistry.nova_magic_armor, 1).build())
                .module(plateSlots);
        define(DTtoolsDefinition.NarcissusWing)
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
                                                   .set(ToolStats.DRAW_SPEED, 3f)
                                                   .set(ToolStats.BLOCK_AMOUNT, 10).build()))
                .module(new MultiplyStatsModule(MultiplierNBT.builder()
                                                             .set(ToolStats.DRAW_SPEED, 1.5f)
                                                             .set(ToolStats.ATTACK_SPEED, 1.2f)
                                                             .set(ToolStats.DURABILITY, 0.6f).build()))
                .module(new ToolSlotsModule(ImmutableMap.of(SlotType.SOUL, 6, SlotType.ABILITY, 2, SlotType.UPGRADE, 1)))
                // traits
                .module(ToolTraitsModule.builder()
                                        .trait(DreamtinkerModifiers.memory_base, 1)//malkuth
                                        .trait(DreamtinkerModifiers.foundation_will, 1)//Yesod
                                        .trait(DreamtinkerModifiers.splendour_heart, 1)//Hod
                                        .trait(ModifierIds.soulbound, 1)
                                        .trait(DreamtinkerModifiers.malum_sol_tiferet)//Tiferet
                                        .build())
                // behavior
                .module(ToolActionsModule.of(ToolActions.SWORD_DIG, ToolActions.PICKAXE_DIG, ToolActions.SHOVEL_DIG, TinkerToolActions.SHIELD_DISABLE))
                .module(IsEffectiveModule.tag(BlockTags.MINEABLE_WITH_PICKAXE))
                .module(new MiningSpeedModifierModule(1.5f, BlockPredicate.or(BlockPredicate.tag(BlockTags.MINEABLE_WITH_SHOVEL),
                                                                              BlockPredicate.set(Blocks.COBWEB))),
                        MiningSpeedModifierModule.blocks(0.10f, Blocks.VINE, Blocks.GLOW_LICHEN), MiningSpeedModifierModule.tag(BlockTags.WOOL, 0.3f))
                .module(new CircleAOEIterator(1, true))
                .module(new CircleWeaponAttack(3));
        define(DTtoolsDefinition.SilenceGlove)
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
                .module(small_soul_weapon_slots)
                // traits
                .module(ToolTraitsModule.builder()
                                        .trait(DreamtinkerModifiers.Ids.weapon_slots)
                                        .trait(DreamtinkerModifiers.weapon_dreams)
                                        .build());
        define(DTtoolsDefinition.ChainSawBlade)
                // parts
                .module(PartStatsModule.parts()
                                       .part(TinkerToolParts.broadBlade, 0.45f)
                                       .part(DreamtinkerToolParts.chainSawTeeth, 0.75f)
                                       .part(DreamtinkerToolParts.chainSawCore, 0.75f)
                                       .part(TinkerToolParts.toughHandle, 0.45f)
                                       .build())
                .module(defaultFourParts)
                // stats
                .module(new SetStatsModule(StatsNBT.builder()
                                                   .set(ToolStats.ATTACK_DAMAGE, 1f)
                                                   .set(ToolStats.ATTACK_SPEED, 0.4f)
                                                   .set(ToolStats.BLOCK_AMOUNT, 10).build()))
                .module(new MultiplyStatsModule(MultiplierNBT.builder()
                                                             .set(ToolStats.ATTACK_DAMAGE, 1.7f)
                                                             .set(ToolStats.MINING_SPEED, 0.4f)
                                                             .set(ToolStats.DURABILITY, 1.15f).build()))
                .largeToolStartingSlots()
                // traits
                .module(ToolTraitsModule.builder()
                                        .trait(DreamtinkerModifiers.death_shredder)
                                        .trait(ModifierIds.stripping).build())
                // behavior
                .module(ToolActionsModule.of(ToolActions.AXE_DIG, ToolActions.SWORD_DIG, TinkerToolActions.SHIELD_DISABLE))
                .module(new ToolModule[]{
                        new IsEffectiveModule(BlockPredicate.or(BlockPredicate.tag(TinkerTags.Blocks.MINABLE_WITH_SWORD),
                                                                BlockPredicate.tag(BlockTags.MINEABLE_WITH_AXE)), false),
                        MiningSpeedModifierModule.blocks(7.5f, Blocks.COBWEB)
                })
                .module(new ConditionalAOEIterator(
                        BlockPredicate.tag(TinkerTags.Blocks.TREE_LOGS), new TreeAOEIterator(0, 0),
                        BoxAOEIterator.builder(0, 4, 0).addWidth(2).addDepth(2).direction(IBoxExpansion.HEIGHT).build()));
        define(NovaRegistry.PerAsperaScriptum)
                // parts
                .module(PartStatsModule.parts()
                                       .part(DreamtinkerToolParts.NovaMisc)
                                       .part(DreamtinkerToolParts.NovaWrapper)
                                       .part(DreamtinkerToolParts.NovaRostrum)
                                       .part(DreamtinkerToolParts.NovaCover).build())
                .module(defaultFourParts)
                // stats
                .module(new SetStatsModule(StatsNBT.builder()
                                                   .set(ToolStats.ATTACK_DAMAGE, 1f)
                                                   .set(ToolStats.ATTACK_SPEED, 1.0f).build()))
                .module(new MultiplyStatsModule(MultiplierNBT.builder()
                                                             .set(ToolStats.DURABILITY, 1.5f).build())) // gets effectively 2x durability from having 2 heads
                .module(ToolTraitsModule.builder()
                                        .trait(DreamtinkerModifiers.Ids.nova_spell_tiers)
                                        .trait(NovaRegistry.nova_scriptum_attributes).build())
                .module(large_soul_weapon_slots);
        // behavior;
        define(DTtoolsDefinition.RitualBlade)
                // parts
                .module(PartStatsModule.parts()
                                       .part(smallBlade)
                                       .part(toolHandle).build())
                .module(defaultTwoParts)
                // stats
                .module(new SetStatsModule(StatsNBT.builder()
                                                   .set(ToolStats.ATTACK_DAMAGE, 4f)
                                                   .set(ToolStats.ATTACK_SPEED, 1.0f)
                                                   .set(ToolStats.BLOCK_AMOUNT, 10)
                                                   .set(ToolStats.USE_ITEM_SPEED, 1.0f).build()))
                .module(new MultiplyStatsModule(MultiplierNBT.builder()
                                                             .set(ToolStats.ATTACK_DAMAGE, 0.75f)
                                                             .set(ToolStats.MINING_SPEED, 0.75f)
                                                             .set(ToolStats.DURABILITY, 0.75f).build()))
                .smallToolStartingSlots()
                // traits
                .module(ToolTraitsModule.builder()
                                        .trait(TinkerModifiers.silky, 1)
                                        .trait(ModifierIds.spilling)
                                        .trait(DreamtinkerModifiers.self_sacrifice)
                                        .trait(TinkerModifiers.melting)
                                        .trait(TinkerModifiers.silkyShears).build())
                // behavior
                .module(ToolActionsModule.of(ToolActions.SWORD_DIG, ToolActions.HOE_DIG))
                .module(IsEffectiveModule.tag(TinkerTags.Blocks.MINABLE_WITH_DAGGER))
                .module(MiningSpeedModifierModule.blocks(7.5f, Blocks.COBWEB))
                // faster tool name logic
                .module(FixedMaterialToolName.FIRST);
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Tool Definition Data Generator";
    }
}
