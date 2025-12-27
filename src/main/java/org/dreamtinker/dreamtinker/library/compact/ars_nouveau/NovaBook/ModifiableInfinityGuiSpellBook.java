package org.dreamtinker.dreamtinker.library.compact.ars_nouveau.NovaBook;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.ISpellValidator;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.client.gui.book.InfinityGuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiSpellSlot;
import com.hollingsworth.arsnouveau.client.gui.buttons.InfinityCraftingButton;
import com.hollingsworth.arsnouveau.common.spell.validation.CombinedSpellValidator;
import com.hollingsworth.arsnouveau.common.spell.validation.GlyphMaxTierValidator;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;

import java.util.ArrayList;

public class ModifiableInfinityGuiSpellBook extends InfinityGuiSpellBook {

    public ModifiableInfinityGuiSpellBook(InteractionHand hand) {
        super(hand);
        ItemStack heldStack;
        if (Minecraft.getInstance().player != null){
            heldStack = Minecraft.getInstance().player.getItemInHand(hand);
            if (heldStack.getItem() instanceof ModifiableSpellBook mb){
                this.unlockedSpells = new ArrayList<>(GlyphRegistry.getSpellpartMap().values());
                int tier = mb.getTier(heldStack).value;
                this.spellValidator = new CombinedSpellValidator(
                        new ISpellValidator[]{ArsNouveauAPI.getInstance().getSpellCraftingSpellValidator(), new GlyphMaxTierValidator(tier)});
                int ui_slots = DTModifierCheck.getItemModifierNum(heldStack, DreamtinkerModifiers.Ids.nova_spell_slots);
                numLinks += ui_slots;
            }
        }
    }

    public static void open(InteractionHand hand) {
        Minecraft.getInstance().setScreen(new ModifiableInfinityGuiSpellBook(hand));
    }

    public void init() {
        this.bookLeft = this.width / 2 - 145;
        this.bookTop = this.height / 2 - 97;
        ISpellCaster caster = CasterUtil.getCaster(this.bookStack);
        int selectedSlot = caster.getCurrentSlot();
        for (int i = 0; i < this.numLinks; ++i) {
            int offset = i >= 5 ? 14 : 0;
            InfinityCraftingButton cell = new InfinityCraftingButton(this.bookLeft + 19 + 24 * i + offset, this.bookTop + 194 - 47, this::onCraftingSlotClick);
            cell.slotNum = i;
            this.addRenderableWidget(cell);
            this.craftingCells.add(cell);
        }
        for (int i = 0; i < this.numLinks; ++i) {
            String name = caster.getSpellName(i);
            GuiSpellSlot slot = new GuiSpellSlot(this.bookLeft + 281, this.bookTop + 1 + 15 * (i + 1), i, name, this::onSlotChange);
            if (i == selectedSlot){
                this.selected_slot = slot;
                this.selectedSpellSlot = i;
                slot.isSelected = true;
            }

            this.addRenderableWidget(slot);
        }
        numLinks = 0;
        super.init();
    }
}
