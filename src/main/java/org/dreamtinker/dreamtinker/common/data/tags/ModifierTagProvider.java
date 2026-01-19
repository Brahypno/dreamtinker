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
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DAMAGE_UPGRADES;

public class ModifierTagProvider extends AbstractModifierTagProvider {
    public ModifierTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, Dreamtinker.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(DreamtinkerTagKeys.Modifiers.EL_CURSED_MODIFIERS)
            .add(ModifierIds.blindshot, ModifierIds.vintage, ModifierIds.flamestance, ModifierIds.entangled,
                 ModifierIds.crystalbound, ModifierIds.crystalstrike, ModifierIds.dense,
                 ModifierIds.heavy, /*Ids.solid,*/
                 ModifierIds.jagged, ModifierIds.stonebound, TinkerModifiers.decay.getId(),
                 TinkerModifiers.selfDestructive.getId())
            .add(strong_heavy.getId(), echoed_attack.getId(), echoed_defence.getId(), glacial_river.getId(),
                 broken_vessel.getId(), ewige_widerkunft.getId(), ouroboric_hourglass.getId(), burning_in_vain.getId(),
                 the_wolf_wonder.getId(), the_wolf_was.getId(), as_one.getId(), Ids.with_tears, isolde.getId(),
                 mei.getId(), ender_dodge.getId(), explosive_hit.getId(), ranged_shoot.getId(), Ids.wither_body,
                 stone_heart.getId(), splendour_heart.getId(), Ids.why_i_cry, Ids.EULA, Ids.huge_ego, wait_until.getId(), Ids.FragileButBright,
                 Ids.thundering_curse, Ids.homunculusLifeCurse, absorption_hit.getId(), absorption_defense.getId(), despair_rain.getId(), despair_wind.getId(),
                 rainbowCatcher.getId(), not_like_was.getId(), light_in_dark.getId(), light_emanation.getId(), Ids.lunarAttractive)
            .addOptional(cursed_ring_bound.getId(), evil_attack.getId(), Ids.malum_tyrving, eldritch_pan.getId(), malum_thirsty.getId(),
                         Ids.el_nemesis_curse, Ids.el_sorrow, Ids.el_eternal_binding);
        this.tag(DreamtinkerTagKeys.Modifiers.EL_CURSED_RELIEF)
            .addOptional(weapon_books.getId(), eldritch_pan.getId(), exiles_faulty.getId());
        this.tag(TinkerTags.Modifiers.MELEE_UPGRADES)
            .addOptional(Ids.malum_haunted, Ids.malum_animated, Ids.bic_frostbitten, Ids.bic_intoxicating);
        this.tag(TinkerTags.Modifiers.RANGED_UPGRADES)
            .add(Ids.icy_memory, Ids.hate_memory, Ids.soul_core);
        this.tag(TinkerTags.Modifiers.GENERAL_UPGRADES)
            .add(Ids.soul_upgrade, Ids.weapon_dreams_order, Ids.weapon_dreams_filter, TheEnd.getId())
            .addOptional(Ids.bic_dark_armor_plate);
        this.tag(TinkerTags.Modifiers.GENERAL_SLOTLESS)
            .add(mei.getId(), fox_blessing.getId())
            .addOptional(Ids.el_eternal_binding, Ids.el_sorrow, Ids.el_nemesis_curse, Ids.nova_spell_tiers);
        this.tag(DAMAGE_UPGRADES)
            .add(Ids.all_slayer, Ids.the_romantic, strong_explode.getId(), signal_axe.getId(), Ids.wrath, Ids.torrent)
            .addOptional(ender_slayer.getId());
        this.tag(TinkerTags.Modifiers.BONUS_SLOTLESS)
            .add(Ids.huge_ego, Ids.five_creations);
        this.tag(TinkerTags.Modifiers.MELEE_ABILITIES)
            .add(real_sweep.getId(), Ids.continuous_explode, flaming_memory.getId())
            .addOptional(weapon_books.getId(), desolation_ring.getId(), eldritch_pan.getId());
        this.tag(TinkerTags.Modifiers.RANGED_ABILITIES)
            .add(Ids.force_to_explosion, love_shooting.getId());
        this.tag(TinkerTags.Modifiers.GENERAL_ABILITIES)
            .add(life_looting.getId());
        this.tag(TinkerTags.Modifiers.BLOCK_WHILE_CHARGING)
            .add(memory_base.getId());
        this.tag(TinkerTags.Modifiers.INTERACTION_ABILITIES)
            //.add()
            .addOptional(Ids.malum_ascension, Ids.malum_rebound);
        this.tag(TinkerTags.Modifiers.GENERAL_ARMOR_UPGRADES)
            .addOptional(spiritual_weapon_transformation.getId(), Ids.nova_mana_reduce);
        this.tag(TinkerTags.Modifiers.GENERAL_ARMOR_ABILITIES)
            .addOptional(nova_magic_armor.getId());
        this.tag(DreamtinkerTagKeys.Modifiers.MALUM_EXPOSE_SOUL)
            .addOptional(malum_hex_staff.getId(), malum_base.getId(), malum_distortion.getId(), malum_spirit_attributes.getId(),
                         Ids.malum_animated, Ids.malum_rebound, Ids.malum_ascension, Ids.malum_haunted, Ids.malum_replenishing, Ids.malum_spirit_plunder,
                         malum_soul_attributes.getId(), malum_malignant_attributes.getId(), Ids.malum_edge_of_deliverance, Ids.malum_tyrving,
                         Ids.malum_world_of_weight, malum_magic_attack.getId(), malum_range_accelerator.getId(), malum_evolution.getId());
        this.tag(TinkerTags.Modifiers.OVERSLIME_FRIEND)
            .add(Ids.shadow_blessing);
        this.tag(TinkerTags.Modifiers.EXTRACT_MODIFIER_BLACKLIST)
            .addOptional(Ids.nova_spell_tiers);
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Modifier Tag Provider.";
    }
}
