package com.perigrine3.createcybernetics.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collections;
import java.util.List;

public class SurgeryChamberBlockTop extends HorizontalDirectionalBlock {
    public static final BooleanProperty OPENED = BooleanProperty.create("opened");
    public static final BooleanProperty SLAVE = BooleanProperty.create("slave");
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
    public static final MapCodec<SurgeryChamberBlockTop> CODEC = simpleCodec(SurgeryChamberBlockTop::new);
    private static final VoxelShape BACKWALL    = Block.box(14, 0, 0, 16, 16, 16);
    private static final VoxelShape WESTWALL    = Block.box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape EASTWALL    = Block.box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape TOPWALL     = Block.box(0, 14, 16, 16, 16, 16);
    private static final VoxelShape DOOR_CLOSED = Block.box(1, 0, 2, 2, 16, 14);

    private static final VoxelShape SHAPE_OPEN = Shapes.or(BACKWALL, WESTWALL, EASTWALL, TOPWALL);
    private static final VoxelShape SHAPE_CLOSED = Shapes.or(BACKWALL, WESTWALL, EASTWALL, TOPWALL, DOOR_CLOSED);

    private static VoxelShape rotateShapeFromNorth(Direction facing, VoxelShape shapeNorth) {
        return switch (facing) {
            case NORTH -> rotateYCounterClockwise(shapeNorth);
            case EAST  -> shapeNorth;
            case SOUTH -> rotateYClockwise(shapeNorth);
            case WEST  -> rotateYClockwise(rotateYClockwise(shapeNorth));
            default    -> shapeNorth;
        };
    }

    private static VoxelShape rotateYClockwise(VoxelShape shape) {
        final VoxelShape[] out = new VoxelShape[]{Shapes.empty()};
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            // coords are 0..1 here, so rotate around 1.0
            out[0] = Shapes.or(out[0], Shapes.box(
                    1.0D - maxZ, minY, minX,
                    1.0D - minZ, maxY, maxX
            ));
        });
        return out[0];
    }

    private static VoxelShape rotateYCounterClockwise(VoxelShape shape) {
        final VoxelShape[] out = new VoxelShape[]{Shapes.empty()};
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            out[0] = Shapes.or(out[0], Shapes.box(
                    minZ, minY, 1.0D - maxX,
                    maxZ, maxY, 1.0D - minX
            ));
        });
        return out[0];
    }


    public SurgeryChamberBlockTop(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPENED, false)
                .setValue(SLAVE, true)
                .setValue(CONNECTED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape baseShape = state.getValue(OPENED) ? SHAPE_OPEN : SHAPE_CLOSED;
        return rotateShapeFromNorth(state.getValue(FACING), baseShape);
    }


    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPENED, SLAVE, CONNECTED);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            boolean newState = !state.getValue(OPENED);
            level.setBlock(pos, state.setValue(OPENED, newState), 3);
            BlockPos bottomPos = pos.below();
            BlockState bottomState = level.getBlockState(bottomPos);
            if (bottomState.is(ModBlocks.SURGERY_CHAMBER_BOTTOM.get())) {
                BlockState newBottomState = bottomState
                        .setValue(SurgeryChamberBlockBottom.OPENED, newState)
                        .setValue(SurgeryChamberBlockBottom.SURGERY_DONE, !newState && bottomState.getValue(SurgeryChamberBlockBottom.SURGERY_DONE));
                if (newState) {
                    newBottomState = newBottomState.setValue(SurgeryChamberBlockBottom.SURGERY_DONE, false);
                }
                level.setBlock(bottomPos, newBottomState, 3);
            }
            level.playSound(
                    null,
                    pos,
                    newState ? net.minecraft.sounds.SoundEvents.IRON_DOOR_OPEN
                            : net.minecraft.sounds.SoundEvents.IRON_DOOR_CLOSE,
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f
            );
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }


    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of();
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            boolean drop = !player.getAbilities().instabuild;

            BlockPos bottomPos = pos.below();
            BlockState bottomState = level.getBlockState(bottomPos);

            if (bottomState.is(ModBlocks.SURGERY_CHAMBER_BOTTOM.get())) {
                level.destroyBlock(bottomPos, drop, player);
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockPos bottomPos = pos.below();
            BlockState bottomState = level.getBlockState(bottomPos);

            if (bottomState.is(ModBlocks.SURGERY_CHAMBER_BOTTOM.get())) {
                level.destroyBlock(bottomPos, false);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    public static boolean hasRoboSurgeonAbove(Level level, BlockPos pos) {
        return level.getBlockState(pos.above()).is(ModBlocks.ROBOSURGEON.get());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        if (!level.isClientSide) {
            boolean connected = hasRoboSurgeonAbove(level, pos);
            if (connected != state.getValue(CONNECTED)) {
                level.setBlock(pos, state.setValue(CONNECTED, connected), 3);
            }
        }
    }
}
