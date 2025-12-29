package org.dreamtinker.dreamtinker.mixin.NovaMixin;

import com.hollingsworth.arsnouveau.common.spell.validation.AugmentCompatibilityValidator;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.dreamtinker.dreamtinker.library.compact.ars_nouveau.Spell.AugmentTinker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(value = AugmentCompatibilityValidator.class, remap = false)
public class AugmentCompatibilityValidatorMixin {
    @WrapOperation(
            method = "lambda$validatePhrase$0",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"
            )
    )
    private static boolean dt$redirectContains(Set instance, Object o, Operation<Boolean> original) {
        return original.call(instance, o) || o instanceof AugmentTinker;
    }
}
