package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.block.SurgeryTableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID)
public class SurgeryTableSleepEvents {

    @SubscribeEvent
    public static void onCanPlayerSleep(CanPlayerSleepEvent event) {
        if (event.getProblem() != Player.BedSleepingProblem.NOT_POSSIBLE_NOW) {
            return;
        }

        BlockPos pos = event.getPos();
        BlockState state = event.getEntity().level().getBlockState(pos);

        if (!isSurgeryTableHead(state)) {
            return;
        }

        event.setProblem(null);
    }

    @SubscribeEvent
    public static void onCanContinueSleeping(CanContinueSleepingEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (player.getSleepingPos().isEmpty()) {
            return;
        }

        BlockPos pos = player.getSleepingPos().get();
        BlockState state = player.level().getBlockState(pos);

        if (!isSurgeryTableHead(state)) {
            return;
        }

        event.setContinueSleeping(true);
    }

    private static boolean isSurgeryTableHead(BlockState state) {
        if (!state.is(ModBlocks.SURGERY_TABLE.get())) {
            return false;
        }

        if (!state.hasProperty(SurgeryTableBlock.PART)) {
            return false;
        }

        return state.getValue(SurgeryTableBlock.PART) == BedPart.HEAD;
    }
}