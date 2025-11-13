package org.dreamtinker.dreamtinker.tools.data.material;

import net.minecraft.data.PackOutput;
import org.dreamtinker.dreamtinker.tools.data.DreamtinkerMaterialIds;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.material.AbstractMaterialTraitDataProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.traits.MaterialTraits;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;

import java.lang.reflect.Method;
import java.util.List;

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
        callGetOrCreate(this, DreamtinkerMaterialIds.valentinite).setTraits(ARMOR, List.of(
                new ModifierEntry(ModifierIds.fireProtection, 4),
                new ModifierEntry(Ids.antimony_usage, 1)));

        addTraits(DreamtinkerMaterialIds.nigrescence_antimony, MELEE_HARVEST, ewige_widerkunft);
        addTraits(DreamtinkerMaterialIds.nigrescence_antimony, RANGED, burning_in_vain);
        addTraits(DreamtinkerMaterialIds.nigrescence_antimony, ARMOR, broken_vessel, ouroboric_hourglass);

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

        addDefaultTraits(DreamtinkerMaterialIds.desire_gem, absorption_hit);
        addTraits(DreamtinkerMaterialIds.desire_gem, ARMOR, absorption_defense.getId());

        addDefaultTraits(DreamtinkerMaterialIds.despair_gem, despair_mist, despair_rain, despair_wind);
        addTraits(DreamtinkerMaterialIds.despair_gem, ARMOR, Ids.ophelia, requiem.getId(), Ids.peaches_in_memory);

        addDefaultTraits(DreamtinkerMaterialIds.shadowskin, Ids.shadow_blessing, TinkerModifiers.overslime.getId());

        addDefaultTraits(DreamtinkerMaterialIds.soul_steel, ModifierIds.soulbound, ModifierIds.ductile);
        addTraits(DreamtinkerMaterialIds.soul_steel, MELEE_HARVEST, ModifierIds.soulbound, ModifierIds.ductile, ModifierIds.swiftstrike);
        addTraits(DreamtinkerMaterialIds.soul_steel, RANGED, ModifierIds.soulbound, ModifierIds.ductile, ModifierIds.pierce);
        addTraits(DreamtinkerMaterialIds.soul_steel, ARMOR, ModifierIds.soulbound, ModifierIds.ductile, ModifierIds.magicProtection);
        addDefaultTraits(DreamtinkerMaterialIds.rainbow_honey_crystal, HoneyTastyModifier);
        addTraits(DreamtinkerMaterialIds.rainbow_honey_crystal, ARMOR, HoneyTastyModifier, rainbowCatcher);

        addDefaultTraits(DreamtinkerMaterialIds.black_sapphire, not_like_was);

        callGetOrCreate(this, DreamtinkerMaterialIds.etherium).setTraits(MELEE_HARVEST, List.of(
                new ModifierEntry(ModifierIds.smite, 2),
                new ModifierEntry(explosive_hit.getId(), 1),
                new ModifierEntry(Ids.el_etherium, 1),
                new ModifierEntry(astral_break.getId(), 1)));
        addTraits(DreamtinkerMaterialIds.etherium, RANGED, ranged_shoot, explosive_hit);
        callGetOrCreate(this, DreamtinkerMaterialIds.etherium).setTraits(ARMOR, List.of(
                new ModifierEntry(ModifierIds.magicProtection, 2),
                new ModifierEntry(etherium_protection.getId(), 1),
                new ModifierEntry(ender_dodge.getId(), 1),
                new ModifierEntry(fly.getId(), 1)));

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

        callGetOrCreate(this, DreamtinkerMaterialIds.hallowed_gold).setTraits(MELEE_HARVEST, List.of(
                new ModifierEntry(ModifierIds.luck, 2)));
        addTraits(DreamtinkerMaterialIds.hallowed_gold, RANGED, TinkerModifiers.golden);

        callGetOrCreate(this, DreamtinkerMaterialIds.mnemonic_auric).setTraits(MELEE_HARVEST, List.of(
                new ModifierEntry(Ids.malum_haunted, 2),
                new ModifierEntry(malum_hex_staff, 1)));

        callGetOrCreate(this, DreamtinkerMaterialIds.soul_stained_steel)
                .setTraits(MELEE_HARVEST, List.of(new ModifierEntry(Ids.malum_haunted, 2),
                                                  new ModifierEntry(Ids.malum_tyrving, 1)));
        addTraits(DreamtinkerMaterialIds.soul_stained_steel, ARMOR, malum_soul_attributes);
        callGetOrCreate(this, DreamtinkerMaterialIds.soul_stained_steel)
                .setTraits(RANGED, List.of(new ModifierEntry(Ids.malum_haunted, 2),
                                           new ModifierEntry(malum_range_accelerator, 1)));
        addDefaultTraits(DreamtinkerMaterialIds.malignant_lead, malum_catalyst_lobber);
        addTraits(DreamtinkerMaterialIds.malignant_pewter, MELEE_HARVEST,
                  malum_distortion.getId(), Ids.malum_world_of_weight,
                  Ids.malum_edge_of_deliverance);
        addTraits(DreamtinkerMaterialIds.malignant_pewter, ARMOR, malum_malignant_attributes);

        addTraits(DreamtinkerMaterialIds.malignant_gluttony, MELEE_HARVEST, Ids.malum_haunted, malum_magic_attack.getId(), malum_erosion.getId(),
                  Ids.thundering_curse, malum_thirsty.getId(), malum_evolution.getId());
        addTraits(DreamtinkerMaterialIds.malignant_gluttony, ARMOR, Ids.thundering_curse, malum_thirsty.getId(), malum_spirit_defense.getId());
        addDefaultTraits(DreamtinkerMaterialIds.soul_rock, Ids.malum_spirit_plunder);


    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Material Modifier Provider";
    }

    private static MaterialTraits.Builder callGetOrCreate(AbstractMaterialTraitDataProvider self, MaterialId id) {
        try {
            Method m = AbstractMaterialTraitDataProvider.class.getDeclaredMethod("getOrCreateMaterialTraits", MaterialId.class);
            m.setAccessible(true);
            return (MaterialTraits.Builder) m.invoke(self, id);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
