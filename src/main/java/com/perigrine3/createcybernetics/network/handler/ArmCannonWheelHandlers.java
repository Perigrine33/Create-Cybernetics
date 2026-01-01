package com.perigrine3.createcybernetics.network.handler;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.cyberware.ArmCannonItem;
import com.perigrine3.createcybernetics.network.payload.ArmCannonWheelPayloads;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ArmCannonWheelHandlers {
    private ArmCannonWheelHandlers() {}

    public static void handleOpen(ArmCannonWheelPayloads.RequestOpenArmCannonWheelPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sp)) return;

            if (!sp.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            ItemStack cannonStack = findInstalledArmCannonStack(data);
            if (cannonStack.isEmpty()) return;

            int segments = ArmCannonItem.SLOT_COUNT;
            int selected = data.getArmCannonSelected();

            PacketDistributor.sendToPlayer(
                    sp,
                    new ArmCannonWheelPayloads.OpenArmCannonWheelClientPayload(segments, selected)
            );
        });
    }

    public static void handleSelect(ArmCannonWheelPayloads.SelectArmCannonAmmoSlotPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sp)) return;

            if (!sp.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            ItemStack cannonStack = findInstalledArmCannonStack(data);
            if (cannonStack.isEmpty()) return;

            int idx = Mth.clamp(payload.slotIndex(), 0, ArmCannonItem.SLOT_COUNT - 1);

            SimpleContainer tmp = new SimpleContainer(ArmCannonItem.SLOT_COUNT);
            ArmCannonItem.loadFromInstalledStack(cannonStack, sp.level().registryAccess(), tmp);

            ItemStack chosen = tmp.getItem(idx);
            if (chosen == null || chosen.isEmpty() || !ArmCannonItem.isValidStoredItem(chosen)) return;

            data.setArmCannonSelected(idx);
            data.setDirty();
            sp.syncData(ModAttachments.CYBERWARE);
        });
    }

    private static ItemStack findInstalledArmCannonStack(PlayerCyberwareData data) {
        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            var arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                var cw = arr[i];
                if (cw == null) continue;

                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;

                if (st.getItem() == ModItems.ARMUPGRADES_ARMCANNON.get()) {
                    return st;
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
