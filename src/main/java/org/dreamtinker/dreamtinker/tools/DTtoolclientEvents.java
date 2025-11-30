package org.dreamtinker.dreamtinker.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.library.client.AggressiveFoxRender;
import org.dreamtinker.dreamtinker.library.client.SlashOrbitRenderer;
import org.dreamtinker.dreamtinker.tools.client.NarcissusFluidProjectileRenderer;
import org.dreamtinker.dreamtinker.tools.items.TNTArrow;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.model.TinkerItemProperties;
import slimeknights.tconstruct.library.client.model.tools.ToolModel;

import java.util.function.Consumer;

import static slimeknights.tconstruct.library.client.model.tools.ToolModel.registerItemColors;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DTtoolclientEvents extends ClientEventBase {
    @SubscribeEvent
    static void clientSetupEvent(FMLClientSetupEvent event) {
        // keybinds
        event.enqueueWork(() -> {
            TinkerItemProperties.registerToolProperties(DreamtinkerTools.mashou);
            TinkerItemProperties.registerToolProperties(DreamtinkerTools.silence_glove);
            TinkerItemProperties.registerToolProperties(DreamtinkerTools.narcissus_wing);
            TinkerItemProperties.registerToolProperties(DreamtinkerTools.chain_saw_blade);

            Consumer<Item> brokenConsumer = TinkerItemProperties::registerBrokenProperty;
            DreamtinkerTools.underPlate.forEach(brokenConsumer);
            EntityRenderers.register(DreamtinkerModifiers.TNTARROW.get(),
                                     (EntityRendererProvider.Context ctx) -> new EntityRenderer<TNTArrow.TNTArrowEntity>(ctx) {
                                         private final ItemRenderer itemRenderer = ctx.getItemRenderer();

                                         @Override
                                         public void render(
                                                 TNTArrow.@NotNull TNTArrowEntity entity, float entityYaw, float partialTicks,
                                                 @NotNull PoseStack pose, @NotNull MultiBufferSource buffers, int packedLight) {
                                             ItemStack stack = entity.getToolStackSynced();
                                             if (stack.isEmpty())
                                                 return;

                                             pose.pushPose();
                                             // 让朝向跟随飞行方向（类似箭）
                                             float yaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
                                             float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

                                             pose.mulPose(Axis.YP.rotationDegrees(yaw - 90.0F));  // 原来是 (yaw - 90.0F)
                                             pose.mulPose(Axis.ZP.rotationDegrees(pitch - 50f));
                                             pose.mulPose(Axis.XP.rotationDegrees(0));         // 可调：-45/30/60

                                             // 适当缩放/微移，避免“埋进”模型
                                             //pose.translate(0.0D, 0.0D, 0.0D);
                                             //pose.scale(0.75f, 0.75f, 0.75f);

                                             // 渲染：GROUND/GUI/NONE 均可按需要选择，这里用 GROUND 比较平衡
                                             itemRenderer.renderStatic(
                                                     stack,
                                                     ItemDisplayContext.NONE,
                                                     packedLight,
                                                     OverlayTexture.NO_OVERLAY,
                                                     pose,
                                                     buffers,
                                                     entity.level(),
                                                     entity.getId()                       // 随机种子，保证粒子/微抖一致
                                             );
                                             pose.popPose();

                                             super.render(entity, entityYaw, partialTicks, pose, buffers, packedLight);
                                         }

                                         @Override
                                         public @NotNull ResourceLocation getTextureLocation(@NotNull TNTArrow.TNTArrowEntity tntArrowEntity) {
                                             return new ResourceLocation("minecraft", "textures/entity/projectiles/arrow.png");
                                         }
                                     });
        });
    }

    @SubscribeEvent
    static void itemColors(RegisterColorHandlersEvent.Item event) {
        final ItemColors colors = event.getItemColors();

        // tint modifiers
        //
        registerItemColors(colors, DreamtinkerTools.mashou);
        registerItemColors(colors, DreamtinkerTools.narcissus_wing);
        registerItemColors(colors, DreamtinkerTools.silence_glove);
        registerItemColors(colors, DreamtinkerTools.chain_saw_blade);
        Consumer<Item> brokenConsumer = item -> event.register(ToolModel.COLOR_HANDLER, item);
        DreamtinkerTools.underPlate.forEach(brokenConsumer);
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DreamtinkerModifiers.NarcissusSpitEntity.get(), NarcissusFluidProjectileRenderer::new);
        event.registerEntityRenderer(DreamtinkerModifiers.SLASH_ORBIT.get(), SlashOrbitRenderer::new);
        event.registerEntityRenderer(DreamtinkerModifiers.AggressiveFOX.get(), AggressiveFoxRender::new);
    }
}
