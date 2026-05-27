package org.dreamtinker.dreamtinker.common.data.tags;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractMaterialTagProvider;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import static org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys.Materials.*;
import static org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds.*;

public class MaterialTagProvider extends AbstractMaterialTagProvider {
    public MaterialTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, Dreamtinker.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(TinkerTags.Materials.EXCLUDE_FROM_LOOT)
            .add(moonlight_ice, nigrescence_antimony, metallivorous_stibium_lupus, echo_alloy, star_regulus, half_rotten_homunculus, desire_gem, despair_gem,
                 RuinWheelSteel, PermanenceScale, PermanenceWing, scolecite, SpiralSpin, ForlornOathSteel, deliverance, whimsyGold)
            .addOptional(etherium, nefarious, soul_etherium,
                         soul_stained_steel, malignant_pewter, malignant_gluttony, malignant_lead, PaladinBone, forgotten_metal);

        this.tag(TinkerTags.Materials.NETHER)
            .add(amber, scolecite)
            .addOptional(nefarious);
        this.tag(TinkerTags.Materials.BARTERED).add(amber, rainbow_honey_crystal, scolecite);
        tag(TinkerTags.Materials.NETHER_GATED).add(amber, scolecite);
        this.tag(TinkerTags.Materials.MELEE)
            .add(crying_obsidian, nigrescence_antimony, moonlight_ice, echo_alloy, metallivorous_stibium_lupus, amber, half_rotten_homunculus, desire_gem,
                 despair_gem, scolecite, shiningFlint, cold_iron, SpikyShard, soul_steel, FifthStone, SpiralSpin, RuinWheelSteel, OathGuardPaleSteel,
                 AtonementSilver, ForlornOathSteel, deliverance)
            .addOptional(nefarious, soul_etherium, soul_stained_steel, malignant_pewter, malignant_gluttony, shadowSilver, ArcaneGold,
                         WickedWeave, DarkMetal, MonsterSkin, PaladinBoneTool, TatteredCloth, WaterEssence, FireEssence, ManipulationEssence, Utherium,
                         forgotten_metal, Cloggrum, Froststeel);
        this.tag(TinkerTags.Materials.HARVEST)
            .add(larimar, rainbow_honey_crystal, TransmutationGold, whimsyGold)
            .addOptional(hallowed_gold, Regalium, Iesnium);
        this.tag(TinkerTags.Materials.GENERAL)
            .add(valentinite, black_sapphire, orichalcum)
            .addOptional(etherium, spirit_fabric, astral_weave);

        this.tag(TinkerTags.Materials.LIGHT)
            .add(nigrescence_string, echo_alloy, larimar, half_rotten_string, desire_gem, cold_iron, TransmutationGold, orichalcum, FifthStone, SpiralSpin)
            .addOptional(spirit_fabric, soul_stained_steel, nefarious, etherium, soul_etherium, metallivorous_stibium_lupus, star_regulus, shadowSilver,
                         TatteredCloth, PaladinBoneTool, Utherium, forgotten_metal, AtonementSilver);
        this.tag(TinkerTags.Materials.BALANCED)
            .add(valentinite, black_sapphire, scolecite, shiningFlint, RuinWheelSteel, OathGuardPaleSteel,
                 ForlornOathSteel)
            .addOptional(MonsterSkin, astral_weave, GooeySlimeSkin);
        this.tag(TinkerTags.Materials.HEAVY)
            .add(crying_obsidian, amber, soul_steel)
            .addOptional(hallowed_gold, ArcaneGold, WickedWeave);
        tag(TinkerTags.Materials.COMPATABILITY_METALS)
                .add(RuinWheelSteel, ForlornOathSteel, AtonementSilver);
        tag(TinkerTags.Materials.COMPATABILITY_BLOCKS)
                .add(echo_alloy, amber, black_sapphire, ForlornOathSteel)
                .addOptional();
        tag(TinkerTags.Materials.COMPATABILITY_ALLOYS)
                .addOptional(soul_etherium, malignant_gluttony);
        this.tag(THROW_STONE)
            .add(FifthStone);
        this.tag(FIRE_FLAME)//must be man made, not natural.
            .add(MaterialIds.blazingBone, MaterialIds.blazewood)
            .addOptional(FireEssence);
        this.tag(ROTATING_WHEEL)
            .add(SpiralSpin);

    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Material Tag Provider";
    }
}

