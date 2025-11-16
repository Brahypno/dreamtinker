package org.dreamtinker.dreamtinker.common.data.tags;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractMaterialTagProvider;

import static org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds.*;

public class MaterialTagProvider extends AbstractMaterialTagProvider {
    public MaterialTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, Dreamtinker.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(TinkerTags.Materials.EXCLUDE_FROM_LOOT)
            .add(moonlight_ice, nigrescence_antimony, metallivorous_stibium_lupus, echo_alloy, star_regulus, half_rotten_homunculus, desire_gem, despair_gem)
            .addOptional(etherium, nefarious, soul_etherium,
                         soul_stained_steel, malignant_pewter, malignant_gluttony, malignant_lead);

        this.tag(TinkerTags.Materials.NETHER).add(nefarious, amber, scolecite);
        this.tag(TinkerTags.Materials.BARTERED).add(amber, rainbow_honey_crystal, scolecite);
        this.tag(TinkerTags.Materials.MELEE)
            .add(crying_obsidian, nigrescence_antimony, moonlight_ice, echo_alloy, metallivorous_stibium_lupus, amber, half_rotten_homunculus, desire_gem,
                 despair_gem, scolecite, shiningFlint)
            .addOptional(nefarious, soul_etherium, spirit_fabric, soul_stained_steel, malignant_pewter, malignant_gluttony);
        this.tag(TinkerTags.Materials.HARVEST)
            .add(larimar, rainbow_honey_crystal)
            .addOptional(hallowed_gold);
        this.tag(TinkerTags.Materials.GENERAL)
            .add(valentinite, black_sapphire)
            .addOptional(etherium, spirit_fabric);
        this.tag(TinkerTags.Materials.LIGHT)
            .add(nigrescence_antimony, echo_alloy, larimar, half_rotten_homunculus, desire_gem)
            .addOptional(spirit_fabric, soul_stained_steel, nefarious, etherium, soul_etherium, metallivorous_stibium_lupus, star_regulus);
        this.tag(TinkerTags.Materials.BALANCED)
            .add(valentinite, black_sapphire, scolecite, shiningFlint);
        this.tag(TinkerTags.Materials.HEAVY)
            .add(crying_obsidian, amber)
            .addOptional(hallowed_gold);
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Material Tag Provider";
    }
}

