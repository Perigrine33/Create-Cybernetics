package com.perigrine3.createcybernetics.network.handler;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.network.payload.OpenArmCannonPayload;
import com.perigrine3.createcybernetics.screen.custom.ArmCannonMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class OpenArmCannonHandler {
    private OpenArmCannonHandler() {}

    private record CannonRef(CyberwareSlot slot, int index) {}

    public static void handle(OpenArmCannonPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sp)) return;

            PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
            CannonRef ref = findInstalledArmCannon(data);
            if (ref == null) return;

            sp.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("gui.armcannon.title");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
                    return new ArmCannonMenu(id, inv, ref.slot(), ref.index());
                }
            }, buf -> {
                buf.writeUtf(ref.slot().name());
                buf.writeVarInt(ref.index());
            });
        });
    }

    private static CannonRef findInstalledArmCannon(PlayerCyberwareData data) {
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
                    return new CannonRef(slot, i);
                }
            }
        }
        return null;
    }
}
