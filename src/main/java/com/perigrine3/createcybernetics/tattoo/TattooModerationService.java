package com.perigrine3.createcybernetics.tattoo;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Files;
import java.nio.file.Path;

public final class TattooModerationService {
    private TattooModerationService() {
    }

    public static TattooUploadResult approve(ResourceLocation id) {
        TattooEntry pending = ServerTattooRegistry.getPending(id);
        if (pending == null) {
            return TattooUploadResult.failure("No pending tattoo found with id: " + id);
        }

        Path source = pending.path();
        Path target = TattooFileNames.uniquePath(TattooDirectories.approved(), pending.fileName());

        try {
            TattooDirectories.ensureCreated();
            Files.move(source, target);

            ServerTattooRegistry.reload();

            ResourceLocation approvedId = ResourceLocation.fromNamespaceAndPath(
                    CreateCybernetics.MODID,
                    TattooFileNames.idPathFromFileName(target.getFileName().toString())
            );

            return TattooUploadResult.success("Tattoo approved.", approvedId, TattooStatus.APPROVED);
        } catch (Exception ex) {
            CreateCybernetics.LOGGER.warn("Failed to approve tattoo '{}'", id, ex);
            return TattooUploadResult.failure("Failed to approve tattoo: " + id);
        }
    }

    public static TattooUploadResult reject(ResourceLocation id) {
        TattooEntry pending = ServerTattooRegistry.getPending(id);
        if (pending == null) {
            return TattooUploadResult.failure("No pending tattoo found with id: " + id);
        }

        Path source = pending.path();
        Path target = TattooFileNames.uniquePath(TattooDirectories.rejected(), pending.fileName());

        try {
            TattooDirectories.ensureCreated();
            Files.move(source, target);

            ServerTattooRegistry.reload();

            return TattooUploadResult.success("Tattoo rejected.", id, TattooStatus.PENDING);
        } catch (Exception ex) {
            CreateCybernetics.LOGGER.warn("Failed to reject tattoo '{}'", id, ex);
            return TattooUploadResult.failure("Failed to reject tattoo: " + id);
        }
    }

    public static TattooUploadResult removeApproved(ResourceLocation id) {
        TattooEntry approved = ServerTattooRegistry.getApproved(id);
        if (approved == null) {
            return TattooUploadResult.failure("No approved tattoo found with id: " + id);
        }

        Path source = approved.path();
        Path target = TattooFileNames.uniquePath(TattooDirectories.rejected(), approved.fileName());

        try {
            TattooDirectories.ensureCreated();
            Files.move(source, target);

            ServerTattooRegistry.reload();

            return TattooUploadResult.success("Approved tattoo removed.", id, TattooStatus.APPROVED);
        } catch (Exception ex) {
            CreateCybernetics.LOGGER.warn("Failed to remove approved tattoo '{}'", id, ex);
            return TattooUploadResult.failure("Failed to remove approved tattoo: " + id);
        }
    }

    public static TattooUploadResult deletePending(ResourceLocation id) {
        TattooEntry pending = ServerTattooRegistry.getPending(id);
        if (pending == null) {
            return TattooUploadResult.failure("No pending tattoo found with id: " + id);
        }

        try {
            Files.deleteIfExists(pending.path());
            ServerTattooRegistry.reload();
            return TattooUploadResult.success("Pending tattoo deleted.", id, TattooStatus.PENDING);
        } catch (Exception ex) {
            CreateCybernetics.LOGGER.warn("Failed to delete pending tattoo '{}'", id, ex);
            return TattooUploadResult.failure("Failed to delete pending tattoo: " + id);
        }
    }
}