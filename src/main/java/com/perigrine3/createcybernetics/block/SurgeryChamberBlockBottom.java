package com.perigrine3.createcybernetics.block;

import com.mojang.serialization.MapCodec;
import com.perigrine3.createcybernetics.block.entity.RobosurgeonBlockEntity;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.surgery.SurgeryController;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SurgeryChamberBlockBottom extends HorizontalDirectionalBlock {
    public static final BooleanProperty OPENED = BooleanProperty.create("opened");
    public static final BooleanProperty SLAVE = BooleanProperty.create("slave");
    public static final BooleanProperty SURGERY_DONE = BooleanProperty.create("surgery_done");
    public static final MapCodec<SurgeryChamberBlockBottom> CODEC = simpleCodec(SurgeryChamberBlockBottom::new);
    private static final VoxelShape BACKWALL     = Block.box(14, 0, 0, 16, 16, 16);
    private static final VoxelShape WESTWALL     = Block.box(0, 0, 14, 16, 16, 16);
    private static final VoxelShape EASTWALL     = Block.box(0, 0, 0, 16, 16, 2);
    private static final VoxelShape BOTTOMWALL   = Block.box(0, 0, 0, 16, 2, 16);
    private static final VoxelShape DOOR_CLOSED  = Block.box(1, 2, 2, 2, 16, 14);

    private static final VoxelShape SHAPE_OPEN = Shapes.or(BACKWALL, WESTWALL, EASTWALL, BOTTOMWALL);
    private static final VoxelShape SHAPE_CLOSED = Shapes.or(BACKWALL, WESTWALL, EASTWALL, BOTTOMWALL, DOOR_CLOSED);

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


    public SurgeryChamberBlockBottom(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPENED, false)
                .setValue(SLAVE, false)
                .setValue(SURGERY_DONE, false));
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
        builder.add(FACING, OPENED, SLAVE, SURGERY_DONE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!level.getBlockState(pos.above()).canBeReplaced(context)) {
            return null;
        }
        BlockState topState = ModBlocks.SURGERY_CHAMBER_TOP.get()
                .defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(SurgeryChamberBlockTop.OPENED, false);
        level.setBlock(pos.above(), topState, 3);
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(OPENED, false)
                .setValue(SLAVE, false);
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

    //Thanks, TwistedGate, you're a lifesaver :D
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (brokenByCreativePlayer(builder)) {
            return List.of();
        }

        if (state.getValue(SLAVE)) {
            return List.of();
        }

        return List.of(new ItemStack(ModBlocks.SURGERY_CHAMBER_BOTTOM.get()));
    }


    private static boolean brokenByCreativePlayer(LootParams.Builder builder) {
        Entity e = builder.getOptionalParameter(LootContextParams.THIS_ENTITY);
        return e instanceof Player p && p.getAbilities().instabuild;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            // Remove top block if bottom is destroyed
            BlockPos topPos = pos.above();
            BlockState topState = level.getBlockState(topPos);
            if (topState.is(ModBlocks.SURGERY_CHAMBER_TOP.get())) {
                level.destroyBlock(topPos, false);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        BlockPos topPos = pos.above();
        BlockState topState = level.getBlockState(topPos);

        if (!topState.is(ModBlocks.SURGERY_CHAMBER_TOP.get())) return;

        boolean connected = topState.getValue(SurgeryChamberBlockTop.CONNECTED);
        boolean closed = !topState.getValue(SurgeryChamberBlockTop.OPENED);

        if (!connected || !closed || state.getValue(SURGERY_DONE)) return;
        BlockPos surgeonPos = topPos.above();
        if (!level.getBlockState(surgeonPos).is(ModBlocks.ROBOSURGEON.get())) return;
        if (!(level.getBlockEntity(surgeonPos) instanceof RobosurgeonBlockEntity surgeon)) return;

        // --- PERFORM SURGERY ---
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);

        SurgeryController.performSurgery(player, surgeon);

        level.setBlock(pos, state.setValue(SURGERY_DONE, true), 3);
    }
}
