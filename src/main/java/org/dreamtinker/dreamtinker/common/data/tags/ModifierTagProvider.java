package org.dreamtinker.dreamtinker.common.data.tags;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierTagProvider;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;

import static org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers.*;

public class ModifierTagProvider extends AbstractModifierTagProvider {
    public ModifierTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, Dreamtinker.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(DreamtinkerTagKeys.Modifiers.EL_CURSED_MODIFIERS)
            .add(ModifierIds.blindshot, ModifierIds.vintage, ModifierIds.flamestance, ModifierIds.entangled,
                 ModifierIds.crystalbound, ModifierIds.crystalstrike, ModifierIds.dense, ModifierIds.sharpweight,
                 ModifierIds.heavy, /*Ids.solid,*/
                 TinkerModifiers.jagged.getId(), TinkerModifiers.stonebound.getId(), TinkerModifiers.decay.getId(),
                 TinkerModifiers.selfDestructive.getId())
            .add(strong_heavy.getId(), echoed_attack.getId(), echoed_defence.getId(), glacial_river.getId(),
                 broken_vessel.getId(), ewige_widerkunft.getId(), ouroboric_hourglass.getId(), burning_in_vain.getId(),
                 the_wolf_wonder.getId(), the_wolf_was.getId(), as_one.getId(), Ids.with_tears, isolde.getId(),
                 mei.getId(), ender_dodge.getId(), explosive_hit.getId(), ranged_shoot.getId(), Ids.wither_body,
                 stone_heart.getId(), splendour_heart.getId(), Ids.why_i_cry, Ids.EULA, Ids.huge_ego, wait_until.getId(), Ids.FragileButBright,
                 Ids.thundering_curse, Ids.homunculusLifeCurse, absorption_hit.getId(), absorption_defense.getId(), despair_rain.getId(), despair_wind.getId())
            .addOptional(cursed_ring_bound.getId(), evil_attack.getId(), Ids.malum_tyrving, eldritch_pan.getId(), malum_thirsty.getId(),
                         Ids.el_nemesis_curse, Ids.el_sorrow, Ids.el_eternal_binding);
        this.tag(DreamtinkerTagKeys.Modifiers.EL_CURSED_RELIEF)
            .addOptional(weapon_books.getId(), eldritch_pan.getId(), exiles_faulty.getId());
        this.tag(TinkerTags.Modifiers.MELEE_UPGRADES)
            .add(strong_explode.getId())
            .addOptional(Ids.malum_haunted, Ids.malum_animated)
            .addOptional(Ids.el_wrath, Ids.el_torrent, Ids.el_slayer);
        this.tag(TinkerTags.Modifiers.RANGED_UPGRADES)
            .add(Ids.icy_memory, Ids.hate_memory);
        this.tag(TinkerTags.Modifiers.GENERAL_UPGRADES)
            .add(Ids.soul_upgrade);
        this.tag(TinkerTags.Modifiers.HARVEST_UPGRADES)
            .add(foundation_will.getId());
        this.tag(TinkerTags.Modifiers.GENERAL_SLOTLESS)
            .add(mei.getId())
            .addOptional(Ids.el_eternal_binding, Ids.el_sorrow, Ids.el_nemesis_curse);
        this.tag(TinkerTags.Modifiers.BONUS_SLOTLESS)
            .add(Ids.huge_ego);
        this.tag(TinkerTags.Modifiers.MELEE_ABILITIES)
            .add(real_sweep.getId(), Ids.continuous_explode, flaming_memory.getId())
            .addOptional(ender_slayer.getId(), weapon_books.getId(), desolation_ring.getId(), eldritch_pan.getId());
        this.tag(TinkerTags.Modifiers.GENERAL_ABILITIES)
            .add(life_looting.getId());
        this.tag(TinkerTags.Modifiers.BLOCK_WHILE_CHARGING)
            .add(memory_base.getId());
        this.tag(TinkerTags.Modifiers.INTERACTION_ABILITIES)
            .add(memory_base.getId())
            .addOptional(Ids.malum_ascension, Ids.malum_rebound);
        this.tag(DreamtinkerTagKeys.Modifiers.MALUM_EXPOSE_SOUL)
            .addOptional(malum_hex_staff.getId(), malum_base.getId(), malum_distortion.getId(), malum_spirit_attributes.getId(),
                         Ids.malum_animated, Ids.malum_rebound, Ids.malum_ascension, Ids.malum_haunted, Ids.malum_replenishing, Ids.malum_spirit_plunder,
                         malum_soul_attributes.getId(), malum_malignant_attributes.getId(), Ids.malum_edge_of_deliverance, Ids.malum_tyrving,
                         Ids.malum_world_of_weight, malum_magic_attack.getId(), malum_range_accelerator.getId(), malum_evolution.getId());
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Modifier Tag Provider.";
    }
}
