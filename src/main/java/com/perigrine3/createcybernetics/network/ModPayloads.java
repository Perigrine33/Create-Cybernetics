package com.perigrine3.createcybernetics.network;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.effect.*;
import com.perigrine3.createcybernetics.network.handler.*;
import com.perigrine3.createcybernetics.network.payload.*;
import com.perigrine3.createcybernetics.screen.custom.hud.CyberwareHudLayer;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModPayloads {
    private ModPayloads() {
    }

    public static void register(PayloadRegistrar r) {
        r.playToServer(
                SculkLungsEffect.SonicUseHeldPayload.TYPE,
                SculkLungsEffect.SonicUseHeldPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    if (ctx.player() instanceof net.minecraft.server.level.ServerPlayer sp) {
                        SculkLungsEffect.setUseHeld(sp, payload.held());
                    }
                })
        );

        r.playToServer(
                GuardianEyeEffect.GuardianEyeUseHeldPayload.TYPE,
                GuardianEyeEffect.GuardianEyeUseHeldPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    if (ctx.player() instanceof net.minecraft.server.level.ServerPlayer sp) {
                        GuardianEyeEffect.setUseHeld(sp, payload.held());
                    }
                })
        );

        r.playToServer(
                AerostasisGyrobladderEffect.GyroJumpHeldPayload.TYPE,
                AerostasisGyrobladderEffect.GyroJumpHeldPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    if (ctx.player() instanceof net.minecraft.server.level.ServerPlayer sp) {
                        AerostasisGyrobladderEffect.handleJumpHeldPayload(sp, payload.held());
                    }
                })
        );

        r.playToServer(
                NeuralContextualizerEffect.SwapHotbarPayload.TYPE,
                NeuralContextualizerEffect.SwapHotbarPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    if (ctx.player() instanceof net.minecraft.server.level.ServerPlayer sp) {
                        NeuralContextualizerEffect.handleSwapHotbarPayload(sp, payload.slot());
                    }
                })
        );

        r.playToClient(
                TargetingHighlightPayload.TYPE,
                TargetingHighlightPayload.STREAM_CODEC,
                TargetingHighlightPayload::handle
        );


        r.playToServer(
                com.perigrine3.createcybernetics.network.payload.OpenExpandedInventoryPayload.TYPE,
                com.perigrine3.createcybernetics.network.payload.OpenExpandedInventoryPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() ->
                        com.perigrine3.createcybernetics.network.payload.OpenExpandedInventoryPayload.handle(payload, ctx)
                )
        );

        r.playToServer(
                OpenSpinalInjectorPayload.TYPE,
                OpenSpinalInjectorPayload.STREAM_CODEC,
                OpenSpinalInjectorHandler::handle
        );

        r.playToServer(
                OpenArmCannonPayload.TYPE,
                OpenArmCannonPayload.STREAM_CODEC,
                OpenArmCannonHandler::handle
        );

        r.playToClient(
                EnergyHudSnapshotPayload.TYPE,
                EnergyHudSnapshotPayload.STREAM_CODEC,
                EnergyHudSnapshotPayload::handle
        );


        r.playToServer(
                ArmCannonWheelPayloads.RequestOpenArmCannonWheelPayload.TYPE,
                ArmCannonWheelPayloads.RequestOpenArmCannonWheelPayload.STREAM_CODEC,
                ArmCannonWheelHandlers::handleOpen
        );

        r.playToClient(
                ArmCannonWheelPayloads.OpenArmCannonWheelClientPayload.TYPE,
                ArmCannonWheelPayloads.OpenArmCannonWheelClientPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    ArmCannonWheelClientHandlers.handleOpen(payload, ctx);
                })
        );

        r.playToServer(
                ArmCannonWheelPayloads.SelectArmCannonAmmoSlotPayload.TYPE,
                ArmCannonWheelPayloads.SelectArmCannonAmmoSlotPayload.STREAM_CODEC,
                ArmCannonWheelHandlers::handleSelect
        );

        r.playToServer(
                ArmCannonFirePayload.TYPE,
                ArmCannonFirePayload.STREAM_CODEC,
                ArmCannonFireHandler::handle
        );

        r.playToServer(
                SetChipwareShardPayload.TYPE,
                SetChipwareShardPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    if (!(ctx.player() instanceof ServerPlayer sp)) return;

                    PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
                    if (data == null) return;
                    int slot = payload.slot();
                    ItemStack stack = payload.stack();
                    if (slot < 0 || slot >= PlayerCyberwareData.CHIPWARE_SLOT_COUNT) return;
                    data.setChipwareStack(slot, stack);

                    data.setDirty();
                    sp.syncData(ModAttachments.CYBERWARE);
                })
        );

        r.playToServer(
                OpenChipwareMiniPayload.TYPE,
                OpenChipwareMiniPayload.STREAM_CODEC,
                OpenChipwareMiniHandler::handle
        );

        r.playToClient(
                CerebralShutdownStatePayload.TYPE,
                CerebralShutdownStatePayload.STREAM_CODEC,
                CerebralShutdownStatePayload::handle
        );


        r.playToClient(
                CopernicusOxygenSyncPayload.TYPE,
                CopernicusOxygenSyncPayload.STREAM_CODEC,
                CopernicusOxygenSyncPayload::handle
        );

        r.playToServer(
                OpenHeatEnginePayload.TYPE,
                OpenHeatEnginePayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    if (!(ctx.player() instanceof ServerPlayer sp)) return;
                    OpenHeatEnginePayloadHandler.handle(payload, sp);
                })
        );


        /* ---------------- TOGGLE WHEEL PAYLOADS ---------------- */

        // Client asks server: "send me the enabled flags for installed toggleables"
        r.playToServer(
                CyberwareTogglePayloads.RequestToggleStatesPayload.TYPE,
                CyberwareTogglePayloads.RequestToggleStatesPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    if (!(ctx.player() instanceof net.minecraft.server.level.ServerPlayer sp)) return;
                    if (!sp.hasData(ModAttachments.CYBERWARE)) return;

                    PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
                    if (data == null) return;

                    for (var entry : data.getAll().entrySet()) {
                        CyberwareSlot slot = entry.getKey();
                        InstalledCyberware[] arr = entry.getValue();
                        if (arr == null) continue;

                        for (int i = 0; i < arr.length; i++) {
                            InstalledCyberware inst = arr[i];
                            if (inst == null) continue;

                            ItemStack stack = inst.getItem();
                            if (stack == null || stack.isEmpty()) continue;
                            if (!stack.is(ModTags.Items.TOGGLEABLE_CYBERWARE)) continue;

                            boolean enabled = data.isEnabled(slot, i);
                            PacketDistributor.sendToPlayer(sp, new CyberwareEnabledStatePayload(slot.name(), i, enabled));
                        }
                    }
                })
        );

        // Client clicked a segment: toggle that installed (slot,index) on server and sync back
        r.playToServer(
                CyberwareTogglePayloads.ToggleCyberwarePayload.TYPE,
                CyberwareTogglePayloads.ToggleCyberwarePayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    if (!(ctx.player() instanceof net.minecraft.server.level.ServerPlayer sp)) return;
                    if (!sp.hasData(ModAttachments.CYBERWARE)) return;

                    PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
                    if (data == null) return;

                    CyberwareSlot slot;
                    try {
                        slot = CyberwareSlot.valueOf(payload.slotName());
                    } catch (IllegalArgumentException ex) {
                        return;
                    }

                    int index = payload.index();
                    InstalledCyberware inst = data.get(slot, index);
                    if (inst == null) return;

                    ItemStack stack = inst.getItem();
                    if (stack == null || stack.isEmpty()) return;
                    if (!stack.is(ModTags.Items.TOGGLEABLE_CYBERWARE)) return;

                    boolean nowEnabled = data.toggleEnabled(slot, index);
                    PacketDistributor.sendToPlayer(sp, new CyberwareEnabledStatePayload(slot.name(), index, nowEnabled));
                })
        );

        r.playToClient(
                CyberwareEnabledStatePayload.TYPE,
                CyberwareEnabledStatePayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> CyberwareEnabledStatePayload.handle(payload, ctx))
        );
    }
}
