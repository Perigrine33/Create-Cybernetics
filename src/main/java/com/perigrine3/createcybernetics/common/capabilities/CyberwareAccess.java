package com.perigrine3.createcybernetics.common.capabilities;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareData;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.Map;

public final class CyberwareAccess {

    private CyberwareAccess() {}

    public static ICyberwareData getData(LivingEntity entity) {
        if (entity == null) return null;

        if (entity instanceof Player player) {
            return player.getData(ModAttachments.CYBERWARE);
        }

        return entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
    }

    public static Map<CyberwareSlot, InstalledCyberware[]> getAll(LivingEntity entity) {
        ICyberwareData data = getData(entity);
        return data != null ? data.getAll() : Collections.emptyMap();
    }

    public static boolean isEnabled(LivingEntity entity, CyberwareSlot slot, int index) {
        if (entity == null) return true;

        if (entity instanceof Player player) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            return data == null || data.isEnabled(slot, index);
        }

        EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        return data == null || data.isEnabled(slot, index);
    }
}