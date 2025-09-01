package org.dreamtinker.dreamtinker.data.providers;

import net.minecraft.data.PackOutput;
import org.dreamtinker.dreamtinker.data.DreamtinkerMaterialIds;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifer;
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
        addTraits(DreamtinkerMaterialIds.echo_shard, ARMOR, ModifierIds.soulbound, DreamtinkerModifer.echoed_defence.getId());
        addTraits(DreamtinkerMaterialIds.echo_shard, MELEE_HARVEST, ModifierIds.soulbound, DreamtinkerModifer.echoed_attack.getId());
        addTraits(DreamtinkerMaterialIds.echo_shard, RANGED, ModifierIds.soulbound, DreamtinkerModifer.echoed_attack.getId());

        addTraits(DreamtinkerMaterialIds.moonlight_ice, MELEE_HARVEST, DreamtinkerModifer.glacial_river);

        addDefaultTraits(DreamtinkerMaterialIds.valentinite, DreamtinkerModifer.antimony_usage);
        callGetOrCreate(this, DreamtinkerMaterialIds.valentinite).setTraits(ARMOR, List.of(
                new ModifierEntry[]{new ModifierEntry(ModifierIds.fireProtection, 4),
                        new ModifierEntry(DreamtinkerModifer.antimony_usage.getId(), 1)}));

        addTraits(DreamtinkerMaterialIds.nigrescence_antimony, MELEE_HARVEST, DreamtinkerModifer.ewige_widerkunft);
        addTraits(DreamtinkerMaterialIds.nigrescence_antimony, RANGED, DreamtinkerModifer.burning_in_vain);
        addTraits(DreamtinkerMaterialIds.nigrescence_antimony, ARMOR, DreamtinkerModifer.broken_vessel, DreamtinkerModifer.ouroboric_hourglass);

        addTraits(DreamtinkerMaterialIds.metallivorous_stibium_lupus, MELEE_HARVEST,
                  DreamtinkerModifer.the_wolf_wonder, DreamtinkerModifer.the_wolf_answer, DreamtinkerModifer.the_wolf_was);
        addTraits(DreamtinkerMaterialIds.metallivorous_stibium_lupus, RANGED,
                  DreamtinkerModifer.the_wolf_wonder, DreamtinkerModifer.the_wolf_answer, DreamtinkerModifer.the_wolf_was);

        addTraits(DreamtinkerMaterialIds.star_regulus, RANGED, DreamtinkerModifer.two_headed_seven);
        addTraits(DreamtinkerMaterialIds.star_regulus, ARMOR, DreamtinkerModifer.as_one, DreamtinkerModifer.fly);

        addTraits(DreamtinkerMaterialIds.crying_obsidian, MELEE_HARVEST, DreamtinkerModifer.sharpened_with);
        addTraits(DreamtinkerMaterialIds.crying_obsidian, RANGED, DreamtinkerModifer.isolde);
        callGetOrCreate(this, DreamtinkerMaterialIds.crying_obsidian).setTraits(ARMOR, List.of(
                new ModifierEntry[]{new ModifierEntry(ModifierIds.luck, 2),
                        new ModifierEntry(DreamtinkerModifer.in_rain.getId(), 1)}));

        callGetOrCreate(this, DreamtinkerMaterialIds.etherium).setTraits(MELEE_HARVEST, List.of(new ModifierEntry[]{new ModifierEntry(ModifierIds.smite, 2),
                new ModifierEntry(DreamtinkerModifer.explosive_hit.getId(), 1), new ModifierEntry(
                DreamtinkerModifer.astral_break.getId(), 1)}));
        addTraits(DreamtinkerMaterialIds.etherium, RANGED, DreamtinkerModifer.ranged_shoot, DreamtinkerModifer.explosive_hit);
        callGetOrCreate(this, DreamtinkerMaterialIds.etherium).setTraits(ARMOR, List.of(
                new ModifierEntry[]{new ModifierEntry(ModifierIds.magicProtection, 2),
                        new ModifierEntry(DreamtinkerModifer.etherium_protection.getId(), 1),
                        new ModifierEntry(DreamtinkerModifer.ender_dodge.getId(), 1),
                        new ModifierEntry(DreamtinkerModifer.fly.getId(), 1)}));

        addTraits(DreamtinkerMaterialIds.nefarious, MELEE_HARVEST, DreamtinkerModifer.cursed_ring_bound, DreamtinkerModifer.wither_body,
                  DreamtinkerModifer.evil_attack);
        addTraits(DreamtinkerMaterialIds.nefarious, RANGED, DreamtinkerModifer.cursed_ring_bound, DreamtinkerModifer.wither_shoot,
                  DreamtinkerModifer.wither_body);
        addTraits(DreamtinkerMaterialIds.nefarious, ARMOR, DreamtinkerModifer.cursed_ring_bound, DreamtinkerModifer.wither_body,
                  DreamtinkerModifer.stone_heart);

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
