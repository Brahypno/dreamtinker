package org.dreamtinker.dreamtinker.common.data.tags;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractMaterialTagProvider;

public class MaterialTagProvider extends AbstractMaterialTagProvider {
    public MaterialTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, Dreamtinker.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(TinkerTags.Materials.EXCLUDE_FROM_LOOT)
            .add(DreamtinkerMaterialIds.moonlight_ice, DreamtinkerMaterialIds.nigrescence_antimony, DreamtinkerMaterialIds.metallivorous_stibium_lupus,
                 DreamtinkerMaterialIds.nigrescence_antimony)
            .addOptional(DreamtinkerMaterialIds.etherium, DreamtinkerMaterialIds.nefarious, DreamtinkerMaterialIds.soul_etherium,
                         DreamtinkerMaterialIds.soul_stained_steel, DreamtinkerMaterialIds.malignant_pewter, DreamtinkerMaterialIds.malignant_gluttony);
        this.tag(TinkerTags.Materials.NETHER).add(DreamtinkerMaterialIds.star_regulus, DreamtinkerMaterialIds.nefarious);
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Material Tag Provider";
    }
}

