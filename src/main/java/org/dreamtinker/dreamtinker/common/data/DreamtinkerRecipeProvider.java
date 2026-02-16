package org.dreamtinker.dreamtinker.common.data;

import com.aizistral.enigmaticlegacy.registries.EnigmaticBlocks;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.sammy.malum.core.systems.spirit.MalumSpiritType;
import com.sammy.malum.data.recipe.builder.SpiritInfusionRecipeBuilder;
import com.sammy.malum.registry.common.SpiritTypeRegistry;
import com.sammy.malum.registry.common.block.BlockRegistry;
import com.sammy.malum.registry.common.item.ItemRegistry;
import com.sammy.malum.registry.common.item.ItemTagRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.crafting.conditions.AndCondition;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.fluids.DreamtinkerFluids;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaRegistry;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.tools.DreamtinkerToolParts;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.dreamtinker.dreamtinker.tools.data.material.DreamtinkerMaterialDataProvider;
import org.dreamtinker.dreamtinker.utils.CastLookup;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.recipe.data.IRecipeHelper;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.CompositeCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.PartSwapCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.ToolHookIngredient;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.SwappableModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.ItemPartRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.recycle.PartBuilderToolRecycleBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.*;
import java.util.function.Consumer;

import static elucent.eidolon.registries.Registry.*;
import static net.mcreator.borninchaosv.init.BornInChaosV1ModEntities.*;
import static net.mcreator.borninchaosv.init.BornInChaosV1ModItems.*;

public class DreamtinkerRecipeProvider extends RecipeProvider implements IMaterialRecipeHelper, IToolRecipeHelper, IConditionBuilder, IRecipeHelper {

    public DreamtinkerRecipeProvider(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        this.addCraftingRecipes(consumer);
        this.addMeltingRecipes(consumer);
        this.addCastingRecipes(consumer);
        this.addAlloyRecipes(consumer);
        this.addMaterialRecipes(consumer);
        this.addCompactMaterialRecipes(consumer);
        this.addCompactMeltingCastingRecipes(consumer);

        this.addPartRecipes(consumer);
        this.addToolBuildingRecipes(consumer);
        this.addModifierRecipes(consumer);
        this.addEntityMeltingRecipes(consumer);
    }

    String Entity_Melting_folder = "smeltery/entity_melting/";

    private void addToolBuildingRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = "tools/building/";
        String armorFolder = "tools/armor/";
        ToolBuildingRecipeBuilder.toolBuildingRecipe(DreamtinkerTools.tntarrow.get())
                                 .outputSize(4)
                                 .save(consumer, prefix(DreamtinkerTools.tntarrow, folder));
        toolBuilding(consumer, DreamtinkerTools.mashou, folder);
        toolBuilding(consumer, DreamtinkerTools.narcissus_wing, folder);
        toolBuilding(consumer, DreamtinkerTools.chain_saw_blade, folder);
        ToolBuildingRecipeBuilder.toolBuildingRecipe(DreamtinkerTools.ritual_blade.get())
                                 .addExtraRequirement(Ingredient.of(TinkerModifiers.silkyCloth))
                                 .addExtraRequirement(Ingredient.of(Blocks.GLASS))
                                 .save(consumer, prefix(id(DreamtinkerTools.ritual_blade), folder));

        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"));
        toolBuilding(wrapped, NovaRegistry.per_aspera_scriptum, folder);

        String recycle_folder = "tools/recycling/";
        PartBuilderToolRecycleBuilder.tools(SizedIngredient.fromItems(4, DreamtinkerTools.tntarrow.get()))
                                     .save(consumer, location(recycle_folder + "tntarrow"));
        DreamtinkerTools.underPlate.forEach(
                item -> ToolBuildingRecipeBuilder.toolBuildingRecipe(item).layoutSlot(Dreamtinker.getLocation("under_plate"))
                                                 .addExtraRequirement(Ingredient.of(TinkerModifiers.silkyCloth))
                                                 .addExtraRequirement(Ingredient.of(TinkerModifiers.silkyCloth))
                                                 .save(consumer, this.prefix(this.id(item), armorFolder)));

