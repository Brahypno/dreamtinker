package org.dreamtinker.dreamtinker.world.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.common.DreamtinkerCommon;

import java.util.List;
import java.util.function.Supplier;

public class ModWorldGen {

    // 资源键
    public static final ResourceKey<ConfiguredFeature<?, ?>> NARCISSUS_PATCH =
            key(Registries.CONFIGURED_FEATURE, "narcissus_patch");

    public static final ResourceKey<PlacedFeature> NARCISSUS_PATCH_PLACED =
            key(Registries.PLACED_FEATURE, "narcissus_patch");

    //
    public static ResourceKey<ConfiguredFeature<?, ?>> configuredSmallLarimarOre =
            key(Registries.CONFIGURED_FEATURE, "larimar_ore_small");
    public static ResourceKey<PlacedFeature> placedSmallLarimarOre =
            key(Registries.PLACED_FEATURE, "larimar_ore_small");
    //
    public static ResourceKey<ConfiguredFeature<?, ?>> configuredLargeLarimarOre =
            key(Registries.CONFIGURED_FEATURE, "larimar_ore_large");
    public static ResourceKey<PlacedFeature> placedLargeLarimarOre =
            key(Registries.PLACED_FEATURE, "larimar_ore_large");

    public static ResourceKey<ConfiguredFeature<?, ?>> configuredSmallAmberOre =
            key(Registries.CONFIGURED_FEATURE, "amber_ore_small");
    public static ResourceKey<PlacedFeature> placedSmallAmberOre =
            key(Registries.PLACED_FEATURE, "amber_ore_small");
    //
    public static ResourceKey<ConfiguredFeature<?, ?>> configuredLargeAmberOre =
            key(Registries.CONFIGURED_FEATURE, "amber_ore_large");
    public static ResourceKey<PlacedFeature> placedLargeAmberOre =
            key(Registries.PLACED_FEATURE, "amber_ore_large");


    public static ResourceKey<ConfiguredFeature<?, ?>> configuredSmallBlackSapphireOre =
            key(Registries.CONFIGURED_FEATURE, "black_sapphire_ore");
    public static ResourceKey<PlacedFeature> placedSmallBlackSapphireOre =
            key(Registries.PLACED_FEATURE, "black_sapphire_ore");


    /**
     * ConfiguredFeature：一簇水仙（cross 小花），仅在能存活的位置尝试放置
     */
    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
        var flowerState = DreamtinkerCommon.narcissus.get().defaultBlockState();

        // “简单方块”子特征，附带 wouldSurvive 过滤（防止刷到无效位置）
        Holder<PlacedFeature> inner = PlacementUtils.filtered(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(flowerState)),
                BlockPredicate.allOf(
                        BlockPredicate.replaceable(),                                // 当前位置必须可替换（空气/草等）
                        BlockPredicate.wouldSurvive(flowerState, BlockPos.ZERO)      // 且能存活（下面是草土等）
                )
        );

        // 一簇：每次尝试 24 下，水平扩散 6，竖直 2
        var patchCfg = new RandomPatchConfiguration(24, 6, 2, inner);
        ctx.register(NARCISSUS_PATCH, new ConfiguredFeature<>(Feature.RANDOM_PATCH, patchCfg));

        RuleTest sandstone = new BlockMatchTest(Blocks.SANDSTONE);
        BlockState larimarOre = DreamtinkerCommon.larimarOre.get().defaultBlockState();
        register(ctx, configuredSmallLarimarOre, Feature.ORE, new OreConfiguration(sandstone, larimarOre, 4));
        register(ctx, configuredLargeLarimarOre, Feature.ORE, new OreConfiguration(sandstone, larimarOre, 6));

        RuleTest basalt = new BlockMatchTest(Blocks.BASALT);
        BlockState amberOre = DreamtinkerCommon.amberOre.get().defaultBlockState();
        register(ctx, configuredSmallAmberOre, Feature.ORE, new OreConfiguration(basalt, amberOre, 3));
        register(ctx, configuredLargeAmberOre, Feature.ORE, new OreConfiguration(basalt, amberOre, 5));

        var stoneTest = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        var deepTest = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        var black_sapphire_targets = List.of(
                OreConfiguration.target(stoneTest, DreamtinkerCommon.blackSapphireOre.get().defaultBlockState()),
                OreConfiguration.target(deepTest, DreamtinkerCommon.DeepSlateBlackSapphireOre.get().defaultBlockState())
        );
        register(ctx, configuredSmallBlackSapphireOre, Feature.ORE, new OreConfiguration(black_sapphire_targets, 6, 0.1f));
    }

    /**
     * PlacedFeature：频率/分布（示例：平均每 2 个区块出现 1 次）
     */
    public static void bootstrapPlaced(BootstapContext<PlacedFeature> ctx) {
        HolderGetter<ConfiguredFeature<?, ?>> conf = ctx.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> cf = conf.getOrThrow(NARCISSUS_PATCH);

        ctx.register(NARCISSUS_PATCH_PLACED, new PlacedFeature(cf, List.of(
                RarityFilter.onAverageOnceEvery(2),              // 稀有度（改大更稀有；或换成 CountPlacement）
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                BiomeFilter.biome()
        )));
        register(ctx, placedSmallLarimarOre, configuredSmallLarimarOre, CountPlacement.of(5), InSquarePlacement.spread(),
                 HeightRangePlacement.triangle(
                         VerticalAnchor.absolute(45), VerticalAnchor.absolute(70)), BiomeFilter.biome());
        register(ctx, placedLargeLarimarOre, configuredLargeLarimarOre, CountPlacement.of(3), InSquarePlacement.spread(),
                 HeightRangePlacement.triangle(
                         VerticalAnchor.absolute(45), VerticalAnchor.absolute(70)), BiomeFilter.biome());

        register(ctx, placedSmallAmberOre, configuredSmallAmberOre,
                 CountPlacement.of(5),
                 InSquarePlacement.spread(),
                 HeightRangePlacement.uniform(VerticalAnchor.absolute(10), VerticalAnchor.absolute(120)),
                 BiomeFilter.biome());

        // 下界里替换玄武岩的大矿脉
        register(ctx, placedLargeAmberOre, configuredLargeAmberOre,
                 CountPlacement.of(3),
                 InSquarePlacement.spread(),
                 HeightRangePlacement.uniform(VerticalAnchor.absolute(10), VerticalAnchor.absolute(120)),
                 BiomeFilter.biome());

        register(ctx, placedSmallBlackSapphireOre, configuredSmallBlackSapphireOre,
                 CountPlacement.of(5),
                 InSquarePlacement.spread(),
                 HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(0)),
                 BiomeFilter.biome());
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        context.register(key, new ConfiguredFeature(feature, config));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, Supplier<F> feature, FC config) {
        register(context, key, (Feature) feature.get(), config);
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, ResourceKey<ConfiguredFeature<?, ?>> configured, PlacementModifier... placement) {
        context.register(key, new PlacedFeature(context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(configured), List.of(placement)));
    }

    protected static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> registry, String name) {
        return ResourceKey.create(registry, Dreamtinker.getLocation(name));
    }
}
