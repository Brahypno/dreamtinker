package org.dreamtinker.dreamtinker.common;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class NarcissusBlock extends FlowerBlock {
    private static final BlockPos[] WATER_NEIGHBOR_OFFSETS = new BlockPos[]{
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),

            // diagonals
            new BlockPos(1, 0, 1),
            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, 0, -1)
    };

    public NarcissusBlock(Supplier<MobEffect> effectSupplier, int p_53513_, Properties p_53514_) {
        super(effectSupplier, p_53513_, p_53514_);
    }

    private static boolean hasWaterAroundBase(LevelReader level, BlockPos basePos) {
        for (BlockPos offset : WATER_NEIGHBOR_OFFSETS) {
            BlockPos checkPos = basePos.offset(offset);

            if (level.getFluidState(checkPos).is(FluidTags.WATER)){
                return true;
            }
        }

        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());

        if (below.is(BlockTags.DIRT) || below.is(Blocks.SAND)){
            return hasWaterAroundBase(level, pos.below());
        }

        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)){
            level.destroyBlock(pos, true);
        }
    }
}
