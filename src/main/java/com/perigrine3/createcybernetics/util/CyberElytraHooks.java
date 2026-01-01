package com.perigrine3.createcybernetics.util;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class CyberElytraHooks {
    private CyberElytraHooks() {}

    public static boolean isDeployableElytraEnabled(Player player) {
        if (player == null) return false;
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        InstalledCyberware[] arr = data.getAll().get(CyberwareSlot.BONE);
        if (arr == null) return false;

        for (int i = 0; i < arr.length; i++) {
            InstalledCyberware cw = arr[i];
            if (cw == null) continue;

            ItemStack st = cw.getItem();
            if (st == null || st.isEmpty()) continue;

            if (ModItems.BONEUPGRADES_ELYTRA != null) {
                if (st.is(ModItems.BONEUPGRADES_ELYTRA.get())) {
                    boolean wheelToggleable = st.is(com.perigrine3.createcybernetics.util.ModTags.Items.TOGGLEABLE_CYBERWARE);
                    return !wheelToggleable || data.isEnabled(CyberwareSlot.BONE, i);
                }
            }
        }

        return false;
    }
}
