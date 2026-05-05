package com.perigrine3.createcybernetics.tattoo;

import com.perigrine3.createcybernetics.ConfigValues;
import com.perigrine3.createcybernetics.network.payload.TattooAccessSyncS2CPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class TattooAccessSync {
    private static final int ADMIN_PERMISSION_LEVEL = 2;

    private TattooAccessSync() {
    }

    public static void sendTo(ServerPlayer player) {
        if (player == null) {
            return;
        }

        boolean admin = player.hasPermissions(ADMIN_PERMISSION_LEVEL);

        boolean canOpenBrowser = switch (ConfigValues.TATTOO_UPLOAD_MODE) {
            case ANY_PLAYER_AUTO_APPROVE, ANY_PLAYER_PENDING_APPROVAL -> true;
            case OP_ONLY_AUTO_APPROVE, SERVER_FILES_ONLY -> admin;
        };

        boolean canUpload = switch (ConfigValues.TATTOO_UPLOAD_MODE) {
            case ANY_PLAYER_AUTO_APPROVE, ANY_PLAYER_PENDING_APPROVAL -> true;
            case OP_ONLY_AUTO_APPROVE -> admin;
            case SERVER_FILES_ONLY -> false;
        };

        boolean canModerate = admin;

        PacketDistributor.sendToPlayer(player, new TattooAccessSyncS2CPayload(
                ConfigValues.TATTOO_UPLOAD_MODE,
                canOpenBrowser,
                canUpload,
                canModerate
        ));
    }
}