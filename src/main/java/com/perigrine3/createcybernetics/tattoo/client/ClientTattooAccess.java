package com.perigrine3.createcybernetics.tattoo.client;

import com.perigrine3.createcybernetics.ConfigValues;

public final class ClientTattooAccess {
    private static ConfigValues.TattooUploadMode uploadMode = ConfigValues.TattooUploadMode.SERVER_FILES_ONLY;
    private static boolean canOpenBrowser;
    private static boolean canUpload;
    private static boolean canModerate;

    private ClientTattooAccess() {
    }

    public static void update(ConfigValues.TattooUploadMode mode, boolean openBrowser, boolean upload, boolean moderate) {
        uploadMode = mode == null ? ConfigValues.TattooUploadMode.SERVER_FILES_ONLY : mode;
        canOpenBrowser = openBrowser;
        canUpload = upload;
        canModerate = moderate;
    }

    public static ConfigValues.TattooUploadMode uploadMode() {
        return uploadMode;
    }

    public static boolean canOpenBrowser() {
        return canOpenBrowser;
    }

    public static boolean canUpload() {
        return canUpload;
    }

    public static boolean canModerate() {
        return canModerate;
    }

    public static void clear() {
        uploadMode = ConfigValues.TattooUploadMode.SERVER_FILES_ONLY;
        canOpenBrowser = false;
        canUpload = false;
        canModerate = false;
    }
}