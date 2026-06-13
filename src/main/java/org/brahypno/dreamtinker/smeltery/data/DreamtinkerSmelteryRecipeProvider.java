package org.brahypno.dreamtinker.smeltery.data;

import com.sammy.malum.registry.common.block.BlockRegistry;
import com.sammy.malum.registry.common.item.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.crafting.conditions.AndCondition;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;
import org.brahypno.dreamtinker.common.DreamtinkerEffects;
import org.brahypno.dreamtinker.common.DreamtinkerTagKeys;
import org.brahypno.dreamtinker.fluids.DreamtinkerFluids;
import org.brahypno.dreamtinker.tools.data.material.DreamtinkerMaterialDataProvider;
import org.brahypno.esotericismtinker.common.EsotericismTinkerCommon;
import org.brahypno.esotericismtinker.fluids.EsotericismTinkerFluids;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.recipe.data.ItemNameIngredient;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.fluids.fluids.PotionFluidType;
import slimeknights.tconstruct.library.data.recipe.IByproduct;
import slimeknights.tconstruct.library.data.recipe.ISmelteryRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelBuilder;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Objects;
import java.util.function.Consumer;

public class DreamtinkerSmelteryRecipeProvider implements IConditionBuilder, ISmelteryRecipeHelper {

    String Entity_Melting_folder = "smeltery/entity_melting/";

    String Casting_folder = "smeltery/casting/";

    String Melting_folder = "smeltery/melting/";

    String metalFolder = Melting_folder + "metal/";

    private static Ingredient itemNameIngredient(String modid, String path) {
        return ItemNameIngredient.from(new ResourceLocation(modid, path));
    }

    public static ICondition tagFilled(TagKey<Item> tagKey) {
        return new OrCondition(ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS, new TagFilledCondition<>(tagKey));
    }

    public static ItemStack ironHeart() {
        ItemStack stack = new ItemStack(Items.IRON_BLOCK);

        stack.setHoverName(
                Component.translatable("item.dreamtinker.iron_golem_heart")
                         .withStyle(style -> style.withItalic(false))
        );

        CompoundTag display = stack.getOrCreateTagElement("display");

        ListTag lore = new ListTag();
        lore.add(StringTag.valueOf(Component.Serializer.toJson(Component.translatable("tooltip.dreamtinker.iron_golem_heart")
                                                                        .withStyle(style -> style.withItalic(false).withColor(ChatFormatting.GRAY)))));

        display.put("Lore", lore);

        return stack;
    }

    public static SmelteryRecipeBuilder.CommonRecipe[] armorBySuffix(String modid) {
        return new SmelteryRecipeBuilder.CommonRecipe[]{
                new SmelteryRecipeBuilder.ToolItemMelting(5, modid, "helmet"),
                new SmelteryRecipeBuilder.ToolItemMelting(8, modid, "chestplate"),
                new SmelteryRecipeBuilder.ToolItemMelting(7, modid, "leggings"),
                new SmelteryRecipeBuilder.ToolItemMelting(4, modid, "boots"),
        };
    }

    public static SmelteryRecipeBuilder.CommonRecipe[] armorBySuffix(String modid, IByproduct... byproducts) {
        if (byproducts == null || byproducts.length == 0){
            return armorBySuffix(modid);
        }

        return new SmelteryRecipeBuilder.CommonRecipe[]{
                new ToolItemMeltingWithByproduct(5, modid, "helmet", byproducts),
                new ToolItemMeltingWithByproduct(8, modid, "chestplate", byproducts),
                new ToolItemMeltingWithByproduct(7, modid, "leggings", byproducts),
                new ToolItemMeltingWithByproduct(4, modid, "boots", byproducts),
        };
    }

    public static SmelteryRecipeBuilder.CommonRecipe[] ToolsBySuffix(String modid) {
        return new SmelteryRecipeBuilder.CommonRecipe[]{
                new SmelteryRecipeBuilder.ToolItemMelting(2, modid, "sword"),
                new SmelteryRecipeBuilder.ToolItemMelting(3, modid, "pickaxe"),
                new SmelteryRecipeBuilder.ToolItemMelting(3, modid, "axe"),
                new SmelteryRecipeBuilder.ToolItemMelting(1, modid, "shovel"),
                new SmelteryRecipeBuilder.ToolItemMelting(2, modid, "hoe")
        };
    }

