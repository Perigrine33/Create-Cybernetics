package com.perigrine3.createcybernetics.client.toggle;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CyberwareToggleClientState {
    private CyberwareToggleClientState() {}

    private static final Map<ResourceLocation, Boolean> STATES = new ConcurrentHashMap<>();

    public static boolean isActive(ResourceLocation id) {
        return STATES.getOrDefault(id, true);
    }

    public static void setActive(ResourceLocation id, boolean active) {
        STATES.put(id, active);
    }

    public static void clear() {
        STATES.clear();
    }
}
