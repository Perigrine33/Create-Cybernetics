package com.perigrine3.createcybernetics.tattoo.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.network.payload.TattooImageDataS2CPayload;
import com.perigrine3.createcybernetics.network.payload.TattooImageRequestC2SPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import com.mojang.blaze3d.platform.NativeImage;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class ClientTattooTextureCache {
    private static final Map<ResourceLocation, CachedTattooTexture> CACHE = new HashMap<>();
    private static final Map<ResourceLocation, String> PENDING_REQUESTS = new HashMap<>();

    private ClientTattooTextureCache() {
    }

    public static ResourceLocation getTexture(ResourceLocation tattooId, String sha256) {
        if (tattooId == null || sha256 == null || sha256.isEmpty()) {
            return null;
        }

        CachedTattooTexture cached = CACHE.get(tattooId);
        if (cached != null && sha256.equals(cached.sha256())) {
            return cached.textureLocation();
        }

        request(tattooId, sha256);
        return null;
    }

    public static boolean hasTexture(ResourceLocation tattooId, String sha256) {
        CachedTattooTexture cached = CACHE.get(tattooId);
        return cached != null && cached.sha256().equals(sha256);
    }

    public static void request(ResourceLocation tattooId, String sha256) {
        if (tattooId == null || sha256 == null || sha256.isEmpty()) {
            return;
        }

        String pendingHash = PENDING_REQUESTS.get(tattooId);
        if (sha256.equals(pendingHash)) {
            return;
        }

        CachedTattooTexture cached = CACHE.get(tattooId);
        if (cached != null && sha256.equals(cached.sha256())) {
            return;
        }

        PENDING_REQUESTS.put(tattooId, sha256);
        PacketDistributor.sendToServer(new TattooImageRequestC2SPayload(tattooId, sha256));
    }

    public static void acceptImage(TattooImageDataS2CPayload payload) {
        if (payload == null || payload.tattooId() == null || payload.sha256().isEmpty() || payload.pngBytes().length == 0) {
            return;
        }

        try {
            NativeImage image = NativeImage.read(new ByteArrayInputStream(payload.pngBytes()));

            if (image.getWidth() != payload.width() || image.getHeight() != payload.height()) {
                image.close();
                CreateCybernetics.LOGGER.warn(
                        "Rejected tattoo image '{}': packet dimensions were {}x{}, decoded image was {}x{}",
                        payload.tattooId(),
                        payload.width(),
                        payload.height(),
                        image.getWidth(),
                        image.getHeight()
                );
                PENDING_REQUESTS.remove(payload.tattooId());
                return;
            }

            CachedTattooTexture old = CACHE.remove(payload.tattooId());
            if (old != null) {
                Minecraft.getInstance().getTextureManager().release(old.textureLocation());
            }

            ResourceLocation textureLocation = dynamicTextureLocation(payload.tattooId(), payload.sha256());
            DynamicTexture texture = new DynamicTexture(image);

            Minecraft.getInstance().getTextureManager().register(textureLocation, texture);

            CACHE.put(payload.tattooId(), new CachedTattooTexture(
                    payload.tattooId(),
                    textureLocation,
                    payload.sha256(),
                    payload.width(),
                    payload.height()
            ));

            PENDING_REQUESTS.remove(payload.tattooId());
        } catch (Exception ex) {
            PENDING_REQUESTS.remove(payload.tattooId());
            CreateCybernetics.LOGGER.warn("Failed to register tattoo texture '{}'", payload.tattooId(), ex);
        }
    }

    public static void clear() {
        Minecraft minecraft = Minecraft.getInstance();

        for (CachedTattooTexture cached : CACHE.values()) {
            minecraft.getTextureManager().release(cached.textureLocation());
        }

        CACHE.clear();
        PENDING_REQUESTS.clear();
    }

    private static ResourceLocation dynamicTextureLocation(ResourceLocation tattooId, String sha256) {
        String path = tattooId.getPath()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_/.-]", "_")
                .replace('/', '_')
                .replace('.', '_')
                .replace('-', '_');

        String shortHash = sha256.length() > 12 ? sha256.substring(0, 12) : sha256;

        return ResourceLocation.fromNamespaceAndPath(
                CreateCybernetics.MODID,
                "dynamic_tattoos/" + path + "_" + shortHash
        );
    }

    public record CachedTattooTexture(
            ResourceLocation tattooId,
            ResourceLocation textureLocation,
            String sha256,
            int width,
            int height
    ) {
    }
}