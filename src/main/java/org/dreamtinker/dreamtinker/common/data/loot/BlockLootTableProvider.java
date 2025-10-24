package org.dreamtinker.dreamtinker.common.data.loot;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
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
        this.dropPottedContents(DreamtinkerCommon.potted_narcissus.get());
    }

    private void addWorld() {
        this.add(DreamtinkerCommon.larimarOre.get(), block -> createOreDrop(block, DreamtinkerCommon.larimar.get()));
        this.add(DreamtinkerCommon.amberOre.get(), block -> createOreDrop(block, DreamtinkerCommon.amber.get()));
    }
}
