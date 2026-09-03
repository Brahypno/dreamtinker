package org.brahypno.dreamtinker.library.worldgen;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.*;

public class ScatterReplaceOreFeature extends Feature<ScatterReplaceOreConfiguration> {
    private static final Direction[] DIRECTIONS = Direction.values();

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

        // Keep the original x/y/z insertion order and HashMap: vein iteration and
        // random consumption must remain identical for an existing world seed.
        Map<BlockPos, OreConfiguration.TargetBlockState> candidates = collectCandidates(level, random, origin, cfg);

        if (candidates.isEmpty()){
            return false;
        }

        // 2. 目标矿 state 集合：用来判断「已经是我矿」的格子，作为连通性桥梁
        Set<BlockState> targetStates = new HashSet<>();
        for (OreConfiguration.TargetBlockState target : cfg.targets()) {
            targetStates.add(target.state);
        }

        Set<BlockPos> visited = new HashSet<>();
        List<BlockPos> veinReplaceCandidates = new ArrayList<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        int totalReplaced = 0;

        for (BlockPos start : candidates.keySet()) {
            if (visited.contains(start)){
                continue;
            }

            // 这一条「矿脉」里真正「仍为原矿、可替换」的位置
            veinReplaceCandidates.clear();
            visited.add(start);
            queue.add(start);

            while (!queue.isEmpty()) {
                BlockPos currentPos = queue.removeFirst();

                // 如果这一格在 candidates 里，说明它是「仍为原矿」
                if (candidates.containsKey(currentPos)){
                    veinReplaceCandidates.add(currentPos);
                }

                for (Direction dir : DIRECTIONS) {
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

                    // 连通性的判定：候选原矿 或 已经是目标矿 均可视作「矿脉的一部分」
                    boolean isReplaceCandidate = candidates.containsKey(neighbor);
                    if (isReplaceCandidate || targetStates.contains(level.getBlockState(neighbor))){
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

    private static Map<BlockPos, OreConfiguration.TargetBlockState> collectCandidates(
            WorldGenLevel level, RandomSource random, BlockPos origin, ScatterReplaceOreConfiguration cfg) {
        Map<BlockPos, OreConfiguration.TargetBlockState> candidates = new HashMap<>();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int range = cfg.range();

        if (!canCacheRules(cfg.targets())){
            // Custom/random rule tests may consume RNG even for non-matches.
            // Do not evaluate them against a palette or skip their original calls.
            for (int dx = -range; dx <= range; dx++) {
                for (int dy = -range; dy <= range; dy++) {
                    for (int dz = -range; dz <= range; dz++) {
                        cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                        BlockState state = level.getBlockState(cursor);
                        for (OreConfiguration.TargetBlockState target : cfg.targets()) {
                            if (target.target.test(state, random)){
                                candidates.put(cursor.immutable(), target);
                                break;
                            }
                        }
                    }
                }
            }
            return candidates;
        }

        // Per-placement caches only: no stale tag membership after /reload, no
        // world retention and no shared mutable state between generation workers.
        SectionScanner scanner = new SectionScanner(level, random, cfg.targets());
        int minZ = origin.getZ() - range;
        int maxZ = origin.getZ() + range;
        for (int dx = -range; dx <= range; dx++) {
            int x = origin.getX() + dx;
            for (int dy = -range; dy <= range; dy++) {
                int y = origin.getY() + dy;
                for (int z = minZ; z <= maxZ; ) {
                    int endZ = Math.min(maxZ, z | 15);
                    SectionMatch match = scanner.section(x, y, z);
                    if (match == null || match.hasTargets()){
                        for (int currentZ = z; currentZ <= endZ; currentZ++) {
                            cursor.set(x, y, currentZ);
                            // Preserve out-of-height behavior for unusual data packs.
                            BlockState state = match == null
                                               ? level.getBlockState(cursor)
                                               : match.section().getBlockState(x & 15, y & 15, currentZ & 15);
                            OreConfiguration.TargetBlockState target = scanner.target(state);
                            if (target != null){
                                candidates.put(cursor.immutable(), target);
                            }
                        }
                    }
                    z = endZ + 1;
                }
            }
        }
        return candidates;
    }

    private static boolean canCacheRules(List<OreConfiguration.TargetBlockState> targets) {
        for (OreConfiguration.TargetBlockState target : targets) {
            Class<?> type = target.target.getClass();
            // Exact classes, not instanceof: a subclass may add RNG or side effects.
            if (type != BlockMatchTest.class && type != BlockStateMatchTest.class
                && type != TagMatchTest.class && type != TagAndTagRuleTest.class){
                return false;
            }
        }
        return true;
    }

    private record SectionMatch(LevelChunkSection section, boolean hasTargets) {}

    private static final class SectionScanner {
        private final WorldGenLevel level;
        private final RandomSource random;
        private final List<OreConfiguration.TargetBlockState> targets;
        private final Long2ObjectOpenHashMap<SectionMatch> sections = new Long2ObjectOpenHashMap<>();
        private final IdentityHashMap<BlockState, OreConfiguration.TargetBlockState> matches = new IdentityHashMap<>();

        private SectionScanner(WorldGenLevel level, RandomSource random, List<OreConfiguration.TargetBlockState> targets) {
            this.level = level;
            this.random = random;
            this.targets = targets;
        }

        private OreConfiguration.TargetBlockState target(BlockState state) {
            if (matches.containsKey(state)){
                return matches.get(state);
            }
            OreConfiguration.TargetBlockState match = null;
            for (OreConfiguration.TargetBlockState target : targets) {
                if (target.target.test(state, random)){
                    match = target;
                    break;
                }
            }
            matches.put(state, match);
            return match;
        }

        private SectionMatch section(int x, int y, int z) {
            if (y < level.getMinBuildHeight() || y >= level.getMaxBuildHeight()){
                return null;
            }
            long key = SectionPos.asLong(x >> 4, y >> 4, z >> 4);
            SectionMatch match = sections.get(key);
            if (match == null){
                LevelChunkSection section = level.getChunk(x >> 4, z >> 4).getSection(level.getSectionIndex(y));
                // maybeHas inspects palette entries, not all 4096 block positions.
                // A false result proves the whole section can be skipped.
                match = new SectionMatch(section, section.maybeHas(state -> target(state) != null));
                sections.put(key, match);
            }
            return match;
        }
    }
}
