package com.perigrine3.createcybernetics.tattoo;

import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;

public record TattooEntry(
        ResourceLocation id,
        String displayName,
        String fileName,
        String sha256,
        long sizeBytes,
        int width,
        int height,
        Path path,
        TattooStatus status
) {
}