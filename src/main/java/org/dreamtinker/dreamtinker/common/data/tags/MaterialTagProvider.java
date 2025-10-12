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
            .add(moonlight_ice, nigrescence_antimony, metallivorous_stibium_lupus)
            .addOptional(etherium, nefarious, soul_etherium,
                         soul_stained_steel, malignant_pewter, malignant_gluttony);
        this.tag(TinkerTags.Materials.NETHER).add(star_regulus, nefarious);
        this.tag(TinkerTags.Materials.MELEE)
            .add(nigrescence_antimony, moonlight_ice, echo_alloy, metallivorous_stibium_lupus)
            .addOptional(nefarious, soul_etherium, spirit_fabric, soul_stained_steel, malignant_pewter, malignant_gluttony);
        this.tag(TinkerTags.Materials.HARVEST)
            .add(crying_obsidian)
            .addOptional(hallowed_gold);
        this.tag(TinkerTags.Materials.GENERAL)
            .add(valentinite)
            .addOptional(etherium, spirit_fabric);
        this.tag(TinkerTags.Materials.LIGHT)
            .add(nigrescence_antimony, echo_alloy)
            .addOptional(spirit_fabric, soul_stained_steel, nefarious, etherium, soul_etherium, metallivorous_stibium_lupus, star_regulus);
        this.tag(TinkerTags.Materials.BALANCED)
            .add(valentinite);
        this.tag(TinkerTags.Materials.HEAVY)
            .add(crying_obsidian)
            .addOptional(hallowed_gold);
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Material Tag Provider";
    }
}

