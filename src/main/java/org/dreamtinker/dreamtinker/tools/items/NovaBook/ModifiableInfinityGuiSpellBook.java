package org.dreamtinker.dreamtinker.tools.items.NovaBook;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.ISpellValidator;
import com.hollingsworth.arsnouveau.client.gui.book.InfinityGuiSpellBook;
import com.hollingsworth.arsnouveau.common.spell.validation.CombinedSpellValidator;
import com.hollingsworth.arsnouveau.common.spell.validation.GlyphMaxTierValidator;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

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
            }
        }
    }

    public static void open(InteractionHand hand) {
        Minecraft.getInstance().setScreen(new ModifiableInfinityGuiSpellBook(hand));
    }
}
