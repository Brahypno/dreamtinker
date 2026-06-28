package org.brahypno.dreamtinker.tools.data;

import com.sammy.malum.data.recipe.builder.SpiritInfusionRecipeBuilder;
import com.sammy.malum.registry.common.SpiritTypeRegistry;
import com.sammy.malum.registry.common.item.ItemRegistry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CompoundIngredient;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;
import org.brahypno.dreamtinker.library.compact.ars_nouveau.NovaRegistry;
import org.brahypno.dreamtinker.smeltery.DreamTinkerSmeltery;
import org.brahypno.dreamtinker.tools.DreamtinkerToolParts;
import org.brahypno.dreamtinker.tools.DreamtinkerTools;
import org.brahypno.dreamtinker.tools.data.material.DreamtinkerMaterialDataProvider;
import org.brahypno.dreamtinker.utils.DTPartInfoLookup;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.casting.material.CompositeCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.PartSwapCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.ItemPartRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.recycle.PartBuilderToolRecycleBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class DreamtinkerPartToolBuildingRecipeProvider implements IToolRecipeHelper {

    String partFolder = "tools/parts/";

    String castFolder = "smeltery/casts/";

    public void addPartRecipes(Consumer<FinishedRecipe> consumer) {
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

        partRecipes(consumer, DreamtinkerToolParts.chainSawTeeth, DreamTinkerSmeltery.chainSawTeethCast, 12, partFolder, castFolder);
        partRecipes(consumer, DreamtinkerToolParts.chainSawCore, DreamTinkerSmeltery.chainSawCoreCast, 8, partFolder, castFolder);
        partRecipes(consumer, DreamtinkerToolParts.NovaCover, DreamTinkerSmeltery.NovaCoverCast, 2, partFolder, castFolder);
        partRecipes(consumer, DreamtinkerToolParts.NovaMisc, DreamTinkerSmeltery.NovaMiscCast, 3, partFolder, castFolder);
        partRecipes(consumer, DreamtinkerToolParts.NovaWrapper, DreamTinkerSmeltery.NovaWrapperCast, 2, partFolder, castFolder);
        partRecipes(consumer, DreamtinkerToolParts.NovaRostrum, DreamTinkerSmeltery.NovaRostrumCast, 4, partFolder, castFolder);
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

        armorPlatingBuilder(wrapped, DreamtinkerMaterialIds.dragon_scale);
    }

    public void addToolBuildingRecipes(Consumer<FinishedRecipe> consumer) {
        String folder = "tools/building/";
        String armorFolder = "tools/armor/";
        ToolBuildingRecipeBuilder.toolBuildingRecipe(DreamtinkerTools.tntarrow.get())
                                 .outputSize(4)
                                 .save(consumer, prefix(DreamtinkerTools.tntarrow, folder));
        toolBuilding(consumer, DreamtinkerTools.mashou, folder);
        toolBuilding(consumer, DreamtinkerTools.narcissus_wing, folder);
        toolBuilding(consumer, DreamtinkerTools.chain_saw_blade, folder);

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
        List<ToolPartItem> parts = DTPartInfoLookup.partList(statsId);
        for (ToolPartItem part : parts) {
            Ingredient castItem = DTPartInfoLookup.datagenCastIngredient(part);
            if (castItem.isEmpty())
                continue;
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

    @Override
    public @NotNull String getModId() {
        return Dreamtinker.MODID;
    }

}
