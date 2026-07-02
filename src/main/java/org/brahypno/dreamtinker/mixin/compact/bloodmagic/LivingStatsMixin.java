package org.brahypno.dreamtinker.mixin.compact.bloodmagic;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import org.brahypno.dreamtinker.utils.CompactUtils.bloodmagic.BloodMagicTconLivingStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("target")
@Pseudo
@Mixin(targets = "wayoftime.bloodmagic.core.living.LivingStats", remap = false)
public abstract class LivingStatsMixin {
    @Inject(
            method = "fromPlayer(Lnet/minecraft/world/entity/player/Player;Z)Lwayoftime/bloodmagic/core/living/LivingStats;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void dreamtinker$fromTconPlayer(Player player, boolean createNew, CallbackInfoReturnable<Object> cir) {
        if (!BloodMagicTconLivingStats.hasTconLivingSet(player)){
            return;
        }

        if (BloodMagicTconLivingStats.isBloodMagicLivingContainer(player.getItemBySlot(EquipmentSlot.CHEST))){
            return;
        }

        Object stats = BloodMagicTconLivingStats.readStats(player);
        if (stats != null){
            cir.setReturnValue(stats);
            return;
        }

        if (!createNew){
            cir.setReturnValue(null);
            return;
        }

        Object created = BloodMagicTconLivingStats.createDefaultStats();
        if (created == null){
            cir.setReturnValue(null);
            return;
        }

        BloodMagicTconLivingStats.writeStats(player, created);
        cir.setReturnValue(created);
    }

    @Inject(
            method = "toPlayer(Lnet/minecraft/world/entity/player/Player;Lwayoftime/bloodmagic/core/living/LivingStats;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void dreamtinker$toTconPlayer(Player player, Object stats, CallbackInfo ci) {
        if (player == null){
            return;
        }

        if (!BloodMagicTconLivingStats.hasTconLivingSet(player)){
            return;
        }

        if (BloodMagicTconLivingStats.isBloodMagicLivingContainer(player.getItemBySlot(EquipmentSlot.CHEST))){
            return;
        }

        BloodMagicTconLivingStats.writeStats(player, stats);
        ci.cancel();
    }
}