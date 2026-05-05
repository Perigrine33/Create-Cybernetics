package com.perigrine3.createcybernetics.tattoo.client;

import java.nio.file.Path;

public record ClientTattooUploadEntry(
        String fileName,
        long sizeBytes,
        Path path
) {
}