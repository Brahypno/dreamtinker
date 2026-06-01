package org.dreamtinker.dreamtinker.smeltery.block.component;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.dreamtinker.dreamtinker.smeltery.block.entity.controller.TransmuteBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryComponentBlockEntity;

import javax.annotation.Nullable;

public class AshenAlloySwitchBlock extends LeverBlock {
    public AshenAlloySwitchBlock(Properties properties) {
        super(properties);
    }

    public static BlockPos getAttachedPos(BlockState state, BlockPos pos) {
        Direction supportDirection = getConnectedDirection(state).getOpposite();
        return pos.relative(supportDirection);
    }

    @Nullable
    private static TransmuteBlockEntity findAttachedController(Level level, BlockPos switchPos, BlockState switchState) {
        BlockPos attachedPos = getAttachedPos(switchState, switchPos);
        BlockEntity be = level.getBlockEntity(attachedPos);

        if (be instanceof TransmuteBlockEntity controller){
            return controller;
        }

        if (be instanceof SmelteryComponentBlockEntity component && component.hasMaster()){
            BlockEntity master = null;
            if (component.getMasterPos() != null){
                master = level.getBlockEntity(component.getMasterPos());
            }
            return master instanceof TransmuteBlockEntity controller ? controller : null;
        }

        return null;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return false;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = super.use(state, level, pos, player, hand, hit);

        if (!level.isClientSide && result.consumesAction()){
            BlockState newState = level.getBlockState(pos);
            TransmuteBlockEntity controller = findAttachedController(level, pos, newState);

            if (controller != null){
                controller.setExternalAlloySwitch(pos, newState.getValue(POWERED));
            }
        }

        return result;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (!level.isClientSide && state.getValue(POWERED)){
            TransmuteBlockEntity controller = findAttachedController(level, pos, state);

            if (controller != null){
                controller.setExternalAlloySwitch(pos, true);
            }
        }
    }

}
