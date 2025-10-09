package org.dreamtinker.dreamtinker.tools.data.material;

import net.minecraft.data.PackOutput;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
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

import static slimeknights.tconstruct.library.materials.MaterialRegistry.*;

public class DreamtinkerMaterialTraitProvider extends AbstractMaterialTraitDataProvider {
    public DreamtinkerMaterialTraitProvider(PackOutput packOutput) {
        super(packOutput, new DreamtinkerMaterialDataProvider(packOutput));
    }

    @Override
    protected void addMaterialTraits() {
        addTraits(DreamtinkerMaterialIds.echo_alloy, ARMOR, ModifierIds.soulbound, DreamtinkerModifiers.echoed_defence.getId());
        addTraits(DreamtinkerMaterialIds.echo_alloy, MELEE_HARVEST, ModifierIds.soulbound, DreamtinkerModifiers.echoed_attack.getId());
        addTraits(DreamtinkerMaterialIds.echo_alloy, RANGED, ModifierIds.soulbound, DreamtinkerModifiers.echoed_attack.getId());

        addTraits(DreamtinkerMaterialIds.moonlight_ice, MELEE_HARVEST, DreamtinkerModifiers.Ids.moonlight_ice_info, DreamtinkerModifiers.glacial_river.getId());

        addDefaultTraits(DreamtinkerMaterialIds.valentinite, DreamtinkerModifiers.antimony_usage);
        callGetOrCreate(this, DreamtinkerMaterialIds.valentinite).setTraits(ARMOR, List.of(
                new ModifierEntry(ModifierIds.fireProtection, 4),
                new ModifierEntry(DreamtinkerModifiers.antimony_usage.getId(), 1)));

        addTraits(DreamtinkerMaterialIds.nigrescence_antimony, MELEE_HARVEST, DreamtinkerModifiers.ewige_widerkunft);
        addTraits(DreamtinkerMaterialIds.nigrescence_antimony, RANGED, DreamtinkerModifiers.burning_in_vain);
        addTraits(DreamtinkerMaterialIds.nigrescence_antimony, ARMOR, DreamtinkerModifiers.broken_vessel, DreamtinkerModifiers.ouroboric_hourglass);

        addTraits(DreamtinkerMaterialIds.metallivorous_stibium_lupus, MELEE_HARVEST,
                  DreamtinkerModifiers.the_wolf_wonder, DreamtinkerModifiers.the_wolf_answer, DreamtinkerModifiers.the_wolf_was);
        addTraits(DreamtinkerMaterialIds.metallivorous_stibium_lupus, RANGED,
                  DreamtinkerModifiers.the_wolf_wonder, DreamtinkerModifiers.the_wolf_answer, DreamtinkerModifiers.the_wolf_was);

        addTraits(DreamtinkerMaterialIds.star_regulus, RANGED, DreamtinkerModifiers.two_headed_seven);
        addTraits(DreamtinkerMaterialIds.star_regulus, ARMOR, DreamtinkerModifiers.as_one, DreamtinkerModifiers.fly);

        addTraits(DreamtinkerMaterialIds.crying_obsidian, MELEE_HARVEST, DreamtinkerModifiers.sharpened_with);
        addTraits(DreamtinkerMaterialIds.crying_obsidian, RANGED, DreamtinkerModifiers.isolde);
        addTraits(DreamtinkerMaterialIds.crying_obsidian, ARMOR, DreamtinkerModifiers.in_rain.getId(), ModifierIds.luck);

        callGetOrCreate(this, DreamtinkerMaterialIds.etherium).setTraits(MELEE_HARVEST, List.of(
                new ModifierEntry(ModifierIds.smite, 2),
                new ModifierEntry(DreamtinkerModifiers.explosive_hit.getId(), 1),
                new ModifierEntry(DreamtinkerModifiers.astral_break.getId(), 1)));
        addTraits(DreamtinkerMaterialIds.etherium, RANGED, DreamtinkerModifiers.ranged_shoot, DreamtinkerModifiers.explosive_hit);
        callGetOrCreate(this, DreamtinkerMaterialIds.etherium).setTraits(ARMOR, List.of(
                new ModifierEntry(ModifierIds.magicProtection, 2),
                new ModifierEntry(DreamtinkerModifiers.etherium_protection.getId(), 1),
                new ModifierEntry(DreamtinkerModifiers.ender_dodge.getId(), 1),
                new ModifierEntry(DreamtinkerModifiers.fly.getId(), 1)));

        addTraits(DreamtinkerMaterialIds.nefarious, MELEE_HARVEST, DreamtinkerModifiers.cursed_ring_bound.getId(), DreamtinkerModifiers.Ids.wither_body,
                  DreamtinkerModifiers.evil_attack.getId());
        addTraits(DreamtinkerMaterialIds.nefarious, RANGED, DreamtinkerModifiers.cursed_ring_bound.getId(), DreamtinkerModifiers.wither_shoot.getId(),
                  DreamtinkerModifiers.Ids.wither_body, DreamtinkerModifiers.evil_attack.getId());
        addTraits(DreamtinkerMaterialIds.nefarious, ARMOR, DreamtinkerModifiers.cursed_ring_bound.getId(), DreamtinkerModifiers.Ids.wither_body,
                  DreamtinkerModifiers.stone_heart.getId());

        addTraits(DreamtinkerMaterialIds.soul_etherium, MELEE_HARVEST, DreamtinkerModifiers.cursed_ring_bound.getId(), ModifierIds.soulbound,
                  DreamtinkerModifiers.Ids.soul_form, DreamtinkerModifiers.open_soul.getId(), DreamtinkerModifiers.exiles_faulty.getId());
        addTraits(DreamtinkerMaterialIds.soul_etherium, RANGED, DreamtinkerModifiers.cursed_ring_bound.getId(), ModifierIds.soulbound,
                  DreamtinkerModifiers.Ids.soul_form, DreamtinkerModifiers.open_soul.getId(), DreamtinkerModifiers.exiles_faulty.getId());
        addTraits(DreamtinkerMaterialIds.soul_etherium, ARMOR, DreamtinkerModifiers.cursed_ring_bound.getId(), ModifierIds.soulbound,
                  DreamtinkerModifiers.Ids.soul_form, DreamtinkerModifiers.soul_blessing.getId());

        addDefaultTraits(DreamtinkerMaterialIds.spirit_fabric, DreamtinkerModifiers.malum_distortion);
        addTraits(DreamtinkerMaterialIds.spirit_fabric, ARMOR, DreamtinkerModifiers.malum_spirit_attributes);

        callGetOrCreate(this, DreamtinkerMaterialIds.hallowed_gold).setTraits(MELEE_HARVEST, List.of(
                new ModifierEntry(ModifierIds.luck, 2)));
        addTraits(DreamtinkerMaterialIds.hallowed_gold, RANGED, TinkerModifiers.golden);

        callGetOrCreate(this, DreamtinkerMaterialIds.mnemonic_fragment).setTraits(MELEE_HARVEST, List.of(
                new ModifierEntry(DreamtinkerModifiers.Ids.malum_haunted, 2),
                new ModifierEntry(DreamtinkerModifiers.malum_hex_staff, 1)));

        callGetOrCreate(this, DreamtinkerMaterialIds.soul_stained_steel)
                .setTraits(MELEE_HARVEST, List.of(new ModifierEntry(DreamtinkerModifiers.Ids.malum_haunted, 2),
                                                  new ModifierEntry(DreamtinkerModifiers.Ids.malum_tyrving, 1)));
        addTraits(DreamtinkerMaterialIds.soul_stained_steel, ARMOR, DreamtinkerModifiers.malum_soul_attributes);
        callGetOrCreate(this, DreamtinkerMaterialIds.soul_stained_steel)
                .setTraits(RANGED, List.of(new ModifierEntry(DreamtinkerModifiers.Ids.malum_haunted, 2),
                                           new ModifierEntry(DreamtinkerModifiers.malum_range_accelerator, 1)));

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
