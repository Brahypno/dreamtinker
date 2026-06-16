package org.brahypno.dreamtinker.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 500)
public abstract class LivingEntityMixin {
    @Inject(method = "getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D", at = @At("RETURN"), cancellable = true)
    private void dreamtinker$attackSpeedFloor(Attribute attribute, CallbackInfoReturnable<Double> cir) {
        if (attribute == Attributes.ATTACK_SPEED){
            int wishes_level = ETModifierCheck.getEntityHandsModifierNum((LivingEntity) (Object) this, DreamtinkerModifiers.many_wishes.getId());
            if (0 < wishes_level){
                double value = cir.getReturnValueD();
                if (value < 1.0D + 0.2 * wishes_level)
                    cir.setReturnValue(1.0D + 0.2 * wishes_level);
            }
        }
    }
}
