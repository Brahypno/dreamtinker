package org.brahypno.dreamtinker.tools.data.material;

import com.sammy.malum.core.systems.spirit.MalumSpiritType;
import com.sammy.malum.registry.common.SpiritTypeRegistry;
import com.sammy.malum.registry.common.block.BlockRegistry;
import com.sammy.malum.registry.common.item.ItemRegistry;
import com.sammy.malum.registry.common.item.ItemTagRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;
import org.brahypno.dreamtinker.fluids.DreamtinkerFluids;
import org.brahypno.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.recipe.data.IRecipeHelper;
import slimeknights.mantle.recipe.data.ItemNameIngredient;
import slimeknights.mantle.registration.object.FlowingFluidObject;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipeBuilder;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import java.util.function.Consumer;

public class DreamtinkerMaterialRecipeProvider implements IMaterialRecipeHelper, IRecipeHelper {

    String materials_folder = "tools/materials/";

    String slimeskinFolder = materials_folder + "slimeskin/";

    private static Ingredient itemNameIngredient(String modid, String path) {
        return ItemNameIngredient.from(new ResourceLocation(modid, path));
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

    private static TagKey<Item> forgeTag(String category, String name) {
        return ItemTags.create(new ResourceLocation("forge", category + "/" + name));
    }

    public static ICondition tagFilled(TagKey<Item> tagKey) {
        return new OrCondition(ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS, new TagFilledCondition<>(tagKey));
    }

    public void addMaterialRecipes(Consumer<FinishedRecipe> consumer) {

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
        materialComposite(consumer, DreamtinkerMaterialIds.moonlight_ice, DreamtinkerMaterialIds.cryo_serpent_shift, DreamtinkerFluids.snake_essence,
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

        materialRecipe(consumer, DreamtinkerMaterialIds.whimsyGold, Ingredient.of(DreamtinkerCommon.whimsy_coin.get()), 1, 1,
                       materials_folder + "whimsy_gold");

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

        standardMetalMaterial(consumer, DreamtinkerMaterialIds.orichalcum, DreamtinkerFluids.molten_orichalcum, "orichalcum", materials_folder);

        standardMetalMaterial(consumer, DreamtinkerMaterialIds.cold_iron, DreamtinkerFluids.molten_cold_iron, "cold_iron", materials_folder);

        standardMetalMaterial(consumer, DreamtinkerMaterialIds.shadowSilver, DreamtinkerFluids.molten_shadow_silver, "shadow_silver", materials_folder);

        standardMetalMaterial(consumer, DreamtinkerMaterialIds.ArcaneGold, DreamtinkerFluids.molten_arcane_gold, "arcane_gold", materials_folder);

        standardMetalMaterial(consumer, DreamtinkerMaterialIds.TransmutationGold, DreamtinkerFluids.molten_transmutation_gold, "transmutation_gold",
                              materials_folder);

        materialRecipe(consumer, DreamtinkerMaterialIds.SpikyShard, Ingredient.of(DreamtinkerCommon.deep_prismarine_shard.get()), 1, 1,
                       materials_folder + "spiny_shell");

        materialRecipe(consumer, DreamtinkerMaterialIds.FifthStone, Ingredient.of(DreamtinkerCommon.fifth_stone.get()), 1, 1,
                       materials_folder + "fifth_stone");

        materialRecipe(consumer, DreamtinkerMaterialIds.SpiralSpin, Ingredient.of(DreamtinkerCommon.spiral_spin.get()), 1, 1,
                       materials_folder + "spiral_spin");

        standardMetalMaterial(consumer, DreamtinkerMaterialIds.Utherium, DreamtinkerFluids.molten_utherium, "utherium", materials_folder);
        standardMetalMaterial(consumer, DreamtinkerMaterialIds.forgotten_metal, DreamtinkerFluids.molten_forgotten_metal, "forgotten_metal", materials_folder);
        standardMetalMaterial(consumer, DreamtinkerMaterialIds.Cloggrum, DreamtinkerFluids.molten_cloggrum, "cloggrum", materials_folder);
        standardMetalMaterial(consumer, DreamtinkerMaterialIds.Froststeel, DreamtinkerFluids.molten_froststeel, "froststeel", materials_folder);
        standardMetalMaterial(consumer, DreamtinkerMaterialIds.Regalium, DreamtinkerFluids.molten_regalium, "regalium", materials_folder);
        standardMetalMaterial(consumer, DreamtinkerMaterialIds.Iesnium, DreamtinkerFluids.molten_iesnium, "iesnium", materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.OathGuardPaleSteel, StrictNBTIngredient.of(ironHeart()), 1, 1,
                       materials_folder + "oath_guard");
        materialMeltingCasting(consumer, DreamtinkerMaterialIds.OathGuardPaleSteel, DreamtinkerFluids.molten_iron_heart, FluidValues.INGOT, materials_folder);
        materialMeltingCasting(consumer, DreamtinkerMaterialIds.AtonementSilver, DreamtinkerFluids.molten_atonement_silver, FluidValues.INGOT,
                               materials_folder);
        materialMeltingCasting(consumer, DreamtinkerMaterialIds.deliverance, DreamtinkerFluids.unmelting_teardrop, FluidValues.NUGGET,
                               materials_folder);
        materialRecipe(consumer, DreamtinkerMaterialIds.deliverance, Ingredient.of(DreamtinkerCommon.eden_fruit.get()), 1, 1, materials_folder + "eden_fruit");

    }

    public void addCompactMaterialRecipes(Consumer<FinishedRecipe> consumer) {
        addELMaterialRecipes(consumer);
        addMalumMaterialRecipes(consumer);
        addEidolonMaterialRecipes(consumer);
        addBICMaterialRecipes(consumer);
        addNovaMaterialRecipes(consumer);
        addUGMaterialRecipes(consumer);
        addLegendaryMonstersMaterialRecipes(consumer);

    }

    private void addELMaterialRecipes(Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"));
        materialMeltingCasting(wrapped, DreamtinkerMaterialIds.etherium, DreamtinkerFluids.unstable_liquid_aether, FluidValues.INGOT, materials_folder);
        materialRecipe(wrapped, DreamtinkerMaterialIds.etherium, itemNameIngredient("enigmaticlegacy", "etherium_ingot"), 1, 1,
                       materials_folder + "etherium");
        materialComposite(wrapped, MaterialIds.string, DreamtinkerMaterialIds.etherium, DreamtinkerFluids.unstable_liquid_aether,
                          FluidValues.INGOT, materials_folder);
        materialMeltingCasting(wrapped, DreamtinkerMaterialIds.nefarious, DreamtinkerFluids.molten_evil, FluidValues.INGOT, materials_folder);
        materialRecipe(wrapped, DreamtinkerMaterialIds.nefarious, itemNameIngredient("enigmaticlegacy", "evil_ingot"), 1, 1,
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
        materialRecipe(wrapped, DreamtinkerMaterialIds.malignant_lead, Ingredient.of(ItemRegistry.MALIGNANT_LEAD.get()), 1, 1,
                       materials_folder + "malignant_lead_ingot");
        materialRecipe(wrapped, DreamtinkerMaterialIds.malignant_lead, Ingredient.of(ItemRegistry.BLOCK_OF_MALIGNANT_LEAD.get()), 9, 1,
                       materials_folder + "malignant_lead_block");
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
        materialRecipe(wrapped, DreamtinkerMaterialIds.TatteredCloth, itemNameIngredient("eidolon", "tattered_cloth"), 1, 2,
                       materials_folder + "tattered_cloth");
        materialRecipe(wrapped, DreamtinkerMaterialIds.WickedWeave, itemNameIngredient("eidolon", "wicked_weave"), 1, 2,
                       materials_folder + "wicked_weave");
        materialRecipe(wrapped, DreamtinkerMaterialIds.PaladinBoneTool, itemNameIngredient("eidolon", "imbued_bones"), 1, 1,
                       materials_folder + "paladin_bone_tool");
        materialRecipe(wrapped, DreamtinkerMaterialIds.SoulGem, itemNameIngredient("eidolon", "soul_shard"), 1, 4,
                       materials_folder + "soul_gem");
        materialRecipe(wrapped, DreamtinkerMaterialIds.CrimsonGem, itemNameIngredient("eidolon", "crimson_gem"), 1, 1,
                       materials_folder + "crimson_gem");
        materialRecipe(wrapped, DreamtinkerMaterialIds.ShadowGem, Ingredient.of(Dreamtinker.forgeItemTag("gems/shadow_gem")), 1, 1,
                       materials_folder + "shadow_gem/gem");
        materialRecipe(wrapped, DreamtinkerMaterialIds.ShadowGem, Ingredient.of(Dreamtinker.forgeItemTag("storage_blocks/shadow_gem")), 9, 1,
                       materials_folder + "shadow_gem/block");
    }

    private void addBICMaterialRecipes(Consumer<FinishedRecipe> consumer) {
        String BIC = "born_in_chaos_v1";
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"));
        materialMeltingCasting(wrapped, DreamtinkerMaterialIds.DarkMetal, DreamtinkerFluids.molten_dark_metal, FluidValues.INGOT * 5,
                               materials_folder);
        materialRecipe(wrapped, DreamtinkerMaterialIds.DarkMetal, ItemNameIngredient.from(new ResourceLocation(BIC, "dark_metal_ingot")), 1, 5,
                       materials_folder + "dark_metal/ingot");
        materialRecipe(wrapped, DreamtinkerMaterialIds.DarkMetal, ItemNameIngredient.from(new ResourceLocation(BIC, "armor_plate_from_dark_metal")), 1, 1,
                       materials_folder + "dark_metal/armor_plate");

        materialRecipe(wrapped, DreamtinkerMaterialIds.MonsterSkin, ItemNameIngredient.from(new ResourceLocation(BIC, "monster_skin")), 1, 1,
                       materials_folder + "monster_skin");
        materialComposite(wrapped, MaterialIds.leather, DreamtinkerMaterialIds.MonsterSkin, DreamtinkerFluids.molten_dark_metal, FluidValues.INGOT,
                          slimeskinFolder, "monsterskin");
        materialComposite(wrapped, DreamtinkerMaterialIds.MonsterSkin, MaterialIds.leather, TinkerFluids.venom, FluidValues.SIP, slimeskinFolder,
                          "monsterskin_cleaning");
        materialRecipe(wrapped, DreamtinkerMaterialIds.SpikyShard, ItemNameIngredient.from(new ResourceLocation(BIC, "spiny_shell")), 1, 1,
                       materials_folder + "spiny_shell_bic");
        materialRecipe(wrapped, DreamtinkerMaterialIds.LifeStealerBone, ItemNameIngredient.from(new ResourceLocation(BIC, "lifestealer_bone")), 1, 1,
                       materials_folder + "life_stealer");
        materialRecipe(wrapped, DreamtinkerMaterialIds.KrampusHorn, ItemNameIngredient.from(new ResourceLocation(BIC, "krampus_horn")), 1, 1,
                       materials_folder + "krampus_horn");
        materialRecipe(wrapped, DreamtinkerMaterialIds.NightMareClaw, ItemNameIngredient.from(new ResourceLocation(BIC, "nightmare_claw")), 1, 1,
                       materials_folder + "nightmare_claw/claw");
        materialRecipe(wrapped, DreamtinkerMaterialIds.NightMareClaw, ItemNameIngredient.from(new ResourceLocation(BIC, "nightmare_stalker_skull")), 4, 1,
                       materials_folder + "nightmare_claw/head");
        materialRecipe(wrapped, DreamtinkerMaterialIds.InfernalEmber, ItemNameIngredient.from(new ResourceLocation(BIC, "smoldering_infernal_ember")), 1, 1,
                       materials_folder + "infernal_ember/item");
        materialRecipe(wrapped, DreamtinkerMaterialIds.InfernalEmber, ItemNameIngredient.from(new ResourceLocation(BIC, "fel_soil")), 4, 1,
                       materials_folder + "infernal_ember/block");
        materialRecipe(wrapped, DreamtinkerMaterialIds.SpiderMandible, ItemNameIngredient.from(new ResourceLocation(BIC, "spider_mandible")), 1, 1,
                       materials_folder + "spider_mandible");
        materialRecipe(wrapped, DreamtinkerMaterialIds.HoundFang, ItemNameIngredient.from(new ResourceLocation(BIC, "fangofthe_hound_leader")), 1, 1,
                       materials_folder + "hound_fang");
    }

    private void addNovaMaterialRecipes(Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("ars_nouveau"));
        materialRecipe(wrapped, DreamtinkerMaterialIds.AbjurationEssence, itemNameIngredient("ars_nouveau", "abjuration_essence"), 1, 1,
                       materials_folder + "abjuration_essence");
        materialRecipe(wrapped, DreamtinkerMaterialIds.ConjurationEssence, itemNameIngredient("ars_nouveau", "conjuration_essence"), 1, 1,
                       materials_folder + "conjuration_essence");
        materialRecipe(wrapped, DreamtinkerMaterialIds.AirEssence, itemNameIngredient("ars_nouveau", "air_essence"), 1, 1,
                       materials_folder + "air_essence");
        materialRecipe(wrapped, DreamtinkerMaterialIds.EarthEssence, itemNameIngredient("ars_nouveau", "earth_essence"), 1, 1,
                       materials_folder + "earth_essence");
        materialRecipe(wrapped, DreamtinkerMaterialIds.FireEssence, itemNameIngredient("ars_nouveau", "fire_essence"), 1, 1,
                       materials_folder + "fire_essence");
        materialRecipe(wrapped, DreamtinkerMaterialIds.ManipulationEssence, itemNameIngredient("ars_nouveau", "manipulation_essence"), 1, 1,
                       materials_folder + "manipulation_essence");
        materialRecipe(wrapped, DreamtinkerMaterialIds.WaterEssence, itemNameIngredient("ars_nouveau", "water_essence"), 1, 1,
                       materials_folder + "water_essence");
        materialRecipe(wrapped, DreamtinkerMaterialIds.WildenHorn, itemNameIngredient("ars_nouveau", "wilden_horn"), 1, 1,
                       materials_folder + "wilden_horn");
        materialRecipe(wrapped, DreamtinkerMaterialIds.WildenWing, itemNameIngredient("ars_nouveau", "wilden_wing"), 1, 1,
                       materials_folder + "wilden_wing");
        materialRecipe(wrapped, DreamtinkerMaterialIds.WildenSpike, itemNameIngredient("ars_nouveau", "wilden_spike"), 1, 1,
                       materials_folder + "wilden_spike");
    }

    private void addUGMaterialRecipes(Consumer<FinishedRecipe> consumer) {
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("undergarden"));

        materialComposite(wrapped, MaterialIds.leather, DreamtinkerMaterialIds.GooeySlimeSkin, DreamtinkerFluids.gooey_slime, FluidValues.SLIMEBALL,
                          slimeskinFolder, "undergarden_gooey_slime");
        materialComposite(wrapped, DreamtinkerMaterialIds.GooeySlimeSkin, MaterialIds.leather, TinkerFluids.venom, FluidValues.SIP, slimeskinFolder,
                          "undergarden_gooey_slime_cleaning");
    }

    private void addLegendaryMonstersMaterialRecipes(Consumer<FinishedRecipe> consumer) {
        String legendaryMonsters = "legendary_monsters";
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded(legendaryMonsters));
        materialMeltingCasting(wrapped, DreamtinkerMaterialIds.legendary_monsters_enderitium, DreamtinkerFluids.molten_enderitium, FluidValues.INGOT,
                               materials_folder);
        materialRecipe(wrapped, DreamtinkerMaterialIds.legendary_monsters_enderitium, itemNameIngredient(legendaryMonsters, "enderitium_ingot"), 1, 1,
                       materials_folder + "legendary_monsters_enderitium/ingot");
        materialRecipe(wrapped, DreamtinkerMaterialIds.legendary_monsters_enderitium, itemNameIngredient(legendaryMonsters, "enderitium_gem"), 1, 9,
                       materials_folder + "legendary_monsters_enderitium/gem");
        materialRecipe(wrapped, DreamtinkerMaterialIds.legendary_monsters_enderitium, itemNameIngredient(legendaryMonsters, "enderitium_block"), 9, 1,
                       materials_folder + "legendary_monsters_enderitium/block");
    }

