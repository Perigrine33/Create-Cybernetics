package com.perigrine3.createcybernetics.common.organs;

import com.perigrine3.createcybernetics.ConfigValues;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.client.skin.CybereyeOverlayHandler;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.surgery.DefaultOrgans;
import com.perigrine3.createcybernetics.common.surgery.RobosurgeonSlotMap;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.generic.XPCapsuleItem;
import com.perigrine3.createcybernetics.network.payload.CybereyeIrisSyncS2CPayload;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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

    private CyberwareDeathReset() {}

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        Player newPlayer = event.getEntity();
        if (newPlayer.level().isClientSide) return;

        Player original = event.getOriginal();

        // ---- (A) Copy cybereye config NBT across death (server-side) ----
        // This is REQUIRED because a new Player entity instance is created on death.
        copyCybereyeCfg(original, newPlayer);

        // ---- (B) Existing cyberware attachment logic (unchanged behavior) ----
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

        // ---- (C) Re-sync cybereye config to the client after entity actually joins the world ----
        // This solves the real issue: persistentData is NOT automatically synced to the client.
        if (player instanceof ServerPlayer sp) {
            syncCybereyeCfgToClients(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.isCanceled()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;
        if (ConfigValues.KEEP_CYBERWARE) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        boolean hadCorticalStack = hasCorticalStackInstalled(player);
        int xpPoints = hadCorticalStack ? getTotalXpPoints(player) : 0;
        boolean capsuleDropped = false;

        for (CyberwareSlot slot : CyberwareSlot.values()) {
            int mappedSize = RobosurgeonSlotMap.mappedSize(slot);

            for (int i = 0; i < mappedSize; i++) {
                InstalledCyberware installed = data.get(slot, i);
                ItemStack installedStack = (installed != null && installed.getItem() != null) ? installed.getItem() : ItemStack.EMPTY;

                ItemStack def = DefaultOrgans.get(slot, i);
                if (def == null) def = ItemStack.EMPTY;

                ItemStack effective = !installedStack.isEmpty() ? installedStack : def;
                if (effective.isEmpty()) continue;

                if (ModItems.BRAINUPGRADES_CORTICALSTACK != null) {
                    if (effective.is(ModItems.BRAINUPGRADES_CORTICALSTACK.get())) {
                        if (hadCorticalStack && !capsuleDropped) {
                            String ownerName = player.getGameProfile().getName();
                            ItemStack capsule = XPCapsuleItem.makeCapsule(ownerName, xpPoints);
                            player.spawnAtLocation(capsule);
                            capsuleDropped = true;
                        }
                        if (!shouldDropInstalledOnDeath(effective, slot)) continue;
                        player.spawnAtLocation(effective.copy());
                        continue;
                    }
                }

                if (shouldDropInstalledOnDeath(effective, slot)) {
                    player.spawnAtLocation(effective.copy());
                }
            }
        }

        dropChipwareShards(player, data);

        data.setDirty();
        player.syncData(ModAttachments.CYBERWARE);
    }

    private static void copyCybereyeCfg(Player from, Player to) {
        if (from == null || to == null) return;

        CompoundTag oldRoot = from.getPersistentData().getCompound(CybereyeOverlayHandler.NBT_ROOT);
        if (oldRoot == null || oldRoot.isEmpty()) return;

        // deep copy to avoid sharing tag instances
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