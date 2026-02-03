package org.dreamtinker.dreamtinker.library.compact.ars_nouveau;

import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModifiableArmorPekHolder extends ArmorPerkHolder {
    private final List<List<List<PerkSlot>>> slotsForTierWithLevels;

    public ModifiableArmorPekHolder(ItemStack stack, List<List<List<PerkSlot>>> slotsForTierWithLevels) {
        super(stack, slotsForTierWithLevels.get(0));
        this.slotsForTierWithLevels = slotsForTierWithLevels;
    }

    public List<PerkSlot> getSlotsForTier() {
        int level = DTModifierCheck.getItemModifierNum(stack, NovaRegistry.nova_magic_armor.getId());
        level = Math.min(Math.max(level - 1, 0), slotsForTierWithLevels.size());
        List<PerkSlot> slots = new ArrayList<>(this.slotsForTierWithLevels.get(level).get(this.getTier()));
        slots.sort(Comparator.comparingInt((a) -> -a.value));
        return slots;
    }
}
