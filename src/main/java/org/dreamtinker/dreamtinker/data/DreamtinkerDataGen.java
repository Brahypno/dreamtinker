package org.dreamtinker.dreamtinker.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.data.tags.DreamtinkerModifierTagProvider;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DreamtinkerDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new DreamtinkerModifierTagProvider(output, helper));
    }
}
