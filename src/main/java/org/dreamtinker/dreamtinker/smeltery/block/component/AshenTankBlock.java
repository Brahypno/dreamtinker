package org.dreamtinker.dreamtinker.smeltery.block.component;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.dreamtinker.dreamtinker.smeltery.DreamTinkerSmeltery;
import org.dreamtinker.dreamtinker.smeltery.block.entity.component.AshenTankBlockEntity;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;

import javax.annotation.Nullable;

public class AshenTankBlock extends SearedTankBlock {
    public AshenTankBlock(Properties properties, int capacity, PushReaction pushReaction) {
        super(properties, capacity, pushReaction);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new AshenTankBlockEntity(DreamTinkerSmeltery.tank.get(), pPos, pState, this);
    }
}
