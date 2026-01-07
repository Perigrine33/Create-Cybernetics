package com.perigrine3.createcybernetics.network.handler;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.network.payload.SetChipwareShardPayload;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class SetChipwareShardHandler {
    private SetChipwareShardHandler() {}

    public static void handle(SetChipwareShardPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sp)) return;

            int slot = msg.slot();
            if (slot < 0 || slot >= PlayerCyberwareData.CHIPWARE_SLOT_COUNT) return;

            if (!sp.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
            if (data == null) return;
            if (!data.hasSpecificItem(ModItems.BRAINUPGRADES_CHIPWARESLOTS.get(), CyberwareSlot.BRAIN)) return;

            ItemStack in = msg.stack();
            if (in == null || in.isEmpty()) {
                data.setChipwareStack(slot, ItemStack.EMPTY);
            } else {
                if (!in.is(ModTags.Items.DATA_SHARDS)) return;
                data.setChipwareStack(slot, in.copyWithCount(1));
            }

            data.setDirty();
            sp.syncData(ModAttachments.CYBERWARE);
        });
    }
}
