package com.perigrine3.createcybernetics.block;

import com.mojang.serialization.MapCodec;
import com.perigrine3.createcybernetics.block.entity.ModBlockEntities;
import com.perigrine3.createcybernetics.block.entity.SurgeryTableBlockEntity;
import com.perigrine3.createcybernetics.common.surgery.PlayerSurgeryMinigameManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SurgeryTableBlock extends BaseEntityBlock implements EntityBlock {

    public static final MapCodec<SurgeryTableBlock> CODEC = simpleCodec(SurgeryTableBlock::new);

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;

    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);
    private static final VoxelShape SHAPE_EAST_WEST = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);

    public SurgeryTableBlock(Properties properties) {
        super(properties);
        registerDefaultState(
                stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(PART, BedPart.FOOT)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        BlockPos footPos = context.getClickedPos();
        BlockPos headPos = footPos.relative(facing);
        Level level = context.getLevel();

        if (headPos.getY() >= level.getMaxBuildHeight()) {
            return null;
        }

        if (!level.getBlockState(headPos).canBeReplaced(context)) {
            return null;
        }

        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(PART, BedPart.FOOT);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) {
            return;
        }

        Direction facing = state.getValue(FACING);
        BlockPos headPos = pos.relative(facing);

        level.setBlock(headPos, state.setValue(PART, BedPart.HEAD), Block.UPDATE_ALL);
        level.blockUpdated(pos, this);
        state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);
        BedPart part = state.getValue(PART);
        Direction otherHalfDirection = part == BedPart.FOOT ? facing : facing.getOpposite();

        if (direction == otherHalfDirection) {
            if (!neighborState.is(this) || neighborState.getValue(PART) == part) {
                return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
            }
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() == newState.getBlock()) {
            super.onRemove(state, level, pos, newState, isMoving);
            return;
        }

        Direction facing = state.getValue(FACING);
        BedPart part = state.getValue(PART);
        BlockPos otherPos = part == BedPart.FOOT ? pos.relative(facing) : pos.relative(facing.getOpposite());
        BlockState otherState = level.getBlockState(otherPos);

        if (otherState.is(this) && otherState.getValue(PART) != part) {
            level.removeBlock(otherPos, false);
        }

        BlockPos headPos = getHeadPos(state, pos);
        if (level.getBlockEntity(headPos) instanceof SurgeryTableBlockEntity table) {
            Player patient = table.getPatient();
            if (patient != null && patient.isSleeping()) {
                patient.stopSleeping();
            }
            table.clearPatient();
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hitResult) {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockPos headPos = getHeadPos(state, pos);
        BlockEntity be = level.getBlockEntity(headPos);

        if (!(be instanceof SurgeryTableBlockEntity table)) {
            return InteractionResult.PASS;
        }

        Player currentPatient = table.getPatient();

        if (player.isShiftKeyDown()
                && currentPatient instanceof ServerPlayer serverPatient
                && player instanceof ServerPlayer serverSurgeon
                && currentPatient != player) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }

            if (!PlayerSurgeryMinigameManager.hasActiveSession(serverSurgeon)
                    && !PlayerSurgeryMinigameManager.hasActiveSession(serverPatient)) {
                PlayerSurgeryMinigameManager.startCountdown(serverSurgeon, serverPatient, table);
            }

            return InteractionResult.CONSUME;
        }

        if (player.isShiftKeyDown()) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            }

            if (player.isSleeping()) {
                player.stopSleeping();
                table.clearPatient();
                return InteractionResult.CONSUME;
            }

            if (currentPatient != null && currentPatient != player) {
                return InteractionResult.CONSUME;
            }

            table.setPatient(player);
            player.startSleeping(headPos);
            return InteractionResult.CONSUME;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(table, buf -> buf.writeBlockPos(headPos));
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter level, BlockPos pos, LivingEntity sleeper) {
        return true;
    }

    @Override
    public Direction getBedDirection(BlockState state, LevelReader level, BlockPos pos) {
        return state.getValue(FACING);
    }

    @Override
    public void setBedOccupied(BlockState state, Level level, BlockPos pos, LivingEntity sleeper, boolean occupied) {
        BlockPos headPos = getHeadPos(state, pos);
        BlockEntity be = level.getBlockEntity(headPos);

        if (be instanceof SurgeryTableBlockEntity table) {
            if (occupied && sleeper instanceof Player player) {
                table.setPatient(player);
            } else if (!occupied) {
                table.clearPatient();
            }
        }
    }

    private BlockPos getHeadPos(BlockState state, BlockPos pos) {
        return state.getValue(PART) == BedPart.HEAD ? pos : pos.relative(state.getValue(FACING));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == BedPart.HEAD ? new SurgeryTableBlockEntity(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return null;
        }

        return createTickerHelper(blockEntityType, ModBlockEntities.SURGERY_TABLE.get(), SurgeryTableBlockEntity::serverTick);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return facing.getAxis() == Direction.Axis.Z ? SHAPE_NORTH_SOUTH : SHAPE_EAST_WEST;
    }
}