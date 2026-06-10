package org.brahypno.dreamtinker.smeltery.block.component;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.brahypno.dreamtinker.utils.DTMessages;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryComponentBlockEntity;

public class AshenButtonBlock extends SearedBlock {
    public static final IntegerProperty Function_Set = IntegerProperty.create("function_set", 0, 4);
    private final int local_max_property;

    public AshenButtonBlock(Properties properties, boolean requiredBlockEntity, int local_max) {
        super(properties, requiredBlockEntity);
        local_max_property = local_max;
        this.registerDefaultState(this.defaultBlockState()
                                      .setValue(Function_Set, 0)
                                      .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(Function_Set, HorizontalDirectionalBlock.FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Deprecated
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        if (state.getValue(SearedBlock.IN_STRUCTURE)){
            return state;
        }
        return state.setValue(HorizontalDirectionalBlock.FACING, rotation.rotate(state.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Deprecated
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (state.getValue(SearedBlock.IN_STRUCTURE)){
            return state;
        }
        return state.rotate(mirror.getRotation(state.getValue(HorizontalDirectionalBlock.FACING)));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(SearedBlock.IN_STRUCTURE)){
            if (!level.isClientSide){
                int current = state.getValue(Function_Set);
                int next = (current + 1) % (local_max_property + 1);

                BlockState newState = state.setValue(Function_Set, next);
                level.setBlock(pos, newState, Block.UPDATE_ALL);

                ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                String path = id.getPath().replace('/', '.');
                String key = "message." + id.getNamespace() + "." + path + ".mode_";
                DTMessages.clientChat(Component.translatable(key + next), false);
                SmelteryComponentBlockEntity.updateNeighbors(level, pos, newState);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }
}
