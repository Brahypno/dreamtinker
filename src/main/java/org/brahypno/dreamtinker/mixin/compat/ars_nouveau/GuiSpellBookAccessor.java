package org.brahypno.dreamtinker.mixin.compat.ars_nouveau;

import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiSpellBook.class, remap = false)
public interface GuiSpellBookAccessor {
    @Accessor("currentCostCache")
    int dreamtinker$getCurrentCostCache();
}
