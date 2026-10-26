package org.brahypno.dreamtinker.mixin.compat.ars_nouveau;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.spell.validation.SpellPhraseValidator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SpellPhraseValidator.SpellPhrase.SpellPartPosition.class, remap = false)
public interface SpellPartPositionAccessor {
    @Accessor("position")
    int dreamtinker$getPosition();

    @Accessor("spellPart")
    AbstractSpellPart dreamtinker$getSpellPart();
}
