package com.perigrine3.createcybernetics.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SurgeryChamberBlockBottom extends HorizontalDirectionalBlock {
    public static final BooleanProperty OPENED = BooleanProperty.create("opened");
    public static final MapCodec<SurgeryChamberBlockBottom> CODEC = simpleCodec(SurgeryChamberBlockBottom::new);
    private static final VoxelShape BACKWALL     = Block.box(14, 0, 0, 16, 16, 16);
    private static final VoxelShape WESTWALL     = Block.box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape EASTWALL     = Block.box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape BOTTOMWALL   = Block.box(0, 0, 0, 16, 2, 16);
    private static final VoxelShape DOOR_CLOSED  = Block.box(1, 2, 2, 2, 16, 14);

    private static final VoxelShape SHAPE_OPEN = Shapes.or(BACKWALL, WESTWALL, EASTWALL, BOTTOMWALL);
    private static final VoxelShape SHAPE_CLOSED = Shapes.or(BACKWALL, WESTWALL, EASTWALL, BOTTOMWALL, DOOR_CLOSED);



    public SurgeryChamberBlockBottom(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPENED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean opened = state.getValue(OPENED);
        return opened ? SHAPE_OPEN : SHAPE_CLOSED;
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPENED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.getBlockState(pos.above()).canBeReplaced(context)) {
            return null;
        }
        BlockState topState = ModBlocks.SURGERY_CHAMBER_TOP.get().defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(SurgeryChamberBlockTop.OPENED, false);
        level.setBlock(pos.above(), topState, 3);
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(OPENED, false);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            boolean newState = !state.getValue(OPENED);
            level.setBlock(pos, state.setValue(OPENED, newState), 3);
            BlockPos topPos = pos.above();
            BlockState topState = level.getBlockState(topPos);
            if (topState.is(ModBlocks.SURGERY_CHAMBER_TOP.get())) {
                level.setBlock(topPos, topState.setValue(SurgeryChamberBlockTop.OPENED, newState), 3);
            }
            level.playSound(null, pos, newState ?
                            net.minecraft.sounds.SoundEvents.IRON_DOOR_OPEN
                            : net.minecraft.sounds.SoundEvents.IRON_DOOR_CLOSE,
                    SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            // Remove top block if bottom is destroyed
            BlockPos topPos = pos.above();
            BlockState topState = level.getBlockState(topPos);
            if (topState.is(ModBlocks.SURGERY_CHAMBER_TOP.get())) {
                level.setBlock(topPos, Blocks.AIR.defaultBlockState(), 35);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
