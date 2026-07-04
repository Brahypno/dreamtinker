package org.brahypno.dreamtinker;

import com.mojang.logging.LogUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.levelgen.Heightmap;
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
import net.minecraftforge.registries.MissingMappingsEvent;
import org.brahypno.dreamtinker.Entity.AggressiveFox;
import org.brahypno.dreamtinker.Entity.DreamtinkerEntityTypes;
import org.brahypno.dreamtinker.common.DreamtinkerAttributes;
import org.brahypno.dreamtinker.common.DreamtinkerCommon;
import org.brahypno.dreamtinker.common.DreamtinkerEffects;
import org.brahypno.dreamtinker.common.DreamtinkerSounds;
import org.brahypno.dreamtinker.common.capabilities.compat.curio.addSilenceGloveCurio;
import org.brahypno.dreamtinker.common.capabilities.compat.enigmatic_legacy.addUnholyWater;
import org.brahypno.dreamtinker.common.capabilities.compat.malum.addConcentratedGluttonyBottle;
import org.brahypno.dreamtinker.common.data.AdvancementsProvider;
import org.brahypno.dreamtinker.common.data.DTCurio;
import org.brahypno.dreamtinker.common.data.DreamtinkerRecipeProvider;
import org.brahypno.dreamtinker.common.data.loot.DreamtinkerGlobalLootModifierProvider;
import org.brahypno.dreamtinker.common.data.loot.DreamtinkerLootTableProvider;
import org.brahypno.dreamtinker.common.data.loot.LootTableInjectionProvider;
import org.brahypno.dreamtinker.common.data.tags.*;
import org.brahypno.dreamtinker.common.event.advancements.star_regulus_boost;
import org.brahypno.dreamtinker.config.DreamtinkerClientConfig;
import org.brahypno.dreamtinker.config.DreamtinkerConfig;
import org.brahypno.dreamtinker.fluids.DreamtinkerFluids;
import org.brahypno.dreamtinker.library.compat.ars_nouveau.NovaRegistry;
import org.brahypno.dreamtinker.library.compat.eidolon.DTEidolonCompat;
import org.brahypno.dreamtinker.network.DNetwork;
import org.brahypno.dreamtinker.smeltery.DreamTinkerSmeltery;
import org.brahypno.dreamtinker.tools.DreamtinkerModifiers;
import org.brahypno.dreamtinker.tools.DreamtinkerToolParts;
import org.brahypno.dreamtinker.tools.DreamtinkerTools;
import org.brahypno.dreamtinker.tools.modifiers.events.compat.ars_nouveau.ArsPlayerCraftEvent;
import org.brahypno.dreamtinker.tools.modifiers.events.compat.ars_nouveau.SpellEvents;
import org.brahypno.dreamtinker.tools.modifiers.events.compat.enigmatic_legacy.EL_events;
import org.brahypno.dreamtinker.tools.modifiers.events.compat.malum.malum_events_handler;
import org.brahypno.dreamtinker.world.data.DTDataPackProvider;
import org.brahypno.esotericismtinker.common.EsotericismTinkerCommon;
import org.brahypno.esotericismtinker.library.event.PlayerLeftClickEvent;
import org.brahypno.esotericismtinker.smeltery.EsotericismTinkerSmeltery;
import org.brahypno.esotericismtinker.tools.EsotericismTinkerTools;
import org.brahypno.esotericismtinker.utils.CompatUtils.CuriosCompat;
import org.slf4j.Logger;
import slimeknights.mantle.registration.RegistrationHelper;
import slimeknights.tconstruct.library.utils.Util;

import java.util.concurrent.CompletableFuture;

