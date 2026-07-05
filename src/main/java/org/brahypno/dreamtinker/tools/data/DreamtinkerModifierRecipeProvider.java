package org.brahypno.dreamtinker.tools.data;

import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.sammy.malum.registry.common.block.BlockRegistry;
import com.sammy.malum.registry.common.item.ItemRegistry;
import com.sammy.malum.registry.common.item.ItemTagRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;
import org.brahypno.dreamtinker.common.DreamtinkerTagKeys;
import org.brahypno.dreamtinker.library.compat.ars_nouveau.NovaRegistry;
import org.brahypno.dreamtinker.library.compat.ars_nouveau.ReactiveModifiableEnchantmentRecipeBuilder;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.dreamtinker.tools.DreamtinkerToolParts;
import org.brahypno.dreamtinker.tools.DreamtinkerTools;
import org.brahypno.dreamtinker.tools.data.material.DreamtinkerMaterialDataProvider;
import org.brahypno.esotericismtinker.library.compat.ars_nouveau.recipe.builder.ModifiableEnchantmentRecipeBuilder;
import org.brahypno.esotericismtinker.library.tools.EsotericismSlotType;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.data.ICommonRecipeHelper;
import slimeknights.mantle.recipe.data.ItemNameIngredient;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.ingredient.ToolHookIngredient;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.SwappableModifierRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class DreamtinkerModifierRecipeProvider implements ICommonRecipeHelper {

    private static Ingredient itemNameIngredient(String modid, String path) {
        return ItemNameIngredient.from(new ResourceLocation(modid, path));
    }

    private static Ingredient ingredientFromTags(TagKey<Item>... tags) {
        Ingredient[] tagIngredients = new Ingredient[tags.length];
        for (int i = 0; i < tags.length; i++) {
            tagIngredients[i] = Ingredient.of(tags[i]);
        }
        return CompoundIngredient.of(tagIngredients);
    }

    public void addModifierRecipes(Consumer<FinishedRecipe> consumer) {
        // modifiers
        String upgradeFolder = "tools/modifiers/upgrade/";
        String abilityFolder = "tools/modifiers/ability/";
        String slotlessFolder = "tools/modifiers/slotless/";
        String defenseFolder = "tools/modifiers/defense/";
        String compatFolder = "tools/modifiers/compat/";
        String worktableFolder = "tools/modifiers/worktable/";
        String soulFolder = "tools/modifiers/soul/";
        String delusionFolder = "tools/modifiers/delusion/";
        // salvage
        String salvageFolder = "tools/modifiers/salvage/";
        String upgradeSalvage = salvageFolder + "upgrade/";
        String abilitySalvage = salvageFolder + "ability/";
        String defenseSalvage = salvageFolder + "defense/";
        String compatSalvage = salvageFolder + "compat/";
        String soulSalvage = salvageFolder + "soul/";
        String delusionSalvage = salvageFolder + "delusion/";
        Consumer<FinishedRecipe> wrapped;
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.strong_explode)
                             .setTools(Ingredient.of(DreamtinkerTools.tntarrow.get()))
                             .addInput(TinkerTools.shuriken.get())
                             .addInput(Tags.Items.GUNPOWDER)
                             .setMaxLevel(4)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.strong_explode, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.strong_explode, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.continuous_explode)
                             .setTools(Ingredient.of(DreamtinkerTools.tntarrow.get()))
                             .addInput(TinkerTools.shuriken.get())
                             .addInput(Items.TNT)
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .disallowCrystal()
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.continuous_explode, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.continuous_explode, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.continuous_explode)
                             .setTools(Ingredient.of(DreamtinkerTools.tntarrow.get()))
                             .addInput(Items.TNT)
                             .addInput(Tags.Items.GUNPOWDER)
                             .setMinLevel(2)
                             .setMaxLevel(3)
                             .setSlots(SlotType.UPGRADE, 1)
                             .disallowCrystal()
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
                             .setSlots(SlotType.UPGRADE, 1)
                             .save(consumer, wrap(DreamtinkerModifiers.life_looting, abilityFolder, "_1"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.soul_upgrade)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(DreamtinkerCommon.persona_cast.get(), 1)
                             .setMaxLevel(1)
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.soul_upgrade, slotlessFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.abyss_inside)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(Items.AMETHYST_SHARD, 1)
                             .addInput(Tags.Items.GEMS_PRISMARINE, 1)
                             .addInput(Items.BONE_MEAL, 1)
                             .setMaxLevel(1)
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.abyss_inside, slotlessFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.meta_morphosis)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(Items.CHORUS_FLOWER, 1)
                             .addInput(Items.SHULKER_SHELL, 1)
                             .setMaxLevel(1)
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.meta_morphosis, slotlessFolder));

        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.soul_core)
                             .setTools(Ingredient.of(DreamtinkerTools.narcissus_wing.get()))
                             .addInput(DreamtinkerCommon.UnbornTurtleEgg.get(), 1)
                             .setMaxLevel(1)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.soul_core, delusionSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.soul_core, delusionFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.soul_core)
                             .setTools(Ingredient.of(DreamtinkerTools.narcissus_wing.get()))
                             .addInput(DreamtinkerCommon.UnbornTurtleEgg.get(), 1)
                             .addInput(DreamtinkerCommon.UnbornSnifferEgg.get(), 1)
                             .setMaxLevel(3)
                             .setSlots(SlotType.UPGRADE, 1)
                             .save(consumer, wrap(DreamtinkerModifiers.Ids.soul_core, delusionFolder, "_1"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.icy_memory)
                             .setTools(Ingredient.of(DreamtinkerTools.narcissus_wing.get()))
                             .addInput(DreamtinkerCommon.unborn_egg.get())
                             .addInput(Items.POWDER_SNOW_BUCKET)
                             .setMaxLevel(2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .disallowCrystal()
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.icy_memory, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.icy_memory, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.icy_memory)
                             .setTools(Ingredient.of(DreamtinkerTools.narcissus_wing.get()))
                             .addInput(DreamtinkerCommon.UnbornDragonEgg.get())
                             .addInput(DreamtinkerCommon.despair_gem.get())
                             .setLevelRange(3, 3)
                             .setSlots(EsotericismSlotType.DELUSION, 1)
                             .disallowCrystal()
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.icy_memory, delusionSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.icy_memory, delusionFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.hate_memory)
                             .setTools(Ingredient.of(DreamtinkerTools.narcissus_wing.get()))
                             .addInput(DreamtinkerCommon.unborn_egg.get())
                             .addInput(Items.WHITE_BANNER)
                             .addInput(Items.IRON_AXE)
                             .addInput(Tags.Items.TOOLS_CROSSBOWS)
                             .setMaxLevel(3)
                             .setSlots(EsotericismSlotType.DELUSION, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.hate_memory, delusionSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.hate_memory, delusionFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.huge_ego)
                             .addInput(DreamtinkerCommon.twist_obsidian_pane.get(), 1)
                             .addInput(Tags.Items.GEMS_EMERALD, 1)
                             .addInput(DreamtinkerCommon.twist_obsidian_pane.get(), 1)
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
                             .setSlots(EsotericismSlotType.DELUSION, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.all_slayer, delusionSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.all_slayer, delusionFolder));

        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.the_romantic)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(Tags.Items.STORAGE_BLOCKS_GOLD, 7)
                             .addInput(Tags.Items.STORAGE_BLOCKS_GOLD)
                             .addInput(TinkerModifiers.silkyCloth, 5)
                             .addInput(TinkerModifiers.silkyCloth)
                             .setSlots(EsotericismSlotType.DELUSION, 1)
                             .setMaxLevel(5)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.the_romantic, delusionSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.the_romantic, delusionFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.virtual_dodge)
                             .setTools(TinkerTags.Items.WORN_ARMOR)
                             .addInput(DreamtinkerCommon.void_pearl.get(), 4)
                             .addInput(Items.ENDER_EYE, 8)
                             .setSlots(EsotericismSlotType.DELUSION, 1)
                             .setMaxLevel(3)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.virtual_dodge, delusionSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.virtual_dodge, delusionFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.sweet_death)
                             .setTools(TinkerTags.Items.SHIELDS)
                             .addInput(Items.SKELETON_SKULL, 1)
                             .addInput(Items.WITHER_SKELETON_SKULL, 1)
                             .addInput(Items.HONEY_BLOCK, 4)
                             .setSlots(EsotericismSlotType.DELUSION, 1)
                             .setMaxLevel(2)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.sweet_death, delusionSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.sweet_death, delusionFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.last_kiss)
                             .setTools(TinkerTags.Items.SHIELDS)
                             .addInput(Items.CLOCK, 1)
                             .addInput(Items.DISC_FRAGMENT_5, 1)
                             .addInput(Items.WHITE_TULIP, 1)
                             .addInput(Items.CHERRY_SAPLING, 1)
                             .setSlots(EsotericismSlotType.DELUSION, 1)
                             .setMaxLevel(2)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.last_kiss, delusionSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.last_kiss, delusionFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.born_with_me)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(Items.BLADE_POTTERY_SHERD, 1)
                             .setSlots(EsotericismSlotType.DELUSION, 1)
                             .setMaxLevel(3)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.born_with_me, delusionSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.born_with_me, delusionFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.weapon_dreams_order)
                             .setTools(Ingredient.of(DreamtinkerTools.silence_glove.get()))
                             .addInput(Items.COMPASS, 2)
                             .addInput(Items.BELL)
                             .addInput(Items.CLOCK)
                             .setSlots(EsotericismSlotType.DELUSION, 1)
                             .setMaxLevel(1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.weapon_dreams_order, delusionSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.weapon_dreams_order, delusionFolder));
        IncrementalModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.falsify_fate)
                                        .setTools(TinkerTags.Items.HARVEST_PRIMARY)
                                        .setInput(DreamtinkerCommon.amber.get(), 1, 40)
                                        .setMaxLevel(3)
                                        .setSlots(EsotericismSlotType.DELUSION, 1)
                                        .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.falsify_fate, delusionSalvage))
                                        .save(consumer, prefix(DreamtinkerModifiers.Ids.falsify_fate, delusionFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.weapon_dreams_filter)
                             .setTools(Ingredient.of(DreamtinkerTools.silence_glove.get()))
                             .addInput(Items.REPEATER, 2)
                             .addInput(Items.COMPARATOR)
                             .addInput(Items.OBSERVER)
                             .setSlots(EsotericismSlotType.DELUSION, 1)
                             .setMaxLevel(1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.weapon_dreams_filter, delusionSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.weapon_dreams_filter, delusionFolder));
        Ingredient under_plates = Ingredient.of(DreamtinkerTools.underPlate.get(ArmorItem.Type.HELMET),
                                                DreamtinkerTools.underPlate.get(ArmorItem.Type.CHESTPLATE),
                                                DreamtinkerTools.underPlate.get(ArmorItem.Type.LEGGINGS),
                                                DreamtinkerTools.underPlate.get(ArmorItem.Type.BOOTS));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.spiritual_weapon_transformation)
                             .setTools(under_plates)
                             .addInput(BlockRegistry.BLOCK_OF_ASTRAL_WEAVE.get(), 3)
                             .addInput(ItemRegistry.TOPHAT.get())
                             .addInput(BlockRegistry.WICKED_SPIRITED_GLASS.get(), 16)
                             .setSlots(EsotericismSlotType.DELUSION, 1)
                             .setMaxLevel(1)
                             .saveSalvage(withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("malum")),
                                          prefix(DreamtinkerModifiers.Ids.spiritual_weapon_transformation, delusionSalvage))
                             .save(withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("malum")),
                                   prefix(DreamtinkerModifiers.Ids.spiritual_weapon_transformation, delusionFolder));
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
        SwappableModifierRecipeBuilder.modifier(ModifierIds.rebalanced, EsotericismSlotType.DELUSION.getName())
                                      .setTools(TinkerTags.Items.BONUS_SLOTS)
                                      .addInput(DreamtinkerCommon.void_pearl.get(), 1)
                                      .addInput(Items.END_CRYSTAL, 1)
                                      .addInput(DreamtinkerCommon.void_pearl.get(), 1)
                                      .addInput(Items.AMETHYST_BLOCK, 1)
                                      .addInput(Items.AMETHYST_BLOCK, 1)
                                      .disallowCrystal()
                                      .save(consumer,
                                            wrap(DreamtinkerModifiers.Ids.five_creations, slotlessFolder, "_" + EsotericismSlotType.DELUSION.getName()));
        SwappableModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.five_creations, "traits")
                                      .setTools(ToolHookIngredient.of(TinkerTags.Items.BONUS_SLOTS, ToolHooks.REBALANCED_TRAIT))
                                      .addInput(TinkerTags.Items.ANCIENT_TOOLS)
                                      .addInput(TinkerTags.Items.ANCIENT_TOOLS)
                                      .addInput(TinkerTags.Items.ANCIENT_TOOLS)
                                      .addInput(DreamtinkerCommon.echo_alloy.get(), 5)
                                      .addInput(Items.CALIBRATED_SCULK_SENSOR, 5)
                                      .disallowCrystal()
                                      .save(consumer, wrap(DreamtinkerModifiers.Ids.five_creations, slotlessFolder, "_traits"));

        SwappableModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.five_creations, "designs")
                                      .setTools(TinkerTags.Items.BONUS_SLOTS)
                                      .addInput(DreamtinkerCommon.snake_fang.get(), 1)
                                      .addInput(DreamtinkerCommon.poisonousHomunculus.get(), 2)
                                      .addInput(DreamtinkerCommon.evilHomunculus.get(), 2)
                                      .addInput(DreamtinkerCommon.rainbow_honey_crystal.get(), 2)
                                      .addInput(DreamtinkerCommon.shiningFlint.get(), 5)
                                      .disallowCrystal()
                                      .save(consumer, wrap(DreamtinkerModifiers.Ids.five_creations, slotlessFolder, "_designs"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.signal_axe)
                             .setTools(Ingredient.of(TinkerTools.broadAxe.get(), TinkerTools.handAxe.get()))
                             .addInput(Blocks.RED_CANDLE, 1)
                             .addInput(Blocks.GREEN_CANDLE, 1)
                             .addInput(Blocks.BLUE_CANDLE, 1)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.signal_axe, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.signal_axe, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.love_shooting)
                             .setTools(TinkerTags.Items.RANGED)
                             .addInput(Items.BUDDING_AMETHYST, 11)
                             .addInput(TinkerWorld.earthGeode.getBudding(), 11)
                             .addInput(TinkerWorld.skyGeode.getBudding(), 11)
                             .addInput(TinkerWorld.ichorGeode.getBudding(), 11)
                             .addInput(TinkerWorld.enderGeode.getBudding(), 11)
                             .setSlots(SlotType.ABILITY, 1)
                             .setMaxLevel(1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.love_shooting, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.love_shooting, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.curse_fire)
                             .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.BOWS, TinkerTags.Items.FISHING_RODS,
                                                          TinkerTags.Items.WORN_ARMOR, TinkerTags.Items.SHIELDS))
                             .addInput(Blocks.BLUE_ICE, 3)
                             .addInput(DreamtinkerCommon.soulSteelBlock, 1)
                             .addInput(DreamtinkerCommon.soul_cast.get(), 1)
                             .addInput(DreamtinkerCommon.poisonousHomunculus.get(), 1)
                             .setSlots(SlotType.ABILITY, 1)
                             .setMaxLevel(1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.curse_fire, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.curse_fire, abilityFolder));
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
                             .addInput(Tags.Items.DUSTS_PRISMARINE, 5)
                             .addInput(Tags.Items.GEMS_PRISMARINE, 5)
                             .setMaxLevel(5)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.torrent, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.torrent, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.wrath)
                             .setTools(throw_weapon)
                             .addInput(Items.PRISMARINE, 5)
                             .addInput(Tags.Items.GEMS_PRISMARINE, 5)
                             .setMaxLevel(4)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.wrath, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.wrath, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.fox_blessing)
                             .setTools(TinkerTags.Items.HELD)
                             .addInput(DreamtinkerCommon.fox_fur.get(), 1)
                             .addInput(Tags.Items.GEMS_PRISMARINE, 15)
                             .setMaxLevel(1)
                             .save(consumer, prefix(DreamtinkerModifiers.fox_blessing, slotlessFolder));
        Ingredient protectableTools = ingredientFromTags(TinkerTags.Items.ARMOR, TinkerTags.Items.HELD);
        IncrementalModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.reprise_protection)
                                        .setInput(DreamtinkerCommon.twist_obsidian_pane.get(), 1, 5)
                                        .setSlots(SlotType.DEFENSE, 1)
                                        .setTools(protectableTools)
                                        .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.reprise_protection, defenseSalvage))
                                        .save(consumer, prefix(DreamtinkerModifiers.Ids.reprise_protection, defenseFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.huge_explosion)
                             .addInput(Items.TNT, 4)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setTools(TinkerTags.Items.AMMO)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.huge_explosion, upgradeSalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.Ids.huge_explosion, upgradeFolder));
        // Start of enigmaticlegacy modifiers
        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("enigmaticlegacy"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.life_looting)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(itemNameIngredient("enigmaticlegacy", "lore_inscriber"))
                             .setMaxLevel(1)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.life_looting, abilitySalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.life_looting, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.weapon_books)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(itemNameIngredient("enigmaticlegacy", "the_twist"))
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .disallowCrystal()
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.weapon_books, abilitySalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.weapon_books, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.weapon_books)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(itemNameIngredient("enigmaticlegacy", "the_infinitum"))
                             .setLevelRange(2, 2)
                             .setSlots(SlotType.UPGRADE, 1)
                             .disallowCrystal()
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.weapon_books, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.weapon_books, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.weapon_books)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(itemNameIngredient("enigmaticlegacy", "the_twist"))
                             .addInput(itemNameIngredient("enigmaticlegacy", "abyssal_heart"))
                             .setLevelRange(3, 3)
                             .disallowCrystal()
                             .setSlots(EsotericismSlotType.DELUSION, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.weapon_books, delusionSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.weapon_books, delusionFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.eldritch_pan)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(itemNameIngredient("enigmaticlegacy", "eldritch_pan"))
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.eldritch_pan, abilitySalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.eldritch_pan, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.desolation_ring)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(itemNameIngredient("enigmaticlegacy", "desolation_ring"))
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

        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.blighted_sigil)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(itemNameIngredient("enigmaticlegacy", "evil_essence"))
                             .addInput(itemNameIngredient("enigmaticlegacy", "evil_essence"))
                             .addInput(itemNameIngredient("enigmaticlegacy", "darkest_scroll"))
                             .setMaxLevel(1)
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.blighted_sigil, slotlessFolder));

        // Start of malum modifiers
        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("malum"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_rebound)
                             .setTools(IntersectionIngredient.of(Ingredient.of(ItemTagRegistry.SCYTHE), Ingredient.of(TinkerTags.Items.MELEE_WEAPON)))
                             .addInput(ItemRegistry.PROCESSED_SOULSTONE.get())
                             .addInput(ItemRegistry.EARTHEN_SPIRIT.get())
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.malum_rebound, abilitySalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.malum_rebound, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_rebound)
                             .setTools(IntersectionIngredient.of(Ingredient.of(ItemTagRegistry.SCYTHE), Ingredient.of(TinkerTags.Items.MELEE_WEAPON)))
                             .addInput(ItemRegistry.PROCESSED_SOULSTONE.get(), 2)
                             .setLevelRange(2, 3)
                             .setSlots(SlotType.UPGRADE, 1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.malum_rebound, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.malum_rebound, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_ascension)
                             .setTools(IntersectionIngredient.of(Ingredient.of(ItemTagRegistry.SCYTHE), Ingredient.of(TinkerTags.Items.MELEE_WEAPON)))
                             .addInput(ItemRegistry.PROCESSED_SOULSTONE.get())
                             .addInput(ItemRegistry.AERIAL_SPIRIT.get())
                             .setMaxLevel(1)
                             .setSlots(SlotType.ABILITY, 1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.Ids.malum_ascension, abilitySalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.malum_ascension, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.malum_ascension)
                             .setTools(IntersectionIngredient.of(Ingredient.of(ItemTagRegistry.SCYTHE), Ingredient.of(TinkerTags.Items.MELEE_WEAPON)))
                             .addInput(ItemRegistry.PROCESSED_SOULSTONE.get())
                             .addInput(ItemRegistry.PROCESSED_SOULSTONE.get())
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

        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.many_us)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(ItemTagRegistry.ASPECTED_SPIRITS)
                             .addInput(ItemTagRegistry.ASPECTED_SPIRITS)
                             .addInput(ItemTagRegistry.ASPECTED_SPIRITS)
                             .addInput(ItemTagRegistry.ASPECTED_SPIRITS)
                             .addInput(ItemTagRegistry.ASPECTED_SPIRITS)
                             .setMaxLevel(1)
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.many_us, slotlessFolder));


        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("eidolon"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.eidolon_sapping)
                             .setTools(TinkerTags.Items.MELEE)
                             .addInput(itemNameIngredient("eidolon", "sapping_sword"))
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
                             .addInput(SizedIngredient.of(itemNameIngredient("eidolon", "death_essence"), 2))
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.eidolon_death_bringer, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.eidolon_death_bringer, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.eidolon_bone_chill)
                             .setTools(TinkerTags.Items.MELEE)
                             .addInput(itemNameIngredient("eidolon", "wraith_heart"))
                             .addInput(SizedIngredient.of(itemNameIngredient("eidolon", "pewter_inlay"), 2))
                             .addInput(SizedIngredient.of(itemNameIngredient("eidolon", "lesser_soul_gem"), 2))
                             .addInput(Dreamtinker.forgeItemTag("gems/shadow_gem"), 1)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.eidolon_bone_chill, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.eidolon_bone_chill, upgradeFolder));

        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.ashen_soul)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(SizedIngredient.of(itemNameIngredient("eidolon", "lesser_soul_gem"), 2))
                             .setMaxLevel(1)
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.ashen_soul, slotlessFolder));

        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("legendary_monsters"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.annihilator_armor_power)
                             .setTools(TinkerTags.Items.WORN_ARMOR)
                             .addInput(ItemNameIngredient.from(new ResourceLocation("legendary_monsters", "portal_shard")))
                             .addInput(ItemNameIngredient.from(new ResourceLocation("legendary_monsters", "enderitium_upgrade_smithing_template")))
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.annihilator_armor_power, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.annihilator_armor_power, upgradeFolder));

        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("born_in_chaos_v1"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.bic_dark_armor_plate)
                             .setTools(TinkerTags.Items.DURABILITY)
                             .addInput(ItemNameIngredient.from(new ResourceLocation("born_in_chaos_v1", "armor_plate_from_dark_metal")))
                             .addInput(ItemNameIngredient.from(new ResourceLocation("born_in_chaos_v1", "dark_upgrade")))
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.bic_dark_armor_plate, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.bic_dark_armor_plate, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.bic_frostbitten)
                             .setTools(TinkerTags.Items.MELEE)
                             .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("born_in_chaos_v1", "permafrost_shard")), 5))
                             .addInput(ItemNameIngredient.from(new ResourceLocation("born_in_chaos_v1", "dark_metal_ingot")))
                             .addInput(ItemNameIngredient.from(new ResourceLocation("born_in_chaos_v1", "bone_handle")))
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(3)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.bic_frostbitten, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.bic_frostbitten, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.bic_intoxicating)
                             .setTools(TinkerTags.Items.MELEE)
                             .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation("born_in_chaos_v1", "intoxicating_decoction")), 3))
                             .addInput(ItemNameIngredient.from(new ResourceLocation("born_in_chaos_v1", "dark_metal_ingot")))
                             .addInput(ItemNameIngredient.from(new ResourceLocation("born_in_chaos_v1", "bone_handle")))
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.bic_intoxicating, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.bic_intoxicating, upgradeFolder));

        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.naughty_chaos)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(ItemNameIngredient.from(new ResourceLocation("born_in_chaos_v1", "seedof_chaos")))
                             .addInput(ItemNameIngredient.from(new ResourceLocation("born_in_chaos_v1", "marigolds")))
                             .addInput(ItemNameIngredient.from(new ResourceLocation("born_in_chaos_v1", "ethereal_spirit")))
                             .addInput(ItemNameIngredient.from(new ResourceLocation("born_in_chaos_v1", "orbofthe_summoner")))
                             .setMaxLevel(1)
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.naughty_chaos, slotlessFolder));


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
                             .addInput(itemNameIngredient("ars_nouveau", "wilden_tribute"))
                             .disallowCrystal()
                             .setMaxLevel(2)
                             .save(wrapped, wrap(DreamtinkerModifiers.Ids.nova_spell_tiers, slotlessFolder, "_archmage"));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_magic_armor)
                             .setTools(under_plates)
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "magebloom_fiber"), 4))
                             .addInput(Tags.Items.INGOTS_GOLD, 8)
                             .setMaxLevel(1)
                             .save(wrapped, wrap(NovaRegistry.nova_magic_armor, slotlessFolder, "_mage"));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_magic_armor)
                             .setTools(under_plates)
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "magebloom_fiber"), 4))
                             .addInput(Tags.Items.GEMS_DIAMOND, 8)
                             .setMaxLevel(2)
                             .save(wrapped, wrap(NovaRegistry.nova_magic_armor, slotlessFolder, "_archmage"));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_magic_armor)
                             .setTools(TinkerTags.Items.WORN_ARMOR)
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "wilden_tribute"), 6))
                             .addInput(Items.TOTEM_OF_UNDYING, 1)
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "magebloom"), 3))
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "blank_parchment"), 2))
                             .setSlots(SlotType.ABILITY, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, wrap(NovaRegistry.nova_magic_armor, abilitySalvage, "_1"))
                             .save(wrapped, wrap(NovaRegistry.nova_magic_armor, abilityFolder, "_1"));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_magic_armor)
                             .setTools(TinkerTags.Items.WORN_ARMOR)
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "magebloom_fiber"), 8))
                             .addInput(Tags.Items.INGOTS_GOLD, 8)
                             .addInput(Tags.Items.INGOTS_GOLD, 8)
                             .setLevelRange(2, 2)
                             .save(wrapped, wrap(NovaRegistry.nova_magic_armor, slotlessFolder, "_general_mage"));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_magic_armor)
                             .setTools(TinkerTags.Items.WORN_ARMOR)
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "magebloom_fiber"), 8))
                             .addInput(Tags.Items.GEMS_DIAMOND, 8)
                             .addInput(Tags.Items.GEMS_DIAMOND, 8)
                             .setLevelRange(3, 3)
                             .save(wrapped, wrap(NovaRegistry.nova_magic_armor, slotlessFolder, "_general_archmage"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.nova_mana_reduce)
                             .setTools(TinkerTags.Items.ARMOR)
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "greater_experience_gem"), 6))
                             .addInput(itemNameIngredient("ars_nouveau", "blank_thread"))
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "magebloom"), 3))
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "blank_parchment"), 2))
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, wrap(DreamtinkerModifiers.Ids.nova_mana_reduce, upgradeSalvage, "_1"))
                             .save(wrapped, wrap(DreamtinkerModifiers.Ids.nova_mana_reduce, upgradeFolder, "_1"));
        IncrementalModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.nova_spell_slots)
                                        .setTools(Ingredient.of(NovaRegistry.per_aspera_scriptum.get()))
                                        .setInput(itemNameIngredient("ars_nouveau", "source_gem_block"), 1, 64)
                                        .setSlots(SlotType.UPGRADE, 1)
                                        .setMaxLevel(10)
                                        .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.nova_spell_slots, upgradeSalvage))
                                        .save(wrapped, prefix(DreamtinkerModifiers.Ids.nova_spell_slots, upgradeFolder));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_enchanter_sword)
                             .setTools(TinkerTags.Items.MELEE_PRIMARY)
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "source_gem_block"), 2))
                             .addInput(Tags.Items.STORAGE_BLOCKS_GOLD, 2)
                             .addInput(Tags.Items.GEMS_DIAMOND, 1)
                             .addInput(Items.DIAMOND_SWORD, 1)
                             .setSlots(SlotType.ABILITY, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(NovaRegistry.nova_enchanter_sword, abilitySalvage))
                             .save(wrapped, prefix(NovaRegistry.nova_enchanter_sword, abilityFolder));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_wand)
                             .setTools(TinkerTags.Items.SPECIAL_TOOLS)
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "source_gem"), 4))
                             .addInput(Tags.Items.INGOTS_GOLD, 2)
                             .addInput(itemNameIngredient("ars_nouveau", "air_essence"))
                             .addInput(itemNameIngredient("ars_nouveau", "manipulation_essence"))
                             .setSlots(SlotType.ABILITY, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(NovaRegistry.nova_wand, abilitySalvage))
                             .save(wrapped, prefix(NovaRegistry.nova_wand, abilityFolder));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_spell_bow)
                             .setTools(TinkerTags.Items.BOWS)
                             .addInput(itemNameIngredient("ars_nouveau", "source_gem"))
                             .addInput(Tags.Items.STORAGE_BLOCKS_GOLD, 1)
                             .addInput(itemNameIngredient("ars_nouveau", "manipulation_essence"))
                             .setSlots(SlotType.ABILITY, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(NovaRegistry.nova_spell_bow, abilitySalvage))
                             .save(wrapped, prefix(NovaRegistry.nova_spell_bow, abilityFolder));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_enchanter_shield)
                             .setTools(TinkerTags.Items.SHIELDS)
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "source_gem_block"), 2))
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "source_gem_block"), 2))
                             .addInput(Items.SHIELD, 1)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(NovaRegistry.nova_enchanter_shield, upgradeSalvage))
                             .save(wrapped, prefix(NovaRegistry.nova_enchanter_shield, upgradeFolder));
        ModifierRecipeBuilder.modifier(NovaRegistry.nova_mana_shield)
                             .setTools(TinkerTags.Items.ARMOR)
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "source_gem_block"), 16))
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "source_gem_block"), 16))
                             .addInput(SizedIngredient.of(itemNameIngredient("ars_nouveau", "abjuration_essence"), 9))
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(3)
                             .saveSalvage(wrapped, prefix(NovaRegistry.nova_mana_shield, upgradeSalvage))
                             .save(wrapped, prefix(NovaRegistry.nova_mana_shield, upgradeFolder));

        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.cosmogony_tetrad)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .addInput(itemNameIngredient("ars_nouveau", "earth_essence"))
                             .addInput(itemNameIngredient("ars_nouveau", "water_essence"))
                             .addInput(itemNameIngredient("ars_nouveau", "air_essence"))
                             .addInput(itemNameIngredient("ars_nouveau", "fire_essence"))
                             .setMaxLevel(1)
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.cosmogony_tetrad, slotlessFolder));

        ReactiveModifiableEnchantmentRecipeBuilder.reactive()
                                                  .setTools(Ingredient.of(TinkerTags.Items.MODIFIABLE))
                                                  .addPedestalItem(itemNameIngredient("ars_nouveau", "spell_parchment"))
                                                  .addPedestalItem(itemNameIngredient("ars_nouveau", "source_gem_block"))
                                                  .addPedestalItem(Tags.Items.STORAGE_BLOCKS_LAPIS)
                                                  .slot(SlotType.UPGRADE, 1)
                                                  .source(3000)
                                                  .save(wrapped, prefix(DreamtinkerModifiers.Ids.nova_reactive, upgradeFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.nova_reactive)
                             .setTools(TinkerTags.Items.MODIFIABLE)
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.nova_reactive, upgradeSalvage));

        ModifiableEnchantmentRecipeBuilder.modifier(DreamtinkerModifiers.Ids.nova_reactive, 2)
                                          .setTools(Ingredient.of(TinkerTags.Items.MODIFIABLE))
                                          .addPedestalItem(4, Ingredient.of(Items.BLAZE_POWDER))
                                          .addPedestalItem(ItemsRegistry.AIR_ESSENCE)
                                          .addPedestalItem(ItemsRegistry.EARTH_ESSENCE)
                                          .addPedestalItem(ItemsRegistry.FIRE_ESSENCE)
                                          .addPedestalItem(ItemsRegistry.WATER_ESSENCE)
                                          .source(6000)
                                          .save(wrapped, wrap(DreamtinkerModifiers.Ids.nova_reactive, upgradeFolder, "_1"));
        ModifiableEnchantmentRecipeBuilder.modifier(DreamtinkerModifiers.Ids.nova_reactive, 3)
                                          .setTools(Ingredient.of(TinkerTags.Items.MODIFIABLE))
                                          .addPedestalItem(4, Ingredient.of(Tags.Items.GEMS_EMERALD))
                                          .addPedestalItem(Ingredient.of(Tags.Items.ENDER_PEARLS))
                                          .addPedestalItem(ItemsRegistry.ABJURATION_ESSENCE)
                                          .addPedestalItem(ItemsRegistry.CONJURATION_ESSENCE)
                                          .addPedestalItem(ItemsRegistry.MANIPULATION_ESSENCE)
                                          .source(9000)
                                          .save(wrapped, wrap(DreamtinkerModifiers.Ids.nova_reactive, upgradeFolder, "_2"));

        String OCC = "occultism";
        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded(OCC));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.occ_view)
                             .setTools(Ingredient.of(TinkerTags.Items.HELMETS))
                             .addInput(ItemNameIngredient.from(new ResourceLocation(OCC, "otherworld_goggles")))
                             .setMaxLevel(1)
                             .save(wrapped, prefix(DreamtinkerModifiers.occ_view, slotlessFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.otherworld_precious)
                             .setTools(Ingredient.of(TinkerTags.Items.HELMETS))
                             .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation(OCC, "spirit_attuned_gem")), 3))
                             .addInput(SizedIngredient.of(ItemNameIngredient.from(new ResourceLocation(OCC, "awakened_feather")), 6))
                             .setMaxLevel(1)
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.otherworld_precious, slotlessFolder));

        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded("botania"));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.botania_pure_smeltery)
                             .setTools(ingredientFromTags(TinkerTags.Items.INTERACTABLE, TinkerTags.Items.WORN_ARMOR))
                             .addInput(ItemNameIngredient.from(new ResourceLocation("botania", "pure_daisy")))
                             .setSlots(SlotType.UPGRADE, 1)
                             .setMaxLevel(4)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.botania_pure_smeltery, upgradeSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.botania_pure_smeltery, upgradeFolder));

        String tinkersThinking = "tinkers_thinking";
        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded(tinkersThinking));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.rainbow_lights)
                             .setTools(Ingredient.of(TinkerTags.Items.MELEE_PRIMARY))
                             .addInput(ItemNameIngredient.from(new ResourceLocation(tinkersThinking, "chromatic_crystal")))
                             .setSlots(SlotType.ABILITY, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, wrap(DreamtinkerModifiers.rainbow_lights, abilitySalvage, tinkersThinking))
                             .save(wrapped, wrap(DreamtinkerModifiers.rainbow_lights, abilityFolder, tinkersThinking));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.rainbow_lights)
                             .setTools(Ingredient.of(TinkerTags.Items.MELEE_PRIMARY))
                             .addInput(Items.RECOVERY_COMPASS)
                             .addInput(Items.END_CRYSTAL)
                             .addInput(Items.AXOLOTL_BUCKET)
                             .addInput(Blocks.WAXED_OXIDIZED_CUT_COPPER)
                             .addInput(DreamtinkerCommon.rainbow_honey_crystal.get())
                             .setSlots(SlotType.ABILITY, 1)
                             .setMaxLevel(1)
                             .saveSalvage(consumer, prefix(DreamtinkerModifiers.rainbow_lights, abilitySalvage))
                             .save(consumer, prefix(DreamtinkerModifiers.rainbow_lights, abilityFolder));
        String fa = "forbidden_arcanus";
        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded(fa));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.unbreakable)
                             .setTools(Ingredient.of(TinkerTags.Items.DURABILITY))
                             .addInput(ItemNameIngredient.from(new ResourceLocation(fa, "darkstone_upgrade_smithing_template")))
                             .addInput(ItemNameIngredient.from(new ResourceLocation(fa, "eternal_stella")))
                             .setSlots(SlotType.ABILITY, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.Ids.unbreakable, abilitySalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.unbreakable, abilityFolder));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.Ids.divineMaledictus)
                             .setTools(Ingredient.of(TinkerTags.Items.MODIFIABLE))
                             .addInput(ItemNameIngredient.from(new ResourceLocation(fa, "divine_pact")))
                             .addInput(ItemNameIngredient.from(new ResourceLocation(fa, "maledictus_pact")))
                             .setMaxLevel(1)
                             .save(wrapped, prefix(DreamtinkerModifiers.Ids.divineMaledictus, slotlessFolder));
        String blm = "bloodmagic";
        wrapped = withCondition(consumer, DreamtinkerMaterialDataProvider.modLoaded(blm));
        ModifierRecipeBuilder.modifier(DreamtinkerModifiers.living_armor)
                             .setTools(Ingredient.of(TinkerTags.Items.CHESTPLATES))
                             .addInput(ItemNameIngredient.from(new ResourceLocation(blm, "reagentbinding")))
                             .setSlots(EsotericismSlotType.DELUSION, 1)
                             .setMaxLevel(1)
                             .saveSalvage(wrapped, prefix(DreamtinkerModifiers.living_armor, delusionSalvage))
                             .save(wrapped, prefix(DreamtinkerModifiers.living_armor, delusionFolder));
    }

    @Override
    public @NotNull String getModId() {
        return Dreamtinker.MODID;
    }

}
