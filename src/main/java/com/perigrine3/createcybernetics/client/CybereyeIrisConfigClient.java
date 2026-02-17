package com.perigrine3.createcybernetics.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CybereyeIrisConfigClient {

    private CybereyeIrisConfigClient() {}

    public enum Eye { LEFT, RIGHT }

    public enum IrisSize {
        ONE_BY_ONE(1, 1, "1x1"),
        ONE_BY_TWO(1, 2, "1x2"),
        TWO_BY_TWO(2, 2, "2x2");

        public final int w;
        public final int h;
        public final String id;

        IrisSize(int w, int h, String id) {
            this.w = w;
            this.h = h;
            this.id = id;
        }

        public static IrisSize fromId(@Nullable String s) {
            if ("1x2".equals(s)) return ONE_BY_TWO;
            if ("2x2".equals(s)) return TWO_BY_TWO;
            return ONE_BY_ONE;
        }
    }

    public static final class EyeLayout {
        public IrisSize size;
        public int x;
        public int y;

        public EyeLayout(IrisSize size, int x, int y) {
            this.size = size;
            this.x = x;
            this.y = y;
        }

        public EyeLayout copy() {
            return new EyeLayout(this.size, this.x, this.y);
        }
    }

    public static final class Layout {
        public final EyeLayout left;
        public final EyeLayout right;

        public Layout(EyeLayout left, EyeLayout right) {
            this.left = left;
            this.right = right;
        }

        public Layout copy() {
            return new Layout(left.copy(), right.copy());
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Cache per UUID. This is what the overlay should read.
    private static final Map<UUID, Layout> CACHE = new ConcurrentHashMap<>();

    // ---- Defaults ----
    // You requested default mapped 2 pixels down. Your previous default was (3,3).
    // So new default = (3,5).
    private static Layout defaultLayout() {
        return new Layout(
                new EyeLayout(IrisSize.ONE_BY_ONE, 3, 5),
                new EyeLayout(IrisSize.ONE_BY_ONE, 3, 5)
        );
    }

    // Face grid clamp (8x8)
    public static void clamp(EyeLayout eye) {
        int maxX = 8 - eye.size.w;
        int maxY = 8 - eye.size.h;

        if (eye.x < 0) eye.x = 0;
        if (eye.y < 0) eye.y = 0;
        if (eye.x > maxX) eye.x = maxX;
        if (eye.y > maxY) eye.y = maxY;
    }

    private static Path fileFor(UUID playerId) {
        Path dir = FMLPaths.CONFIGDIR.get().resolve(CreateCybernetics.MODID);
        return dir.resolve("cybereye_iris_" + playerId + ".json");
    }

    /** Main entrypoint for overlay rendering. Always returns something valid. */
    public static Layout get(UUID playerId) {
        return CACHE.computeIfAbsent(playerId, CybereyeIrisConfigClient::loadOrDefault);
    }

    /** Forces reload from disk (use on relog/login). */
    public static void reload(UUID playerId) {
        CACHE.put(playerId, loadOrDefault(playerId));
    }

    /** Clears cached data for that UUID. */
    public static void invalidate(UUID playerId) {
        CACHE.remove(playerId);
    }

    /** Deletes file + resets cached layout to defaults. */
    public static void reset(UUID playerId) {
        try {
            Files.deleteIfExists(fileFor(playerId));
        } catch (Throwable ignored) {}
        CACHE.put(playerId, defaultLayout());
    }

    /** Persist a layout to disk and update cache. Call this from your UI when the player hits Save. */
    public static void save(UUID playerId, Layout layout) {
        if (layout == null) return;

        // clamp before saving so disk always stays valid
        clamp(layout.left);
        clamp(layout.right);

        CACHE.put(playerId, layout.copy());

        Path file = fileFor(playerId);
        try {
            Files.createDirectories(file.getParent());

            JsonObject root = new JsonObject();

            JsonObject l = new JsonObject();
            l.addProperty("size", layout.left.size.id);
            l.addProperty("x", layout.left.x);
            l.addProperty("y", layout.left.y);

            JsonObject r = new JsonObject();
            r.addProperty("size", layout.right.size.id);
            r.addProperty("x", layout.right.x);
            r.addProperty("y", layout.right.y);

            root.add("left", l);
            root.add("right", r);

            try (Writer w = Files.newBufferedWriter(file)) {
                GSON.toJson(root, w);
            }
        } catch (Throwable ignored) {
        }
    }

    private static Layout loadOrDefault(UUID playerId) {
        Path file = fileFor(playerId);
        if (!Files.exists(file)) {
            return defaultLayout();
        }

        try (Reader r = Files.newBufferedReader(file)) {
            JsonObject obj = GSON.fromJson(r, JsonObject.class);
            if (obj == null) return defaultLayout();

            EyeLayout left = new EyeLayout(IrisSize.ONE_BY_ONE, 3, 5);
            EyeLayout right = new EyeLayout(IrisSize.ONE_BY_ONE, 3, 5);

            if (obj.has("left")) {
                JsonObject l = obj.getAsJsonObject("left");
                left.size = IrisSize.fromId(l.has("size") ? l.get("size").getAsString() : "1x1");
                left.x = l.has("x") ? l.get("x").getAsInt() : 3;
                left.y = l.has("y") ? l.get("y").getAsInt() : 5;
            }

            if (obj.has("right")) {
                JsonObject rr = obj.getAsJsonObject("right");
                right.size = IrisSize.fromId(rr.has("size") ? rr.get("size").getAsString() : "1x1");
                right.x = rr.has("x") ? rr.get("x").getAsInt() : 3;
                right.y = rr.has("y") ? rr.get("y").getAsInt() : 5;
            }

            clamp(left);
            clamp(right);

            return new Layout(left, right);
        } catch (Throwable ignored) {
            return defaultLayout();
        }
    }
}
