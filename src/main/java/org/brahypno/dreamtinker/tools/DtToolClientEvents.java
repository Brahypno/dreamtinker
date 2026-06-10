package org.brahypno.dreamtinker.tools;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.brahypno.dreamtinker.Dreamtinker;
import org.brahypno.dreamtinker.DreamtinkerModule;
import org.brahypno.dreamtinker.library.client.DTItemProperties;
import org.brahypno.dreamtinker.library.client.particle.ColoredSweepParticle;
import org.brahypno.dreamtinker.library.client.particle.VibeBarParticle;
import org.brahypno.dreamtinker.library.compact.ars_nouveau.NovaRegistry;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.model.TinkerItemProperties;
import slimeknights.tconstruct.library.client.model.tools.ToolModel;

import java.util.function.Consumer;

import static slimeknights.tconstruct.library.client.model.tools.ToolModel.registerItemColors;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DtToolClientEvents extends ClientEventBase {
    @SubscribeEvent
    static void clientSetupEvent(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            DTItemProperties.register();
            TinkerItemProperties.registerToolProperties(DreamtinkerTools.mashou);
            TinkerItemProperties.registerToolProperties(DreamtinkerTools.silence_glove);
            TinkerItemProperties.registerToolProperties(DreamtinkerTools.narcissus_wing);
            TinkerItemProperties.registerToolProperties(DreamtinkerTools.chain_saw_blade);
            TinkerItemProperties.registerToolProperties(DreamtinkerTools.ritual_blade);
            if (ModList.get().isLoaded("ars_nouveau")){
                TinkerItemProperties.registerToolProperties(NovaRegistry.per_aspera_scriptum);
            }
            Consumer<Item> brokenConsumer = TinkerItemProperties::registerBrokenProperty;
            DreamtinkerTools.underPlate.forEach(brokenConsumer);
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
        registerItemColors(colors, DreamtinkerTools.ritual_blade);
        if (ModList.get().isLoaded("ars_nouveau")){
            registerItemColors(colors, NovaRegistry.per_aspera_scriptum);
        }
        Consumer<Item> brokenConsumer = item -> event.register(ToolModel.COLOR_HANDLER, item);
        DreamtinkerTools.underPlate.forEach(brokenConsumer);
    }


    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent e) {
        e.registerSpriteSet(DreamtinkerModule.VIBE_BAR.get(), VibeBarParticle.Provider::new);
        e.registerSpriteSet(DreamtinkerModule.COLORED_SWEEP.get(), ColoredSweepParticle.Provider::new);
    }
}
