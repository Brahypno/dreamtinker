package org.dreamtinker.dreamtinker.data.providers.loot;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public class DreamtinkerLootTableProvider extends LootTableProvider {
    private static final Set<ResourceLocation> REQUIRED_TABLES = Set.of();

    public DreamtinkerLootTableProvider(PackOutput packOutput) {
        super(packOutput, REQUIRED_TABLES, List.of(
                new LootTableProvider.SubProviderEntry(DreamtinkerBlockLootTableProvider::new, LootContextParamSets.BLOCK)));
        //new LootTableProvider.SubProviderEntry(AdvancementLootTableProvider::new, LootContextParamSets.ADVANCEMENT_REWARD),
        //new LootTableProvider.SubProviderEntry(EntityLootTableProvider::new, LootContextParamSets.ENTITY)));
    }

}
