package com.perigrine3.createcybernetics.tattoo.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.network.payload.TattooUploadC2SPayload;
import com.perigrine3.createcybernetics.tattoo.TattooPngValidator;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class ClientTattooUploadManager {
    private ClientTattooUploadManager() {
    }

    public static List<ClientTattooUploadEntry> listUploadablePngs() {
        ClientTattooUploadDirectories.ensureCreated();

        Path folder = ClientTattooUploadDirectories.uploadFolder();

        try (var stream = Files.walk(folder, 1)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(ClientTattooUploadManager::isPng)
                    .map(ClientTattooUploadManager::toEntry)
                    .filter(entry -> entry != null)
                    .sorted(Comparator.comparing(ClientTattooUploadEntry::fileName, String.CASE_INSENSITIVE_ORDER))
                    .toList();
        } catch (IOException ex) {
            CreateCybernetics.LOGGER.warn("Failed to list client tattoo upload folder '{}'", folder, ex);
            return List.of();
        }
    }

    public static boolean upload(ClientTattooUploadEntry entry) {
        if (entry == null) {
            sendClientMessage(Component.literal("No tattoo file selected."));
            return false;
        }

        return upload(entry.path());
    }

    public static boolean upload(Path path) {
        if (path == null) {
            sendClientMessage(Component.literal("No tattoo file selected."));
            return false;
        }

        ClientTattooUploadDirectories.ensureCreated();

        if (!Files.isRegularFile(path)) {
            sendClientMessage(Component.literal("Tattoo file does not exist."));
            return false;
        }

        if (!isPng(path)) {
            sendClientMessage(Component.literal("Tattoo file must be a PNG."));
            return false;
        }

        try {
            long size = Files.size(path);
            if (size <= 0L) {
                sendClientMessage(Component.literal("Tattoo file is empty."));
                return false;
            }

            if (size > TattooPngValidator.MAX_FILE_SIZE_BYTES) {
                sendClientMessage(Component.literal("Tattoo file is too large. Maximum size is 256 KB."));
                return false;
            }

            byte[] bytes = Files.readAllBytes(path);
            String fileName = path.getFileName().toString();

            PacketDistributor.sendToServer(new TattooUploadC2SPayload(fileName, bytes));

            sendClientMessage(Component.literal("Uploading tattoo: " + fileName));
            return true;
        } catch (Exception ex) {
            CreateCybernetics.LOGGER.warn("Failed to upload client tattoo file '{}'", path, ex);
            sendClientMessage(Component.literal("Failed to read tattoo file."));
            return false;
        }
    }

    private static ClientTattooUploadEntry toEntry(Path path) {
        try {
            return new ClientTattooUploadEntry(
                    path.getFileName().toString(),
                    Files.size(path),
                    path
            );
        } catch (IOException ex) {
            CreateCybernetics.LOGGER.warn("Failed to inspect client tattoo file '{}'", path, ex);
            return null;
        }
    }

    private static boolean isPng(Path path) {
        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.endsWith(".png");
    }

    private static void sendClientMessage(Component component) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player != null) {
            minecraft.player.sendSystemMessage(component);
        }
    }
}