package org.dreamtinker.dreamtinker.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.data.providers.*;
import slimeknights.tconstruct.fluids.data.FluidBucketModelProvider;
import slimeknights.tconstruct.library.client.data.material.MaterialPartTextureGenerator;
import slimeknights.tconstruct.tools.data.sprite.TinkerMaterialSpriteProvider;
import slimeknights.tconstruct.tools.data.sprite.TinkerPartSpriteProvider;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Dreamtinker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DreamtinkerDataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new DreamtinkerModifierTagProvider(output, helper));
        generator.addProvider(event.includeClient(), new DreamtinkerFluidEffectProvider(output));
        generator.addProvider(event.includeClient(), new DreamtinkerMaterialRenderInfoProvider(output, new DreamtinkerMaterialSpriteProvider(), helper));
        generator.addProvider(event.includeClient(), new DreamtinkerMaterialTagProvider(output, helper));
        generator.addProvider(event.includeClient(),
                              new MaterialPartTextureGenerator(output, helper, new DreamtinkerPartSpriteProvider(), new TinkerMaterialSpriteProvider(),
                                                               new DreamtinkerMaterialSpriteProvider()));
        generator.addProvider(event.includeClient(),
                              new MaterialPartTextureGenerator(output, helper, new TinkerPartSpriteProvider(), new DreamtinkerMaterialSpriteProvider()));
        generator.addProvider(event.includeClient(), new DreamtinkerFluidTextureProvider(output));
        generator.addProvider(event.includeClient(), new FluidBucketModelProvider(output, Dreamtinker.MODID));
        generator.addProvider(event.includeClient(), new DreamtinkerFluidTagProvider(output, lookupProvider, Dreamtinker.MODID, helper));
        DreamtinkerBlockTagProvider blockTags = new DreamtinkerBlockTagProvider(output, lookupProvider, Dreamtinker.MODID, helper);
        //generator.addProvider(event.includeClient(), new DreamtinkerItemTagProvider(output, lookupProvider, blockTags.contentsGetter(), Dreamtinker.MODID, helper));
        generator.addProvider(event.includeClient(), new DreamtinkerItemModelProvider(output, helper));
        generator.addProvider(event.includeClient(), new DreamtinkerMaterialDataProvider(output));
        generator.addProvider(event.includeClient(), new DreamtinkerMaterialStatProvider(output));
        generator.addProvider(event.includeClient(), new DreamtinkerMaterialModifierProvider(output));
        generator.addProvider(event.includeClient(), new DreamtinkerRecipeProvider(output));
        generator.addProvider(event.includeClient(), new DreamtinkerModifierProvider(output));


    }


}
