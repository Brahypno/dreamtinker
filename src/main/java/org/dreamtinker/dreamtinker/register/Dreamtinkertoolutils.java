package org.dreamtinker.dreamtinker.register;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.dreamtinker.dreamtinker.entity.TNTArrowEntity;
import org.jetbrains.annotations.NotNull;

import static org.dreamtinker.dreamtinker.Dreamtinker.MODID;


@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class  Dreamtinkertoolutils {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRenderers.register(DreamtinkerEntity.TNTARROW.get(), (EntityRendererProvider.Context context) -> new ArrowRenderer<>(context) {
                @Override
                public @NotNull ResourceLocation getTextureLocation(@NotNull TNTArrowEntity tntArrowEntity) {
                    return new  ResourceLocation("minecraft", "textures/entity/projectiles/arrow.png");

                }
            });
        });
    }
}
