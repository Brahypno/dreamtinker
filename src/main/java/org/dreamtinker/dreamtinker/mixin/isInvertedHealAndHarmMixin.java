package org.dreamtinker.dreamtinker.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class isInvertedHealAndHarmMixin {

    // 在返回之前拿到原始返回值，并可替换
    @Inject(method = "isInvertedHealAndHarm", at = @At("RETURN"), cancellable = true)
    private void dreamtinker$overrideMobType(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        Boolean original = cir.getReturnValue();
        if (!original && DTModifierCheck.haveModifierIn(self, DreamtinkerModifiers.Ids.wither_body))
            cir.setReturnValue(true);
    }
}
