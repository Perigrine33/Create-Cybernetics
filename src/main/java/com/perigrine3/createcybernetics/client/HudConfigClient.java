package com.perigrine3.createcybernetics.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-only persisted HUD layout/options.
 * Stored per-player UUID under config/createcybernetics/hud_layout_<uuid>.json
 *
 * Persists across relog + death by design (disk).
 */
public final class HudConfigClient {

    private HudConfigClient() {}

    public enum TargetMode {
        ABOVE_HOTBAR,      // current behavior (your Y=250 offset relative to center)
        UNDER_CROSSHAIR,   // Y = 50
        OFF
    }

    public enum BatteryMode {
        TEXT_ONLY,                   // just "current/capacity"
        ICON_ONLY,                   // icon only
        ICON_PLUS_CAPACITY,          // icon + "current/capacity"
        ICON_PLUS_CAPACITY_PLUS_STATS// icon + "current/capacity" + GEN/USE
    }

    public static final class HudConfig {
        public boolean coordsEnabled;
        public boolean toggleListEnabled;
        public boolean shardsEnabled;
        public TargetMode targetMode;
        public BatteryMode batteryMode;

        public HudConfig(boolean coordsEnabled,
                         boolean toggleListEnabled,
                         boolean shardsEnabled,
                         TargetMode targetMode,
                         BatteryMode batteryMode) {
            this.coordsEnabled = coordsEnabled;
            this.toggleListEnabled = toggleListEnabled;
            this.shardsEnabled = shardsEnabled;
            this.targetMode = targetMode;
            this.batteryMode = batteryMode;
        }

        public HudConfig copy() {
            return new HudConfig(coordsEnabled, toggleListEnabled, shardsEnabled, targetMode, batteryMode);
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<UUID, HudConfig> CACHE = new ConcurrentHashMap<>();

    /** Defaults = exactly your current HUD behavior. */
    private static HudConfig defaultConfig() {
        return new HudConfig(
                true, // coords+biome ON
                true, // toggleables ON
                true, // shards ON
                TargetMode.ABOVE_HOTBAR, // current placement
                BatteryMode.ICON_PLUS_CAPACITY_PLUS_STATS // full battery block
        );
    }

    private static Path fileFor(UUID playerId) {
        Path dir = FMLPaths.CONFIGDIR.get().resolve(CreateCybernetics.MODID);
        return dir.resolve("hud_layout_" + playerId + ".json");
    }

    /** Main entrypoint for HUD rendering. Always returns something valid. */
    public static HudConfig get(UUID playerId) {
        return CACHE.computeIfAbsent(playerId, HudConfigClient::loadOrDefault);
    }

    /** Forces reload from disk (use on relog/login). */
    public static void reload(UUID playerId) {
        CACHE.put(playerId, loadOrDefault(playerId));
    }

    /** Clears cached data for that UUID. */
    public static void invalidate(UUID playerId) {
        CACHE.remove(playerId);
    }

    /** Deletes file + resets cached config to defaults. */
    public static void reset(UUID playerId) {
        try {
            Files.deleteIfExists(fileFor(playerId));
        } catch (Throwable ignored) {}
        CACHE.put(playerId, defaultConfig());
    }

    /** Persist config to disk and update cache. */
    public static void save(UUID playerId, HudConfig cfg) {
        if (cfg == null) return;

        // Defensive: never allow null modes
        if (cfg.targetMode == null) cfg.targetMode = TargetMode.ABOVE_HOTBAR;
        if (cfg.batteryMode == null) cfg.batteryMode = BatteryMode.ICON_PLUS_CAPACITY_PLUS_STATS;

        CACHE.put(playerId, cfg.copy());

        Path file = fileFor(playerId);
        try {
            Files.createDirectories(file.getParent());

            JsonObject root = new JsonObject();
            root.addProperty("coordsEnabled", cfg.coordsEnabled);
            root.addProperty("toggleListEnabled", cfg.toggleListEnabled);
            root.addProperty("shardsEnabled", cfg.shardsEnabled);
            root.addProperty("targetMode", cfg.targetMode.name());
            root.addProperty("batteryMode", cfg.batteryMode.name());

            try (Writer w = Files.newBufferedWriter(file)) {
                GSON.toJson(root, w);
            }
        } catch (Throwable ignored) {
        }
    }

    private static HudConfig loadOrDefault(UUID playerId) {
        Path file = fileFor(playerId);
        if (!Files.exists(file)) return defaultConfig();

        try (Reader r = Files.newBufferedReader(file)) {
            JsonObject obj = GSON.fromJson(r, JsonObject.class);
            if (obj == null) return defaultConfig();

            HudConfig cfg = defaultConfig();

            if (obj.has("coordsEnabled")) cfg.coordsEnabled = obj.get("coordsEnabled").getAsBoolean();
            if (obj.has("toggleListEnabled")) cfg.toggleListEnabled = obj.get("toggleListEnabled").getAsBoolean();
            if (obj.has("shardsEnabled")) cfg.shardsEnabled = obj.get("shardsEnabled").getAsBoolean();

            if (obj.has("targetMode")) cfg.targetMode = parseTargetMode(obj.get("targetMode").getAsString());
            if (obj.has("batteryMode")) cfg.batteryMode = parseBatteryMode(obj.get("batteryMode").getAsString());

            if (cfg.targetMode == null) cfg.targetMode = TargetMode.ABOVE_HOTBAR;
            if (cfg.batteryMode == null) cfg.batteryMode = BatteryMode.ICON_PLUS_CAPACITY_PLUS_STATS;

            return cfg;
        } catch (Throwable ignored) {
            return defaultConfig();
        }
    }

    private static TargetMode parseTargetMode(@Nullable String s) {
        if (s == null) return TargetMode.ABOVE_HOTBAR;
        try {
            return TargetMode.valueOf(s);
        } catch (Throwable ignored) {
            return TargetMode.ABOVE_HOTBAR;
        }
    }

    private static BatteryMode parseBatteryMode(@Nullable String s) {
        if (s == null) return BatteryMode.ICON_PLUS_CAPACITY_PLUS_STATS;
        try {
            return BatteryMode.valueOf(s);
        } catch (Throwable ignored) {
            return BatteryMode.ICON_PLUS_CAPACITY_PLUS_STATS;
        }
    }
}