        PartSwapCastingRecipeBuilder.tableRecipe(Ingredient.of(DreamtinkerTools.silence_glove), 4)
                                    .index(2)
                                    .save(consumer, location(folder + "silence_glove_leather"));
        PartSwapCastingRecipeBuilder.tableRecipe(Ingredient.of(DreamtinkerTools.silence_glove), 6)
                                    .index(0)
                                    .save(consumer, location(folder + "silence_glove_hardware"));
        PartBuilderToolRecycleBuilder.tool(DreamtinkerTools.silence_glove)
                                     .part(TinkerToolParts.smallBlade)
                                     .part(TinkerToolParts.toughBinding)
                                     .part(TinkerToolParts.repairKit)
                                     .save(consumer, location(recycle_folder + "silence_glove"));

    }

    private void addAlloyRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = "smeltery/alloy/";
        Consumer<FinishedRecipe> wrapped;
        AlloyRecipeBuilder.alloy(FluidOutput.fromFluid(DreamtinkerFluids.molten_albedo_stibium.get(), FluidValues.GEM), 1500)
                          .addCatalyst(FluidIngredient.of(DreamtinkerFluids.molten_lupi_antimony.getTag(), FluidValues.INGOT))
                          .addInput(TinkerTags.Fluids.METAL_TOOLTIPS, FluidValues.INGOT)
                          .save(consumer, prefix(DreamtinkerFluids.molten_albedo_stibium, folder));
        AlloyRecipeBuilder.alloy(FluidOutput.fromFluid(DreamtinkerFluids.liquid_smoky_antimony.get(), FluidValues.INGOT * 2), 3600)
                          .addInput(DreamtinkerFluids.molten_ascending_antimony.getTag(), FluidValues.INGOT)
                          .addInput(TinkerFluids.liquidSoul.getTag(), FluidValues.GLASS_BLOCK)
                          .save(consumer, prefix(DreamtinkerFluids.liquid_smoky_antimony, folder));
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.molten_evil, FluidValues.INGOT * 2)
                          .addInput(DreamtinkerFluids.molten_nefariousness.getTag(), FluidValues.GEM * 4)
                          .addInput(DreamtinkerFluids.liquid_trist.getTag(), FluidValues.NUGGET * 4)
                          .addInput(TinkerFluids.moltenNetherite.getTag(), FluidValues.INGOT)
                          .save(consumer, prefix(DreamtinkerFluids.molten_evil, folder));
        AlloyRecipeBuilder.alloy(FluidOutput.fromFluid(DreamtinkerFluids.liquid_pure_soul.get(), FluidValues.GEM), 1600)
                          .addInput(DreamtinkerFluids.liquid_trist.getTag(), FluidValues.NUGGET)
                          .addInput(TinkerFluids.liquidSoul.getTag(), FluidValues.GLASS_BLOCK * 10)
                          .save(consumer, prefix(DreamtinkerFluids.liquid_pure_soul, folder));
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.molten_soul_aether, FluidValues.INGOT)
                          .addInput(DreamtinkerFluids.molten_nefariousness.getTag(), FluidValues.GEM)
                          .addInput(DreamtinkerFluids.liquid_pure_soul.getTag(), FluidValues.GEM)
                          .addInput(DreamtinkerFluids.unstable_liquid_aether.getTag(), FluidValues.INGOT)
                          .addInput(DreamtinkerFluids.molten_void.getTag(), FluidValues.SLIMEBALL)
                          .addInput(DreamtinkerTagKeys.Fluids.molten_echo_shard, FluidValues.GEM)
                          .save(consumer, prefix(DreamtinkerFluids.molten_soul_aether, folder));
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.molten_soul_aether, FluidValues.INGOT)
                          .addInput(DreamtinkerFluids.molten_nefariousness.getTag(), FluidValues.GEM)
                          .addInput(DreamtinkerFluids.liquid_pure_soul.getTag(), FluidValues.GEM)
                          .addInput(DreamtinkerFluids.unstable_liquid_aether.getTag(), FluidValues.INGOT)
                          .addInput(DreamtinkerFluids.molten_void.getTag(), FluidValues.SLIMEBALL)
                          .addInput(DreamtinkerTagKeys.Fluids.molten_echo, FluidValues.GEM)
                          .save(consumer, wrap(DreamtinkerFluids.molten_soul_aether, folder, "_1"));
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.reversed_shadow, FluidValues.SLIMEBALL)
                          .addInput(DreamtinkerFluids.molten_void.getTag(), FluidValues.SLIMEBALL * 2)
                          .addInput(TinkerFluids.moltenEnder.getTag(), FluidValues.SLIMEBALL * 2)
                          .save(consumer, prefix(DreamtinkerFluids.reversed_shadow, folder));
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.molten_echo_alloy, FluidValues.GEM)
                          .addInput(DreamtinkerTagKeys.Fluids.molten_echo_shard, FluidValues.GEM)
                          .addInput(TinkerFluids.moltenEnder.getTag(), FluidValues.SLIMEBALL * 2)
                          .save(consumer, prefix(DreamtinkerFluids.molten_echo_alloy, folder));
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.molten_echo_alloy, FluidValues.GEM)
                          .addInput(DreamtinkerTagKeys.Fluids.molten_echo, FluidValues.GEM)
                          .addInput(TinkerFluids.moltenEnder.getTag(), FluidValues.SLIMEBALL * 2)
                          .save(consumer, wrap(DreamtinkerFluids.molten_echo_alloy, folder, "_1"));
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.despair_essence, FluidValues.GEM)
                          .addInput(DreamtinkerTagKeys.Fluids.molten_crying_obsidian, FluidValues.GLASS_BLOCK * 3)
                          .addInput(DreamtinkerFluids.liquid_amber.getTag(), FluidValues.GEM * 4)
                          .addInput(DreamtinkerFluids.molten_desire.getTag(), FluidValues.GEM * 9)
                          .addInput(DreamtinkerFluids.reversed_shadow.getTag(), FluidValues.SLIMEBALL * 7)
                          .addInput(DreamtinkerFluids.molten_lupi_antimony.getTag(), FluidValues.INGOT * 2)
                          .save(consumer, prefix(DreamtinkerFluids.despair_essence, folder));
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.molten_soul_steel, FluidValues.INGOT)
                          .addInput(TinkerFluids.moltenSteel.getTag(), FluidValues.INGOT)
                          .addInput(TinkerFluids.moltenCobalt.getTag(), FluidValues.INGOT)
                          .addInput(TinkerFluids.liquidSoul.getTag(), FluidValues.GLASS_BLOCK * 2)
                          .save(consumer, prefix(DreamtinkerFluids.molten_soul_steel, folder));
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.molten_bee_gem, FluidValues.GEM * 2)
                          .addInput(DreamtinkerFluids.rainbow_honey.getTag(), FluidValues.BOTTLE)
                          .addInput(TinkerFluids.meatSoup.getTag(), FluidValues.BOWL * 2)
                          .addInput(TinkerFluids.moltenEmerald.getTag(), FluidValues.GEM)
                          .save(consumer, prefix(DreamtinkerFluids.molten_bee_gem, folder));
        wrapped = withCondition(consumer, tagFilled(Dreamtinker.forgeItemTag("ingots/silver")));
        AlloyRecipeBuilder.alloy(TinkerFluids.moltenElectrum, FluidValues.INGOT * 2)
                          .addInput(TinkerFluids.moltenGold.ingredient(FluidValues.INGOT))
                          .addInput(DreamtinkerFluids.liquid_amber.ingredient(FluidValues.GEM))
                          .save(wrapped, prefix(TinkerFluids.moltenElectrum, folder));
        wrapped =
                withCondition(consumer,
                              new AndCondition(tagFilled(DreamtinkerTagKeys.Items.arcaneGoldIngot), tagFilled(Dreamtinker.forgeItemTag("gems/cinnabar"))));
        AlloyRecipeBuilder.alloy(FluidOutput.fromFluid(DreamtinkerFluids.molten_transmutation_gold.get(), FluidValues.INGOT * 2), 1200)
                          .addInput(DreamtinkerTagKeys.Fluids.molten_arcane_gold, FluidValues.INGOT)
                          .addInput(DreamtinkerFluids.mercury.ingredient(FluidValues.GEM))
                          .save(wrapped, prefix(DreamtinkerFluids.molten_transmutation_gold, folder));
    }

    String Casting_folder = "smeltery/casting/";

    private void addCastingRecipes(Consumer<FinishedRecipe> consumer) {
        // Pure Fluid Recipes
        Consumer<FinishedRecipe> wrapped;

        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.regulus.get())
                                .setFluid(DreamtinkerFluids.liquid_smoky_antimony.getLocalTag(), FluidValues.INGOT)
                                .setCoolingTime(100)
                                .setCast(Tags.Items.DUSTS_REDSTONE, true)
                                .save(consumer, location(Casting_folder + "currus_triumphalis_antimonii/smoky_to_star"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.metallivorous_stibium_lupus.get())
                                .setFluid(DreamtinkerFluids.molten_albedo_stibium.getLocalTag(), FluidValues.GEM * 4)
                                .setCoolingTime(70)
                                .setCast(DreamtinkerTagKeys.Items.TransmutationGoldBlock, true)
                                .save(consumer, location(Casting_folder + "currus_triumphalis_antimonii/albedo_to_lupus_block"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.metallivorous_stibium_lupus.get())
                                .setFluid(DreamtinkerFluids.molten_albedo_stibium.getLocalTag(), FluidValues.GEM * 4)
                                .setCoolingTime(50)
                                .setCast(DreamtinkerTagKeys.Items.TransmutationGoldDusts, true)
                                .save(consumer, location(Casting_folder + "currus_triumphalis_antimonii/albedo_to_lupus_dust"));

        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.crying_obsidian_plane)
                                .setFluidAndTime(DreamtinkerFluids.molten_crying_obsidian, FluidValues.GLASS_PANE)
                                .save(consumer, location(Casting_folder + "crying_obsidian/pane"));

        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.void_pearl.get())
                                .setCoolingTime(IMeltingRecipe.getTemperature(DreamtinkerFluids.molten_void), FluidValues.SLIMEBALL)
                                .setFluid(FluidIngredient.of(new FluidStack(DreamtinkerFluids.molten_void.get(), FluidValues.SLIMEBALL)))
                                .save(consumer, location(
                                        Casting_folder + "void_pearl/slime"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.twist_obsidian_pane.get())
                                .setFluidAndTime(DreamtinkerFluids.liquid_trist, FluidValues.NUGGET * 3)
                                .setCast(DreamtinkerCommon.crying_obsidian_plane.get(), true)
                                .save(consumer, location(Casting_folder + "twist_obsidian/pane"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.unborn_egg.get())
                                .setCast(Tags.Items.EGGS, true)
                                .setFluid(FluidIngredient.of(DreamtinkerFluids.molten_nigrescence_antimony.getTag(), FluidValues.GEM * 3))
                                .setCoolingTime(10)
                                .save(consumer, location(Casting_folder + "unborn_egg"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.unborn_turtle_egg.get())
                                .setCast(Items.TURTLE_EGG, true)
                                .setFluid(FluidIngredient.of(DreamtinkerFluids.molten_nigrescence_antimony.getTag(), FluidValues.GEM * 3))
                                .setCoolingTime(10)
                                .save(consumer, location(Casting_folder + "unborn_turtle_egg"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.unborn_sniffer_egg.get())
                                .setCast(Items.SNIFFER_EGG, true)
                                .setFluid(FluidIngredient.of(DreamtinkerFluids.molten_nigrescence_antimony.getTag(), FluidValues.GEM * 3))
                                .setCoolingTime(10)
                                .save(consumer, location(Casting_folder + "unborn_sniffer_egg"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.unborn_dragon_egg.get())
                                .setCast(Items.DRAGON_EGG, true)
                                .setFluid(FluidIngredient.of(DreamtinkerFluids.molten_albedo_stibium.getTag(), FluidValues.GEM * 3))
                                .setCoolingTime(10)
                                .save(consumer, location(Casting_folder + "unborn_dragon_egg"));
        ItemCastingRecipeBuilder.basinRecipe(BlockRegistry.BLOCK_OF_SOUL_STAINED_STEEL.get())
                                .setFluidAndTime(DreamtinkerFluids.molten_soul_stained_steel, FluidValues.METAL_BLOCK)
                                .save(consumer, location(Casting_folder + "soul_stained_steel/block"));

        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.poisonousHomunculus.get())
                                .setCast(Items.GLASS_BOTTLE, true)
                                .setFluidAndTime(DreamtinkerFluids.half_festering_blood, FluidValues.BOTTLE)
                                .save(consumer, location(Casting_folder + "filling/" +
                                                         Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(DreamtinkerCommon.poisonousHomunculus.get()))
                                                                .getPath()));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.evilHomunculus.get())
                                .setCast(Items.GLASS_BOTTLE, true)
                                .setFluidAndTime(DreamtinkerFluids.festering_blood, FluidValues.BOTTLE)
                                .save(consumer, location(Casting_folder + "filling/" +
                                                         Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(DreamtinkerCommon.evilHomunculus.get()))
                                                                .getPath()));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.rainbow_honey.get())
                                .setCast(Items.GLASS_BOTTLE, true)
                                .setFluidAndTime(DreamtinkerFluids.rainbow_honey, FluidValues.BOTTLE)
                                .save(consumer, location(Casting_folder + "filling/" +
                                                         Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(DreamtinkerCommon.rainbow_honey.get()))
                                                                .getPath()));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.desire_gem.get())
                                .setFluidAndTime(TinkerFluids.moltenDiamond, FluidValues.LARGE_GEM_BLOCK)
                                .setCast(Items.NETHER_STAR, true)
                                .save(consumer, location(Casting_folder + "desire_gem"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.shiningFlint.get())
                                .setFluidAndTime(TinkerFluids.moltenGlass, FluidValues.GLASS_BLOCK)
                                .setCast(Items.FLINT, true)
                                .save(consumer, location(Casting_folder + "shining_flint"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.deep_prismarine_shard.get())
                                .setFluidAndTime(TinkerFluids.skySlime, FluidValues.SLIME_BLOCK)
                                .setCast(Tags.Items.DUSTS_PRISMARINE, true)
                                .save(consumer, location(Casting_folder + "deep_prismarine_shard"));
    }

    String Melting_folder = "smeltery/melting/";
    String metalFolder = Melting_folder + "metal/";

    private void addMeltingRecipes(Consumer<FinishedRecipe> consumer) {
        MeltingFuelBuilder.fuel(DreamtinkerFluids.molten_lupi_antimony.ingredient(FluidValues.SIP), 360, 3600)
                          .save(consumer, location(Melting_folder + "fuel/molten_lupi_antimony"));
        MeltingFuelBuilder.fuel(DreamtinkerFluids.reversed_shadow.ingredient(FluidValues.SIP), 220, 2200)
                          .save(consumer, location(Melting_folder + "fuel/reversed_shadow"));

        meltCastBlock(DreamtinkerFluids.molten_crying_obsidian.get(), Items.CRYING_OBSIDIAN, FluidValues.GLASS_BLOCK, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.crying_obsidian_plane), DreamtinkerFluids.molten_crying_obsidian, FluidValues.GLASS_PANE,
                                     1.5f)
                            .save(consumer, location(Melting_folder + "crying_obsidian/plane"));

        meltCast(DreamtinkerFluids.molten_echo_shard.get(), Items.ECHO_SHARD, FluidValues.GEM, consumer);

        meltCast(DreamtinkerFluids.molten_echo_alloy.get(), DreamtinkerCommon.echo_alloy.get(), FluidValues.GEM, consumer);
        fake_block_to_ingot(consumer, DreamtinkerMaterialIds.echo_alloy, DreamtinkerCommon.echo_alloy.get());

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.metallivorous_stibium_lupus.get()),
                                     DreamtinkerFluids.molten_lupi_antimony, FluidValues.INGOT / 2, 2.0f)
                            .addByproduct(DreamtinkerFluids.molten_ascending_antimony.result(FluidValues.NUGGET * 3))
                            .setOre(IMeltingContainer.OreRateType.GEM)
                            .save(consumer, location(Melting_folder + "foundry/metallivorous_stibium_lupus/ingot"));
        cast(DreamtinkerFluids.molten_lupi_antimony.get(), DreamtinkerCommon.metallivorous_stibium_lupus.get(), FluidValues.INGOT, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.metallivorous_stibium_lupus_block.get()),
                                     DreamtinkerFluids.molten_lupi_antimony, FluidValues.METAL_BLOCK / 2, 2.0f)
                            .addByproduct(DreamtinkerFluids.molten_ascending_antimony.result(FluidValues.INGOT * 3))
                            .setOre(IMeltingContainer.OreRateType.GEM)
                            .save(consumer, location(Melting_folder + "foundry/metallivorous_stibium_lupus/block"));
        ItemCastingRecipeBuilder.basinRecipe(DreamtinkerCommon.metallivorous_stibium_lupus_block.get())
                                .setFluidAndTime(DreamtinkerFluids.molten_lupi_antimony, FluidValues.METAL_BLOCK)
                                .save(consumer, location(Casting_folder + "metallivorous_stibium_lupus/block"));


        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.nigrescence_antimony.get()),
                                     DreamtinkerFluids.molten_nigrescence_antimony, 75, 2.0f)
                            .addByproduct(DreamtinkerFluids.molten_albedo_stibium.result(75))
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(consumer, location(Melting_folder + "foundry/nigrescence_antimony/gem"));
        cast(DreamtinkerFluids.molten_nigrescence_antimony.get(), DreamtinkerCommon.nigrescence_antimony.get(), FluidValues.GEM, consumer);

        meltCast(DreamtinkerFluids.liquid_trist.get(), Items.GHAST_TEAR, FluidValues.NUGGET, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.void_pearl.get()), DreamtinkerFluids.molten_void, FluidValues.SLIMEBALL, 4.0f)
                            .save(consumer, location(Melting_folder + "void_pearl/slime"));

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.twist_obsidian_pane.get()), DreamtinkerFluids.liquid_trist, FluidValues.NUGGET * 3)
                            .addByproduct(DreamtinkerFluids.molten_crying_obsidian.result(FluidValues.GLASS_PANE))
                            .save(consumer, location(Melting_folder + "twist/reinforcement"));

        meltCast(DreamtinkerFluids.liquid_amber.get(), DreamtinkerCommon.amber.get(), FluidValues.GEM, consumer);
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.amberOre), DreamtinkerFluids.liquid_amber.get(), FluidValues.GEM, 1.0f)
                            .setOre(IMeltingContainer.OreRateType.GEM)
                            .save(consumer, location(Melting_folder + "amber/ore"));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.blackSapphireOre),
                                     DreamtinkerFluids.molten_black_sapphire.get(), FluidValues.GEM, 1.0f)
                            .setOre(IMeltingContainer.OreRateType.GEM)
                            .save(consumer, location(Melting_folder + "black_sapphire/ore"));
        fake_block_to_ingot(consumer, DreamtinkerMaterialIds.black_sapphire, DreamtinkerCommon.black_sapphire.get());
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.scoleciteOre),
                                     DreamtinkerFluids.molten_scolecite.get(), FluidValues.GEM, 1.0f)
                            .setOre(IMeltingContainer.OreRateType.GEM)
                            .save(consumer, location(Melting_folder + "scolecite/ore"));
        Consumer<FinishedRecipe> wrapped;
        wrapped = withCondition(consumer, tagFilled(Dreamtinker.forgeItemTag("ingots/silver")));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.OrichalcumOre, DreamtinkerCommon.DeepslateOrichalcumOre),
                                     DreamtinkerFluids.molten_orichalcum.get(), FluidValues.INGOT * 2, 1.0f)
                            .addByproduct(DreamtinkerFluids.molten_shadow_silver.result(FluidValues.INGOT * 2))
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "orichalcum/ore_wc"));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.RawOrichalcumBlock),
                                     DreamtinkerFluids.molten_orichalcum.get(), FluidValues.METAL_BLOCK, 1.0f)
                            .addByproduct(DreamtinkerFluids.molten_shadow_silver.result(FluidValues.METAL_BLOCK))
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "orichalcum/raw_storage_blocks_wc"));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.raw_orichalcum.get()),
                                     DreamtinkerFluids.molten_orichalcum.get(), FluidValues.INGOT, 1.0f)
                            .addByproduct(DreamtinkerFluids.molten_shadow_silver.result(FluidValues.INGOT))
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "orichalcum/raw_materials_wc"));

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.ShadowSilverOre),
                                     DreamtinkerFluids.molten_shadow_silver.get(), FluidValues.INGOT * 2, 1.0f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "shadow_silver/ore_wc"));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.RawShadowSilverBlock),
                                     DreamtinkerFluids.molten_shadow_silver.get(), FluidValues.METAL_BLOCK, 1.0f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "shadow_silver/raw_storage_blocks_wc"));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.raw_ShadowSilver),
                                     DreamtinkerFluids.molten_shadow_silver.get(), FluidValues.INGOT, 1.0f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "shadow_silver/raw_materials_wc"));

        wrapped = withCondition(consumer, not(tagFilled(Dreamtinker.forgeItemTag("ingots/silver"))));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.OrichalcumOre, DreamtinkerCommon.DeepslateOrichalcumOre),
                                     DreamtinkerFluids.molten_orichalcum.get(), FluidValues.INGOT * 2, 1.0f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "orichalcum/ore"));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.RawOrichalcumBlock),
                                     DreamtinkerFluids.molten_orichalcum.get(), FluidValues.METAL_BLOCK, 1.0f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "orichalcum/raw_storage_blocks"));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.raw_orichalcum.get()),
                                     DreamtinkerFluids.molten_orichalcum.get(), FluidValues.INGOT, 1.0f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "orichalcum/raw_materials"));

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.coldIronOre),
                                     DreamtinkerFluids.molten_cold_iron.get(), FluidValues.INGOT * 2, 1.0f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .addByproduct(DreamtinkerFluids.molten_orichalcum.result(FluidValues.INGOT * 2))
                            .save(consumer, location(Melting_folder + "cold_iron/ore"));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.RawColdIronBlock),
                                     DreamtinkerFluids.molten_cold_iron.get(), FluidValues.METAL_BLOCK, 1.0f)
                            .addByproduct(DreamtinkerFluids.molten_orichalcum.result(FluidValues.METAL_BLOCK))
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(consumer, location(Melting_folder + "cold_iron/raw_storage_blocks"));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.raw_coldIron),
                                     DreamtinkerFluids.molten_cold_iron.get(), FluidValues.INGOT, 1.0f)
                            .addByproduct(DreamtinkerFluids.molten_orichalcum.result(FluidValues.INGOT))
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(consumer, location(Melting_folder + "cold_iron/raw_materials"));

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.TransmutationGoldOre),
                                     DreamtinkerFluids.molten_transmutation_gold.get(), FluidValues.INGOT * 2, 1.0f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(consumer, location(Melting_folder + "transmutation_gold/ore"));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.RawTransmutationGoldBlock),
                                     DreamtinkerFluids.molten_transmutation_gold.get(), FluidValues.METAL_BLOCK, 1.0f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(consumer, location(Melting_folder + "transmutation_gold/raw_storage_blocks"));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.raw_TransmutationGold),
                                     DreamtinkerFluids.molten_transmutation_gold.get(), FluidValues.INGOT, 1.0f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(consumer, location(Melting_folder + "transmutation_gold/raw_materials"));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.TransmutationGoldDusts),
                                     DreamtinkerFluids.molten_transmutation_gold.get(), FluidValues.INGOT, 0.05f)
                            .save(consumer, location(Melting_folder + "transmutation_gold/dust"));

        meltCast(DreamtinkerFluids.molten_desire.get(), DreamtinkerCommon.desire_gem.get(), FluidValues.GEM, consumer);
        meltCast(DreamtinkerFluids.despair_essence.get(), DreamtinkerCommon.despair_gem.get(), FluidValues.GEM, consumer);
        meltCast(DreamtinkerFluids.molten_soul_steel.get(), DreamtinkerCommon.soul_steel.get(), FluidValues.INGOT, consumer);
        meltCastBlock(DreamtinkerFluids.molten_soul_steel.get(), DreamtinkerCommon.soulSteelBlock.get(), FluidValues.METAL_BLOCK, consumer);
        meltCast(DreamtinkerFluids.molten_bee_gem.get(), DreamtinkerCommon.rainbow_honey_crystal.get(), FluidValues.GEM, consumer);
        meltCast(DreamtinkerFluids.molten_black_sapphire.get(), DreamtinkerCommon.black_sapphire.get(), FluidValues.GEM, consumer);
        meltCast(DreamtinkerFluids.molten_scolecite.get(), DreamtinkerCommon.scolecite.get(), FluidValues.GEM, consumer);

        meltCast(DreamtinkerFluids.molten_orichalcum.get(), DreamtinkerCommon.orichalcum_nugget.get(), FluidValues.NUGGET, consumer);
        meltCast(DreamtinkerFluids.molten_orichalcum.get(), DreamtinkerCommon.orichalcum.get(), FluidValues.INGOT, consumer);
        meltCastBlock(DreamtinkerFluids.molten_orichalcum.get(), DreamtinkerCommon.OrichalcumBlock.get(), FluidValues.METAL_BLOCK, consumer);

        meltCast(DreamtinkerFluids.molten_cold_iron.get(), DreamtinkerCommon.cold_iron_nugget.get(), FluidValues.NUGGET, consumer);
        meltCast(DreamtinkerFluids.molten_cold_iron.get(), DreamtinkerCommon.cold_iron_ingot.get(), FluidValues.INGOT, consumer);
        meltCastBlock(DreamtinkerFluids.molten_cold_iron.get(), DreamtinkerCommon.ColdIronBlock.get(), FluidValues.METAL_BLOCK, consumer);

        meltCast(DreamtinkerFluids.molten_shadow_silver.get(), DreamtinkerCommon.shadow_silver_nugget.get(), FluidValues.NUGGET, consumer);
        meltCast(DreamtinkerFluids.molten_shadow_silver.get(), DreamtinkerCommon.shadow_silver_ingot.get(), FluidValues.INGOT, consumer);
        meltCastBlock(DreamtinkerFluids.molten_shadow_silver.get(), DreamtinkerCommon.ShadowSilverBlock.get(), FluidValues.METAL_BLOCK, consumer);

        meltCast(DreamtinkerFluids.molten_transmutation_gold.get(), DreamtinkerCommon.transmutation_gold_nugget.get(), FluidValues.NUGGET, consumer);
        meltCast(DreamtinkerFluids.molten_transmutation_gold.get(), DreamtinkerCommon.transmutation_gold_ingot.get(), FluidValues.INGOT, consumer);
        meltCastBlock(DreamtinkerFluids.molten_transmutation_gold.get(), DreamtinkerCommon.TransmutationGoldBlock.get(), FluidValues.METAL_BLOCK, consumer);

        wrapped = withCondition(consumer, tagFilled(Dreamtinker.forgeItemTag("gems/cinnabar")));
        MeltingRecipeBuilder.melting(Ingredient.of(Dreamtinker.forgeItemTag("gems/cinnabar")),//in case someone add molten cinnabar
                                     DreamtinkerFluids.mercury.get(), FluidValues.GEM, 1.0f)
                            .save(wrapped, location(Melting_folder + "mercury/gem"));
        MeltingRecipeBuilder.melting(Ingredient.of(Dreamtinker.forgeItemTag("ingots/mercury")),
                                     DreamtinkerFluids.mercury.get(), FluidValues.GEM, 1.0f)
                            .save(wrapped, location(Melting_folder + "mercury/ingot"));

        wrapped = withCondition(consumer, tagFilled(DreamtinkerTagKeys.Items.arcaneGoldNugget));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.arcaneGoldNugget),
                                     DreamtinkerFluids.molten_arcane_gold.get(), FluidValues.NUGGET, 0.05f)
                            .save(wrapped, location(Melting_folder + "arcane_gold/nugget"));

        wrapped = withCondition(consumer, tagFilled(DreamtinkerTagKeys.Items.arcaneGoldIngot));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.arcaneGoldIngot),
                                     DreamtinkerFluids.molten_arcane_gold.get(), FluidValues.INGOT, 0.05f)
                            .save(wrapped, location(Melting_folder + "arcane_gold/ingot"));
        wrapped = withCondition(consumer, tagFilled(DreamtinkerTagKeys.Items.arcaneGoldBlock));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.arcaneGoldBlock),
                                     DreamtinkerFluids.molten_arcane_gold.get(), FluidValues.METAL_BLOCK, 0.05f)
                            .save(wrapped, location(Melting_folder + "arcane_gold/block"));
    }

    private void addCompactMeltingCastingRecipes(Consumer<FinishedRecipe> consumer) {
        addCompactELMeltingCastingRecipes(consumer);
        addCompactMalumMeltingCastingRecipes(consumer);
        addCompactEidolonMeltingCastingRecipes(consumer);
        addCompactBICMeltingCastingRecipes(consumer);
    }

    private void addCompactELMeltingCastingRecipes(Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"));
        meltCastBlock(DreamtinkerFluids.unstable_liquid_aether.get(), EnigmaticBlocks.ETHERIUM_BLOCK, FluidValues.METAL_BLOCK, consumer);
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_ORE), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT, 4.0F)
                            .addByproduct(DreamtinkerFluids.reversed_shadow.result(30))
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "etherium/ore"));
        //I am sure it's not the best way, but who cares
        int[] etherium_damage = {FluidValues.NUGGET, FluidValues.SLIME_DROP};
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_AXE), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 4, 4.0f)
                            .setDamagable(etherium_damage)
                            .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL * 2))
                            .save(wrapped, location(Melting_folder + "etherium/axe"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_SWORD), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 2, 4.0f)
                            .setDamagable(FluidValues.NUGGET, FluidValues.SLIME_DROP, FluidValues.GEM_SHARD)
                            .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL))
                            .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 2))
                            .save(wrapped, location(Melting_folder + "etherium/sword"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_SCYTHE), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 2, 4.0f)
                            .setDamagable(etherium_damage)
                            .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL * 2))
                            .save(wrapped, location(Melting_folder + "etherium/scythe"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_PICKAXE), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 3, 4.0f)
                            .setDamagable(etherium_damage)
                            .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL * 2))
                            .save(wrapped, location(Melting_folder + "etherium/pickaxe"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_SHOVEL), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT, 4.0f)
                            .setDamagable(etherium_damage)
                            .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL * 2))
                            .save(wrapped, location(Melting_folder + "etherium/shovel"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_HELMET), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 5, 4.0f)
                            .setDamagable(FluidValues.NUGGET)
                            .save(wrapped, location(Melting_folder + "etherium/helmet"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_CHESTPLATE), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 8, 4.0f)
                            .setDamagable(FluidValues.NUGGET)
                            .save(wrapped, location(Melting_folder + "etherium/chestplate"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_LEGGINGS), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 7, 4.0f)
                            .setDamagable(FluidValues.NUGGET)
                            .save(wrapped, location(Melting_folder + "etherium/leggings"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_BOOTS), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 4, 4.0f)
                            .setDamagable(FluidValues.NUGGET)
                            .save(wrapped, location(Melting_folder + "etherium/boots"));
        meltCast(DreamtinkerFluids.unstable_liquid_aether.get(), EnigmaticItems.ETHERIUM_INGOT, FluidValues.INGOT, wrapped);
        meltCast(DreamtinkerFluids.unstable_liquid_aether.get(), EnigmaticItems.ETHERIUM_NUGGET, FluidValues.NUGGET, wrapped);

        meltCast(DreamtinkerFluids.molten_evil.get(), EnigmaticItems.EVIL_INGOT, FluidValues.INGOT, wrapped);

        meltCast(DreamtinkerFluids.molten_nefariousness.get(), EnigmaticItems.EVIL_ESSENCE, FluidValues.GEM, wrapped);

        meltCast(DreamtinkerFluids.liquid_pure_soul.get(), EnigmaticItems.SOUL_CRYSTAL, FluidValues.GEM, wrapped);
        meltCast(DreamtinkerFluids.molten_soul_aether.get(), DreamtinkerCommon.soul_etherium.get(), FluidValues.INGOT, wrapped);
        fake_block_to_ingot(wrapped, DreamtinkerMaterialIds.soul_etherium, DreamtinkerCommon.soul_etherium.get());

        ItemCastingRecipeBuilder.tableRecipe(EnigmaticItems.VOID_PEARL)
                                .setCoolingTime(2000, 10)
                                .setCast(DreamtinkerCommon.void_pearl.get(), true)
                                .setFluid(FluidIngredient.of(new FluidStack(DreamtinkerFluids.molten_ascending_antimony.get(), FluidValues.METAL_BLOCK)))
                                .save(wrapped, location(Casting_folder + "void_pearl/ascending"));
        ItemCastingRecipeBuilder.tableRecipe(EnigmaticItems.OCEAN_STONE)
                                .setCoolingTime(2000, 10)
                                .setCast(Items.HEART_OF_THE_SEA, true)
                                .setFluid(FluidIngredient.of(new FluidStack(DreamtinkerFluids.molten_ascending_antimony.get(), FluidValues.METAL_BLOCK)))
                                .save(wrapped, location(Casting_folder + "ocean_stone/ascending"));
    }

    private void addCompactMalumMeltingCastingRecipes(Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("malum"));
        MeltingRecipeBuilder.melting(Ingredient.of(BlockRegistry.BLOCK_OF_SOUL_STAINED_STEEL.get()), DreamtinkerFluids.molten_soul_stained_steel,
                                     FluidValues.METAL_BLOCK, 2.0f)
                            .save(wrapped, location(Melting_folder + "soul_stained_steel/block"));
        MeltingRecipeBuilder.melting(Ingredient.of(ItemRegistry.SOUL_STAINED_STEEL_PLATING.get()), DreamtinkerFluids.molten_soul_stained_steel,
                                     65,//FluidValues.NUGGET * 6.5,
                                     2.0f)
                            .save(wrapped, location(Melting_folder + "soul_stained_steel/plating"));
        meltCast(DreamtinkerFluids.molten_soul_stained_steel.get(), ItemRegistry.SOUL_STAINED_STEEL_INGOT.get(), FluidValues.INGOT, wrapped);
        meltCast(DreamtinkerFluids.molten_soul_stained_steel.get(), ItemRegistry.SOUL_STAINED_STEEL_NUGGET.get(), FluidValues.NUGGET, wrapped);

        meltCastBlock(DreamtinkerFluids.molten_malignant_pewter.get(), BlockRegistry.BLOCK_OF_MALIGNANT_PEWTER.get(), FluidValues.METAL_BLOCK, wrapped);
        MeltingRecipeBuilder.melting(Ingredient.of(ItemRegistry.MALIGNANT_PEWTER_PLATING.get()), DreamtinkerFluids.molten_malignant_pewter, 65, 2.0f)
                            .save(wrapped, location(Melting_folder + "malignant_pewter/plating"));
        meltCast(DreamtinkerFluids.molten_malignant_pewter.get(), ItemRegistry.MALIGNANT_PEWTER_INGOT.get(), FluidValues.INGOT, wrapped);
        meltCast(DreamtinkerFluids.molten_malignant_pewter.get(), ItemRegistry.MALIGNANT_PEWTER_NUGGET.get(), FluidValues.NUGGET, wrapped);

        meltCast(DreamtinkerFluids.molten_malignant_gluttony.get(), DreamtinkerCommon.malignant_gluttony.get(), FluidValues.INGOT, wrapped);
        fake_block_to_ingot(wrapped, DreamtinkerMaterialIds.malignant_gluttony, DreamtinkerCommon.malignant_gluttony.get());
        ItemCastingRecipeBuilder.tableRecipe(ItemRegistry.CONCENTRATED_GLUTTONY.get())
                                .setCast(Items.GLASS_BOTTLE, true)
                                .setFluidAndTime(DreamtinkerFluids.liquid_concentrated_gluttony, FluidValues.BOTTLE)
                                .save(wrapped, location(Casting_folder + "filling/" +
                                                        Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ItemRegistry.CONCENTRATED_GLUTTONY.get()))
                                                               .getPath()));
    }

    private void addCompactEidolonMeltingCastingRecipes(Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("eidolon"));
        MeltingRecipeBuilder.melting(Ingredient.of(GOLD_INLAY.get()),
                                     DreamtinkerFluids.molten_arcane_gold.get(), FluidValues.INGOT * 2, 0.05f)
                            .save(wrapped, location(Melting_folder + "arcane_gold/inlay"));
        MeltingRecipeBuilder.melting(Ingredient.of(PEWTER_INLAY.get()),
                                     TinkerFluids.moltenPewter, FluidValues.INGOT * 2, 0.05f)
                            .save(wrapped, location(Melting_folder + "pewter/inlay"));
        MeltingRecipeBuilder.melting(Ingredient.of(PEWTER_BLEND.get()),
                                     TinkerFluids.moltenPewter, FluidValues.INGOT, 0.05f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "pewter/blend"));

        cast(DreamtinkerFluids.molten_arcane_gold.get(), ARCANE_GOLD_NUGGET.get(), FluidValues.NUGGET, wrapped);
        cast(DreamtinkerFluids.molten_arcane_gold.get(), ARCANE_GOLD_INGOT.get(), FluidValues.INGOT, wrapped);
        cast(DreamtinkerFluids.molten_arcane_gold.get(), ARCANE_GOLD_BLOCK.get(), FluidValues.METAL_BLOCK, wrapped);
    }

    private void addCompactBICMeltingCastingRecipes(Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"));
        MeltingRecipeBuilder.melting(Ingredient.of(DARK_METAL_DEPOSIT.get()),
                                     DreamtinkerFluids.molten_dark_metal.get(), FluidValues.INGOT, 0.05f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "dark_metal/ore"));
        MeltingRecipeBuilder.melting(Ingredient.of(PIECEOFDARKMETAL.get()),
                                     DreamtinkerFluids.molten_dark_metal.get(), FluidValues.NUGGET, 0.05f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "dark_metal/piece"));
        MeltingRecipeBuilder.melting(Ingredient.of(PILEOF_DARK_METAL.get()),
                                     DreamtinkerFluids.molten_dark_metal.get(), FluidValues.NUGGET, 0.05f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "dark_metal/pile"));
        MeltingRecipeBuilder.melting(Ingredient.of(ARMOR_PLATE_FROM_DARK_METAL.get()),
                                     DreamtinkerFluids.molten_dark_metal.get(), FluidValues.INGOT * 5, 0.05f)
                            .save(wrapped, location(Melting_folder + "dark_metal/plate"));
        MeltingRecipeBuilder.melting(Ingredient.of(DARK_GRID.get()),
                                     DreamtinkerFluids.molten_dark_metal.get(), FluidValues.INGOT / 4, 0.05f)
                            .save(wrapped, location(Melting_folder + "dark_metal/grid"));
        MeltingRecipeBuilder.melting(Ingredient.of(INFECTED_DIAMOND_ORE.get(), INFECTED_DEEPSLATE_DIAMOND_ORE.get()),
                                     TinkerFluids.moltenDiamond, FluidValues.GEM, 0.05f)
                            .setOre(IMeltingContainer.OreRateType.GEM)
                            .addByproduct(TinkerFluids.moltenDebris.result(FluidValues.GEM / 2))
                            .addByproduct(DreamtinkerFluids.molten_dark_metal.result(FluidValues.INGOT / 2))
                            .save(wrapped, location(Melting_folder + "diamond/infected"));
        meltCast(DreamtinkerFluids.molten_dark_metal.get(), DARK_METAL_NUGGET.get(), FluidValues.NUGGET, wrapped);
        meltCast(DreamtinkerFluids.molten_dark_metal.get(), DARK_METAL_INGOT.get(), FluidValues.INGOT, wrapped);
        meltCastBlock(DreamtinkerFluids.molten_dark_metal.get(), DARK_METAL_BLOCK.get(), FluidValues.METAL_BLOCK, wrapped);
        int[] darkMetalArmorSizes = {FluidValues.NUGGET, FluidValues.SLIME_DROP, FluidValues.GEM_SHARD};
        MeltingRecipeBuilder.melting(Ingredient.of(DARK_METAL_ARMOR_HELMET.get()), DreamtinkerFluids.molten_dark_metal.get(), FluidValues.INGOT * 5)
                            .setDamagable(darkMetalArmorSizes)
                            .addByproduct(TinkerFluids.moltenNetherite.result(FluidValues.INGOT))
                            .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 5))
                            .save(consumer, location(metalFolder + "dark_metal/helmet"));
        MeltingRecipeBuilder.melting(Ingredient.of(DARK_METAL_ARMOR_CHESTPLATE.get()), DreamtinkerFluids.molten_dark_metal.get(), FluidValues.INGOT * 5)
                            .setDamagable(darkMetalArmorSizes)
                            .addByproduct(TinkerFluids.moltenNetherite.result(FluidValues.INGOT))
                            .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 8))
                            .save(consumer, location(metalFolder + "dark_metal/chestplate"));
        MeltingRecipeBuilder.melting(Ingredient.of(DARK_METAL_ARMOR_LEGGINGS.get()), DreamtinkerFluids.molten_dark_metal.get(), FluidValues.INGOT * 5)
                            .setDamagable(darkMetalArmorSizes)
                            .addByproduct(TinkerFluids.moltenNetherite.result(FluidValues.INGOT))
                            .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 7))
                            .save(consumer, location(metalFolder + "dark_metal/leggings"));
        MeltingRecipeBuilder.melting(Ingredient.of(DARK_METAL_ARMOR_BOOTS.get()), DreamtinkerFluids.molten_dark_metal.get(), FluidValues.INGOT * 5)
                            .setDamagable(darkMetalArmorSizes)
                            .addByproduct(TinkerFluids.moltenNetherite.result(FluidValues.INGOT))
                            .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 4))
                            .save(consumer, location(metalFolder + "dark_metal/boots"));
        MeltingRecipeBuilder.melting(Ingredient.of(DARKWARBLADE.get()), DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.METAL_BLOCK * 2 + FluidValues.NUGGET)
                            .setDamagable(FluidValues.INGOT)
                            .save(consumer, location(metalFolder + "dark_metal/warblade"));
        MeltingRecipeBuilder.melting(Ingredient.of(DARK_RITUAL_DAGGER.get(), SPIRITUAL_SWORD.get(), BONESCALLER_STAFF.get(), SHELL_MACE.get()),
                                     DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.NUGGET * 3)
                            .setDamagable(FluidValues.NUGGET / 2)
                            .save(consumer, location(metalFolder + "dark_metal/dagger"));
        MeltingRecipeBuilder.melting(Ingredient.of(SHARPENED_DARK_METAL_SWORD.get(), SPIDER_BITE_SWORD.get()), DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.INGOT * 2 + FluidValues.NUGGET)
                            .setDamagable(FluidValues.NUGGET)
                            .save(consumer, location(metalFolder + "dark_metal/sword"));
        MeltingRecipeBuilder.melting(Ingredient.of(FROSTBITTEN_BLADE.get(), INTOXICATING_DAGGER.get()), DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.INGOT + FluidValues.NUGGET)
                            .setDamagable(FluidValues.NUGGET)
                            .save(consumer, location(metalFolder + "dark_metal/frostbitten_blade"));
        MeltingRecipeBuilder.melting(Ingredient.of(SOUL_CUTLASS.get(), NIGHTMARE_SCYTHE.get()), DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.INGOT + FluidValues.NUGGET * 2)
                            .setDamagable(FluidValues.NUGGET)
                            .save(consumer, location(metalFolder + "dark_metal/soul_cutlass"));
        MeltingRecipeBuilder.melting(Ingredient.of(GREAT_REAPER_AXE.get()), DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.INGOT * 4 + FluidValues.NUGGET * 2)
                            .setDamagable(FluidValues.NUGGET)
                            .save(consumer, location(metalFolder + "dark_metal/greater_axe"));
        MeltingRecipeBuilder.melting(Ingredient.of(SKULLBREAKER_HAMMER.get()), DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.METAL_BLOCK + FluidValues.NUGGET * 2)
                            .setDamagable(FluidValues.INGOT)
                            .save(consumer, location(metalFolder + "dark_metal/skull_breaker_hammer"));

        EntityMeltingRecipeBuilder.melting(
                                          EntityIngredient.of(FALLEN_CHAOS_KNIGHT.get(), SKELETON_THRASHER.get(), DIAMOND_TERMITE.get()),
                                          DreamtinkerFluids.molten_dark_metal.result(FluidValues.NUGGET), 5)
                                  .save(consumer, location(Entity_Melting_folder + "molten_dark_metal/elite"));
        EntityMeltingRecipeBuilder.melting(
                                          EntityIngredient.of(MISSIONER.get(), LIFESTEALER.get(), KRAMPUS.get()),
                                          DreamtinkerFluids.molten_dark_metal.result(FluidValues.NUGGET), 10)
                                  .save(consumer, location(Entity_Melting_folder + "molten_dark_metal/boss"));
    }

    String materials_folder = "tools/materials/";
    String slimeskinFolder = materials_folder + "slimeskin/";

    private void addMaterialRecipes(Consumer<FinishedRecipe> consumer) {

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.crying_obsidian, DreamtinkerFluids.molten_crying_obsidian, FluidValues.GLASS_BLOCK,
                               materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.crying_obsidian, Ingredient.of(Items.CRYING_OBSIDIAN), 1, 1, materials_folder + "crying_obsidian");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.echo_alloy, DreamtinkerFluids.molten_echo_alloy, FluidValues.GEM, materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.echo_alloy, Ingredient.of(DreamtinkerCommon.echo_alloy.get()), 1, 1, materials_folder + "echo_alloy");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.metallivorous_stibium_lupus, DreamtinkerFluids.molten_lupi_antimony, FluidValues.INGOT,
                               materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.metallivorous_stibium_lupus, Ingredient.of(DreamtinkerCommon.metallivorous_stibium_lupus.get()), 1, 1,
                       materials_folder + "metallivorous_stibium_lupus");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.nigrescence_antimony, DreamtinkerFluids.molten_nigrescence_antimony, FluidValues.GEM,
                               materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.nigrescence_antimony, Ingredient.of(DreamtinkerCommon.nigrescence_antimony.get()), 1, 1,
                       materials_folder + "nigrescence_antimony");

        materialComposite(consumer, MaterialIds.string, DreamtinkerMaterialIds.nigrescence_string, DreamtinkerFluids.molten_nigrescence_antimony,
                          FluidValues.GEM, materials_folder);

        materialRecipe(consumer, DreamtinkerMaterialIds.star_regulus, Ingredient.of(DreamtinkerCommon.regulus.get()), 1, 1,
                       materials_folder + "star_regulus");
        materialRecipe(consumer, DreamtinkerMaterialIds.valentinite, Ingredient.of(DreamtinkerCommon.valentinite.get()), 1, 1,
                       materials_folder + "valentinite");
        materialRecipe(consumer, DreamtinkerMaterialIds.larimar, Ingredient.of(DreamtinkerCommon.larimar.get()), 1, 1,
                       materials_folder + "larimar");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.amber, DreamtinkerFluids.liquid_amber, FluidValues.GEM, materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.amber, Ingredient.of(DreamtinkerCommon.amber.get()), 1, 1,
                       materials_folder + "amber");

        MaterialFluidRecipeBuilder.material(DreamtinkerMaterialIds.half_rotten_homunculus)
                                  .setFluid(DreamtinkerFluids.half_festering_blood.ingredient(FluidValues.BOTTLE))
                                  .setTemperature(10).save(consumer, this.location(
                                          materials_folder + "half_rotten_homunculus" + "casting/" + DreamtinkerMaterialIds.half_rotten_homunculus.getLocation('_').getPath()));
        materialRecipe(consumer, DreamtinkerMaterialIds.half_rotten_homunculus, Ingredient.of(DreamtinkerCommon.poisonousHomunculus.get()), 1, 1,
                       materials_folder + "half_rotten_homunculus");

        materialComposite(consumer, MaterialIds.string, DreamtinkerMaterialIds.half_rotten_string, DreamtinkerFluids.half_festering_blood,
                          FluidValues.BOTTLE, materials_folder);

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.desire_gem, DreamtinkerFluids.molten_desire, FluidValues.GEM,
                               materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.desire_gem, Ingredient.of(DreamtinkerCommon.desire_gem.get()), 1, 1,
                       materials_folder + "desire_gem");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.despair_gem, DreamtinkerFluids.despair_essence, FluidValues.GEM,
                               materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.despair_gem, Ingredient.of(DreamtinkerCommon.despair_gem.get()), 1, 1,
                       materials_folder + "despair_gem");

        materialComposite(consumer, MaterialIds.leather, DreamtinkerMaterialIds.shadowskin, DreamtinkerFluids.molten_void, FluidValues.SLIMEBALL,
                          slimeskinFolder, "shadowskin");
        materialComposite(consumer, DreamtinkerMaterialIds.shadowskin, MaterialIds.leather, TinkerFluids.venom, FluidValues.SIP, slimeskinFolder,
                          "shadowskin_cleaning");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.soul_steel, DreamtinkerFluids.molten_soul_steel, FluidValues.INGOT,
                               materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.soul_steel, Ingredient.of(DreamtinkerCommon.soul_steel.get()), 1, 1,
                       materials_folder + "soul_steel");

        materialRecipe(consumer, DreamtinkerMaterialIds.soul_steel, Ingredient.of(DreamtinkerCommon.soulSteelBlock.get()), 9, 1,
                       materials_folder + "soul_steel_block");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.rainbow_honey_crystal, DreamtinkerFluids.molten_bee_gem, FluidValues.GEM,
                               materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.rainbow_honey_crystal, Ingredient.of(DreamtinkerCommon.rainbow_honey_crystal.get()), 1, 1,
                       materials_folder + "rainbow_honey_crystal");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.black_sapphire, DreamtinkerFluids.molten_black_sapphire, FluidValues.GEM,
                               materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.black_sapphire, Ingredient.of(DreamtinkerCommon.black_sapphire.get()), 1, 1,
                       materials_folder + "black_sapphire");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.scolecite, DreamtinkerFluids.molten_scolecite, FluidValues.GEM,
                               materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.scolecite, Ingredient.of(DreamtinkerCommon.scolecite.get()), 1, 1,
                       materials_folder + "scolecite");

        materialRecipe(consumer, DreamtinkerMaterialIds.shiningFlint, Ingredient.of(DreamtinkerCommon.shiningFlint.get()), 1, 1,
                       materials_folder + "shining_flint");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.orichalcum, DreamtinkerFluids.molten_orichalcum, FluidValues.INGOT,
                               materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.orichalcum, Ingredient.of(DreamtinkerTagKeys.Items.OrichalcumIngot), 1, 1,
                       materials_folder + "orichalcum/ingot");
        materialRecipe(consumer, DreamtinkerMaterialIds.orichalcum, Ingredient.of(DreamtinkerTagKeys.Items.OrichalcumNuggets), 1, 9,
                       materials_folder + "orichalcum/nugget");
        materialRecipe(consumer, DreamtinkerMaterialIds.orichalcum, Ingredient.of(DreamtinkerTagKeys.Items.OrichalcumBlock), 9, 1,
                       materials_folder + "orichalcum/block");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.cold_iron, DreamtinkerFluids.molten_cold_iron, FluidValues.INGOT,
                               materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.cold_iron, Ingredient.of(DreamtinkerTagKeys.Items.coldIronIngot), 1, 1,
                       materials_folder + "cold_iron/ingot");
        materialRecipe(consumer, DreamtinkerMaterialIds.cold_iron, Ingredient.of(DreamtinkerTagKeys.Items.coldIronNuggets), 1, 9,
                       materials_folder + "cold_iron/nugget");
        materialRecipe(consumer, DreamtinkerMaterialIds.cold_iron, Ingredient.of(DreamtinkerTagKeys.Items.coldIronBlock), 9, 1,
                       materials_folder + "cold_iron/block");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.shadowSilver, DreamtinkerFluids.molten_shadow_silver, FluidValues.INGOT,
                               materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.shadowSilver, Ingredient.of(DreamtinkerTagKeys.Items.ShadowSilverIngot), 1, 1,
                       materials_folder + "shadow_silver/ingot");
        materialRecipe(consumer, DreamtinkerMaterialIds.shadowSilver, Ingredient.of(DreamtinkerTagKeys.Items.ShadowSilverNuggets), 1, 9,
                       materials_folder + "shadow_silver/nugget");
        materialRecipe(consumer, DreamtinkerMaterialIds.shadowSilver, Ingredient.of(DreamtinkerTagKeys.Items.ShadowSilverBlock), 9, 1,
                       materials_folder + "shadow_silver/block");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.TransmutationGold, DreamtinkerFluids.molten_transmutation_gold, FluidValues.INGOT,
                               materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.TransmutationGold, Ingredient.of(DreamtinkerTagKeys.Items.TransmutationGoldIngot), 1, 1,
                       materials_folder + "transmutation_gold/ingot");
        materialRecipe(consumer, DreamtinkerMaterialIds.TransmutationGold, Ingredient.of(DreamtinkerTagKeys.Items.TransmutationGoldDusts), 1, 1,
                       materials_folder + "transmutation_gold/dust");
        materialRecipe(consumer, DreamtinkerMaterialIds.TransmutationGold, Ingredient.of(DreamtinkerTagKeys.Items.TransmutationGoldNuggets), 1, 9,
                       materials_folder + "transmutation_gold/nugget");
        materialRecipe(consumer, DreamtinkerMaterialIds.TransmutationGold, Ingredient.of(DreamtinkerTagKeys.Items.TransmutationGoldBlock), 9, 1,
                       materials_folder + "transmutation_gold/block");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.ArcaneGold, DreamtinkerFluids.molten_arcane_gold, FluidValues.INGOT,
                               materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.ArcaneGold, Ingredient.of(DreamtinkerTagKeys.Items.arcaneGoldIngot), 1, 1,
                       materials_folder + "arcane_gold/ingot");
        materialRecipe(consumer, DreamtinkerMaterialIds.ArcaneGold, Ingredient.of(DreamtinkerTagKeys.Items.arcaneGoldNugget), 1, 9,
                       materials_folder + "arcane_gold/nugget");
        materialRecipe(consumer, DreamtinkerMaterialIds.ArcaneGold, Ingredient.of(DreamtinkerTagKeys.Items.arcaneGoldBlock), 9, 1,
                       materials_folder + "arcane_gold/block");

        materialRecipe(consumer, DreamtinkerMaterialIds.SpikyShard, Ingredient.of(DreamtinkerCommon.deep_prismarine_shard.get()), 1, 1,
                       materials_folder + "spiny_shell");

    }

    private void addCompactMaterialRecipes(Consumer<FinishedRecipe> consumer) {
        addELMaterialRecipes(consumer);
        addMalumMaterialRecipes(consumer);
        addEidolonMaterialRecipes(consumer);
        addBICMaterialRecipes(consumer);
        addNovaMaterialRecipes(consumer);
    }

    private void addELMaterialRecipes(Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"));
        materialMeltingCasting(wrapped, DreamtinkerMaterialIds.etherium, DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT, materials_folder);
        materialRecipe(wrapped, DreamtinkerMaterialIds.etherium, Ingredient.of(EnigmaticItems.ETHERIUM_INGOT), 1, 1,
                       materials_folder + "etherium");
        materialComposite(wrapped, MaterialIds.string, DreamtinkerMaterialIds.etherium, DreamtinkerFluids.unstable_liquid_aether,
                          FluidValues.INGOT, materials_folder);
        materialMeltingCasting(wrapped, DreamtinkerMaterialIds.nefarious, DreamtinkerFluids.molten_evil, FluidValues.INGOT, materials_folder);
        materialRecipe(wrapped, DreamtinkerMaterialIds.nefarious, Ingredient.of(EnigmaticItems.EVIL_INGOT), 1, 1,
                       materials_folder + "nefarious");
        materialComposite(wrapped, MaterialIds.string, DreamtinkerMaterialIds.nefarious, DreamtinkerFluids.molten_evil,
                          FluidValues.INGOT, materials_folder);
        materialMeltingCasting(wrapped, DreamtinkerMaterialIds.soul_etherium, DreamtinkerFluids.molten_soul_aether, FluidValues.INGOT, materials_folder);
        materialRecipe(wrapped, DreamtinkerMaterialIds.soul_etherium, Ingredient.of(DreamtinkerCommon.soul_etherium.get()), 1, 1,
                       materials_folder + "soul_etherium");
        materialComposite(wrapped, MaterialIds.string, DreamtinkerMaterialIds.soul_etherium, DreamtinkerFluids.molten_soul_aether,
                          FluidValues.INGOT, materials_folder);
    }

    private void addMalumMaterialRecipes(Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("malum"));
        materialRecipe(wrapped, DreamtinkerMaterialIds.spirit_fabric, Ingredient.of(ItemRegistry.SPIRIT_FABRIC.get()), 1, 3,
                       materials_folder + "spirit_fabric");
        materialRecipe(wrapped, DreamtinkerMaterialIds.hallowed_gold, Ingredient.of(ItemRegistry.HALLOWED_GOLD_INGOT.get()), 1, 1,
                       materials_folder + "hallowed_gold");
        materialRecipe(wrapped, DreamtinkerMaterialIds.mnemonic, Ingredient.of(ItemRegistry.MNEMONIC_FRAGMENT.get()), 1, 4,
                       materials_folder + "mnemonic_fragment/mnemonic");
        materialRecipe(wrapped, DreamtinkerMaterialIds.auric, Ingredient.of(ItemRegistry.AURIC_EMBERS.get()), 1, 4,
                       materials_folder + "mnemonic_fragment/auric");
        materialRecipe(wrapped, DreamtinkerMaterialIds.soul_stained_steel, Ingredient.of(ItemRegistry.SOUL_STAINED_STEEL_PLATING.get()), 1, 2,
                       materials_folder + "soul_stained_steel");
        materialMeltingCasting(wrapped, DreamtinkerMaterialIds.soul_stained_steel, DreamtinkerFluids.molten_soul_stained_steel, 130,
                               materials_folder);
        materialRecipe(wrapped, DreamtinkerMaterialIds.malignant_pewter, Ingredient.of(ItemRegistry.MALIGNANT_PEWTER_PLATING.get()), 1, 2,
                       materials_folder + "malignant_pewter");
        materialMeltingCasting(wrapped, DreamtinkerMaterialIds.malignant_pewter, DreamtinkerFluids.molten_malignant_pewter, 130,
                               materials_folder);
        materialRecipe(wrapped, DreamtinkerMaterialIds.malignant_gluttony, Ingredient.of(DreamtinkerCommon.malignant_gluttony.get()), 1, 1,
                       materials_folder + "malignant_gluttony");
        materialMeltingCasting(wrapped, DreamtinkerMaterialIds.malignant_gluttony, DreamtinkerFluids.molten_malignant_gluttony, FluidValues.INGOT,
                               materials_folder);
        materialRecipe(wrapped, DreamtinkerMaterialIds.tainted, Ingredient.of(ItemTagRegistry.TAINTED_BLOCKS), 1, 1, materials_folder + "soul_rock/tainted");
        materialRecipe(wrapped, DreamtinkerMaterialIds.twisted, Ingredient.of(ItemTagRegistry.TWISTED_BLOCKS), 1, 1, materials_folder + "soul_rock/twisted");
        materialRecipe(wrapped, DreamtinkerMaterialIds.refined, Ingredient.of(ItemRegistry.PROCESSED_SOULSTONE.get()), 1, 1,
                       materials_folder + "soul_rock/refined");

        materialRecipe(consumer, DreamtinkerMaterialIds.blazing_quartz, Ingredient.of(ItemRegistry.BLAZING_QUARTZ.get()), 1, 1,
                       materials_folder + "blazing_quartz");
        for (MalumSpiritType types : SpiritTypeRegistry.SPIRITS.values()) {
            String name = types.identifier;
            materialRecipe(wrapped, MaterialVariantId.create(DreamtinkerMaterialIds.spirits, name), Ingredient.of(types.spiritShard.get()), 1, 1,
                           materials_folder + "spirits/" + name);
        }
        materialRecipe(wrapped, DreamtinkerMaterialIds.grim_talc, Ingredient.of(ItemRegistry.GRIM_TALC.get()), 1, 1, materials_folder + "grim_talc/item");
        materialRecipe(wrapped, DreamtinkerMaterialIds.grim_talc, Ingredient.of(BlockRegistry.BLOCK_OF_GRIM_TALC.get()), 9, 1,
                       materials_folder + "grim_talc/block");

        materialRecipe(wrapped, DreamtinkerMaterialIds.astral_weave, Ingredient.of(ItemRegistry.ASTRAL_WEAVE.get()), 1, 1,
                       materials_folder + "astral_weave/item");
        materialRecipe(wrapped, DreamtinkerMaterialIds.astral_weave, Ingredient.of(BlockRegistry.BLOCK_OF_ASTRAL_WEAVE.get()), 9, 1,
                       materials_folder + "astral_weave/block");
        materialRecipe(wrapped, DreamtinkerMaterialIds.null_slate, Ingredient.of(ItemRegistry.NULL_SLATE.get()), 1, 1,
                       materials_folder + "null_slate/item");
        materialRecipe(wrapped, DreamtinkerMaterialIds.null_slate, Ingredient.of(BlockRegistry.BLOCK_OF_NULL_SLATE.get()), 9, 1,
                       materials_folder + "null_slate/block");


    }

    private void addEidolonMaterialRecipes(Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("eidolon"));
        materialRecipe(wrapped, DreamtinkerMaterialIds.TatteredCloth, Ingredient.of(TATTERED_CLOTH.get()), 1, 2,
                       materials_folder + "tattered_cloth");
        materialRecipe(wrapped, DreamtinkerMaterialIds.WickedWeave, Ingredient.of(WICKED_WEAVE.get()), 1, 2,
                       materials_folder + "wicked_weave");
        materialRecipe(wrapped, DreamtinkerMaterialIds.PaladinBoneTool, Ingredient.of(IMBUED_BONES.get()), 1, 1,
                       materials_folder + "paladin_bone_tool");
        materialRecipe(wrapped, DreamtinkerMaterialIds.SoulGem, Ingredient.of(SOUL_SHARD.get()), 1, 4,
                       materials_folder + "soul_gem");
        materialRecipe(wrapped, DreamtinkerMaterialIds.CrimsonGem, Ingredient.of(CRIMSON_GEM.get()), 1, 1,
                       materials_folder + "crimson_gem");
        materialRecipe(wrapped, DreamtinkerMaterialIds.ShadowGem, Ingredient.of(Dreamtinker.forgeItemTag("gems/shadow_gem")), 1, 1,
                       materials_folder + "shadow_gem/gem");
        materialRecipe(wrapped, DreamtinkerMaterialIds.ShadowGem, Ingredient.of(Dreamtinker.forgeItemTag("storage_blocks/shadow_gem")), 9, 1,
                       materials_folder + "shadow_gem/block");
    }

    private void addBICMaterialRecipes(Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"));
        materialMeltingCasting(wrapped, DreamtinkerMaterialIds.DarkMetal, DreamtinkerFluids.molten_dark_metal, FluidValues.INGOT * 5,
                               materials_folder);
        materialRecipe(wrapped, DreamtinkerMaterialIds.DarkMetal, Ingredient.of(DARK_METAL_INGOT.get()), 1, 5,
                       materials_folder + "dark_metal/ingot");
        materialRecipe(wrapped, DreamtinkerMaterialIds.DarkMetal, Ingredient.of(ARMOR_PLATE_FROM_DARK_METAL.get()), 1, 1,
                       materials_folder + "dark_metal/armor_plate");

        materialRecipe(wrapped, DreamtinkerMaterialIds.MonsterSkin, Ingredient.of(MONSTER_SKIN.get()), 1, 1,
                       materials_folder + "monster_skin");
        materialComposite(wrapped, MaterialIds.leather, DreamtinkerMaterialIds.MonsterSkin, DreamtinkerFluids.molten_dark_metal, FluidValues.INGOT,
                          slimeskinFolder, "monsterskin");
        materialComposite(wrapped, DreamtinkerMaterialIds.MonsterSkin, MaterialIds.leather, TinkerFluids.venom, FluidValues.SIP, slimeskinFolder,
                          "monsterskin_cleaning");
        materialRecipe(wrapped, DreamtinkerMaterialIds.SpikyShard, Ingredient.of(SPINY_SHELL.get()), 1, 1,
                       materials_folder + "spiny_shell_bic");
        materialRecipe(wrapped, DreamtinkerMaterialIds.LifeStealerBone, Ingredient.of(LIFESTEALER_BONE.get()), 1, 1,
                       materials_folder + "life_stealer");
        materialRecipe(wrapped, DreamtinkerMaterialIds.KrampusHorn, Ingredient.of(KRAMPUS_HORN.get()), 1, 1,
                       materials_folder + "krampus_horn");
        materialRecipe(wrapped, DreamtinkerMaterialIds.NightMareClaw, Ingredient.of(NIGHTMARE_CLAW.get()), 1, 1,
                       materials_folder + "nightmare_claw/claw");
        materialRecipe(wrapped, DreamtinkerMaterialIds.NightMareClaw, Ingredient.of(NIGHTMARE_STALKER_SKULL.get()), 4, 1,
                       materials_folder + "nightmare_claw/head");
        materialRecipe(wrapped, DreamtinkerMaterialIds.InfernalEmber, Ingredient.of(SMOLDERING_INFERNAL_EMBER.get()), 1, 1,
                       materials_folder + "infernal_ember/item");
        materialRecipe(wrapped, DreamtinkerMaterialIds.InfernalEmber, Ingredient.of(FEL_SOIL.get()), 4, 1,
                       materials_folder + "infernal_ember/block");
        materialRecipe(wrapped, DreamtinkerMaterialIds.SpiderMandible, Ingredient.of(SPIDER_MANDIBLE.get()), 1, 1,
                       materials_folder + "spider_mandible");
        materialRecipe(wrapped, DreamtinkerMaterialIds.HoundFang, Ingredient.of(FANGOFTHE_HOUND_LEADER.get()), 1, 1,
                       materials_folder + "hound_fang");
    }

    private void addNovaMaterialRecipes(Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"));
        materialRecipe(wrapped, DreamtinkerMaterialIds.AbjurationEssence, Ingredient.of(ItemsRegistry.ABJURATION_ESSENCE.get()), 1, 1,
                       materials_folder + "abjuration_essence");
        materialRecipe(wrapped, DreamtinkerMaterialIds.ConjurationEssence, Ingredient.of(ItemsRegistry.CONJURATION_ESSENCE.get()), 1, 1,
                       materials_folder + "conjuration_essence");
        materialRecipe(wrapped, DreamtinkerMaterialIds.AirEssence, Ingredient.of(ItemsRegistry.AIR_ESSENCE.get()), 1, 1,
                       materials_folder + "air_essence");
        materialRecipe(wrapped, DreamtinkerMaterialIds.EarthEssence, Ingredient.of(ItemsRegistry.EARTH_ESSENCE.get()), 1, 1,
                       materials_folder + "earth_essence");
        materialRecipe(wrapped, DreamtinkerMaterialIds.FireEssence, Ingredient.of(ItemsRegistry.FIRE_ESSENCE.get()), 1, 1,
                       materials_folder + "fire_essence");
        materialRecipe(wrapped, DreamtinkerMaterialIds.ManipulationEssence, Ingredient.of(ItemsRegistry.MANIPULATION_ESSENCE.get()), 1, 1,
                       materials_folder + "manipulation_essence");
        materialRecipe(wrapped, DreamtinkerMaterialIds.WaterEssence, Ingredient.of(ItemsRegistry.WATER_ESSENCE.get()), 1, 1,
                       materials_folder + "water_essence");
        materialRecipe(wrapped, DreamtinkerMaterialIds.WildenHorn, Ingredient.of(ItemsRegistry.WILDEN_HORN.get()), 1, 1,
                       materials_folder + "wilden_horn");
        materialRecipe(wrapped, DreamtinkerMaterialIds.WildenWing, Ingredient.of(ItemsRegistry.WILDEN_WING.get()), 1, 1,
                       materials_folder + "wilden_wing");
        materialRecipe(wrapped, DreamtinkerMaterialIds.WildenSpike, Ingredient.of(ItemsRegistry.WILDEN_SPIKE.get()), 1, 1,
                       materials_folder + "wilden_spike");
    }

    String common_folder = "common/";

    private void night_one_receipts(Consumer<FinishedRecipe> consumer, Item ingotLike, Item BlockLike) {
        String i2b = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ingotLike)).getPath() + "_to_" +
                     Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(BlockLike)).getPath();
        String b2i = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(BlockLike)).getPath() + "_to_" +
                     Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ingotLike)).getPath();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BlockLike)
                           .define('e', ingotLike)
                           .pattern("eee")
                           .pattern("eee")
                           .pattern("eee")
                           .unlockedBy("has_" + Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ingotLike)).getPath(), has(ingotLike))
                           .save(consumer, location(common_folder + i2b));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingotLike, 9)
                              .requires(BlockLike)
                              .unlockedBy("has_" + Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ingotLike)).getPath(), has(BlockLike))
                              .save(consumer, location(common_folder + b2i));
    }

    private ItemStack fake_block(MaterialVariantId id) {
        ItemStack fake_block = new ItemStack(TinkerToolParts.fakeStorageBlock.get());
        CompoundTag nbt = new CompoundTag();
        nbt.putString("Material", id.toString());
        fake_block.getOrCreateTag().merge(nbt);
        return fake_block;
    }

    private void fake_block_to_ingot(Consumer<FinishedRecipe> consumer, MaterialVariantId id, Item ingotLike) {
        ItemStack fake_block = fake_block(id);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingotLike, 9)
                              .requires(StrictNBTIngredient.of(fake_block))
                              .unlockedBy("has_item", has(TinkerToolParts.fakeStorageBlock))
                              .save(consumer, location(
                                      partFolder + "fake_block_to_ingots/" +
                                      (id.getVariant().isBlank() || id.getVariant().isEmpty() ? id.getId().getPath() : id.getVariant())));
    }

    private void addCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, DreamtinkerCommon.nigrescence_antimony.get(), 1)
                              .requires(DreamtinkerCommon.valentinite.get())
                              .requires(Items.ROTTEN_FLESH)
                              .unlockedBy("has_valentinite", has(DreamtinkerCommon.valentinite.get()))
                              .save(consumer, location(common_folder + "currus_triumphalis_antimonii/valentinite_nigredo"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(DreamtinkerTagKeys.Items.raw_stibnite),
                                            RecipeCategory.MISC,
                                            DreamtinkerCommon.valentinite.get(),
                                            1.0f,
                                            100)
                                  .unlockedBy("has_stibnite", has(DreamtinkerTagKeys.Items.raw_stibnite))
                                  .save(consumer, location(common_folder + "currus_triumphalis_antimonii/stibnite_to_valentinite" + "_blast"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(DreamtinkerTagKeys.Items.raw_orichalcum),
                                            RecipeCategory.MISC,
                                            DreamtinkerCommon.orichalcum.get(),
                                            3.0f,
                                            100)
                                  .unlockedBy("has_orichalcum", has(DreamtinkerTagKeys.Items.raw_orichalcum))
                                  .save(consumer, location(common_folder + "raw_orichalcum" + "/blasting"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(DreamtinkerTagKeys.Items.OrichalcumOre),
                                            RecipeCategory.MISC,
                                            DreamtinkerCommon.orichalcum.get(),
                                            3.0f,
                                            200)
                                  .unlockedBy("has_orichalcum_ore", has(DreamtinkerTagKeys.Items.OrichalcumOre))
                                  .save(consumer, location(common_folder + "orichalcum_ore" + "/blasting"));

        SimpleCookingRecipeBuilder.blasting(Ingredient.of(DreamtinkerTagKeys.Items.raw_coldIron),
                                            RecipeCategory.MISC,
                                            DreamtinkerCommon.cold_iron_ingot.get(),
                                            4.0f,
                                            100)
                                  .unlockedBy("has_cold_iron", has(DreamtinkerTagKeys.Items.raw_coldIron))
                                  .save(consumer, location(common_folder + "raw_cold_iron" + "/blasting"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(DreamtinkerTagKeys.Items.coldIronOre),
                                            RecipeCategory.MISC,
                                            DreamtinkerCommon.cold_iron_ingot.get(),
                                            3.0f,
                                            100)
                                  .unlockedBy("has_cold_iron_ore", has(DreamtinkerTagKeys.Items.coldIronOre))
                                  .save(consumer, location(common_folder + "cold_iron_ore" + "/blasting"));

        SimpleCookingRecipeBuilder.blasting(Ingredient.of(DreamtinkerTagKeys.Items.raw_ShadowSilver),
                                            RecipeCategory.MISC,
                                            DreamtinkerCommon.shadow_silver_ingot.get(),
                                            4.0f,
                                            100)
                                  .unlockedBy("has_shadow_silver", has(DreamtinkerTagKeys.Items.raw_ShadowSilver))
                                  .save(consumer, location(common_folder + "raw_shadow_silver" + "/blasting"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(DreamtinkerTagKeys.Items.ShadowSilverOre),
                                            RecipeCategory.MISC,
                                            DreamtinkerCommon.shadow_silver_ingot.get(),
                                            4.0f,
                                            100)
                                  .unlockedBy("has_shadow_silver_ore", has(DreamtinkerTagKeys.Items.ShadowSilverOre))
                                  .save(consumer, location(common_folder + "shadow_silver_ore" + "/blasting"));

        SimpleCookingRecipeBuilder.blasting(Ingredient.of(DreamtinkerTagKeys.Items.raw_TransmutationGold),
                                            RecipeCategory.MISC,
                                            DreamtinkerCommon.transmutation_gold_ingot.get(),
                                            4.0f,
                                            100)
                                  .unlockedBy("has_transmutation_gold", has(DreamtinkerTagKeys.Items.raw_TransmutationGold))
                                  .save(consumer, location(common_folder + "raw_transmutation_gold" + "/blasting"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(DreamtinkerTagKeys.Items.TransmutationGoldOre),
                                            RecipeCategory.MISC,
                                            DreamtinkerCommon.transmutation_gold_ingot.get(),
                                            4.0f,
                                            100)
                                  .unlockedBy("has_transmutation_gold_ore", has(DreamtinkerTagKeys.Items.TransmutationGoldOre))
                                  .save(consumer, location(common_folder + "transmutation_goldr_ore" + "/blasting"));

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(DreamtinkerTagKeys.Items.blackSapphireOre),
                                            RecipeCategory.MISC,
                                            DreamtinkerCommon.black_sapphire.get(),
                                            3.0f,
                                            200)
                                  .unlockedBy("has_black_sapphire_ore", has(DreamtinkerTagKeys.Items.blackSapphireOre))
                                  .save(consumer, location(common_folder + "black_sapphire_ore" + "/smelting"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(DreamtinkerTagKeys.Items.blackSapphireOre),
                                            RecipeCategory.MISC,
                                            DreamtinkerCommon.black_sapphire.get(),
                                            3.0f,
                                            100)
                                  .unlockedBy("has_black_sapphire_ore", has(DreamtinkerTagKeys.Items.blackSapphireOre))
                                  .save(consumer, location(common_folder + "black_sapphire_ore" + "/blasting"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, DreamtinkerCommon.persona_cast.get())
                           .define('e', Items.WEEPING_VINES)
                           .define('M', DreamtinkerCommon.twist_obsidian_pane.get())
                           .pattern(" e ")
                           .pattern("eMe")
                           .pattern(" e ")
                           .unlockedBy("has_item", has(DreamtinkerCommon.twist_obsidian_pane.get()))
                           .save(consumer, location(common_folder + "casts/" + DreamtinkerCommon.persona_cast.get()));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, DreamtinkerCommon.wish_cast.get())
                           .define('e', Tags.Items.GEMS_LAPIS)
                           .define('M', DreamtinkerCommon.unborn_egg.get())
                           .pattern("eee")
                           .pattern("eMe")
                           .pattern("eee")
                           .unlockedBy("has_unborn_egg", has(DreamtinkerCommon.unborn_egg.get()))
                           .save(consumer, location(common_folder + "casts/" + DreamtinkerCommon.reason_cast.get()));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, DreamtinkerCommon.reason_cast.get())
                           .define('e', DreamtinkerCommon.narcissus.get())
                           .define('M', Items.CLOCK)
                           .pattern("eee")
                           .pattern("eMe")
                           .pattern("eee")
                           .unlockedBy("has_unborn_egg", has(DreamtinkerCommon.unborn_egg.get()))
                           .save(consumer, location(common_folder + "casts/" + DreamtinkerCommon.wish_cast.get()));
        night_one_receipts(consumer, DreamtinkerCommon.soul_steel.get(), DreamtinkerCommon.soulSteelBlock.asItem());

        night_one_receipts(consumer, DreamtinkerCommon.raw_orichalcum.get(), DreamtinkerCommon.RawOrichalcumBlock.asItem());
        night_one_receipts(consumer, DreamtinkerCommon.orichalcum.get(), DreamtinkerCommon.OrichalcumBlock.asItem());
        night_one_receipts(consumer, DreamtinkerCommon.orichalcum_nugget.get(), DreamtinkerCommon.orichalcum.get());

        night_one_receipts(consumer, DreamtinkerCommon.raw_cold_iron.get(), DreamtinkerCommon.RawColdIronBlock.asItem());
        night_one_receipts(consumer, DreamtinkerCommon.cold_iron_ingot.get(), DreamtinkerCommon.ColdIronBlock.asItem());
        night_one_receipts(consumer, DreamtinkerCommon.cold_iron_nugget.get(), DreamtinkerCommon.cold_iron_ingot.get());

        night_one_receipts(consumer, DreamtinkerCommon.raw_shadow_silver.get(), DreamtinkerCommon.RawShadowSilverBlock.asItem());
        night_one_receipts(consumer, DreamtinkerCommon.shadow_silver_ingot.get(), DreamtinkerCommon.ShadowSilverBlock.asItem());
        night_one_receipts(consumer, DreamtinkerCommon.shadow_silver_nugget.get(), DreamtinkerCommon.shadow_silver_ingot.get());

        night_one_receipts(consumer, DreamtinkerCommon.raw_transmutation_gold.get(), DreamtinkerCommon.RawTransmutationGoldBlock.asItem());
        night_one_receipts(consumer, DreamtinkerCommon.transmutation_gold_ingot.get(), DreamtinkerCommon.TransmutationGoldBlock.asItem());
        night_one_receipts(consumer, DreamtinkerCommon.transmutation_gold_nugget.get(), DreamtinkerCommon.transmutation_gold_ingot.get());
        night_one_receipts(consumer, DreamtinkerCommon.metallivorous_stibium_lupus.get(), DreamtinkerCommon.metallivorous_stibium_lupus_block.asItem());

        new SpiritInfusionRecipeBuilder(ItemRegistry.THE_VESSEL.get(), 1, new ItemStack(DreamtinkerCommon.malignant_gluttony.get()))
                .addExtraItem(ItemRegistry.NULL_SLATE.get(), 4)
                .addExtraItem(ItemRegistry.MALIGNANT_PEWTER_INGOT.get(), 1)
                .addExtraItem(ItemRegistry.CURSED_SAP.get(), 3)
                .addExtraItem(ItemRegistry.FUSED_CONSCIOUSNESS.get(), 1)
                .addSpirit(SpiritTypeRegistry.WICKED_SPIRIT, 6)
                .addSpirit(SpiritTypeRegistry.SACRED_SPIRIT, 6)
                .addSpirit(SpiritTypeRegistry.ELDRITCH_SPIRIT, 6)
                .addSpirit(SpiritTypeRegistry.INFERNAL_SPIRIT, 6)
                .build(consumer, "malum_" + DreamtinkerCommon.malignant_gluttony.getId().getPath());
        new SpiritInfusionRecipeBuilder(Items.BUCKET, 1, new ItemStack(
                Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(Dreamtinker.getLocation("liquid_arcana_juice_bucket")))))
                .addExtraItem(ItemRegistry.NULL_SLATE.get(), 8)
                .addExtraItem(ItemRegistry.MNEMONIC_FRAGMENT.get(), 8)
                .addExtraItem(ItemRegistry.CURSED_SAP.get(), 2)
                .addExtraItem(ItemRegistry.RUNIC_SAP.get(), 2)
                .addSpirit(SpiritTypeRegistry.ARCANE_SPIRIT, 6)
                .addSpirit(SpiritTypeRegistry.AQUEOUS_SPIRIT, 6)
                .addSpirit(SpiritTypeRegistry.SACRED_SPIRIT, 6)
                .addSpirit(SpiritTypeRegistry.ELDRITCH_SPIRIT, 6)
                .build(consumer, "malum_" + "liquid_arcana_juice_bucket");
    }

    String partFolder = "tools/parts/";
    String castFolder = "smeltery/casts/";

    private void addPartRecipes(Consumer<FinishedRecipe> consumer) {
        //armor

        Consumer<FinishedRecipe> wrapped;
        armorPlatingBuilder(consumer, DreamtinkerMaterialIds.star_regulus);

        //explode_core
        PartRecipeBuilder.partRecipe(DreamtinkerToolParts.explode_core.get()).setPattern(this.id(DreamtinkerToolParts.explode_core.get()))
                         .setPatternItem(Ingredient.of(DreamtinkerToolParts.explode_core.get())).setCost(8)
                         .save(consumer, this.location(partFolder + "builder/" + this.id(DreamtinkerToolParts.explode_core.get()).getPath()));
        MaterialCastingRecipeBuilder.tableRecipe(DreamtinkerToolParts.explode_core.get())
                                    .setCast(Items.GUNPOWDER, true)
                                    .setItemCost(8)
                                    .save(consumer, location(partFolder + "explode_core_cast"));
        CompositeCastingRecipeBuilder.table(DreamtinkerToolParts.explode_core.get(), 8)
                                     .save(consumer, this.location(castFolder + "explode_core_composite"));
        MaterialCastingRecipeBuilder.tableRecipe(TinkerToolParts.shieldCore.get())
                                    .setCast(Items.SHIELD, true)
                                    .setItemCost(4)
                                    .save(consumer, location(partFolder + "shield_core_cast"));
        partRecipes(consumer, DreamtinkerToolParts.chainSawTeeth, DreamtinkerToolParts.chainSawTeethCast, 12, partFolder, castFolder);
        partRecipes(consumer, DreamtinkerToolParts.chainSawCore, DreamtinkerToolParts.chainSawCoreCast, 8, partFolder, castFolder);
        partRecipes(consumer, DreamtinkerToolParts.NovaCover, DreamtinkerToolParts.NovaCoverCast, 2, partFolder, castFolder);
        partRecipes(consumer, DreamtinkerToolParts.NovaMisc, DreamtinkerToolParts.NovaMiscCast, 3, partFolder, castFolder);
        partRecipes(consumer, DreamtinkerToolParts.NovaWrapper, DreamtinkerToolParts.NovaWrapperCast, 2, partFolder, castFolder);
        partRecipes(consumer, DreamtinkerToolParts.NovaRostrum, DreamtinkerToolParts.NovaRostrumCast, 4, partFolder, castFolder);
        //five Orthant
        ToolPartItem[] tree_parts =
                new ToolPartItem[]{DreamtinkerToolParts.memoryOrthant.get(), DreamtinkerToolParts.wishOrthant.get(), DreamtinkerToolParts.soulOrthant.get(), DreamtinkerToolParts.personaOrthant.get(), DreamtinkerToolParts.reasonEmanation.get()};
        Item[] tree_casts =
                new Item[]{DreamtinkerCommon.memory_cast.get(), DreamtinkerCommon.wish_cast.get(), DreamtinkerCommon.soul_cast.get(), DreamtinkerCommon.persona_cast.get(), DreamtinkerCommon.reason_cast.get()};
        int[] tree_costs = new int[]{8, 3, 3, 3, 8};
        for (int i = 0; i < tree_parts.length; i++) {
            PartRecipeBuilder.partRecipe(tree_parts[i]).setPattern(this.id(tree_parts[i]))
                             .setPatternItem(Ingredient.of(tree_casts[i])).setCost(tree_costs[i])
                             .save(consumer, this.location(partFolder + "builder/" + this.id(tree_parts[i]).getPath()));
            MaterialCastingRecipeBuilder.tableRecipe(tree_parts[i]).setItemCost(tree_costs[i]).setCast(tree_casts[i], true)
                                        .save(consumer, this.location(castFolder + this.id(tree_parts[i]).getPath() + "_cast"));
            CompositeCastingRecipeBuilder.table(tree_parts[i], tree_costs[i])
                                         .save(consumer, this.location(castFolder + this.id(tree_parts[i]).getPath() + "_composite"));
        }
        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("malum"));
        armorPlatingBuilder(wrapped, DreamtinkerMaterialIds.spirit_fabric);

        malumCompactMaterialBuilder(wrapped, DreamtinkerMaterialIds.mnemonic, ItemRegistry.MNEMONIC_FRAGMENT.get(), HeadMaterialStats.ID, 1);
        malumCompactMaterialBuilder(wrapped, DreamtinkerMaterialIds.mnemonic, ItemRegistry.MNEMONIC_FRAGMENT.get(), HandleMaterialStats.ID, 1);
        malumCompactMaterialBuilder(wrapped, DreamtinkerMaterialIds.mnemonic, ItemRegistry.MNEMONIC_FRAGMENT.get(),
                                    StatlessMaterialStats.BINDING.getIdentifier(), 1);
        malumCompactMaterialBuilder(wrapped, DreamtinkerMaterialIds.auric, ItemRegistry.AURIC_EMBERS.get(), HeadMaterialStats.ID, 1);
        malumCompactMaterialBuilder(wrapped, DreamtinkerMaterialIds.auric, ItemRegistry.AURIC_EMBERS.get(), HandleMaterialStats.ID, 1);
        malumCompactMaterialBuilder(wrapped, DreamtinkerMaterialIds.auric, ItemRegistry.AURIC_EMBERS.get(), StatlessMaterialStats.BINDING.getIdentifier(), 1);
        
        malumCompactMaterialBuilder(wrapped, DreamtinkerMaterialIds.malignant_lead, ItemRegistry.MALIGNANT_LEAD.get(), HandleMaterialStats.ID, 1);

        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("eidolon"));
        armorPlatingBuilder(wrapped, DreamtinkerMaterialIds.WickedWeave);

        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"));
        armorPlatingBuilder(wrapped, DreamtinkerMaterialIds.MonsterSkin);
        armorPlatingBuilder(wrapped, DreamtinkerMaterialIds.SpikyShard);

        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"));
        armorPlatingBuilder(wrapped, DreamtinkerMaterialIds.AbjurationEssence);
        armorPlatingBuilder(wrapped, DreamtinkerMaterialIds.EarthEssence);


    }

    private void armorPlatingBuilder(Consumer<FinishedRecipe> consumer, MaterialId id) {
        ArrayList<CastItemObject> armor_casts = new ArrayList<>(
                Arrays.asList(TinkerSmeltery.helmetPlatingCast, TinkerSmeltery.chestplatePlatingCast, TinkerSmeltery.leggingsPlatingCast,
                              TinkerSmeltery.bootsPlatingCast));
        List<ToolPartItem> toolParts = TinkerToolParts.plating.values();
        int[] armor_costs = {3, 6, 5, 2};
        CompoundTag nbt = new CompoundTag();
        nbt.putString("Material", id.toString());
        for (int i = 0; i < armor_casts.size(); i++) {
            ItemStack stack = new ItemStack(toolParts.get(i));
            stack.getOrCreateTag().merge(nbt);
            ItemPartRecipeBuilder.item(armor_casts.get(i).getName(), ItemOutput.fromStack(stack))
                                 .material(id, armor_costs[i])
                                 .setPatternItem(CompoundIngredient.of(Ingredient.of(TinkerTags.Items.DEFAULT_PATTERNS),
                                                                       Ingredient.of(armor_casts.get(i).get())))
                                 .save(consumer, location(partFolder + "builder/" + id.getPath() + "/" + armor_casts.get(i).getName().getPath()));
        }
    }

    private void malumCompactMaterialBuilder(Consumer<FinishedRecipe> consumer, MaterialVariantId id, Item item, MaterialStatsId statsId, int count) {
        List<ToolPartItem> Parts = DTHelper.getPartList(statsId);
        Map<ToolPartItem, CastLookup.CastTriple> map = CastLookup.findCastsForParts(Parts);
        for (ToolPartItem part : Parts) {
            Item castItem = map.get(part).cast(); // 可能为 null（没注册）
            if (part == DreamtinkerToolParts.memoryOrthant.get())
                castItem = DreamtinkerCommon.memory_cast.get();
            if (part == DreamtinkerToolParts.reasonEmanation.get())
                castItem = DreamtinkerCommon.reason_cast.get();
            if (part == DreamtinkerToolParts.explode_core.get())
                castItem = DreamtinkerToolParts.explode_core.get();
            if (part == DreamtinkerToolParts.wishOrthant.get())
                castItem = DreamtinkerCommon.wish_cast.get();
            if (part == DreamtinkerToolParts.chainSawCore.get())
                castItem = DreamtinkerToolParts.chainSawCoreCast.get();
            if (part == DreamtinkerToolParts.chainSawTeeth.get())
                castItem = DreamtinkerToolParts.chainSawTeethCast.get();
            if (part == TinkerToolParts.arrowHead.get())
                castItem = TinkerSmeltery.arrowCast.get();
            CompoundTag nbt = new CompoundTag();
            nbt.putString("Material", id.toString());
            ItemStack stack = new ItemStack(part, count);
            stack.getOrCreateTag().merge(nbt);
            new SpiritInfusionRecipeBuilder(castItem, 1, stack)
                    .addExtraItem(item, 2 * 4)
                    .addExtraItem(ItemRegistry.SOUL_STAINED_STEEL_INGOT.get(), 1)
                    .addExtraItem(ItemRegistry.FUSED_CONSCIOUSNESS.get(), 1)
                    .addSpirit(SpiritTypeRegistry.WICKED_SPIRIT, 4 * 4)
                    .addSpirit(SpiritTypeRegistry.AERIAL_SPIRIT, 4 * 4)
                    .addSpirit(SpiritTypeRegistry.AQUEOUS_SPIRIT, 2 * 4)
                    .addSpirit(SpiritTypeRegistry.ELDRITCH_SPIRIT, 2 * 4)
                    .addSpirit(SpiritTypeRegistry.INFERNAL_SPIRIT, 2 * 4)
                    .build(consumer, (id.getVariant().isBlank() || id.getVariant().isEmpty() ? id.getId().getPath() : id.getVariant()) + "_" + part);
        }
    }


    private void addModifierRecipes(Consumer<FinishedRecipe> consumer) {
        // modifiers
        String upgradeFolder = "tools/modifiers/upgrade/";
        String abilityFolder = "tools/modifiers/ability/";
        String slotlessFolder = "tools/modifiers/slotless/";
        String defenseFolder = "tools/modifiers/defense/";
        String compatFolder = "tools/modifiers/compat/";
        String worktableFolder = "tools/modifiers/worktable/";
        String soulFolder = "tools/modifiers/soul/";
        // salvage
        String salvageFolder = "tools/modifiers/salvage/";
        String upgradeSalvage = salvageFolder + "upgrade/";
        String abilitySalvage = salvageFolder + "ability/";
        String defenseSalvage = salvageFolder + "defense/";
        String compatSalvage = salvageFolder + "compat/";
        String soulSalvage = salvageFolder + "soul/";
        Consumer<FinishedRecipe> wrapped;
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.real_sweep)
                             .setTools(Ingredient.of(DreamtinkerTools.mashou.get()))
                             .addInput(Items.ECHO_SHARD)
                             .addInput(Items.ECHO_SHARD)
                             .setMaxLevel(2)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.real_sweep, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.real_sweep, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.strong_explode)
                             .setTools(Ingredient.of(DreamtinkerTools.tntarrow.get()))
                             .addInput(TinkerTools.shuriken.get())
                             .addInput(Tags.Items.GUNPOWDER)
                             .setMaxLevel(4)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.strong_explode, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.strong_explode, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.continuous_explode)
                             .setTools(Ingredient.of(DreamtinkerTools.tntarrow.get()))
                             .addInput(TinkerTools.shuriken.get())
                             .addInput(Items.TNT)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.continuous_explode, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.continuous_explode, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.continuous_explode)
                             .setTools(Ingredient.of(DreamtinkerTools.tntarrow.get()))
                             .addInput(Items.TNT)
                             .addInput(Tags.Items.GUNPOWDER)
                             .setMinLevel(2)
                             .setMaxLevel(3)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.continuous_explode, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.continuous_explode, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.force_to_explosion)
                             .setTools(Ingredient.of(DreamtinkerTools.tntarrow.get()))
                             .addInput(Items.STONE_PICKAXE)
                             .addInput(Tags.Items.GUNPOWDER)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.force_to_explosion, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.force_to_explosion, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.mei)
                             .setTools(TinkerTags.Items.MELEE)
                             .addInput(Items.POPPY).addInput(Items.POPPY)
                             .addInput(Items.CHAIN).addInput(Items.CHAIN)
                             .save(consumer, prefix(DreamtinkerModifiers.mei, slotlessFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.life_looting)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(Tags.Items.GEMS_EMERALD, 1)
                             .addInput(Tags.Items.STORAGE_BLOCKS_EMERALD, 1)
                             .addInput(Tags.Items.GEMS_EMERALD, 1)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .save(consumer, wrap(DreamtinkerModifiers.life_looting, abilityFolder, "_1"));

        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.soul_upgrade)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(DreamtinkerCommon.twist_obsidian_pane.get(), 10)
                             .addInput(Items.WEEPING_VINES, 40)
                             .setMaxLevel(10)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.soul_upgrade, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.soul_upgrade, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.soul_core)
                             .setTools(Ingredient.of(DreamtinkerTools.narcissus_wing.get()))
                             .addInput(DreamtinkerCommon.unborn_turtle_egg.get(), 1)
                             .setMaxLevel(1)
                             .setSlots(SlotType.SOUL, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.soul_core, soulSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.soul_core, soulFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.soul_core)
                             .setTools(Ingredient.of(DreamtinkerTools.narcissus_wing.get()))
                             .addInput(DreamtinkerCommon.unborn_turtle_egg.get(), 1)
                             .addInput(DreamtinkerCommon.unborn_sniffer_egg.get(), 1)
                             .setMaxLevel(3)
                             .setSlots(SlotType.SOUL, 1)
                             .save(consumer, wrap(DreamtinkerModifiers.Ids.soul_core, soulFolder, "_1"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.icy_memory)
                             .setTools(Ingredient.of(DreamtinkerTools.narcissus_wing.get()))
                             .addInput(DreamtinkerCommon.unborn_egg.get())
                             .addInput(Items.POWDER_SNOW_BUCKET)
                             .setMaxLevel(2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.icy_memory, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.icy_memory, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.icy_memory)
                             .setTools(Ingredient.of(DreamtinkerTools.narcissus_wing.get()))
                             .addInput(DreamtinkerCommon.unborn_dragon_egg.get())
                             .addInput(DreamtinkerCommon.despair_gem.get())
                             .setLevelRange(3, 3)
                             .setSlots(SlotType.SOUL, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.icy_memory, soulSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.icy_memory, soulFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.hate_memory)
                             .setTools(Ingredient.of(DreamtinkerTools.narcissus_wing.get()))
                             .addInput(DreamtinkerCommon.unborn_egg.get())
                             .addInput(Items.WHITE_BANNER)
                             .addInput(Items.IRON_AXE)
                             .addInput(Tags.Items.TOOLS_CROSSBOWS)
                             .setMaxLevel(3)
                             .setSlots(SlotType.SOUL, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.hate_memory, soulSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.hate_memory, soulFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.huge_ego)
                             .addInput(DreamtinkerCommon.twist_obsidian_pane.get(), 10)
                             .setMaxLevel(3)
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.huge_ego, slotlessFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.flaming_memory)
                             .setTools(Ingredient.of(DreamtinkerTools.narcissus_wing))
                             .addInput(DreamtinkerToolParts.memoryOrthant.get(), 1)
                             .addInput(DreamtinkerCommon.nigrescence_antimony.get(), 6)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.flaming_memory, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.flaming_memory, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.all_slayer)
                             .setTools(TinkerTags.Items.MELEE)
                             .addInput(Items.IRON_SWORD)
                             .addInput(Items.IRON_SWORD)
                             .addInput(Items.GOLDEN_SWORD)
                             .addInput(Items.IRON_AXE)
                             .addInput(Items.IRON_AXE)
                             .setMaxLevel(4)
                             .setSlots(SlotType.SOUL, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.all_slayer, soulSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.all_slayer, soulFolder));

        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.the_romantic)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(Tags.Items.STORAGE_BLOCKS_GOLD, 7)
                             .addInput(Tags.Items.STORAGE_BLOCKS_GOLD)
                             .addInput(TinkerModifiers.silkyCloth, 5)
                             .addInput(TinkerModifiers.silkyCloth)
                             .setSlots(SlotType.SOUL, 1)
                             .setMaxLevel(5)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.the_romantic, soulSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.the_romantic, soulFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.weapon_dreams_order)
                             .setTools(Ingredient.of(DreamtinkerTools.silence_glove.get()))
                             .addInput(Items.COMPASS, 2)
                             .addInput(Items.BELL)
                             .addInput(Items.CLOCK)
                             .setSlots(SlotType.SOUL, 1)
                             .setMaxLevel(1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.weapon_dreams_order, soulSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.weapon_dreams_order, soulFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.weapon_dreams_filter)
                             .setTools(Ingredient.of(DreamtinkerTools.silence_glove.get()))
                             .addInput(Items.REPEATER, 2)
                             .addInput(Items.COMPARATOR)
                             .addInput(Items.OBSERVER)
                             .setSlots(SlotType.SOUL, 1)
                             .setMaxLevel(1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.weapon_dreams_filter, soulSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.weapon_dreams_filter, soulFolder));
        Ingredient under_plates = Ingredient.of(DreamtinkerTools.underPlate.get(ArmorItem.Type.HELMET),
                                                DreamtinkerTools.underPlate.get(ArmorItem.Type.CHESTPLATE),
                                                DreamtinkerTools.underPlate.get(ArmorItem.Type.LEGGINGS),
                                                DreamtinkerTools.underPlate.get(ArmorItem.Type.BOOTS));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.spiritual_weapon_transformation)
                             .setTools(under_plates)
                             .addInput(BlockRegistry.BLOCK_OF_ASTRAL_WEAVE.get(), 3)
                             .addInput(ItemRegistry.TOPHAT.get())
                             .addInput(BlockRegistry.WICKED_SPIRITED_GLASS.get(), 16)
                             .setSlots(SlotType.SOUL, 1)
                             .setMaxLevel(1)
                             .saveSalvage(withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("malum")),
                                          prefix(DreamtinkerModifiers.spiritual_weapon_transformation, soulSalvage))
                             .save(withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("malum")),
                                   prefix(DreamtinkerModifiers.spiritual_weapon_transformation, soulFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.TheEnd)
                             .setTools(TinkerTags.Items.SPECIAL_TOOLS)
                             .addInput(Blocks.DIRT, 2)
                             .addInput(Blocks.DIRT, 2)
                             .addInput(Blocks.DIRT, 2)
                             .addInput(Blocks.GRASS_BLOCK, 3)
                             .addInput(Blocks.GRASS_BLOCK, 3)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.TheEnd, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.TheEnd, upgradeFolder));
        SwappableModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.five_creations, SlotType.SOUL.getName())
                                      .setTools(TinkerTags.Items.BONUS_SLOTS)
                                      .addInput(DreamtinkerCommon.nigrescence_antimony.get(), 5)
                                      .addInput(DreamtinkerCommon.blackSapphireOre.asItem(), 5)
                                      .addInput(DreamtinkerCommon.void_pearl.get(), 16)
                                      .addInput(DreamtinkerCommon.nigrescence_antimony.get(), 5)
                                      .addInput(DreamtinkerCommon.blackSapphireOre.asItem(), 5)
                                      .disallowCrystal()
                                      .save(consumer, wrap(DreamtinkerModifiers.Ids.five_creations, slotlessFolder, "_" + SlotType.SOUL.getName()));
        SwappableModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.five_creations, "traits")
                                      .setTools(ToolHookIngredient.of(TinkerTags.Items.BONUS_SLOTS, ToolHooks.REBALANCED_TRAIT))
                                      .addInput(TinkerTags.Items.ANCIENT_TOOLS)
                                      .addInput(TinkerTags.Items.ANCIENT_TOOLS)
                                      .addInput(TinkerTags.Items.ANCIENT_TOOLS)
                                      .addInput(DreamtinkerCommon.echo_alloy.get(), 5)
                                      .addInput(Items.CALIBRATED_SCULK_SENSOR, 5)
                                      .disallowCrystal()
                                      .save(consumer, wrap(DreamtinkerModifiers.Ids.five_creations, slotlessFolder, "_traits"));

        SwappableModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.five_creations, SlotType.DEFENSE.getName())
                                      .setTools(IntersectionIngredient.of(ingredientFromTags(TinkerTags.Items.ARMOR, TinkerTags.Items.HELD),
                                                                          Ingredient.of(TinkerTags.Items.BONUS_SLOTS)))
                                      .addInput(DreamtinkerCommon.cold_iron_ingot.get(), 5)
                                      .addInput(DreamtinkerCommon.orichalcum.get(), 5)
                                      .addInput(DreamtinkerCommon.rainbow_honey_crystal.get(), 5)
                                      .addInput(DreamtinkerCommon.transmutation_gold_ingot.get(), 5)
                                      .addInput(DreamtinkerCommon.shiningFlint.get(), 5)
                                      .disallowCrystal()
                                      .save(consumer, wrap(DreamtinkerModifiers.Ids.five_creations, slotlessFolder, SlotType.DEFENSE.getName()));
        SwappableModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.five_creations, "designs")
                                      .setTools(TinkerTags.Items.BONUS_SLOTS)
                                      .addInput(DreamtinkerCommon.unborn_dragon_egg.get(), 5)
                                      .addInput(DreamtinkerCommon.poisonousHomunculus.get(), 5)
                                      .addInput(DreamtinkerCommon.evilHomunculus.get(), 5)
                                      .addInput(DreamtinkerCommon.rainbow_honey_crystal.get(), 5)
                                      .addInput(DreamtinkerCommon.shiningFlint.get(), 5)
                                      .disallowCrystal()
                                      .save(consumer, wrap(DreamtinkerModifiers.Ids.five_creations, slotlessFolder, "_designs"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.signal_axe)
                             .setTools(Ingredient.of(TinkerTools.broadAxe.get(), TinkerTools.handAxe.get()))
                             .addInput(Blocks.RED_CANDLE, 2)
                             .addInput(Blocks.GREEN_CANDLE, 2)
                             .addInput(Blocks.BLUE_CANDLE, 2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.signal_axe, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.signal_axe, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.love_shooting)
                             .setTools(TinkerTags.Items.RANGED)
                             .addInput(Blocks.BUDDING_AMETHYST, 11)
                             .addInput(TinkerWorld.earthGeode.getBlock(), 11)
                             .addInput(TinkerWorld.skyGeode.getBlock(), 11)
                             .addInput(TinkerWorld.ichorGeode.getBlock(), 11)
                             .addInput(TinkerWorld.enderGeode.getBlock(), 11)
                             .setSlots(SlotType.ABILITY, 1)
                             .setMaxLevel(1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.love_shooting, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.love_shooting, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.ender_slayer)//2 Modifier share same id so This should be fine
                             .setTools(TinkerTags.Items.MELEE_WEAPON)
                             .addInput(Tags.Items.OBSIDIAN, 2)
                             .addInput(Items.GHAST_TEAR, 2)
                             .addInput(Items.ENDER_EYE, 2)
                             .setMaxLevel(1)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.ender_slayer, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.ender_slayer, upgradeFolder));
        Ingredient throw_weapon = CompoundIngredient.of(IntersectionIngredient.of(Ingredient.of(Dreamtinker.forgeItemTag("tools/tridents")),
                                                                                  Ingredient.of(TinkerTags.Items.MELEE_WEAPON)),
                                                        Ingredient.of(TinkerTags.Items.THROWN_AMMO));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.torrent)
                             .setTools(throw_weapon)
                             .addInput(Tags.Items.DUSTS_PRISMARINE, 15)
                             .addInput(Tags.Items.GEMS_PRISMARINE, 15)
                             .setMaxLevel(5)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.torrent, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.torrent, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.wrath)
                             .setTools(throw_weapon)
                             .addInput(Items.PRISMARINE, 4)
                             .addInput(Tags.Items.GEMS_PRISMARINE, 15)
                             .setMaxLevel(4)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.wrath, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.wrath, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.fox_blessing)
                             .setTools(TinkerTags.Items.HELD)
                             .addInput(DreamtinkerCommon.fox_fur.get(), 2)
                             .addInput(Tags.Items.GEMS_PRISMARINE, 15)
                             .setMaxLevel(1)
                             .save(consumer, prefix(DreamtinkerModifiers.fox_blessing, slotlessFolder));
        // Start of enigmaticlegacy modifiers
        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.life_looting)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(EnigmaticItems.LORE_INSCRIBER, 1)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.life_looting, abilitySalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.life_looting, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.weapon_books)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(EnigmaticItems.THE_ACKNOWLEDGMENT, 1)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.weapon_books, abilitySalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.weapon_books, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.weapon_books)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(EnigmaticItems.THE_TWIST, 1)
                             .setLevelRange(2, 2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.weapon_books, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.weapon_books, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.weapon_books)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(EnigmaticItems.THE_INFINITUM, 1)
                             .setLevelRange(3, 3)
                             .setSlots(SlotType.SOUL, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.weapon_books, soulSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.weapon_books, soulFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.eldritch_pan)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(EnigmaticItems.ELDRITCH_PAN, 1)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.eldritch_pan, abilitySalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.eldritch_pan, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.desolation_ring)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(EnigmaticItems.EVIL_ESSENCE, 2)
                             .addInput(Items.HEART_OF_THE_SEA, 2)
                             .addInput(Tags.Items.INGOTS_NETHERITE, 2)
                             .addInput(EnigmaticItems.ABYSSAL_HEART, 1)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.desolation_ring, abilitySalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.desolation_ring, abilityFolder));

        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.el_eternal_binding)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(Items.CHAIN, 10)
                             .addInput(Items.ENCHANTING_TABLE)
                             .setMaxLevel(1)
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.el_eternal_binding, slotlessFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.el_sorrow)
                             .setTools(TinkerTags.Items.ARMOR)
                             .addInput(Items.WEEPING_VINES, 10)
                             .addInput(Items.ENCHANTING_TABLE)
                             .setMaxLevel(1)
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.el_sorrow, slotlessFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.el_nemesis_curse)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(Items.SHIELD)
                             .addInput(Items.ENCHANTING_TABLE)
                             .setMaxLevel(1)
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.el_nemesis_curse, slotlessFolder));

        // Start of malum modifiers
        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("malum"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_rebound)
                             .setTools(IntersectionIngredient.of(Ingredient.of(ItemTagRegistry.SCYTHE), Ingredient.of(TinkerTags.Items.MELEE_WEAPON)))
                             .addInput(ItemRegistry.CRUDE_SCYTHE.get())
                             .addInput(ItemRegistry.EARTHEN_SPIRIT.get())
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.malum_rebound, abilitySalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.malum_rebound, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_rebound)
                             .setTools(IntersectionIngredient.of(Ingredient.of(ItemTagRegistry.SCYTHE), Ingredient.of(TinkerTags.Items.MELEE_WEAPON)))
                             .addInput(ItemRegistry.CRUDE_SCYTHE.get())
                             .setLevelRange(2, 3)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.malum_rebound, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.malum_rebound, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_ascension)
                             .setTools(IntersectionIngredient.of(Ingredient.of(ItemTagRegistry.SCYTHE), Ingredient.of(TinkerTags.Items.MELEE_WEAPON)))
                             .addInput(ItemRegistry.CRUDE_SCYTHE.get())
                             .addInput(ItemRegistry.AERIAL_SPIRIT.get())
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.malum_ascension, abilitySalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.malum_ascension, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_ascension)
                             .setTools(IntersectionIngredient.of(Ingredient.of(ItemTagRegistry.SCYTHE), Ingredient.of(TinkerTags.Items.MELEE_WEAPON)))
                             .addInput(ItemRegistry.CRUDE_SCYTHE.get())
                             .setLevelRange(2, 3)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.malum_ascension, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.malum_ascension, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_animated)
                             .setTools(IntersectionIngredient.of(Ingredient.of(ItemTagRegistry.SCYTHE), Ingredient.of(TinkerTags.Items.MELEE_WEAPON)))
                             .addInput(ItemRegistry.WICKED_SPIRIT.get())
                             .addInput(ItemRegistry.AERIAL_SPIRIT.get())
                             .setMaxLevel(2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.malum_animated, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.malum_animated, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_haunted)
                             .setTools(Ingredient.of(TinkerTags.Items.MELEE_WEAPON))
                             .addInput(ItemRegistry.WICKED_SPIRIT.get())
                             .addInput(ItemRegistry.SACRED_SPIRIT.get())
                             .setMaxLevel(2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.malum_haunted, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.malum_haunted, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_spirit_plunder)
                             .setTools(IntersectionIngredient.of(Ingredient.of(ItemTagRegistry.SOUL_HUNTER_WEAPON),
                                                                 Ingredient.of(TinkerTags.Items.MELEE_WEAPON)))
                             .addInput(ItemRegistry.SACRED_SPIRIT.get())
                             .addInput(ItemRegistry.SACRED_SPIRIT.get())
                             .setMaxLevel(2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.malum_spirit_plunder, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.malum_spirit_plunder, upgradeFolder));


        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("eidolon"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.eidolon_sapping)
                             .setTools(TinkerTags.Items.MELEE)
                             .addInput(SAPPING_SWORD.get(), 1)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(3)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.eidolon_sapping, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.eidolon_sapping, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.eidolon_death_bringer)
                             .setTools(DreamtinkerTagKeys.Items.dt_scythe)
                             .addInput(Dreamtinker.forgeItemTag("gems/shadow_gem"), 1)
                             .addInput(Dreamtinker.forgeItemTag("bones"), 6)
                             .addInput(Items.SKELETON_SKULL, 1)
                             .addInput(Items.WITHER_SKELETON_SKULL, 1)
                             .addInput(DEATH_ESSENCE.get(), 2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.eidolon_death_bringer, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.eidolon_death_bringer, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.eidolon_bone_chill)
                             .setTools(TinkerTags.Items.MELEE)
                             .addInput(WRAITH_HEART.get(), 1)
                             .addInput(PEWTER_INLAY.get(), 2)
                             .addInput(LESSER_SOUL_GEM.get(), 2)
                             .addInput(Dreamtinker.forgeItemTag("gems/shadow_gem"), 1)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.eidolon_bone_chill, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.eidolon_bone_chill, upgradeFolder));

        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.bic_dark_armor_plate)
                             .setTools(TinkerTags.Items.DURABILITY)
                             .addInput(ARMOR_PLATE_FROM_DARK_METAL.get(), 1)
                             .addInput(DARK_UPGRADE.get(), 1)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.bic_dark_armor_plate, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.bic_dark_armor_plate, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.bic_frostbitten)
                             .setTools(TinkerTags.Items.MELEE)
                             .addInput(PERMAFROST_SHARD.get(), 5)
                             .addInput(DARK_METAL_INGOT.get(), 1)
                             .addInput(BONE_HANDLE.get(), 1)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(3)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.bic_frostbitten, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.bic_frostbitten, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.bic_intoxicating)
                             .setTools(TinkerTags.Items.MELEE)
                             .addInput(INTOXICATING_DECOCTION.get(), 3)
                             .addInput(DARK_METAL_INGOT.get(), 1)
                             .addInput(BONE_HANDLE.get(), 1)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.bic_intoxicating, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.bic_intoxicating, upgradeFolder));


        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.nova_spell_tiers)
                             .setTools(Ingredient.of(NovaRegistry.per_aspera_scriptum.get()))
                             .addInput(Tags.Items.OBSIDIAN, 1)
                             .addInput(Tags.Items.GEMS_DIAMOND, 3)
                             .addInput(Tags.Items.STORAGE_BLOCKS_QUARTZ, 3)
                             .addInput(Tags.Items.RODS_BLAZE, 2)
                             .disallowCrystal()
                             .setMaxLevel(1)
                             .save(wrapped, wrap(DreamtinkerModifiers.Ids.nova_spell_tiers, slotlessFolder, "_mage"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.nova_spell_tiers)
                             .setTools(Ingredient.of(NovaRegistry.per_aspera_scriptum.get()))
                             .addInput(Tags.Items.NETHER_STARS, 1)
                             .addInput(Tags.Items.GEMS_EMERALD, 2)
                             .addInput(Tags.Items.ENDER_PEARLS, 3)
                             .addInput(Items.TOTEM_OF_UNDYING, 1)
                             .addInput(ItemsRegistry.WILDEN_TRIBUTE, 1)
                             .disallowCrystal()
                             .setMaxLevel(2)
                             .save(wrapped, wrap(DreamtinkerModifiers.Ids.nova_spell_tiers, slotlessFolder, "_archmage"));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_magic_armor)
                             .setTools(under_plates)
                             .addInput(ItemsRegistry.MAGE_FIBER, 4)
                             .addInput(Tags.Items.INGOTS_GOLD, 8)
                             .setMaxLevel(1)
                             .save(wrapped, wrap(NovaRegistry.nova_magic_armor, slotlessFolder, "_mage"));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_magic_armor)
                             .setTools(under_plates)
                             .addInput(ItemsRegistry.MAGE_FIBER, 4)
                             .addInput(Tags.Items.GEMS_DIAMOND, 8)
                             .setMaxLevel(2)
                             .save(wrapped, wrap(NovaRegistry.nova_magic_armor, slotlessFolder, "_archmage"));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_magic_armor)
                             .setTools(TinkerTags.Items.ARMOR)
                             .addInput(ItemsRegistry.WILDEN_TRIBUTE, 6)
                             .addInput(Items.TOTEM_OF_UNDYING, 1)
                             .addInput(ItemsRegistry.MAGE_BLOOM, 3)
                             .addInput(ItemsRegistry.BLANK_PARCHMENT, 2)
                             .setSlots(SlotType.ABILITY, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, wrap(NovaRegistry.nova_magic_armor, abilitySalvage, "_1"))
                             .save(wrapped, wrap(NovaRegistry.nova_magic_armor, abilityFolder, "_1"));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_magic_armor)
                             .setTools(TinkerTags.Items.ARMOR)
                             .addInput(ItemsRegistry.MAGE_FIBER, 8)
                             .addInput(Tags.Items.INGOTS_GOLD, 8)
                             .addInput(Tags.Items.INGOTS_GOLD, 8)
                             .setLevelRange(2, 2)
                             .save(wrapped, wrap(NovaRegistry.nova_magic_armor, slotlessFolder, "_general_mage"));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_magic_armor)
                             .setTools(TinkerTags.Items.ARMOR)
                             .addInput(ItemsRegistry.MAGE_FIBER, 8)
                             .addInput(Tags.Items.GEMS_DIAMOND, 8)
                             .addInput(Tags.Items.GEMS_DIAMOND, 8)
                             .setLevelRange(3, 3)
                             .save(wrapped, wrap(NovaRegistry.nova_magic_armor, slotlessFolder, "_general_archmage"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.nova_mana_reduce)
                             .setTools(TinkerTags.Items.ARMOR)
                             .addInput(ItemsRegistry.GREATER_EXPERIENCE_GEM, 6)
                             .addInput(ItemsRegistry.BLANK_THREAD, 1)
                             .addInput(ItemsRegistry.MAGE_BLOOM, 3)
                             .addInput(ItemsRegistry.BLANK_PARCHMENT, 2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, wrap(DreamtinkerModifiers.Ids.nova_mana_reduce, upgradeSalvage, "_1"))
                             .save(wrapped, wrap(DreamtinkerModifiers.Ids.nova_mana_reduce, upgradeFolder, "_1"));
        IncrementalModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.nova_spell_slots)
                                        .setTools(Ingredient.of(NovaRegistry.per_aspera_scriptum.get()))
                                        .setInput(com.hollingsworth.arsnouveau.setup.registry.BlockRegistry.SOURCE_GEM_BLOCK.asItem(), 1, 64)
                                        .setSlots(SlotType.UPGRADE, 1)
                                        .setMaxLevel(10)
                                        .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.nova_spell_slots, upgradeSalvage))
                                        .save(wrapped, prefix(DreamtinkerModifiers.Ids.nova_spell_slots, upgradeFolder));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_enchanter_sword)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(com.hollingsworth.arsnouveau.setup.registry.BlockRegistry.SOURCE_GEM_BLOCK.asItem(), 2)
                             .addInput(Tags.Items.STORAGE_BLOCKS_GOLD, 2)
                             .addInput(Tags.Items.GEMS_DIAMOND, 1)
                             .addInput(Items.DIAMOND_SWORD, 1)
                             .setSlots(SlotType.ABILITY, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(NovaRegistry.nova_enchanter_sword, abilitySalvage))
                             .save(wrapped, prefix(NovaRegistry.nova_enchanter_sword, abilityFolder));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_wand)
                             .setTools(TinkerTags.Items.SPECIAL_TOOLS)
                             .addInput(ItemsRegistry.SOURCE_GEM, 4)
                             .addInput(Tags.Items.INGOTS_GOLD, 2)
                             .addInput(ItemsRegistry.AIR_ESSENCE, 1)
                             .addInput(ItemsRegistry.MANIPULATION_ESSENCE, 1)
                             .setSlots(SlotType.ABILITY, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(NovaRegistry.nova_wand, abilitySalvage))
                             .save(wrapped, prefix(NovaRegistry.nova_wand, abilityFolder));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_spell_bow)
                             .setTools(TinkerTags.Items.BOWS)
                             .addInput(ItemsRegistry.SOURCE_GEM, 1)
                             .addInput(Tags.Items.STORAGE_BLOCKS_GOLD, 1)
                             .addInput(ItemsRegistry.MANIPULATION_ESSENCE, 1)
                             .setSlots(SlotType.ABILITY, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(NovaRegistry.nova_spell_bow, abilitySalvage))
                             .save(wrapped, prefix(NovaRegistry.nova_spell_bow, abilityFolder));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_enchanter_shield)
                             .setTools(TinkerTags.Items.SHIELDS)
                             .addInput(com.hollingsworth.arsnouveau.setup.registry.BlockRegistry.SOURCE_GEM_BLOCK.asItem(), 2)
                             .addInput(com.hollingsworth.arsnouveau.setup.registry.BlockRegistry.SOURCE_GEM_BLOCK.asItem(), 2)
                             .addInput(Items.SHIELD, 1)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(NovaRegistry.nova_enchanter_shield, upgradeSalvage))
                             .save(wrapped, prefix(NovaRegistry.nova_enchanter_shield, upgradeFolder));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_mana_shield)
                             .setTools(TinkerTags.Items.ARMOR)
                             .addInput(com.hollingsworth.arsnouveau.setup.registry.BlockRegistry.SOURCE_GEM_BLOCK.asItem(), 16)
                             .addInput(com.hollingsworth.arsnouveau.setup.registry.BlockRegistry.SOURCE_GEM_BLOCK.asItem(), 16)
                             .addInput(ItemsRegistry.ABJURATION_ESSENCE, 9)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(3)
                             .saveSalvage(wrapped, prefix(NovaRegistry.nova_mana_shield, upgradeSalvage))
                             .save(wrapped, prefix(NovaRegistry.nova_mana_shield, upgradeFolder));

    }

    private void addEntityMeltingRecipes(Consumer<FinishedRecipe> consumer) {

        String headFolder = "smeltery/entity_melting/heads/";

        EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.WARDEN), DreamtinkerFluids.molten_echo_shard.result(FluidValues.GEM_SHARD), 5)
                                  .save(consumer, location(Entity_Melting_folder + "molten_echo_shard"));
    }

    @Override
    public @NotNull String getModId() {
        return Dreamtinker.MODID;
    }

    private void meltCast(Fluid fluid, ItemLike ingredient, int amount, Consumer<FinishedRecipe> consumer) {
        String key2 =
                FluidValues.GEM == amount ? "gem" : FluidValues.INGOT == amount ? "ingot" : "nugget";
        MeltingRecipeBuilder.melting(Ingredient.of(ingredient), fluid, amount, 3.0f)
                            .save(consumer, location(
                                    Melting_folder + Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ingredient.asItem())).getPath() + "/" + key2));
        cast(fluid, ingredient, amount, consumer);
    }

    private void meltCastBlock(Fluid fluid, ItemLike ingredient, int amount, Consumer<FinishedRecipe> consumer) {

        MeltingRecipeBuilder.melting(Ingredient.of(ingredient), fluid, amount, 3.0f)
                            .save(consumer, location(
                                    Melting_folder + Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ingredient.asItem())).getPath() + "/" + "block"));
        ItemCastingRecipeBuilder.basinRecipe(ingredient)
                                .setCoolingTime(IMeltingRecipe.getTemperature(fluid), amount)
                                .setFluid(FluidIngredient.of(new FluidStack(fluid, amount)))
                                .save(consumer, location(
                                        Casting_folder + Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ingredient.asItem())).getPath() +
                                        "/block"));
    }


    private void cast(Fluid fluid, ItemLike ingredient, int amount, Consumer<FinishedRecipe> consumer) {
        CastItemObject cast =
                FluidValues.GEM == amount ? TinkerSmeltery.gemCast : FluidValues.INGOT == amount ? TinkerSmeltery.ingotCast : TinkerSmeltery.nuggetCast;
        ItemCastingRecipeBuilder.tableRecipe(ingredient).setCoolingTime(IMeltingRecipe.getTemperature(fluid), amount)
                                .setFluid(FluidIngredient.of(new FluidStack(fluid, amount)))
                                .setCast(cast.getSingleUseTag(), true)
                                .save(consumer, location(
                                        Casting_folder + Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ingredient.asItem())).getPath() + "/single"));
        ItemCastingRecipeBuilder.tableRecipe(ingredient).setCoolingTime(IMeltingRecipe.getTemperature(fluid), amount)
                                .setFluid(FluidIngredient.of(new FluidStack(fluid, amount)))
                                .setCast(cast.getMultiUseTag(), false).save(consumer, location(
                                        Casting_folder + Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ingredient.asItem())).getPath() + "/multi"));
    }

    public static ICondition tagFilled(TagKey<Item> tagKey) {
        return new OrCondition(ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS, new TagFilledCondition<>(tagKey));
    }

    @SafeVarargs
    private static Ingredient ingredientFromTags(TagKey<Item>... tags) {
        Ingredient[] tagIngredients = new Ingredient[tags.length];
        for (int i = 0; i < tags.length; i++) {
            tagIngredients[i] = Ingredient.of(tags[i]);
        }
        return CompoundIngredient.of(tagIngredients);
    }
}

