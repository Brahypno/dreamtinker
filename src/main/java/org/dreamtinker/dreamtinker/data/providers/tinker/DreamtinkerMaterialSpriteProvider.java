package org.dreamtinker.dreamtinker.data.providers.tinker;

import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.data.DreamtinkerMaterialIds;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider;
import slimeknights.tconstruct.library.client.data.spritetransformer.GreyToSpriteTransformer;
import slimeknights.tconstruct.tools.stats.*;

public class DreamtinkerMaterialSpriteProvider extends AbstractMaterialSpriteProvider {
    @Override
    public @NotNull String getName() {
        return "Dreamtinker Material Sprite Provider";
    }

    @Override
    protected void addAllMaterials() {
        this.buildMaterial(DreamtinkerMaterialIds.echo_shard).ranged().meleeHarvest().armor().fallbacks("crystal", "metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addTexture(0, Dreamtinker.getLocation("material/echo_shard/gradient_echo_shard_0"))
                                                .addTexture(63, Dreamtinker.getLocation("material/echo_shard/gradient_echo_shard_1"))
                                                .addTexture(102, Dreamtinker.getLocation("material/echo_shard/gradient_echo_shard_2"))
                                                .addTexture(140, Dreamtinker.getLocation("material/echo_shard/gradient_echo_shard_3"))
                                                .addTexture(178, Dreamtinker.getLocation("material/echo_shard/gradient_echo_shard_4"))
                                                .addTexture(216, Dreamtinker.getLocation("material/echo_shard/gradient_echo_shard_5"))
                                                .addTexture(255, Dreamtinker.getLocation("material/echo_shard/gradient_echo_shard_6"))
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.moonlight_ice).statType(HeadMaterialStats.ID).repairKit().fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF081829)
                                                .addARGB(63, 0xFF0F2E52)
                                                .addARGB(102, 0xFF1C4A7B)
                                                .addARGB(140, 0xFF2D6EC0)
                                                .addARGB(178, 0xFF4990E4)
                                                .addARGB(216, 0xFFCFE6F9)
                                                .addARGB(255, 0xFFEFFAFF)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.valentinite)
            .statType(HandleMaterialStats.ID).statType(StatlessMaterialStats.BINDING.getIdentifier()).repairKit().maille().statType(LimbMaterialStats.ID)
            .fallbacks("crystal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF7A5C18)
                                                .addARGB(63, 0xFFBB8923)
                                                .addARGB(102, 0xFFE5AC2E)
                                                .addARGB(140, 0xFFEDC954)
                                                .addARGB(178, 0xFFF1D970)
                                                .addARGB(216, 0xFFF4E8A3)
                                                .addARGB(255, 0xFFFAF9E6)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.nigrescence_antimony)
            .meleeHarvest().plating().statType(StatlessMaterialStats.BOWSTRING)
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF060606)
                                                .addARGB(63, 0xFF0D0D0F)
                                                .addARGB(102, 0xFF1A1A1F)
                                                .addARGB(140, 0xFF26262B)
                                                .addARGB(178, 0xFF36363C)
                                                .addARGB(216, 0xFF51515A)
                                                .addARGB(255, 0xFF888890)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.metallivorous_stibium_lupus)
            .meleeHarvest().statType(GripMaterialStats.ID)
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFFE1B873)
                                                .addARGB(50, 0xFFE6C78E)
                                                .addARGB(100, 0xFFEBE3AB)
                                                .addARGB(150, 0xFFF3F0C7)
                                                .addARGB(175, 0xFFF8F6DC)
                                                .addARGB(200, 0xFFFBF9EF)
                                                .addARGB(220, 0xFFFCFBF7)
                                                .addARGB(240, 0xFFFEFCFD)
                                                .addARGB(255, 0xFFEFFAFF)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.star_regulus)
            .maille().armor().repairKit().statType(LimbMaterialStats.ID)
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF4B0000)
                                                .addARGB(63, 0xFF8C1F1F)
                                                .addARGB(102, 0xFFB33A3A)
                                                .addARGB(140, 0xFFD75C5C)
                                                .addARGB(178, 0xFFF08080)
                                                .addARGB(216, 0xFFFABBBB)
                                                .addARGB(255, 0xFFFFEDED)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.crying_obsidian)
            .ranged().meleeHarvest().armor()
            .fallbacks("rock")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF292828)
                                                .addARGB(63, 0xFF292825)
                                                .addARGB(102, 0xFF06030B)
                                                .addARGB(140, 0xFF100C1C)
                                                .addARGB(178, 0xFF524572)
                                                .addARGB(216, 0xFF3B2754)
                                                .addARGB(255, 0xFF5b3c82)
                                                .build());

        this.buildMaterial(DreamtinkerMaterialIds.etherium)
            .ranged().meleeHarvest().armor().statType(StatlessMaterialStats.BOWSTRING)
            .fallbacks("metal", "crystal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF8FE7E7)
                                                .addARGB(63, 0xFF98EEEE)
                                                .addARGB(102, 0xFF9CF0F0)
                                                .addARGB(140, 0xFF96E8E0)
                                                .addARGB(178, 0xFFB5F5ED)
                                                .addARGB(216, 0xFFC8FDF5)
                                                .addARGB(255, 0xFFCEFFF8)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.nefarious)
            .ranged().meleeHarvest().armor().statType(StatlessMaterialStats.BOWSTRING)
            .fallbacks("metal", "crystal", "gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF781190)
                                                .addARGB(63, 0xFF771191)
                                                .addARGB(102, 0xFFA01EAF)
                                                .addARGB(140, 0xFF760AB0)
                                                .addARGB(178, 0xFFB54ABC)
                                                .addARGB(216, 0xFFC77FCA)
                                                .addARGB(255, 0xFFC77FCA)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.soul_etherium)
            .ranged().meleeHarvest().armor().statType(StatlessMaterialStats.BOWSTRING)
            .fallbacks("gem", "metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF062E31)  // 深青
                                                .addARGB(63, 0xFF0C6D79)  // 深青→中青
                                                .addARGB(102, 0xFF1098AA)  // 中青
                                                .addARGB(140, 0xFF15BFCF)  // 亮青
                                                .addARGB(178, 0xFF8656DF)  // 高饱和紫
                                                .addARGB(216, 0xFFF84CB7)  // 亮粉
                                                .addARGB(255, 0xFFFFD5EC)  // 粉白高光
                                                .build());
    }
}
