package com.perigrine3.createcybernetics.network.handler;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.network.payload.OpenSpinalInjectorPayload;
import com.perigrine3.createcybernetics.screen.custom.SpinalInjectorMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class OpenSpinalInjectorHandler {
    private OpenSpinalInjectorHandler() {}

    private record InjectorRef(CyberwareSlot slot, int index) {}

    public static void handle(OpenSpinalInjectorPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sp)) return;

            if (!sp.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            InjectorRef ref = findInstalledInjector(data);
            if (ref == null) return;

            sp.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal("Spinal Injector");
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
                    return new SpinalInjectorMenu(id, inv, ref.slot(), ref.index());
                }
            }, buf -> {
                buf.writeUtf(ref.slot().name());
                buf.writeVarInt(ref.index());
            });
        });
    }

    private static InjectorRef findInstalledInjector(PlayerCyberwareData data) {
        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            var arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                var cw = arr[i];
                if (cw == null) continue;

                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;

                if (st.getItem() == ModItems.BONEUPGRADES_SPINALINJECTOR.get()) {
                    return new InjectorRef(slot, i);
                }
            }
        }
        return null;
    }
}
