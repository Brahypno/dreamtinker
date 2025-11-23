package org.dreamtinker.dreamtinker.tools.data.material;

import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.library.data.material.AbstractMaterialDataProvider;

public class DreamtinkerMaterialDataProvider extends AbstractMaterialDataProvider {
    public DreamtinkerMaterialDataProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void addMaterials() {
        addMaterial(DreamtinkerMaterialIds.echo_alloy, 4, 35, false, false, null);
        addMaterial(DreamtinkerMaterialIds.moonlight_ice, 3, 35, false, false, null);
        addMaterial(DreamtinkerMaterialIds.valentinite, 2, 5, true, false, null);
        addMaterial(DreamtinkerMaterialIds.nigrescence_antimony, 3, 23, false, false, null);
        addMaterial(DreamtinkerMaterialIds.nigrescence_string, 3, 23, false, false, null);
        addMaterial(DreamtinkerMaterialIds.metallivorous_stibium_lupus, 5, 45, false, false, null);
        addMaterial(DreamtinkerMaterialIds.star_regulus, 6, 50, true, false, null);
        addMaterial(DreamtinkerMaterialIds.crying_obsidian, 3, 30, false, false, null);
        addMaterial(DreamtinkerMaterialIds.larimar, 3, 20, true, false, null);
        addMaterial(DreamtinkerMaterialIds.amber, 2, 10, false, false, null);
        addMaterial(DreamtinkerMaterialIds.half_rotten_homunculus, 3, 20, false, false, null);
        addMaterial(DreamtinkerMaterialIds.half_rotten_string, 3, 20, false, false, null);
        addMaterial(DreamtinkerMaterialIds.desire_gem, 4, 35, false, false, null);
        addMaterial(DreamtinkerMaterialIds.despair_gem, 6, 75, false, false, null);
        addMaterial(DreamtinkerMaterialIds.shadowskin, 4, 35, false, false, null);
        addMaterial(DreamtinkerMaterialIds.soul_steel, 3, 25, false, false, null);
        addMaterial(DreamtinkerMaterialIds.rainbow_honey_crystal, 3, 25, false, false, null);
        addMaterial(DreamtinkerMaterialIds.black_sapphire, 4, 35, false, false, null);
        addMaterial(DreamtinkerMaterialIds.scolecite, 3, 20, false, false, null);
        addMaterial(DreamtinkerMaterialIds.shiningFlint, 3, 20, true, false, null);
        addMaterial(DreamtinkerMaterialIds.orichalcum, 2, 5, false, false, null);
        addMaterial(DreamtinkerMaterialIds.cold_iron, 2, 6, false, false, null);
        addMaterial(DreamtinkerMaterialIds.shadowSilver, 2, 6, false, false, tagFilled(Dreamtinker.forgeItemTag("ingots/silver")));


        addMaterial(DreamtinkerMaterialIds.etherium, 4, 40, false, false, modLoaded("enigmaticlegacy"));
        addMaterial(DreamtinkerMaterialIds.nefarious, 4, 40, false, false, modLoaded("enigmaticlegacy"));
        addMaterial(DreamtinkerMaterialIds.soul_etherium, 5, 50, false, false, modLoaded("enigmaticlegacy"));

        addMaterial(DreamtinkerMaterialIds.spirit_fabric, 2, 10, true, false, modLoaded("malum"));
        addMaterial(DreamtinkerMaterialIds.hallowed_gold, 2, 15, true, false, modLoaded("malum"));
        addMaterial(DreamtinkerMaterialIds.mnemonic_auric, 2, 30, false, true, modLoaded("malum"));
        addMaterial(DreamtinkerMaterialIds.soul_stained_steel, 3, 30, false, false, modLoaded("malum"));
        addMaterial(DreamtinkerMaterialIds.malignant_lead, 3, 35, false, true, modLoaded("malum"));
        addMaterial(DreamtinkerMaterialIds.malignant_pewter, 4, 45, false, true, modLoaded("malum"));
        addMaterial(DreamtinkerMaterialIds.malignant_gluttony, 5, 50, false, true, modLoaded("malum"));
        addMaterial(DreamtinkerMaterialIds.soul_rock, 2, 10, true, false, modLoaded("malum"));
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Material Data Provider";
    }

    public static ICondition modLoaded(String modId) {
        return new OrCondition(ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS, new ModLoadedCondition(modId));
    }

    public static ICondition tagFilled(TagKey<Item> tagKey) {
        return new OrCondition(ConfigEnabledCondition.FORCE_INTEGRATION_MATERIALS, new TagFilledCondition<>(tagKey));
    }
}
