package org.dreamtinker.dreamtinker.world;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class worldEvent {
    @SubscribeEvent
    static void wanderingTrades(WandererTradesEvent event) {
        // add ancient tools to the wandering trader table
        event.getRareTrades().add((trader, rand) ->
                                          new MerchantOffer(new ItemStack(Items.EMERALD, 30),
                                                            new ItemStack(Items.GLASS_BOTTLE, 1),
                                                            new ItemStack(DreamtinkerCommon.rainbow_honey.get(), 1),
                                                            4, 1, 0.05f));
    }
}
