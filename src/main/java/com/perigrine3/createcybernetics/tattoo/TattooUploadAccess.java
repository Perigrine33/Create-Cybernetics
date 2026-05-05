package com.perigrine3.createcybernetics.tattoo;

import com.perigrine3.createcybernetics.ConfigValues;
import net.minecraft.server.level.ServerPlayer;

public final class TattooUploadAccess {
    private static final int OP_UPLOAD_PERMISSION_LEVEL = 2;

    private TattooUploadAccess() {
    }

    public static boolean canUpload(ServerPlayer player) {
        return switch (ConfigValues.TATTOO_UPLOAD_MODE) {
            case SERVER_FILES_ONLY -> false;
            case OP_ONLY_AUTO_APPROVE -> player.hasPermissions(OP_UPLOAD_PERMISSION_LEVEL);
            case ANY_PLAYER_PENDING_APPROVAL, ANY_PLAYER_AUTO_APPROVE -> true;
        };
    }

    public static boolean autoApproves(ServerPlayer player) {
        return switch (ConfigValues.TATTOO_UPLOAD_MODE) {
            case SERVER_FILES_ONLY -> false;
            case OP_ONLY_AUTO_APPROVE -> player.hasPermissions(OP_UPLOAD_PERMISSION_LEVEL);
            case ANY_PLAYER_PENDING_APPROVAL -> false;
            case ANY_PLAYER_AUTO_APPROVE -> true;
        };
    }

    public static boolean requiresApproval(ServerPlayer player) {
        return canUpload(player) && !autoApproves(player);
    }
}