package org.dreamtinker.dreamtinker.mixin.enigmatic_legacy_mixin;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.dreamtinker.dreamtinker.Dreamtinker.configCompactDisabled;

@Mixin(value = SuperpositionHandler.class, remap = false)
public abstract class SuperpositionHandlerMixin {

    // 方法签名：getCurseAmount(Lnet/minecraft/world/item/ItemStack;)I
    @Inject(method = "getCurseAmount(Lnet/minecraft/world/item/ItemStack;)I", at = @At("RETURN"), cancellable = true)
    private static void dreamtinker$injectCurseAmountBeforeReturn(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!configCompactDisabled("enigmaticlegacy")){
            int total = cir.getReturnValue() +
                        DTModifierCheck.getItemModifierNum(stack, DreamtinkerTagKeys.Modifiers.EL_CURSED_MODIFIERS);

            cir.setReturnValue(total);
        }
    }

    @Inject(method = "isTheWorthyOne(Lnet/minecraft/world/entity/player/Player;)Z", at = @At("RETURN"), cancellable = true)
    private static void dreamtinker$injectBeforeWorthyReturn(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (!configCompactDisabled("enigmaticlegacy") &&
            !cir.getReturnValue())
            cir.setReturnValue(DTModifierCheck.haveModifierIn(player, DreamtinkerModifiers.Ids.el_by_pass_worthy));

    }
}