import static org.brahypno.esotericismtinker.EsotericismTinker.configCompactDisabled;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Dreamtinker.MODID)
public class Dreamtinker {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "dreamtinker";
    public static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings({"removal"})
    public Dreamtinker() {
        MinecraftForge.EVENT_BUS.addListener(Dreamtinker::missingMappings);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, DreamtinkerConfig.specs, "DreamTinkerConfig.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DreamtinkerClientConfig.specs, "DreamTinkerClientConfig.toml");
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        modEventBus.register(new DreamtinkerEntityTypes());
        modEventBus.register(new DreamtinkerAttributes());
        modEventBus.register(new DreamtinkerFluids());
        modEventBus.register(new DreamtinkerEffects());
        modEventBus.register(new DreamtinkerToolParts());
        modEventBus.register(new DreamtinkerTools());
        modEventBus.register(new DreamTinkerSmeltery());
        modEventBus.register(new DreamtinkerCommon());
        modEventBus.register(new DreamtinkerSounds());
        modEventBus.register(new DreamtinkerModifiers());
        DreamtinkerModule.initRegisters(modEventBus);
        CuriosCompat.registerPreferredModifiable(stack -> stack.is(DreamtinkerTools.silence_glove.asItem()));
        if (ModList.get().isLoaded("ars_nouveau")){
            NovaRegistry.NovaInit(modEventBus);
        }

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        // Register ourselves for server and other game events we are interested in

        MinecraftForge.EVENT_BUS.register(this);

        forgeEventBus.addListener(EventPriority.HIGHEST, PlayerLeftClickEvent::onLeftClickBlock);
        forgeEventBus.addListener(EventPriority.HIGHEST, PlayerLeftClickEvent::onLeftClick);
        forgeEventBus.addListener(EventPriority.HIGHEST, PlayerLeftClickEvent::onLeftClickEntity);
        forgeEventBus.addListener(star_regulus_boost::onServerTick);

        DNetwork.registerPackets();
        modEventBus.addListener(this::gatherData);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
            if (ModList.get().isLoaded("curios") && !configCompactDisabled("curios")){
                forgeEventBus.addGenericListener(ItemStack.class, addSilenceGloveCurio::attachCaps);
            }
            if (ModList.get().isLoaded("enigmaticlegacy") && !configCompactDisabled("enigmaticlegacy")){
                forgeEventBus.addGenericListener(ItemStack.class, addUnholyWater::attachCaps);
                forgeEventBus.addListener(EL_events::onLivingDeath);
                forgeEventBus.addListener(EventPriority.LOWEST, EL_events::onLivingDrops);
                forgeEventBus.addListener(EL_events::onEntityJoinLevel);
            }
            if (ModList.get().isLoaded("malum") && !configCompactDisabled("malum")){
                forgeEventBus.addGenericListener(ItemStack.class, addConcentratedGluttonyBottle::attachCaps);
                forgeEventBus.addListener(malum_events_handler::MalumLivingHurtEvent);
                forgeEventBus.addListener(malum_events_handler::MalumLivingDeathEvent);
            }
            if (ModList.get().isLoaded("ars_nouveau") && !configCompactDisabled("ars_nouveau")){
                forgeEventBus.addListener(ArsPlayerCraftEvent::PlayerCraftEvent);
                forgeEventBus.addListener(EventPriority.HIGHEST, SpellEvents::PreSpellDamageEvent);
                forgeEventBus.addListener(EventPriority.HIGHEST, SpellEvents::PostSpellDamageEvent);
                forgeEventBus.addListener(EventPriority.HIGHEST, SpellEvents::SpellProjectileHitEvent);
                forgeEventBus.addListener(EventPriority.HIGHEST, SpellEvents::SpellCostCalcEvent);
                forgeEventBus.addListener(EventPriority.HIGHEST, SpellEvents::PreEffectResolveEvent);
                forgeEventBus.addListener(SpellEvents::EffectResolveEvent);
                NovaRegistry.postInit();
            }

            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(DreamtinkerCommon.narcissus.getId(), DreamtinkerCommon.potted_narcissus);
            SpawnPlacements.register(DreamtinkerEntityTypes.AggressiveFOX.get(),
                                     SpawnPlacements.Type.ON_GROUND,
                                     Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                                     AggressiveFox::checkAggressiveFoxSpawnRules);
            if (ModList.get().isLoaded("eidolon")){
                event.enqueueWork(DTEidolonCompat::init);
            }
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

        generator.addProvider(event.includeClient(), new FluidTagProvider(output, lookupProvider, Dreamtinker.MODID, helper));
        generator.addProvider(event.includeServer(), new EntityTypeTagProvider(output, lookupProvider, helper));


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
        generator.addProvider(event.includeServer(), new AdvancementsProvider(output));
        generator.addProvider(event.includeServer(), new DTMobEffectTagsProvider(output, event.getLookupProvider(), event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new DreamtinkerGlobalLootModifierProvider(output));
    }

    private static void missingMappings(MissingMappingsEvent event) {
        RegistrationHelper.handleMissingMappings(event, MODID, Registries.BLOCK, name -> switch (name) {
            // moved to esotericism tinker
            case "ender_mortar" -> EsotericismTinkerSmeltery.enderMortar.get();
            case "ashen_lamp" -> EsotericismTinkerSmeltery.ashenLamp.get();
            case "ashen_stone" -> EsotericismTinkerSmeltery.ashenStone.get();
            case "polished_ashen_stone" -> EsotericismTinkerSmeltery.polishedAshenStone.get();
            case "ashen_bricks" -> EsotericismTinkerSmeltery.ashenBricks.get();
            case "ashen_bricks_slab" -> EsotericismTinkerSmeltery.ashenBricks.getSlab();
            case "ashen_bricks_stairs" -> EsotericismTinkerSmeltery.ashenBricks.getStairs();
            case "ashen_bricks_fence" -> EsotericismTinkerSmeltery.ashenBricks.getFence();
            case "ashen_road" -> EsotericismTinkerSmeltery.ashenRoad.get();
            case "ashen_road_slab" -> EsotericismTinkerSmeltery.ashenRoad.getSlab();
            case "ashen_road_stairs" -> EsotericismTinkerSmeltery.ashenRoad.getStairs();
            case "chiseled_ashen_bricks" -> EsotericismTinkerSmeltery.chiseledAshenBricks.get();
            case "ashen_heater" -> EsotericismTinkerSmeltery.ashenHeater.get();
            case "ashen_accelerator" -> EsotericismTinkerSmeltery.ashenAccel.get();
            case "ashen_alloy_switch" -> EsotericismTinkerSmeltery.ashenAlloySwitch.get();
            case "ashen_melt_switch" -> EsotericismTinkerSmeltery.ashenMeltSwitch.get();
            case "ashen_glass" -> EsotericismTinkerSmeltery.ashenGlass.get();
            case "ashen_glass_pane" -> EsotericismTinkerSmeltery.ashenGlassPane.get();
            case "ashen_tinted_glass" -> EsotericismTinkerSmeltery.ashenTintedGlass.get();
            case "ashen_soul_glass" -> EsotericismTinkerSmeltery.ashenSoulGlass.get();
            case "ashen_soul_glass_pane" -> EsotericismTinkerSmeltery.ashenSoulGlassPane.get();
            default -> null;
        });
        RegistrationHelper.handleMissingMappings(event, MODID, Registries.ITEM, name -> switch (name) {
            case "ender_mortar" -> EsotericismTinkerSmeltery.enderMortar.asItem();
            case "ashen_brick" -> EsotericismTinkerSmeltery.ashenBrick.get();
            case "ashen_lamp" -> EsotericismTinkerSmeltery.ashenLamp.asItem();
            case "ashen_stone" -> EsotericismTinkerSmeltery.ashenStone.asItem();
            case "polished_ashen_stone" -> EsotericismTinkerSmeltery.polishedAshenStone.asItem();
            case "ashen_bricks" -> EsotericismTinkerSmeltery.ashenBricks.asItem();
            case "ashen_bricks_slab" -> EsotericismTinkerSmeltery.ashenBricks.getSlab().asItem();
            case "ashen_bricks_stairs" -> EsotericismTinkerSmeltery.ashenBricks.getStairs().asItem();
            case "ashen_bricks_fence" -> EsotericismTinkerSmeltery.ashenBricks.getFence().asItem();
            case "ashen_road" -> EsotericismTinkerSmeltery.ashenRoad.asItem();
            case "ashen_road_slab" -> EsotericismTinkerSmeltery.ashenRoad.getSlab().asItem();
            case "ashen_road_stairs" -> EsotericismTinkerSmeltery.ashenRoad.getStairs().asItem();
            case "chiseled_ashen_bricks" -> EsotericismTinkerSmeltery.chiseledAshenBricks.asItem();
            case "ashen_heater" -> EsotericismTinkerSmeltery.ashenHeater.asItem();
            case "ashen_accelerator" -> EsotericismTinkerSmeltery.ashenAccel.asItem();
            case "ashen_alloy_switch" -> EsotericismTinkerSmeltery.ashenAlloySwitch.asItem();
            case "ashen_melt_switch" -> EsotericismTinkerSmeltery.ashenMeltSwitch.asItem();
            case "ashen_glass" -> EsotericismTinkerSmeltery.ashenGlass.asItem();
            case "ashen_glass_pane" -> EsotericismTinkerSmeltery.ashenGlassPane.asItem();
            case "ashen_tinted_glass" -> EsotericismTinkerSmeltery.ashenTintedGlass.asItem();
            case "ashen_soul_glass" -> EsotericismTinkerSmeltery.ashenSoulGlass.asItem();
            case "ashen_soul_glass_pane" -> EsotericismTinkerSmeltery.ashenSoulGlassPane.asItem();
            case "hypnagogic_transmute" -> EsotericismTinkerCommon.hypnagogic_transmute.get();
            case "ritual_blade" -> EsotericismTinkerTools.ritual_blade.asItem();
            default -> null;
        });
    }

}
