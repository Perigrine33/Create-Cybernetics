package com.perigrine3.createcybernetics.client.skin;

import com.mojang.blaze3d.platform.NativeImage;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Builds a per-player 64x64 dynamic overlay texture for cybereyes by stamping
 * one of six small mask templates (L/R × 1x1,1x2,2x2) at the offsets chosen in the UI.
 *
 * - The UI highlight is NOT used here; this produces only the overlay mask.
 * - Actual color comes from your existing SkinModifier tint (dye color).
 * - Offsets are clamped to the 8x8 *base face layer* area: (8,8) size 8x8 on 64x64 skin.
 */
public final class CybereyeOverlayHandler {

    private CybereyeOverlayHandler() {}

    // ---- NBT KEYS (adjust to match whatever your UI already writes) ----
    // Stored on the PLAYER entity persistent data so it's available client-side after sync.
    public static final String NBT_ROOT = "cc_cybereye_cfg";
    public static final String NBT_LEFT = "left";
    public static final String NBT_RIGHT = "right";
    public static final String NBT_X = "x";
    public static final String NBT_Y = "y";
    public static final String NBT_VARIANT = "variant"; // 0=1x1, 1=1x2, 2=2x2

    // ---- FACE (base layer) clamp region on 64x64 skin ----
    // Head front face: u=8..15, v=8..15 (8x8)
    private static final int FACE_U = 8;
    private static final int FACE_V = 8;
    private static final int FACE_W = 8;
    private static final int FACE_H = 8;

    public enum EyeSide { LEFT, RIGHT }
    public enum Variant { V1x1, V1x2, V2x2 }

    public record EyePlacement(int x, int y, Variant variant) {}

    // Cache of per-player dynamic textures
    private static final Map<UUID, Entry> CACHE = new ConcurrentHashMap<>();

    private static final class Entry {
        final ResourceLocation textureId;
        final DynamicTexture dyn;
        int lastHash;

        Entry(ResourceLocation textureId, DynamicTexture dyn, int lastHash) {
            this.textureId = textureId;
            this.dyn = dyn;
            this.lastHash = lastHash;
        }
    }

    // Template cache: side->variant->NativeImage (small mask)
    private static final Map<EyeSide, EnumMap<Variant, NativeImage>> TEMPLATES = new EnumMap<>(EyeSide.class);
    private static boolean templatesLoaded = false;
    private static boolean templatesFailed = false;

    /**
     * Ensure and return a per-player overlay texture RL.
     * Rebuilds only when placement changes.
     */
    public static ResourceLocation getOrBuildOverlay(Player player) {
        if (player == null) return null;

        EyePlacement left = readPlacement(player, EyeSide.LEFT);
        EyePlacement right = readPlacement(player, EyeSide.RIGHT);

        // Default: 1x1 for both eyes, with a sane default position on face
        // (these should match your "default offsets in mind")
        if (left == null) left = defaultPlacement(EyeSide.LEFT);
        if (right == null) right = defaultPlacement(EyeSide.RIGHT);

        left = clampToFace(left);
        right = clampToFace(right);

        int hash = hash(left, right);

        UUID id = player.getUUID();
        Entry e = CACHE.get(id);

        if (e != null && e.lastHash == hash) {
            return e.textureId;
        }

        ensureTemplatesLoaded();
        if (templatesFailed) {
            // If templates are missing, fail safely: no overlay
            return null;
        }

        // Create or reuse a 64x64 dynamic texture
        Minecraft mc = Minecraft.getInstance();
        if (e == null) {
            ResourceLocation texId = ResourceLocation.fromNamespaceAndPath(
                    CreateCybernetics.MODID,
                    "dynamic/cybereyes/" + id
            );

            DynamicTexture dyn = new DynamicTexture(64, 64, true);
            mc.getTextureManager().register(texId, dyn);

            e = new Entry(texId, dyn, -1);
            CACHE.put(id, e);
        }

        // Rebuild into the dynamic texture's image
        NativeImage img = e.dyn.getPixels();
        if (img == null) return null;

        clear(img);
        stamp(img, EyeSide.LEFT, left);
        stamp(img, EyeSide.RIGHT, right);

        e.dyn.upload();
        e.lastHash = hash;

        return e.textureId;
    }

    /**
     * Call this when you *know* the config changed (e.g., right after your UI saves),
     * to force rebuild next time.
     */
    public static void invalidate(Player player) {
        if (player == null) return;
        Entry e = CACHE.get(player.getUUID());
        if (e != null) e.lastHash = -1;
    }

    /**
     * Cleanup on world unload if you want to be tidy.
     */
    public static void clearAll() {
        CACHE.clear();
    }

    /* ---------------------------- Placement IO ---------------------------- */