    public void addMeltingRecipes(Consumer<FinishedRecipe> consumer) {
        MeltingFuelBuilder.fuel(DreamtinkerFluids.molten_lupi_antimony.ingredient(FluidValues.SIP), 666, 6666)
                          .save(consumer, location(Melting_folder + "fuel/molten_lupi_antimony"));
        MeltingFuelBuilder.fuel(DreamtinkerFluids.reversed_shadow.ingredient(FluidValues.SIP), 180, 3600)
                          .save(consumer, location(Melting_folder + "fuel/reversed_shadow"));

        meltCastBlock(DreamtinkerFluids.molten_crying_obsidian.get(), Items.CRYING_OBSIDIAN, FluidValues.GLASS_BLOCK, consumer);

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.crying_obsidian_plane), DreamtinkerFluids.molten_crying_obsidian, FluidValues.GLASS_PANE,
                                     1.5f)
                            .save(consumer, location(Melting_folder + "crying_obsidian/plane"));

        meltCast(DreamtinkerFluids.molten_echo_shard.get(), Items.ECHO_SHARD, FluidValues.GEM, consumer);

        meltCast(DreamtinkerFluids.molten_echo_alloy.get(), DreamtinkerCommon.echo_alloy.get(), FluidValues.GEM, consumer);


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

        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.void_pearl.get()), DreamtinkerFluids.molten_void, FluidValues.SLIMEBALL, 0.5f)
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
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerTagKeys.Items.scoleciteOre),
                                     DreamtinkerFluids.molten_scolecite.get(), FluidValues.GEM, 1.0f)
                            .setOre(IMeltingContainer.OreRateType.GEM)
                            .addByproduct(DreamtinkerFluids.liquid_trist.result(FluidValues.NUGGET * 2))
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
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.snake_fang.get()), DreamtinkerFluids.snake_essence, FluidValues.GEM, 0.5f)
                            .setDamagable(FluidValues.GEM_SHARD)
                            .save(consumer, location(Melting_folder + "snake_fang/gem"));
        MeltingRecipeBuilder.melting(Ingredient.of(DreamtinkerCommon.eden_fruit.get()), DreamtinkerFluids.unmelting_teardrop, FluidValues.NUGGET, 0.5f)
                            .save(consumer, location(Melting_folder + "eden_fruit"));

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

        fluid(consumer, "arcane_gold", DreamtinkerFluids.molten_arcane_gold).optional()
                                                                            .baseUnit(FluidValues.INGOT).damageUnit(FluidValues.NUGGET)
                                                                            .metal().dust().plate().gear().coin().sheetmetal().geore().oreberry();

        MeltingRecipeBuilder.melting(StrictNBTIngredient.of(ironHeart()),
                                     DreamtinkerFluids.molten_iron_heart, FluidValues.INGOT, 0.5f)
                            .save(consumer, location(Melting_folder + "iron_heart"));

    }

    public void addCastingRecipes(Consumer<FinishedRecipe> consumer) {
        // Pure Fluid Recipes
        Consumer<FinishedRecipe> wrapped;
        ItemCastingRecipeBuilder.tableRecipe(EsotericismTinkerCommon.hypnagogic_transmute.get())
                                .setFluid(DreamtinkerFluids.molten_nigrescence_antimony.getLocalTag(), FluidValues.GEM)
                                .setCoolingTime(100)
                                .setCast(Items.BOOK, true)
                                .save(consumer, location(Casting_folder + "hypnagogic_transmute"));

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
                                .save(consumer, location(Casting_folder + "void_pearl/slime"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.twist_obsidian_pane.get())
                                .setFluidAndTime(DreamtinkerFluids.liquid_trist, FluidValues.NUGGET * 3)
                                .setCast(DreamtinkerCommon.crying_obsidian_plane.get(), true)
                                .save(consumer, location(Casting_folder + "twist_obsidian/pane"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.unborn_egg.get())
                                .setCast(Tags.Items.EGGS, true)
                                .setFluid(FluidIngredient.of(DreamtinkerFluids.molten_nigrescence_antimony.getTag(), FluidValues.GEM * 3))
                                .setCoolingTime(10)
                                .save(consumer, location(Casting_folder + "unborn_egg"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.UnbornTurtleEgg.get())
                                .setCast(Items.TURTLE_EGG, true)
                                .setFluid(FluidIngredient.of(DreamtinkerFluids.molten_nigrescence_antimony.getTag(), FluidValues.GEM * 3))
                                .setCoolingTime(10)
                                .save(consumer, location(Casting_folder + "unborn_turtle_egg"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.UnbornSnifferEgg.get())
                                .setCast(Items.SNIFFER_EGG, true)
                                .setFluid(FluidIngredient.of(DreamtinkerFluids.molten_nigrescence_antimony.getTag(), FluidValues.GEM * 3))
                                .setCoolingTime(10)
                                .save(consumer, location(Casting_folder + "unborn_sniffer_egg"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.UnbornDragonEgg.get())
                                .setCast(Items.DRAGON_EGG, true)
                                .setFluid(FluidIngredient.of(DreamtinkerFluids.molten_albedo_stibium.getTag(), FluidValues.GEM * 3))
                                .setCoolingTime(10)
                                .save(consumer, location(Casting_folder + "unborn_dragon_egg"));

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
                                .setFluidAndTime(DreamtinkerFluids.molten_orichalcum, FluidValues.METAL_BLOCK)
                                .setCast(Items.NETHER_STAR, true)
                                .save(consumer, location(Casting_folder + "desire_gem"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.shiningFlint.get())
                                .setFluidAndTime(TinkerFluids.moltenGlass, FluidValues.GLASS_BLOCK)
                                .setCast(Items.FLINT, true)
                                .save(consumer, location(Casting_folder + "shining_flint"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.eden_fruit.get())
                                .setFluidAndTime(DreamtinkerFluids.snake_essence, FluidValues.GEM)
                                .setCast(Items.ENCHANTED_GOLDEN_APPLE, true)
                                .save(consumer, location(Casting_folder + "eden_fruit"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.deep_prismarine_shard.get())
                                .setFluidAndTime(TinkerFluids.skySlime, FluidValues.SLIME_BLOCK)
                                .setCast(Tags.Items.DUSTS_PRISMARINE, true)
                                .save(consumer, location(Casting_folder + "deep_prismarine_shard"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.fifth_stone.get())
                                .setFluidAndTime(TinkerFluids.searedStone, FluidValues.BRICK)
                                .setCast(Items.FLINT, true)
                                .save(consumer, location(Casting_folder + "fifth_stone"));
        ItemCastingRecipeBuilder.basinRecipe(Items.BUDDING_AMETHYST)
                                .setFluidAndTime(DreamtinkerFluids.molten_ascending_antimony, FluidValues.GEM)
                                .setCast(Items.AMETHYST_BLOCK, true)
                                .save(consumer, location(Casting_folder + "budding/amethyst"));
        ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.earthGeode.getBudding())
                                .setFluidAndTime(DreamtinkerFluids.molten_ascending_antimony, FluidValues.GEM)
                                .setCast(TinkerWorld.earthGeode.getBlock(), true)
                                .save(consumer, location(Casting_folder + "budding/earth"));
        ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.skyGeode.getBudding())
                                .setFluidAndTime(DreamtinkerFluids.molten_ascending_antimony, FluidValues.GEM)
                                .setCast(TinkerWorld.skyGeode.getBlock(), true)
                                .save(consumer, location(Casting_folder + "budding/sky"));
        ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.ichorGeode.getBudding())
                                .setFluidAndTime(DreamtinkerFluids.molten_ascending_antimony, FluidValues.GEM)
                                .setCast(TinkerWorld.ichorGeode.getBlock(), true)
                                .save(consumer, location(Casting_folder + "budding/ichor"));
        ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.enderGeode.getBudding())
                                .setFluidAndTime(DreamtinkerFluids.molten_ascending_antimony, FluidValues.GEM)
                                .setCast(TinkerWorld.enderGeode.getBlock(), true)
                                .save(consumer, location(Casting_folder + "budding/end"));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerCommon.spiral_spin.get())
                                .setFluidAndTime(TinkerFluids.moltenSteel, FluidValues.INGOT)
                                .setCast(Items.NAUTILUS_SHELL, true)
                                .save(consumer, location(Casting_folder + "spiral_spin"));
        ItemCastingRecipeBuilder.tableRecipe(Items.MOURNER_POTTERY_SHERD)
                                .setFluidAndTime(DreamtinkerFluids.liquid_trist, FluidValues.INGOT)
                                .setCast(DreamtinkerTagKeys.Items.modTag("decorated_pot_sherds"), true)
                                .save(consumer, location(Casting_folder + "mourner_pottery"));

        wrapped = withCondition(consumer, tagFilled(DreamtinkerTagKeys.Items.CursedDroplet));
        ItemCastingRecipeBuilder.tableRecipe(DreamtinkerTagKeys.Items.CursedDroplet)
                                .setFluidAndTime(DreamtinkerFluids.liquid_trist, FluidValues.LARGE_GEM_BLOCK)
                                .setCast(TinkerSmeltery.gemCast, true)
                                .save(wrapped, location(Casting_folder + "cursed_droplet"));

    }

    public void addAlloyRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = "smeltery/alloy/";
        Consumer<FinishedRecipe> wrapped;
        AlloyRecipeBuilder.alloy(FluidOutput.fromFluid(DreamtinkerFluids.molten_albedo_stibium.get(), FluidValues.GEM), 1500)
                          .addCatalyst(FluidIngredient.of(DreamtinkerFluids.molten_lupi_antimony.getTag(), FluidValues.INGOT))
                          .addInput(TinkerTags.Fluids.METAL_TOOLTIPS, FluidValues.INGOT)
                          .save(consumer, prefix(DreamtinkerFluids.molten_albedo_stibium, folder));
        AlloyRecipeBuilder.alloy(FluidOutput.fromFluid(DreamtinkerFluids.liquid_smoky_antimony.get(), FluidValues.INGOT * 2), 3600)
                          .addInput(DreamtinkerFluids.molten_ascending_antimony.getTag(), FluidValues.INGOT)
                          .addInput(DreamtinkerFluids.liquid_pure_soul.getTag(), FluidValues.GLASS_BLOCK)
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
                          .addInput(EsotericismTinkerFluids.blood_soul.getTag(), FluidValues.GLASS_BLOCK * 2)
                          .save(consumer, prefix(DreamtinkerFluids.molten_soul_steel, folder));
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.molten_bee_gem, FluidValues.GEM * 2)
                          .addInput(DreamtinkerFluids.rainbow_honey.getTag(), FluidValues.BOTTLE)
                          .addInput(TinkerFluids.meatSoup.getTag(), FluidValues.BOWL * 2)
                          .addInput(TinkerFluids.moltenEmerald.getTag(), FluidValues.GEM)
                          .save(consumer, prefix(DreamtinkerFluids.molten_bee_gem, folder));
        AlloyRecipeBuilder.alloy(PotionFluidType.potionResult(DreamtinkerEffects.TemptationPotion.get(), FluidValues.BOTTLE), 100)
                          .addCatalyst(FluidIngredient.of(DreamtinkerFluids.molten_desire.getTag(), FluidValues.GEM))
                          .addInput(TinkerTags.Fluids.LARGE_GEM_TOOLTIPS, FluidValues.GEM)
                          .save(consumer, location(folder + "temptation_potion"));
        AlloyRecipeBuilder.alloy(PotionFluidType.potionResult(DreamtinkerEffects.SoulFirePotion.get(), FluidValues.BOTTLE), 100)
                          .addCatalyst(FluidIngredient.of(EsotericismTinkerFluids.molten_ender_ash.getTag(), FluidValues.BRICK))
                          .addInput(TinkerTags.Fluids.CLAY_TOOLTIPS, FluidValues.BRICK)
                          .save(consumer, location(folder + "soul_fire_potion"));
        wrapped = withCondition(consumer, tagFilled(Dreamtinker.forgeItemTag("ingots/silver")));
        AlloyRecipeBuilder.alloy(TinkerFluids.moltenElectrum, FluidValues.INGOT * 2)
                          .addInput(TinkerFluids.moltenGold.ingredient(FluidValues.INGOT))
                          .addInput(DreamtinkerFluids.liquid_amber.ingredient(FluidValues.GEM))
                          .save(wrapped, prefix(TinkerFluids.moltenElectrum, folder));
        AlloyRecipeBuilder.alloy(DreamtinkerFluids.molten_atonement_silver, FluidValues.INGOT)
                          .addInput(TinkerFluids.moltenSilver.ingredient(FluidValues.INGOT))
                          .addInput(DreamtinkerFluids.liquid_amber.ingredient(FluidValues.GEM))
                          .save(wrapped, prefix(DreamtinkerFluids.molten_atonement_silver, folder));
        wrapped =
                withCondition(consumer,
                              new AndCondition(tagFilled(DreamtinkerTagKeys.Items.arcaneGoldIngot), tagFilled(Dreamtinker.forgeItemTag("gems/cinnabar"))));
        AlloyRecipeBuilder.alloy(FluidOutput.fromFluid(DreamtinkerFluids.molten_transmutation_gold.get(), FluidValues.INGOT * 2), 1200)
                          .addInput(DreamtinkerTagKeys.Fluids.molten_arcane_gold, FluidValues.INGOT)
                          .addInput(DreamtinkerFluids.mercury.ingredient(FluidValues.GEM))
                          .save(wrapped, prefix(DreamtinkerFluids.molten_transmutation_gold, folder));
    }

    public void addCompactMeltingCastingRecipes(Consumer<FinishedRecipe> consumer) {
        addCompactELMeltingCastingRecipes(consumer);
        addCompactMalumMeltingCastingRecipes(consumer);
        addCompactEidolonMeltingCastingRecipes(consumer);
        addCompactBICMeltingCastingRecipes(consumer);
        addCompactLegendaryMonstersMeltingCastingRecipes(consumer);
        addCompactUGMeltingCastingRecipes(consumer);
        addCompactOCMeltingCastingRecipes(consumer);

    }

    private void addCompactEidolonMeltingCastingRecipes(Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("eidolon"));
        MeltingRecipeBuilder.melting(itemNameIngredient("eidolon", "gold_inlay"),
                                     DreamtinkerFluids.molten_arcane_gold.get(), FluidValues.INGOT * 2, 0.05f)
                            .save(wrapped, location(Melting_folder + "arcane_gold/inlay"));
        MeltingRecipeBuilder.melting(itemNameIngredient("eidolon", "pewter_inlay"),
                                     TinkerFluids.moltenPewter, FluidValues.INGOT * 2, 0.05f)
                            .save(wrapped, location(Melting_folder + "pewter/inlay"));
        MeltingRecipeBuilder.melting(itemNameIngredient("eidolon", "pewter_blend"),
                                     TinkerFluids.moltenPewter, FluidValues.INGOT, 0.05f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "pewter/blend"));
    }

    private void addCompactELMeltingCastingRecipes(Consumer<FinishedRecipe> consumer) {
        String el = "enigmaticlegacy";
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded(el));
        metalBySuffix(el, "etherium", DreamtinkerFluids.unstable_liquid_aether.get(), wrapped);
        MeltingRecipeBuilder.melting(itemNameIngredient(el, "etherium_ore"), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT, 0.5F)
                            .addByproduct(DreamtinkerFluids.reversed_shadow.result(30))
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "etherium/ore"));
        //I am sure it's not the best way, but who cares
        int[] etherium_damage = {FluidValues.NUGGET, FluidValues.SLIME_DROP};
        MeltingRecipeBuilder.melting(itemNameIngredient(el, "etherium_axe"), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 4, 0.5f)
                            .setDamagable(etherium_damage)
                            .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL * 2))
                            .save(wrapped, location(Melting_folder + "etherium/axe"));
        MeltingRecipeBuilder.melting(itemNameIngredient(el, "etherium_sword"), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 2, 0.5f)
                            .setDamagable(FluidValues.NUGGET, FluidValues.SLIME_DROP, FluidValues.GEM_SHARD)
                            .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL))
                            .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 2))
                            .save(wrapped, location(Melting_folder + "etherium/sword"));
        MeltingRecipeBuilder.melting(itemNameIngredient(el, "etherium_scythe"), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 2, 0.5f)
                            .setDamagable(etherium_damage)
                            .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL * 2))
                            .save(wrapped, location(Melting_folder + "etherium/scythe"));
        MeltingRecipeBuilder.melting(itemNameIngredient(el, "etherium_pickaxe"), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT * 3, 0.5f)
                            .setDamagable(etherium_damage)
                            .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL * 2))
                            .save(wrapped, location(Melting_folder + "etherium/pickaxe"));
        MeltingRecipeBuilder.melting(itemNameIngredient(el, "etherium_shovel"), DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT, 0.5f)
                            .setDamagable(etherium_damage)
                            .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL * 2))
                            .save(wrapped, location(Melting_folder + "etherium/shovel"));
        fluid(consumer, "etherium", DreamtinkerFluids.unstable_liquid_aether)
                .optional()
                .baseUnit(FluidValues.INGOT).damageUnit(FluidValues.NUGGET)
                .common(armorBySuffix(el));

        meltByName(el, "evil_ingot", DreamtinkerFluids.molten_evil.get(), FluidValues.INGOT, wrapped);

        meltByName(el, "evil_essence", DreamtinkerFluids.molten_nefariousness.get(), FluidValues.GEM, wrapped);

        meltByName(el, "soul_crystal", DreamtinkerFluids.liquid_pure_soul.get(), FluidValues.GEM, wrapped);
        meltCast(DreamtinkerFluids.molten_soul_aether.get(), DreamtinkerCommon.soul_etherium.get(), FluidValues.INGOT, wrapped);
        // Moved to src/main/resources so data generation does not directly require Enigmatic Legacy item instances.
        //        ItemCastingRecipeBuilder.tableRecipe(ForgeRegistries.ITEMS.getValue(new ResourceLocation(el, "void_pearl")))
        //                                .setCoolingTime(2000, 10)
        //                                .setCast(DreamtinkerCommon.void_pearl.get(), true)
        //                                .setFluid(FluidIngredient.of(new FluidStack(DreamtinkerFluids.molten_ascending_antimony.get(), FluidValues.METAL_BLOCK)))
        //                                .save(wrapped, location(Casting_folder + "void_pearl/ascending"));
        //        ItemCastingRecipeBuilder.tableRecipe(ForgeRegistries.ITEMS.getValue(new ResourceLocation(el, "ocean_stone")))
        //                                .setCoolingTime(2000, 10)
        //                                .setCast(Items.HEART_OF_THE_SEA, true)
        //                                .setFluid(FluidIngredient.of(new FluidStack(DreamtinkerFluids.molten_ascending_antimony.get(), FluidValues.METAL_BLOCK)))
        //                                .save(wrapped, location(Casting_folder + "ocean_stone/ascending"));

    }

    private void addCompactMalumMeltingCastingRecipes(Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("malum"));
        MeltingRecipeBuilder.melting(Ingredient.of(ItemRegistry.SOUL_STAINED_STEEL_PLATING.get()), DreamtinkerFluids.molten_soul_stained_steel,
                                     65,//FluidValues.NUGGET * 6.5,
                                     2.0f)
                            .save(wrapped, location(Melting_folder + "soul_stained_steel/plating"));
        MeltingRecipeBuilder.melting(Ingredient.of(ItemRegistry.SOUL_STAINED_STEEL_HELMET.get(), ItemRegistry.SOUL_STAINED_STEEL_CHESTPLATE.get(),
                                                   ItemRegistry.SOUL_STAINED_STEEL_LEGGINGS.get(), ItemRegistry.SOUL_STAINED_STEEL_BOOTS.get()),
                                     DreamtinkerFluids.molten_soul_stained_steel, 6 * 65, 2.0f)
                            .save(wrapped, location(Melting_folder + "soul_stained_steel/armors"));
        meltCast(DreamtinkerFluids.molten_soul_stained_steel.get(), ItemRegistry.SOUL_STAINED_STEEL_INGOT.get(), FluidValues.INGOT, wrapped);
        meltCast(DreamtinkerFluids.molten_soul_stained_steel.get(), ItemRegistry.SOUL_STAINED_STEEL_NUGGET.get(), FluidValues.NUGGET, wrapped);
        meltCastBlock(DreamtinkerFluids.molten_soul_stained_steel.get(), BlockRegistry.BLOCK_OF_SOUL_STAINED_STEEL.get(), FluidValues.METAL_BLOCK, wrapped);

        meltCast(DreamtinkerFluids.molten_malignant_pewter.get(), ItemRegistry.MALIGNANT_PEWTER_INGOT.get(), FluidValues.INGOT, wrapped);
        meltCast(DreamtinkerFluids.molten_malignant_pewter.get(), ItemRegistry.MALIGNANT_PEWTER_NUGGET.get(), FluidValues.NUGGET, wrapped);
        meltCastBlock(DreamtinkerFluids.molten_malignant_pewter.get(), BlockRegistry.BLOCK_OF_MALIGNANT_PEWTER.get(), FluidValues.METAL_BLOCK, wrapped);
        MeltingRecipeBuilder.melting(Ingredient.of(ItemRegistry.MALIGNANT_PEWTER_PLATING.get()), DreamtinkerFluids.molten_malignant_pewter, 65, 2.0f)
                            .save(wrapped, location(Melting_folder + "malignant_pewter/plating"));

        meltCast(DreamtinkerFluids.molten_malignant_gluttony.get(), DreamtinkerCommon.malignant_gluttony.get(), FluidValues.INGOT, wrapped);
        ItemCastingRecipeBuilder.tableRecipe(ItemRegistry.CONCENTRATED_GLUTTONY.get())
                                .setCast(Items.GLASS_BOTTLE, true)
                                .setFluidAndTime(DreamtinkerFluids.liquid_concentrated_gluttony, FluidValues.BOTTLE)
                                .save(wrapped, location(Casting_folder + "filling/" +
                                                        Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ItemRegistry.CONCENTRATED_GLUTTONY.get()))
                                                               .getPath()));
        fluid(consumer, "soul_stained_steel", DreamtinkerFluids.molten_soul_stained_steel)
                .optional()
                .baseUnit(FluidValues.INGOT).damageUnit(FluidValues.NUGGET)
                .common(ToolsBySuffix("malum"));
    }

    private void addCompactBICMeltingCastingRecipes(Consumer<FinishedRecipe> consumer) {
        String BIC = "born_in_chaos_v1";
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded(BIC));
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(BIC, "dark_metal_deposit")),
                                     DreamtinkerFluids.molten_dark_metal.get(), FluidValues.INGOT, 0.05f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "dark_metal/ore"));
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(BIC, "pieceofdarkmetal")),
                                     DreamtinkerFluids.molten_dark_metal.get(), FluidValues.NUGGET, 0.05f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "dark_metal/piece"));
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(BIC, "pileof_dark_metal")),
                                     DreamtinkerFluids.molten_dark_metal.get(), FluidValues.INGOT, 0.05f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "dark_metal/pile"));
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(BIC, "armor_plate_from_dark_metal")),
                                     DreamtinkerFluids.molten_dark_metal.get(), FluidValues.INGOT * 5, 0.05f)
                            .save(wrapped, location(Melting_folder + "dark_metal/plate"));
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(BIC, "dark_grid")),
                                     DreamtinkerFluids.molten_dark_metal.get(), FluidValues.INGOT / 4, 0.05f)
                            .save(wrapped, location(Melting_folder + "dark_metal/grid"));
        MeltingRecipeBuilder.melting(CompoundIngredient.of(ItemNameIngredient.from(new ResourceLocation(BIC, "infected_diamond_ore")),
                                                           ItemNameIngredient.from(new ResourceLocation(BIC, "infected_deepslate_diamond_ore"))),
                                     TinkerFluids.moltenDiamond, FluidValues.GEM, 0.05f)
                            .setOre(IMeltingContainer.OreRateType.GEM)
                            .addByproduct(TinkerFluids.moltenDebris.result(FluidValues.GEM / 2))
                            .addByproduct(DreamtinkerFluids.molten_dark_metal.result(FluidValues.INGOT / 2))
                            .save(wrapped, location(Melting_folder + "diamond/infected"));
        fluid(consumer, "dark_metal", DreamtinkerFluids.molten_dark_metal)
                .optional()
                .baseUnit(FluidValues.INGOT).damageUnit(FluidValues.NUGGET).metal();
        int[] darkMetalArmorSizes = {FluidValues.NUGGET, FluidValues.SLIME_DROP, FluidValues.GEM_SHARD};
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(BIC, "dark_metal_armor_helmet")), DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.INGOT * 5)
                            .setDamagable(darkMetalArmorSizes)
                            .addByproduct(TinkerFluids.moltenNetherite.result(FluidValues.INGOT))
                            .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 5))
                            .save(wrapped, location(metalFolder + "dark_metal/helmet"));
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(BIC, "dark_metal_armor_chestplate")),
                                     DreamtinkerFluids.molten_dark_metal.get(), FluidValues.INGOT * 5)
                            .setDamagable(darkMetalArmorSizes)
                            .addByproduct(TinkerFluids.moltenNetherite.result(FluidValues.INGOT))
                            .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 8))
                            .save(wrapped, location(metalFolder + "dark_metal/chestplate"));
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(BIC, "dark_metal_armor_leggings")), DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.INGOT * 5)
                            .setDamagable(darkMetalArmorSizes)
                            .addByproduct(TinkerFluids.moltenNetherite.result(FluidValues.INGOT))
                            .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 7))
                            .save(wrapped, location(metalFolder + "dark_metal/leggings"));
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(BIC, "dark_metal_armor_boots")), DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.INGOT * 5)
                            .setDamagable(darkMetalArmorSizes)
                            .addByproduct(TinkerFluids.moltenNetherite.result(FluidValues.INGOT))
                            .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 4))
                            .save(wrapped, location(metalFolder + "dark_metal/boots"));
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(BIC, "darkwarblade")), DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.METAL_BLOCK * 2 + FluidValues.NUGGET)
                            .setDamagable(FluidValues.INGOT)
                            .save(wrapped, location(metalFolder + "dark_metal/warblade"));
        MeltingRecipeBuilder.melting(CompoundIngredient.of(ItemNameIngredient.from(new ResourceLocation(BIC, "dark_ritual_dagger")),
                                                           ItemNameIngredient.from(new ResourceLocation(BIC, "spiritual_sword")),
                                                           ItemNameIngredient.from(new ResourceLocation(BIC, "bonescaller_staff")),
                                                           ItemNameIngredient.from(new ResourceLocation(BIC, "shell_mace"))),
                                     DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.NUGGET * 3)
                            .setDamagable(FluidValues.NUGGET / 2)
                            .save(wrapped, location(metalFolder + "dark_metal/dagger"));
        MeltingRecipeBuilder.melting(CompoundIngredient.of(ItemNameIngredient.from(new ResourceLocation(BIC, "sharpened_dark_metal_sword")),
                                                           ItemNameIngredient.from(new ResourceLocation(BIC, "spider_bite_sword"))),
                                     DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.INGOT * 2 + FluidValues.NUGGET)
                            .setDamagable(FluidValues.NUGGET)
                            .save(wrapped, location(metalFolder + "dark_metal/sword"));
        MeltingRecipeBuilder.melting(CompoundIngredient.of(ItemNameIngredient.from(new ResourceLocation(BIC, "frostbitten_blade")),
                                                           ItemNameIngredient.from(new ResourceLocation(BIC, "intoxicating_dagger"))),
                                     DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.INGOT + FluidValues.NUGGET)
                            .setDamagable(FluidValues.NUGGET)
                            .save(wrapped, location(metalFolder + "dark_metal/frostbitten_blade"));
        MeltingRecipeBuilder.melting(CompoundIngredient.of(ItemNameIngredient.from(new ResourceLocation(BIC, "soul_cutlass")),
                                                           ItemNameIngredient.from(new ResourceLocation(BIC, "nightmare_scythe"))),
                                     DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.INGOT + FluidValues.NUGGET * 2)
                            .setDamagable(FluidValues.NUGGET)
                            .save(wrapped, location(metalFolder + "dark_metal/soul_cutlass"));
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(BIC, "great_reaper_axe")), DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.INGOT * 4 + FluidValues.NUGGET * 2)
                            .setDamagable(FluidValues.NUGGET)
                            .save(wrapped, location(metalFolder + "dark_metal/greater_axe"));
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(BIC, "skullbreaker_hammer")), DreamtinkerFluids.molten_dark_metal.get(),
                                     FluidValues.METAL_BLOCK + FluidValues.NUGGET * 2)
                            .setDamagable(FluidValues.INGOT)
                            .save(wrapped, location(metalFolder + "dark_metal/skull_breaker_hammer"));
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(BIC, "diamond_termite_shard")), TinkerFluids.moltenDiamond,
                                     FluidValues.NUGGET)
                            .save(wrapped, location(metalFolder + "diamond/melting_termite"));

        EntityMeltingRecipeBuilder.melting(
                                          EntityIngredient.of(DreamtinkerTagKeys.EntityTypes.CHAOS_ELITE),
                                          DreamtinkerFluids.molten_dark_metal.result(FluidValues.NUGGET), 1)
                                  .save(wrapped, location(Entity_Melting_folder + "molten_dark_metal/elite"));
        EntityMeltingRecipeBuilder.melting(
                                          EntityIngredient.of(DreamtinkerTagKeys.EntityTypes.CHAOS_BOSS),
                                          DreamtinkerFluids.molten_dark_metal.result(FluidValues.NUGGET), 1)
                                  .save(wrapped, location(Entity_Melting_folder + "molten_dark_metal/boss"));
    }

    private void addCompactLegendaryMonstersMeltingCastingRecipes(Consumer<FinishedRecipe> consumer) {
        String legendaryMonsters = "legendary_monsters";
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("legendary_monsters"));
        MeltingRecipeBuilder.melting(itemNameIngredient("legendary_monsters", "soul_great_sword"),
                                     DreamtinkerFluids.molten_soul_steel, FluidValues.INGOT * 6, 0.5f)
                            .setDamagable(FluidValues.NUGGET)
                            .save(wrapped, location(Melting_folder + "soul_steel/soul_great_sword"));
        MeltingRecipeBuilder.melting(itemNameIngredient(legendaryMonsters, "enderitium_ingot"),
                                     DreamtinkerFluids.molten_enderitium, FluidValues.INGOT, 1.0f)
                            .save(wrapped, location(Melting_folder + "enderitium/ingot"));
        MeltingRecipeBuilder.melting(itemNameIngredient(legendaryMonsters, "enderitium_gem"),
                                     DreamtinkerFluids.molten_enderitium, FluidValues.NUGGET, 1.0f)
                            .save(wrapped, location(Melting_folder + "enderitium/gem"));
        MeltingRecipeBuilder.melting(itemNameIngredient(legendaryMonsters, "enderitium_block"),
                                     DreamtinkerFluids.molten_enderitium, FluidValues.METAL_BLOCK, 1.0f)
                            .save(wrapped, location(Melting_folder + "enderitium/block"));
        MeltingRecipeBuilder.melting(itemNameIngredient(legendaryMonsters, "enderitium_ore"),
                                     DreamtinkerFluids.molten_enderitium, FluidValues.NUGGET, 1.5f)
                            .setOre(IMeltingContainer.OreRateType.METAL)
                            .save(wrapped, location(Melting_folder + "enderitium/ore"));
        fluid(wrapped, "enderitium", DreamtinkerFluids.molten_enderitium)
                .baseUnit(FluidValues.INGOT).damageUnit(FluidValues.NUGGET)
                .common(ToolsBySuffix(legendaryMonsters));
    }

    private void addCompactUGMeltingCastingRecipes(Consumer<FinishedRecipe> consumer) {
        String undergarden = "undergarden";
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded(undergarden));

        fluid(consumer, "utherium", DreamtinkerFluids.molten_utherium)
                .optional()
                .baseUnit(FluidValues.INGOT).damageUnit(FluidValues.NUGGET).ore()
                .metal().dust().plate().gear().coin().sheetmetal().geore().oreberry()
                .common(ToolsBySuffix(undergarden))
                .common(armorBySuffix(undergarden));
        fluid(consumer, "forgotten_metal", DreamtinkerFluids.molten_forgotten_metal)
                .optional()
                .baseUnit(FluidValues.INGOT).damageUnit(FluidValues.NUGGET)
                .metal().dust().plate().gear().coin().sheetmetal().geore().oreberry();

        ToolsBySuffix(wrapped, DreamtinkerFluids.molten_forgotten_metal, DreamtinkerFluids.molten_cloggrum, undergarden, "forgotten", 1);
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(undergarden, "forgotten_battleaxe")),
                                     DreamtinkerFluids.molten_forgotten_metal, FluidValues.INGOT, 0.5f)
                            .setDamagable(FluidValues.NUGGET)
                            .addByproduct(DreamtinkerFluids.molten_cloggrum.result(FluidValues.INGOT * 6))
                            .save(wrapped, location(Melting_folder + "forgotten/undergarden_battleaxe"));

        fluid(consumer, "cloggrum", DreamtinkerFluids.molten_cloggrum)
                .optional()
                .baseUnit(FluidValues.INGOT).damageUnit(FluidValues.NUGGET).ore()
                .metal().dust().plate().gear().coin().sheetmetal().geore().oreberry()
                .common(ToolsBySuffix(undergarden))
                .common(armorBySuffix(undergarden))
                .metalMelting(1.0f / 3.0f, "undergarden", "bars", false);
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(undergarden, "cloggrum_shield")),
                                     DreamtinkerFluids.molten_cloggrum, FluidValues.INGOT * 6, 0.5f)
                            .setDamagable(FluidValues.NUGGET)
                            .save(wrapped, location(Melting_folder + "cloggrum/undergarden_shield"));

        fluid(consumer, "froststeel", DreamtinkerFluids.molten_froststeel)
                .optional()
                .baseUnit(FluidValues.INGOT).damageUnit(FluidValues.NUGGET).ore()
                .metal().dust().plate().gear().coin().sheetmetal().geore().oreberry()
                .common(ToolsBySuffix(undergarden))
                .common(armorBySuffix(undergarden));

        fluid(consumer, "regalium", DreamtinkerFluids.molten_regalium)
                .optional()
                .baseUnit(FluidValues.INGOT).damageUnit(FluidValues.NUGGET)
                .metal().dust().plate().gear().coin().sheetmetal().geore().oreberry();
        EntityMeltingRecipeBuilder.melting(EntityIngredient.of(DreamtinkerTagKeys.EntityTypes.ROTSPAWN),
                                           DreamtinkerFluids.molten_utherium.result(FluidValues.NUGGET))
                                  .save(wrapped, location(Entity_Melting_folder + "molten_utherium/entity"));

        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(undergarden, "goo_ball")),
                                     DreamtinkerFluids.gooey_slime, FluidValues.SLIMEBALL, 0.5f)
                            .save(wrapped, location(Melting_folder + "gooey_slime/ball"));
        /*
        SeveringRecipeBuilder.severing(EntityIngredient.of(DreamtinkerTagKeys.EntityTypes.ROTSPAWN),
                                       ForgeRegistries.ITEMS.getValue(new ResourceLocation(undergarden, "utheric_shard")))
                             .save(wrapped, location(serving_folder + "utherium_shard"));
        EntityMeltingRecipeBuilder.melting(EntityIngredient.of(ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(undergarden, "forgotten_guardian"))),
                                           DreamtinkerFluids.molten_forgotten_metal.result(FluidValues.NUGGET))
                                  .save(wrapped, location(Entity_Melting_folder + "molten_forgotten_metal/entity"));
        SeveringRecipeBuilder.severing(EntityIngredient.of(ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(undergarden, "forgotten_guardian"))),
                                       ForgeRegistries.ITEMS.getValue(new ResourceLocation(undergarden, "forgotten_nugget")))
                             .save(wrapped, location(serving_folder + "forgetten_metal"));

        SeveringRecipeBuilder.severing(EntityIngredient.of(ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(undergarden, "scintling"))),
                                       ForgeRegistries.ITEMS.getValue(new ResourceLocation(undergarden, "goo_ball")))
                             .save(wrapped, location(serving_folder + "goo_ball"));
        ItemCastingRecipeBuilder.tableRecipe(ForgeRegistries.ITEMS.getValue(new ResourceLocation(undergarden, "goo_ball")))
                                .setCoolingTime(IMeltingRecipe.getTemperature(DreamtinkerFluids.gooey_slime), FluidValues.SLIMEBALL)
                                .setFluid(FluidIngredient.of(new FluidStack(DreamtinkerFluids.gooey_slime.get(), FluidValues.SLIMEBALL)))
                                .save(wrapped, location(Casting_folder + "gooey_slime"));
        ItemCastingRecipeBuilder.tableRecipe(ForgeRegistries.ITEMS.getValue(new ResourceLocation(undergarden, "cloggrum_bars")))
                                .setCoolingTime(IMeltingRecipe.getTemperature(DreamtinkerFluids.molten_cloggrum), FluidValues.NUGGET * 3)
                                .setFluid(FluidIngredient.of(new FluidStack(DreamtinkerFluids.molten_cloggrum.get(), FluidValues.NUGGET * 3)))
                                .save(wrapped, location(Casting_folder + "cloggrum/bars"));
         */
    }

    private void addCompactOCMeltingCastingRecipes(Consumer<FinishedRecipe> consumer) {
        String occultism = "occultism";

        fluid(consumer, "iesnium", DreamtinkerFluids.molten_iesnium)
                .optional()
                .baseUnit(FluidValues.INGOT).damageUnit(FluidValues.NUGGET).ore()
                .metal().dust().plate().gear().coin().sheetmetal().geore().oreberry()
                .common(ToolsBySuffix(occultism))
                .common(armorBySuffix(occultism));
    }

    private void meltCast(Fluid fluid, ItemLike ingredient, int amount, Consumer<FinishedRecipe> consumer) {
        String key2 =
                FluidValues.GEM == amount ? "gem" : FluidValues.INGOT == amount ? "ingot" : "nugget";
        MeltingRecipeBuilder.melting(Ingredient.of(ingredient), fluid, amount, 0.5f)
                            .save(consumer, location(
                                    Melting_folder + Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(ingredient.asItem())).getPath() + "/" + key2));
        cast(fluid, ingredient, amount, consumer);
    }

    private void meltCastBlock(Fluid fluid, ItemLike ingredient, int amount, Consumer<FinishedRecipe> consumer) {

        MeltingRecipeBuilder.melting(Ingredient.of(ingredient), fluid, amount, 0.5f)
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
                FluidValues.GEM == amount ? TinkerSmeltery.gemCast :
                FluidValues.INGOT == amount || FluidValues.BRICK == amount ? TinkerSmeltery.ingotCast : TinkerSmeltery.nuggetCast;
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

    public SmelteryRecipeBuilder metal(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid) {
        return molten(consumer, fluid).castingFolder("smeltery/casting/metal").meltingFolder("smeltery/melting/metal");
    }

    private void meltByName(String modid, String path, Fluid fluid, int amount, Consumer<FinishedRecipe> consumer) {
        if (amount == FluidValues.INGOT * 9 || amount == FluidValues.GEM * 9 || amount == FluidValues.GEM * 4){
            MeltingRecipeBuilder.melting(itemNameIngredient(modid, path), fluid, amount, 0.5f)
                                .save(consumer, location(Melting_folder + path + "/block"));
        }else {
            MeltingRecipeBuilder.melting(itemNameIngredient(modid, path), fluid, amount, (float) Math.sqrt((double) amount / FluidValues.NUGGET))
                                .save(consumer, location(Melting_folder + path + "/melting"));
        }
    }

    private void metalBySuffix(String modid, String key, Fluid fluid, Consumer<FinishedRecipe> consumer) {
        meltByName(modid, key + "_block", fluid, FluidValues.INGOT * 9, consumer);
        meltByName(modid, key + "_ingot", fluid, FluidValues.INGOT, consumer);
        meltByName(modid, key + "_nugget", fluid, FluidValues.NUGGET, consumer);
    }

    private void gemBySuffix(String modid, String key, Fluid fluid, int storageSize, Consumer<FinishedRecipe> consumer) {
        meltByName(modid, key + "_block", fluid, FluidValues.GEM * storageSize, consumer);
        meltByName(modid, key, fluid, FluidValues.GEM, consumer);
    }

    private void itemMeltingWithProduct(Consumer<FinishedRecipe> consumer, FlowingFluidObject<ForgeFlowingFluid> main, FlowingFluidObject<ForgeFlowingFluid> byproduct, String modid, String prefix, String suffix, int main_scale, int byproduct_scale) {
        MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation(modid, prefix + "_" + suffix)),
                                     main, FluidValues.INGOT * main_scale, 0.5f)
                            .setDamagable(FluidValues.NUGGET)
                            .addByproduct(byproduct.result(FluidValues.INGOT * byproduct_scale))
                            .save(consumer, location(Melting_folder + prefix + "/" + modid + "_" + suffix));
    }

    public void ToolsBySuffix(
            Consumer<FinishedRecipe> consumer, FlowingFluidObject<ForgeFlowingFluid> main, FlowingFluidObject<ForgeFlowingFluid> byproduct, String modid, String prefix,
            int main_scale) {

        itemMeltingWithProduct(consumer, main, byproduct, modid, prefix, "sword", main_scale, 2);
        itemMeltingWithProduct(consumer, main, byproduct, modid, prefix, "pickaxe", main_scale, 3);
        itemMeltingWithProduct(consumer, main, byproduct, modid, prefix, "axe", main_scale, 3);
        itemMeltingWithProduct(consumer, main, byproduct, modid, prefix, "shovel", main_scale, 1);
        itemMeltingWithProduct(consumer, main, byproduct, modid, prefix, "hoe", main_scale, 2);
    }

    @Override
    public @NotNull String getModId() {
        return Dreamtinker.MODID;
    }

    public record ToolItemMeltingWithByproduct(int cost, String domain, String name, IByproduct... byproducts)
            implements SmelteryRecipeBuilder.CommonRecipe {
        @Override
        public void accept(SmelteryRecipeBuilder builder) {
            builder.unitByproducts(byproducts)
                   .toolItemMelting(cost, domain, name)
                   .unitByproducts(); // 清空，避免影响后面的 recipe
        }
    }


}
