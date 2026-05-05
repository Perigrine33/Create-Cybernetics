package com.perigrine3.createcybernetics.tattoo;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TattooDirectories {
    private TattooDirectories() {
    }

    public static Path root() {
        return FMLPaths.CONFIGDIR.get()
                .resolve(CreateCybernetics.MODID)
                .resolve("tattoos");
    }

    public static Path approved() {
        return root().resolve("approved");
    }

    public static Path pending() {
        return root().resolve("pending");
    }

    public static Path rejected() {
        return root().resolve("rejected");
    }

    public static void ensureCreated() {
        try {
            Files.createDirectories(approved());
            Files.createDirectories(pending());
            Files.createDirectories(rejected());
        } catch (IOException ex) {

        }
    }
}