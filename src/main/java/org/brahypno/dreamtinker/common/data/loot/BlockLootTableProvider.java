package org.brahypno.dreamtinker.common.data.loot;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.loot.function.RetexturedLootFunction;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.FenceBuildingBlockObject;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockLootTableProvider extends BlockLootSubProvider {
    protected BlockLootTableProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream()
                                      .filter(block -> Dreamtinker.MODID.equals(BuiltInRegistries.BLOCK.getKey(block).getNamespace()))
                                      .collect(Collectors.toList());
    }

    @Override
    protected void generate() {
        this.addDecorative();
        this.addWorld();
    }

    private void addDecorative() {
        this.dropSelf(DreamtinkerCommon.metallivorous_stibium_lupus_block.get());
        this.dropSelf(DreamtinkerCommon.crying_obsidian_plane.get());
        this.dropSelf(DreamtinkerCommon.narcissus.get());
        this.dropSelf(DreamtinkerCommon.soulSteelBlock.get());
        this.dropPottedContents(DreamtinkerCommon.potted_narcissus.get());
        this.dropSelf(DreamtinkerCommon.RawOrichalcumBlock.get());
        this.dropSelf(DreamtinkerCommon.OrichalcumBlock.get());
        this.dropSelf(DreamtinkerCommon.RawColdIronBlock.get());
        this.dropSelf(DreamtinkerCommon.ColdIronBlock.get());
        this.dropSelf(DreamtinkerCommon.RawShadowSilverBlock.get());
        this.dropSelf(DreamtinkerCommon.ShadowSilverBlock.get());
        this.dropSelf(DreamtinkerCommon.RawTransmutationGoldBlock.get());
        this.dropSelf(DreamtinkerCommon.TransmutationGoldBlock.get());
        this.dropSelf(DreamtinkerCommon.UnbornDragonEgg.get());
        this.dropSelf(DreamtinkerCommon.UnbornSnifferEgg.get());
        this.dropSelf(DreamtinkerCommon.UnbornTurtleEgg.get());
    }

    private void addWorld() {
        this.add(DreamtinkerCommon.larimarOre.get(), block -> createOreDrop(block, DreamtinkerCommon.larimar.get()));
        this.add(DreamtinkerCommon.amberOre.get(), block -> createOreDrop(block, DreamtinkerCommon.amber.get()));
        this.add(DreamtinkerCommon.blackSapphireOre.get(), block -> createOreDrop(block, DreamtinkerCommon.black_sapphire.get()));
        this.add(DreamtinkerCommon.scoleciteOre.get(), block -> createOreDrop(block, DreamtinkerCommon.scolecite.get()));
        this.add(DreamtinkerCommon.coldIronOre.get(), block -> createCopperLikeOreDrops(block, DreamtinkerCommon.raw_cold_iron.get()));
        this.add(DreamtinkerCommon.DeepslateColdIronOre.get(), block -> createCopperLikeOreDrops(block, DreamtinkerCommon.raw_cold_iron.get()));
        this.add(DreamtinkerCommon.OrichalcumOre.get(), block -> createOreDrop(block, DreamtinkerCommon.raw_orichalcum.get()));
        this.add(DreamtinkerCommon.DeepslateOrichalcumOre.get(), block -> createOreDrop(block, DreamtinkerCommon.raw_orichalcum.get()));
        this.add(DreamtinkerCommon.ShadowSilverOre.get(), block -> createOreDrop(block, DreamtinkerCommon.raw_shadow_silver.get()));
        this.add(DreamtinkerCommon.DeepslateShadowSilverOre.get(), block -> createOreDrop(block, DreamtinkerCommon.raw_shadow_silver.get()));
        this.add(DreamtinkerCommon.TransmutationGoldOre.get(), block -> createOreDrop(block, DreamtinkerCommon.raw_transmutation_gold.get()));
        this.add(DreamtinkerCommon.DeepslateTransmutationGoldOre.get(), block -> createOreDrop(block, DreamtinkerCommon.raw_transmutation_gold.get()));
    }

    protected LootTable.Builder createCopperLikeOreDrops(Block p_251306_, Item item) {
        return createSilkTouchDispatchTable(p_251306_,
                                            this.applyExplosionDecay(p_251306_, LootItem.lootTableItem(item).apply(
                                                    SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F))).apply(ApplyBonusCount.addOreBonusCount(
                                                    Enchantments.BLOCK_FORTUNE))));
    }


    private void registerFenceBuildingLootTables(FenceBuildingBlockObject object) {
        registerBuildingLootTables(object);
        this.dropSelf(object.getFence());
    }

    private LootTable.Builder droppingWithFunctions(Block block, Function<LootItem.Builder<?>, LootItem.Builder<?>> mapping) {
        return LootTable.lootTable().withPool(
                applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(mapping.apply(LootItem.lootTableItem(block)))));
    }

    /**
     * Copies a material block texture
     */
    private final LootItemFunction.Builder COPY_NAME = CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY);
    /**
     * Copies a material block texture
     */
    private final LootItemFunction.Builder COPY_MATERIAL =
            CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(IMaterialItem.MATERIAL_TAG, IMaterialItem.MATERIAL_TAG);
    /**
     * Properties for a standard table
     */
    private final Function<Block, LootTable.Builder> ADD_TABLE = block -> droppingWithFunctions(block, (builder) ->
            builder.apply(COPY_NAME).apply(RetexturedLootFunction::new));

    /**
     * Registers a block that drops with its own texture stored in NBT
     */
    private void dropTable(Block table) {
        this.add(table, ADD_TABLE);
    }

    private void registerBuildingLootTables(BuildingBlockObject object) {
        this.dropSelf(object.get());
        this.add(object.getSlab(), this::createSlabItemTable);
        this.dropSelf(object.getStairs());
    }
}
