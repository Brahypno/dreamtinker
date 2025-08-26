package org.dreamtinker.dreamtinker.data.providers;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.data.DreamtinkerMaterialIds;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractMaterialTagProvider;

public class DreamtinkerMaterialTagProvider extends AbstractMaterialTagProvider {
    public DreamtinkerMaterialTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, Dreamtinker.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(TinkerTags.Materials.EXCLUDE_FROM_LOOT)
            .add(DreamtinkerMaterialIds.moonlight_ice, DreamtinkerMaterialIds.nigrescence_antimony, DreamtinkerMaterialIds.metallivorous_stibium_lupus,
                 DreamtinkerMaterialIds.nigrescence_antimony);
        this.tag(TinkerTags.Materials.NETHER).add(DreamtinkerMaterialIds.star_regulus);
    }

    @Override
    public String getName() {
        return "Dreamtinker Material Tag Provider";
    }
}

