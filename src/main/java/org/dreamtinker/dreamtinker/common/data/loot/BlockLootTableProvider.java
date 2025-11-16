package org.dreamtinker.dreamtinker.common.data.loot;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
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
        this.dropSelf(DreamtinkerCommon.crying_obsidian_plane.get());
        this.dropSelf(DreamtinkerCommon.narcissus.get());
        this.dropSelf(DreamtinkerCommon.soulSteelBlock.get());
        this.dropPottedContents(DreamtinkerCommon.potted_narcissus.get());
    }

    private void addWorld() {
        this.add(DreamtinkerCommon.larimarOre.get(), block -> createOreDrop(block, DreamtinkerCommon.larimar.get()));
        this.add(DreamtinkerCommon.amberOre.get(), block -> createOreDrop(block, DreamtinkerCommon.amber.get()));
        this.add(DreamtinkerCommon.blackSapphireOre.get(), block -> createOreDrop(block, DreamtinkerCommon.black_sapphire.get()));
        this.add(DreamtinkerCommon.DeepSlateBlackSapphireOre.get(), block -> createOreDrop(block, DreamtinkerCommon.black_sapphire.get()));
        this.add(DreamtinkerCommon.scoleciteOre.get(), block -> createOreDrop(block, DreamtinkerCommon.scolecite.get()));
        this.add(DreamtinkerCommon.coldIronOre.get(), block -> createCopperLikeOreDrops(block, Items.RAW_IRON));
        this.add(DreamtinkerCommon.DeepslateColdIronOre.get(), block -> createCopperLikeOreDrops(block, Items.RAW_IRON));
    }

    protected LootTable.Builder createCopperLikeOreDrops(Block p_251306_, Item item) {
        return createSilkTouchDispatchTable(p_251306_,
                                            this.applyExplosionDecay(p_251306_, LootItem.lootTableItem(item).apply(
                                                    SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F))).apply(ApplyBonusCount.addOreBonusCount(
                                                    Enchantments.BLOCK_FORTUNE))));
    }
}
