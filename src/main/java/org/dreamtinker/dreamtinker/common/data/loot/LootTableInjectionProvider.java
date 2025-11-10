package org.dreamtinker.dreamtinker.common.data.loot;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.loot.AbstractLootTableInjectionProvider;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.loot.AddToolDataFunction;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.RandomMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

public class LootTableInjectionProvider extends AbstractLootTableInjectionProvider {
    public LootTableInjectionProvider(PackOutput packOutput) {
        super(packOutput, Dreamtinker.MODID);
    }

    @Override
    protected void addTables() {
        IJsonPredicate<MaterialVariantId> includeInLoot = MaterialPredicate.tag(TinkerTags.Materials.EXCLUDE_FROM_LOOT).inverted();
        RandomMaterial random = RandomMaterial.random().allowHidden().material(includeInLoot).build();
        AddToolDataFunction.Builder ancientToolData3 = AddToolDataFunction.builder().addMaterial(random).addMaterial(random).addMaterial(random);
        injectChest("ancient_city")
                .addToPool("main", LootItem.lootTableItem(DreamtinkerTools.silence_glove.get())
                                           .setWeight(6)
                                           .apply(ancientToolData3)
                                           .build());
        inject("trail_ruins_rare", "archaeology/trail_ruins_rare")
                .addToPool("main", LootItem.lootTableItem(DreamtinkerTools.silence_glove.get())
                                           .setWeight(5)
                                           .apply(ancientToolData3)
                                           .build());
        inject("trail_ruins_common", "archaeology/trail_ruins_common")
                .addToPool("main", LootItem.lootTableItem(DreamtinkerTools.silence_glove.get())
                                           .setWeight(3)
                                           .apply(ancientToolData3)
                                           .build());
        inject("fishing_treasure", new ResourceLocation("gameplay/fishing/treasure"))
                .addToPool("main", LootItem.lootTableItem(DreamtinkerCommon.rainbow_honey.get())
                                           .setWeight(1) // all treasure from fishing is the same weight
                                           .build());
    }

    @Override
    public @NotNull String getName() {
        return "Dream Tinkers' Loot Table Injections";
    }
}
