package org.dreamtinker.dreamtinker.data.providers;

import net.minecraft.data.PackOutput;
import org.dreamtinker.dreamtinker.data.DreamtinkerMaterialIds;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifer;
import slimeknights.tconstruct.library.data.material.AbstractMaterialTraitDataProvider;
import slimeknights.tconstruct.tools.data.ModifierIds;

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
        addTraits(DreamtinkerMaterialIds.valentinite, ARMOR,
                  ModifierIds.fireProtection, ModifierIds.fireProtection, ModifierIds.fireProtection, ModifierIds.fireProtection);

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
        addTraits(DreamtinkerMaterialIds.crying_obsidian, ARMOR,
                  DreamtinkerModifer.in_rain.getId(), ModifierIds.luck, ModifierIds.luck);

        addTraits(DreamtinkerMaterialIds.etherium, MELEE_HARVEST, ModifierIds.smite, ModifierIds.smite, DreamtinkerModifer.explosive_hit.getId(),
                  DreamtinkerModifer.astral_break.getId());
        addTraits(DreamtinkerMaterialIds.etherium, RANGED, DreamtinkerModifer.ranged_shoot, DreamtinkerModifer.explosive_hit);
        addTraits(DreamtinkerMaterialIds.etherium, ARMOR,
                  DreamtinkerModifer.etherium_protection.getId(), ModifierIds.magicProtection, ModifierIds.magicProtection, DreamtinkerModifer.fly.getId(),
                  DreamtinkerModifer.ender_dodge.getId());

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

}
