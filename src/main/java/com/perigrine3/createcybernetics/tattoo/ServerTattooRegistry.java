package com.perigrine3.createcybernetics.tattoo;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ServerTattooRegistry {
    private static final Map<ResourceLocation, TattooEntry> APPROVED = new LinkedHashMap<>();
    private static final Map<ResourceLocation, TattooEntry> PENDING = new LinkedHashMap<>();

    private ServerTattooRegistry() {
    }

    public static void reload() {
        TattooDirectories.ensureCreated();

        APPROVED.clear();
        PENDING.clear();

        scanFolder(TattooDirectories.approved(), TattooStatus.APPROVED, APPROVED);
        scanFolder(TattooDirectories.pending(), TattooStatus.PENDING, PENDING);

        CreateCybernetics.LOGGER.info(
                "Loaded {} approved tattoos and {} pending tattoos",
                APPROVED.size(),
                PENDING.size()
        );
    }

    public static List<TattooEntry> approvedTattoos() {
        return APPROVED.values()
                .stream()
                .sorted(Comparator.comparing(TattooEntry::displayName))
                .toList();
    }

    public static List<TattooEntry> pendingTattoos() {
        return PENDING.values()
                .stream()
                .sorted(Comparator.comparing(TattooEntry::displayName))
                .toList();
    }

    public static TattooEntry getApproved(ResourceLocation id) {
        return APPROVED.get(id);
    }

    public static TattooEntry getPending(ResourceLocation id) {
        return PENDING.get(id);
    }

    private static void scanFolder(Path folder, TattooStatus status, Map<ResourceLocation, TattooEntry> target) {
        if (!Files.isDirectory(folder)) {
            return;
        }

        List<Path> files = new ArrayList<>();

        try (var stream = Files.walk(folder, 1)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".png"))
                    .forEach(files::add);
        } catch (IOException ex) {
            CreateCybernetics.LOGGER.warn("Failed to scan tattoo folder '{}'", folder, ex);
            return;
        }

        files.sort(Comparator.comparing(path -> path.getFileName().toString()));

        for (Path path : files) {
            loadTattooFile(path, status, target);
        }
    }

    private static void loadTattooFile(Path path, TattooStatus status, Map<ResourceLocation, TattooEntry> target) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            TattooValidationResult validation = TattooPngValidator.validate(bytes);

            if (!validation.success()) {
                CreateCybernetics.LOGGER.warn("Skipping tattoo '{}': {}", path.getFileName(), validation.message());
                return;
            }

            String fileName = path.getFileName().toString();
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    CreateCybernetics.MODID,
                    TattooFileNames.idPathFromFileName(fileName)
            );

            TattooEntry entry = new TattooEntry(
                    id,
                    TattooFileNames.displayNameFromFileName(fileName),
                    fileName,
                    validation.sha256(),
                    bytes.length,
                    validation.width(),
                    validation.height(),
                    path,
                    status
            );

            if (target.containsKey(id)) {
                CreateCybernetics.LOGGER.warn("Skipping duplicate tattoo id '{}' from file '{}'", id, fileName);
                return;
            }

            target.put(id, entry);
        } catch (Exception ex) {
            CreateCybernetics.LOGGER.warn("Failed to load tattoo file '{}'", path, ex);
        }
    }
}