package com.perigrine3.createcybernetics.block.entity;

import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HoloprojectorBlockEntity extends BlockEntity {

    public static final String NBT_PROJECTION_MODE = "ProjectionMode";

    private static final String NBT_ITEM = "ProjectedItem";

    private static final String NBT_PLAYER_UUID = "ProjectedPlayerUUID";
    private static final String NBT_PLAYER_NAME = "ProjectedPlayerName";
    private static final String NBT_CYBERWARE_SNAPSHOT = "ProjectedCyberwareSnapshot";

    private static final String NBT_ENTITY_TYPE = "ProjectedEntityType";
    private static final String NBT_ENTITY_NAME = "ProjectedEntityName";
    private static final String NBT_ENTITY_NBT  = "ProjectedEntityNbt";

    private static final String TAG_ITEM_ALPHA = "cc_item_alpha";
    private static final String TAG_PLAYER_ALPHA = "cc_player_alpha";

    // 1.0 = fully opaque, 0.0 = invisible
    private float itemAlpha = 0.75f;
    private float playerAlpha = 0.75f;

    public float getItemAlpha() { return itemAlpha; }
    public float getPlayerAlpha() { return playerAlpha; }

    public void setItemAlpha(float a) {
        itemAlpha = Mth.clamp(a, 0f, 1f);
        setChangedAndSync();
    }

    public void setPlayerAlpha(float a) {
        playerAlpha = Mth.clamp(a, 0f, 1f);
        setChangedAndSync();
    }

    public enum ProjectionMode {
        NONE, ITEM, PLAYER, ENTITY
    }

    private ProjectionMode mode = ProjectionMode.NONE;

    private ItemStack projectedStack = ItemStack.EMPTY;

    private @Nullable UUID projectedPlayerUuid = null;
    private String projectedPlayerName = "";
    private CompoundTag projectedCyberwareSnapshot = new CompoundTag();

    // ENTITY payload
    private String projectedEntityTypeId = ""; // ResourceLocation as string, e.g. "minecraft:zombie"
    private String projectedEntityName = "";
    private CompoundTag projectedEntityNbt = new CompoundTag();

    public HoloprojectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HOLOPROJECTOR_BLOCKENTITY.get(), pos, state);
    }

    // ===== API =====

    public ProjectionMode getMode() {
        return mode;
    }

    public ItemStack getProjectedStack() {
        return projectedStack;
    }

    public @Nullable UUID getProjectedPlayerUuid() {
        return projectedPlayerUuid;
    }

    public String getProjectedPlayerName() {
        return projectedPlayerName;
    }

    public CompoundTag getProjectedCyberwareSnapshot() {
        return projectedCyberwareSnapshot;
    }

    // ENTITY getters
    public String getProjectedEntityTypeId() {
        return projectedEntityTypeId;
    }

    public String getProjectedEntityName() {
        return projectedEntityName;
    }

    public CompoundTag getProjectedEntityNbt() {
        return projectedEntityNbt;
    }

    public void setProjectedItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            clearProjection();
            return;
        }

        this.mode = ProjectionMode.ITEM;
        this.projectedStack = stack.copy();
        this.projectedStack.setCount(1);

        // clear others
        this.projectedPlayerUuid = null;
        this.projectedPlayerName = "";
        this.projectedCyberwareSnapshot = new CompoundTag();

        this.projectedEntityTypeId = "";
        this.projectedEntityName = "";
        this.projectedEntityNbt = new CompoundTag();

        setChangedAndSync();
    }

    public void setProjectedPlayer(UUID uuid, String name) {
        if (uuid == null) {
            clearProjection();
            return;
        }

        this.mode = ProjectionMode.PLAYER;
        this.projectedPlayerUuid = uuid;
        this.projectedPlayerName = (name == null) ? "" : name;

        this.projectedStack = ItemStack.EMPTY;

        // clear entity
        this.projectedEntityTypeId = "";
        this.projectedEntityName = "";
        this.projectedEntityNbt = new CompoundTag();

        setChangedAndSync();
    }

    public void setProjectedPlayerFrom(ServerPlayer player) {
        if (player == null) {
            clearProjection();
            return;
        }

        this.mode = ProjectionMode.PLAYER;
        this.projectedPlayerUuid = player.getUUID();
        this.projectedPlayerName = player.getGameProfile().getName();
        this.projectedStack = ItemStack.EMPTY;

        // clear entity
        this.projectedEntityTypeId = "";
        this.projectedEntityName = "";
        this.projectedEntityNbt = new CompoundTag();

        this.projectedCyberwareSnapshot = new CompoundTag();
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data != null) {
            CompoundTag snap = data.serializeNBT(player.registryAccess());
            this.projectedCyberwareSnapshot = (snap != null) ? snap : new CompoundTag();
        }

        setChangedAndSync();
    }

    /**
     * Sets ENTITY mode. typeId should be a ResourceLocation string like "minecraft:zombie".
     * nbt is optional (appearance/variant only recommended).
     */
    public void setProjectedEntity(String typeId, @Nullable CompoundTag nbt, @Nullable String name) {
        if (typeId == null || typeId.isBlank()) {
            clearProjection();
            return;
        }

        this.mode = ProjectionMode.ENTITY;

        this.projectedEntityTypeId = typeId;
        this.projectedEntityName = (name == null) ? "" : name;
        this.projectedEntityNbt = (nbt == null) ? new CompoundTag() : nbt.copy();

        // clear others
        this.projectedStack = ItemStack.EMPTY;
        this.projectedPlayerUuid = null;
        this.projectedPlayerName = "";
        this.projectedCyberwareSnapshot = new CompoundTag();

        setChangedAndSync();
    }

    public void clearProjection() {
        this.mode = ProjectionMode.NONE;

        this.projectedStack = ItemStack.EMPTY;

        this.projectedPlayerUuid = null;
        this.projectedPlayerName = "";
        this.projectedCyberwareSnapshot = new CompoundTag();

        this.projectedEntityTypeId = "";
        this.projectedEntityName = "";
        this.projectedEntityNbt = new CompoundTag();

        setChangedAndSync();
    }

    private void setChangedAndSync() {
        setChanged();

        if (level == null) return;

        if (!level.isClientSide) {
            level.blockEntityChanged(worldPosition);
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    // ===== Saving / Loading =====

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putFloat(TAG_ITEM_ALPHA, itemAlpha);
        tag.putFloat(TAG_PLAYER_ALPHA, playerAlpha);

        tag.putString(NBT_PROJECTION_MODE, mode.name());

        if (mode == ProjectionMode.ITEM && !projectedStack.isEmpty()) {
            tag.put(NBT_ITEM, projectedStack.save(registries));
        }

        if (mode == ProjectionMode.PLAYER && projectedPlayerUuid != null) {
            tag.putString(NBT_PLAYER_UUID, projectedPlayerUuid.toString());
            if (!projectedPlayerName.isEmpty()) {
                tag.putString(NBT_PLAYER_NAME, projectedPlayerName);
            }
            if (projectedCyberwareSnapshot != null && !projectedCyberwareSnapshot.isEmpty()) {
                tag.put(NBT_CYBERWARE_SNAPSHOT, projectedCyberwareSnapshot.copy());
            }
        }

        if (mode == ProjectionMode.ENTITY && projectedEntityTypeId != null && !projectedEntityTypeId.isBlank()) {
            tag.putString(NBT_ENTITY_TYPE, projectedEntityTypeId);
            if (!projectedEntityName.isEmpty()) {
                tag.putString(NBT_ENTITY_NAME, projectedEntityName);
            }
            if (projectedEntityNbt != null && !projectedEntityNbt.isEmpty()) {
                tag.put(NBT_ENTITY_NBT, projectedEntityNbt.copy());
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        this.itemAlpha = tag.contains(TAG_ITEM_ALPHA, Tag.TAG_FLOAT) ? tag.getFloat(TAG_ITEM_ALPHA) : 0.75f;
        this.playerAlpha = tag.contains(TAG_PLAYER_ALPHA, Tag.TAG_FLOAT) ? tag.getFloat(TAG_PLAYER_ALPHA) : 0.75f;
        this.itemAlpha = Mth.clamp(this.itemAlpha, 0f, 1f);
        this.playerAlpha = Mth.clamp(this.playerAlpha, 0f, 1f);

        this.mode = readMode(tag.getString(NBT_PROJECTION_MODE));

        // reset everything
        this.projectedStack = ItemStack.EMPTY;

        this.projectedPlayerUuid = null;
        this.projectedPlayerName = "";
        this.projectedCyberwareSnapshot = new CompoundTag();

        this.projectedEntityTypeId = "";
        this.projectedEntityName = "";
        this.projectedEntityNbt = new CompoundTag();

        if (this.mode == ProjectionMode.ITEM && tag.contains(NBT_ITEM, Tag.TAG_COMPOUND)) {
            CompoundTag itemTag = tag.getCompound(NBT_ITEM);
            this.projectedStack = ItemStack.parse(registries, itemTag).orElse(ItemStack.EMPTY);
            if (this.projectedStack.isEmpty()) this.mode = ProjectionMode.NONE;
        }

        if (this.mode == ProjectionMode.PLAYER && tag.contains(NBT_PLAYER_UUID, Tag.TAG_STRING)) {
            try {
                this.projectedPlayerUuid = UUID.fromString(tag.getString(NBT_PLAYER_UUID));
            } catch (IllegalArgumentException ignored) {
                this.projectedPlayerUuid = null;
                this.mode = ProjectionMode.NONE;
            }

            this.projectedPlayerName = tag.contains(NBT_PLAYER_NAME, Tag.TAG_STRING)
                    ? tag.getString(NBT_PLAYER_NAME)
                    : "";

            if (tag.contains(NBT_CYBERWARE_SNAPSHOT, Tag.TAG_COMPOUND)) {
                this.projectedCyberwareSnapshot = tag.getCompound(NBT_CYBERWARE_SNAPSHOT).copy();
            }
        }

        if (this.mode == ProjectionMode.ENTITY) {
            this.projectedEntityTypeId = tag.contains(NBT_ENTITY_TYPE, Tag.TAG_STRING) ? tag.getString(NBT_ENTITY_TYPE) : "";
            this.projectedEntityName = tag.contains(NBT_ENTITY_NAME, Tag.TAG_STRING) ? tag.getString(NBT_ENTITY_NAME) : "";
            if (tag.contains(NBT_ENTITY_NBT, Tag.TAG_COMPOUND)) {
                this.projectedEntityNbt = tag.getCompound(NBT_ENTITY_NBT).copy();
            } else {
                this.projectedEntityNbt = new CompoundTag();
            }

            if (this.projectedEntityTypeId.isBlank()) {
                this.mode = ProjectionMode.NONE;
            }
        }
    }

    private static ProjectionMode readMode(String raw) {
        if (raw == null || raw.isBlank()) return ProjectionMode.NONE;
        try {
            return ProjectionMode.valueOf(raw);
        } catch (IllegalArgumentException ignored) {
            return ProjectionMode.NONE;
        }
    }

    // ===== Client sync =====

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);

        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            loadAdditional(tag, registries);
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }
}
