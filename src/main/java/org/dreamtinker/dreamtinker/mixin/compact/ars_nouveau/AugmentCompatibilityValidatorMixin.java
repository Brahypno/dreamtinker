package org.dreamtinker.dreamtinker.mixin.compact.ars_nouveau;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.common.spell.validation.AugmentCompatibilityValidator;
import com.hollingsworth.arsnouveau.common.spell.validation.SpellPhraseValidator;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.Spell.AugmentTinker;
import org.dreamtinker.dreamtinker.utils.DTHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.List;

@Mixin(value = AugmentCompatibilityValidator.class, remap = false)
public class AugmentCompatibilityValidatorMixin {

    @Unique
    private static int dreamtinker$getSpellPosition(Object spell) {
        try {
            Field field = spell.getClass().getDeclaredField("position");
            field.setAccessible(true);
            return field.getInt(spell);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Unique
    private static Object dreamtinker$getSpellPart(Object spell) {
        try {
            Field field = spell.getClass().getDeclaredField("spellPart");
            field.setAccessible(true);
            return field.get(spell);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject(
            method = "validatePhrase",
            at = @At(value = "TAIL")
    )
    private void dt$removeError(SpellPhraseValidator.SpellPhrase phrase, List<SpellValidationError> validationErrors, CallbackInfo ci) {
        validationErrors.removeIf(error ->
                                  {
                                      List<SpellPhraseValidator.SpellPhrase.SpellPartPosition<AbstractAugment>> spells =
                                              phrase.getAugmentPositionMap().get(AugmentTinker.INSTANCE.getRegistryName());

                                      boolean containsSpell = spells != null &&
                                                              spells.stream().anyMatch(
                                                                      spell -> dreamtinker$getSpellPosition(spell) == error.getPosition() &&
                                                                               dreamtinker$getSpellPart(spell) instanceof AugmentTinker);
                                      return containsSpell ||
                                             DTHelper.containsTranslationKey(error.makeTextComponentExisting(), AugmentTinker.INSTANCE.getLocalizationKey());
                                  });
    }
}
