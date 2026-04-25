package com.perigrine3.createcybernetics.client.skin;

import com.mojang.blaze3d.platform.NativeImage;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CybereyeOverlayHandler {

    private CybereyeOverlayHandler() {}

    public static final String NBT_ROOT = "cc_cybereye_cfg";
    public static final String NBT_LEFT = "left";
    public static final String NBT_RIGHT = "right";
    public static final String NBT_X = "x";
    public static final String NBT_Y = "y";
    public static final String NBT_VARIANT = "variant";

    private static final int FACE_U = 8;
    private static final int FACE_V = 8;
    private static final int FACE_W = 8;
    private static final int FACE_H = 8;

    public enum EyeSide { LEFT, RIGHT }
    public enum Variant { V1x1, V1x2, V2x2 }

    public record EyePlacement(int x, int y, Variant variant) {}

    private static final Map<UUID, Entry> CACHE = new ConcurrentHashMap<>();
    private static final Map<UUID, CompoundTag> SYNCED_CONFIGS = new ConcurrentHashMap<>();

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

    private static final Map<EyeSide, EnumMap<Variant, NativeImage>> TEMPLATES = new EnumMap<>(EyeSide.class);
    private static boolean templatesLoaded = false;
    private static boolean templatesFailed = false;

    public static ResourceLocation getOrBuildOverlay(Player player) {
        if (player == null) return null;

        UUID id = player.getUUID();
        CompoundTag root = getEffectiveRoot(player);

        EyePlacement left = readPlacement(root, EyeSide.LEFT);
        EyePlacement right = readPlacement(root, EyeSide.RIGHT);

        if (left == null) left = defaultPlacement(EyeSide.LEFT);
        if (right == null) right = defaultPlacement(EyeSide.RIGHT);

        left = clampToFace(left);
        right = clampToFace(right);

        int hash = hash(left, right);

        Entry e = CACHE.get(id);
        if (e != null && e.lastHash == hash) {
            return e.textureId;
        }

        ensureTemplatesLoaded();
        if (templatesFailed) {
            return null;
        }

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

        NativeImage img = e.dyn.getPixels();
        if (img == null) return null;

        clear(img);
        stamp(img, EyeSide.LEFT, left);
        stamp(img, EyeSide.RIGHT, right);

        e.dyn.upload();
        e.lastHash = hash;

        return e.textureId;
    }

    public static void applySyncedConfig(UUID playerId,
                                         int leftX, int leftY, int leftVariant,
                                         int rightX, int rightY, int rightVariant) {
        if (playerId == null) return;

        CompoundTag root = buildRoot(leftX, leftY, leftVariant, rightX, rightY, rightVariant);
        SYNCED_CONFIGS.put(playerId, root);
        invalidate(playerId);

        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            Player player = mc.level.getPlayerByUUID(playerId);
            if (player != null) {
                player.getPersistentData().put(NBT_ROOT, root.copy());
            }
        }
    }

    public static void applySyncedConfig(Player player,
                                         int leftX, int leftY, int leftVariant,
                                         int rightX, int rightY, int rightVariant) {
        if (player == null) return;

        CompoundTag root = buildRoot(leftX, leftY, leftVariant, rightX, rightY, rightVariant);
        SYNCED_CONFIGS.put(player.getUUID(), root.copy());
        player.getPersistentData().put(NBT_ROOT, root.copy());
        invalidate(player);
    }

    public static void invalidate(Player player) {
        if (player == null) return;
        invalidate(player.getUUID());
    }

    public static void invalidate(UUID playerId) {
        if (playerId == null) return;
        Entry e = CACHE.get(playerId);
        if (e != null) {
            e.lastHash = -1;
        }
    }

    public static void clearAll() {
        CACHE.clear();
        SYNCED_CONFIGS.clear();
    }

    private static CompoundTag getEffectiveRoot(Player player) {
        CompoundTag synced = SYNCED_CONFIGS.get(player.getUUID());
        if (synced != null && !synced.isEmpty()) {
            return synced;
        }

        CompoundTag root = player.getPersistentData().getCompound(NBT_ROOT);
        return root == null ? new CompoundTag() : root;
    }

    private static CompoundTag buildRoot(int leftX, int leftY, int leftVariant,
                                         int rightX, int rightY, int rightVariant) {
        CompoundTag root = new CompoundTag();

        CompoundTag left = new CompoundTag();
        left.putInt(NBT_X, leftX);
        left.putInt(NBT_Y, leftY);
        left.putInt(NBT_VARIANT, clampVariant(leftVariant));
        root.put(NBT_LEFT, left);

        CompoundTag right = new CompoundTag();
        right.putInt(NBT_X, rightX);
        right.putInt(NBT_Y, rightY);
        right.putInt(NBT_VARIANT, clampVariant(rightVariant));
        root.put(NBT_RIGHT, right);

        return root;
    }

    private static EyePlacement readPlacement(CompoundTag root, EyeSide side) {
        if (root == null || root.isEmpty()) return null;

        CompoundTag tag = root.getCompound(side == EyeSide.LEFT ? NBT_LEFT : NBT_RIGHT);
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
        return v == Variant.V2x2 ? 2 : 1;
    }

    private static int variantH(Variant v) {
        return v == Variant.V1x2 || v == Variant.V2x2 ? 2 : 1;
    }

    private static int clampVariant(int variant) {
        return Mth.clamp(variant, 0, 2);
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

    private static void ensureTemplatesLoaded() {
        if (templatesLoaded || templatesFailed) return;

        try {
            TEMPLATES.put(EyeSide.LEFT, new EnumMap<>(Variant.class));
            TEMPLATES.put(EyeSide.RIGHT, new EnumMap<>(Variant.class));

            loadTemplate(EyeSide.LEFT, Variant.V1x1, "textures/entity/cybereyes/left_1x1.png");
            loadTemplate(EyeSide.LEFT, Variant.V1x2, "textures/entity/cybereyes/left_1x2.png");
            loadTemplate(EyeSide.LEFT, Variant.V2x2, "textures/entity/cybereyes/left_2x2.png");

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
            TEMPLATES.get(side).put(variant, img);
        }
    }

    private static void clear(NativeImage img) {
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

                int out = (a << 24) | 0x00FFFFFF;
                dst.setPixelRGBA(dx, dy, out);
            }
        }
    }
}