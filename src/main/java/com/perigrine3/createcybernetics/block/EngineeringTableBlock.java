package com.perigrine3.createcybernetics.block;

import com.mojang.serialization.MapCodec;
import com.perigrine3.createcybernetics.block.entity.EngineeringTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EngineeringTableBlock extends BaseEntityBlock {
    public static final MapCodec<EngineeringTableBlock> CODEC = simpleCodec(EngineeringTableBlock::new);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape BASE = box(0, 0, 0, 16, 16, 16);
    private static final VoxelShape ARM_TALL = box(4, 16, 0, 12, 32, 4);
    private static final VoxelShape TOP_TALL = box(4, 24, 4, 12, 32, 12);

    private static final VoxelShape SHAPE_NORTH = Shapes.or(BASE, ARM_TALL, TOP_TALL);
    private static final VoxelShape SHAPE_EAST  = rotateYClockwise(SHAPE_NORTH);
    private static final VoxelShape SHAPE_SOUTH = rotateYClockwise(SHAPE_EAST);
    private static final VoxelShape SHAPE_WEST  = rotateYClockwise(SHAPE_SOUTH);
    public EngineeringTableBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    private static VoxelShape shapeForFacing(Direction facing) {
        return switch (facing) {
            case NORTH -> SHAPE_SOUTH;
            case EAST  -> SHAPE_WEST;
            case SOUTH -> SHAPE_NORTH;
            case WEST  -> SHAPE_EAST;
            default    -> SHAPE_NORTH;
        };
    }

    private static VoxelShape rotateYClockwise(VoxelShape shape) {
        final VoxelShape[] out = new VoxelShape[]{Shapes.empty()};
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            out[0] = Shapes.or(out[0], Shapes.box(
                    1.0D - maxZ, minY, minX,
                    1.0D - minZ, maxY, maxX
            ));
        });
        return out[0];
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeForFacing(state.getValue(FACING));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapeForFacing(state.getValue(FACING));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    private static boolean upperSpaceIsClear(Level level, BlockPos pos) {
        return level.getBlockState(pos.above()).canBeReplaced();
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block block, BlockPos fromPos, boolean moving) {
        super.neighborChanged(state, level, pos, block, fromPos, moving);

        if (level.isClientSide) return;
        if (!fromPos.equals(pos.above())) return;
        if (!upperSpaceIsClear(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EngineeringTableBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();

        if (!level.getBlockState(pos.above()).canBeReplaced(ctx)) return null;
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return this.rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof EngineeringTableBlockEntity blockEntity) {
                blockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            if (level.getBlockEntity(pos) instanceof EngineeringTableBlockEntity be) {
                sp.openMenu(be, (RegistryFriendlyByteBuf buf) -> buf.writeBlockPos(pos));
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }
}
