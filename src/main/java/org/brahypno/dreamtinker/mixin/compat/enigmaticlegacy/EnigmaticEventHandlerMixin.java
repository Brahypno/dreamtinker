package org.brahypno.dreamtinker.mixin.compat.enigmaticlegacy;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.brahypno.dreamtinker.common.DreamtinkerTagKeys;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import static org.brahypno.esotericismtinker.EsotericismTinker.configCompactDisabled;

@Pseudo
@Mixin(targets = "com.aizistral.enigmaticlegacy.handlers.EnigmaticEventHandler", remap = false)
public abstract class EnigmaticEventHandlerMixin {
    @ModifyVariable(
            method = "onEntityHurt(Lnet/minecraftforge/event/entity/living/LivingHurtEvent;)V",
            at = @At(value = "STORE", ordinal = 0), // 该切片内的第 1 次 boolean STORE
            // 写入的第 1 个 boolean 变量（通常就是 bypass）
            slice = @Slice(
                    from = @At(value = "INVOKE",
                            target = "Lcom/aizistral/enigmaticlegacy/handlers/SuperpositionHandler;isTheCursedOne(Lnet/minecraft/world/entity/player/Player;)Z",
                            ordinal = 2),
                    to = @At(value = "INVOKE",
                            target = "Lnet/minecraftforge/event/entity/living/LivingHurtEvent;setAmount(F)V",
                            ordinal = 16)
            ),
            name = "bypass")

    private boolean dreamtinker$adjustBypassBeforeCheck(boolean bypass, LivingHurtEvent event) {
        if (!configCompactDisabled("enigmaticlegacy") &&
            !bypass && null != event.getSource().getDirectEntity() && event.getSource().getDirectEntity() instanceof LivingEntity entity)
            return 0 < ETModifierCheck.getItemModifierNum(entity.getMainHandItem(), DreamtinkerTagKeys.Modifiers.EL_CURSED_RELIEF);

        return bypass;
    }
}
