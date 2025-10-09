package org.dreamtinker.dreamtinker.tools.data.material;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialRenderInfoProvider;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider;

import javax.annotation.Nullable;

public class DreamtinkerMaterialRenderInfoProvider extends AbstractMaterialRenderInfoProvider {
    public DreamtinkerMaterialRenderInfoProvider(PackOutput packOutput, @Nullable AbstractMaterialSpriteProvider materialSprites, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, materialSprites, existingFileHelper);
    }

    @Override
    protected void addMaterialRenderInfo() {
        buildRenderInfo(DreamtinkerMaterialIds.echo_alloy).color(0xFF415099).fallbacks("crystal", "metal").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.moonlight_ice).color(0xFFFFFFFF).fallbacks("metal", "crystal").luminosity(15);
        buildRenderInfo(DreamtinkerMaterialIds.valentinite).color(0xFFF6E07E).fallbacks("crystal").luminosity(7);
        buildRenderInfo(DreamtinkerMaterialIds.nigrescence_antimony).color(0xFF332222).fallbacks("crystal").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.metallivorous_stibium_lupus).color(0xFFFDF8EB).fallbacks("metal").luminosity(9);
        buildRenderInfo(DreamtinkerMaterialIds.star_regulus).color(0xFF8C1F1F).fallbacks("metal").luminosity(15);
        buildRenderInfo(DreamtinkerMaterialIds.crying_obsidian).color(0xFF3B2754).fallbacks("rock").luminosity(3);

        buildRenderInfo(DreamtinkerMaterialIds.etherium).color(0xFF96E8E0).fallbacks("metal", "crystal").luminosity(15);
        buildRenderInfo(DreamtinkerMaterialIds.nefarious).color(0xFF8513A0).fallbacks("metal", "crystal", "gem").luminosity(8);
        buildRenderInfo(DreamtinkerMaterialIds.soul_etherium).color(0xFFC2A2C2).fallbacks("metal", "crystal", "gem").luminosity(15);

        buildRenderInfo(DreamtinkerMaterialIds.spirit_fabric).color(0xFF6E2CA3).fallbacks("cloth").luminosity(5);
        buildRenderInfo(DreamtinkerMaterialIds.hallowed_gold).color(0xFFB86F22).fallbacks("metal").luminosity(7);
        buildRenderInfo(DreamtinkerMaterialIds.mnemonic_fragment).color(0xFF7F08FC).fallbacks("gem").luminosity(9);
        buildRenderInfo(DreamtinkerMaterialIds.soul_stained_steel).color(0xFFBE86E9).fallbacks("metal").luminosity(12);
        buildRenderInfo(DreamtinkerMaterialIds.malignant_pewter).color(0xFFC9B7D6).fallbacks("metal").luminosity(15);

    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Material Info Provider";
    }
}
