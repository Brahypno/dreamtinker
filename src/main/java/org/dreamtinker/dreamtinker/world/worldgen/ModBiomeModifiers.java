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
import static org.dreamtinker.dreamtinker.world.worldgen.ModWorldGen.placedLargeLarimarOre;
import static org.dreamtinker.dreamtinker.world.worldgen.ModWorldGen.placedSmallLarimarOre;

public class ModBiomeModifiers {

    public static final ResourceKey<BiomeModifier> ADD_NARCISSUS =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Dreamtinker.getLocation("add_narcissus"));
    public static ResourceKey<BiomeModifier> spawnLarimarOre =
            ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, Dreamtinker.getLocation("add_larimar_ore"));

    /**
     * 把上面的 PlacedFeature 加进目标群系（此处示例：平原 + 花林）
     */
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
                                                                                       GenerationStep.Decoration.UNDERGROUND_DECORATION));
    }
}
