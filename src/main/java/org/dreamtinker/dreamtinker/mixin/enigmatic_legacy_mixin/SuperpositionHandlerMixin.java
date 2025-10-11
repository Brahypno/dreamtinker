package org.dreamtinker.dreamtinker.mixin.enigmatic_legacy_mixin;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagkeys;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.utils.DTModiferCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SuperpositionHandler.class, remap = false)
public abstract class SuperpositionHandlerMixin {

    // 方法签名：getCurseAmount(Lnet/minecraft/world/item/ItemStack;)I
    @Inject(method = "getCurseAmount(Lnet/minecraft/world/item/ItemStack;)I", at = @At("RETURN"), cancellable = true)
    private static void dreamtinker$injectCurseAmountBeforeReturn(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        int total = cir.getReturnValue() +
                    DTModiferCheck.getItemModifierNum(stack, DreamtinkerTagkeys.Modifiers.EL_CURSED_MODIFIERS);

        cir.setReturnValue(total);
    }

    @Inject(method = "isTheWorthyOne(Lnet/minecraft/world/entity/player/Player;)Z", at = @At("RETURN"), cancellable = true)
    private static void dreamtinker$injectBeforeWorthyReturn(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue())
            cir.setReturnValue(DTModiferCheck.haveModifierIn(player, DreamtinkerModifiers.by_pass_worthy.getId()));

    }
}
