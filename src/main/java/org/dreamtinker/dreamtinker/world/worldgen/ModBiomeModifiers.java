package org.dreamtinker.dreamtinker.world.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import org.dreamtinker.dreamtinker.Dreamtinker;

import static net.minecraft.core.HolderSet.direct;
import static org.dreamtinker.dreamtinker.world.worldgen.ModWorldGen.*;

public class ModBiomeModifiers {

    public static final ResourceKey<BiomeModifier> ADD_NARCISSUS =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Dreamtinker.getLocation("add_narcissus"));
    public static ResourceKey<BiomeModifier> spawnLarimarOre =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Dreamtinker.getLocation("add_larimar_ore"));
    public static ResourceKey<BiomeModifier> spawnAmberOre =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Dreamtinker.getLocation("add_amber_ore"));
    public static ResourceKey<BiomeModifier> spawnBlackSapphire =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Dreamtinker.getLocation("add_black_sapphire_ore"));
    public static ResourceKey<BiomeModifier> spawnScoleciteOre =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Dreamtinker.getLocation("add_scolecite_ore"));
    public static ResourceKey<BiomeModifier> spawnColdIronOre =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Dreamtinker.getLocation("add_cold_iron_ore"));
    public static ResourceKey<BiomeModifier> spawnOrichalcumOre =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Dreamtinker.getLocation("add_orichalcum_ore"));
    public static ResourceKey<BiomeModifier> spawnShadowSilverOre =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Dreamtinker.getLocation("add_shadow_silver_ore"));
    public static ResourceKey<BiomeModifier> spawnTransmutationGoldOre =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Dreamtinker.getLocation("add_transmutation_gold_ore"));


    public static void bootstrap(BootstapContext<BiomeModifier> ctx) {
        HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placed = ctx.lookup(Registries.PLACED_FEATURE);

        HolderSet<Biome> narcissus_targets = direct(
                biomes.getOrThrow(Biomes.RIVER),
                biomes.getOrThrow(Biomes.LUSH_CAVES),
                biomes.getOrThrow(Biomes.FLOWER_FOREST),
                biomes.getOrThrow(Biomes.MEADOW),
                biomes.getOrThrow(Biomes.SNOWY_SLOPES)
        );

        HolderSet<PlacedFeature> feats = direct(
                placed.getOrThrow(ModWorldGen.NARCISSUS_PATCH_PLACED)
        );

        ctx.register(ADD_NARCISSUS, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                narcissus_targets, feats, GenerationStep.Decoration.VEGETAL_DECORATION));
        ctx.register(spawnLarimarOre, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.IS_BEACH),
                                                                                       direct(placed.getOrThrow(placedSmallLarimarOre),
                                                                                              placed.getOrThrow(placedLargeLarimarOre)),
                                                                                       GenerationStep.Decoration.UNDERGROUND_ORES));
        ctx.register(spawnAmberOre, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.IS_NETHER),
                                                                                     direct(placed.getOrThrow(placedSmallAmberOre),
                                                                                            placed.getOrThrow(placedLargeAmberOre)),
                                                                                     GenerationStep.Decoration.UNDERGROUND_ORES));
        ctx.register(spawnBlackSapphire, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(HolderSet.direct(biomes.getOrThrow(Biomes.DEEP_DARK)),
                                                                                          direct(placed.getOrThrow(placedSmallBlackSapphireOre)),
                                                                                          GenerationStep.Decoration.UNDERGROUND_ORES));
        ctx.register(spawnScoleciteOre, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.IS_NETHER),
                                                                                         direct(placed.getOrThrow(placedLargeScoleciteOre)),
                                                                                         GenerationStep.Decoration.UNDERGROUND_ORES));


        HolderSet<Biome> colds =
                HolderSet.direct(biomes.getOrThrow(Biomes.FROZEN_RIVER), biomes.getOrThrow(Biomes.FROZEN_OCEAN), biomes.getOrThrow(Biomes.FROZEN_PEAKS),
                                 // 常见冷平原 / 冰刺
                                 biomes.getOrThrow(Biomes.SNOWY_PLAINS),
                                 biomes.getOrThrow(Biomes.ICE_SPIKES),
                                 // 冷针叶林系
                                 biomes.getOrThrow(Biomes.SNOWY_TAIGA),
                                 // 冷山体 / 高山草甸
                                 biomes.getOrThrow(Biomes.GROVE),
                                 biomes.getOrThrow(Biomes.SNOWY_SLOPES),
                                 biomes.getOrThrow(Biomes.JAGGED_PEAKS),
                                 biomes.getOrThrow(Biomes.STONY_PEAKS),
                                 // 冷海洋系
                                 biomes.getOrThrow(Biomes.COLD_OCEAN),
                                 biomes.getOrThrow(Biomes.DEEP_COLD_OCEAN),
                                 biomes.getOrThrow(Biomes.DEEP_FROZEN_OCEAN),
                                 biomes.getOrThrow(Biomes.WINDSWEPT_GRAVELLY_HILLS));


        ctx.register(spawnColdIronOre, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(colds,
                                                                                        direct(placed.getOrThrow(placedSmallColdIronOreUnder),
                                                                                               placed.getOrThrow(placedSmallColdIronOreHigh)),
                                                                                        GenerationStep.Decoration.UNDERGROUND_DECORATION));

        HolderSet<Biome> orichalcumOre_targets =
                HolderSet.direct(biomes.getOrThrow(Biomes.DRIPSTONE_CAVES),
                                 biomes.getOrThrow(Biomes.GROVE),
                                 biomes.getOrThrow(Biomes.SNOWY_SLOPES),
                                 biomes.getOrThrow(Biomes.JAGGED_PEAKS),
                                 biomes.getOrThrow(Biomes.FROZEN_PEAKS),
                                 biomes.getOrThrow(Biomes.STONY_PEAKS));

        ctx.register(spawnOrichalcumOre, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(orichalcumOre_targets,
                                                                                          direct(placed.getOrThrow(placedSmallOrichalcumOre)),
                                                                                          GenerationStep.Decoration.UNDERGROUND_DECORATION));
        ctx.register(spawnShadowSilverOre, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.HAS_STRONGHOLD),
                                                                                            direct(placed.getOrThrow(placedSmallShadowSilverOre)),
                                                                                            GenerationStep.Decoration.UNDERGROUND_DECORATION));
        ctx.register(spawnTransmutationGoldOre, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                                                                                                 direct(placed.getOrThrow(placedSmallTransmutationGoldOre)),
                                                                                                 GenerationStep.Decoration.UNDERGROUND_DECORATION));
    }
}
