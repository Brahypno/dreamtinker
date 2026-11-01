package org.brahypno.dreamtinker.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.brahypno.dreamtinker.library.client.sigil.NarcissusSigilClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class NarcissusHandRendererMixin {
    /** Both first-person hands and third-person ItemInHandLayer use this entry point. */
    @Inject(method = "renderItem", at = @At("TAIL"))
    private void dreamtinker$renderSigil(LivingEntity entity, ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack pose,
                                       MultiBufferSource buffer, int light, CallbackInfo ci) {
        NarcissusSigilClient.renderHeld(entity, stack, context, leftHand, pose, buffer);
    }
}
