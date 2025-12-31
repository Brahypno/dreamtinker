package org.dreamtinker.dreamtinker.mixin.NovaMixin;

import com.hollingsworth.arsnouveau.client.renderer.tile.AlterationTableRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.dreamtinker.dreamtinker.utils.model.SideAwareArmorModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AlterationTableRenderer.class, remap = false)
public class AlterationTableRendererMixin {
    @Inject(method = "renderModel", at = @At("HEAD"))
    public void dreamtinker$summonProjectiles(PoseStack pPoseStack, MultiBufferSource pBuffer, int packedLight, boolean p_117111_, Model pModel, float p_117114_, float p_117115_, float p_117116_, ResourceLocation armorResource, CallbackInfo ci) {
        if (pModel instanceof SideAwareArmorModel spm){
            spm.setBuffer(pBuffer);
        }
    }

}
