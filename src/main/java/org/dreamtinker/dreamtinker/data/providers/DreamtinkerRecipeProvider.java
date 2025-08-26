package org.dreamtinker.dreamtinker.data.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.data.DreamtinkerMaterialIds;
import org.dreamtinker.dreamtinker.data.DreamtinkerTagkeys;
import org.dreamtinker.dreamtinker.register.DreamtinkerFluid;
import org.dreamtinker.dreamtinker.register.DreamtinkerItem;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifer;
import slimeknights.mantle.recipe.data.IRecipeHelper;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelBuilder;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.ItemPartRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.recycle.PartBuilderToolRecycleBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class DreamtinkerRecipeProvider extends RecipeProvider implements IMaterialRecipeHelper, IToolRecipeHelper, IConditionBuilder, IRecipeHelper {

    public DreamtinkerRecipeProvider(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        this.addCraftingRecipes(consumer);
        this.addMeltingRecipes(consumer);
        this.addCastingRecipes(consumer);
        this.addAlloyRecipes(consumer);
        this.addMaterialRecipes(consumer);

        this.addCompatRecipes(consumer);
        this.addPartRecipes(consumer);
        this.addToolBuildingRecipes(consumer);
        this.addModifierRecipes(consumer);
    }

    private void addToolBuildingRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = "tools/building/";
        String armorFolder = "tools/armor/";
        ToolBuildingRecipeBuilder.toolBuildingRecipe(DreamtinkerItem.tntarrow.get())
                                 .outputSize(4)
                                 .save(consumer, prefix(DreamtinkerItem.tntarrow, folder));
        toolBuilding(consumer, DreamtinkerItem.masu, folder);
        folder = "tools/recycling/";
        PartBuilderToolRecycleBuilder.tools(SizedIngredient.fromItems(4, DreamtinkerItem.tntarrow.get()))
                                     .save(consumer, location(folder + "tntarrow"));
    }

    private void addCompatRecipes(Consumer<FinishedRecipe> consumer) {}

    private void addAlloyRecipes(Consumer<FinishedRecipe> consumer) {
        AlloyRecipeBuilder.alloy(DreamtinkerFluid.molten_albedo_stibium, FluidValues.GEM)
                          .addCatalyst(DreamtinkerFluid.molten_lupi_antimony.ingredient(FluidValues.INGOT))
                          .addInput(TinkerTags.Fluids.METAL_TOOLTIPS, FluidValues.INGOT)
                          .save(consumer, location("currus_triumphalis_antimonii/lupi_to_albedo"));
        AlloyRecipeBuilder.alloy(FluidOutput.fromStack(new FluidStack(DreamtinkerFluid.liquid_smoky_antimony.get(), FluidValues.INGOT * 2)), 6000)
                          .addInput(DreamtinkerFluid.molten_ascending_antimony.ingredient(FluidValues.INGOT))
                          .addInput(TinkerFluids.liquidSoul.ingredient(FluidValues.INGOT))
                          .save(consumer, location("currus_triumphalis_antimonii/ascending_to_smoky"));
    }

    private void addCastingRecipes(Consumer<FinishedRecipe> consumer) {
        // Pure Fluid Recipes
        String folder = "smeltery/casting/";
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerItem.regulus.get())
                                .setFluid(DreamtinkerFluid.liquid_smoky_antimony.getLocalTag(), FluidValues.INGOT)
                                .setCoolingTime(100)
                                .setCast(Tags.Items.DUSTS_REDSTONE, true)
                                .save(consumer, location("currus_triumphalis_antimonii/smoky_to_star"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerItem.metallivorous_stibium_lupus.get())
                                .setFluid(DreamtinkerFluid.molten_albedo_stibium.getLocalTag(), FluidValues.GEM)
                                .setCoolingTime(100)
                                .setCast(Tags.Items.STORAGE_BLOCKS_GOLD, true)
                                .save(consumer, location("currus_triumphalis_antimonii/albedo_to_lupus_block"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerItem.metallivorous_stibium_lupus.get())
                                .setFluid(DreamtinkerFluid.molten_albedo_stibium.getLocalTag(), FluidValues.GEM)
                                .setCoolingTime(100)
                                .setCast(forgeItemTag("dusts/gold"), true)
                                .save(withCondition(consumer, tagCondition("dusts/gold")), location("currus_triumphalis_antimonii/albedo_to_lupus_dust"));

        ItemCastingRecipeBuilder.basinRecipe(Blocks.CRYING_OBSIDIAN)
                                .setFluidAndTime(DreamtinkerFluid.molten_crying_obsidian, FluidValues.GLASS_BLOCK)
                                .save(consumer, location(folder + "crying_obsidian/block"));
    }

    private void addMeltingRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = "smeltery/melting/";
        MeltingFuelBuilder.fuel(DreamtinkerFluid.molten_lupi_antimony.ingredient(FluidValues.INGOT), 776, 7776)
                          .save(consumer, location(folder + "fuel/molten_lupi_antimony"));

        MeltingRecipeBuilder.melting(Ingredient.of(Items.CRYING_OBSIDIAN), DreamtinkerFluid.molten_crying_obsidian, FluidValues.GLASS_BLOCK, 2.0f)
                            .save(consumer, location(folder + "crying_obsidian/block"));

        MeltingRecipeBuilder.melting(Ingredient.of(Items.ECHO_SHARD), DreamtinkerFluid.molten_echo_shard, FluidValues.GEM, 2.0f)
                            .save(consumer, location(folder + "echo_shard/gem"));
        cast(DreamtinkerFluid.molten_echo_shard.get(), Items.ECHO_SHARD, FluidValues.GEM, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerItem.metallivorous_stibium_lupus.get()),
                                     DreamtinkerFluid.molten_lupi_antimony, FluidValues.INGOT, 2.0f)
                            .addByproduct(DreamtinkerFluid.molten_ascending_antimony.result(FluidValues.NUGGET))
                            .save(consumer, location(folder + "metallivorous_stibium_lupus/foundry"));
        cast(DreamtinkerFluid.molten_lupi_antimony.get(), DreamtinkerItem.metallivorous_stibium_lupus.get(), FluidValues.INGOT, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerItem.nigrescence_antimony.get()),
                                     DreamtinkerFluid.molten_nigrescence_antimony, FluidValues.GEM, 2.0f)
                            .addByproduct(DreamtinkerFluid.molten_albedo_stibium.result(FluidValues.NUGGET * 3))
                            .save(consumer, location(folder + "nigrescence_antimony/foundry"));
        cast(DreamtinkerFluid.molten_nigrescence_antimony.get(), DreamtinkerItem.nigrescence_antimony.get(), FluidValues.GEM, consumer);
    }

    private void addMaterialRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = "tools/materials/";
        materialMeltingCasting(consumer, DreamtinkerMaterialIds.crying_obsidian, DreamtinkerFluid.molten_crying_obsidian, FluidValues.GLASS_BLOCK, folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.crying_obsidian, Ingredient.of(Items.CRYING_OBSIDIAN), 1, 1, folder + "crying_obsidian");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.echo_shard, DreamtinkerFluid.molten_echo_shard, FluidValues.GEM, folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.echo_shard, Ingredient.of(Items.ECHO_SHARD), 1, 1, folder + "echo_shard");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.metallivorous_stibium_lupus, DreamtinkerFluid.molten_lupi_antimony, FluidValues.INGOT, folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.metallivorous_stibium_lupus, Ingredient.of(DreamtinkerItem.metallivorous_stibium_lupus.get()), 1, 1,
                       folder + "metallivorous_stibium_lupus");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.nigrescence_antimony, DreamtinkerFluid.molten_nigrescence_antimony, FluidValues.GEM, folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.nigrescence_antimony, Ingredient.of(DreamtinkerItem.nigrescence_antimony.get()), 1, 1,
                       folder + "nigrescence_antimony");
        materialComposite(consumer, MaterialIds.string, DreamtinkerMaterialIds.nigrescence_antimony, DreamtinkerFluid.molten_nigrescence_antimony,
                          FluidValues.GEM, folder);

        materialRecipe(consumer, DreamtinkerMaterialIds.star_regulus, Ingredient.of(DreamtinkerItem.regulus.get()), 1, 1,
                       folder + "star_regulus");

        materialRecipe(consumer, DreamtinkerMaterialIds.valentinite, Ingredient.of(DreamtinkerItem.valentinite.get()), 1, 1,
                       folder + "valentinite");
    }

    private void addCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, DreamtinkerItem.nigrescence_antimony.get(), 1)
                              .requires(DreamtinkerItem.valentinite.get())
                              .requires(Items.ROTTEN_FLESH)
                              .unlockedBy("has_valentinite", has(DreamtinkerItem.valentinite.get()))
                              .save(consumer, location("currus_triumphalis_antimonii/valentinite_nigredo"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(DreamtinkerTagkeys.Items.raw_stibnite),
                                            RecipeCategory.MISC,
                                            DreamtinkerItem.valentinite.get(),
                                            1.0f,
                                            100)
                                  .unlockedBy("has_stibnite", has(DreamtinkerTagkeys.Items.raw_stibnite))
                                  .save(consumer, location("currus_triumphalis_antimonii/stibnite_to_valentinite"));
    }

    private void addPartRecipes(Consumer<FinishedRecipe> consumer) {
        String partFolder = "tools/parts/";
        String castFolder = "smeltery/casts/";
        //backdoor for star_regulus
        ArrayList<CastItemObject> casts = new ArrayList<>(
                Arrays.asList(TinkerSmeltery.helmetPlatingCast, TinkerSmeltery.chestplatePlatingCast, TinkerSmeltery.leggingsPlatingCast,
                              TinkerSmeltery.bootsPlatingCast));
        List<ToolPartItem> ai = TinkerToolParts.plating.values();
        int[] costs = {3, 6, 5, 2};
        CompoundTag nbt = new CompoundTag();
        nbt.putString("Material", "dreamtinker:star_regulus");
        for (int i = 0; i < casts.size(); i++) {
            ItemStack stack = new ItemStack(ai.get(i));
            stack.getOrCreateTag().merge(nbt);
            ItemPartRecipeBuilder.item(casts.get(i).getName(), ItemOutput.fromStack(stack))
                                 .material(DreamtinkerMaterialIds.star_regulus, costs[i])
                                 .setPatternItem(CompoundIngredient.of(Ingredient.of(TinkerTags.Items.DEFAULT_PATTERNS),
                                                                       Ingredient.of(casts.get(i).get())))
                                 .save(consumer, location(partFolder + "builder/star_regulus/" + casts.get(i).getName().getPath()));
        }

        MaterialCastingRecipeBuilder.tableRecipe(DreamtinkerItem.explode_core.get())
                                    .setCast(Items.GUNPOWDER, true)
                                    .setItemCost(8)
                                    .save(consumer, location(partFolder + "explode_core"));
    }

    private void addModifierRecipes(Consumer<FinishedRecipe> consumer) {
        // modifiers
        String upgradeFolder = "tools/modifiers/upgrade/";
        String abilityFolder = "tools/modifiers/ability/";
        String slotlessFolder = "tools/modifiers/slotless/";
        String defenseFolder = "tools/modifiers/defense/";
        String compatFolder = "tools/modifiers/compat/";
        String worktableFolder = "tools/modifiers/worktable/";
        // salvage
        String salvageFolder = "tools/modifiers/salvage/";
        String upgradeSalvage = salvageFolder + "upgrade/";
        String abilitySalvage = salvageFolder + "ability/";
        String defenseSalvage = salvageFolder + "defense/";
        String compatSalvage = salvageFolder + "compat/";
        ModifierRecipeBuilder.modifier(DreamtinkerModifer.realsweep)
                             .setTools(Ingredient.of(DreamtinkerItem.masu.get()))
                             .addInput(Items.ECHO_SHARD)
                             .addInput(Items.ECHO_SHARD)
                             .setMaxLevel(2)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifer.realsweep, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifer.realsweep, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifer.strong_explode)
                             .setTools(Ingredient.of(DreamtinkerItem.masu.get()))
                             .addInput(TinkerGadgets.efln)
                             .addInput(Tags.Items.GUNPOWDER)
                             .setMaxLevel(3)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifer.strong_explode, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifer.strong_explode, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifer.mei)
                             .setTools(TinkerTags.Items.MELEE)
                             .addInput(Items.POPPY).addInput(Items.POPPY)
                             .addInput(Items.CHAIN).addInput(Items.CHAIN)
                             .save(consumer, prefix(DreamtinkerModifer.mei, slotlessFolder));
    }

    @Override
    public String getModId() {
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

    private void cast(Fluid fluid, ItemLike ingredient, int amount, Consumer<FinishedRecipe> consumer) {
        CastItemObject cast = FluidValues.GEM == amount ? TinkerSmeltery.gemCast : TinkerSmeltery.ingotCast;
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

