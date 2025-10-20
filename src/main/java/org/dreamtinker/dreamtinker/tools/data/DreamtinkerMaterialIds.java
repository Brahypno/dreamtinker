package org.dreamtinker.dreamtinker.tools.data;

import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;
import static slimeknights.tconstruct.library.materials.definition.MaterialVariantId.create;

public class DreamtinkerMaterialIds {

    public static final MaterialId echo_alloy = new MaterialId(new ResourceLocation(MODID, "echo_alloy"));
    public static final MaterialId moonlight_ice = new MaterialId(new ResourceLocation(MODID, "moonlight_ice"));
    public static final MaterialId valentinite = new MaterialId(new ResourceLocation(MODID, "valentinite"));
    public static final MaterialId nigrescence_antimony = new MaterialId(new ResourceLocation(MODID, "nigrescence_antimony"));
    public static final MaterialId metallivorous_stibium_lupus = new MaterialId(new ResourceLocation(MODID, "metallivorous_stibium_lupus"));
    public static final MaterialId star_regulus = new MaterialId(new ResourceLocation(MODID, "star_regulus"));
    public static final MaterialId crying_obsidian = new MaterialId(new ResourceLocation(MODID, "crying_obsidian"));
    public static final MaterialId larimar = new MaterialId(new ResourceLocation(MODID, "larimar"));
    //Compact-enigmatic legacy
    public static final MaterialId etherium = new MaterialId(new ResourceLocation(MODID, "etherium"));
    public static final MaterialId nefarious = new MaterialId(new ResourceLocation(MODID, "nefarious"));
    public static final MaterialId soul_etherium = new MaterialId(new ResourceLocation(MODID, "soul_etherium"));
    //Compact malum
    public static final MaterialId spirit_fabric = new MaterialId(new ResourceLocation(MODID, "malum_spirit_fabric"));
    public static final MaterialId hallowed_gold = new MaterialId(new ResourceLocation(MODID, "malum_hallowed_gold"));
    public static final MaterialId mnemonic_auric = new MaterialId(new ResourceLocation(MODID, "malum_mnemonic_auric"));
    public static final MaterialVariantId mnemonic = create(mnemonic_auric, "mnemonic");
    public static final MaterialVariantId auric = create(mnemonic_auric, "auric");
    public static final MaterialId soul_stained_steel = new MaterialId(new ResourceLocation(MODID, "malum_soul_stained_steel"));
    public static final MaterialId malignant_pewter = new MaterialId(new ResourceLocation(MODID, "malum_malignant_pewter"));
    public static final MaterialId malignant_gluttony = new MaterialId(new ResourceLocation(MODID, "malum_malignant_gluttony"));
    public static final MaterialId soul_rock = new MaterialId(new ResourceLocation(MODID, "malum_soul_rock"));
    public static final MaterialVariantId twisted = create(soul_rock, "twisted");
    public static final MaterialVariantId tainted = create(soul_rock, "tainted");
    public static final MaterialVariantId refined = create(soul_rock, "refined");


}
