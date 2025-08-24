package org.dreamtinker.dreamtinker.mixin.enigmaticlegacymixin;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import net.minecraft.world.item.ItemStack;
import org.dreamtinker.dreamtinker.data.DreamtinkerTagkeys;
import org.dreamtinker.dreamtinker.utils.DTModiferCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SuperpositionHandler.class, remap = false)
public abstract class SuperpositionHandlerMixin {

    // 方法签名：getCurseAmount(Lnet/minecraft/world/item/ItemStack;)I
    @Inject(method = "getCurseAmount(Lnet/minecraft/world/item/ItemStack;)I", at = @At("RETURN"), cancellable = true)
    private static void dreamtinker$injectBeforeReturn(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        int total = cir.getReturnValue() + DTModiferCheck.getItemModifierTagNum(stack,
                                                                                DreamtinkerTagkeys.Modifiers.CURSED_MODIFIERS);

        cir.setReturnValue(total);  // 覆盖返回值
    }
}
