package org.dreamtinker.dreamtinker.data.providers.tinker;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.data.DreamtinkerTagkeys;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierTagProvider;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;

import static org.dreamtinker.dreamtinker.register.DreamtinkerModifers.*;

public class DreamtinkerModifierTagProvider extends AbstractModifierTagProvider {
    public DreamtinkerModifierTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(packOutput, Dreamtinker.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(DreamtinkerTagkeys.Modifiers.EL_CURSED_MODIFIERS)
            .add(ModifierIds.blindshot, ModifierIds.vintage, ModifierIds.flamestance, ModifierIds.entangled,
                 ModifierIds.crystalbound, ModifierIds.crystalstrike, ModifierIds.dense, ModifierIds.sharpweight,
                 ModifierIds.heavy, /*Ids.solid,*/
                 TinkerModifiers.jagged.getId(), TinkerModifiers.stonebound.getId(), TinkerModifiers.decay.getId(),
                 TinkerModifiers.selfDestructive.getId())
            .add(strong_heavy.getId(), echoed_attack.getId(), echoed_defence.getId(), glacial_river.getId(),
                 broken_vessel.getId(), ewige_widerkunft.getId(), ouroboric_hourglass.getId(), burning_in_vain.getId(),
                 the_wolf_wonder.getId(), the_wolf_was.getId(), as_one.getId(), in_rain.getId(), isolde.getId(),
                 mei.getId(), ender_dodge.getId(), explosive_hit.getId(), ranged_shoot.getId(), Ids.wither_body,
                 stone_heart.getId())
            .addOptional(cursed_ring_bound.getId(), evil_attack.getId());
        this.tag(DreamtinkerTagkeys.Modifiers.EL_CURSED_RELIEF)
            .addOptional(weapon_books.getId(), eldritch_pan.getId(), exiles_faulty.getId());
        this.tag(TinkerTags.Modifiers.MELEE_UPGRADES)
            .add(strong_explode.getId());
        this.tag(TinkerTags.Modifiers.GENERAL_UPGRADES)
            .add(Ids.soul_upgrade);
        this.tag(TinkerTags.Modifiers.GENERAL_SLOTLESS)
            .add(mei.getId());
        this.tag(TinkerTags.Modifiers.MELEE_ABILITIES)
            .add(realsweep.getId(), Ids.continuous_explode)
            .addOptional(ender_slayer.getId(), weapon_books.getId(), desolation_ring.getId(), eldritch_pan.getId());
        this.tag(TinkerTags.Modifiers.GENERAL_ABILITIES)
            .add(life_looting.getId());
    }

    @Override
    public String getName() {
        return "Dreamtinker Modifier Tag Provider.";
    }
}
