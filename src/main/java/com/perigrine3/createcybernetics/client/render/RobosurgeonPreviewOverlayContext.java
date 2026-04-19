package com.perigrine3.createcybernetics.client.render;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RobosurgeonPreviewOverlayContext {
    private static State current;

    private RobosurgeonPreviewOverlayContext() {}

    public static void begin(Entity target, List<Entry> entries) {
        List<Entry> overlays = new ArrayList<>();
        if (entries != null) {
            for (Entry entry : entries) {
                if (entry == null) continue;
                if (entry.texture() == null) continue;
                if (entry.alpha() <= 0f) continue;
                overlays.add(entry);
            }
        }
        current = new State(target, Collections.unmodifiableList(overlays));
    }

    public static void end() {
        current = null;
    }

    public static State get() {
        return current;
    }

    public static Entry entry(ResourceLocation texture, float alpha) {
        return new Entry(texture, alpha);
    }

    public record Entry(ResourceLocation texture, float alpha) {}

    public record State(Entity target, List<Entry> overlays) {}
}