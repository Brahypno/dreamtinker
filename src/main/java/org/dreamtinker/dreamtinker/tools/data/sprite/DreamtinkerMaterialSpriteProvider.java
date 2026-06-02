package org.dreamtinker.dreamtinker.tools.data.sprite;

import com.sammy.malum.core.systems.spirit.MalumSpiritType;
import com.sammy.malum.registry.common.SpiritTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider;
import slimeknights.tconstruct.library.client.data.spritetransformer.FramesSpriteTransformer;
import slimeknights.tconstruct.library.client.data.spritetransformer.GreyToColorMapping;
import slimeknights.tconstruct.library.client.data.spritetransformer.GreyToSpriteTransformer;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.tools.stats.*;

import static slimeknights.tconstruct.tools.data.sprite.TinkerPartSpriteProvider.INGOT;
import static slimeknights.tconstruct.tools.data.sprite.TinkerPartSpriteProvider.STORAGE_BLOCK;

public class DreamtinkerMaterialSpriteProvider extends AbstractMaterialSpriteProvider {
    @Override
    public @NotNull String getName() {
        return "Dreamtinker Material Sprite Provider";
    }

    @Override
    protected void addAllMaterials() {
        this.buildMaterial(DreamtinkerMaterialIds.echo_alloy)
            .ranged().meleeHarvest().armor().fallbacks("crystal", "metal").statType(STORAGE_BLOCK)
            .transformer(GreyToSpriteTransformer.builderFromBlack()
                                                .addARGB(63, 0xFF000711)
                                                .addARGB(102, 0xFF04192A)
                                                .addARGB(140, 0xFF0A344A)
                                                .addARGB(178, 0xFF1A5C69)
                                                .addARGB(216, 0xFF48BEB3)
                                                .addARGB(255, 0xFFD8FFF6)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.moonlight_ice)
            .meleeHarvest().fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF081829)
                                                .addARGB(63, 0xFF0F2E52)
                                                .addARGB(102, 0xFF1C4A7B)
                                                .addARGB(140, 0xFF2D6EC0)
                                                .addARGB(178, 0xFF4990E4)
                                                .addARGB(216, 0xFFCFE6F9)
                                                .addARGB(255, 0xFFEFFAFF)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.cryo_serpent_shift)
            .meleeHarvest().armor().ranged().fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builderFromBlack()
                                                .addARGB(63, 0xFF1B153F)
                                                .addARGB(102, 0xFF302A72)
                                                .addARGB(140, 0xFF5148A8)
                                                .addARGB(178, 0xFF7B73D6)
                                                .addARGB(216, 0xFFB4B3F2)
                                                .addARGB(255, 0xFFE8ECFF)
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
            .meleeHarvest().plating()
            .fallbacks("crystal", "gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF060606)
                                                .addARGB(63, 0xFF0D0D0F)
                                                .addARGB(102, 0xFF1A1A1F)
                                                .addARGB(140, 0xFF26262B)
                                                .addARGB(178, 0xFF36363C)
                                                .addARGB(216, 0xFF51515A)
                                                .addARGB(255, 0xFF888890)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.nigrescence_string)
            .statType(StatlessMaterialStats.BOWSTRING).repairKit()
            .fallbacks("crystal", "gem")
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
            .meleeHarvest().statType(GripMaterialStats.ID).statType(STORAGE_BLOCK)
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
            .transformer(GreyToSpriteTransformer.builderFromBlack()
                                                .addARGB(25, 0xFF2A0204)
                                                .addTexture(26, Dreamtinker.getLocation("generator/star_regulus"))
                                                .addTexture(63, Dreamtinker.getLocation("generator/star_regulus"))
                                                .addARGB(140, 0xFF3A0105)
                                                .addARGB(178, 0xFFA30B13)
                                                .addARGB(216, 0xFFFF3442)
                                                .addARGB(255, 0xFFFFD8DD)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.crying_obsidian)
            .ranged().meleeHarvest().armor()
            .fallbacks("rock")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF05030A)
                                                .addARGB(63, 0xFF0B0713)
                                                .addARGB(102, 0xFF160B27)
                                                .addARGB(140, 0xFF251241)
                                                .addARGB(178, 0xFF40206B)
                                                .addARGB(216, 0xFF6730A8)
                                                .addARGB(255, 0xFF9650E6)
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
            .ranged().meleeHarvest().armor().statType(STORAGE_BLOCK)
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
            .statType(HandleMaterialStats.ID).statType(StatlessMaterialStats.BINDING.getIdentifier()).maille().repairKit()
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
        this.buildMaterial(DreamtinkerMaterialIds.half_rotten_string)
            .statType(StatlessMaterialStats.BOWSTRING).repairKit()
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
        this.buildMaterial(DreamtinkerMaterialIds.desire_gem)
            .meleeHarvest().armor()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builderFromBlack()
                                                .addARGB(63, 0xFF160B24)
                                                .addARGB(102, 0xFF2A1644)
                                                .addARGB(140, 0xFF48236F)
                                                .addARGB(178, 0xFF6D3BA0)
                                                .addARGB(216, 0xFFB38BE8)
                                                .addARGB(255, 0xFFEDE4FF)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.musou)
            .meleeHarvest().armor()
            .fallbacks("gem")
            .transformer(new FramesSpriteTransformer(
                    Dreamtinker.getLocation("generator/desire"),
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(63, 0xFF24113A)
                                           .addARGB(102, 0xFF4A2380)
                                           .addARGB(140, 0xFF7B3FD0)
                                           .addARGB(178, 0xFFC26DFF)
                                           .addARGB(216, 0xFFE9C8FF)
                                           .addARGB(255, 0xFFFFFFFF)
                                           .build(),

                    // Frame 2
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(63, 0xFF2E1648)
                                           .addARGB(102, 0xFF5F2AA0)
                                           .addARGB(140, 0xFF9A55F0)
                                           .addARGB(178, 0xFFD996FF)
                                           .addARGB(216, 0xFFF4DEFF)
                                           .addARGB(255, 0xFFFFFFFF)
                                           .build()
            ));
        this.buildMaterial(DreamtinkerMaterialIds.despair_gem)
            .meleeHarvest().armor()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0x00000000)
                                                .addARGB(63, 0xFF120001)  // 近黑红
                                                .addARGB(102, 0xFF2B0003)  // 深血红
                                                .addARGB(140, 0xFF5A000B)  // 深红主体
                                                .addARGB(178, 0xFF8F0018)  // 鲜红
                                                .addARGB(216, 0xFFC70022)  // 高饱和亮红
                                                .addARGB(255, 0xFFFF1A1A)  // 极亮纯红高光（仍是红，不偏橙）
                                                .build());

        buildMaterial(DreamtinkerMaterialIds.shadowskin)
                .fallbacks("cloth")
                .cuirass().maille()
                .colorMapper(GreyToColorMapping.builderFromBlack()
                                               .addARGB(63, 0xFF050505)
                                               .addARGB(102, 0xFF0A0A0A)
                                               .addARGB(140, 0xFF111111)
                                               .addARGB(178, 0xFF1A1A1A)
                                               .addARGB(216, 0xFF262626)
                                               .addARGB(255, 0xFF3A3A3A)
                                               .build());
        this.buildMaterial(DreamtinkerMaterialIds.soul_steel)
            .meleeHarvest().armor().ranged()
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF1A202B)
                                                .addARGB(102, 0xFF232C3A)
                                                .addARGB(140, 0xFF2E3848)
                                                .addARGB(178, 0xFF394458)
                                                .addARGB(216, 0xFF45556C)
                                                .addARGB(255, 0xFF87C7FF)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.whimsyGold)
            .meleeHarvest()
            .fallbacks("crystal", "gem", "metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF05040A)
                                                .addARGB(102, 0xFF181020)
                                                .addARGB(140, 0xFF38284A)
                                                .addARGB(178, 0xFF817289)
                                                .addARGB(216, 0xFFC29A52)
                                                .addARGB(255, 0xFFF2EDE2)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.rainbow_honey_crystal)
            .meleeHarvest().armor()
            .fallbacks("crystal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF6A6A1A)
                                                .addARGB(102, 0xFF848A1F)
                                                .addARGB(140, 0xFF9CAA28)
                                                .addARGB(178, 0xFFB9C532)
                                                .addARGB(216, 0xFFD3E03B)
                                                .addARGB(255, 0xFFEAF748)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.black_sapphire)
            .meleeHarvest().armor().ranged().statType(STORAGE_BLOCK)
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF0A0B0E)
                                                .addARGB(102, 0xFF12151A)
                                                .addARGB(140, 0xFF1A1F26)
                                                .addARGB(178, 0xFF26303A)
                                                .addARGB(216, 0xFF374351)
                                                .addARGB(255, 0xFF596678)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.scolecite)
            .meleeHarvest().armor().ranged().shieldCore()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF6C6E73)
                                                .addARGB(102, 0xFF87898C)
                                                .addARGB(140, 0xFFA3A3A1)
                                                .addARGB(178, 0xFFBCBAB6)
                                                .addARGB(216, 0xFFE6E7E5)
                                                .addARGB(255, 0xFFFCFCFA)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.PermanenceScale)
            .meleeHarvest().armor().ranged().shieldCore()
            .fallbacks("gem")
            .transformer(new FramesSpriteTransformer(
                    Dreamtinker.getLocation("generator/ruin_steel"),//This seems could be generic
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(25, 0xFF0B0503)
                                           .addARGB(26, 0xFF120804)
                                           .addARGB(102, 0xFF3A2410)
                                           .addARGB(140, 0xFF553817)
                                           .addARGB(178, 0xFF6B4F25)
                                           .addARGB(216, 0xFF88733E)
                                           .addARGB(255, 0xFFA89561)
                                           .build(),

                    // Frame 2
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(25, 0xFF100704)
                                           .addARGB(26, 0xFF1B0D06)
                                           .addARGB(102, 0xFF4B3115)
                                           .addARGB(140, 0xFF6B4B20)
                                           .addARGB(178, 0xFF84622F)
                                           .addARGB(216, 0xFFA28A4B)
                                           .addARGB(255, 0xFFC0AD76)
                                           .build()
            ));
        this.buildMaterial(DreamtinkerMaterialIds.PermanenceWing)
            .meleeHarvest().armor().ranged().shieldCore()
            .fallbacks("gem")
            .transformer(new FramesSpriteTransformer(
                    Dreamtinker.getLocation("generator/ruin_steel"),//This seems could be generic
                    // Frame 1：敛羽，深青、冷暗
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(25, 0xFF030A0D)
                                           .addARGB(26, 0xFF061417)
                                           .addARGB(102, 0xFF123F47)
                                           .addARGB(140, 0xFF2E6E75)
                                           .addARGB(178, 0xFF6FA19E)
                                           .addARGB(216, 0xFFAFC7BC)
                                           .addARGB(255, 0xFFD9DCCB)
                                           .build(),

                    // Frame 2：展翼，青白翻光、月白明显抬起
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(25, 0xFF0A1D20)
                                           .addARGB(26, 0xFF164047)
                                           .addARGB(102, 0xFF3A8C92)
                                           .addARGB(140, 0xFF8CC3BA)
                                           .addARGB(178, 0xFFCFE2D2)
                                           .addARGB(216, 0xFFF0EEDC)
                                           .addARGB(255, 0xFFFFF8E8)
                                           .build()
            ));

        this.buildMaterial(DreamtinkerMaterialIds.shiningFlint)
            .meleeHarvest().ranged().arrowHead()
            .fallbacks("crystal", "rock", "stick")
            .colorMapper(GreyToColorMapping.builderFromBlack()
                                           .addARGB(63, 0xFF3C96B9)
                                           .addARGB(102, 0xFFA8D0D9)
                                           .addARGB(140, 0x00000000)
                                           .addARGB(216, 0x00000000)
                                           .addARGB(255, 0xFFD0EAE9).build());

        this.buildMaterial(DreamtinkerMaterialIds.orichalcum)
            .meleeHarvest().armor().ranged().shieldCore()
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF552060) // 极暗深梅紫
                                                .addARGB(63, 0xFF6D2975) // 暗紫阴影
                                                .addARGB(102, 0xFF8A3FA8) // 中暗紫
                                                .addARGB(140, 0xFFA25BCC) // 偏亮紫
                                                .addARGB(178, 0xFF5EC97F) // 亮绿（主体高光起点）
                                                .addARGB(216, 0xFFB872E8) // 明亮紫（略带粉光）
                                                .addARGB(255, 0xFFCB9CFF) // 顶级高光：淡紫反光
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.cold_iron)
            .meleeHarvest().armor().ranged().shieldCore()
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF2C488F) // 最暗：偏深的冷蓝
                                                .addARGB(63, 0xFF3B5B97) // 深蓝阴影
                                                .addARGB(102, 0xFF4489CC) // 中亮冰蓝（主体色偏暗）
                                                .addARGB(140, 0xFFA5E2E6) // 浅冰蓝高光
                                                .addARGB(178, 0xFFAAEAEE) // 更亮的蓝白高光
                                                .addARGB(216, 0xFFB0F1F5) // 接近白的冷光
                                                .addARGB(255, 0xFFFDFDFD) // 几乎纯白的最强反光
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.shadowSilver)
            .meleeHarvest().armor().ranged().shieldCore()
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF7C6220)  // 最深的暖金黄，偏暗月影
                                                .addARGB(63, 0xFF987A2C)  // 稍亮一点的金黄
                                                .addARGB(102, 0xFFB29444)  // 中等亮度的暖月黄
                                                .addARGB(140, 0xFFCAAC60)  // 偏亮的浅金月光
                                                .addARGB(178, 0xFFDCC484)  // 柔和的淡黄月辉
                                                .addARGB(216, 0xFFEAD6A4)  // 乳黄色月白
                                                .addARGB(255, 0xFFFCF4E8)  // 接近纯白的月光高光
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.TransmutationGold)
            .meleeHarvest().armor().ranged().shieldCore()
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFFD2501E)  // 更深的红橙暗部
                                                .addARGB(102, 0xFFDE6428)  // 中暗橙红
                                                .addARGB(140, 0xFFE87E3C)  // 主体橙红
                                                .addARGB(178, 0xFFF29A54)  // 偏亮橙金
                                                .addARGB(216, 0xFFEBB46D)  // 保持原亮金
                                                .addARGB(255, 0xFFFFE4B0)  // 保持原高光
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.AtonementSilver)
            .ranged().meleeHarvest().armor().statType(INGOT).statType(STORAGE_BLOCK)
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF3A2D2A)
                                                .addARGB(102, 0xFF6A4938)
                                                .addARGB(140, 0xFFA96D3F)
                                                .addARGB(178, 0xFFD49A58)
                                                .addARGB(216, 0xFFC9C8C0)
                                                .addARGB(255, 0xFFFFF1C8)
                                                .build());

        this.buildMaterial(DreamtinkerMaterialIds.ArcaneGold)
            .meleeHarvest().armor().ranged()
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF5D232C)
                                                .addARGB(102, 0xFF7E3230)
                                                .addARGB(140, 0xFFB96843)
                                                .addARGB(178, 0xFFEDAE66)
                                                .addARGB(216, 0xFFF7D88E)
                                                .addARGB(255, 0xFFFBFCF2)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.Utherium)
            .meleeHarvest().armor().ranged().arrowHead()
            .fallbacks("gem", "metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF220F17)
                                                .addARGB(63, 0xFF451B1F)
                                                .addARGB(102, 0xFF682727)
                                                .addARGB(140, 0xFF983630)
                                                .addARGB(178, 0xFFC3434C)
                                                .addARGB(216, 0xFFDC584A)
                                                .addARGB(255, 0xFFFF7869)
                                                .build());

        this.buildMaterial(DreamtinkerMaterialIds.forgotten_metal)
            .meleeHarvest().armor().ranged()
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(37, 0xFF27373A)
                                                .addARGB(63, 0xFF2F4C4C)
                                                .addARGB(89, 0xFF2A695F)
                                                .addARGB(115, 0xFF278A6F)
                                                .addARGB(140, 0xFF28AB8A)
                                                .addARGB(166, 0xFF48C88E)
                                                .addARGB(192, 0xFF57E3B3)
                                                .addARGB(222, 0xFF7BFFBD)
                                                .build());

        this.buildMaterial(DreamtinkerMaterialIds.Cloggrum)
            .meleeHarvest().armor().shieldCore()
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF342926)
                                                .addARGB(63, 0xFF483832)
                                                .addARGB(102, 0xFF564137)
                                                .addARGB(140, 0xFF635043)
                                                .addARGB(178, 0xFF7A6858)
                                                .addARGB(216, 0xFF947F67)
                                                .addARGB(255, 0xFFB89B7A)
                                                .build());

        this.buildMaterial(DreamtinkerMaterialIds.Froststeel)
            .meleeHarvest().armor()
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF2C314A)
                                                .addARGB(63, 0xFF4E557C)
                                                .addARGB(102, 0xFF606999)
                                                .addARGB(140, 0xFF6886AE)
                                                .addARGB(178, 0xFF7C9CBC)
                                                .addARGB(216, 0xFF88ABCD)
                                                .addARGB(255, 0xFFA5C2DD)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.Regalium)
            .meleeHarvest().armor().arrowHead()
            .fallbacks("gem", "metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF6D4516)
                                                .addARGB(63, 0xFF964B28)
                                                .addARGB(102, 0xFFB96B1D)
                                                .addARGB(140, 0xFFD8964A)
                                                .addARGB(178, 0xFFEBBE76)
                                                .addARGB(216, 0xFFFCD87D)
                                                .addARGB(255, 0xFFFFEFB2)
                                                .build());

        this.buildMaterial(DreamtinkerMaterialIds.Iesnium)
            .meleeHarvest()
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF485565)
                                                .addARGB(63, 0xFF4D687B)
                                                .addARGB(102, 0xFF648090)
                                                .addARGB(140, 0xFF5EA09C)
                                                .addARGB(178, 0xFF79A7AF)
                                                .addARGB(216, 0xFFAEE3E0)
                                                .addARGB(255, 0xFFEFF7F6)
                                                .build());


        this.buildMaterial(DreamtinkerMaterialIds.SpikyShard)
            .meleeHarvest().armor().shieldCore().arrowHead()
            .fallbacks("bone")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF3A4859)
                                                .addARGB(102, 0xFF3E4B5B)
                                                .addARGB(140, 0xFF4D5B6D)
                                                .addARGB(178, 0xFF596D7B)
                                                .addARGB(216, 0xFF738A99)
                                                .addARGB(255, 0xFF9FB5BF)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.FifthStone)
            .meleeHarvest().ranged().arrowHead()
            .fallbacks("stone")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF2F2B28)
                                                .addARGB(102, 0xFF49423D)
                                                .addARGB(140, 0xFF635A51)
                                                .addARGB(178, 0xFF80776B)
                                                .addARGB(216, 0xFFADA392)
                                                .addARGB(255, 0xFFD8CCB7)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.SpiralSpin)
            .meleeHarvest().ranged().fletching()
            .fallbacks("mental")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF2B2E3A)
                                                .addARGB(102, 0xFF444857)
                                                .addARGB(140, 0xFF666A78)
                                                .addARGB(178, 0xFF9294A0)
                                                .addARGB(216, 0xFFC8C9CF)
                                                .addARGB(255, 0xFFFFFFFF)
                                                .build());
        ResourceLocation ruin_steel = Dreamtinker.getLocation("generator/ruin_steel");
        this.buildMaterial(DreamtinkerMaterialIds.RuinWheelSteel)
            .meleeHarvest().ranged().fletching().statType(INGOT)
            .fallbacks("mental")
            .transformer(new FramesSpriteTransformer(
                    ruin_steel,
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(25, 0xFF120904)
                                           .addTexture(26, ruin_steel)
                                           .addTexture(102, ruin_steel)
                                           .addARGB(140, 0xFF865016)
                                           .addARGB(178, 0xFF5A1808)
                                           .addARGB(216, 0xFF331108)
                                           .addARGB(255, 0xFF160A04)
                                           .build(),
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(25, 0xFF120904)
                                           .addTexture(26, ruin_steel)
                                           .addTexture(102, ruin_steel)
                                           .addARGB(140, 0xFFFFF0B0)
                                           .addARGB(178, 0xFFFFC957)
                                           .addARGB(216, 0xFFD18A22)
                                           .addARGB(255, 0xFF865016)
                                           .build()
            ));

        this.buildMaterial(DreamtinkerMaterialIds.OathGuardPaleSteel)
            .meleeHarvest().ranged().armor().statType(INGOT).statType(STORAGE_BLOCK).shieldCore()
            .fallbacks("mental")
            .transformer(GreyToSpriteTransformer.builderFromBlack()
                                                .addARGB(63, 0xFF272534)
                                                .addARGB(102, 0xFF4D5068)
                                                .addARGB(140, 0xFF8087A6)
                                                .addARGB(178, 0xFFC2CCE5)
                                                .addARGB(216, 0xFFF0F5FF)
                                                .addARGB(255, 0xFFFFFFFF)
                                                .build());

        this.buildMaterial(DreamtinkerMaterialIds.ForlornOathSteel)
            .meleeHarvest().ranged().armor().statType(INGOT).statType(STORAGE_BLOCK).shieldCore()
            .fallbacks("mental")
            .transformer(GreyToSpriteTransformer.builderFromBlack()
                                                .addARGB(63, 0xFF151926)
                                                .addARGB(102, 0xFF2B344A)
                                                .addARGB(140, 0xFF4E5F7D)
                                                .addARGB(178, 0xFF8299BD)
                                                .addARGB(216, 0xFFD3E4F6)
                                                .addARGB(255, 0xFFFFFFFF)
                                                .build());

        this.buildMaterial(DreamtinkerMaterialIds.deliverance)
            .meleeHarvest().armor()
            .fallbacks("metal", "crystal")
            .transformer(new FramesSpriteTransformer(
                    Dreamtinker.getLocation("generator/etherium"),
                    // Generator frame 0: skyfire erosion.
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(63, 0xFF271919)
                                           .addARGB(102, 0xFF542F26)
                                           .addARGB(140, 0xFF97522E)
                                           .addARGB(178, 0xFFE08430)
                                           .addARGB(216, 0xFFFFBE52)
                                           .addARGB(255, 0xFFFFF4CA)
                                           .build(),
                    // Generator frame 1: white-hot deliverance.
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(63, 0xFF300C07)
                                           .addARGB(102, 0xFF69180C)
                                           .addARGB(140, 0xFFB13012)
                                           .addARGB(178, 0xFFF4641A)
                                           .addARGB(216, 0xFFFFB139)
                                           .addARGB(255, 0xFFFFF2C3)
                                           .build(),
                    // Generator frame 2: frostfire threshold.
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(63, 0xFF121E2C)
                                           .addARGB(102, 0xFF23485C)
                                           .addARGB(140, 0xFF528497)
                                           .addARGB(178, 0xFF9CC6C6)
                                           .addARGB(216, 0xFFE7DAAB)
                                           .addARGB(255, 0xFFFFF7DA)
                                           .build(),
                    // Generator frame 3: frozen state.
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(63, 0xFF0D192B)
                                           .addARGB(102, 0xFF193E5B)
                                           .addARGB(140, 0xFF347092)
                                           .addARGB(178, 0xFF68B2CD)
                                           .addARGB(216, 0xFFB8E8F2)
                                           .addARGB(255, 0xFFF4FEFF)
                                           .build()
            ));


        addELMaterials();
        addMalumMaterials();
        addEidolonMaterials();
        addBICMaterials();
        addNovaMaterials();
    }

    protected void addELMaterials() {
        this.buildMaterial(DreamtinkerMaterialIds.etherium)
            .ranged().meleeHarvest().armor().statType(StatlessMaterialStats.BOWSTRING).shieldCore()
            .fallbacks("metal", "crystal")
            .transformer(new FramesSpriteTransformer(
                    Dreamtinker.getLocation("generator/etherium"),
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(63, 0xFF1E5656)
                                           .addARGB(102, 0xFF2F8A8A)
                                           .addARGB(140, 0xFF55CACA)
                                           .addARGB(178, 0xFF9AF5EA)
                                           .addARGB(216, 0xFFE8FFFF)
                                           .addARGB(255, 0xFFFFFFFF)
                                           .build(),
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(63, 0xFF55CACA)
                                           .addARGB(102, 0xFF9AF5EA)
                                           .addARGB(140, 0xFFD8FFFF)
                                           .addARGB(178, 0xFFF4FFFF)
                                           .addARGB(216, 0xFFFFFFFF)
                                           .addARGB(255, 0xFFFFFFFF)
                                           .build(),
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(63, 0xFF102020)
                                           .addARGB(102, 0xFF1E5656)
                                           .addARGB(140, 0xFF2F8A8A)
                                           .addARGB(178, 0xFF55CACA)
                                           .addARGB(216, 0xFFB8FFF2)
                                           .addARGB(255, 0xFFF4FFFF)
                                           .build(),
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(63, 0xFF081010)
                                           .addARGB(102, 0xFF102020)
                                           .addARGB(140, 0xFF183838)
                                           .addARGB(178, 0xFF1E5656)
                                           .addARGB(216, 0xFF3E9A9A)
                                           .addARGB(255, 0xFF7FE0D8)
                                           .build()
            ));
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
        this.buildMaterial(DreamtinkerMaterialIds.soul_etherium).shieldCore()
            .ranged().meleeHarvest().armor().statType(StatlessMaterialStats.BOWSTRING).statType(STORAGE_BLOCK)
            .fallbacks("gem", "metal")
            .transformer(new FramesSpriteTransformer(
                    Dreamtinker.getLocation("generator/etherium"),
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(63, 0xFF8BB5BE)
                                           .addARGB(102, 0xFFD0C1D0)
                                           .addARGB(140, 0xFFC9E8E1)
                                           .addARGB(178, 0xFFB8F1EA)
                                           .addARGB(216, 0xFFF7E5F7)
                                           .addARGB(255, 0xFFCBFEF6)
                                           .build(),
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(63, 0xFFA3FFF9)
                                           .addARGB(102, 0xFFD8FFFF)
                                           .addARGB(140, 0xFFE0FFFF)
                                           .addARGB(178, 0xFFF4FFFF)
                                           .addARGB(216, 0xFFFFFFFF)
                                           .addARGB(255, 0xFFFFFFFF)
                                           .build(),
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(63, 0xFF7E90A1)
                                           .addARGB(102, 0xFFB9A3B9)
                                           .addARGB(140, 0xFFADDDD3)
                                           .addARGB(178, 0xFFA7E9DF)
                                           .addARGB(216, 0xFFF3D9F3)
                                           .addARGB(255, 0xFFBEFDF2)
                                           .build(),
                    GreyToSpriteTransformer.builderFromBlack()
                                           .addARGB(63, 0xFF664666)
                                           .addARGB(102, 0xFF8A658A)
                                           .addARGB(140, 0xFF77C6B5)
                                           .addARGB(178, 0xFFE4B9E4)
                                           .addARGB(216, 0xFF8BE1CF)
                                           .addARGB(255, 0xFFA4FCE9)
                                           .build()
            ));
    }

    protected void addMalumMaterials() {
        this.buildMaterial(DreamtinkerMaterialIds.spirit_fabric)
            .statType(StatlessMaterialStats.BINDING).armor().statType(StatlessMaterialStats.BOWSTRING).repairKit()
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
            .statType(HeadMaterialStats.ID).ranged().statType(StatlessMaterialStats.BOWSTRING).arrowHead()
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
            .meleeHarvest().arrowHead()
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
            .meleeHarvest().arrowHead()
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
        for (MalumSpiritType types : SpiritTypeRegistry.SPIRITS.values()) {
            String name = types.identifier;
            MaterialSpriteInfoBuilder builder = buildMaterial(MaterialVariantId.create(DreamtinkerMaterialIds.spirits, name));
            builder.arrowHead().transformer(GreyToSpriteTransformer.builder()
                                                                   .addARGB(0, types.getPrimaryColor().darker().darker().darker().getRGB())
                                                                   .addARGB(63, types.getPrimaryColor().darker().darker().getRGB())
                                                                   .addARGB(102, types.getPrimaryColor().darker().getRGB())
                                                                   .addARGB(140, types.getPrimaryColor().getRGB())
                                                                   .addARGB(178, types.getPrimaryColor().getRGB())
                                                                   .addARGB(216, types.getPrimaryColor().brighter().getRGB())
                                                                   .addARGB(255, types.getPrimaryColor().brighter().brighter().getRGB())
                                                                   .build()
            );
            builder.variant(true);
        }
        buildMaterial(DreamtinkerMaterialIds.blazing_quartz)
                .arrowHead()
                .colorMapper(GreyToColorMapping.builderFromBlack()
                                               .addARGB(63, 0xFF7D1A35)
                                               .addARGB(102, 0xFFA9254A)
                                               .addARGB(140, 0xFFE99432)
                                               .addARGB(178, 0xFFFCB236)
                                               .addARGB(216, 0xFFFCE35C)
                                               .addARGB(255, 0xFFFCEEA5)
                                               .build());

        this.buildMaterial(DreamtinkerMaterialIds.grim_talc)
            .arrowShaft()
            .fallbacks("bone")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF3A2A24)
                                                .addARGB(102, 0xFF694D40)
                                                .addARGB(140, 0xFF8C7C51)
                                                .addARGB(178, 0xFFB6AA69)
                                                .addARGB(216, 0xFFD6D3A1)
                                                .addARGB(255, 0xFFFCF7ED)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.astral_weave)
            .fletching().maille().statType(StatlessMaterialStats.BINDING).statType(StatlessMaterialStats.BOWSTRING).repairKit()
            .fallbacks("cloth")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF3B2B51)
                                                .addARGB(102, 0xFF524175)
                                                .addARGB(140, 0xFF655A87)
                                                .addARGB(178, 0xFF769CC1)
                                                .addARGB(216, 0xFF7BCBC5)
                                                .addARGB(255, 0xFFB9EEF1)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.null_slate)
            .arrowShaft()
            .fallbacks("cloth")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF0B0612)
                                                .addARGB(102, 0xFF22103B)
                                                .addARGB(140, 0xFF4A166C)
                                                .addARGB(178, 0xFF8E1FA3)
                                                .addARGB(216, 0xFFE85BE1)
                                                .addARGB(255, 0xFFFFE6A8)
                                                .build());
    }

    protected void addEidolonMaterials() {
        this.buildMaterial(DreamtinkerMaterialIds.TatteredCloth)
            .statType(StatlessMaterialStats.BINDING).statType(StatlessMaterialStats.BOWSTRING).repairKit()
            .fallbacks("cloth")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF313237)
                                                .addARGB(102, 0xFF424446)
                                                .addARGB(140, 0xFF5B5957)
                                                .addARGB(178, 0xFF74706D)
                                                .addARGB(216, 0xFF9A9290)
                                                .addARGB(255, 0xFFC2B7B5)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.WickedWeave)
            .statType(StatlessMaterialStats.BINDING).armor().statType(StatlessMaterialStats.BOWSTRING).repairKit()
            .fallbacks("cloth")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF112035)
                                                .addARGB(102, 0xFF22294C)
                                                .addARGB(140, 0xFF384185)
                                                .addARGB(178, 0xFF4A4EA4)
                                                .addARGB(216, 0xFF756FCC)
                                                .addARGB(255, 0xFFBDA4DD)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.PaladinBone)
            .armor()
            .fallbacks("bone")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF161C18)
                                                .addARGB(102, 0xFF29372F)
                                                .addARGB(140, 0xFF3E5B4B)
                                                .addARGB(178, 0xFF5B8A72)
                                                .addARGB(216, 0xFFB0D2B2)
                                                .addARGB(255, 0xFFF3F6DD)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.PaladinBoneTool)
            .meleeHarvest()
            .fallbacks("bone")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF161C18)
                                                .addARGB(102, 0xFF29372F)
                                                .addARGB(140, 0xFF3E5B4B)
                                                .addARGB(178, 0xFF5B8A72)
                                                .addARGB(216, 0xFFB0D2B2)
                                                .addARGB(255, 0xFFF3F6DD)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.SoulGem)
            .arrowHead()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF2A0F24)
                                                .addARGB(102, 0xFF4A153A)
                                                .addARGB(140, 0xFF7A2E6C)
                                                .addARGB(178, 0xFFB35AA6)
                                                .addARGB(216, 0xFFE5A8D9)
                                                .addARGB(255, 0xFFFFF0FA)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.CrimsonGem)
            .arrowHead()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF1A0506)
                                                .addARGB(102, 0xFF4A0D12)
                                                .addARGB(140, 0xFF8A1D1B)
                                                .addARGB(178, 0xFFD43B21)
                                                .addARGB(216, 0xFFFF7C2B)
                                                .addARGB(255, 0xFFFFE7B6)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.ShadowGem)
            .arrowHead()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF090014)
                                                .addARGB(102, 0xFF2A0B3A)
                                                .addARGB(140, 0xFF5A1880)
                                                .addARGB(178, 0xFF9A4AD7)
                                                .addARGB(216, 0xFFE28BFF)
                                                .addARGB(255, 0xFFFFE8FF)
                                                .build());
    }

    protected void addBICMaterials() {
        this.buildMaterial(DreamtinkerMaterialIds.DarkMetal)
            .meleeHarvest().armor().shieldCore()
            .fallbacks("metal")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF0B0B0C)
                                                .addARGB(102, 0xFF211E27)
                                                .addARGB(140, 0xFF2C2934)
                                                .addARGB(178, 0xFF494550)
                                                .addARGB(216, 0xFF66646E)
                                                .addARGB(255, 0xFF9899A1)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.MonsterSkin)
            .statType(StatlessMaterialStats.BINDING).statType(StatlessMaterialStats.BOWSTRING).armor().cuirass()
            .fallbacks("cloth")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF18161B)
                                                .addARGB(102, 0xFF2A262D)
                                                .addARGB(140, 0xFF3A3340)
                                                .addARGB(178, 0xFF4C4255)
                                                .addARGB(216, 0xFF534E63)
                                                .addARGB(255, 0xFF605D78)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.LifeStealerBone)
            .arrowShaft()
            .fallbacks("bone")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF0C0E0F)
                                                .addARGB(102, 0xFF161A1B)
                                                .addARGB(140, 0xFF212628)
                                                .addARGB(178, 0xFF2B3234)
                                                .addARGB(216, 0xFF353E41)
                                                .addARGB(255, 0xFF455054)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.KrampusHorn)
            .arrowShaft()
            .fallbacks("bone")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF17021C)
                                                .addARGB(102, 0xFF28041E)
                                                .addARGB(140, 0xFF4A0425)
                                                .addARGB(178, 0xFF600A27)
                                                .addARGB(216, 0xFF7C192E)
                                                .addARGB(255, 0xFF962637)
                                                .build());

        this.buildMaterial(DreamtinkerMaterialIds.NightMareClaw)
            .arrowHead()
            .fallbacks("bone")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF120E1A)
                                                .addARGB(102, 0xFF1D1624)
                                                .addARGB(140, 0xFF241826)
                                                .addARGB(178, 0xFF251D2E)
                                                .addARGB(216, 0xFF30273C)
                                                .addARGB(255, 0xFF3E2E49)
                                                .build());

        this.buildMaterial(DreamtinkerMaterialIds.InfernalEmber)
            .arrowHead()
            .fallbacks("bone")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF040604)
                                                .addARGB(102, 0xFF0A120A)
                                                .addARGB(140, 0xFF173117)
                                                .addARGB(178, 0xFF225722)
                                                .addARGB(216, 0xFF42A42A)
                                                .addARGB(255, 0xFFB7FF49)
                                                .build());

        this.buildMaterial(DreamtinkerMaterialIds.SpiderMandible)
            .arrowHead()
            .fallbacks("bone")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF0C0F13)
                                                .addARGB(102, 0xFF111316)
                                                .addARGB(140, 0xFF191D22)
                                                .addARGB(178, 0xFF252B2D)
                                                .addARGB(216, 0xFF353B3A)
                                                .addARGB(255, 0xFF575E57)
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.HoundFang)
            .arrowHead()
            .fallbacks("bone")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF3C5262)
                                                .addARGB(102, 0xFF4F697B)
                                                .addARGB(140, 0xFF758C9D)
                                                .addARGB(178, 0xFF899DAC)
                                                .addARGB(216, 0xFFA7B7C3)
                                                .addARGB(255, 0xFFCED9E2)
                                                .build());
    }

    protected void addNovaMaterials() {
        this.buildMaterial(DreamtinkerMaterialIds.AbjurationEssence)
            .armor().shieldCore()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF8A3FAF)    // 最深紫色（暗部）
                                                .addARGB(102, 0xFFA25AC2)   // 深紫色
                                                .addARGB(140, 0xFFB97ED5)   // 中紫色
                                                .addARGB(178, 0xFFCE9FE8)   // 浅紫色
                                                .addARGB(216, 0xFFE1BFFA)   // 更浅紫色
                                                .addARGB(255, 0xFFF0D8FD)   // 最浅紫色（亮部）
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.ConjurationEssence)
            .ranged().arrowHead()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF2A5A6F)    // 最深蓝绿（暗部）
                                                .addARGB(102, 0xFF3D7A93)   // 深蓝绿
                                                .addARGB(140, 0xFF579CB3)   // 中蓝绿
                                                .addARGB(178, 0xFF72BCCC)   // 浅蓝绿
                                                .addARGB(216, 0xFF95D1DB)   // 更浅蓝绿
                                                .addARGB(255, 0xFFBCE1E8)   // 最浅蓝绿（亮部）
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.AirEssence)
            .ranged().arrowHead()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF4B6B1F)    // 最深黄绿（暗部）
                                                .addARGB(102, 0xFF6B912E)   // 深黄绿
                                                .addARGB(140, 0xFF8DB83D)   // 中黄绿
                                                .addARGB(178, 0xFFB1D54C)   // 浅黄绿
                                                .addARGB(216, 0xFFD5F25B)   // 更浅黄绿
                                                .addARGB(255, 0xFFF9FF6A)   // 最浅黄绿（亮部
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.EarthEssence)
            .armor().shieldCore()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF1A5325)    // 最深墨绿（暗部）
                                                .addARGB(102, 0xFF2A7A38)   // 深翠绿
                                                .addARGB(140, 0xFF3AB14B)   // 中鲜绿
                                                .addARGB(178, 0xFF5AD96E)   // 浅亮绿
                                                .addARGB(216, 0xFF8BFF9C)   // 更浅荧光绿
                                                .addARGB(255, 0xFFB6FFC1)   // 最浅淡绿（亮部）
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.FireEssence)
            .meleeHarvest().arrowHead()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF2C0A0F)     // 最深暗红（暗部）
                                                .addARGB(102, 0xFF5C1A21)    // 深酒红
                                                .addARGB(140, 0xFF8C2A33)    // 中红棕
                                                .addARGB(178, 0xFFB64A4F)    // 浅红橙
                                                .addARGB(216, 0xFFE07A6B)    // 亮橙红
                                                .addARGB(255, 0xFFFFA087)    // 最浅亮橙（高光）
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.ManipulationEssence)
            .meleeHarvest()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF4A350A)     // 最深暗金（暗部）
                                                .addARGB(102, 0xFF7A5514)    // 深棕金
                                                .addARGB(140, 0xFFAA751E)    // 中黄铜色
                                                .addARGB(178, 0xFFD09528)    // 浅金棕
                                                .addARGB(216, 0xFFEAA032)    // 亮金色
                                                .addARGB(255, 0xFFFFCF46)    // 最浅亮金（高光）
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.WaterEssence)
            .meleeHarvest().arrowHead()
            .fallbacks("gem")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF2A1A4A)     // 最深暗紫（暗部）
                                                .addARGB(102, 0xFF4A357A)    // 深紫棕
                                                .addARGB(140, 0xFF6A50AA)    // 中紫晶色
                                                .addARGB(178, 0xFF8A6BD0)    // 浅紫灰
                                                .addARGB(216, 0xFFAA8BEA)    // 亮浅紫
                                                .addARGB(255, 0xFFCAAFFF)    // 最浅淡紫（高光）
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.WildenHorn)
            .arrowShaft()
            .fallbacks("bone")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF3A4A35)     // 最深暗灰绿（暗部）
                                                .addARGB(102, 0xFF6A7A65)    // 深灰绿
                                                .addARGB(140, 0xFF9AA095)    // 中浅灰绿
                                                .addARGB(178, 0xFFC0C5B5)    // 浅灰绿
                                                .addARGB(216, 0xFFE0E5D5)    // 亮浅灰
                                                .addARGB(255, 0xFFF0F5E5)    // 最浅灰白（高光）
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.WildenWing)
            .arrowShaft()
            .fallbacks("bone")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF2A2015)     // 最深暗棕（暗部）
                                                .addARGB(102, 0xFF5A4A35)    // 深棕褐
                                                .addARGB(140, 0xFF8A7A55)    // 中棕黄
                                                .addARGB(178, 0xFFB09A75)    // 浅棕灰
                                                .addARGB(216, 0xFFD0B595)    // 亮浅棕
                                                .addARGB(255, 0xFFE0C5A5)    // 最浅棕黄（高光）
                                                .build());
        this.buildMaterial(DreamtinkerMaterialIds.WildenSpike)
            .arrowShaft()
            .fallbacks("bone")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(63, 0xFF3A1A15)     // 最深暗红棕（暗部）
                                                .addARGB(102, 0xFF6A3A25)    // 深酒红棕
                                                .addARGB(140, 0xFF9A5A35)    // 中红棕
                                                .addARGB(178, 0xFFC07A45)    // 浅橙棕
                                                .addARGB(216, 0xFFE09A55)    // 亮红橙
                                                .addARGB(255, 0xFFF0B565)    // 最浅橙黄（高光）
                                                .build());


        this.buildMaterial(DreamtinkerMaterialIds.GooeySlimeSkin)
            .statType(StatlessMaterialStats.BOWSTRING).cuirass()
            .fallbacks("cloth")
            .transformer(GreyToSpriteTransformer.builder()
                                                .addARGB(0, 0xFF493F30)
                                                .addARGB(63, 0xFF5A4D38)
                                                .addARGB(102, 0xFF645842)
                                                .addARGB(140, 0xFF665F4A)
                                                .addARGB(178, 0xFF6E694F)
                                                .addARGB(216, 0xFF7C7960)
                                                .addARGB(255, 0xFF999E7F)
                                                .build());
    }
}
