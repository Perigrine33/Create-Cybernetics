package com.perigrine3.createcybernetics.block.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.perigrine3.createcybernetics.client.skin.CybereyeOverlayHandler;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class HoloprojectorBlockEntityRenderer implements BlockEntityRenderer<HoloprojectorBlockEntity> {

    private final Map<UUID, RemotePlayer> hologramPlayers = new HashMap<>();
    private final Map<Long, CachedEntity> hologramEntities = new HashMap<>();

    private static final Set<String> STRIP_KEYS = Set.of(
            "UUID", "Pos", "Motion", "Rotation", "FallDistance", "Fire", "Air",
            "OnGround", "PortalCooldown", "Dimension",
            "Passengers", "Vehicle", "Leash", "Tags", "Brain",
            "HandItems", "HandDropChances", "ArmorItems", "ArmorDropChances",
            "Inventory", "Items", "Offers"
    );

    public HoloprojectorBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(HoloprojectorBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (be == null) return;
        if (be.getLevel() == null) return;

        switch (be.getMode()) {
            case ITEM -> renderItem(be, partialTick, poseStack, buffer, packedLight, packedOverlay);
            case PLAYER -> renderPlayer(be, partialTick, poseStack, buffer, packedLight);
            case ENTITY -> renderEntity(be, partialTick, poseStack, buffer, packedLight);
            default -> {}
        }
    }

    private void renderItem(HoloprojectorBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemStack stack = be.getProjectedStack();
        if (stack.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        float alpha = Mth.clamp(be.getItemAlpha(), 0f, 1f);
        if (alpha <= 0.001f) return;

        long time = be.getLevel().getGameTime();
        float rot = (time + partialTick) * 4.0f;
        float bob = Mth.sin((time + partialTick) * 0.1f) * 0.03f;

        ItemRenderer itemRenderer = mc.getItemRenderer();

        poseStack.pushPose();

        poseStack.translate(0.5, 0.5 + bob, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(rot));
        float scale = 1.25f;
        poseStack.scale(scale, scale, scale);

        MultiBufferSource alphaBuffer = new AlphaMultiBufferSource(buffer, alpha);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.GROUND,
                packedLight,
                packedOverlay,
                poseStack,
                alphaBuffer,
                mc.level,
                0
        );

        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    private void renderPlayer(HoloprojectorBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        UUID uuid = be.getProjectedPlayerUuid();
        if (uuid == null) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        float alpha = Mth.clamp(be.getPlayerAlpha(), 0f, 1f);
        if (alpha <= 0.001f) return;

        final String desiredName = (be.getProjectedPlayerName() == null || be.getProjectedPlayerName().isBlank())
                ? "Hologram"
                : be.getProjectedPlayerName();

        RemotePlayer hologram = hologramPlayers.get(uuid);
        if (hologram == null || !desiredName.equals(hologram.getGameProfile().getName())) {
            hologram = new RemotePlayer(level, new GameProfile(uuid, desiredName));
            hologramPlayers.put(uuid, hologram);
        }

        forceOuterLayersOn(hologram, mc.player);

        hologram.tickCount = (int) level.getGameTime();

        CompoundTag snap = be.getProjectedCyberwareSnapshot();
        CompoundTag pd = hologram.getPersistentData();

        if (snap != null && !snap.isEmpty()) {
            pd.putBoolean(PlayerCyberwareData.HOLO_SNAPSHOT_FLAG, true);
            pd.put(PlayerCyberwareData.HOLO_SNAPSHOT_CYBERWARE, snap.copy());
        } else {
            pd.remove(PlayerCyberwareData.HOLO_SNAPSHOT_FLAG);
            pd.remove(PlayerCyberwareData.HOLO_SNAPSHOT_CYBERWARE);
        }

        // ------------------------------------------------------------
        // CYBEREYE IRIS LAYOUT -> HOLOGRAM (client-side copy)
        // ------------------------------------------------------------
        Player real = level.getPlayerByUUID(uuid);
        if (real != null) {
            CompoundTag eyeRoot = real.getPersistentData().getCompound(CybereyeOverlayHandler.NBT_ROOT);

            if (eyeRoot != null && !eyeRoot.isEmpty()) {
                pd.put(CybereyeOverlayHandler.NBT_ROOT, eyeRoot.copy());
                CybereyeOverlayHandler.invalidate(hologram);
            } else {
                pd.remove(CybereyeOverlayHandler.NBT_ROOT);
            }
        } else {
            pd.remove(CybereyeOverlayHandler.NBT_ROOT);
        }


        long time = level.getGameTime();
        float yaw = (time + partialTick) * 2.5f;
        yaw = Mth.wrapDegrees(yaw);

        hologram.setYRot(yaw);
        hologram.setXRot(0.0f);
        hologram.yRotO = yaw;
        hologram.xRotO = 0.0f;

        hologram.yBodyRot = yaw;
        hologram.yBodyRotO = yaw;

        hologram.yHeadRot = yaw;
        hologram.yHeadRotO = yaw;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        float bob = Mth.sin((time + partialTick) * 0.08f) * 0.02f;
        poseStack.translate(0.0, bob, 0.0);

        float scale = 0.9f;
        poseStack.scale(scale, scale, scale);

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        MultiBufferSource alphaBuffer = new AlphaMultiBufferSource(buffer, alpha);
        dispatcher.render(hologram, 0.0, 0.0, 0.0, yaw, partialTick, poseStack, alphaBuffer, packedLight);

        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private void renderEntity(HoloprojectorBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        float alpha = Mth.clamp(be.getPlayerAlpha(), 0f, 1f);
        if (alpha <= 0.001f) return;

        String typeRaw = be.getProjectedEntityTypeId();
        if (typeRaw == null || typeRaw.isBlank()) return;

        ResourceLocation typeId = ResourceLocation.tryParse(typeRaw);
        if (typeId == null) return;

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(typeId);
        if (type == null) return;

        long key = be.getBlockPos().asLong();

        int payloadHash = 31 * typeRaw.hashCode()
                + (be.getProjectedEntityName() != null ? be.getProjectedEntityName().hashCode() : 0)
                + (be.getProjectedEntityNbt() != null ? be.getProjectedEntityNbt().hashCode() : 0);

        CachedEntity cached = hologramEntities.get(key);

        if (cached == null
                || cached.entity == null
                || cached.entity.level() != level
                || cached.type != type
                || cached.payloadHash != payloadHash) {

            Entity e = type.create(level);
            if (e == null) return;

            e.setCustomName(null);
            e.setCustomNameVisible(false);

            CompoundTag nbt = be.getProjectedEntityNbt();
            if (nbt != null && !nbt.isEmpty()) {
                CompoundTag safe = nbt.copy();
                sanitizeEntityTagClient(safe);
                try { e.load(safe); } catch (Throwable ignored) {}
            }

            cached = new CachedEntity(e, type, payloadHash);
            hologramEntities.put(key, cached);
        }

        Entity hologram = cached.entity;
        if (hologram == null) return;

        hologram.setCustomName(null);
        hologram.setCustomNameVisible(false);
        hologram.tickCount = (int) level.getGameTime();

        long time = level.getGameTime();
        float yaw = (time + partialTick) * 2.5f;
        yaw = Mth.wrapDegrees(yaw);

        hologram.setYRot(yaw);
        hologram.yRotO = yaw;

        if (hologram instanceof net.minecraft.world.entity.LivingEntity le) {
            le.yBodyRot = yaw;
            le.yBodyRotO = yaw;

            le.yHeadRot = yaw;
            le.yHeadRotO = yaw;

            le.setXRot(0.0f);
            le.xRotO = 0.0f;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        float bob = Mth.sin((time + partialTick) * 0.08f) * 0.02f;
        poseStack.translate(0.0, bob, 0.0);

        float scale = 0.9f;
        poseStack.scale(scale, scale, scale);

        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Entity translucency implemented "like player": alpha-multiply + base-pass remap using renderer texture.
        MultiBufferSource alphaBuffer = new EntityHologramBufferSource(buffer, dispatcher, hologram, alpha);

        dispatcher.render(hologram, 0.0, 0.0, 0.0, yaw, partialTick, poseStack, alphaBuffer, packedLight);

        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private static void sanitizeEntityTagClient(CompoundTag tag) {
        for (String k : STRIP_KEYS) tag.remove(k);
    }

    private static final class CachedEntity {
        final Entity entity;
        final EntityType<?> type;
        final int payloadHash;

        private CachedEntity(Entity entity, EntityType<?> type, int payloadHash) {
            this.entity = entity;
            this.type = type;
            this.payloadHash = payloadHash;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void forceOuterLayersOn(net.minecraft.world.entity.player.Player hologram, @Nullable net.minecraft.world.entity.player.Player copyFrom) {
        try {
            java.lang.reflect.Field f = null;

            Class<?> cls = net.minecraft.world.entity.player.Player.class;
            for (String name : new String[] {
                    "DATA_PLAYER_MODE_CUSTOMISATION",
                    "DATA_PLAYER_MODE_CUSTOMIZATION",
                    "DATA_PLAYER_MODE_CUSTOMISATION_ID",
                    "DATA_PLAYER_MODE_CUSTOMIZATION_ID"
            }) {
                try {
                    f = cls.getDeclaredField(name);
                    break;
                } catch (NoSuchFieldException ignored) {}
            }

            if (f == null) return;

            f.setAccessible(true);
            Object accessorObj = f.get(null);
            if (!(accessorObj instanceof net.minecraft.network.syncher.EntityDataAccessor<?> accessor)) return;

            byte mask = 0x7F;
            if (copyFrom != null) {
                try {
                    Object val = copyFrom.getEntityData().get((net.minecraft.network.syncher.EntityDataAccessor) accessor);
                    if (val instanceof Byte b) mask = b;
                } catch (Throwable ignored) {}
            }

            hologram.getEntityData().set((net.minecraft.network.syncher.EntityDataAccessor) accessor, mask);
        } catch (Throwable ignored) {
        }
    }

    // ------------------------------------------------------------
    // Buffer sources / alpha consumers
    // ------------------------------------------------------------

    private static final class AlphaMultiBufferSource implements MultiBufferSource {
        private final MultiBufferSource delegate;
        private final float alphaMul;

        private AlphaMultiBufferSource(MultiBufferSource delegate, float alphaMul) {
            this.delegate = delegate;
            this.alphaMul = alphaMul;
        }

        @Override
        public VertexConsumer getBuffer(RenderType type) {
            RenderType mapped = mapToTranslucentIfNeeded(type);
            return new AlphaVertexConsumer(delegate.getBuffer(mapped), alphaMul);
        }

        // This is intentionally for blocks/items (your original behavior).
        private static RenderType mapToTranslucentIfNeeded(RenderType type) {
            if (type == RenderType.solid()
                    || type == RenderType.cutout()
                    || type == RenderType.cutoutMipped()
                    || type == RenderType.tripwire()) {
                return RenderType.translucent();
            }
            return type;
        }
    }

    /**
     * Entity-specific hologram buffer:
     * - always alpha-multiplies like your player path
     * - additionally remaps ONLY the entity's base model passes (solid/cutout families for its texture)
     *   to entityTranslucent(texture), preserving the renderer’s texture and leaving other layers alone.
     */
    private static final class EntityHologramBufferSource implements MultiBufferSource {
        private final MultiBufferSource delegate;
        private final float alphaMul;

        private final @Nullable RenderType solid;
        private final @Nullable RenderType cutout;
        private final @Nullable RenderType cutoutNoCull;
        private final @Nullable RenderType cutoutNoCullZOffset;
        private final @Nullable RenderType translucent;

        private EntityHologramBufferSource(MultiBufferSource delegate, EntityRenderDispatcher dispatcher, Entity entity, float alphaMul) {
            this.delegate = delegate;
            this.alphaMul = alphaMul;

            ResourceLocation tex;
            try {
                tex = dispatcher.getRenderer(entity).getTextureLocation(entity);
            } catch (Throwable t) {
                tex = null;
            }

            if (tex != null) {
                this.solid = RenderType.entitySolid(tex);
                this.cutout = RenderType.entityCutout(tex);
                this.cutoutNoCull = RenderType.entityCutoutNoCull(tex);
                this.cutoutNoCullZOffset = RenderType.entityCutoutNoCullZOffset(tex);
                this.translucent = RenderType.entityTranslucent(tex);
            } else {
                this.solid = null;
                this.cutout = null;
                this.cutoutNoCull = null;
                this.cutoutNoCullZOffset = null;
                this.translucent = null;
            }
        }

        @Override
        public VertexConsumer getBuffer(RenderType type) {
            RenderType mapped = mapEntityBasePassToTranslucent(type);
            return new AlphaVertexConsumer(delegate.getBuffer(mapped), alphaMul);
        }

        private RenderType mapEntityBasePassToTranslucent(RenderType type) {
            if (translucent == null) return type;

            if (type == solid || type == cutout || type == cutoutNoCull || type == cutoutNoCullZOffset) {
                return translucent;
            }

            return type;
        }
    }

    private static final class AlphaVertexConsumer implements VertexConsumer {
        private final VertexConsumer delegate;
        private final float alphaMul;

        private AlphaVertexConsumer(VertexConsumer delegate, float alphaMul) {
            this.delegate = delegate;
            this.alphaMul = alphaMul;
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            return delegate.addVertex(x, y, z);
        }

        @Override
        public VertexConsumer setColor(int r, int g, int b, int a) {
            int a2 = Mth.clamp((int) (a * alphaMul), 0, 255);
            return delegate.setColor(r, g, b, a2);
        }

        @Override public VertexConsumer setUv(float u, float v) { return delegate.setUv(u, v); }
        @Override public VertexConsumer setUv1(int u, int v) { return delegate.setUv1(u, v); }
        @Override public VertexConsumer setUv2(int u, int v) { return delegate.setUv2(u, v); }
        @Override public VertexConsumer setNormal(float x, float y, float z) { return delegate.setNormal(x, y, z); }
    }

    @Override
    public boolean shouldRenderOffScreen(HoloprojectorBlockEntity be) {
        return true;
    }
}
