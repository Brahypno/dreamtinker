package org.dreamtinker.dreamtinker.data.providers;

import com.aizistral.enigmaticlegacy.registries.EnigmaticBlocks;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.data.DreamtinkerMaterialIds;
import org.dreamtinker.dreamtinker.data.DreamtinkerTagkeys;
import org.dreamtinker.dreamtinker.register.DreamtinkerFluids;
import org.dreamtinker.dreamtinker.register.DreamtinkerItems;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifers;
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
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelBuilder;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer;
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

        this.addPartRecipes(consumer);
        this.addToolBuildingRecipes(consumer);
        this.addModifierRecipes(consumer);
        this.addEntityMeltingRecipes(consumer);
    }

    private void addToolBuildingRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = "tools/building/";
        String armorFolder = "tools/armor/";
        ToolBuildingRecipeBuilder.toolBuildingRecipe(DreamtinkerItems.tntarrow.get())
                                 .outputSize(4)
                                 .save(consumer, prefix(DreamtinkerItems.tntarrow, folder));
        toolBuilding(consumer, DreamtinkerItems.masu, folder);
        folder = "tools/recycling/";
        PartBuilderToolRecycleBuilder.tools(SizedIngredient.fromItems(4, DreamtinkerItems.tntarrow.get()))
                                     .save(consumer, location(folder + "tntarrow"));
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
    }

    private void addCastingRecipes(Consumer<FinishedRecipe> consumer) {
        // Pure Fluid Recipes
        String folder = "smeltery/casting/";
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerItems.regulus.get())
                                .setFluid(DreamtinkerFluids.liquid_smoky_antimony.getLocalTag(), FluidValues.INGOT)
                                .setCoolingTime(100)
                                .setCast(Tags.Items.DUSTS_REDSTONE, true)
                                .save(consumer, location("currus_triumphalis_antimonii/smoky_to_star"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerItems.metallivorous_stibium_lupus.get())
                                .setFluid(DreamtinkerFluids.molten_albedo_stibium.getLocalTag(), FluidValues.GEM)
                                .setCoolingTime(100)
                                .setCast(Tags.Items.STORAGE_BLOCKS_GOLD, true)
                                .save(consumer, location("currus_triumphalis_antimonii/albedo_to_lupus_block"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerItems.metallivorous_stibium_lupus.get())
                                .setFluid(DreamtinkerFluids.molten_albedo_stibium.getLocalTag(), FluidValues.GEM)
                                .setCoolingTime(100)
                                .setCast(forgeItemTag("dusts/gold"), true)
                                .save(withCondition(consumer, tagCondition("dusts/gold")), location("currus_triumphalis_antimonii/albedo_to_lupus_dust"));

        ItemCastingRecipeBuilder.basinRecipe(Blocks.CRYING_OBSIDIAN)
                                .setFluidAndTime(DreamtinkerFluids.molten_crying_obsidian, FluidValues.GLASS_BLOCK)
                                .save(consumer, location(folder + "crying_obsidian/block"));

        ItemCastingRecipeBuilder.basinRecipe(EnigmaticBlocks.ETHERIUM_BLOCK)
                                .setFluidAndTime(DreamtinkerFluids.unstable_liquid_aether, FluidValues.METAL_BLOCK)
                                .save(consumer, location(folder + "etherium/block"));
        ItemCastingRecipeBuilder.tableRecipe(EnigmaticItems.VOID_PEARL)
                                .setCoolingTime(2000, 10)
                                .setCast(DreamtinkerItems.void_pearl.get(), true)
                                .setFluid(FluidIngredient.of(new FluidStack(DreamtinkerFluids.molten_ascending_antimony.get(), FluidValues.METAL_BLOCK)))
                                .save(consumer, location("void_pearl/ascending"));
        ItemCastingRecipeBuilder.tableRecipe(EnigmaticItems.OCEAN_STONE)
                                .setCoolingTime(2000, 10)
                                .setCast(Items.HEART_OF_THE_SEA, true)
                                .setFluid(FluidIngredient.of(new FluidStack(DreamtinkerFluids.molten_ascending_antimony.get(), FluidValues.METAL_BLOCK)))
                                .save(consumer, location("ocean_stone/ascending"));
    }

    private void addMeltingRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = "smeltery/melting/";
        MeltingFuelBuilder.fuel(DreamtinkerFluids.molten_lupi_antimony.ingredient(FluidValues.INGOT), 776, 7776)
                          .save(consumer, location(folder + "fuel/molten_lupi_antimony"));
        MeltingFuelBuilder.fuel(DreamtinkerFluids.reversed_shadow.ingredient(FluidValues.SLIMEBALL), 17, 2200)
                          .save(consumer, location(folder + "fuel/reversed_shadow"));

        MeltingRecipeBuilder.melting(Ingredient.of(Items.CRYING_OBSIDIAN), DreamtinkerFluids.molten_crying_obsidian, FluidValues.GLASS_BLOCK, 2.0f)
                            .save(consumer, location(folder + "crying_obsidian/block"));

        MeltingRecipeBuilder.melting(Ingredient.of(Items.ECHO_SHARD), DreamtinkerFluids.molten_echo_shard, FluidValues.GEM, 2.0f)
                            .save(consumer, location(folder + "echo_shard/gem"));
        cast(DreamtinkerFluids.molten_echo_shard.get(), Items.ECHO_SHARD, FluidValues.GEM, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerItems.metallivorous_stibium_lupus.get()),
                                     DreamtinkerFluids.molten_lupi_antimony, FluidValues.INGOT, 2.0f)
                            .addByproduct(DreamtinkerFluids.molten_ascending_antimony.result(FluidValues.NUGGET))
                            .save(consumer, location(folder + "metallivorous_stibium_lupus/foundry"));
        cast(DreamtinkerFluids.molten_lupi_antimony.get(), DreamtinkerItems.metallivorous_stibium_lupus.get(), FluidValues.INGOT, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerItems.nigrescence_antimony.get()),
                                     DreamtinkerFluids.molten_nigrescence_antimony, FluidValues.GEM, 2.0f)
                            .addByproduct(DreamtinkerFluids.molten_albedo_stibium.result(FluidValues.NUGGET * 3))
                            .save(consumer, location(folder + "nigrescence_antimony/foundry"));
        cast(DreamtinkerFluids.molten_nigrescence_antimony.get(), DreamtinkerItems.nigrescence_antimony.get(), FluidValues.GEM, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_INGOT), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT, 4.0f)
                            .save(consumer, location(folder + "etherium/ingot"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_NUGGET), DreamtinkerFluids.unstable_liquid_aether, FluidValues.NUGGET, 4.0f)
                            .save(consumer, location(folder + "etherium/nugget"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticBlocks.ETHERIUM_BLOCK), DreamtinkerFluids.unstable_liquid_aether, FluidValues.METAL_BLOCK, 4.0f)
                            .save(consumer, location(folder + "etherium/block"));
        MeltingRecipeBuilder.melting(Ingredient.of(EnigmaticItems.ETHERIUM_ORE), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT, 4.0F)
                            .setOre(IMeltingContainer.OreRateType.METAL, new IMeltingContainer.OreRateType[0])
                            .addByproduct(DreamtinkerFluids.unstable_liquid_aether.result(30)).save(consumer, location(folder + "etherium/ore"));
        //I am sure its not the best way, but who cares
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

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerItems.void_pearl.get()), DreamtinkerFluids.molten_void, FluidValues.SLIMEBALL, 4.0f)
                            .save(consumer, location(folder + "void_pearl/slime"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerItems.void_pearl.get())
                                .setCoolingTime(IMeltingRecipe.getTemperature(DreamtinkerFluids.molten_void), FluidValues.SLIMEBALL)
                                .setFluid(FluidIngredient.of(new FluidStack(DreamtinkerFluids.molten_void.get(), FluidValues.SLIMEBALL)))
                                .save(consumer, location(
                                        "smeltery/casting/" +
                                        Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(DreamtinkerItems.void_pearl.get())).getPath() + "/slime"));

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerItems.soul_etherium.get()),
                                     DreamtinkerFluids.molten_soul_aether, FluidValues.INGOT, 2.0f)
                            .save(consumer, location(folder + "soul_etherium/foundry"));
        cast(DreamtinkerFluids.molten_soul_aether.get(), DreamtinkerItems.soul_etherium.get(), FluidValues.INGOT, consumer);

    }

    private void addMaterialRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = "tools/materials/";
        materialMeltingCasting(consumer, DreamtinkerMaterialIds.crying_obsidian, DreamtinkerFluids.molten_crying_obsidian, FluidValues.GLASS_BLOCK, folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.crying_obsidian, Ingredient.of(Items.CRYING_OBSIDIAN), 1, 1, folder + "crying_obsidian");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.echo_shard, DreamtinkerFluids.molten_echo_shard, FluidValues.GEM, folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.echo_shard, Ingredient.of(Items.ECHO_SHARD), 1, 1, folder + "echo_shard");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.metallivorous_stibium_lupus, DreamtinkerFluids.molten_lupi_antimony, FluidValues.INGOT, folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.metallivorous_stibium_lupus, Ingredient.of(DreamtinkerItems.metallivorous_stibium_lupus.get()), 1, 1,
                       folder + "metallivorous_stibium_lupus");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.nigrescence_antimony, DreamtinkerFluids.molten_nigrescence_antimony, FluidValues.GEM, folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.nigrescence_antimony, Ingredient.of(DreamtinkerItems.nigrescence_antimony.get()), 1, 1,
                       folder + "nigrescence_antimony");
        materialComposite(consumer, MaterialIds.string, DreamtinkerMaterialIds.nigrescence_antimony, DreamtinkerFluids.molten_nigrescence_antimony,
                          FluidValues.GEM, folder);

        materialRecipe(consumer, DreamtinkerMaterialIds.star_regulus, Ingredient.of(DreamtinkerItems.regulus.get()), 1, 1,
                       folder + "star_regulus");

        materialRecipe(consumer, DreamtinkerMaterialIds.valentinite, Ingredient.of(DreamtinkerItems.valentinite.get()), 1, 1,
                       folder + "valentinite");

        materialMeltingCasting(consumer, DreamtinkerMaterialIds.etherium, DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT, folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.etherium, Ingredient.of(EnigmaticItems.ETHERIUM_INGOT), 1, 1,
                       folder + "etherium");
        materialComposite(consumer, MaterialIds.string, DreamtinkerMaterialIds.etherium, DreamtinkerFluids.unstable_liquid_aether,
                          FluidValues.INGOT, folder);
        materialMeltingCasting(consumer, DreamtinkerMaterialIds.nefarious, DreamtinkerFluids.molten_evil, FluidValues.INGOT, folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.nefarious, Ingredient.of(EnigmaticItems.EVIL_INGOT), 1, 1,
                       folder + "nefarious");
        materialComposite(consumer, MaterialIds.string, DreamtinkerMaterialIds.nefarious, DreamtinkerFluids.molten_evil,
                          FluidValues.INGOT, folder);
        materialMeltingCasting(consumer, DreamtinkerMaterialIds.soul_etherium, DreamtinkerFluids.molten_soul_aether, FluidValues.INGOT, folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.soul_etherium, Ingredient.of(DreamtinkerItems.soul_etherium.get()), 1, 1,
                       folder + "soul_etherium");
        materialComposite(consumer, MaterialIds.string, DreamtinkerMaterialIds.soul_etherium, DreamtinkerFluids.molten_soul_aether,
                          FluidValues.INGOT, folder);
    }

    private void addCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, DreamtinkerItems.nigrescence_antimony.get(), 1)
                              .requires(DreamtinkerItems.valentinite.get())
                              .requires(Items.ROTTEN_FLESH)
                              .unlockedBy("has_valentinite", has(DreamtinkerItems.valentinite.get()))
                              .save(consumer, location("currus_triumphalis_antimonii/valentinite_nigredo"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(DreamtinkerTagkeys.Items.raw_stibnite),
                                            RecipeCategory.MISC,
                                            DreamtinkerItems.valentinite.get(),
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
        List<ToolPartItem> toolParts = TinkerToolParts.plating.values();
        int[] costs = {3, 6, 5, 2};
        CompoundTag nbt = new CompoundTag();
        nbt.putString("Material", "dreamtinker:star_regulus");
        for (int i = 0; i < casts.size(); i++) {
            ItemStack stack = new ItemStack(toolParts.get(i));
            stack.getOrCreateTag().merge(nbt);
            ItemPartRecipeBuilder.item(casts.get(i).getName(), ItemOutput.fromStack(stack))
                                 .material(DreamtinkerMaterialIds.star_regulus, costs[i])
                                 .setPatternItem(CompoundIngredient.of(Ingredient.of(TinkerTags.Items.DEFAULT_PATTERNS),
                                                                       Ingredient.of(casts.get(i).get())))
                                 .save(consumer, location(partFolder + "builder/star_regulus/" + casts.get(i).getName().getPath()));
        }

        MaterialCastingRecipeBuilder.tableRecipe(DreamtinkerItems.explode_core.get())
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
        String soulFolder = "tools/modifiers/soul/";
        // salvage
        String salvageFolder = "tools/modifiers/salvage/";
        String upgradeSalvage = salvageFolder + "upgrade/";
        String abilitySalvage = salvageFolder + "ability/";
        String defenseSalvage = salvageFolder + "defense/";
        String compatSalvage = salvageFolder + "compat/";
        String soulSalvage = salvageFolder + "soul/";
        ModifierRecipeBuilder.modifier(DreamtinkerModifers.realsweep)
                             .setTools(Ingredient.of(DreamtinkerItems.masu.get()))
                             .addInput(Items.ECHO_SHARD)
                             .addInput(Items.ECHO_SHARD)
                             .setMaxLevel(2)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifers.realsweep, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifers.realsweep, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifers.strong_explode)
                             .setTools(Ingredient.of(DreamtinkerItems.masu.get()))
                             .addInput(TinkerGadgets.efln)
                             .addInput(Tags.Items.GUNPOWDER)
                             .setMaxLevel(3)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifers.strong_explode, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifers.strong_explode, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifers.mei)
                             .setTools(TinkerTags.Items.MELEE)
                             .addInput(Items.POPPY).addInput(Items.POPPY)
                             .addInput(Items.CHAIN).addInput(Items.CHAIN)
                             .save(consumer, prefix(DreamtinkerModifers.mei, slotlessFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifers.ender_slayer)
                             .setTools(TinkerTags.Items.MELEE)
                             .addInput(Tags.Items.OBSIDIAN, 2)
                             .addInput(Items.GHAST_TEAR, 2)
                             .addInput(Items.ENDER_EYE, 2)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifers.ender_slayer, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifers.ender_slayer, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifers.life_looting)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(EnigmaticItems.LORE_INSCRIBER, 1)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifers.life_looting, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifers.life_looting, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifers.weapon_books)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(EnigmaticItems.THE_ACKNOWLEDGMENT, 1)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifers.weapon_books, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifers.weapon_books, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifers.weapon_books)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(EnigmaticItems.THE_TWIST, 1)
                             .setLevelRange(2, 2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifers.weapon_books, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifers.weapon_books, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifers.weapon_books)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(EnigmaticItems.THE_INFINITUM, 1)
                             .setLevelRange(3, 3)
                             .setSlots(SlotType.SOUL, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifers.weapon_books, soulSalvage))
                             .save(consumer, prefix(DreamtinkerModifers.weapon_books, soulFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifers.eldritch_pan)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(EnigmaticItems.ELDRITCH_PAN, 1)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifers.eldritch_pan, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifers.eldritch_pan, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifers.desolation_ring)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(EnigmaticItems.EVIL_ESSENCE, 2)
                             .addInput(Items.HEART_OF_THE_SEA, 2)
                             .addInput(Tags.Items.INGOTS_NETHERITE, 2)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifers.desolation_ring, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifers.desolation_ring, abilityFolder));
    }

    private void addEntityMeltingRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = "smeltery/entity_melting/";
        String headFolder = "smeltery/entity_melting/heads/";

        EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.WARDEN), DreamtinkerFluids.molten_echo_shard.result(FluidValues.GEM_SHARD), 5)
                                  .save(consumer, location(folder + "molten_echo_shard"));
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

