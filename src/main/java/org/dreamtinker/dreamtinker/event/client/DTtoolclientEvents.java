package org.dreamtinker.dreamtinker.event.client;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerItems;
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
            TinkerItemProperties.registerToolProperties(DreamtinkerItems.masu);

            Consumer<Item> brokenConsumer = TinkerItemProperties::registerBrokenProperty;
            DreamtinkerItems.underPlate.forEach(brokenConsumer);
        });
    }

    @SubscribeEvent
    static void itemColors(RegisterColorHandlersEvent.Item event) {
        final ItemColors colors = event.getItemColors();

        // tint modifiers
        //
        registerItemColors(colors, DreamtinkerItems.masu);
        Consumer<Item> brokenConsumer = item -> event.register(ToolModel.COLOR_HANDLER, item);
        DreamtinkerItems.underPlate.forEach(brokenConsumer);
/*
        // modifier crystal
        event.register((stack, index) -> {
            ModifierId modifier = ModifierCrystalItem.getModifier(stack);
            if (modifier != null) {
                return ResourceColorManager.getColor(Util.makeTranslationKey("modifier", modifier));
            }
            return -1;
        }, TinkerModifiers.modifierCrystal);
 */
    }
}
