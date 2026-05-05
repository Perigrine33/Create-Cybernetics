package com.perigrine3.createcybernetics.tattoo;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class TattooAutoReloadService {
    private static Map<String, FileState> lastSnapshot = Map.of();

    private TattooAutoReloadService() {
    }

    public static void initialize() {
        TattooDirectories.ensureCreated();
        lastSnapshot = snapshot();
    }

    public static void poll(MinecraftServer server) {
        if (server == null) {
            return;
        }

        TattooDirectories.ensureCreated();

        Map<String, FileState> current = snapshot();

        if (!current.equals(lastSnapshot)) {
            lastSnapshot = current;

            ServerTattooRegistry.reload();
            ServerTattooSync.sendToAll(server);

            CreateCybernetics.LOGGER.info("Tattoo folders changed; reloaded and synced tattoo registry.");
        }
    }

    private static Map<String, FileState> snapshot() {
        Map<String, FileState> result = new HashMap<>();

        scanFolder("approved", TattooDirectories.approved(), result);
        scanFolder("pending", TattooDirectories.pending(), result);

        return result;
    }

    private static void scanFolder(String prefix, Path folder, Map<String, FileState> result) {
        if (!Files.isDirectory(folder)) {
            return;
        }

        try (var stream = Files.walk(folder, 1)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".png"))
                    .forEach(path -> addFile(prefix, path, result));
        } catch (Exception ex) {
            CreateCybernetics.LOGGER.warn("Failed to scan tattoo folder '{}'", folder, ex);
        }
    }

    private static void addFile(String prefix, Path path, Map<String, FileState> result) {
        try {
            String key = prefix + "/" + path.getFileName();
            result.put(key, new FileState(
                    Files.size(path),
                    Files.getLastModifiedTime(path).toMillis()
            ));
        } catch (Exception ex) {
            CreateCybernetics.LOGGER.warn("Failed to inspect tattoo file '{}'", path, ex);
        }
    }

    private record FileState(long size, long modifiedTime) {
    }
}