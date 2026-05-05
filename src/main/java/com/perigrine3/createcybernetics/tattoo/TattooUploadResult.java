package com.perigrine3.createcybernetics.tattoo;

import net.minecraft.resources.ResourceLocation;

public record TattooUploadResult(
        boolean success,
        String message,
        ResourceLocation tattooId,
        TattooStatus status
) {
    public static TattooUploadResult success(String message, ResourceLocation tattooId, TattooStatus status) {
        return new TattooUploadResult(true, message, tattooId, status);
    }

    public static TattooUploadResult failure(String message) {
        return new TattooUploadResult(false, message, null, null);
    }
}