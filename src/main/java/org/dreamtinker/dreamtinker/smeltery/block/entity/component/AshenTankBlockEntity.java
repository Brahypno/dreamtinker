package org.dreamtinker.dreamtinker.smeltery.block.entity.component;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.dreamtinker.dreamtinker.smeltery.DreamTinkerSmeltery;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity;

public class AshenTankBlockEntity extends TankBlockEntity {
    public AshenTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ITankBlock block) {
        super(type, pos, state, block);
    }

    public AshenTankBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, state.getBlock() instanceof ITankBlock tank
                         ? tank
                         : TinkerSmeltery.searedTank.get(SearedTankBlock.TankType.FUEL_TANK));
    }

    public AshenTankBlockEntity(BlockPos pos, BlockState state, ITankBlock block) {
        this(DreamTinkerSmeltery.tank.get(), pos, state, block);
    }
}
