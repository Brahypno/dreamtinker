package org.dreamtinker.dreamtinker.tools.data.sprite;

import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
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
        this.buildMaterial(DreamtinkerMaterialIds.echo_alloy).ranged().meleeHarvest().armor().fallbacks("crystal", "metal")
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
        this.buildMaterial(DreamtinkerMaterialIds.larimar)
            .ranged().meleeHarvest()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF0E4A66)  // 深影：冷青蓝
                                                .addARGB(63, 0xFF1F6F88)  // 深蓝向青过渡
                                                .addARGB(102, 0xFF2E90A5)  // 中深段
                                                .addARGB(140, 0xFF51BACE)  // 标志性Larimar蓝
                                                .addARGB(178, 0xFF86DDE2)  // 亮蓝青
                                                .addARGB(216, 0xFFC7F7F6)  // 近白高光
                                                .addARGB(255, 0xFFFFFFFF)  // 纯白镜面
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.amber)
            .ranged().meleeHarvest().armor()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF6D2901)  // 深琥珀棕（最暗阴影）
                                                .addARGB(63, 0xFFDA5200)  // 暗橙过渡
                                                .addARGB(102, 0xFFED7000)  // 亮橙中深
                                                .addARGB(140, 0xFFF27A00)  // 琥珀橙
                                                .addARGB(178, 0xFFFE9300)  // 明亮琥珀
                                                .addARGB(216, 0xFFFFAC00)  // 金琥珀高光
                                                .addARGB(255, 0xFFFFDA25)  // 近金黄高光
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.half_rotten_homunculus)
            .statType(HandleMaterialStats.ID).statType(StatlessMaterialStats.BINDING.getIdentifier()).maille().statType(StatlessMaterialStats.BOWSTRING)
            .repairKit()
            .fallbacks("bone")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF000000)  // 最暗凝块
                                                .addARGB(63, 0xFF220000)  // 深红黑
                                                .addARGB(102, 0xFF280000)  // 暗红
                                                .addARGB(140, 0xFF360100)  // 暗棕红
                                                .addARGB(178, 0xFF550703)  // 浓血红
                                                .addARGB(216, 0xFFCF703E)  // 血色高光（偏棕橙）
                                                .addARGB(255, 0xFFFBF2DA)  // 极亮高光/反光
                                                .build());
        addELMaterials();
        addMalumMaterials();
    }

    protected void addELMaterials() {
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

    protected void addMalumMaterials() {
        this.buildMaterial(DreamtinkerMaterialIds.spirit_fabric)
            .statType(StatlessMaterialStats.BINDING).armor().statType(StatlessMaterialStats.BOWSTRING)
            .fallbacks("cloth")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF0D0E0F)
                                                .addARGB(63, 0xFF1A1B20)  // 暗灰
                                                .addARGB(102, 0xFF1A1B20)
                                                .addARGB(140, 0xFF1A1B20)
                                                .addARGB(178, 0xFF442B4C)
                                                .addARGB(216, 0xFF5A3670)
                                                .addARGB(255, 0xFF9F32BC)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.hallowed_gold)
            .statType(HeadMaterialStats.ID).ranged().statType(StatlessMaterialStats.BOWSTRING)
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF1E0F07)  // 极暗棕，底部阴影
                                                .addARGB(63, 0xFF5A3C18)  // 暗金棕
                                                .addARGB(102, 0xFFB86F22)  // 橙金中层
                                                .addARGB(140, 0xFFE5B12D)  // 明金层
                                                .addARGB(178, 0xFFF4C942)  // 高亮黄
                                                .addARGB(216, 0xFFFCE14B)  // 亮面反光
                                                .addARGB(255, 0xFFFFFAC2)  // 最亮反光白
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.mnemonic)
            .statType(StatlessMaterialStats.BINDING).repairKit()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF3B2542)  // 深紫黑（阴影）
                                                .addARGB(63, 0xFF401FAD)  // 暗紫
                                                .addARGB(102, 0xFF7F08FC)  // 深靛紫
                                                .addARGB(140, 0xFFFC4EE7)  // 亮紫
                                                .addARGB(178, 0xFFFCA87C)  // 品红高光
                                                .addARGB(216, 0xFFFCF4D3)  // 暖橙亮面
                                                .addARGB(255, 0xFFFCF4D3)  // 乳白反光
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.auric)
            .statType(StatlessMaterialStats.BINDING).repairKit()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF582F55)  // 深紫外缘
                                                .addARGB(63, 0xFF973B41)  // 炽红暗部
                                                .addARGB(102, 0xFFE19A2A)  // 橙金过渡（开始转金）
                                                .addARGB(140, 0xFFF0C24A)  // 亮金
                                                .addARGB(178, 0xFFF6DE6A)  // 浅金黄
                                                .addARGB(216, 0xFFFFF2B8)  // 淡金高光
                                                .addARGB(255, 0xFFFFFCF0)  // 微黄近白高光
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.soul_stained_steel)
            .meleeHarvest().ranged().armor()
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF0B0710)  // 最暗：近黑紫
                                                .addARGB(63, 0xFF181022)  // 深阴影紫
                                                .addARGB(102, 0xFF3A1F57)  // 深紫
                                                .addARGB(140, 0xFF5E2F8F)  // 中紫
                                                .addARGB(178, 0xFF8A57C8)  // 明亮紫
                                                .addARGB(216, 0xFFBE86E9)  // 淡粉紫高光
                                                .addARGB(255, 0xFFF3C9FF)  // 最亮高光（近白粉）
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.malignant_lead)
            .statType(HandleMaterialStats.ID)
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF39374A)  // 极深蓝灰（外缘阴影）
                                                .addARGB(63, 0xFF4E4E61)  // 深段过渡：冷灰蓝
                                                .addARGB(102, 0xFF626477)  // 钢蓝中深
                                                .addARGB(140, 0xFF737B95)  // 向冰蓝过渡
                                                .addARGB(178, 0xFF97A8BF)  // 冰蓝中亮
                                                .addARGB(216, 0xFFAFC5D5)  // 淡冰蓝
                                                .addARGB(255, 0xFFE8F3F4)  // 高光近白
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.malignant_pewter)
            .meleeHarvest().armor()
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF120B16)  // 最暗：深紫近黑
                                                .addARGB(63, 0xFF24152A)  // 暗紫阴影
                                                .addARGB(102, 0xFF3A2548)  // 深紫
                                                .addARGB(140, 0xFF5E4A7A)  // 中紫
                                                .addARGB(178, 0xFF8F79A8)  // 淡紫中亮
                                                .addARGB(216, 0xFFC9B7D6)  // 柔和高光
                                                .addARGB(255, 0xFFF3EAF7)  // 最亮高光（近白紫）
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.malignant_gluttony)
            .meleeHarvest().armor()
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF142728)  // 最暗：墨青黑（阴影边）
                                                .addARGB(63, 0xFF285052)  // 深青
                                                .addARGB(102, 0xFF285052)  // 深青（过渡保持）
                                                .addARGB(140, 0xFF5F6543)  // 橄榄灰（中段暗部）
                                                .addARGB(178, 0xFF1FA60A)  // 荧绿点缀（贴图里有少量亮绿像素）
                                                .addARGB(216, 0xFF52A5A9)  // 亮青
                                                .addARGB(255, 0xFFFFEAF7)  // 最亮高光：偏粉白
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.tainted)
            .shieldCore()
            .fallbacks("rock")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF363139)  // 深灰紫
                                                .addARGB(63, 0xFF4A434F)
                                                .addARGB(102, 0xFF5E5663)  // 中段主色
                                                .addARGB(140, 0xFF736A79)
                                                .addARGB(178, 0xFF8E8296)  // 浅灰紫
                                                .addARGB(216, 0xFFB0A6BA)
                                                .addARGB(255, 0xFFD9D0E1)  // 高光
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.twisted)
            .shieldCore()
            .fallbacks("rock")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF0F111A)  // 极深影：蓝黑偏紫
                                                .addARGB(63, 0xFF1A1A26)  // 深段
                                                .addARGB(102, 0xFF262438)  // 深紫灰
                                                .addARGB(140, 0xFF332E48)  // 中深段：紫影
                                                .addARGB(178, 0xFF433659)  // 中段主色：冷紫
                                                .addARGB(216, 0xFF58466F)  // 亮部：灰紫
                                                .addARGB(255, 0xFF7B5D95)  // 高光：淡紫
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.refined)
            .shieldCore()
            .fallbacks("rock")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF30253B)  // 极深影：冷紫黑  (48,37,59)
                                                .addARGB(63, 0xFF433048)  // 深段过渡：暗紫   (~67,48,72)
                                                .addARGB(102, 0xFF4D3A54)  // 深紫灰         (77,58,84)
                                                .addARGB(140, 0xFF80417B)  // 中段主色：紫绛 (128,65,123)
                                                .addARGB(178, 0xFF9A39A3)  // 亮紫/洋红      (154,57,163)
                                                .addARGB(216, 0xFFAE3CB5)  // 高亮过渡       (~174,60,181)
                                                .addARGB(255, 0xFFC13FC7)  // 高光：亮洋红紫 (193,63,199)
                                                .build());
    }
}
