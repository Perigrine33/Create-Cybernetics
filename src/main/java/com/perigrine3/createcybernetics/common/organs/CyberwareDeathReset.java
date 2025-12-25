package com.perigrine3.createcybernetics.common.organs;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.surgery.DefaultOrgans;
import com.perigrine3.createcybernetics.common.surgery.RobosurgeonSlotMap;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.generic.XPCapsuleItem;
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

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class CyberwareDeathReset {

    private CyberwareDeathReset() {}

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        PlayerCyberwareData newData = player.getData(ModAttachments.CYBERWARE);
        if (newData == null) return;

        newData.resetToDefaultOrgans();
        newData.setDirty();
        player.syncData(ModAttachments.CYBERWARE);
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
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.isCanceled()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

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

                if (shouldDropInstalledOnDeath(effective, slot)) {
                    player.spawnAtLocation(effective.copy());
                }
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

                if (st.is(ModItems.BRAINUPGRADES_CORTICALSTACK.get())) {
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

        if (hasCorticalStackInstalled(player)) {
            event.setDroppedExperience(0);
        }
    }
}
