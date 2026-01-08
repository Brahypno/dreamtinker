package org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaCast;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.HashMap;
import java.util.Map;

public class ModifiableSpellCaster  implements ISpellCaster {

    private Map<Integer, Spell> spells = new HashMap<>();
    private int slot;
    public IToolStackView tool;
    public String flavorText = "";
    public boolean isHidden;
    public String hiddenText = "";

    public ModifiableSpellCaster(IToolStackView tool) {
        this(tool.getPersistentData());
        this.tool = tool;
    }

    @NotNull
    @Override
    public Spell getSpell() {
        return spells.getOrDefault(getCurrentSlot(), new Spell());
    }

    @Override
    public@NotNull Spell getSpell(int slot) {
        return spells.getOrDefault(slot, new Spell());
    }

    @Override
    public int getMaxSlots() {
        return 1;
    }

    @Override
    public int getCurrentSlot() {
        return slot;
    }

    @Override
    public void setCurrentSlot(int slot) {
        this.slot = slot;
        writeItem(tool);
    }

    @Override
    public void setSpell(Spell spell, int slot) {
        this.spells.put(slot, spell);
        writeItem(tool);
    }

    @Override
    public void setSpell(Spell spell) {
        setSpell(spell, getCurrentSlot());
    }

    @Override
    public ParticleColor getColor(int slot) {
        return this.getSpell(slot).color;
    }

    @Override
    public void setFlavorText(String str) {
        this.flavorText = str;
        writeItem(tool);
    }

    @Override
    public String getSpellName(int slot) {
        return this.getSpell(slot).name;
    }

    @Override
    public String getSpellName() {
        return this.getSpellName(getCurrentSlot());
    }

    @Override
    public void setSpellName(String name) {
        setSpellName(name, getCurrentSlot());
    }

    @Override
    public void setSpellName(String name, int slot) {
        this.getSpell(slot).name = name;
        writeItem(tool);
    }

    @Override
    public void setSpellHidden(boolean hidden) {
        this.isHidden = hidden;
        writeItem(tool);
    }

    @Override
    public boolean isSpellHidden() {
        return isHidden;
    }

    @Override
    public void setHiddenRecipe(String recipe) {
        this.hiddenText = recipe;
        writeItem(tool);
    }

    @Override
    public String getHiddenRecipe() {
        return hiddenText;
    }

    @Override
    public String getFlavorText() {
        return flavorText == null ? "" : flavorText;
    }

    @Override
    public void setColor(ParticleColor color) {
        setColor(color, getCurrentSlot());
    }

    @Override
    public void setColor(ParticleColor color, int slot) {
        this.getSpell(slot).color = color;
        writeItem(tool);
    }

    @NotNull
    @Override
    public ConfiguredSpellSound getSound(int slot) {
        return this.getSpell(slot).sound;
    }

    @Override
    public void setSound(ConfiguredSpellSound sound) {
        this.setSound(sound, getCurrentSlot());
    }

    @Override
    public void setSound(ConfiguredSpellSound sound, int slot) {
        this.getSpell(slot).sound = sound;
        writeItem(tool);
    }

    @NotNull
    @Override
    public ParticleColor getColor() {
        return this.getSpell().color;
    }

    @Override
    public Map<Integer, Spell> getSpells() {
        return spells;
    }

    public ModDataNBT writeTag(ModDataNBT tag) {
        tag.putInt(Dreamtinker.getLocation("current_slot"), getCurrentSlot());
        tag.putString(Dreamtinker.getLocation("flavor"), getFlavorText());
        CompoundTag spellTag = new CompoundTag();

        for (int i = 0; i < getMaxSlots(); i++) {
            Spell spell = getSpell(i);
            spellTag.put("spell" + i, spell.serialize());
        }
        tag.put(Dreamtinker.getLocation("spells"), spellTag);
        tag.putInt(Dreamtinker.getLocation("spell_count"), getSpells().size());
        tag.putBoolean(Dreamtinker.getLocation("is_hidden"), isSpellHidden());
        tag.putString(Dreamtinker.getLocation("hidden_recipe"), getHiddenRecipe());
        return tag;
    }

    public ModifiableSpellCaster(ModDataNBT tag) {

        this.slot = tag.getInt(Dreamtinker.getLocation("current_slot"));
        this.flavorText = tag.getString(Dreamtinker.getLocation("flavor"));
        this.isHidden = tag.getBoolean(Dreamtinker.getLocation("is_hidden"));
        this.hiddenText = tag.getString(Dreamtinker.getLocation("hidden_recipe"));
        CompoundTag spellTag = tag.getCompound(Dreamtinker.getLocation("spells"));
        for (int i = 0; i < getMaxSlots(); i++) {
            if (spellTag.contains("spell" + i)) {
                Spell spell = Spell.fromTag(spellTag.getCompound("spell" + i));
                spells.put(i, spell);
            }
        }
    }

    public void writeItem(IToolStackView tool) {
        writeTag(tool.getPersistentData());
    }

    /**
     * Writes this compound data to the provided tag, stored with the caster ID.
     *
     * @param tool The tag to add this serialized tag to.
     */
    public void serializeOnTag(IToolStackView tool) {
        writeTag(tool.getPersistentData());
    }

    @Override
    public ResourceLocation getTagID() {
        return new ResourceLocation(ArsNouveau.MODID, "caster");
    }
}

