package com.perigrine3.createcybernetics.client.skin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class FaceplateSkinOverrideClient {

    private FaceplateSkinOverrideClient() {}

    public record ResolvedSkin(ResourceLocation texture, PlayerSkin.Model model) {}

    private static final Map<String, ResolvedSkin> READY = new ConcurrentHashMap<>();
    private static final Map<String, Long> FAIL_UNTIL = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> PENDING = new ConcurrentHashMap<>();

    // Optional: cache name->uuid to reduce extra calls
    private static final Map<String, UUID> UUID_CACHE = new ConcurrentHashMap<>();

    private static final long FAIL_BACKOFF_TICKS = 20L * 30L; // 30s (stored as gameTime)
    private static final int MAX_NAME_LEN = 16;

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    /**
     * Returns a cached skin if available, otherwise schedules a resolve if eligible.
     * Returns null when not ready or when username is invalid / not found.
     */
    public static ResolvedSkin getOrRequest(String rawName) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return null;

        String name = sanitize(rawName);
        if (name == null) return null;

        ResolvedSkin cached = READY.get(name);
        if (cached != null) return cached;

        long now = mc.level.getGameTime();
        Long until = FAIL_UNTIL.get(name);
        if (until != null && now < until) return null;

        if (PENDING.putIfAbsent(name, Boolean.TRUE) != null) return null;

        Util.backgroundExecutor().execute(() -> resolveAsync(name));
        return null;
    }

    public static void clearCache() {
        READY.clear();
        FAIL_UNTIL.clear();
        PENDING.clear();
        UUID_CACHE.clear();
    }

    private static String sanitize(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty() || s.length() > MAX_NAME_LEN) return null;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            boolean ok = (c >= '0' && c <= '9')
                    || (c >= 'A' && c <= 'Z')
                    || (c >= 'a' && c <= 'z')
                    || c == '_';
            if (!ok) return null;
        }
        return s;
    }

    private static void resolveAsync(String name) {
        Minecraft mc = Minecraft.getInstance();
        try {
            // 1) name -> uuid (only succeeds for real accounts)
            UUID uuid = UUID_CACHE.get(name);
            if (uuid == null) {
                uuid = fetchUuidByName(name);
                if (uuid == null) {
                    // Not a real username (or API failed). Do NOT change skin.
                    fail(mc, name);
                    return;
                }
                UUID_CACHE.put(name, uuid);
            }

            // 2) uuid -> textures (skin url + model)
            SkinInfo info = fetchSkinInfo(uuid);
            if (info == null || info.skinUrl == null || info.skinUrl.isBlank()) {
                // Real account but no skin info resolved -> treat as no override
                fail(mc, name);
                return;
            }

            // 3) download png + register as dynamic texture on render thread
            byte[] png = fetchBytes(info.skinUrl);
            if (png == null || png.length == 0) {
                fail(mc, name);
                return;
            }

            PlayerSkin.Model model = info.slim ? PlayerSkin.Model.SLIM : PlayerSkin.Model.WIDE;

            byte[] finalPng = png;
            mc.execute(() -> {
                try {
                    ResourceLocation rl = registerDynamicSkinTexture(mc, name, finalPng);
                    if (rl != null) {
                        READY.put(name, new ResolvedSkin(rl, model));
                        FAIL_UNTIL.remove(name);
                    } else {
                        backoffFail(mc, name);
                    }
                } finally {
                    PENDING.remove(name);
                }
            });

        } catch (Throwable t) {
            mc.execute(() -> {
                try {
                    backoffFail(mc, name);
                } finally {
                    PENDING.remove(name);
                }
            });
        }
    }

    private static void fail(Minecraft mc, String name) {
        mc.execute(() -> {
            try {
                backoffFail(mc, name);
            } finally {
                PENDING.remove(name);
            }
        });
    }

    private static void backoffFail(Minecraft mc, String name) {
        long now = (mc.level != null) ? mc.level.getGameTime() : 0L;
        FAIL_UNTIL.put(name, now + FAIL_BACKOFF_TICKS);
    }

    // -----------------------------
    // Mojang API calls
    // -----------------------------

    // Mojang profile API: returns 204 if not found.
    // https://api.mojang.com/users/profiles/minecraft/<name>
    private static UUID fetchUuidByName(String name) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + name))
                .timeout(Duration.ofSeconds(8))
                .header("User-Agent", "CreateCybernetics-FaceplateSkin/1.0")
                .GET()
                .build();

        HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (res.statusCode() == 204 || res.statusCode() == 404) return null;
        if (res.statusCode() != 200) return null;

        JsonObject obj = JsonParser.parseString(res.body()).getAsJsonObject();
        if (!obj.has("id")) return null;

        String raw = obj.get("id").getAsString(); // 32 hex, no dashes
        return parseUuidNoDashes(raw);
    }

    // Session server: includes "properties" with base64 textures payload
    // https://sessionserver.mojang.com/session/minecraft/profile/<uuid>?unsigned=false
    private static SkinInfo fetchSkinInfo(UUID uuid) throws Exception {
        String u = uuid.toString().replace("-", "");
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + u + "?unsigned=false"))
                .timeout(Duration.ofSeconds(8))
                .header("User-Agent", "CreateCybernetics-FaceplateSkin/1.0")
                .GET()
                .build();

        HttpResponse<String> res = HTTP.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() != 200) return null;

        JsonObject obj = JsonParser.parseString(res.body()).getAsJsonObject();
        if (!obj.has("properties")) return null;

        for (JsonElement el : obj.getAsJsonArray("properties")) {
            if (!el.isJsonObject()) continue;
            JsonObject prop = el.getAsJsonObject();
            if (!"textures".equals(getString(prop, "name"))) continue;

            String value = getString(prop, "value");
            if (value == null || value.isBlank()) continue;

            String decoded = new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
            JsonObject texRoot = JsonParser.parseString(decoded).getAsJsonObject();

            JsonObject textures = texRoot.has("textures") ? texRoot.getAsJsonObject("textures") : null;
            if (textures == null || !textures.has("SKIN")) return null;

            JsonObject skin = textures.getAsJsonObject("SKIN");
            String url = getString(skin, "url");

            boolean slim = false;
            if (skin.has("metadata") && skin.get("metadata").isJsonObject()) {
                JsonObject meta = skin.getAsJsonObject("metadata");
                String model = getString(meta, "model");
                slim = "slim".equalsIgnoreCase(model);
            }

            return new SkinInfo(url, slim);
        }

        return null;
    }

    private static byte[] fetchBytes(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("User-Agent", "CreateCybernetics-FaceplateSkin/1.0")
                .GET()
                .build();

        HttpResponse<byte[]> res = HTTP.send(req, HttpResponse.BodyHandlers.ofByteArray());
        if (res.statusCode() != 200) return null;
        return res.body();
    }

    // -----------------------------
    // Texture registration
    // -----------------------------

    private static ResourceLocation registerDynamicSkinTexture(Minecraft mc, String name, byte[] pngBytes) {
        try (NativeImage img = NativeImage.read(new ByteArrayInputStream(pngBytes))) {
            if (img == null) return null;

            DynamicTexture tex = new DynamicTexture(img);

            // stable id; updates overwrite the same key
            String safe = name.toLowerCase(Locale.ROOT);
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("createcybernetics", "faceplate_skin/" + safe);

            mc.getTextureManager().register(id, tex);
            return id;
        } catch (Throwable t) {
            return null;
        }
    }

    // -----------------------------
    // Helpers
    // -----------------------------

    private static String getString(JsonObject obj, String key) {
        if (obj == null || key == null) return null;
        JsonElement el = obj.get(key);
        if (el == null || el.isJsonNull()) return null;
        return el.getAsString();
    }

    private static UUID parseUuidNoDashes(String s) {
        if (s == null) return null;
        String hex = s.trim();
        if (hex.length() != 32) return null;
        String dashed = hex.substring(0, 8) + "-"
                + hex.substring(8, 12) + "-"
                + hex.substring(12, 16) + "-"
                + hex.substring(16, 20) + "-"
                + hex.substring(20);
        return UUID.fromString(dashed);
    }

    private static final class SkinInfo {
        final String skinUrl;
        final boolean slim;
        SkinInfo(String skinUrl, boolean slim) {
            this.skinUrl = skinUrl;
            this.slim = slim;
        }
    }
}
