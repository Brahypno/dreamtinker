package org.dreamtinker.dreamtinker.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 500)
public abstract class LivingEntityMixin {
    @Inject(method = "getAttributeValue*", at = @At("RETURN"), cancellable = true)
    private void dreamtinker$attackSpeedFloor(Attribute attribute, CallbackInfoReturnable<Double> cir) {
        if (attribute == Attributes.ATTACK_SPEED)
            if (DTModifierCheck.ModifierInHand((LivingEntity) (Object) this, DreamtinkerModifiers.many_wishes.getId())){
                double value = cir.getReturnValueD();
                if (value < 1.0D)
                    cir.setReturnValue(1.0D);
            }
    }
}
