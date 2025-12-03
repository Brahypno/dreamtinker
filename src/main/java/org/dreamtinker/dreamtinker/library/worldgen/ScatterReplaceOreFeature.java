package org.dreamtinker.dreamtinker.library.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import java.util.*;

public class ScatterReplaceOreFeature extends Feature<ScatterReplaceOreConfiguration> {

    public ScatterReplaceOreFeature(Codec<ScatterReplaceOreConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<ScatterReplaceOreConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        RandomSource random = ctx.random();
        BlockPos origin = ctx.origin();
        ScatterReplaceOreConfiguration cfg = ctx.config();

        int range = cfg.range();
        float chance = cfg.chance();
        int maxPerVein = cfg.maxPerVein();

        if (range <= 0 || maxPerVein <= 0 || chance <= 0.0F || cfg.targets().isEmpty()){
            return false;
        }

        // 1. 收集扫描范围内所有“潜在可替换的矿”
        Map<BlockPos, OreConfiguration.TargetBlockState> candidates = new HashMap<>();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int dx = -range; dx <= range; ++dx) {
            for (int dy = -range; dy <= range; ++dy) {
                for (int dz = -range; dz <= range; ++dz) {
                    mutable.set(origin.getX() + dx,
                                origin.getY() + dy,
                                origin.getZ() + dz);

                    BlockState current = level.getBlockState(mutable);

                    for (OreConfiguration.TargetBlockState target : cfg.targets()) {
                        if (target.target.test(current, random)){
                            // 命中某个 target，就记录下来，并不继续匹配其它 target
                            candidates.put(mutable.immutable(), target);
                            break;
                        }
                    }
                }
            }
        }

        if (candidates.isEmpty()){
            return false;
        }

        // 2. 按相邻关系（6 方向）划分“矿脉”（连通块）
        Set<BlockPos> visited = new HashSet<>();
        int totalReplaced = 0;

        for (BlockPos start : candidates.keySet()) {
            if (visited.contains(start)){
                continue;
            }

            // BFS / flood fill 找出这一团 vein
            List<BlockPos> veinPositions = new ArrayList<>();
            Deque<BlockPos> queue = new ArrayDeque<>();
            visited.add(start);
            queue.add(start);

            while (!queue.isEmpty()) {
                BlockPos currentPos = queue.removeFirst();
                veinPositions.add(currentPos);

                for (Direction dir : Direction.values()) {
                    // 只用 6 方向邻接
                    BlockPos neighbor = currentPos.relative(dir);
                    if (!visited.contains(neighbor) && candidates.containsKey(neighbor)){
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }

            if (veinPositions.isEmpty()){
                continue;
            }

            // 3. 对这一团矿脉进行随机化 + 按 maxPerVein 限制替换数量
            // 为了随机选择 vein 内哪些位置被替换，我们先打乱顺序
            // RandomSource 没有直接的 shuffle，用 JDK Random 包一下
            Collections.shuffle(veinPositions, new Random(random.nextLong()));

            int replacedInVein = 0;

            for (BlockPos pos : veinPositions) {
                if (replacedInVein >= maxPerVein){
                    break;
                }

                // 再次确认这个位置还在 candidates 里（理论上在）
                OreConfiguration.TargetBlockState target = candidates.get(pos);
                if (target == null){
                    continue;
                }

                if (random.nextFloat() < chance){
                    level.setBlock(pos, target.state, 2);
                    replacedInVein++;
                    totalReplaced++;
                }
            }
        }

        return totalReplaced > 0;
    }
}
