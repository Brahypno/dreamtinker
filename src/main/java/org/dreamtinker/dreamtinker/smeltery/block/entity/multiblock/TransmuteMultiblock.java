package org.dreamtinker.dreamtinker.smeltery.block.entity.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.smeltery.block.component.AshenButtonBlock;
import org.dreamtinker.dreamtinker.smeltery.block.entity.controller.TransmuteBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.HeatingStructureMultiblock;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.MultiblockStructureData;

import javax.annotation.Nullable;

public class TransmuteMultiblock extends HeatingStructureMultiblock<TransmuteBlockEntity> {
    public TransmuteMultiblock(TransmuteBlockEntity parent) {
        super(parent, true, true, true);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected boolean isValidBlock(Block block) {
        return block.builtInRegistryHolder().is(DreamtinkerTagKeys.Blocks.TRANSMUTE);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected boolean isValidFloor(Block block) {
        return block.builtInRegistryHolder().is(DreamtinkerTagKeys.Blocks.TRANSMUTE_FLOOR);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected boolean isValidTank(Block block) {
        return block.builtInRegistryHolder().is(DreamtinkerTagKeys.Blocks.TRANSMUTE_TANKS);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected boolean isValidWall(Block block) {
        return block.builtInRegistryHolder().is(DreamtinkerTagKeys.Blocks.TRANSMUTE_WALL);
    }

    private boolean hasAtMostOneMatching(Level level, BlockPos minPos, BlockPos maxPos) {
        int minX = minPos.getX();
        int minY = minPos.getY();
        int minZ = minPos.getZ();
        int maxX = maxPos.getX();
        int maxY = maxPos.getY();
        int maxZ = maxPos.getZ();
        
        int melt_switch_count = 0;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    boolean onBorder = x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ;
                    if (!onBorder){
                        continue;
                    }
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (isValidMeltingSwitch(state)){
                        melt_switch_count++;
                        if (melt_switch_count > 1)
                            return false;
                    }
                }
            }
        }

        return true;
    }

    @SuppressWarnings("deprecation")
    protected boolean isValidMeltingSwitch(BlockState state) {
        return state.hasProperty(AshenButtonBlock.Function_Set) &&
               state.getBlock().builtInRegistryHolder().is(DreamtinkerTagKeys.Blocks.TRANSMUTE_MELTING_SWITCH);
    }

    @Override
    public boolean shouldUpdate(Level world, MultiblockStructureData structure, BlockPos pos, BlockState state) {
        return isValidMeltingSwitch(state) || super.shouldUpdate(world, structure, pos, state);
    }

    @Nullable
    @Override
    public StructureData detectMultiblock(Level world, BlockPos master, Direction facing) {
        StructureData data = super.detectMultiblock(world, master, facing);
        if (null != data && hasAtMostOneMatching(world, data.getMinPos(), data.getMaxPos())){
            return data;
        }else
            return null;
    }
}