    private void standardMetalMaterial(Consumer<FinishedRecipe> consumer, MaterialId materialId, FlowingFluidObject<ForgeFlowingFluid> fluid, String name, String folder) {
        materialMeltingCasting(consumer, materialId, fluid, FluidValues.INGOT, folder);

        TagKey<Item> ingots = forgeTag("ingots", name);
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, tagFilled(ingots));
        materialRecipe(wrapped, materialId, Ingredient.of(ingots), 1, 1, folder + name + "/ingot");
        TagKey<Item> nuggets = forgeTag("nuggets", name);
        wrapped = withCondition(consumer, tagFilled(nuggets));
        materialRecipe(wrapped, materialId, Ingredient.of(nuggets), 1, 9, folder + name + "/nugget");
        TagKey<Item> sb = forgeTag("storage_blocks", name);
        wrapped = withCondition(consumer, tagFilled(sb));
        materialRecipe(wrapped, materialId, Ingredient.of(sb), 9, 1, folder + name + "/block");
    }

    private void standardGemMaterial(Consumer<FinishedRecipe> consumer, MaterialId materialId, FluidObject<?> fluid, String name, String folder) {
        materialMeltingCasting(consumer, materialId, fluid, FluidValues.GEM, folder);
        TagKey<Item> gems = forgeTag("gems", name);
        Consumer<FinishedRecipe> wrapped = withCondition(consumer, tagFilled(gems));
        materialRecipe(wrapped, materialId, Ingredient.of(gems), 1, 1, folder + name + "/gem");
        TagKey<Item> sb = forgeTag("storage_blocks", name);
        wrapped = withCondition(consumer, tagFilled(sb));
        materialRecipe(wrapped, materialId, Ingredient.of(forgeTag("storage_blocks", name)), 9, 1, folder + name + "/block");
    }

    @Override
    public @NotNull String getModId() {
        return Dreamtinker.MODID;
    }

}
