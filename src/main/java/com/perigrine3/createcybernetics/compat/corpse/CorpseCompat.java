package com.perigrine3.createcybernetics.compat.corpse;

import com.perigrine3.createcybernetics.ConfigValues;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.surgery.DefaultOrgans;
import com.perigrine3.createcybernetics.common.surgery.RobosurgeonSlotMap;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.cyberware.ArmCannonItem;
import com.perigrine3.createcybernetics.item.cyberware.SpinalInjectorItem;
import com.perigrine3.createcybernetics.item.generic.XPCapsuleItem;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CorpseCompat {

    public static final String CORPSE_MODID = "corpse";
    private static final String CORPSE_ENTITY_CLASS = "de.maxhenkel.corpse.entities.CorpseEntity";

    private static final String ROOT_TAG = CreateCybernetics.MODID + "_corpse_cyberware";
    private static final String ITEMS_TAG = "Items";
    private static final String DATA_TAG = "CorpseCyberwareData";

    public static final int CYBERWARE_SLOT_COUNT = 80;

    private static final Map<UUID, PendingCorpseData> PENDING = new ConcurrentHashMap<>();

    private static volatile Class<?> corpseEntityClass;
    private static volatile boolean triedResolveCorpseClass = false;
    private static volatile Method corpseGetCorpseUuidMethod;
    private static volatile boolean triedResolveGetCorpseUuidMethod = false;

    private CorpseCompat() {
    }

    public static void init() {
        if (isLoaded()) {
            NeoForge.EVENT_BUS.register(new CorpseCompat());
        }
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(CORPSE_MODID);
    }

    public static boolean capturePlayerDeathForCorpse(ServerPlayer player) {
        if (!isLoaded()) return false;
        if (player.level().isClientSide) return false;
        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return false;
        if (ConfigValues.KEEP_CYBERWARE) return false;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        HolderLookup.Provider provider = player.registryAccess();

        CorpseCyberwareData corpseData = new CorpseCyberwareData();
        corpseData.captureFromPlayer(player, provider);

        NonNullList<ItemStack> stored = buildStoredCyberwareList(player, data, provider);

        PENDING.put(player.getUUID(), new PendingCorpseData(corpseData, stored));
        return true;
    }

    public static void syncPendingCorpseVisualSnapshotOnDeath(ServerPlayer player) {
        if (player == null) return;
        if (player.level().isClientSide) return;

        PendingCorpseData pending = PENDING.get(player.getUUID());
        if (pending == null) return;
        if (pending.data() == null || pending.data().isEmpty()) return;

        CorpseVisualSnapshotPayload payload =
                new CorpseVisualSnapshotPayload(player.getUUID(), pending.data().serializeNBT());

        PacketDistributor.sendToPlayer(player, payload);
        PacketDistributor.sendToPlayersTrackingEntity(player, payload);
    }

    @SubscribeEvent
    public void onCorpseJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;

        Entity entity = event.getEntity();
        if (!isCorpseEntity(entity)) return;

        tryApplyPendingToCorpse(entity);
        syncCorpseVisualSnapshot(entity);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide()) return;

        for (Entity entity : player.serverLevel().getAllEntities()) {
            if (!isCorpseEntity(entity)) continue;
            ensureCorpseCyberwareLoaded(entity);
            syncCorpseVisualSnapshotTo(player, entity);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        Entity target = event.getTarget();
        if (!isCorpseEntity(target)) return;

        ensureCorpseCyberwareLoaded(target);
        syncCorpseVisualSnapshotTo(player, target);
    }

    public static void ensureCorpseCyberwareLoaded(Entity corpseEntity) {
        if (corpseEntity == null) return;
        if (!isCorpseEntity(corpseEntity)) return;

        if (!hasStoredCorpseCyberwareData(corpseEntity)) {
            tryApplyPendingToCorpse(corpseEntity);
        }
    }

    private static void tryApplyPendingToCorpse(Entity corpseEntity) {
        if (corpseEntity == null) return;
        if (!isCorpseEntity(corpseEntity)) return;

        Optional<UUID> corpseOwnerUuid = getCorpseOwnerUuid(corpseEntity);
        if (corpseOwnerUuid.isEmpty()) return;

        UUID ownerUuid = corpseOwnerUuid.get();
        PendingCorpseData pending = PENDING.get(ownerUuid);
        if (pending == null) return;

        writeCorpseCyberwareItems(corpseEntity, pending.items());
        setStoredCorpseCyberwareData(corpseEntity, pending.data());

        if (hasStoredCorpseCyberwareData(corpseEntity)) {
            PENDING.remove(ownerUuid);
        }
    }

    private static void syncCorpseVisualSnapshot(Entity corpseEntity) {
        if (corpseEntity == null) return;
        if (corpseEntity.level().isClientSide()) return;
        if (!isCorpseEntity(corpseEntity)) return;

        CorpseCyberwareData data = getStoredCorpseCyberwareData(corpseEntity);
        if (data.isEmpty()) return;

        PacketDistributor.sendToPlayersTrackingEntity(
                corpseEntity,
                new CorpseVisualSnapshotPayload(corpseEntity.getUUID(), data.serializeNBT())
        );
    }

    private static void syncCorpseVisualSnapshotTo(ServerPlayer player, Entity corpseEntity) {
        if (player == null || corpseEntity == null) return;
        if (!isCorpseEntity(corpseEntity)) return;

        CorpseCyberwareData data = getStoredCorpseCyberwareData(corpseEntity);
        if (data.isEmpty()) return;

        PacketDistributor.sendToPlayer(
                player,
                new CorpseVisualSnapshotPayload(corpseEntity.getUUID(), data.serializeNBT())
        );
    }

    public static boolean hasCyberware(Entity corpseEntity) {
        for (ItemStack stack : getCorpseCyberwareItems(corpseEntity)) {
            if (!stack.isEmpty()) return true;
        }
        return false;
    }

    public static NonNullList<ItemStack> getCorpseCyberwareItems(Entity corpseEntity) {
        NonNullList<ItemStack> items = NonNullList.withSize(CYBERWARE_SLOT_COUNT, ItemStack.EMPTY);

        if (corpseEntity == null) {
            return items;
        }

        CompoundTag persistent = corpseEntity.getPersistentData();
        if (!persistent.contains(ROOT_TAG, Tag.TAG_COMPOUND)) {
            return items;
        }

        CompoundTag root = persistent.getCompound(ROOT_TAG);
        if (!root.contains(ITEMS_TAG, Tag.TAG_COMPOUND)) {
            return items;
        }

        ContainerHelper.loadAllItems(root.getCompound(ITEMS_TAG), items, corpseEntity.registryAccess());
        return items;
    }

    public static void writeCorpseCyberwareItems(Entity corpseEntity, NonNullList<ItemStack> items) {
        if (corpseEntity == null) return;

        CompoundTag persistent = corpseEntity.getPersistentData();
        CompoundTag root = persistent.contains(ROOT_TAG, Tag.TAG_COMPOUND)
                ? persistent.getCompound(ROOT_TAG)
                : new CompoundTag();

        CompoundTag itemsTag = new CompoundTag();
        ContainerHelper.saveAllItems(itemsTag, items, corpseEntity.registryAccess());

        root.put(ITEMS_TAG, itemsTag);
        persistent.put(ROOT_TAG, root);
    }

    public static boolean hasStoredCorpseCyberwareData(Entity corpseEntity) {
        if (corpseEntity == null) return false;

        CompoundTag persistent = corpseEntity.getPersistentData();
        if (!persistent.contains(ROOT_TAG, Tag.TAG_COMPOUND)) return false;

        CompoundTag root = persistent.getCompound(ROOT_TAG);
        return root.contains(DATA_TAG, Tag.TAG_COMPOUND)
                && !root.getCompound(DATA_TAG).isEmpty();
    }

    public static CorpseCyberwareData getStoredCorpseCyberwareData(Entity corpseEntity) {
        CorpseCyberwareData out = new CorpseCyberwareData();

        if (corpseEntity == null) {
            return out;
        }

        CompoundTag persistent = corpseEntity.getPersistentData();
        if (!persistent.contains(ROOT_TAG, Tag.TAG_COMPOUND)) {
            return out;
        }

        CompoundTag root = persistent.getCompound(ROOT_TAG);
        if (!root.contains(DATA_TAG, Tag.TAG_COMPOUND)) {
            return out;
        }

        out.deserializeNBT(root.getCompound(DATA_TAG));
        return out;
    }

    public static void setStoredCorpseCyberwareData(Entity corpseEntity, CorpseCyberwareData data) {
        if (corpseEntity == null || data == null) return;

        CompoundTag persistent = corpseEntity.getPersistentData();
        CompoundTag root = persistent.contains(ROOT_TAG, Tag.TAG_COMPOUND)
                ? persistent.getCompound(ROOT_TAG)
                : new CompoundTag();

        root.put(DATA_TAG, data.serializeNBT());
        persistent.put(ROOT_TAG, root);
    }

    private static NonNullList<ItemStack> buildStoredCyberwareList(ServerPlayer player, PlayerCyberwareData data, HolderLookup.Provider provider) {
        NonNullList<ItemStack> stored = NonNullList.withSize(CYBERWARE_SLOT_COUNT, ItemStack.EMPTY);

        boolean hadCorticalStack = hasCorticalStackInstalled(player);
        int xpPoints = hadCorticalStack ? getTotalXpPoints(player) : 0;
        boolean capsuleStored = false;

        for (CyberwareSlot slot : CyberwareSlot.values()) {
            int mappedSize = RobosurgeonSlotMap.mappedSize(slot);

            for (int i = 0; i < mappedSize; i++) {
                InstalledCyberware installed = data.get(slot, i);
                ItemStack installedStack = installed != null && installed.getItem() != null
                        ? installed.getItem()
                        : ItemStack.EMPTY;

                ItemStack def = DefaultOrgans.get(slot, i);
                if (def == null) def = ItemStack.EMPTY;

                ItemStack effective = !installedStack.isEmpty() ? installedStack : def;
                if (effective.isEmpty()) continue;
                if (!shouldDropInstalledOnDeath(effective, slot)) continue;

                if (ModItems.BRAINUPGRADES_CORTICALSTACK != null
                        && effective.is(ModItems.BRAINUPGRADES_CORTICALSTACK.get())
                        && hadCorticalStack
                        && !capsuleStored) {
                    ItemStack capsule = XPCapsuleItem.makeCapsule(player.getGameProfile().getName(), xpPoints);
                    putFirstEmpty(stored, capsule);
                    capsuleStored = true;
                }

                ItemStack sanitized = sanitizeStoredInventoryNbtForCorpse(effective, provider);
                putFirstEmpty(stored, sanitized);
            }
        }

        for (int i = 0; i < PlayerCyberwareData.CHIPWARE_SLOT_COUNT; i++) {
            ItemStack st = data.getChipwareStack(i);
            if (st == null || st.isEmpty()) continue;
            if (!st.is(ModTags.Items.DATA_SHARDS)) continue;

            ItemStack drop = st.copy();
            drop.setCount(1);
            putFirstEmpty(stored, drop);
        }

        for (int i = 0; i < PlayerCyberwareData.CYBERDECK_SLOT_COUNT; i++) {
            ItemStack st = data.getCyberdeckStack(i);
            if (st == null || st.isEmpty()) continue;
            if (!st.is(ModTags.Items.QUICKHACK_SHARDS)) continue;

            ItemStack drop = st.copy();
            drop.setCount(1);
            putFirstEmpty(stored, drop);
        }

        for (int i = 0; i < SpinalInjectorItem.SLOT_COUNT; i++) {
            ItemStack st = data.getSpinalInjectorStack(i);
            if (st == null || st.isEmpty()) continue;

            putFirstEmpty(stored, st.copy());
        }

        boolean addedArmCannonFromPlayerData = false;
        for (int i = 0; i < ArmCannonItem.SLOT_COUNT; i++) {
            ItemStack st = data.getArmCannonStack(i);
            if (st == null || st.isEmpty()) continue;

            putFirstEmpty(stored, st.copy());
            addedArmCannonFromPlayerData = true;
        }

        if (!addedArmCannonFromPlayerData) {
            ItemStack installedArmCannon = getInstalledArmCannonStack(player);
            if (!installedArmCannon.isEmpty()) {
                SimpleContainer temp = new SimpleContainer(ArmCannonItem.SLOT_COUNT);
                ArmCannonItem.loadFromInstalledStack(installedArmCannon, provider, temp);

                for (int i = 0; i < ArmCannonItem.SLOT_COUNT; i++) {
                    ItemStack st = temp.getItem(i);
                    if (st == null || st.isEmpty()) continue;

                    putFirstEmpty(stored, st.copy());
                }
            }
        }

        for (int i = 0; i < PlayerCyberwareData.HEAT_ENGINE_SLOT_COUNT; i++) {
            ItemStack st = data.getHeatEngineStack(i);
            if (st == null || st.isEmpty()) continue;

            putFirstEmpty(stored, st.copy());
        }

        return stored;
    }

    private static ItemStack getInstalledArmCannonStack(ServerPlayer player) {
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return ItemStack.EMPTY;

        for (CyberwareSlot slot : CyberwareSlot.values()) {
            InstalledCyberware[] arr = data.getAll().get(slot);
            if (arr == null) continue;

            for (InstalledCyberware inst : arr) {
                if (inst == null) continue;

                ItemStack st = inst.getItem();
                if (st == null || st.isEmpty()) continue;

                if (ModItems.ARMUPGRADES_ARMCANNON != null && st.is(ModItems.ARMUPGRADES_ARMCANNON.get())) {
                    return st;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack sanitizeStoredInventoryNbtForCorpse(ItemStack stack, HolderLookup.Provider provider) {
        if (stack == null || stack.isEmpty()) return ItemStack.EMPTY;

        ItemStack copy = stack.copy();
        copy.setCount(1);

        if (copy.is(ModItems.BONEUPGRADES_SPINALINJECTOR.get())) {
            SimpleContainer empty = new SimpleContainer(SpinalInjectorItem.SLOT_COUNT);
            int[] counts = new int[SpinalInjectorItem.SLOT_COUNT];
            SpinalInjectorItem.saveIntoInstalledStack(copy, provider, empty, counts);
            return copy;
        }

        if (copy.is(ModItems.ARMUPGRADES_ARMCANNON.get())) {
            SimpleContainer empty = new SimpleContainer(ArmCannonItem.SLOT_COUNT);
            ArmCannonItem.saveIntoInstalledStack(copy, provider, empty);
            return copy;
        }

        return copy;
    }

    private static void putFirstEmpty(NonNullList<ItemStack> items, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, stack.copy());
                return;
            }
        }
    }

    private static boolean shouldDropInstalledOnDeath(ItemStack installedStack, CyberwareSlot slot) {
        if (installedStack.isEmpty()) return false;
        if (installedStack.getItem() instanceof ICyberwareItem cw) {
            return cw.dropsOnDeath(installedStack, slot);
        }
        return true;
    }

    private static boolean hasCorticalStackInstalled(ServerPlayer player) {
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        for (CyberwareSlot slot : CyberwareSlot.values()) {
            InstalledCyberware[] arr = data.getAll().get(slot);
            if (arr == null) continue;

            for (InstalledCyberware inst : arr) {
                if (inst == null) continue;
                ItemStack st = inst.getItem();
                if (st == null || st.isEmpty()) continue;

                if (ModItems.BRAINUPGRADES_CORTICALSTACK != null
                        && st.is(ModItems.BRAINUPGRADES_CORTICALSTACK.get())) {
                    return true;
                }
            }
        }

        return false;
    }

    private static int totalXpForLevel(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int) (2.5 * level * level - 40.5 * level + 360);
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    private static int getTotalXpPoints(Player player) {
        int level = player.experienceLevel;
        int base = totalXpForLevel(level);
        int toNext = player.getXpNeededForNextLevel();
        int within = Math.round(player.experienceProgress * (float) toNext);
        return Math.max(0, base + within);
    }

    private static boolean isCorpseEntity(Entity entity) {
        if (entity == null || !isLoaded()) return false;

        Class<?> corpseClass = getCorpseEntityClass();
        return corpseClass != null && corpseClass.isInstance(entity);
    }

    public static boolean isCorpseEntityForCompat(Entity entity) {
        return isCorpseEntity(entity);
    }

    public static Optional<UUID> getCorpseOwnerUuidForCompat(Entity entity) {
        return getCorpseOwnerUuid(entity);
    }

    private static Optional<UUID> getCorpseOwnerUuid(Entity entity) {
        if (entity == null || !isCorpseEntity(entity)) {
            return Optional.empty();
        }

        try {
            Method method = getCorpseGetCorpseUuidMethod();
            if (method == null) {
                return Optional.empty();
            }

            Object result = method.invoke(entity);
            if (result instanceof Optional<?> optional) {
                Object value = optional.orElse(null);
                if (value instanceof UUID uuid) {
                    return Optional.of(uuid);
                }
            }
        } catch (Throwable ignored) {
        }

        return Optional.empty();
    }

    private static Class<?> getCorpseEntityClass() {
        if (triedResolveCorpseClass) {
            return corpseEntityClass;
        }

        triedResolveCorpseClass = true;

        try {
            corpseEntityClass = Class.forName(CORPSE_ENTITY_CLASS);
        } catch (Throwable ignored) {
            corpseEntityClass = null;
        }

        return corpseEntityClass;
    }

    private static Method getCorpseGetCorpseUuidMethod() {
        if (triedResolveGetCorpseUuidMethod) {
            return corpseGetCorpseUuidMethod;
        }

        triedResolveGetCorpseUuidMethod = true;

        try {
            Class<?> corpseClass = getCorpseEntityClass();
            if (corpseClass != null) {
                corpseGetCorpseUuidMethod = corpseClass.getMethod("getCorpseUUID");
            }
        } catch (Throwable ignored) {
            corpseGetCorpseUuidMethod = null;
        }

        return corpseGetCorpseUuidMethod;
    }

    private record PendingCorpseData(CorpseCyberwareData data, NonNullList<ItemStack> items) {
    }
}