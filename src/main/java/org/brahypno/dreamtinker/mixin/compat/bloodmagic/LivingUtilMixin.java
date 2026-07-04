package org.brahypno.dreamtinker.mixin.compat.bloodmagic;

import net.minecraft.world.entity.player.Player;
import org.brahypno.dreamtinker.utils.CompatUtils.bloodmagic.BloodMagicTconLivingStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("target")
@Pseudo
@Mixin(targets = "wayoftime.bloodmagic.core.living.LivingUtil", remap = false)
public abstract class LivingUtilMixin {
    @Inject(
            method = "hasFullSet(Lnet/minecraft/world/entity/player/Player;)Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void dreamtinker$hasTconLivingSet(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()){
            return;
        }

        if (BloodMagicTconLivingStats.hasTconLivingSet(player)){
            cir.setReturnValue(true);
        }
    }
}