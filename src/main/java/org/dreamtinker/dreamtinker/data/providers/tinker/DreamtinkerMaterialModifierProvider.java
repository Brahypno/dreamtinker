package org.dreamtinker.dreamtinker.data.providers.tinker;

import net.minecraft.data.PackOutput;
import org.dreamtinker.dreamtinker.data.DreamtinkerMaterialIds;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifers;
import slimeknights.tconstruct.library.data.material.AbstractMaterialTraitDataProvider;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.traits.MaterialTraits;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.tools.data.ModifierIds;

import java.lang.reflect.Method;
import java.util.List;

import static slimeknights.tconstruct.library.materials.MaterialRegistry.*;

public class DreamtinkerMaterialModifierProvider extends AbstractMaterialTraitDataProvider {
    public DreamtinkerMaterialModifierProvider(PackOutput packOutput) {
        super(packOutput, new DreamtinkerMaterialDataProvider(packOutput));
    }

    @Override
    protected void addMaterialTraits() {
        addTraits(DreamtinkerMaterialIds.echo_shard, ARMOR, ModifierIds.soulbound, DreamtinkerModifers.echoed_defence.getId());
        addTraits(DreamtinkerMaterialIds.echo_shard, MELEE_HARVEST, ModifierIds.soulbound, DreamtinkerModifers.echoed_attack.getId());
        addTraits(DreamtinkerMaterialIds.echo_shard, RANGED, ModifierIds.soulbound, DreamtinkerModifers.echoed_attack.getId());

        addTraits(DreamtinkerMaterialIds.moonlight_ice, MELEE_HARVEST, DreamtinkerModifers.glacial_river);

        addDefaultTraits(DreamtinkerMaterialIds.valentinite, DreamtinkerModifers.antimony_usage);
        callGetOrCreate(this, DreamtinkerMaterialIds.valentinite).setTraits(ARMOR, List.of(
                new ModifierEntry[]{new ModifierEntry(ModifierIds.fireProtection, 4),
                        new ModifierEntry(DreamtinkerModifers.antimony_usage.getId(), 1)}));

        addTraits(DreamtinkerMaterialIds.nigrescence_antimony, MELEE_HARVEST, DreamtinkerModifers.ewige_widerkunft);
        addTraits(DreamtinkerMaterialIds.nigrescence_antimony, RANGED, DreamtinkerModifers.burning_in_vain);
        addTraits(DreamtinkerMaterialIds.nigrescence_antimony, ARMOR, DreamtinkerModifers.broken_vessel, DreamtinkerModifers.ouroboric_hourglass);

        addTraits(DreamtinkerMaterialIds.metallivorous_stibium_lupus, MELEE_HARVEST,
                  DreamtinkerModifers.the_wolf_wonder, DreamtinkerModifers.the_wolf_answer, DreamtinkerModifers.the_wolf_was);
        addTraits(DreamtinkerMaterialIds.metallivorous_stibium_lupus, RANGED,
                  DreamtinkerModifers.the_wolf_wonder, DreamtinkerModifers.the_wolf_answer, DreamtinkerModifers.the_wolf_was);

        addTraits(DreamtinkerMaterialIds.star_regulus, RANGED, DreamtinkerModifers.two_headed_seven);
        addTraits(DreamtinkerMaterialIds.star_regulus, ARMOR, DreamtinkerModifers.as_one, DreamtinkerModifers.fly);

        addTraits(DreamtinkerMaterialIds.crying_obsidian, MELEE_HARVEST, DreamtinkerModifers.sharpened_with);
        addTraits(DreamtinkerMaterialIds.crying_obsidian, RANGED, DreamtinkerModifers.isolde);
        callGetOrCreate(this, DreamtinkerMaterialIds.crying_obsidian).setTraits(ARMOR, List.of(
                new ModifierEntry[]{new ModifierEntry(ModifierIds.luck, 2),
                        new ModifierEntry(DreamtinkerModifers.in_rain.getId(), 1)}));

        callGetOrCreate(this, DreamtinkerMaterialIds.etherium).setTraits(MELEE_HARVEST, List.of(new ModifierEntry[]{new ModifierEntry(ModifierIds.smite, 2),
                new ModifierEntry(DreamtinkerModifers.explosive_hit.getId(), 1), new ModifierEntry(
                DreamtinkerModifers.astral_break.getId(), 1)}));
        addTraits(DreamtinkerMaterialIds.etherium, RANGED, DreamtinkerModifers.ranged_shoot, DreamtinkerModifers.explosive_hit);
        callGetOrCreate(this, DreamtinkerMaterialIds.etherium).setTraits(ARMOR, List.of(
                new ModifierEntry[]{new ModifierEntry(ModifierIds.magicProtection, 2),
                        new ModifierEntry(DreamtinkerModifers.etherium_protection.getId(), 1),
                        new ModifierEntry(DreamtinkerModifers.ender_dodge.getId(), 1),
                        new ModifierEntry(DreamtinkerModifers.fly.getId(), 1)}));

        addTraits(DreamtinkerMaterialIds.nefarious, MELEE_HARVEST, DreamtinkerModifers.cursed_ring_bound.getId(), DreamtinkerModifers.Ids.wither_body,
                  DreamtinkerModifers.evil_attack.getId());
        addTraits(DreamtinkerMaterialIds.nefarious, RANGED, DreamtinkerModifers.cursed_ring_bound.getId(), DreamtinkerModifers.wither_shoot.getId(),
                  DreamtinkerModifers.Ids.wither_body, DreamtinkerModifers.evil_attack.getId());
        addTraits(DreamtinkerMaterialIds.nefarious, ARMOR, DreamtinkerModifers.cursed_ring_bound.getId(), DreamtinkerModifers.Ids.wither_body,
                  DreamtinkerModifers.stone_heart.getId());

        addTraits(DreamtinkerMaterialIds.soul_etherium, MELEE_HARVEST, DreamtinkerModifers.cursed_ring_bound.getId(), ModifierIds.soulbound,
                  DreamtinkerModifers.Ids.soul_form, DreamtinkerModifers.open_soul.getId(), DreamtinkerModifers.exiles_faulty.getId());
        addTraits(DreamtinkerMaterialIds.soul_etherium, RANGED, DreamtinkerModifers.cursed_ring_bound.getId(), ModifierIds.soulbound,
                  DreamtinkerModifers.Ids.soul_form, DreamtinkerModifers.open_soul.getId(), DreamtinkerModifers.exiles_faulty.getId());
        addTraits(DreamtinkerMaterialIds.soul_etherium, ARMOR, DreamtinkerModifers.cursed_ring_bound.getId(), ModifierIds.soulbound,
                  DreamtinkerModifers.Ids.soul_form, DreamtinkerModifers.soul_blessing.getId());

    }

    @Override
    public String getName() {
        return "Dreamtinker Material Modifier Provider";
    }

    @SuppressWarnings("unchecked")
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
