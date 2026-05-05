package com.perigrine3.createcybernetics.tattoo;

public record TattooValidationResult(
        boolean success,
        String message,
        int width,
        int height,
        String sha256
) {
    public static TattooValidationResult success(int width, int height, String sha256) {
        return new TattooValidationResult(true, "OK", width, height, sha256);
    }

    public static TattooValidationResult failure(String message) {
        return new TattooValidationResult(false, message, 0, 0, "");
    }
}