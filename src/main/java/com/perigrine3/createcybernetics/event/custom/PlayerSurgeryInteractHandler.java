package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.block.entity.SurgeryTableBlockEntity;
import com.perigrine3.createcybernetics.common.surgery.PlayerSurgeryMinigameManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID)
public final class PlayerSurgeryInteractHandler {

    private PlayerSurgeryInteractHandler() {
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer surgeon)) {
            return;
        }

        if (!surgeon.isShiftKeyDown()) {
            return;
        }

        Entity target = event.getTarget();
        if (!(target instanceof ServerPlayer patient)) {
            return;
        }

        if (surgeon == patient) {
            return;
        }

        if (!patient.isSleeping() || patient.getSleepingPos().isEmpty()) {
            return;
        }

        BlockEntity blockEntity = patient.level().getBlockEntity(patient.getSleepingPos().get());
        if (!(blockEntity instanceof SurgeryTableBlockEntity table)) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.CONSUME);

        if (PlayerSurgeryMinigameManager.hasActiveSession(surgeon)
                || PlayerSurgeryMinigameManager.hasActiveSession(patient)) {
            return;
        }

        PlayerSurgeryMinigameManager.startCountdown(surgeon, patient, table);
    }
}