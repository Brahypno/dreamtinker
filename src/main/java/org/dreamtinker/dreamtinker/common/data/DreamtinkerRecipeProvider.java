package org.dreamtinker.dreamtinker.common.data;

import com.aizistral.enigmaticlegacy.registries.EnigmaticBlocks;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.sammy.malum.data.recipe.builder.SpiritInfusionRecipeBuilder;
import com.sammy.malum.registry.common.SpiritTypeRegistry;
import com.sammy.malum.registry.common.item.ItemRegistry;
import com.sammy.malum.registry.common.item.ItemTagRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.crafting.conditions.ItemExistsCondition;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagkeys;
import org.dreamtinker.dreamtinker.fluids.DreamtinkerFluids;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.tools.DreamtinkerToolParts;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.dreamtinker.dreamtinker.utils.CastLookup;
import org.dreamtinker.dreamtinker.utils.DThelper;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.data.IRecipeHelper;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.CompositeCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelBuilder;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.ItemPartRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.recycle.PartBuilderToolRecycleBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import java.util.*;
import java.util.function.Consumer;

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

        this.addPartRecipes(consumer);
        this.addToolBuildingRecipes(consumer);
        this.addModifierRecipes(consumer);
        this.addEntityMeltingRecipes(consumer);
    }

    private void addToolBuildingRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = "tools/building/";
        String armorFolder = "tools/armor/";
        ToolBuildingRecipeBuilder.toolBuildingRecipe(DreamtinkerTools.tntarrow.get())
                                 .outputSize(4)
                                 .save(consumer, prefix(DreamtinkerTools.tntarrow, folder));
        toolBuilding(consumer, DreamtinkerTools.mashou, folder);
        toolBuilding(consumer, DreamtinkerTools.narcissus_wing, folder);
        String recycle_folder = "tools/recycling/";
        PartBuilderToolRecycleBuilder.tools(SizedIngredient.fromItems(4, DreamtinkerTools.tntarrow.get()))
                                     .save(consumer, location(recycle_folder + "tntarrow"));
        DreamtinkerTools.underPlate.forEach(
                item -> ToolBuildingRecipeBuilder.toolBuildingRecipe(item).layoutSlot(Dreamtinker.getLocation("under_plate"))
                                                 .addExtraRequirement(Ingredient.of(TinkerModifiers.silkyCloth))
                                                 .addExtraRequirement(Ingredient.of(TinkerModifiers.silkyCloth))
                                                 .save(consumer, this.prefix(this.id(item), armorFolder)));

    }

    private void addAlloyRecipes(Consumer<FinishedRecipe> consumer) {
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.molten_albedo_stibium, FluidValues.GEM)
                          .addCatalyst(FluidIngredient.of(DreamtinkerTagkeys.Fluids.molten_lupi_antimony, FluidValues.INGOT))
                          .addInput(TinkerTags.Fluids.METAL_TOOLTIPS, FluidValues.INGOT)
                          .save(consumer, location("currus_triumphalis_antimonii/lupi_to_albedo"));
        AlloyRecipeBuilder.alloy(FluidOutput.fromStack(new FluidStack(DreamtinkerFluids.liquid_smoky_antimony.get(), FluidValues.INGOT * 2)), 6000)
                          .addInput(DreamtinkerTagkeys.Fluids.molten_ascending_antimony, FluidValues.INGOT)
                          .addInput(TinkerFluids.liquidSoul.getTag(), FluidValues.GLASS_BLOCK)
                          .save(consumer, location("currus_triumphalis_antimonii/ascending_to_smoky"));
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.molten_evil, FluidValues.INGOT * 2)
                          .addInput(DreamtinkerTagkeys.Fluids.molten_nefariousness, FluidValues.GEM * 4)
                          .addInput(DreamtinkerTagkeys.Fluids.liquid_trist, FluidValues.NUGGET * 4)
                          .addInput(TinkerFluids.moltenNetherite.getTag(), FluidValues.INGOT)
                          .save(consumer, location("evil_ingot"));
        AlloyRecipeBuilder.alloy(FluidOutput.fromStack(new FluidStack(DreamtinkerFluids.liquid_pure_soul.get(), FluidValues.GEM)), 1600)
                          .addInput(DreamtinkerTagkeys.Fluids.liquid_trist, FluidValues.NUGGET)
                          .addInput(TinkerFluids.liquidSoul.getTag(), FluidValues.GLASS_BLOCK * 10)
                          .save(consumer, location("liquid_pure_soul"));
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.molten_soul_aether, FluidValues.INGOT)
                          .addInput(DreamtinkerTagkeys.Fluids.molten_nefariousness, FluidValues.GEM)
                          .addInput(DreamtinkerTagkeys.Fluids.liquid_pure_soul, FluidValues.GEM)
                          .addInput(DreamtinkerTagkeys.Fluids.unstable_liquid_aether, FluidValues.INGOT)
                          .addInput(DreamtinkerTagkeys.Fluids.molten_void, FluidValues.SLIMEBALL)
                          .addInput(DreamtinkerTagkeys.Fluids.molten_echo_shard, FluidValues.GEM)
                          .save(consumer, location("molten_soul_aether"));
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.reversed_shadow, FluidValues.SLIMEBALL)
                          .addInput(DreamtinkerTagkeys.Fluids.molten_void, FluidValues.SLIMEBALL * 2)
                          .addInput(TinkerFluids.moltenEnder.getTag(), FluidValues.SLIMEBALL * 2)
                          .save(consumer, location("reversed_shadow"));
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.molten_echo_alloy, FluidValues.GEM)
                          .addInput(DreamtinkerTagkeys.Fluids.molten_echo_shard, FluidValues.GEM)
                          .addInput(TinkerFluids.moltenEnder.getTag(), FluidValues.SLIMEBALL * 2)
                          .save(consumer, location("molten_echo_alloy"));
    }

    private void addCastingRecipes(Consumer<FinishedRecipe> consumer) {
        // Pure Fluid Recipes
        String folder = "smeltery/casting/";
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.regulus.get())
                                .setFluid(DreamtinkerFluids.liquid_smoky_antimony.getLocalTag(), FluidValues.INGOT)
                                .setCoolingTime(100)
                                .setCast(Tags.Items.DUSTS_REDSTONE, true)
                                .save(consumer, location(folder + "currus_triumphalis_antimonii/smoky_to_star"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.metallivorous_stibium_lupus.get())
                                .setFluid(DreamtinkerFluids.molten_albedo_stibium.getLocalTag(), FluidValues.GEM)
                                .setCoolingTime(100)
                                .setCast(Tags.Items.STORAGE_BLOCKS_GOLD, true)
                                .save(consumer, location(folder + "currus_triumphalis_antimonii/albedo_to_lupus_block"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.metallivorous_stibium_lupus.get())
                                .setFluid(DreamtinkerFluids.molten_albedo_stibium.getLocalTag(), FluidValues.GEM)
                                .setCoolingTime(100)
                                .setCast(forgeItemTag("dusts/gold"), true)
                                .save(withCondition(consumer, tagCondition("dusts/gold")),
                                      location(folder + "currus_triumphalis_antimonii/albedo_to_lupus_dust"));

        ItemCastingRecipeBuilder.basinRecipe(Blocks.CRYING_OBSIDIAN)
                                .setFluidAndTime(DreamtinkerFluids.molten_crying_obsidian, FluidValues.GLASS_BLOCK)
                                .save(consumer, location(folder + "crying_obsidian/block"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.crying_obsidian_plane)
                                .setFluidAndTime(DreamtinkerFluids.molten_crying_obsidian, FluidValues.GLASS_PANE)
                                .save(consumer, location(folder + "crying_obsidian/pane"));

        ItemCastingRecipeBuilder.basinRecipe(EnigmaticBlocks.ETHERIUM_BLOCK)
                                .setFluidAndTime(DreamtinkerFluids.unstable_liquid_aether, FluidValues.METAL_BLOCK)
                                .save(consumer, location(folder + "etherium/block"));
        ItemCastingRecipeBuilder.tableRecipe(EnigmaticItems.VOID_PEARL)
                                .setCoolingTime(2000, 10)
                                .setCast(DreamtinkerCommon.void_pearl.get(), true)
                                .setFluid(FluidIngredient.of(new FluidStack(DreamtinkerFluids.molten_ascending_antimony.get(), FluidValues.METAL_BLOCK)))
                                .save(withCondition(consumer, new ItemExistsCondition(ForgeRegistries.ITEMS.getKey(EnigmaticItems.VOID_PEARL))),
                                      location(folder + "void_pearl/ascending"));
        ItemCastingRecipeBuilder.tableRecipe(EnigmaticItems.OCEAN_STONE)
                                .setCoolingTime(2000, 10)
                                .setCast(Items.HEART_OF_THE_SEA, true)
                                .setFluid(FluidIngredient.of(new FluidStack(DreamtinkerFluids.molten_ascending_antimony.get(), FluidValues.METAL_BLOCK)))
                                .save(withCondition(consumer, new ItemExistsCondition(ForgeRegistries.ITEMS.getKey(EnigmaticItems.OCEAN_STONE))),
                                      location(folder + "ocean_stone/ascending"));

        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.twist_obsidian_pane.get())
                                .setFluidAndTime(DreamtinkerFluids.liquid_trist, FluidValues.NUGGET * 3)
                                .setCast(DreamtinkerCommon.crying_obsidian_plane.get(), true)
                                .save(consumer, location(folder + folder + "twist_obsidian/pane"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.unborn_egg.get())
                                .setCast(Tags.Items.EGGS, true)
                                .setFluid(FluidIngredient.of(DreamtinkerTagkeys.Fluids.molten_nigrescence_antimony, FluidValues.GEM * 3))
                                .setCoolingTime(10)
                                .save(consumer, location(folder + "unborn_egg"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.unborn_turtle_egg.get())
                                .setCast(Items.TURTLE_EGG, true)
                                .setFluid(FluidIngredient.of(DreamtinkerTagkeys.Fluids.molten_nigrescence_antimony, FluidValues.GEM * 3))
                                .setCoolingTime(10)
                                .save(consumer, location(folder + "unborn_turtle_egg"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.unborn_sniffer_egg.get())
                                .setCast(Items.SNIFFER_EGG, true)
                                .setFluid(FluidIngredient.of(DreamtinkerTagkeys.Fluids.molten_nigrescence_antimony, FluidValues.GEM * 3))
                                .setCoolingTime(10)
                                .save(consumer, location(folder + "unborn_sniffer_egg"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.unborn_dragon_egg.get())
                                .setCast(Items.DRAGON_EGG, true)
                                .setFluid(FluidIngredient.of(DreamtinkerTagkeys.Fluids.molten_albedo_stibium, FluidValues.GEM * 3))
                                .setCoolingTime(10)
                                .save(consumer, location(folder + "unborn_dragon_egg"));
    }

    private void addMeltingRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = "smeltery/melting/";
        MeltingFuelBuilder.fuel(DreamtinkerFluids.molten_lupi_antimony.ingredient(FluidValues.INGOT), 776, 7776)
                          .save(consumer, location(folder + "fuel/molten_lupi_antimony"));
        MeltingFuelBuilder.fuel(DreamtinkerFluids.reversed_shadow.ingredient(FluidValues.SLIMEBALL), 17, 2200)
                          .save(consumer, location(folder + "fuel/reversed_shadow"));

        MeltingRecipeBuilder.melting(Ingredient.of(Items.CRYING_OBSIDIAN), DreamtinkerFluids.molten_crying_obsidian, FluidValues.GLASS_BLOCK, 2.0f)
                            .save(consumer, location(folder + "crying_obsidian/block"));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.crying_obsidian_plane), DreamtinkerFluids.molten_crying_obsidian, FluidValues.GLASS_PANE,
                                     1.5f)
                            .save(consumer, location(folder + "crying_obsidian/pane"));

        MeltingRecipeBuilder.melting(Ingredient.of(Items.ECHO_SHARD), DreamtinkerFluids.molten_echo_shard, FluidValues.GEM, 2.0f)
                            .save(consumer, location(folder + "echo_shard/gem"));
        cast(DreamtinkerFluids.molten_echo_shard.get(), Items.ECHO_SHARD, FluidValues.GEM, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.echo_alloy.get()), DreamtinkerFluids.molten_echo_alloy, FluidValues.GEM, 2.0f)
                            .save(consumer, location(folder + "echo_alloy/gem"));
        cast(DreamtinkerFluids.molten_echo_alloy.get(), DreamtinkerCommon.echo_alloy.get(), FluidValues.GEM, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.metallivorous_stibium_lupus.get()),
                                     DreamtinkerFluids.molten_lupi_antimony, FluidValues.INGOT, 2.0f)
                            .addByproduct(DreamtinkerFluids.molten_ascending_antimony.result(FluidValues.NUGGET))
                            .save(consumer, location(folder + "metallivorous_stibium_lupus/foundry"));
        cast(DreamtinkerFluids.molten_lupi_antimony.get(), DreamtinkerCommon.metallivorous_stibium_lupus.get(), FluidValues.INGOT, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.nigrescence_antimony.get()),
                                     DreamtinkerFluids.molten_nigrescence_antimony, FluidValues.GEM, 2.0f)
                            .addByproduct(DreamtinkerFluids.molten_albedo_stibium.result(FluidValues.NUGGET * 3))
                            .save(consumer, location(folder + "nigrescence_antimony/foundry"));
        cast(DreamtinkerFluids.molten_nigrescence_antimony.get(), DreamtinkerCommon.nigrescence_antimony.get(), FluidValues.GEM, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_INGOT), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT, 4.0f)
                            .save(consumer, location(folder + "etherium/ingot"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_NUGGET), DreamtinkerFluids.unstable_liquid_aether, FluidValues.NUGGET, 4.0f)
                            .save(consumer, location(folder + "etherium/nugget"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticBlocks.ETHERIUM_BLOCK), DreamtinkerFluids.unstable_liquid_aether, FluidValues.METAL_BLOCK, 4.0f)
                            .save(consumer, location(folder + "etherium/block"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_ORE), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT, 4.0F)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .addByproduct(DreamtinkerFluids.unstable_liquid_aether.result(30)).save(consumer, location(folder + "etherium/ore"));
        //I am sure it's not the best way, but who cares
        int[] etherium_damage = {FluidValues.NUGGET, FluidValues.SLIME_DROP};
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_AXE), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 4, 4.0f)
                            .setDamagable(etherium_damage)
                            .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL * 2))
                            .save(consumer, location(folder + "etherium/axe"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_SWORD), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 2, 4.0f)
                            .setDamagable(FluidValues.NUGGET, FluidValues.SLIME_DROP, FluidValues.GEM_SHARD)
                            .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL))
                            .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 2))
                            .save(consumer, location(folder + "etherium/sword"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_SCYTHE), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 2, 4.0f)
                            .setDamagable(etherium_damage)
                            .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL * 2))
                            .save(consumer, location(folder + "etherium/scythe"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_PICKAXE), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 3, 4.0f)
                            .setDamagable(etherium_damage)
                            .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL * 2))
                            .save(consumer, location(folder + "etherium/pickaxe"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_SHOVEL), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT, 4.0f)
                            .setDamagable(etherium_damage)
                            .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL * 2))
                            .save(consumer, location(folder + "etherium/shovel"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_HELMET), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 5, 4.0f)
                            .setDamagable(FluidValues.NUGGET)
                            .save(consumer, location(folder + "etherium/helmet"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_CHESTPLATE), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 8, 4.0f)
                            .setDamagable(FluidValues.NUGGET)
                            .save(consumer, location(folder + "etherium/chestplate"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_LEGGINGS), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 7, 4.0f)
                            .setDamagable(FluidValues.NUGGET)
                            .save(consumer, location(folder + "etherium/leggings"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_BOOTS), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 4, 4.0f)
                            .setDamagable(FluidValues.NUGGET)
                            .save(consumer, location(folder + "etherium/boots"));
        cast(DreamtinkerFluids.unstable_liquid_aether.get(), EnigmaticItems.ETHERIUM_INGOT, FluidValues.INGOT, consumer);
        cast(DreamtinkerFluids.unstable_liquid_aether.get(), EnigmaticItems.ETHERIUM_NUGGET, FluidValues.NUGGET, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.EVIL_INGOT), DreamtinkerFluids.molten_evil, FluidValues.INGOT, 4.0f)
                            .save(consumer, location(folder + "evil/ingot"));
        cast(DreamtinkerFluids.molten_evil.get(), EnigmaticItems.EVIL_INGOT, FluidValues.INGOT, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.EVIL_ESSENCE), DreamtinkerFluids.molten_nefariousness, FluidValues.GEM, 4.0f)
                            .save(consumer, location(folder + "evil/gem"));
        cast(DreamtinkerFluids.molten_nefariousness.get(), EnigmaticItems.EVIL_ESSENCE, FluidValues.GEM, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(Items.GHAST_TEAR), DreamtinkerFluids.liquid_trist, FluidValues.NUGGET, 4.0f)
                            .save(consumer, location(folder + "ghast_tear/nugget"));
        cast(DreamtinkerFluids.liquid_trist.get(), Items.GHAST_TEAR, FluidValues.NUGGET, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.SOUL_CRYSTAL), DreamtinkerFluids.liquid_pure_soul, FluidValues.GEM, 4.0f)
                            .save(consumer, location(folder + "soul_crystal/gem"));
        cast(DreamtinkerFluids.liquid_pure_soul.get(), EnigmaticItems.SOUL_CRYSTAL, FluidValues.GEM, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.void_pearl.get()), DreamtinkerFluids.molten_void, FluidValues.SLIMEBALL, 4.0f)
                            .save(consumer, location(folder + "void_pearl/slime"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.void_pearl.get())
                                .setCoolingTime(IMeltingRecipe.getTemperature(DreamtinkerFluids.molten_void), FluidValues.SLIMEBALL)
                                .setFluid(FluidIngredient.of(new FluidStack(DreamtinkerFluids.molten_void.get(), FluidValues.SLIMEBALL)))
                                .save(consumer, location(
                                        "smeltery/casting/" +
                                        Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(DreamtinkerCommon.void_pearl.get())).getPath() + "/slime"));

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.soul_etherium.get()),
                                     DreamtinkerFluids.molten_soul_aether, FluidValues.INGOT, 2.0f)
                            .save(consumer, location(folder + "soul_etherium/foundry"));
        cast(DreamtinkerFluids.molten_soul_aether.get(), DreamtinkerCommon.soul_etherium.get(), FluidValues.INGOT, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.twist_obsidian_pane.get()), DreamtinkerFluids.liquid_trist, FluidValues.NUGGET * 3)
                            .addByproduct(DreamtinkerFluids.molten_crying_obsidian.result(FluidValues.GLASS_PANE))
                            .save(consumer, location(folder + "twist/reinforcement"));

    }

    String materials_folder = "tools/materials/";

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
        materialComposite(consumer, MaterialIds.string, DreamtinkerMaterialIds.nigrescence_antimony, DreamtinkerFluids.molten_nigrescence_antimony,
                          FluidValues.GEM, materials_folder);

        materialRecipe(consumer, DreamtinkerMaterialIds.star_regulus, Ingredient.of(DreamtinkerCommon.regulus.get()), 1, 1,
                       materials_folder + "star_regulus");
        materialRecipe(consumer, DreamtinkerMaterialIds.valentinite, Ingredient.of(DreamtinkerCommon.valentinite.get()), 1, 1,
                       materials_folder + "valentinite");
    }

    private void addCompactMaterialRecipes(Consumer<FinishedRecipe> consumer) {
        addELMaterialRecipes(consumer);
        addMalumMatarialRecipes(consumer);
    }

    private void addELMaterialRecipes(Consumer<FinishedRecipe> consumer) {

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.etherium, DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT, materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.etherium, Ingredient.of(EnigmaticItems.ETHERIUM_INGOT), 1, 1,
                       materials_folder + "etherium");
        materialComposite(consumer, MaterialIds.string, DreamtinkerMaterialIds.etherium, DreamtinkerFluids.unstable_liquid_aether,
                          FluidValues.INGOT, materials_folder);
        materialMeltingCasting(consumer, DreamtinkerMaterialIds.nefarious, DreamtinkerFluids.molten_evil, FluidValues.INGOT, materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.nefarious, Ingredient.of(EnigmaticItems.EVIL_INGOT), 1, 1,
                       materials_folder + "nefarious");
        materialComposite(consumer, MaterialIds.string, DreamtinkerMaterialIds.nefarious, DreamtinkerFluids.molten_evil,
                          FluidValues.INGOT, materials_folder);
        materialMeltingCasting(consumer, DreamtinkerMaterialIds.soul_etherium, DreamtinkerFluids.molten_soul_aether, FluidValues.INGOT, materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.soul_etherium, Ingredient.of(DreamtinkerCommon.soul_etherium.get()), 1, 1,
                       materials_folder + "soul_etherium");
        materialComposite(consumer, MaterialIds.string, DreamtinkerMaterialIds.soul_etherium, DreamtinkerFluids.molten_soul_aether,
                          FluidValues.INGOT, materials_folder);
    }

    private void addMalumMatarialRecipes(Consumer<FinishedRecipe> consumer) {
        materialRecipe(consumer, DreamtinkerMaterialIds.spirit_fabric, Ingredient.of(ItemRegistry.SPIRIT_FABRIC.get()), 1, 3,
                       materials_folder + "spirit_fabric");
        materialRecipe(consumer, DreamtinkerMaterialIds.hallowed_gold, Ingredient.of(ItemRegistry.HALLOWED_GOLD_INGOT.get()), 1, 1,
                       materials_folder + "hallowed_gold");
        materialRecipe(consumer, DreamtinkerMaterialIds.mnemonic_fragment, Ingredient.of(ItemRegistry.MNEMONIC_FRAGMENT.get()), 1, 4,
                       materials_folder + "mnemonic_fragment");
    }

    private void addCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, DreamtinkerCommon.nigrescence_antimony.get(), 1)
                              .requires(DreamtinkerCommon.valentinite.get())
                              .requires(Items.ROTTEN_FLESH)
                              .unlockedBy("has_valentinite", has(DreamtinkerCommon.valentinite.get()))
                              .save(consumer, location("currus_triumphalis_antimonii/valentinite_nigredo"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(DreamtinkerTagkeys.Items.raw_stibnite),
                                            RecipeCategory.MISC,
                                            DreamtinkerCommon.valentinite.get(),
                                            1.0f,
                                            100)
                                  .unlockedBy("has_stibnite", has(DreamtinkerTagkeys.Items.raw_stibnite))
                                  .save(consumer, location("currus_triumphalis_antimonii/stibnite_to_valentinite"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, DreamtinkerCommon.persona_cast.get())
                           .define('e', Items.WEEPING_VINES)
                           .define('M', DreamtinkerCommon.twist_obsidian_pane.get())
                           .pattern(" e ")
                           .pattern("eMe")
                           .pattern(" e ")
                           .unlockedBy("has_item", has(DreamtinkerCommon.twist_obsidian_pane.get()))
                           //.group(prefix("fancy_item_frame"))
                           .save(consumer, location("casts/" + DreamtinkerCommon.persona_cast.get()));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, DreamtinkerCommon.wish_cast.get())
                           .define('e', Tags.Items.GEMS_LAPIS)
                           .define('M', DreamtinkerCommon.unborn_egg.get())
                           .pattern("eee")
                           .pattern("eMe")
                           .pattern("eee")
                           .unlockedBy("has_item", has(DreamtinkerCommon.unborn_egg.get()))
                           //.group(prefix("fancy_item_frame"))
                           .save(consumer, location("casts/" + DreamtinkerCommon.reason_cast.get()));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, DreamtinkerCommon.reason_cast.get())
                           .define('e', DreamtinkerCommon.narcissus.get())
                           .define('M', Items.CLOCK)
                           .pattern("eee")
                           .pattern("eMe")
                           .pattern("eee")
                           .unlockedBy("has_item", has(DreamtinkerCommon.unborn_egg.get()))
                           //.group(prefix("fancy_item_frame"))
                           .save(consumer, location("casts/" + DreamtinkerCommon.wish_cast.get()));
    }

    String partFolder = "tools/parts/";
    String castFolder = "smeltery/casts/";

    private void addPartRecipes(Consumer<FinishedRecipe> consumer) {
        //armor
        armorPlatingBuilder(consumer, DreamtinkerMaterialIds.star_regulus);
        armorPlatingBuilder(consumer, DreamtinkerMaterialIds.spirit_fabric);
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
        //partRecipes(consumer, DreamtinkerToolParts.memoryOrthant, TinkerSmeltery.pickHeadCast, 2, partFolder, castFolder);
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
        malumCompactMaterialBuilder(consumer, DreamtinkerMaterialIds.mnemonic_fragment, 4, HeadMaterialStats.ID);
        malumCompactMaterialBuilder(consumer, DreamtinkerMaterialIds.mnemonic_fragment, 4, HandleMaterialStats.ID);
        malumCompactMaterialBuilder(consumer, DreamtinkerMaterialIds.mnemonic_fragment, 4, StatlessMaterialStats.BINDING.getIdentifier());
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

    private void malumCompactMaterialBuilder(Consumer<FinishedRecipe> consumer, MaterialId id, int value, MaterialStatsId statsId) {
        List<ToolPartItem> Parts = DThelper.getPartList(statsId);
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
            CompoundTag nbt = new CompoundTag();
            nbt.putString("Material", id.toString());
            ItemStack stack = new ItemStack(part);
            stack.getOrCreateTag().merge(nbt);
            new SpiritInfusionRecipeBuilder(castItem, 1, stack)
                    .addExtraItem(ItemRegistry.MNEMONIC_FRAGMENT.get(), 2 * value)
                    .addExtraItem(ItemRegistry.SOUL_STAINED_STEEL_INGOT.get(), 1)
                    .addExtraItem(ItemRegistry.AURIC_EMBERS.get(), 2 * value)
                    .addExtraItem(ItemRegistry.FUSED_CONSCIOUSNESS.get(), 1)
                    .addSpirit(SpiritTypeRegistry.WICKED_SPIRIT, 6 * value)
                    .addSpirit(SpiritTypeRegistry.AERIAL_SPIRIT, 4 * value)
                    .addSpirit(SpiritTypeRegistry.AQUEOUS_SPIRIT, 2 * value)
                    .addSpirit(SpiritTypeRegistry.ELDRITCH_SPIRIT, 2 * value)
                    .addSpirit(SpiritTypeRegistry.INFERNAL_SPIRIT, 2 * value)
                    .build(consumer, id.getPath() + part);
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
                             .addInput(TinkerGadgets.efln)
                             .addInput(Tags.Items.GUNPOWDER)
                             .setMaxLevel(3)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.strong_explode, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.strong_explode, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.continuous_explode)
                             .setTools(Ingredient.of(DreamtinkerTools.tntarrow.get()))
                             .addInput(TinkerGadgets.efln)
                             .addInput(Items.TNT)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.continuous_explode, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.continuous_explode, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.continuous_explode)
                             .setTools(Ingredient.of(DreamtinkerTools.tntarrow.get()))
                             .addInput(Items.TNT)
                             .addInput(Tags.Items.GUNPOWDER)
                             .setMinLevel(1)
                             .setMaxLevel(3)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.continuous_explode, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.continuous_explode, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.mei)
                             .setTools(TinkerTags.Items.MELEE)
                             .addInput(Items.POPPY).addInput(Items.POPPY)
                             .addInput(Items.CHAIN).addInput(Items.CHAIN)
                             .save(consumer, prefix(DreamtinkerModifiers.mei, slotlessFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.ender_slayer)
                             .setTools(TinkerTags.Items.MELEE)
                             .addInput(Tags.Items.OBSIDIAN, 2)
                             .addInput(Items.GHAST_TEAR, 2)
                             .addInput(Items.ENDER_EYE, 2)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.ender_slayer, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.ender_slayer, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.life_looting)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(EnigmaticItems.LORE_INSCRIBER, 1)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.life_looting, abilitySalvage))
                             .save(withCondition(consumer, new ItemExistsCondition(ForgeRegistries.ITEMS.getKey(EnigmaticItems.LORE_INSCRIBER))),
                                   prefix(DreamtinkerModifiers.life_looting, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.life_looting)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(Tags.Items.GEMS_EMERALD, 1)
                             .addInput(Tags.Items.STORAGE_BLOCKS_EMERALD, 1)
                             .addInput(Tags.Items.GEMS_EMERALD, 1)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .save(consumer, wrap(DreamtinkerModifiers.life_looting, abilityFolder, "_1"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.weapon_books)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(EnigmaticItems.THE_ACKNOWLEDGMENT, 1)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.weapon_books, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.weapon_books, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.weapon_books)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(EnigmaticItems.THE_TWIST, 1)
                             .setLevelRange(2, 2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.weapon_books, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.weapon_books, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.weapon_books)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(EnigmaticItems.THE_INFINITUM, 1)
                             .setLevelRange(3, 3)
                             .setSlots(SlotType.SOUL, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.weapon_books, soulSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.weapon_books, soulFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.eldritch_pan)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(EnigmaticItems.ELDRITCH_PAN, 1)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.eldritch_pan, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.eldritch_pan, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.desolation_ring)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(EnigmaticItems.EVIL_ESSENCE, 2)
                             .addInput(Items.HEART_OF_THE_SEA, 2)
                             .addInput(Tags.Items.INGOTS_NETHERITE, 2)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.desolation_ring, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.desolation_ring, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.soul_upgrade)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(DreamtinkerCommon.twist_obsidian_pane.get(), 1)
                             .setMaxLevel(3)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.soul_upgrade, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.soul_upgrade, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.soul_core)
                             .setTools(TinkerTags.Items.MELEE_WEAPON)
                             .addInput(DreamtinkerCommon.unborn_turtle_egg.get(), 1)
                             .setMaxLevel(1)
                             .setSlots(SlotType.SOUL, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.soul_core, soulSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.soul_core, soulFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.soul_core)
                             .setTools(TinkerTags.Items.MELEE_WEAPON)
                             .addInput(DreamtinkerCommon.unborn_turtle_egg.get(), 1)
                             .addInput(DreamtinkerCommon.unborn_sniffer_egg.get(), 1)
                             .setMaxLevel(3)
                             .setSlots(SlotType.SOUL, 1)
                             .save(consumer, wrap(DreamtinkerModifiers.Ids.soul_core, soulFolder, "_1"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.memory_base)
                             .setTools(Ingredient.of(DreamtinkerTools.narcissus_wing.get()))
                             .addInput(DreamtinkerTools.narcissus_wing)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.memory_base, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.memory_base, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.icy_memory)
                             .setTools(TinkerTags.Items.MELEE_WEAPON)
                             .addInput(DreamtinkerCommon.unborn_egg.get())
                             .addInput(Items.POWDER_SNOW_BUCKET)
                             .setMaxLevel(2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.icy_memory, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.icy_memory, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.hate_memory)
                             .setTools(TinkerTags.Items.MELEE_WEAPON)
                             .addInput(DreamtinkerCommon.unborn_egg.get())
                             .addInput(Items.WHITE_BANNER)
                             .addInput(Items.IRON_AXE)
                             .addInput(Tags.Items.TOOLS_CROSSBOWS)
                             .setMaxLevel(3)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.hate_memory, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.hate_memory, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.foundation_will)
                             .setTools(Ingredient.of(DreamtinkerTools.narcissus_wing))
                             .addInput(DreamtinkerCommon.unborn_turtle_egg.get())
                             .addInput(DreamtinkerCommon.unborn_turtle_egg.get())
                             .addInput(TinkerTags.Items.HARVEST_PRIMARY)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.foundation_will, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.foundation_will, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.huge_ego)
                             .addInput(DreamtinkerCommon.persona_cast.get())
                             .addInput(DreamtinkerCommon.persona_cast.get())
                             .setMaxLevel(3)
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.huge_ego, slotlessFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_rebound)
                             .setTools(ItemTagRegistry.SCYTHE)
                             //.setTools(TinkerTags.Items.MELEE_WEAPON)
                             .addInput(ItemRegistry.CRUDE_SCYTHE.get())
                             .addInput(ItemRegistry.EARTHEN_SPIRIT.get())
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.malum_rebound, abilitySalvage))
                             .save(withCondition(consumer, new ItemExistsCondition(ForgeRegistries.ITEMS.getKey(ItemRegistry.CRUDE_SCYTHE.get()))),
                                   prefix(DreamtinkerModifiers.Ids.malum_rebound, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_rebound)
                             .setTools(ItemTagRegistry.SCYTHE)
                             //.setTools(TinkerTags.Items.MELEE_WEAPON)
                             .addInput(ItemRegistry.CRUDE_SCYTHE.get())
                             .setLevelRange(2, 3)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.malum_rebound, upgradeSalvage))
                             .save(withCondition(consumer, new ItemExistsCondition(ForgeRegistries.ITEMS.getKey(ItemRegistry.CRUDE_SCYTHE.get()))),
                                   prefix(DreamtinkerModifiers.Ids.malum_rebound, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_ascension)
                             .setTools(ItemTagRegistry.SCYTHE)
                             //.setTools(TinkerTags.Items.MELEE_WEAPON)
                             .addInput(ItemRegistry.CRUDE_SCYTHE.get())
                             .addInput(ItemRegistry.AERIAL_SPIRIT.get())
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.malum_ascension, abilitySalvage))
                             .save(withCondition(consumer, new ItemExistsCondition(ForgeRegistries.ITEMS.getKey(ItemRegistry.CRUDE_SCYTHE.get()))),
                                   prefix(DreamtinkerModifiers.Ids.malum_ascension, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_ascension)
                             .setTools(ItemTagRegistry.SCYTHE)
                             //.setTools(TinkerTags.Items.MELEE_WEAPON)
                             .addInput(ItemRegistry.CRUDE_SCYTHE.get())
                             .setLevelRange(2, 3)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.malum_ascension, upgradeSalvage))
                             .save(withCondition(consumer, new ItemExistsCondition(ForgeRegistries.ITEMS.getKey(ItemRegistry.CRUDE_SCYTHE.get()))),
                                   prefix(DreamtinkerModifiers.Ids.malum_ascension, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_animated)
                             .setTools(ItemTagRegistry.SCYTHE)
                             //.setTools(TinkerTags.Items.MELEE_WEAPON)
                             .addInput(ItemRegistry.WICKED_SPIRIT.get())
                             .addInput(ItemRegistry.AERIAL_SPIRIT.get())
                             .setMaxLevel(2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.malum_animated, upgradeSalvage))
                             .save(withCondition(consumer, new ItemExistsCondition(ForgeRegistries.ITEMS.getKey(ItemRegistry.WICKED_SPIRIT.get()))),
                                   prefix(DreamtinkerModifiers.Ids.malum_animated, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_haunted)
                             .setTools(ItemTagRegistry.SCYTHE)
                             //.setTools(TinkerTags.Items.MELEE_WEAPON)
                             .addInput(ItemRegistry.WICKED_SPIRIT.get())
                             .addInput(ItemRegistry.SACRED_SPIRIT.get())
                             .setMaxLevel(2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.malum_haunted, upgradeSalvage))
                             .save(withCondition(consumer, new ItemExistsCondition(ForgeRegistries.ITEMS.getKey(ItemRegistry.WICKED_SPIRIT.get()))),
                                   prefix(DreamtinkerModifiers.Ids.malum_haunted, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_spirit_plunder)
                             .setTools(ItemTagRegistry.SCYTHE)
                             //.setTools(TinkerTags.Items.MELEE_WEAPON)
                             .addInput(ItemRegistry.SACRED_SPIRIT.get())
                             .addInput(ItemRegistry.SACRED_SPIRIT.get())
                             .setMaxLevel(2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.malum_spirit_plunder, upgradeSalvage))
                             .save(withCondition(consumer, new ItemExistsCondition(ForgeRegistries.ITEMS.getKey(ItemRegistry.WICKED_SPIRIT.get()))),
                                   prefix(DreamtinkerModifiers.Ids.malum_spirit_plunder, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.flaming_memory)
                             .setTools(Ingredient.of(DreamtinkerTools.narcissus_wing))
                             .addInput(DreamtinkerToolParts.memoryOrthant.get(), 1)
                             .addInput(DreamtinkerCommon.nigrescence_antimony.get(), 6)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 2)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.flaming_memory, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.flaming_memory, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.el_torrent)
                             .setTools(Ingredient.of(DreamtinkerTools.mashou))
                             .addInput(Tags.Items.DUSTS_PRISMARINE, 15)
                             .addInput(Tags.Items.GEMS_PRISMARINE, 15)
                             .setMaxLevel(5)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.el_torrent, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.el_torrent, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.el_wrath)
                             .setTools(Ingredient.of(DreamtinkerTools.mashou))
                             .addInput(Items.PRISMARINE, 4)
                             .addInput(Tags.Items.GEMS_PRISMARINE, 15)
                             .setMaxLevel(4)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.el_wrath, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.el_wrath, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.el_slayer)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(Items.IRON_SWORD)
                             .addInput(Items.IRON_SWORD)
                             .addInput(Items.GOLDEN_SWORD)
                             .addInput(Items.IRON_AXE)
                             .addInput(Items.IRON_AXE)
                             .setMaxLevel(4)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.el_slayer, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.el_slayer, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.el_eternal_binding)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(Items.CHAIN, 10)
                             .addInput(Items.ENCHANTING_TABLE)
                             .setMaxLevel(1)
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.el_eternal_binding, slotlessFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.el_sorrow)
                             .setTools(TinkerTags.Items.ARMOR)
                             .addInput(Items.WEEPING_VINES, 10)
                             .addInput(Items.ENCHANTING_TABLE)
                             .setMaxLevel(1)
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.el_sorrow, slotlessFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.el_nemesis_curse)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(Items.SHIELD)
                             .addInput(Items.ENCHANTING_TABLE)
                             .setMaxLevel(1)
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.el_nemesis_curse, slotlessFolder));
    }

    private void addEntityMeltingRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = "smeltery/entity_melting/";
        String headFolder = "smeltery/entity_melting/heads/";

        EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.WARDEN), DreamtinkerFluids.molten_echo_shard.result(FluidValues.GEM_SHARD), 5)
                                  .save(consumer, location(folder + "molten_echo_shard"));
    }

    @Override
    public @NotNull String getModId() {
        return Dreamtinker.MODID;
    }

    private static TagKey<Item> forgeItemTag(String name) {
        return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("forge", name));
    }

    private static TagKey<Fluid> forgeFluidTag(String name) {
        return TagKey.create(ForgeRegistries.FLUIDS.getRegistryKey(), new ResourceLocation("forge", name));
    }

    private static TagKey<Item> tconItemTag(String name) {
        return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("tconstruct", name));
    }

    private static TagKey<Fluid> tconfluidTag(String name) {
        return TagKey.create(ForgeRegistries.FLUIDS.getRegistryKey(), new ResourceLocation("tconstruct", name));
    }

    private void cast(Fluid fluid, ItemLike ingredient, int amount, Consumer<FinishedRecipe> consumer) {
        CastItemObject cast =
                FluidValues.GEM == amount ? TinkerSmeltery.gemCast : FluidValues.INGOT == amount ? TinkerSmeltery.ingotCast : TinkerSmeltery.nuggetCast;
        ItemCastingRecipeBuilder.tableRecipe(ingredient).setCoolingTime(IMeltingRecipe.getTemperature(fluid), amount)
                                .setFluid(FluidIngredient.of(new FluidStack(fluid, amount)))
                                .setCast(cast.getSingleUseTag(), true)
                                .save(consumer, location(
                                        "smeltery/casting/" + Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ingredient.asItem())).getPath() + "/single"));
        ItemCastingRecipeBuilder.tableRecipe(ingredient).setCoolingTime(IMeltingRecipe.getTemperature(fluid), amount)
                                .setFluid(FluidIngredient.of(new FluidStack(fluid, amount)))
                                .setCast(cast.getMultiUseTag(), false).save(consumer, location(
                                        "smeltery/casting/" + Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ingredient.asItem())).getPath() + "/multi"));
    }
}

