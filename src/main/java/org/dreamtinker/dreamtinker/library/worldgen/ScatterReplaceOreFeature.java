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
import java.util.stream.Collectors;

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

        // 1. 收集当前范围内「仍为原矿、可被替换」的格子
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
                            // 命中 RuleTest：这是「仍为原矿、可被替换」的位置
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

        // 2. 目标矿 state 集合：用来判断「已经是我矿」的格子，作为连通性桥梁
        Set<BlockState> targetStates = cfg.targets().stream()
                                          .map(targetBlockState -> targetBlockState.state)
                                          .collect(Collectors.toSet());

        Set<BlockPos> visited = new HashSet<>();
        int totalReplaced = 0;

        for (BlockPos start : candidates.keySet()) {
            if (visited.contains(start)){
                continue;
            }

            // 这一条「矿脉」里的所有位置（包括原矿 + 已为目标矿）
            List<BlockPos> veinAllPositions = new ArrayList<>();
            // 这一条「矿脉」里真正「仍为原矿、可替换」的位置
            List<BlockPos> veinReplaceCandidates = new ArrayList<>();

            Deque<BlockPos> queue = new ArrayDeque<>();
            visited.add(start);
            queue.add(start);

            while (!queue.isEmpty()) {
                BlockPos currentPos = queue.removeFirst();
                veinAllPositions.add(currentPos);

                // 如果这一格在 candidates 里，说明它是「仍为原矿」
                if (candidates.containsKey(currentPos)){
                    veinReplaceCandidates.add(currentPos);
                }

                for (Direction dir : Direction.values()) {
                    BlockPos neighbor = currentPos.relative(dir);

                    if (visited.contains(neighbor)){
                        continue;
                    }

                    // 限制在 [-range, range] 立方体内，避免 BFS 突然跑出扫描范围
                    if (Math.abs(neighbor.getX() - origin.getX()) > range
                        || Math.abs(neighbor.getY() - origin.getY()) > range
                        || Math.abs(neighbor.getZ() - origin.getZ()) > range){
                        continue;
                    }

                    BlockState neighborState = level.getBlockState(neighbor);

                    // 连通性的判定：候选原矿 或 已经是目标矿 均可视作「矿脉的一部分」
                    boolean isReplaceCandidate = candidates.containsKey(neighbor);
                    boolean isAlreadyTarget = targetStates.contains(neighborState);

                    if (isReplaceCandidate || isAlreadyTarget){
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }

            if (veinReplaceCandidates.isEmpty()){
                continue;
            }

            // 3. 对这一条连通矿脉中的「可替换原矿」应用 maxPerVein + chance
            Collections.shuffle(veinReplaceCandidates, new Random(random.nextLong()));

            int replacedInVein = 0;

            for (BlockPos pos : veinReplaceCandidates) {
                if (replacedInVein >= maxPerVein){
                    break;
                }

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
