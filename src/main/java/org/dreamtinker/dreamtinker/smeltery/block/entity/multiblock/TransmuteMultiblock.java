package org.dreamtinker.dreamtinker.smeltery.block.entity.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.dreamtinker.dreamtinker.common.DreamtinkerTagKeys;
import org.dreamtinker.dreamtinker.smeltery.block.entity.controller.TransmuteBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.HeatingStructureMultiblock;
import slimeknights.tconstruct.smeltery.block.entity.multiblock.MultiblockStructureData;

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

    @Override
    public boolean shouldUpdate(Level world, MultiblockStructureData structure, BlockPos pos, BlockState state) {
        return super.shouldUpdate(world, structure, pos, state);
    }
}
