package com.perigrine3.createcybernetics.tattoo.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ClientTattooUploadDirectories {
    private ClientTattooUploadDirectories() {
    }

    public static Path uploadFolder() {
        return FMLPaths.CONFIGDIR.get()
                .resolve(CreateCybernetics.MODID)
                .resolve("tattoos")
                .resolve("upload");
    }

    public static void ensureCreated() {
        try {
            Files.createDirectories(uploadFolder());
        } catch (IOException ex) {
            CreateCybernetics.LOGGER.error("Failed to create client tattoo upload folder", ex);
        }
    }
}