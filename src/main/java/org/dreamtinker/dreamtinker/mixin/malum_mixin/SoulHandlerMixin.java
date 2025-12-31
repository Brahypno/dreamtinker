package org.dreamtinker.dreamtinker.mixin.malum_mixin;

import com.sammy.malum.core.handlers.SoulDataHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.utils.DTModifierCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.dreamtinker.dreamtinker.Dreamtinker.configCompactDisabled;

@Mixin(value = SoulDataHandler.class, remap = false)
public abstract class SoulHandlerMixin {
    @Inject(
            method = "exposeSoul(Lnet/minecraftforge/event/entity/living/LivingHurtEvent;)V",
            at = @At("TAIL")
    )
    private static void dreamtinker$extraSoulCheck(LivingHurtEvent event, CallbackInfo ci) {
        if (event.isCanceled() || event.getAmount() <= 0 || configCompactDisabled("malum"))
            return;

        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof LivingEntity attacker))
            return;

        ItemStack stack;
        try {
            stack = SoulDataHandler.getSoulHunterWeapon(source, attacker);
        }
        catch (Throwable ignore) {
            stack = attacker.getMainHandItem();
        }

        if (0 < DTModifierCheck.getItemModifierNum(stack, DreamtinkerTagKeys.Modifiers.MALUM_EXPOSE_SOUL)){
            // 直接复用原逻辑
            SoulDataHandler.exposeSoul(event.getEntity());
        }
    }
}
