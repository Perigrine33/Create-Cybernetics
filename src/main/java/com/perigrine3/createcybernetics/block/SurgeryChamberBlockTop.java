package com.perigrine3.createcybernetics.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SurgeryChamberBlockTop extends HorizontalDirectionalBlock {
    public static final BooleanProperty OPENED = BooleanProperty.create("opened");
    public static final MapCodec<SurgeryChamberBlockTop> CODEC = simpleCodec(SurgeryChamberBlockTop::new);
    private static final VoxelShape BACKWALL    = Block.box(14, 0, 0, 16, 16, 16);
    private static final VoxelShape WESTWALL    = Block.box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape EASTWALL    = Block.box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape TOPWALL     = Block.box(0, 14, 16, 16, 16, 16);
    private static final VoxelShape DOOR_CLOSED = Block.box(1, 0, 2, 2, 16, 14);

    private static final VoxelShape SHAPE_OPEN = Shapes.or(BACKWALL, WESTWALL, EASTWALL, TOPWALL);
    private static final VoxelShape SHAPE_CLOSED = Shapes.or(BACKWALL, WESTWALL, EASTWALL, TOPWALL, DOOR_CLOSED);



    public SurgeryChamberBlockTop(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPENED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean opened = state.getValue(OPENED);
        return opened ? SHAPE_OPEN : SHAPE_CLOSED;    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPENED);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {

        if (!level.isClientSide) {
            boolean newState = !state.getValue(OPENED);
            level.setBlock(pos, state.setValue(OPENED, newState), 3);
            BlockPos bottomPos = pos.below();
            BlockState bottomState = level.getBlockState(bottomPos);
            if (bottomState.is(ModBlocks.SURGERY_CHAMBER_BOTTOM.get())) {
                level.setBlock(bottomPos, bottomState.setValue(SurgeryChamberBlockBottom.OPENED, newState), 3);
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
            // Remove bottom block if top is destroyed
            BlockPos bottomPos = pos.below();
            BlockState bottomState = level.getBlockState(bottomPos);
            if (bottomState.is(ModBlocks.SURGERY_CHAMBER_BOTTOM.get())) {
                level.setBlock(bottomPos, Blocks.AIR.defaultBlockState(), 35);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
