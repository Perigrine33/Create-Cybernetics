package com.perigrine3.createcybernetics.block;

import com.perigrine3.createcybernetics.common.energy.EnergyController;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ChargingBlock extends Block {

    public ChargingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);

        if (level.isClientSide) return;

        if (entity instanceof Player player) {
            EnergyController.markOnChargingBlock(player);
        }
    }
}