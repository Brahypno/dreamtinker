package org.dreamtinker.dreamtinker;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.dreamtinker.dreamtinker.event.PlayerEvent;
import org.dreamtinker.dreamtinker.network.Dnetwork;
import org.dreamtinker.dreamtinker.register.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Dreamtinker.MODID)
public class Dreamtinker {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "dreamtinker";

    public Dreamtinker() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        DreamtinkerItem.ITEMS.register(modEventBus);
        DreamtinkerFluid.FLUIDS.register(modEventBus);
        DreamtinkerModifer.MODIFIERS.register(modEventBus);
        DreamtinkerEntity.ENTITIES.register(modEventBus);
        DreamtinkerEffect.EFFECT.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        forgeEventBus.addListener(PlayerEvent::onLeftClickBlock);
        forgeEventBus.addListener(PlayerEvent::onLeftClick);
        forgeEventBus.addListener(PlayerEvent::onLeftClickEntity);

        Dnetwork.registerPackets();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

}
