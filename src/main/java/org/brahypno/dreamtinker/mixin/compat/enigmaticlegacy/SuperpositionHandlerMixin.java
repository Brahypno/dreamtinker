package org.brahypno.dreamtinker.mixin.compat.enigmaticlegacy;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.brahypno.dreamtinker.common.DreamtinkerTagKeys;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.esotericismtinker.utils.ETModifierCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.brahypno.esotericismtinker.EsotericismTinker.configCompactDisabled;

@Pseudo
@Mixin(targets = "com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler", remap = false)
public abstract class SuperpositionHandlerMixin {

    // 方法签名：getCurseAmount(Lnet/minecraft/world/item/ItemStack;)I
    @Inject(method = "getCurseAmount(Lnet/minecraft/world/item/ItemStack;)I", at = @At("RETURN"), cancellable = true)
    private static void dreamtinker$injectCurseAmountBeforeReturn(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!configCompactDisabled("enigmaticlegacy")){
            int total = cir.getReturnValue() +
                        ETModifierCheck.getItemModifierNum(stack, DreamtinkerTagKeys.Modifiers.EL_CURSED_MODIFIERS);

            cir.setReturnValue(total);
        }
    }

    @Inject(method = "isTheWorthyOne(Lnet/minecraft/world/entity/player/Player;)Z", at = @At("RETURN"), cancellable = true)
    private static void dreamtinker$injectBeforeWorthyReturn(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (!configCompactDisabled("enigmaticlegacy") &&
            !cir.getReturnValue())
            cir.setReturnValue(ETModifierCheck.haveModifierIn(player, DreamtinkerModifiers.Ids.el_by_pass_worthy));

    }
}
