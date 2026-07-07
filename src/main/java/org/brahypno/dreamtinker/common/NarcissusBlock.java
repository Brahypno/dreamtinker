package org.brahypno.dreamtinker.common;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class NarcissusBlock extends FlowerBlock implements BonemealableBlock {
    private static final BlockPos[] WATER_NEIGHBOR_OFFSETS = new BlockPos[]{
            new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1),
            new BlockPos(1, 0, 1), new BlockPos(1, 0, -1), new BlockPos(-1, 0, 1), new BlockPos(-1, 0, -1)
    };

    public NarcissusBlock(Supplier<MobEffect> effectSupplier, int duration, Properties properties) {
        super(effectSupplier, duration, properties);
    }

    private static boolean hasWaterAroundBase(LevelReader level, BlockPos basePos) {
        for (BlockPos offset : WATER_NEIGHBOR_OFFSETS) {
            if (level.getFluidState(basePos.offset(offset)).is(FluidTags.WATER)){
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return (below.is(BlockTags.DIRT) || below.is(Blocks.SAND) || below.is(Blocks.MOSS_BLOCK)) && hasWaterAroundBase(level, pos.below());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)){
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return random.nextFloat() < 0.65f;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockState flower = defaultBlockState();

        for (int i = 0; i < 16; i++) {
            BlockPos target = pos.offset(random.nextInt(7) - 3, random.nextInt(3) - 1, random.nextInt(7) - 3);
            if (level.isEmptyBlock(target) && flower.canSurvive(level, target)){
                level.setBlock(target, flower, 3);
                return;
            }
        }
    }
}