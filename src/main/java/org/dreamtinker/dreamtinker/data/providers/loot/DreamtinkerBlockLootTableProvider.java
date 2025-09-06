package org.dreamtinker.dreamtinker.data.providers.loot;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerBlocks;

import java.util.Set;
import java.util.stream.Collectors;

public class DreamtinkerBlockLootTableProvider extends BlockLootSubProvider {
    protected DreamtinkerBlockLootTableProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream()
                                      .filter(block -> Dreamtinker.MODID.equals(BuiltInRegistries.BLOCK.getKey(block).getNamespace()))
                                      .collect(Collectors.toList());
    }

    @Override
    protected void generate() {
        this.addDecorative();
    }

    private void addDecorative() {
        this.dropSelf(DreamtinkerBlocks.crying_obsidian_plane.get());

    }
}
