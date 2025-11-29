package org.dreamtinker.dreamtinker.library.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.FoxHeldItemLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.library.tools.item.IModifiable;

@OnlyIn(Dist.CLIENT)
public class AggressiveFoxRender extends FoxRenderer {
    public AggressiveFoxRender(EntityRendererProvider.Context p_174127_) {
        super(p_174127_);
        this.layers.removeIf(layer -> layer instanceof FoxHeldItemLayer);
        this.addLayer(new AggressiveFoxHeldItemLayer(this, p_174127_.getItemInHandRenderer()));
    }

    private static class AggressiveFoxHeldItemLayer extends RenderLayer<Fox, FoxModel<Fox>> {
        public AggressiveFoxHeldItemLayer(RenderLayerParent<Fox, FoxModel<Fox>> p_234838_, ItemInHandRenderer p_234839_) {
            super(p_234838_);
            this.itemInHandRenderer = p_234839_;
        }

        private final ItemInHandRenderer itemInHandRenderer;

        public void render(PoseStack p_117007_, MultiBufferSource p_117008_, int p_117009_, Fox p_117010_, float p_117011_, float p_117012_, float p_117013_, float p_117014_, float p_117015_, float p_117016_) {
            boolean $$10 = p_117010_.isSleeping();
            boolean $$11 = p_117010_.isBaby();
            p_117007_.pushPose();
            if ($$11){
                float $$12 = 0.75F;
                p_117007_.scale(0.75F, 0.75F, 0.75F);
                p_117007_.translate(0.0F, 0.5F, 0.209375F);
            }

            p_117007_.translate(((FoxModel) this.getParentModel()).head.x / 16.0F, ((FoxModel) this.getParentModel()).head.y / 16.0F,
                                ((FoxModel) this.getParentModel()).head.z / 16.0F);
            float $$13 = p_117010_.getHeadRollAngle(p_117013_);
            p_117007_.mulPose(Axis.ZP.rotation($$13));
            p_117007_.mulPose(Axis.YP.rotationDegrees(p_117015_));
            p_117007_.mulPose(Axis.XP.rotationDegrees(p_117016_));
            if (p_117010_.isBaby()){
                if ($$10){
                    p_117007_.translate(0.4F, 0.26F, 0.15F);
                }else {
                    p_117007_.translate(0.06F, 0.26F, -0.5F);
                }
            }else if ($$10){
                p_117007_.translate(0.46F, 0.26F, 0.22F);
            }else {
                p_117007_.translate(0.06F, 0.27F, -0.5F);
            }

            p_117007_.mulPose(Axis.XP.rotationDegrees(90.0F));
            if ($$10){
                p_117007_.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }
            if (p_117010_.getMainHandItem().getItem() instanceof IModifiable){
                p_117007_.mulPose(Axis.ZP.rotationDegrees(180.0F));
            }


            ItemStack $$14 = p_117010_.getItemBySlot(EquipmentSlot.MAINHAND);
            this.itemInHandRenderer.renderItem(p_117010_, $$14, ItemDisplayContext.GROUND, false, p_117007_, p_117008_, p_117009_);
            p_117007_.popPose();
        }

    }
}
