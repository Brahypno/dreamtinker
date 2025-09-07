package org.dreamtinker.dreamtinker.event.client;

import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerItems;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.model.TinkerItemProperties;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DTtoolclientEvents extends ClientEventBase {
    @SubscribeEvent
    static void clientSetupEvent(FMLClientSetupEvent event) {
        // keybinds
        event.enqueueWork(() -> {
            TinkerItemProperties.registerToolProperties(DreamtinkerItems.masu);

            Consumer<Item> brokenConsumer = TinkerItemProperties::registerBrokenProperty;
            DreamtinkerItems.underPlate.forEach(brokenConsumer);
        });
    }
}
