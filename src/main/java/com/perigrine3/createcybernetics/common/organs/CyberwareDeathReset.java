package com.perigrine3.createcybernetics.common.organs;

import com.perigrine3.createcybernetics.ConfigValues;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.client.skin.CybereyeOverlayHandler;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.surgery.DefaultOrgans;
import com.perigrine3.createcybernetics.common.surgery.RobosurgeonSlotMap;
import com.perigrine3.createcybernetics.compat.corpse.CorpseCompat;
import com.perigrine3.createcybernetics.entity.ModEntities;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.cyberware.ArmCannonItem;
import com.perigrine3.createcybernetics.item.cyberware.SpinalInjectorItem;
import com.perigrine3.createcybernetics.item.generic.XPCapsuleItem;
import com.perigrine3.createcybernetics.network.payload.CybereyeIrisSyncS2CPayload;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class CyberwareDeathReset {

    private static final float MOB_CYBERWARE_DROP_CHANCE = 0.15f;
    private static final float SMASHER_CYBERWARE_DROP_CHANCE = 0.75f;

    private CyberwareDeathReset() {}

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        Player newPlayer = event.getEntity();
        if (newPlayer.level().isClientSide) return;

        Player original = event.getOriginal();

        copyCybereyeCfg(original, newPlayer);

        PlayerCyberwareData newData = newPlayer.getData(ModAttachments.CYBERWARE);
        if (newData == null) return;

        if (ConfigValues.KEEP_CYBERWARE) {
            PlayerCyberwareData oldData = original.getData(ModAttachments.CYBERWARE);
            if (oldData == null) return;

            HolderLookup.Provider provider = newPlayer.registryAccess();
            CompoundTag copied = oldData.serializeNBT(provider);
            newData.deserializeNBT(copied, provider);
            reapplyInstalledCyberwareHooks(newPlayer instanceof ServerPlayer sp ? sp : null, newData);

            newData.setDirty();
            newPlayer.syncData(ModAttachments.CYBERWARE);
            return;
        }

        newData.resetToDefaultOrgans();
        newData.setDirty();
        newPlayer.syncData(ModAttachments.CYBERWARE);
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        if (!data.hasAnyInSlots(CyberwareSlot.BRAIN)) {
            data.resetToDefaultOrgans();
            data.setDirty();
        }

        player.syncData(ModAttachments.CYBERWARE);

        if (player instanceof ServerPlayer sp) {
            syncCybereyeCfgToClients(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.isCanceled()) return;

        LivingEntity living = event.getEntity();
        if (living.level().isClientSide) return;

        if (living instanceof ServerPlayer player) {
            handlePlayerDeath(player);
            return;
        }

        if (dropsScavengedCyberware(living)) {
            dropInstalledCyberware(living, true);
            return;
        }

        if (dropsFreshCyberware(living)) {
            dropInstalledCyberware(living, false);
        }
    }

    private static void handlePlayerDeath(ServerPlayer player) {
        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;
        if (ConfigValues.KEEP_CYBERWARE) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        if (CorpseCompat.capturePlayerDeathForCorpse(player)) {
            CorpseCompat.syncPendingCorpseVisualSnapshotOnDeath(player);
            return;
        }

        boolean hadCorticalStack = hasCorticalStackInstalled(player);
        int xpPoints = hadCorticalStack ? getTotalXpPoints(player) : 0;
        boolean capsuleDropped = false;

        HolderLookup.Provider provider = player.registryAccess();

        for (CyberwareSlot slot : CyberwareSlot.values()) {
            int mappedSize = RobosurgeonSlotMap.mappedSize(slot);

            for (int i = 0; i < mappedSize; i++) {
                InstalledCyberware installed = data.get(slot, i);
                ItemStack installedStack = (installed != null && installed.getItem() != null)
                        ? installed.getItem()
                        : ItemStack.EMPTY;

                ItemStack def = DefaultOrgans.get(slot, i);
                if (def == null) def = ItemStack.EMPTY;

                ItemStack effective = !installedStack.isEmpty() ? installedStack : def;
                if (effective.isEmpty()) continue;

                if (ModItems.BRAINUPGRADES_CORTICALSTACK != null
                        && effective.is(ModItems.BRAINUPGRADES_CORTICALSTACK.get())) {
                    if (hadCorticalStack && !capsuleDropped) {
                        String ownerName = player.getGameProfile().getName();
                        ItemStack capsule = XPCapsuleItem.makeCapsule(ownerName, xpPoints);
                        player.spawnAtLocation(capsule);
                        capsuleDropped = true;
                    }

                    if (!shouldDropInstalledOnDeath(effective, slot)) continue;

                    ItemStack drop = sanitizeStoredInventoryNbtForDeathDrop(effective, provider);
                    player.spawnAtLocation(drop);
                    continue;
                }

                if (shouldDropInstalledOnDeath(effective, slot)) {
                    ItemStack drop = sanitizeStoredInventoryNbtForDeathDrop(effective, provider);
                    player.spawnAtLocation(drop);
                }
            }
        }

        dropChipwareShards(player, data);
        dropCyberdeckShards(player, data);
        dropSpinalInjectorInventory(player, data);
        dropArmCannonInventory(player, data);
        dropHeatEngineInventory(player, data);

        clearTransferredStoredInventories(data);
        data.setDirty();
        player.syncData(ModAttachments.CYBERWARE);
    }

    private static boolean dropsScavengedCyberware(LivingEntity entity) {
        return entity.getType() == ModEntities.CYBERZOMBIE.get()
                || entity.getType() == ModEntities.CYBERSKELETON.get();
    }

    private static boolean dropsFreshCyberware(LivingEntity entity) {
        return entity.getType() == ModEntities.SMASHER.get();
    }

    private static void dropInstalledCyberware(LivingEntity entity, boolean scavenged) {
        if (!entity.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) return;

        EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) return;

        boolean hadCorticalStack = hasCorticalStackInstalled(data);
        boolean capsuleDropped = false;
        float dropChance = getMobCyberwareDropChance(entity);

        for (CyberwareSlot slot : CyberwareSlot.values()) {
            InstalledCyberware[] arr = data.getAll().get(slot);
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware installed = arr[i];
                if (installed == null) continue;

                ItemStack installedStack = installed.getItem();
                if (installedStack == null || installedStack.isEmpty()) continue;
                if (!shouldDropInstalledOnDeath(installedStack, slot)) continue;
                if (entity.getRandom().nextFloat() >= dropChance) continue;

                if (ModItems.BRAINUPGRADES_CORTICALSTACK != null && installedStack.is(ModItems.BRAINUPGRADES_CORTICALSTACK.get())) {
                    if (hadCorticalStack && !capsuleDropped) {
                        int xpPoints = entity.getRandom().nextInt(50, 251);
                        ItemStack capsule = XPCapsuleItem.makeCorruptedCapsule(xpPoints);
                        entity.spawnAtLocation(capsule);
                        capsuleDropped = true;
                    }
                }

                ItemStack drop = scavenged ? toScavengedVariant(installedStack) : toFreshVariant(installedStack);
                if (drop.isEmpty()) continue;

                entity.spawnAtLocation(drop);
            }
        }
    }

    private static float getMobCyberwareDropChance(LivingEntity entity) {
        if (entity != null && entity.getType() == ModEntities.SMASHER.get()) {
            return SMASHER_CYBERWARE_DROP_CHANCE;
        }
        return MOB_CYBERWARE_DROP_CHANCE;
    }

    private static ItemStack toFreshVariant(ItemStack installedStack) {
        if (installedStack == null || installedStack.isEmpty()) return ItemStack.EMPTY;

        ItemStack out = installedStack.copy();
        out.setCount(1);
        return out;
    }

    private static ItemStack toScavengedVariant(ItemStack installedStack) {
        if (installedStack == null || installedStack.isEmpty()) return ItemStack.EMPTY;

        Item installedItem = installedStack.getItem();
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(installedItem);
        if (key == null) return ItemStack.EMPTY;
        if (!CreateCybernetics.MODID.equals(key.getNamespace())) return ItemStack.EMPTY;

        String path = key.getPath();
        if (path == null || path.isBlank()) return ItemStack.EMPTY;

        if (path.startsWith("scavenged_")) {
            ItemStack out = installedStack.copy();
            out.setCount(1);
            return out;
        }

        int firstUnderscore = path.indexOf('_');
        if (firstUnderscore < 0 || firstUnderscore + 1 >= path.length()) return ItemStack.EMPTY;

        String suffix = path.substring(firstUnderscore + 1);
        String scavengedPath = "scavenged_" + suffix;

        Item scavengedItem = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, scavengedPath));
        if (scavengedItem == null) return ItemStack.EMPTY;
        if (scavengedItem == net.minecraft.world.item.Items.AIR) return ItemStack.EMPTY;

        return new ItemStack(scavengedItem);
    }

    private static void copyCybereyeCfg(Player from, Player to) {
        if (from == null || to == null) return;

        CompoundTag oldRoot = from.getPersistentData().getCompound(CybereyeOverlayHandler.NBT_ROOT);
        if (oldRoot == null || oldRoot.isEmpty()) return;

        to.getPersistentData().put(CybereyeOverlayHandler.NBT_ROOT, oldRoot.copy());
    }

    private static void syncCybereyeCfgToClients(ServerPlayer player) {
        CompoundTag root = player.getPersistentData().getCompound(CybereyeOverlayHandler.NBT_ROOT);
        if (root == null || root.isEmpty()) return;

        CompoundTag left = root.getCompound(CybereyeOverlayHandler.NBT_LEFT);
        CompoundTag right = root.getCompound(CybereyeOverlayHandler.NBT_RIGHT);

        int lx = left.getInt(CybereyeOverlayHandler.NBT_X);
        int ly = left.getInt(CybereyeOverlayHandler.NBT_Y);
        int lv = left.getInt(CybereyeOverlayHandler.NBT_VARIANT);

        int rx = right.getInt(CybereyeOverlayHandler.NBT_X);
        int ry = right.getInt(CybereyeOverlayHandler.NBT_Y);
        int rv = right.getInt(CybereyeOverlayHandler.NBT_VARIANT);

        CybereyeIrisSyncS2CPayload out = new CybereyeIrisSyncS2CPayload(player.getUUID(), lx, ly, lv, rx, ry, rv);

        PacketDistributor.sendToPlayer(player, out);
        PacketDistributor.sendToPlayersTrackingEntity(player, out);
    }

    private static void dropChipwareShards(ServerPlayer player, PlayerCyberwareData data) {
        for (int i = 0; i < PlayerCyberwareData.CHIPWARE_SLOT_COUNT; i++) {
            ItemStack st = data.getChipwareStack(i);
            if (st == null || st.isEmpty()) continue;
            if (!st.is(ModTags.Items.DATA_SHARDS)) continue;

            ItemStack drop = st.copy();
            drop.setCount(1);

            player.spawnAtLocation(drop);
            data.setChipwareStack(i, ItemStack.EMPTY);
        }
    }

    private static void dropCyberdeckShards(ServerPlayer player, PlayerCyberwareData data) {
        for (int i = 0; i < PlayerCyberwareData.CYBERDECK_SLOT_COUNT; i++) {
            ItemStack st = data.getCyberdeckStack(i);
            if (st == null || st.isEmpty()) continue;
            if (!st.is(ModTags.Items.QUICKHACK_SHARDS)) continue;

            ItemStack drop = st.copy();
            drop.setCount(1);

            player.spawnAtLocation(drop);
            data.setCyberdeckStack(i, ItemStack.EMPTY);
        }
    }

    private static void dropSpinalInjectorInventory(ServerPlayer player, PlayerCyberwareData data) {
        for (int i = 0; i < SpinalInjectorItem.SLOT_COUNT; i++) {
            ItemStack st = data.getSpinalInjectorStack(i);
            if (st == null || st.isEmpty()) continue;

            player.spawnAtLocation(st.copy());
            data.setSpinalInjectorStack(i, ItemStack.EMPTY);
        }
    }

    private static void dropArmCannonInventory(ServerPlayer player, PlayerCyberwareData data) {
        boolean droppedAny = false;

        for (int i = 0; i < ArmCannonItem.SLOT_COUNT; i++) {
            ItemStack st = data.getArmCannonStack(i);
            if (st == null || st.isEmpty()) continue;

            player.spawnAtLocation(st.copy());
            data.setArmCannonStack(i, ItemStack.EMPTY);
            droppedAny = true;
        }

        if (droppedAny) {
            clearInstalledArmCannonNbt(player);
            return;
        }

        ItemStack installedArmCannon = getInstalledArmCannonStack(player);
        if (installedArmCannon.isEmpty()) return;

        SimpleContainer temp = new SimpleContainer(ArmCannonItem.SLOT_COUNT);
        ArmCannonItem.loadFromInstalledStack(installedArmCannon, player.registryAccess(), temp);

        for (int i = 0; i < ArmCannonItem.SLOT_COUNT; i++) {
            ItemStack st = temp.getItem(i);
            if (st == null || st.isEmpty()) continue;

            player.spawnAtLocation(st.copy());
            temp.setItem(i, ItemStack.EMPTY);
        }

        ArmCannonItem.saveIntoInstalledStack(installedArmCannon, player.registryAccess(), temp);

        for (int i = 0; i < ArmCannonItem.SLOT_COUNT; i++) {
            data.setArmCannonStack(i, ItemStack.EMPTY);
        }
    }

    private static void dropHeatEngineInventory(ServerPlayer player, PlayerCyberwareData data) {
        for (int i = 0; i < PlayerCyberwareData.HEAT_ENGINE_SLOT_COUNT; i++) {
            ItemStack st = data.getHeatEngineStack(i);
            if (st == null || st.isEmpty()) continue;

            player.spawnAtLocation(st.copy());
            data.setHeatEngineStack(i, ItemStack.EMPTY);
        }
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

                if (st.is(ModItems.ARMUPGRADES_ARMCANNON.get())) {
                    return st;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    private static void clearInstalledArmCannonNbt(ServerPlayer player) {
        ItemStack installedArmCannon = getInstalledArmCannonStack(player);
        if (installedArmCannon.isEmpty()) return;

        SimpleContainer empty = new SimpleContainer(ArmCannonItem.SLOT_COUNT);
        ArmCannonItem.saveIntoInstalledStack(installedArmCannon, player.registryAccess(), empty);
    }

    private static ItemStack sanitizeStoredInventoryNbtForDeathDrop(ItemStack stack, HolderLookup.Provider provider) {
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

    private static void clearTransferredStoredInventories(PlayerCyberwareData data) {
        if (data == null) return;

        data.clearChipwareInventory();
        data.clearCyberdeckInventory();
        data.clearSpinalInjectorInventory();
        data.clearArmCannonInventory();

        for (int i = 0; i < PlayerCyberwareData.HEAT_ENGINE_SLOT_COUNT; i++) {
            data.setHeatEngineStack(i, ItemStack.EMPTY);
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

                if (ModItems.BRAINUPGRADES_CORTICALSTACK != null) {
                    if (st.is(ModItems.BRAINUPGRADES_CORTICALSTACK.get())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean hasCorticalStackInstalled(EntityCyberwareData data) {
        if (data == null) return false;

        for (CyberwareSlot slot : CyberwareSlot.values()) {
            InstalledCyberware[] arr = data.getAll().get(slot);
            if (arr == null) continue;

            for (InstalledCyberware inst : arr) {
                if (inst == null) continue;
                ItemStack st = inst.getItem();
                if (st == null || st.isEmpty()) continue;

                if (ModItems.BRAINUPGRADES_CORTICALSTACK != null && st.is(ModItems.BRAINUPGRADES_CORTICALSTACK.get())) {
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

    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide) return;
        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;
        if (ConfigValues.KEEP_CYBERWARE) return;

        if (hasCorticalStackInstalled(player)) {
            event.setDroppedExperience(0);
        }
    }

    private static void reapplyInstalledCyberwareHooks(ServerPlayer player, PlayerCyberwareData data) {
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            InstalledCyberware[] arr = data.getAll().get(slot);
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware inst = arr[i];
                if (inst == null) continue;

                ItemStack st = inst.getItem();
                if (st == null || st.isEmpty()) continue;
                if (!data.isEnabled(slot, i)) continue;

                if (st.getItem() instanceof ICyberwareItem cw) {
                    cw.onInstalled(player);
                }
            }
        }
    }
}