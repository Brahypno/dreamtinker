package org.dreamtinker.dreamtinker.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import org.dreamtinker.dreamtinker.register.DreamtinkerModifer;
import org.dreamtinker.dreamtinker.utils.DTModiferCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityGetMobTypeMixin {

    // 在返回之前拿到原始返回值，并可替换
    @Inject(method = "getMobType", at = @At("RETURN"), cancellable = true)
    private void dreamtinker$overrideMobType(CallbackInfoReturnable<MobType> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        MobType original = cir.getReturnValue();
        if (DTModiferCheck.haveModifierIn(self, DreamtinkerModifer.wither_body.getId()))
            cir.setReturnValue(MobType.UNDEAD);
    }
}
