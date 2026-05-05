package com.perigrine3.createcybernetics.tattoo;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Files;
import java.nio.file.Path;

public final class TattooUploadService {
    private TattooUploadService() {
    }

    public static TattooUploadResult upload(ServerPlayer player, String originalFileName, byte[] bytes) {
        if (!TattooUploadAccess.canUpload(player)) {
            return TattooUploadResult.failure("Tattoo uploads are disabled on this server.");
        }

        TattooValidationResult validation = TattooPngValidator.validate(bytes);
        if (!validation.success()) {
            return TattooUploadResult.failure(validation.message());
        }

        TattooDirectories.ensureCreated();

        boolean autoApprove = TattooUploadAccess.autoApproves(player);
        TattooStatus status = autoApprove ? TattooStatus.APPROVED : TattooStatus.PENDING;
        Path folder = autoApprove ? TattooDirectories.approved() : TattooDirectories.pending();

        String safeFileName = TattooFileNames.sanitizePngFileName(originalFileName);
        Path target = TattooFileNames.uniquePath(folder, safeFileName);

        try {
            Files.write(target, bytes);

            ResourceLocation tattooId = ResourceLocation.fromNamespaceAndPath(
                    CreateCybernetics.MODID,
                    TattooFileNames.idPathFromFileName(target.getFileName().toString())
            );

            ServerTattooRegistry.reload();

            if (autoApprove) {
                ServerTattooSync.sendToAll(player.server);
            }

            if (autoApprove) {
                return TattooUploadResult.success("Tattoo uploaded and approved.", tattooId, status);
            }

            return TattooUploadResult.success("Tattoo uploaded and is pending admin approval.", tattooId, status);
        } catch (Exception ex) {
            CreateCybernetics.LOGGER.warn(
                    "Failed to save tattoo upload '{}' from '{}'",
                    originalFileName,
                    player.getGameProfile().getName(),
                    ex
            );

            return TattooUploadResult.failure("Tattoo upload failed on the server.");
        }
    }
}