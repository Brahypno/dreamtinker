package org.dreamtinker.dreamtinker.smeltery;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import slimeknights.tconstruct.smeltery.client.render.HeatingStructureBlockEntityRenderer;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DreamTinkerSmelteryClientEvents {

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(DreamTinkerSmeltery.Transmute.get(), HeatingStructureBlockEntityRenderer::new);
    }
}
