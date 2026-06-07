package org.dreamtinker.dreamtinker.common.data.tags;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierTagProvider;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;

import static org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaRegistry.*;
import static org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers.*;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.COSMETIC_SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DAMAGE_UPGRADES;

public class ModifierTagProvider extends AbstractModifierTagProvider {
    public ModifierTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, Dreamtinker.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        final String TIAC = "tinkers_advanced";
        final String TIT = "tinkers_thinking";
        tag(DreamtinkerTagKeys.Modifiers.ArmorWorkingWhenUnequipped)
                .add(as_one.getId());
        this.tag(DreamtinkerTagKeys.Modifiers.EL_CURSED_MODIFIERS)
            .add(ModifierIds.weak, ModifierIds.blindshot, ModifierIds.erratic, ModifierIds.vintage, ModifierIds.flamestance,
                 ModifierIds.crystalbound, ModifierIds.crystalstrike, ModifierIds.dense, ModifierIds.spiny,
                 TinkerModifiers.decay.getId(),
                 TinkerModifiers.selfDestructive.getId())
            .add(strong_heavy.getId(), glacial_river.getId(),
                 broken_vessel.getId(), ewige_widerkunft.getId(), ouroboric_hourglass.getId(), burning_in_vain.getId(),
                 the_wolf_was.getId(), Ids.with_tears, isolde.getId(), mei.getId(), explosive_hit.getId(), Ids.wither_body,
                 stone_heart.getId(), splendour_heart.getId(), why_i_cry.getId(), Ids.huge_ego, wait_until.getId(),
                 Ids.thundering_curse, homunculus_life_curse.getId(), absorption_defense.getId(), despair_rain.getId(), despair_wind.getId(),
                 light_in_dark.getId(), light_emanation.getId(), hiddenHit.getId(), signal_axe.getId(), Ids.golden_face)
            .addOptional(cursed_ring_bound.getId(), evil_attack.getId(), eldritch_pan.getId(), malum_thirsty.getId(),
                         Ids.el_nemesis_curse, Ids.el_sorrow, Ids.el_eternal_binding)
            .addOptional(new ResourceLocation(TIAC, "fragile"), new ResourceLocation(TIAC, "disintegrate"), new ResourceLocation(TIAC, "heavy_material"),
                         new ResourceLocation(TIAC, "blazing"))
            .addOptional(new ResourceLocation(TIT, "nonsense"), new ResourceLocation(TIT, "nocturnal"), new ResourceLocation(TIT, "sharp_circumstance"))
            .addOptional(new ResourceLocation("mushroom_daydream.arrogant"));
        this.tag(DreamtinkerTagKeys.Modifiers.EL_CURSED_RELIEF)
            .addOptional(weapon_books.getId(), eldritch_pan.getId(), exiles_faulty.getId());
        this.tag(TinkerTags.Modifiers.MELEE_UPGRADES)
            .add(born_with_me.getId())
            .addOptional(Ids.malum_haunted, Ids.malum_animated, Ids.bic_frostbitten, Ids.bic_intoxicating);
        this.tag(TinkerTags.Modifiers.RANGED_UPGRADES)
            .add(Ids.icy_memory, Ids.hate_memory, Ids.soul_core, Ids.wrath, Ids.torrent);
        this.tag(TinkerTags.Modifiers.GENERAL_UPGRADES)
            .add(Ids.weapon_dreams_order, Ids.weapon_dreams_filter, TheEnd.getId())
            .addOptional(Ids.bic_dark_armor_plate);
        this.tag(TinkerTags.Modifiers.HARVEST_UPGRADES)
            .add(Ids.falsify_fate);
        this.tag(TinkerTags.Modifiers.SPECIAL_DEFENSE)
            .add(Ids.reprise_protection);

        this.tag(TinkerTags.Modifiers.GENERAL_SLOTLESS)
            .add(mei.getId(), fox_blessing.getId())
            .addOptional(Ids.el_eternal_binding, Ids.el_sorrow, Ids.el_nemesis_curse, Ids.nova_spell_tiers);
        this.tag(DAMAGE_UPGRADES)
            .add(Ids.all_slayer, Ids.the_romantic, Ids.strong_explode, signal_axe.getId())
            .addOptional(ender_slayer.getId());
        this.tag(TinkerTags.Modifiers.BONUS_SLOTLESS)
            .add(Ids.huge_ego, Ids.five_creations, Ids.soul_upgrade, Ids.abyss_inside, Ids.meta_morphosis)
            .addOptional(Ids.many_us, Ids.blighted_sigil, Ids.ashen_soul, Ids.naughty_chaos, Ids.cosmogony_tetrad, Ids.otherworld_precious);
        this.tag(COSMETIC_SLOTLESS)
            .addOptional(occ_view.getId());

        this.tag(TinkerTags.Modifiers.MELEE_ABILITIES)
            .add(Ids.continuous_explode, flaming_memory.getId(), Ids.curse_fire, rainbow_lights.getId())
            .addOptional(weapon_books.getId(), desolation_ring.getId(), eldritch_pan.getId(), nova_enchanter_sword.getId());
        this.tag(TinkerTags.Modifiers.RANGED_ABILITIES)
            .add(Ids.force_to_explosion, love_shooting.getId(), Ids.curse_fire)
            .addOptional(nova_spell_bow.getId(), nova_wand.getId());
        this.tag(TinkerTags.Modifiers.GENERAL_ARMOR_ABILITIES)
            .add(Ids.curse_fire)
            .addOptional(nova_magic_armor.getId());
        this.tag(TinkerTags.Modifiers.GENERAL_ABILITIES)
            .add(life_looting.getId());

        this.tag(TinkerTags.Modifiers.BLOCK_WHILE_CHARGING)
            .add(memory_base.getId());
        this.tag(TinkerTags.Modifiers.INTERACTION_ABILITIES)
            //.add()
            .addOptional(Ids.malum_ascension, Ids.malum_rebound);
        this.tag(TinkerTags.Modifiers.GENERAL_ARMOR_UPGRADES)
            .add(virtual_dodge.getId(), Ids.sweet_death, Ids.last_kiss)
            .addOptional(spiritual_weapon_transformation.getId(), Ids.nova_mana_reduce, annihilator_armor_power.getId());
        this.tag(DreamtinkerTagKeys.Modifiers.MALUM_EXPOSE_SOUL)
            .addOptional(malum_hex_staff.getId(), malum_base.getId(), malum_distortion.getId(), malum_spirit_attributes.getId(),
                         Ids.malum_animated, Ids.malum_rebound, Ids.malum_ascension, Ids.malum_haunted, Ids.malum_replenishing, Ids.malum_spirit_plunder,
                         malum_soul_attributes.getId(), malum_malignant_attributes.getId(), Ids.malum_edge_of_deliverance, Ids.malum_tyrving,
                         Ids.malum_world_of_weight, malum_magic_attack.getId(), malum_range_accelerator.getId(), malum_evolution.getId());
        this.tag(TinkerTags.Modifiers.OVERSLIME_FRIEND)
            .add(Ids.shadow_blessing, over_sticky.getId(), Ids.sticky_string);
        this.tag(TinkerTags.Modifiers.EXTRACT_MODIFIER_BLACKLIST)
            .addOptional(Ids.nova_spell_tiers);
    }

    @Override
    public @NotNull String getName() {
        return "Dreamtinker Modifier Tag Provider.";
    }
}