    private static EyePlacement readPlacement(Player player, EyeSide side) {
        var root = player.getPersistentData().getCompound(NBT_ROOT);
        if (root == null || root.isEmpty()) return null;

        var tag = root.getCompound(side == EyeSide.LEFT ? NBT_LEFT : NBT_RIGHT);
        if (tag == null || tag.isEmpty()) return null;

        int x = tag.getInt(NBT_X);
        int y = tag.getInt(NBT_Y);
        int v = tag.getInt(NBT_VARIANT);

        Variant variant = switch (v) {
            case 1 -> Variant.V1x2;
            case 2 -> Variant.V2x2;
            default -> Variant.V1x1;
        };

        return new EyePlacement(x, y, variant);
    }

    private static EyePlacement defaultPlacement(EyeSide side) {
        // These defaults are on the head-front face area (8..15,8..15).
        // Tune these to your exact desired defaults.
        // Common Steve-ish defaults:
        // left eye around (10, 10), right eye around (13, 10) for 1x1.
        int x = (side == EyeSide.LEFT) ? 10 : 13;
        int y = 10;
        return new EyePlacement(x, y, Variant.V1x1);
    }

    private static EyePlacement clampToFace(EyePlacement p) {
        int w = variantW(p.variant);
        int h = variantH(p.variant);

        int minX = FACE_U;
        int minY = FACE_V;
        int maxX = FACE_U + FACE_W - w;
        int maxY = FACE_V + FACE_H - h;

        int cx = Mth.clamp(p.x, minX, maxX);
        int cy = Mth.clamp(p.y, minY, maxY);

        return new EyePlacement(cx, cy, p.variant);
    }

    private static int variantW(Variant v) {
        return (v == Variant.V2x2) ? 2 : 1;
    }

    private static int variantH(Variant v) {
        return (v == Variant.V1x2 || v == Variant.V2x2) ? 2 : 1;
    }

    private static int hash(EyePlacement l, EyePlacement r) {
        int h = 17;
        h = 31 * h + l.x;
        h = 31 * h + l.y;
        h = 31 * h + l.variant.ordinal();
        h = 31 * h + r.x;
        h = 31 * h + r.y;
        h = 31 * h + r.variant.ordinal();
        return h;
    }

    /* ---------------------------- Template IO ---------------------------- */

    private static void ensureTemplatesLoaded() {
        if (templatesLoaded || templatesFailed) return;

        try {
            TEMPLATES.put(EyeSide.LEFT, new EnumMap<>(Variant.class));
            TEMPLATES.put(EyeSide.RIGHT, new EnumMap<>(Variant.class));

            loadTemplate(EyeSide.LEFT, Variant.V1x1,  "textures/entity/cybereyes/left_1x1.png");
            loadTemplate(EyeSide.LEFT, Variant.V1x2,  "textures/entity/cybereyes/left_1x2.png");
            loadTemplate(EyeSide.LEFT, Variant.V2x2,  "textures/entity/cybereyes/left_2x2.png");

            loadTemplate(EyeSide.RIGHT, Variant.V1x1, "textures/entity/cybereyes/right_1x1.png");
            loadTemplate(EyeSide.RIGHT, Variant.V1x2, "textures/entity/cybereyes/right_1x2.png");
            loadTemplate(EyeSide.RIGHT, Variant.V2x2, "textures/entity/cybereyes/right_2x2.png");

            templatesLoaded = true;
        } catch (Throwable t) {
            templatesFailed = true;
        }
    }

    private static void loadTemplate(EyeSide side, Variant variant, String path) throws Exception {
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, path);
        Resource res = mc.getResourceManager().getResourceOrThrow(rl);

        try (InputStream in = res.open()) {
            NativeImage img = NativeImage.read(in);

            // Optional sanity: ensure dimensions match variant
            int wantW = variantW(variant);
            int wantH = variantH(variant);
            if (img.getWidth() != wantW || img.getHeight() != wantH) {
                // You *can* allow mismatches, but they usually indicate wrong asset.
                // We'll still accept it but stamping uses the image's actual dimensions.
            }

            TEMPLATES.get(side).put(variant, img);
        }
    }

    /* ---------------------------- Composition ---------------------------- */

    private static void clear(NativeImage img) {
        // Set all pixels to transparent
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                img.setPixelRGBA(x, y, 0x00000000);
            }
        }
    }

    private static void stamp(NativeImage dst, EyeSide side, EyePlacement p) {
        NativeImage mask = TEMPLATES.get(side).get(p.variant);
        if (mask == null) return;

        int w = mask.getWidth();
        int h = mask.getHeight();

        // p.x,p.y already clamped, but clamp again using actual mask dims just in case.
        int maxX = FACE_U + FACE_W - w;
        int maxY = FACE_V + FACE_H - h;
        int ox = Mth.clamp(p.x, FACE_U, maxX);
        int oy = Mth.clamp(p.y, FACE_V, maxY);

        for (int my = 0; my < h; my++) {
            for (int mx = 0; mx < w; mx++) {
                int rgba = mask.getPixelRGBA(mx, my);
                int a = (rgba >>> 24) & 0xFF;
                if (a == 0) continue;

                int dx = ox + mx;
                int dy = oy + my;

                // Set to white with mask alpha. Tint is applied later via SkinModifier color.
                int out = (a << 24) | 0x00FFFFFF;
                dst.setPixelRGBA(dx, dy, out);
            }
        }
    }
}
