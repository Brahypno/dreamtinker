package org.dreamtinker.dreamtinker.library.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import java.util.List;

public class ScatterReplaceOreConfiguration implements FeatureConfiguration {

    public static final Codec<ScatterReplaceOreConfiguration> CODEC =
            RecordCodecBuilder.create(instance ->
                                              instance.group(
                                                      OreConfiguration.TargetBlockState.CODEC
                                                              .listOf()
                                                              .fieldOf("targets")
                                                              .forGetter(ScatterReplaceOreConfiguration::targets),
                                                      Codec.INT
                                                              .fieldOf("range")
                                                              .forGetter(ScatterReplaceOreConfiguration::range),
                                                      Codec.FLOAT
                                                              .fieldOf("chance")
                                                              .forGetter(ScatterReplaceOreConfiguration::chance),
                                                      Codec.INT
                                                              .fieldOf("max_per_vein")
                                                              .forGetter(ScatterReplaceOreConfiguration::maxPerVein)
                                              ).apply(instance, ScatterReplaceOreConfiguration::new)
            );

    private final List<OreConfiguration.TargetBlockState> targets;
    private final int range;
    private final float chance;
    private final int maxPerVein;

    public ScatterReplaceOreConfiguration(
            List<OreConfiguration.TargetBlockState> targets,
            int range,
            float chance,
            int maxPerVein) {
        this.targets = targets;
        this.range = range;
        this.chance = chance;
        this.maxPerVein = maxPerVein;
    }

    public List<OreConfiguration.TargetBlockState> targets() {
        return targets;
    }

    /**
     * 半径，扫描 [-range, range] 的立方体范围
     */
    public int range() {
        return range;
    }

    /**
     * 矿脉内部每一格被尝试替换的概率（0.0F~1.0F）
     */
    public float chance() {
        return chance;
    }

    /**
     * 一团相邻矿脉（连通块）内最多替换多少格
     */
    public int maxPerVein() {
        return maxPerVein;
    }
}
