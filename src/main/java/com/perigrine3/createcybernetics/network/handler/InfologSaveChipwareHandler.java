package com.perigrine3.createcybernetics.network.handler;

import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.generic.InfologTextData;
import com.perigrine3.createcybernetics.network.payload.InfologSaveChipwarePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class InfologSaveChipwareHandler {
    private InfologSaveChipwareHandler() {}

    public static void handle(InfologSaveChipwarePayload payload, ServerPlayer sp) {
        if (!sp.hasData(ModAttachments.CYBERWARE)) return;

        PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        int slot = payload.chipwareSlot();
        if (slot < 0 || slot >= PlayerCyberwareData.CHIPWARE_SLOT_COUNT) return;

        ItemStack current = data.getChipwareStack(slot);
        if (current.isEmpty()) return;
        if (!current.is(ModItems.DATA_SHARD_INFOLOG.get())) return;

        boolean alreadyLocked = InfologTextData.isLocked(current);
        if (alreadyLocked && !payload.locked()) return;

        String text = payload.text();
        if (text == null) text = "";
        if (text.length() > 32000) text = text.substring(0, 32000);

        ItemStack updated = current.copy();
        updated.setCount(1);

        if (!alreadyLocked) {
            InfologTextData.setText(updated, text);
        }
        InfologTextData.setLocked(updated, payload.locked() || alreadyLocked);

        data.setChipwareStack(slot, updated);
        data.setDirty();
        sp.syncData(ModAttachments.CYBERWARE);
    }
}
