package org.dreamtinker.dreamtinker.data.providers.tinker;

import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import org.dreamtinker.dreamtinker.data.DreamtinkerMaterialIds;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;

public class DreamtinkerMaterialDataProvider extends AbstractMaterialDataProvider {
    public DreamtinkerMaterialDataProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addMaterials() {
        addMaterial(DreamtinkerMaterialIds.echo_shard, 4, 35, false, false, null);
        addMaterial(DreamtinkerMaterialIds.moonlight_ice, 4, 35, false, false, null);
        addMaterial(DreamtinkerMaterialIds.valentinite, 3, 16, true, false, null);
        addMaterial(DreamtinkerMaterialIds.nigrescence_antimony, 3, 23, false, false, null);
        addMaterial(DreamtinkerMaterialIds.metallivorous_stibium_lupus, 6, 45, false, false, null);
        addMaterial(DreamtinkerMaterialIds.star_regulus, 7, 50, true, false, null);
        addMaterial(DreamtinkerMaterialIds.crying_obsidian, 3, 30, false, false, null);

        addMaterial(DreamtinkerMaterialIds.etherium, 4, 40, false, false, modLoaded("enigmaticlegacy"));
        addMaterial(DreamtinkerMaterialIds.nefarious, 4, 40, false, false, modLoaded("enigmaticlegacy"));
        addMaterial(DreamtinkerMaterialIds.soul_etherium, 5, 50, false, false, modLoaded("enigmaticlegacy"));
    }

    @Override
    public String getName() {
        return "Dreamtinker Material Data Provider";
    }

    public static ICondition modLoaded(String modId) {
        return new OrCondition(ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS, new ModLoadedCondition(modId));
    }

    public static ICondition tagFilled(TagKey<Item> tagKey) {
        return new OrCondition(ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS, new TagFilledCondition<>(tagKey));
    }
}
