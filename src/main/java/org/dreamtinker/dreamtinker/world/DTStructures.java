package org.dreamtinker.dreamtinker.world;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DTStructures {
    @SubscribeEvent
    void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        boolean server = event.includeServer();
        //generator.addProvider(server, new StructureUpdater(packOutput, existingFileHelper, Dreamtinker.MODID, PackOutput.Target.DATA_PACK, "structures"));
        //generator.addProvider(event.includeClient(),
        //                     new StructureUpdater(packOutput, existingFileHelper, Dreamtinker.MODID, PackOutput.Target.RESOURCE_PACK, "book/structures"));
    }
}
