package org.dreamtinker.dreamtinker.tools.data.material;

import net.minecraft.data.PackOutput;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.material.AbstractMaterialTraitDataProvider;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;

import static org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers.*;
import static slimeknights.tconstruct.library.materials.MaterialRegistry.*;

public class DreamtinkerMaterialTraitProvider extends AbstractMaterialTraitDataProvider {
    public DreamtinkerMaterialTraitProvider(PackOutput packOutput) {
        super(packOutput, new DreamtinkerMaterialDataProvider(packOutput));
    }

    @Override
    protected void addMaterialTraits() {
        addTraits(DreamtinkerMaterialIds.echo_alloy, ARMOR, ModifierIds.soulbound, echoed_defence.getId());
        addTraits(DreamtinkerMaterialIds.echo_alloy, MELEE_HARVEST, ModifierIds.soulbound, echoed_attack.getId());
        addTraits(DreamtinkerMaterialIds.echo_alloy, RANGED, ModifierIds.soulbound, echoed_attack.getId());

        addTraits(DreamtinkerMaterialIds.moonlight_ice, MELEE_HARVEST, glacial_river.getId());

        addDefaultTraits(DreamtinkerMaterialIds.valentinite, Ids.antimony_usage);
        addTraits(DreamtinkerMaterialIds.valentinite, ARMOR, new ModifierEntry(ModifierIds.fireProtection, 2), new ModifierEntry(Ids.antimony_usage, 1));

        addTraits(DreamtinkerMaterialIds.nigrescence_antimony, MELEE_HARVEST, ewige_widerkunft);
        addTraits(DreamtinkerMaterialIds.nigrescence_antimony, ARMOR, broken_vessel, ouroboric_hourglass);
        addTraits(DreamtinkerMaterialIds.nigrescence_string, RANGED, burning_in_vain);

        addTraits(DreamtinkerMaterialIds.metallivorous_stibium_lupus, MELEE_HARVEST,
                  the_wolf_wonder, the_wolf_answer, the_wolf_was);
        addTraits(DreamtinkerMaterialIds.metallivorous_stibium_lupus, RANGED,
                  the_wolf_wonder, the_wolf_answer, the_wolf_was);

        addTraits(DreamtinkerMaterialIds.star_regulus, RANGED, two_headed_seven.getId(), ModifierIds.soulbound);
        addTraits(DreamtinkerMaterialIds.star_regulus, ARMOR, as_one.getId(), fly.getId(), ModifierIds.soulbound);

        addTraits(DreamtinkerMaterialIds.crying_obsidian, MELEE_HARVEST, sharpened_with);
        addTraits(DreamtinkerMaterialIds.crying_obsidian, RANGED, isolde);
        addTraits(DreamtinkerMaterialIds.crying_obsidian, ARMOR, Ids.in_rain, Ids.with_tears);

        addTraits(DreamtinkerMaterialIds.larimar, MELEE_HARVEST, Ids.ykhEULA, Ids.MorningLordEULA, Ids.EULA);
        addTraits(DreamtinkerMaterialIds.larimar, RANGED, Ids.why_i_cry, deep_sleep_with_roar.getId());

        addTraits(DreamtinkerMaterialIds.amber, MELEE_HARVEST, wait_until.getId(), Ids.AsSand);
        addTraits(DreamtinkerMaterialIds.amber, RANGED, wait_until.getId(), Ids.AsSand);
        addTraits(DreamtinkerMaterialIds.amber, ARMOR, Ids.FragileButBright);

        addDefaultTraits(DreamtinkerMaterialIds.half_rotten_homunculus, anvil_hit.getId(), TinkerModifiers.necrotic.getId());
        addTraits(DreamtinkerMaterialIds.half_rotten_homunculus, ARMOR, Ids.homunculusLifeCurse, Ids.homunculusGift);
        addDefaultTraits(DreamtinkerMaterialIds.half_rotten_string, anvil_hit.getId(), TinkerModifiers.necrotic.getId());

        addDefaultTraits(DreamtinkerMaterialIds.desire_gem, absorption_hit);
        addTraits(DreamtinkerMaterialIds.desire_gem, ARMOR, absorption_defense.getId());

        addDefaultTraits(DreamtinkerMaterialIds.despair_gem, despair_mist, despair_rain, despair_wind);
        addTraits(DreamtinkerMaterialIds.despair_gem, ARMOR, Ids.ophelia, Ids.requiem, Ids.peaches_in_memory);

        addDefaultTraits(DreamtinkerMaterialIds.shadowskin, Ids.shadow_blessing, TinkerModifiers.overslime.getId());

        addDefaultTraits(DreamtinkerMaterialIds.soul_steel, ModifierIds.soulbound, ModifierIds.ductile, Ids.golden_face);
        addTraits(DreamtinkerMaterialIds.soul_steel, MELEE_HARVEST, ModifierIds.soulbound, ModifierIds.ductile, ModifierIds.swiftstrike, Ids.golden_face);
        addTraits(DreamtinkerMaterialIds.soul_steel, RANGED, ModifierIds.soulbound, ModifierIds.ductile, ModifierIds.pierce, Ids.golden_face);
        addTraits(DreamtinkerMaterialIds.soul_steel, ARMOR, ModifierIds.soulbound, ModifierIds.ductile, ModifierIds.magicProtection, Ids.golden_face);

        addDefaultTraits(DreamtinkerMaterialIds.rainbow_honey_crystal, HoneyTastyModifier);
        addTraits(DreamtinkerMaterialIds.rainbow_honey_crystal, ARMOR, HoneyTastyModifier, rainbowCatcher);

        addDefaultTraits(DreamtinkerMaterialIds.black_sapphire, not_like_was);
        addDefaultTraits(DreamtinkerMaterialIds.scolecite, light_in_dark);
        addDefaultTraits(DreamtinkerMaterialIds.shiningFlint, ModifierIds.jagged, Ids.fiber_glass_fragments, light_emanation.getId());

        addDefaultTraits(DreamtinkerMaterialIds.orichalcum, Ids.lunarAttractive, Ids.lunarRejection);
        addTraits(DreamtinkerMaterialIds.orichalcum, ARMOR, Ids.lunarProtection, lunar_defense.getId());

        addDefaultTraits(DreamtinkerMaterialIds.cold_iron, Ids.slowness);
        addTraits(DreamtinkerMaterialIds.cold_iron, ARMOR, OreViewer.getId());

        addDefaultTraits(DreamtinkerMaterialIds.shadowSilver, hiddenHit, knockArts);
        addTraits(DreamtinkerMaterialIds.shadowSilver, ARMOR, knockArts);

        addDefaultTraits(DreamtinkerMaterialIds.TransmutationGold, OreMultiplier.getId(), Ids.soul_unchanged);
        addTraits(DreamtinkerMaterialIds.TransmutationGold, RANGED, Ids.sun_shine, Ids.soul_unchanged);
        addTraits(DreamtinkerMaterialIds.TransmutationGold, ARMOR, SunAway.getId(), Ids.soul_unchanged);

        addDefaultTraits(DreamtinkerMaterialIds.ArcaneGold, Ids.arcane_hit);
        addTraits(DreamtinkerMaterialIds.ArcaneGold, ARMOR, TinkerModifiers.golden.getId(), ModifierIds.magicProtection, Ids.arcane_protection);
        addTraits(DreamtinkerMaterialIds.ArcaneGold, PlatingMaterialStats.SHIELD.getId(), ModifierIds.magicProtection, Ids.arcane_protection);


        addDefaultTraits(DreamtinkerMaterialIds.etherium,
                         new ModifierEntry(ModifierIds.smite, 2),
                         new ModifierEntry(explosive_hit.getId(), 1),
                         new ModifierEntry(Ids.el_etherium, 1),
                         new ModifierEntry(astral_break.getId(), 1));

        addTraits(DreamtinkerMaterialIds.etherium, RANGED, ranged_shoot, explosive_hit);
        addTraits(DreamtinkerMaterialIds.etherium, ARMOR,
                  new ModifierEntry(ModifierIds.magicProtection, 2),
                  new ModifierEntry(etherium_protection.getId(), 1),
                  new ModifierEntry(ender_dodge.getId(), 1),
                  new ModifierEntry(fly.getId(), 1));

        addTraits(DreamtinkerMaterialIds.nefarious, MELEE_HARVEST, cursed_ring_bound.getId(), Ids.wither_body,
                  evil_attack.getId());
        addTraits(DreamtinkerMaterialIds.nefarious, RANGED, cursed_ring_bound.getId(), wither_shoot.getId(),
                  Ids.wither_body, evil_attack.getId());
        addTraits(DreamtinkerMaterialIds.nefarious, ARMOR, cursed_ring_bound.getId(), Ids.wither_body,
                  stone_heart.getId());

        addTraits(DreamtinkerMaterialIds.soul_etherium, MELEE_HARVEST, cursed_ring_bound.getId(), ModifierIds.soulbound,
                  Ids.soul_form, open_soul.getId(), exiles_faulty.getId(), Ids.el_etherium);
        addTraits(DreamtinkerMaterialIds.soul_etherium, RANGED, cursed_ring_bound.getId(), ModifierIds.soulbound,
                  Ids.soul_form, open_soul.getId(), exiles_faulty.getId());
        addTraits(DreamtinkerMaterialIds.soul_etherium, ARMOR, cursed_ring_bound.getId(), ModifierIds.soulbound,
                  Ids.soul_form, soul_blessing.getId());


        addDefaultTraits(DreamtinkerMaterialIds.spirit_fabric, malum_distortion);
        addTraits(DreamtinkerMaterialIds.spirit_fabric, ARMOR, malum_spirit_attributes);

        addDefaultTraits(DreamtinkerMaterialIds.spirit_fabric, new ModifierEntry(ModifierIds.luck, 2));
        addTraits(DreamtinkerMaterialIds.hallowed_gold, RANGED, TinkerModifiers.golden);
        addTraits(DreamtinkerMaterialIds.hallowed_gold, AMMO, Ids.light_arrow);

        addDefaultTraits(DreamtinkerMaterialIds.mnemonic_auric, new ModifierEntry(Ids.malum_haunted, 2), new ModifierEntry(malum_hex_staff, 1));
        addTraits(DreamtinkerMaterialIds.mnemonic_auric, AMMO, teleport_shooting);

        addDefaultTraits(DreamtinkerMaterialIds.soul_stained_steel, new ModifierEntry(Ids.malum_haunted, 2), new ModifierEntry(Ids.malum_tyrving, 1));
        addTraits(DreamtinkerMaterialIds.soul_stained_steel, ARMOR, malum_soul_attributes);
        addTraits(DreamtinkerMaterialIds.soul_stained_steel, RANGED, new ModifierEntry(Ids.malum_haunted, 2), new ModifierEntry(malum_range_accelerator, 1));

        addDefaultTraits(DreamtinkerMaterialIds.malignant_lead, malum_catalyst_lobber);
        addTraits(DreamtinkerMaterialIds.malignant_pewter, MELEE_HARVEST,
                  malum_distortion.getId(), Ids.malum_world_of_weight,
                  Ids.malum_edge_of_deliverance);
        addTraits(DreamtinkerMaterialIds.malignant_pewter, ARMOR, malum_malignant_attributes);

        addTraits(DreamtinkerMaterialIds.malignant_gluttony, MELEE_HARVEST, Ids.malum_haunted, malum_magic_attack.getId(), malum_erosion.getId(),
                  Ids.thundering_curse, malum_thirsty.getId(), malum_evolution.getId());
        addTraits(DreamtinkerMaterialIds.malignant_gluttony, ARMOR, Ids.thundering_curse, malum_thirsty.getId(), malum_spirit_defense.getId());
        addDefaultTraits(DreamtinkerMaterialIds.soul_rock, Ids.malum_spirit_plunder);

        addTraits(DreamtinkerMaterialIds.spirits, AMMO, ModifierIds.spike, malum_expose_soul.getId());
        addTraits(DreamtinkerMaterialIds.blazing_quartz, AMMO, ModifierIds.keen, ModifierIds.fiery);
        addTraits(DreamtinkerMaterialIds.grim_talc, AMMO, Ids.heavy_arrow);
        addDefaultTraits(DreamtinkerMaterialIds.astral_weave, Ids.hidden_shape);
        addTraits(DreamtinkerMaterialIds.astral_weave, AMMO, ModifierIds.power);
        addTraits(DreamtinkerMaterialIds.null_slate, AMMO, Ids.null_void);


        addDefaultTraits(DreamtinkerMaterialIds.TatteredCloth, Ids.eidolon_vulnerable);

        addDefaultTraits(DreamtinkerMaterialIds.WickedWeave, Ids.drinker_magic);
        addTraits(DreamtinkerMaterialIds.WickedWeave, ARMOR, Ids.eidolon_warlock, Ids.drinker_magic);

        addDefaultTraits(DreamtinkerMaterialIds.PaladinBone, Ids.eidolon_soul_hearts, Ids.eidolon_paladin_bone);
        addDefaultTraits(DreamtinkerMaterialIds.PaladinBoneTool, ModifierIds.smite, ModifierIds.pierce);

        addDefaultTraits(DreamtinkerMaterialIds.DarkMetal, bic_dark_defense.getId(), ModifierIds.meleeProtection, ModifierIds.worldbound);
        addTraits(DreamtinkerMaterialIds.DarkMetal, PlatingMaterialStats.SHIELD.getId(), ModifierIds.meleeProtection,
                  ModifierIds.projectileProtection, ModifierIds.worldbound);
        addTraits(DreamtinkerMaterialIds.DarkMetal, MELEE_HARVEST, bic_dark_blade.getId(), ModifierIds.worldbound);

        addDefaultTraits(DreamtinkerMaterialIds.MonsterSkin, TinkerModifiers.tanned.getId(), bic_nightmare_defense.getId(), Ids.monster_blood);

        addDefaultTraits(DreamtinkerMaterialIds.SpikyShard, new ModifierEntry(ModifierIds.thorns, 2));

        addTraits(DreamtinkerMaterialIds.SpikyShard, MELEE_HARVEST, Ids.deeper_water_killer);

    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Material Modifier Provider";
    }
}
