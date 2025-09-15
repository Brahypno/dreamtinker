package org.dreamtinker.dreamtinker.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import org.dreamtinker.dreamtinker.Dreamtinker;
import org.dreamtinker.dreamtinker.register.DreamtinkerBlocks;

import java.util.List;

public class ModWorldgen {

    // 资源键
    public static final ResourceKey<ConfiguredFeature<?, ?>> NARCISSUS_PATCH =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, Dreamtinker.getLocation("narcissus_patch"));

    public static final ResourceKey<PlacedFeature> NARCISSUS_PATCH_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, Dreamtinker.getLocation("narcissus_patch"));


    /**
     * ConfiguredFeature：一簇水仙（cross 小花），仅在能存活的位置尝试放置
     */
    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
        var flowerState = DreamtinkerBlocks.narcissus.get().defaultBlockState();

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
    }
}
