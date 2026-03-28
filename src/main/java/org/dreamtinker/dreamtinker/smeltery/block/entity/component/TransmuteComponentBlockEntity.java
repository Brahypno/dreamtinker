package org.dreamtinker.dreamtinker.smeltery.block.entity.component;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.dreamtinker.dreamtinker.smeltery.DreamTinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryComponentBlockEntity;

public class TransmuteComponentBlockEntity extends SmelteryComponentBlockEntity {
    public TransmuteComponentBlockEntity(BlockPos pos, BlockState state) {
        super(DreamTinkerSmeltery.transmuteComponent.get(), pos, state);
    }
}
