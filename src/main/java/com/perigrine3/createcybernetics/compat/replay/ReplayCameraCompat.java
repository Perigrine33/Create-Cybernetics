package com.perigrine3.createcybernetics.compat.replay;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.world.entity.player.Player;

public final class ReplayCameraCompat {

    private ReplayCameraCompat() {}

    private static final String REFORGEDPLAY_CAMERA_CLASS = "com.replaymod.replay.camera.CameraEntity";

    public static boolean isReplayCameraPlayer(Player player) {
        if (player == null) {
            return false;
        }

        Class<?> cls = player.getClass();
        while (cls != null) {
            if (REFORGEDPLAY_CAMERA_CLASS.equals(cls.getName())) {
                return true;
            }
            cls = cls.getSuperclass();
        }

        return false;
    }

    public static boolean hasEyesForVision(Player player, PlayerCyberwareData data) {
        if (data != null && data.hasAnyTagged(ModTags.Items.EYE_ITEMS, CyberwareSlot.EYES)) {
            return true;
        }

        return isReplayCameraPlayer(player);
    }
}