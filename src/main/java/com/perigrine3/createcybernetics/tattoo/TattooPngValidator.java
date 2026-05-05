package com.perigrine3.createcybernetics.tattoo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.util.HexFormat;

public final class TattooPngValidator {
    public static final int REQUIRED_WIDTH = 64;
    public static final int REQUIRED_HEIGHT = 64;
    public static final int MAX_FILE_SIZE_BYTES = 256 * 1024;

    private TattooPngValidator() {
    }

    public static TattooValidationResult validate(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return TattooValidationResult.failure("Tattoo file is empty.");
        }

        if (bytes.length > MAX_FILE_SIZE_BYTES) {
            return TattooValidationResult.failure("Tattoo file is too large. Maximum size is 256 KB.");
        }

        if (!hasPngSignature(bytes)) {
            return TattooValidationResult.failure("Tattoo file is not a valid PNG.");
        }

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null) {
                return TattooValidationResult.failure("Tattoo file could not be decoded as a PNG.");
            }

            int width = image.getWidth();
            int height = image.getHeight();

            if (width != REQUIRED_WIDTH || height != REQUIRED_HEIGHT) {
                return TattooValidationResult.failure("Tattoo overlay must be exactly 64x64 pixels.");
            }

            return TattooValidationResult.success(width, height, sha256(bytes));
        } catch (Exception ex) {
            return TattooValidationResult.failure("Tattoo file could not be validated.");
        }
    }

    private static boolean hasPngSignature(byte[] bytes) {
        return bytes.length >= 8
                && (bytes[0] & 0xFF) == 0x89
                && bytes[1] == 0x50
                && bytes[2] == 0x4E
                && bytes[3] == 0x47
                && bytes[4] == 0x0D
                && bytes[5] == 0x0A
                && bytes[6] == 0x1A
                && bytes[7] == 0x0A;
    }

    public static String sha256(byte[] bytes) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest(bytes));
    }
}