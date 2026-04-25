package com.perigrine3.createcybernetics.util;

import com.perigrine3.createcybernetics.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public final class SurgerySleepHelper {

    private SurgerySleepHelper() {
    }

    public static boolean isSleepingOnSurgeryTable(LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }

        if (!player.isSleeping() || player.getSleepingPos().isEmpty()) {
            return false;
        }

        BlockPos sleepingPos = player.getSleepingPos().get();
        BlockState state = player.level().getBlockState(sleepingPos);

        return state.is(ModBlocks.SURGERY_TABLE.get());
    }
}