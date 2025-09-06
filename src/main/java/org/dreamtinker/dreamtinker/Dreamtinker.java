package org.dreamtinker.dreamtinker;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.dreamtinker.dreamtinker.config.DreamtinkerConfig;
import org.dreamtinker.dreamtinker.event.PlayerEvent;
import org.dreamtinker.dreamtinker.event.addUnholywater;
import org.dreamtinker.dreamtinker.event.compact.death_handler;
import org.dreamtinker.dreamtinker.event.star_regulus_boost;
import org.dreamtinker.dreamtinker.network.Dnetwork;
import org.dreamtinker.dreamtinker.register.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Dreamtinker.MODID)
public class Dreamtinker {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "dreamtinker";

    @SuppressWarnings({"removal"})
    public Dreamtinker() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, DreamtinkerConfig.specs, "DreamTinkerConfig.toml");

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        // Register ourselves for server and other game events we are interested in

        DreamtinkerItems.ITEMS.register(modEventBus);
        DreamtinkerFluids.FLUIDS.register(modEventBus);
        DreamtinkerModifers.MODIFIERS.register(modEventBus);
        DreamtinkerEntity.ENTITIES.register(modEventBus);
        DreamtinkerEffects.EFFECT.register(modEventBus);
        DreamtinkerLoots.LOOTMODIFIERS.register(modEventBus);
        DreamtinkerTab.TABS.register(modEventBus);
        if (ModList.get().isLoaded("enigmaticlegacy")){
            DreamtinkerModifers.EL_MODIFIERS.register(modEventBus);
            DreamtinkerFluids.EL_FLUIDS.register(modEventBus);
            DreamtinkerEffects.EL_EFFECT.register(modEventBus);
            DreamtinkerItems.EL_ITEMS.register(modEventBus);
            forgeEventBus.addGenericListener(ItemStack.class, addUnholywater::attachCaps);
            forgeEventBus.addListener(death_handler::onLivingDeath);
        }

        MinecraftForge.EVENT_BUS.register(this);
        forgeEventBus.addListener(star_regulus_boost::onServerTick);
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

    public static ResourceLocation getLocation(String name) {return new ResourceLocation(MODID, name);}

}
