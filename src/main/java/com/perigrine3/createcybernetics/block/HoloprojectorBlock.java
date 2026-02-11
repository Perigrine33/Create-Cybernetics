package com.perigrine3.createcybernetics.block;

import com.mojang.serialization.MapCodec;
import com.perigrine3.createcybernetics.block.entity.HoloprojectorBlockEntity;
import com.perigrine3.createcybernetics.item.generic.HoloProjectionChipItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class HoloprojectorBlock extends BaseEntityBlock {

    public static final MapCodec<HoloprojectorBlock> CODEC = simpleCodec(HoloprojectorBlock::new);
    private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 1.5D, 12.0D);

    public HoloprojectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof HoloprojectorBlockEntity holo)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        final boolean shift = player.isShiftKeyDown();

        if (shift) {
            holo.clearProjection();
            return ItemInteractionResult.CONSUME;
        }

        if (stack.isEmpty()) {
            if (player instanceof ServerPlayer sp) {
                holo.setProjectedPlayerFrom(sp);
                return ItemInteractionResult.CONSUME;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (HoloProjectionChipItem.hasEntityData(stack)) {
            CompoundTag chip = HoloProjectionChipItem.getChipTag(stack);

            String typeId = chip.getString(HoloProjectionChipItem.TAG_ENTITY_TYPE);
            String name = chip.getString(HoloProjectionChipItem.TAG_ENTITY_NAME);

            CompoundTag entityNbt = chip.contains(HoloProjectionChipItem.TAG_ENTITY_NBT, Tag.TAG_COMPOUND)
                    ? chip.getCompound(HoloProjectionChipItem.TAG_ENTITY_NBT)
                    : new CompoundTag();

            holo.setProjectedEntity(typeId, entityNbt, name);
            return ItemInteractionResult.CONSUME;
        }

        ItemStack one = stack.copy();
        one.setCount(1);
        holo.setProjectedItem(one);
        return ItemInteractionResult.CONSUME;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HoloprojectorBlockEntity(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
}
