package com.perigrine3.createcybernetics.tattoo.client;

import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ClientTattooRegistry {
    private static final Map<ResourceLocation, ClientTattooEntry> APPROVED = new LinkedHashMap<>();

    private ClientTattooRegistry() {
    }

    public static void replaceApproved(List<ClientTattooEntry> entries) {
        APPROVED.clear();

        entries.stream()
                .sorted(Comparator.comparing(ClientTattooEntry::displayName, String.CASE_INSENSITIVE_ORDER))
                .forEach(entry -> APPROVED.put(entry.id(), entry));
    }

    public static List<ClientTattooEntry> approvedTattoos() {
        return APPROVED.values()
                .stream()
                .sorted(Comparator.comparing(ClientTattooEntry::displayName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public static ClientTattooEntry get(ResourceLocation id) {
        return APPROVED.get(id);
    }

    public static void clear() {
        APPROVED.clear();
    }

    public record ClientTattooEntry(
            ResourceLocation id,
            String displayName,
            String fileName,
            String sha256,
            long sizeBytes,
            int width,
            int height
    ) {
    }
}