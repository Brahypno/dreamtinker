package org.brahypno.dreamtinker.common.data;

import com.sammy.malum.data.recipe.builder.SpiritInfusionRecipeBuilder;
import com.sammy.malum.data.recipe.builder.VoidFavorRecipeBuilder;
import com.sammy.malum.registry.common.SpiritTypeRegistry;
import com.sammy.malum.registry.common.item.ItemRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;
import org.brahypno.dreamtinker.common.DreamtinkerEffects;
import org.brahypno.dreamtinker.common.DreamtinkerTagKeys;
import org.brahypno.dreamtinker.smeltery.data.DreamtinkerEntityTransmuteRecipeProvider;
import org.brahypno.dreamtinker.smeltery.data.DreamtinkerSmelteryRecipeProvider;
import org.brahypno.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.brahypno.dreamtinker.tools.data.DreamtinkerModifierRecipeProvider;
import org.brahypno.dreamtinker.tools.data.DreamtinkerPartToolBuildingRecipeProvider;
import org.brahypno.dreamtinker.tools.data.material.DreamtinkerMaterialDataProvider;
import org.brahypno.dreamtinker.tools.data.material.DreamtinkerMaterialRecipeProvider;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.data.ICommonRecipeHelper;
import slimeknights.mantle.recipe.data.IRecipeHelper;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipeBuilder;
import slimeknights.tconstruct.tools.TinkerToolParts;

import java.util.Objects;
import java.util.function.Consumer;

public class DreamtinkerRecipeProvider extends RecipeProvider implements IRecipeHelper, ICommonRecipeHelper {

    public DreamtinkerRecipeProvider(PackOutput p_248933_) {
        super(p_248933_);
    }

    String common_folder = "common/";
    String partFolder = "tools/parts/";

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        DreamtinkerSmelteryRecipeProvider smeltery = new DreamtinkerSmelteryRecipeProvider();
        DreamtinkerMaterialRecipeProvider material = new DreamtinkerMaterialRecipeProvider();
        DreamtinkerPartToolBuildingRecipeProvider partToolBuilding = new DreamtinkerPartToolBuildingRecipeProvider();
        DreamtinkerModifierRecipeProvider modifier = new DreamtinkerModifierRecipeProvider();
        DreamtinkerEntityTransmuteRecipeProvider entityTransmute = new DreamtinkerEntityTransmuteRecipeProvider();

        addCraftingRecipes(consumer);
        smeltery.addMeltingRecipes(consumer);
        smeltery.addCastingRecipes(consumer);
        smeltery.addAlloyRecipes(consumer);
        material.addMaterialRecipes(consumer);
        material.addCompactMaterialRecipes(consumer);
        smeltery.addCompactMeltingCastingRecipes(consumer);

        partToolBuilding.addPartRecipes(consumer);
        partToolBuilding.addToolBuildingRecipes(consumer);
        modifier.addModifierRecipes(consumer);
        entityTransmute.addEntityMeltingRecipes(consumer);
        entityTransmute.addTransmuteRecipes(consumer);
    }

    String serving_folder = "tools/severing/";

    public void addCraftingRecipes(Consumer<FinishedRecipe> consumer) {
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

        fake_block_to_ingot(consumer, DreamtinkerMaterialIds.echo_alloy, DreamtinkerCommon.echo_alloy.get());
        fake_block_to_ingot(consumer, DreamtinkerMaterialIds.black_sapphire, DreamtinkerCommon.black_sapphire.get());

        new SpiritInfusionRecipeBuilder(ItemRegistry.THE_VESSEL.get(), 1, new ItemStack(DreamtinkerCommon.malignant_gluttony.get()))
                .addExtraItem(ItemRegistry.NULL_SLATE.get(), 4)
                .addExtraItem(ItemRegistry.MALIGNANT_PEWTER_INGOT.get(), 1)
                .addExtraItem(ItemRegistry.CURSED_SAP.get(), 3)
                .addExtraItem(ItemRegistry.FUSED_CONSCIOUSNESS.get(), 1)
                .addSpirit(SpiritTypeRegistry.WICKED_SPIRIT, 3)
                .addSpirit(SpiritTypeRegistry.SACRED_SPIRIT, 3)
                .addSpirit(SpiritTypeRegistry.ELDRITCH_SPIRIT, 3)
                .addSpirit(SpiritTypeRegistry.INFERNAL_SPIRIT, 3)
                .build(consumer, DreamtinkerCommon.malignant_gluttony.getId().getPath());
        new SpiritInfusionRecipeBuilder(Items.GLASS_BOTTLE, 1,
                                        PotionUtils.setPotion(new ItemStack(Items.POTION, 8), DreamtinkerEffects.ArcanaJuicePotion.get()))
                .addExtraItem(ItemRegistry.NULL_SLATE.get(), 8)
                .addExtraItem(ItemRegistry.MNEMONIC_FRAGMENT.get(), 8)
                .addExtraItem(ItemRegistry.CURSED_SAP.get(), 2)
                .addExtraItem(ItemRegistry.RUNIC_SAP.get(), 2)
                .addSpirit(SpiritTypeRegistry.ARCANE_SPIRIT, 6)
                .addSpirit(SpiritTypeRegistry.AQUEOUS_SPIRIT, 6)
                .addSpirit(SpiritTypeRegistry.SACRED_SPIRIT, 6)
                .addSpirit(SpiritTypeRegistry.ELDRITCH_SPIRIT, 6)
                .build(consumer, "potion/arcana_juice");
        new VoidFavorRecipeBuilder(Ingredient.of(Tags.Items.ENDER_PEARLS), new ItemStack(DreamtinkerCommon.void_pearl.get()))
                .build(consumer, "void_pearl");

        SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.ENDER_DRAGON), DreamtinkerCommon.snake_fang.get())
                             .save(consumer, location(serving_folder + "snake_fang"));


        String el = "enigmaticlegacy";
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded(el));
        fake_block_to_ingot(wrapped, DreamtinkerMaterialIds.soul_etherium, DreamtinkerCommon.soul_etherium.get());


        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("malum"));

        fake_block_to_ingot(wrapped, DreamtinkerMaterialIds.malignant_gluttony, DreamtinkerCommon.malignant_gluttony.get());
    }

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

    public void fake_block_to_ingot(Consumer<FinishedRecipe> consumer, MaterialVariantId id, Item ingotLike) {
        ItemStack fake_block = fake_block(id);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingotLike, 9)
                              .requires(StrictNBTIngredient.of(fake_block))
                              .unlockedBy("has_item", has(TinkerToolParts.fakeStorageBlock))
                              .save(consumer, location(
                                      partFolder + "fake_block_to_ingots/" +
                                      (id.getVariant().isBlank() || id.getVariant().isEmpty() ? id.getId().getPath() : id.getVariant())));
    }

    @Override
    public @NotNull String getModId() {
        return Dreamtinker.MODID;
    }
}
