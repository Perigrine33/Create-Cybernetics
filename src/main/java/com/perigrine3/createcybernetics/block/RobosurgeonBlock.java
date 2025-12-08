package com.perigrine3.createcybernetics.common.block;

import com.mojang.serialization.MapCodec;
import com.perigrine3.createcybernetics.block.entity.RobosurgeonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class RobosurgeonBlock extends BaseEntityBlock {
    public static final MapCodec<RobosurgeonBlock> CODEC = simpleCodec(RobosurgeonBlock::new);

    public RobosurgeonBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }


    /*   BLOCK ENTITY STUFF   */

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RobosurgeonBlockEntity(blockPos, blockState);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof RobosurgeonBlockEntity robosurgeonBlockEntity) {
                robosurgeonBlockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof RobosurgeonBlockEntity robosurgeonBlockEntity)
            if (!level.isClientSide()) {
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(robosurgeonBlockEntity, Component.literal("_" + player.getName().getString() + ".exe")), pos);
                return ItemInteractionResult.SUCCESS;
            }
        return ItemInteractionResult.SUCCESS;
    }
}
