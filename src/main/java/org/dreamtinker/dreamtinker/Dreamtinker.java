package org.dreamtinker.dreamtinker;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;
import org.dreamtinker.dreamtinker.common.DreamtinkerEffects;
import org.dreamtinker.dreamtinker.common.data.DTCurio;
import org.dreamtinker.dreamtinker.common.data.DreamtinkerRecipeProvider;
import org.dreamtinker.dreamtinker.common.data.loot.DreamtinkerLootTableProvider;
import org.dreamtinker.dreamtinker.common.data.loot.LootTableInjectionProvider;
import org.dreamtinker.dreamtinker.common.data.tags.BlockTagProvider;
import org.dreamtinker.dreamtinker.common.data.tags.DamageTypeTagProvider;
import org.dreamtinker.dreamtinker.common.data.tags.FluidTagProvider;
import org.dreamtinker.dreamtinker.common.data.tags.ItemTagProvider;
import org.dreamtinker.dreamtinker.common.event.advancements.star_regulus_boost;
import org.dreamtinker.dreamtinker.common.event.compact.curio.addSilenceGloveCurio;
import org.dreamtinker.dreamtinker.common.event.compact.enigmatic_legacy.addUnholyWater;
import org.dreamtinker.dreamtinker.common.event.compact.malum.addConcentratedGluttonyBottle;
import org.dreamtinker.dreamtinker.config.DreamtinkerConfig;
import org.dreamtinker.dreamtinker.fluids.DreamtinkerFluids;
import org.dreamtinker.dreamtinker.fluids.data.DreamtinkerFluidTextureProvider;
import org.dreamtinker.dreamtinker.library.event.PlayerLeftClickEvent;
import org.dreamtinker.dreamtinker.network.Dnetwork;
import org.dreamtinker.dreamtinker.tools.DreamtinkerModifiers;
import org.dreamtinker.dreamtinker.tools.DreamtinkerToolParts;
import org.dreamtinker.dreamtinker.tools.DreamtinkerTools;
import org.dreamtinker.dreamtinker.tools.modifiers.events.compact.curio.curio_hurt_handler;
import org.dreamtinker.dreamtinker.tools.modifiers.events.compact.enigmatic_legacy.death_handler;
import org.dreamtinker.dreamtinker.tools.modifiers.events.compact.malum.malum_events_handler;
import org.dreamtinker.dreamtinker.world.data.DTDataPackProvider;
import slimeknights.tconstruct.fluids.data.FluidBucketModelProvider;
import slimeknights.tconstruct.library.utils.Util;

import java.util.concurrent.CompletableFuture;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Dreamtinker.MODID)
public class Dreamtinker {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "dreamtinker";

    @SuppressWarnings({"removal"})
    public Dreamtinker() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, DreamtinkerConfig.specs, "DreamTinkerConfig.toml");
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        // Register ourselves for server and other game events we are interested in
        modEventBus.register(new DreamtinkerFluids());
        modEventBus.register(new DreamtinkerEffects());
        modEventBus.register(new DreamtinkerToolParts());
        modEventBus.register(new DreamtinkerTools());
        modEventBus.register(new DreamtinkerCommon());
        modEventBus.register(new DreamtinkerModifiers());
        DreamtinkerModule.initRegisters(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        if (ModList.get().isLoaded("curios")){
            forgeEventBus.addGenericListener(ItemStack.class, addSilenceGloveCurio::attachCaps);
            forgeEventBus.addListener(curio_hurt_handler::LivingHurtEvent);
        }
        if (ModList.get().isLoaded("enigmaticlegacy")){
            forgeEventBus.addGenericListener(ItemStack.class, addUnholyWater::attachCaps);
            forgeEventBus.addListener(death_handler::onLivingDeath);
        }
        if (ModList.get().isLoaded("malum")){
            forgeEventBus.addGenericListener(ItemStack.class, addConcentratedGluttonyBottle::attachCaps);
            forgeEventBus.addListener(malum_events_handler::MalumLivingHurtEvent);
            forgeEventBus.addListener(malum_events_handler::MalumLivingDeathEvent);
        }

        forgeEventBus.addListener(EventPriority.HIGHEST, PlayerLeftClickEvent::onLeftClickBlock);
        forgeEventBus.addListener(EventPriority.HIGHEST, PlayerLeftClickEvent::onLeftClick);
        forgeEventBus.addListener(EventPriority.HIGHEST, PlayerLeftClickEvent::onLeftClickEntity);
        forgeEventBus.addListener(star_regulus_boost::onServerTick);

        Dnetwork.registerPackets();
        modEventBus.addListener(this::gatherData);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(DreamtinkerCommon.narcissus.getId(), DreamtinkerCommon.potted_narcissus);
        });
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    public static ResourceLocation getLocation(String name) {return new ResourceLocation(MODID, name);}

    public static String makeTranslationKey(String base, String name) {
        return Util.makeTranslationKey(base, getLocation(name));
    }

    public static MutableComponent makeTranslation(String base, String name) {
        return Component.translatable(makeTranslationKey(base, name));
    }

    public static TagKey<Block> mcBlockTag(String name) {
        return TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation("minecraft", name));
    }

    public static TagKey<Item> mcItemTag(String name) {
        return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("minecraft", name));
    }

    public static TagKey<Item> forgeItemTag(String name) {
        return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("forge", name));
    }

    public static TagKey<Block> forgeBlockTag(String name) {
        return TagKey.create(ForgeRegistries.BLOCKS.getRegistryKey(), new ResourceLocation("forge", name));
    }

    public void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new DreamtinkerFluidTextureProvider(output));
        generator.addProvider(event.includeClient(), new FluidBucketModelProvider(output, Dreamtinker.MODID));
        generator.addProvider(event.includeClient(), new FluidTagProvider(output, lookupProvider, Dreamtinker.MODID, helper));


        BlockTagProvider blockTags = new BlockTagProvider(output, lookupProvider, Dreamtinker.MODID, helper);
        generator.addProvider(event.includeClient(), blockTags);
        generator.addProvider(event.includeServer(),
                              new ItemTagProvider(output, lookupProvider, blockTags.contentsGetter(), Dreamtinker.MODID, helper));


        generator.addProvider(event.includeClient(), new DreamtinkerRecipeProvider(output));

        generator.addProvider(event.includeServer(), new DreamtinkerLootTableProvider(output));
        DatapackBuiltinEntriesProvider provider = new DTDataPackProvider(output, lookupProvider);

        generator.addProvider(event.includeServer(), provider);
        generator.addProvider(event.includeServer(), new DamageTypeTagProvider(output, provider.getRegistryProvider(), helper));
        generator.addProvider(event.includeServer(), new LootTableInjectionProvider(output));

        generator.addProvider(event.includeServer(), new DTCurio(output, helper, provider.getRegistryProvider()));
    }

}
