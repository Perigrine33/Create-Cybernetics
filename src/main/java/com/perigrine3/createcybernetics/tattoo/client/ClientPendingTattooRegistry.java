package com.perigrine3.createcybernetics.tattoo.client;

import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ClientPendingTattooRegistry {
    private static final Map<ResourceLocation, ClientTattooRegistry.ClientTattooEntry> PENDING = new LinkedHashMap<>();

    private ClientPendingTattooRegistry() {
    }

    public static void replacePending(List<ClientTattooRegistry.ClientTattooEntry> entries) {
        PENDING.clear();

        if (entries == null) {
            return;
        }

        entries.stream()
                .sorted(Comparator.comparing(ClientTattooRegistry.ClientTattooEntry::displayName, String.CASE_INSENSITIVE_ORDER))
                .forEach(entry -> PENDING.put(entry.id(), entry));
    }

    public static List<ClientTattooRegistry.ClientTattooEntry> pendingTattoos() {
        return PENDING.values()
                .stream()
                .sorted(Comparator.comparing(ClientTattooRegistry.ClientTattooEntry::displayName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public static ClientTattooRegistry.ClientTattooEntry get(ResourceLocation id) {
        return PENDING.get(id);
    }

    public static void remove(ResourceLocation id) {
        if (id != null) {
            PENDING.remove(id);
        }
    }

    public static void clear() {
        PENDING.clear();
    }
}