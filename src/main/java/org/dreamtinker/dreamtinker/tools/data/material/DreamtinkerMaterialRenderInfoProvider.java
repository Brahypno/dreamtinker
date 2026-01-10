package org.dreamtinker.dreamtinker.tools.data.material;

import com.sammy.malum.core.systems.spirit.MalumSpiritType;
import com.sammy.malum.registry.common.SpiritTypeRegistry;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialRenderInfoProvider;
import slimeknights.tconstruct.library.client.data.material.AbstractMaterialSpriteProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

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
        buildRenderInfo(DreamtinkerMaterialIds.nigrescence_string).color(0xFF332222).fallbacks("crystal").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.metallivorous_stibium_lupus).color(0xFFFDF8EB).fallbacks("metal").luminosity(9);
        buildRenderInfo(DreamtinkerMaterialIds.star_regulus).color(0xFF8C1F1F).fallbacks("metal").luminosity(15);
        buildRenderInfo(DreamtinkerMaterialIds.crying_obsidian).color(0xFF3B2754).fallbacks("rock").luminosity(3);
        buildRenderInfo(DreamtinkerMaterialIds.larimar).color(0xFF51BACE).fallbacks("gem").luminosity(8);
        buildRenderInfo(DreamtinkerMaterialIds.amber).color(0xFFFE9300).fallbacks("gem").luminosity(7);
        buildRenderInfo(DreamtinkerMaterialIds.half_rotten_homunculus).color(0xFF360100).fallbacks("bone").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.half_rotten_string).color(0xFF360100).fallbacks("bone").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.desire_gem).color(0xFF7AB97B).fallbacks("gem").luminosity(15);
        buildRenderInfo(DreamtinkerMaterialIds.despair_gem).color(0xFF870721).fallbacks("gem").luminosity(15);
        buildRenderInfo(DreamtinkerMaterialIds.shadowskin);
        buildRenderInfo(DreamtinkerMaterialIds.soul_steel).color(0xFF3E5568).fallbacks("metal").luminosity(14);
        buildRenderInfo(DreamtinkerMaterialIds.rainbow_honey_crystal).color(0xFFB9C532).fallbacks("gem").luminosity(14);
        buildRenderInfo(DreamtinkerMaterialIds.black_sapphire).color(0xFF1A1F26).fallbacks("gem").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.scolecite).color(0xFF12161A).fallbacks("gem").luminosity(12);
        buildRenderInfo(DreamtinkerMaterialIds.shiningFlint).color(0xFF3C96B9).fallbacks("crystal").luminosity(15);
        buildRenderInfo(DreamtinkerMaterialIds.orichalcum).color(0xFF51C272).fallbacks("metal").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.cold_iron).color(0xFF2C488F).fallbacks("metal").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.shadowSilver).color(0xFF987A2C).fallbacks("metal").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.TransmutationGold).color(0xFFCC641A).fallbacks("metal").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.ArcaneGold).color(0xFFEDAE66).fallbacks("metal").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.SpikyShard).color(0xFF738A99).fallbacks("bone").luminosity(4);

        buildRenderInfo(DreamtinkerMaterialIds.etherium).color(0xFF96E8E0).fallbacks("metal", "crystal").luminosity(15);
        buildRenderInfo(DreamtinkerMaterialIds.nefarious).color(0xFF8513A0).fallbacks("metal", "crystal", "gem").luminosity(8);
        buildRenderInfo(DreamtinkerMaterialIds.soul_etherium).color(0xFFC2A2C2).fallbacks("metal", "crystal", "gem").luminosity(15);

        buildRenderInfo(DreamtinkerMaterialIds.spirit_fabric).color(0xFF6E2CA3).fallbacks("cloth").luminosity(5);
        buildRenderInfo(DreamtinkerMaterialIds.hallowed_gold).color(0xFFB86F22).fallbacks("metal").luminosity(7);
        redirect(DreamtinkerMaterialIds.mnemonic_auric, DreamtinkerMaterialIds.mnemonic);
        buildRenderInfo(DreamtinkerMaterialIds.mnemonic).color(0xFF7F08FC).fallbacks("gem").luminosity(9);
        buildRenderInfo(DreamtinkerMaterialIds.auric);
        buildRenderInfo(DreamtinkerMaterialIds.soul_stained_steel).color(0xFFBE86E9).fallbacks("metal").luminosity(12);
        buildRenderInfo(DreamtinkerMaterialIds.malignant_lead).color(0xFF626477).fallbacks("gem").luminosity(15);
        buildRenderInfo(DreamtinkerMaterialIds.malignant_pewter).color(0xFFC9B7D6).fallbacks("metal").luminosity(15);
        buildRenderInfo(DreamtinkerMaterialIds.malignant_gluttony).color(0xFF1FA60A).fallbacks("metal").luminosity(0);
        redirect(DreamtinkerMaterialIds.soul_rock, DreamtinkerMaterialIds.tainted);
        buildRenderInfo(DreamtinkerMaterialIds.tainted).color(0x534C58);
        buildRenderInfo(DreamtinkerMaterialIds.twisted);
        buildRenderInfo(DreamtinkerMaterialIds.refined);
        buildRenderInfo(DreamtinkerMaterialIds.blazing_quartz).color(0xFFFCE35C).fallbacks("gem");
        MaterialVariantId sacred_spirit = MaterialVariantId.create(DreamtinkerMaterialIds.spirits, SpiritTypeRegistry.SACRED_SPIRIT.identifier);
        redirect(DreamtinkerMaterialIds.spirits, sacred_spirit);
        for (MalumSpiritType types : SpiritTypeRegistry.SPIRITS.values()) {
            String name = types.identifier;
            buildRenderInfo(MaterialVariantId.create(DreamtinkerMaterialIds.spirits, name));
        }
        buildRenderInfo(DreamtinkerMaterialIds.grim_talc).color(0xFFD6D3A1).fallbacks("bone");
        buildRenderInfo(DreamtinkerMaterialIds.astral_weave).color(0xFF7BCBC5).fallbacks("cloth");
        buildRenderInfo(DreamtinkerMaterialIds.null_slate).color(0xFFE85BE1).fallbacks("stone");

        buildRenderInfo(DreamtinkerMaterialIds.TatteredCloth).color(0xFF5B5957).fallbacks("cloth").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.WickedWeave).color(0xFF384185).fallbacks("cloth").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.PaladinBone).color(0xFF46514F).fallbacks("bone").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.PaladinBoneTool).color(0xFF46514F).fallbacks("bone").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.SoulGem).color(0xFFD2A4E6).fallbacks("gem").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.ShadowGem).color(0xFFE28BFF).fallbacks("gem").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.CrimsonGem).color(0xFFFF7C2B).fallbacks("gem").luminosity(0);

        buildRenderInfo(DreamtinkerMaterialIds.DarkMetal).color(0xFF777883).fallbacks("metal").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.MonsterSkin).color(0xFF534E63).fallbacks("cloth").luminosity(0);
        buildRenderInfo(DreamtinkerMaterialIds.LifeStealerBone).color(0xFF353E41).fallbacks("bone").luminosity(4);
        buildRenderInfo(DreamtinkerMaterialIds.KrampusHorn).color(0xFF7C192E).fallbacks("bone").luminosity(4);
        buildRenderInfo(DreamtinkerMaterialIds.NightMareClaw).color(0xFF30273C).fallbacks("bone").luminosity(4);
        buildRenderInfo(DreamtinkerMaterialIds.InfernalEmber).color(0xFF42A42A).fallbacks("bone").luminosity(4);
        buildRenderInfo(DreamtinkerMaterialIds.SpiderMandible).color(0xFF353B3A).fallbacks("bone").luminosity(4);
        buildRenderInfo(DreamtinkerMaterialIds.HoundFang).color(0xFFA7B7C3).fallbacks("bone").luminosity(4);

        buildRenderInfo(DreamtinkerMaterialIds.AbjurationEssence).color(0xFFE1BFFA).fallbacks("gem").luminosity(4);
        buildRenderInfo(DreamtinkerMaterialIds.ConjurationEssence).color(0xFF95D1DB).fallbacks("gem").luminosity(4);
        buildRenderInfo(DreamtinkerMaterialIds.AirEssence).color(0xFFD5F25B).fallbacks("gem").luminosity(4);
        buildRenderInfo(DreamtinkerMaterialIds.EarthEssence).color(0xFF8BFF9C).fallbacks("gem").luminosity(4);
        buildRenderInfo(DreamtinkerMaterialIds.FireEssence).color(0xFFE07A6B).fallbacks("gem").luminosity(4);
        buildRenderInfo(DreamtinkerMaterialIds.ManipulationEssence).color(0xFFEAA032).fallbacks("gem").luminosity(4);
        buildRenderInfo(DreamtinkerMaterialIds.WaterEssence).color(0xFFAA8BEA).fallbacks("gem").luminosity(4);
        buildRenderInfo(DreamtinkerMaterialIds.WildenHorn).color(0xFFE0E5D5).fallbacks("bone").luminosity(4);
        buildRenderInfo(DreamtinkerMaterialIds.WildenWing).color(0xFFD0B595).fallbacks("bone").luminosity(4);
        buildRenderInfo(DreamtinkerMaterialIds.WildenSpike).color(0xFFE09A55).fallbacks("bone").luminosity(4);

    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Material Info Provider";
    }
}
