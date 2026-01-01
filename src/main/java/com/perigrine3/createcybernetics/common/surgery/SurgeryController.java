package com.perigrine3.createcybernetics.common.surgery;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.block.entity.RobosurgeonBlockEntity;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.damage.ModDamageSources;
import com.perigrine3.createcybernetics.event.custom.CyberwareSurgeryEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class SurgeryController {

    private SurgeryController() {}

    public static void performSurgery(Player player, RobosurgeonBlockEntity surgeon) {
        performSurgery(player, surgeon, surgeon.staged, surgeon.markedForRemoval);
    }

    public static void performSurgery(Player player, RobosurgeonBlockEntity surgeon, boolean[] staged, boolean[] markedForRemoval) {
        if (player.level().isClientSide) return;

        surgeon.beginSurgery();

        try {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            boolean didWork = false;
            int installs = 0;
            int removals = 0;

            List<CyberwareSurgeryEvent.Change> installedChanges = new ArrayList<>();
            List<CyberwareSurgeryEvent.Change> removedChanges = new ArrayList<>();

            for (CyberwareSlot slot : CyberwareSlot.values()) {
                for (int i = 0; i < slot.size; i++) {

                    int invIndex = RobosurgeonSlotMap.toInventoryIndex(slot, i);
                    if (invIndex < 0 || invIndex >= surgeon.inventory.getSlots()) {
                        continue;
                    }

                    ItemStack stackInGui = surgeon.inventory.getStackInSlot(invIndex);

                    boolean isStaged = staged != null && invIndex < staged.length && staged[invIndex];
                    boolean isMarked = markedForRemoval != null && invIndex < markedForRemoval.length && markedForRemoval[invIndex];

                    boolean willRemove = isMarked;
                    boolean willInstall = isStaged && !stackInGui.isEmpty();

                    // --- REMOVAL PHASE ---
                    if (willRemove) {
                        InstalledCyberware removed = data.remove(slot, i);

                        if (removed != null && removed.getItem() != null && !removed.getItem().isEmpty()) {
                            didWork = true;
                            removals++;
                            removedChanges.add(new CyberwareSurgeryEvent.Change(slot, i, removed.getItem().copy()));

                            if (removed.getItem().getItem() instanceof ICyberwareItem cw) {
                                cw.onRemoved(player);
                            }

                            ItemStack giveBack = removed.getItem().copy();
                            if (!player.getInventory().add(giveBack)) {
                                player.drop(giveBack, false);
                            }
                        }

                        if (!willInstall) {
                            surgeon.inventory.setStackInSlot(invIndex, ItemStack.EMPTY);
                            surgeon.installed[invIndex] = false;
                            surgeon.staged[invIndex] = false;
                            surgeon.markedForRemoval[invIndex] = false;
                            continue;
                        }
                    }

                    // --- INSTALL PHASE ---
                    if (!willInstall) {
                        if (isStaged && stackInGui.isEmpty()) {
                            surgeon.staged[invIndex] = false;
                        }
                        continue;
                    }

                    if (stackInGui.getItem() instanceof ICyberwareItem cw) {
                        int cap = Math.max(1, cw.maxStacksPerSlotType(stackInGui, slot));
                        int currentlyInstalledSame = countInstalledSameInSlotType(data, slot, stackInGui);
                        int plannedRemovedSame = countPlannedRemovalsSameInSlotType(data, slot, surgeon, markedForRemoval, stackInGui);
                        int effectiveSameAfterAllRemovals = currentlyInstalledSame - plannedRemovedSame;

                        if (effectiveSameAfterAllRemovals >= cap) {
                            ItemStack giveBack = stackInGui.copy();
                            if (!player.getInventory().add(giveBack)) {
                                player.drop(giveBack, false);
                            }

                            surgeon.inventory.setStackInSlot(invIndex, ItemStack.EMPTY);
                            surgeon.installed[invIndex] = false;
                            surgeon.staged[invIndex] = false;
                            surgeon.markedForRemoval[invIndex] = false;
                            continue;
                        }
                    }

                    if (stackInGui.getItem() instanceof ICyberwareItem cwReq) {
                        Set<Item> required = cwReq.requiresCyberware(stackInGui, slot);
                        if (required != null && !required.isEmpty() && !hasAnyRequiredCyberware(data, surgeon, staged, required, slot)) {
                            ItemStack giveBack = stackInGui.copy();
                            if (!player.getInventory().add(giveBack)) {
                                player.drop(giveBack, false);
                            }

                            surgeon.inventory.setStackInSlot(invIndex, ItemStack.EMPTY);
                            surgeon.installed[invIndex] = false;
                            surgeon.staged[invIndex] = false;
                            surgeon.markedForRemoval[invIndex] = false;
                            continue;
                        }
                    }

                    // --- INCOMPATIBILITY CHECK ---
                    if (stackInGui.getItem() instanceof ICyberwareItem cwInc) {
                        if (hasAnyIncompatibleCyberware(data, surgeon, staged, markedForRemoval, stackInGui, slot, i)) {
                            ItemStack giveBack = stackInGui.copy();
                            if (!player.getInventory().add(giveBack)) {
                                player.drop(giveBack, false);
                            }

                            surgeon.inventory.setStackInSlot(invIndex, ItemStack.EMPTY);
                            surgeon.installed[invIndex] = false;
                            surgeon.staged[invIndex] = false;
                            surgeon.markedForRemoval[invIndex] = false;
                            continue;
                        }
                    }

                    // --- SUCCESSFUL INSTALL ---
                    int humanityCost = 0;
                    if (stackInGui.getItem() instanceof ICyberwareItem cyberwareItem) {
                        humanityCost = cyberwareItem.getHumanityCost();
                    }

                    InstalledCyberware installed = new InstalledCyberware(stackInGui.copy(), slot, i, humanityCost);
                    installed.setPowered(true);
                    data.set(slot, i, installed);

                    didWork = true;
                    installs++;
                    installedChanges.add(new CyberwareSurgeryEvent.Change(slot, i, installed.getItem().copy()));

                    if (stackInGui.getItem() instanceof ICyberwareItem cw) {
                        cw.onInstalled(player, installed.getItem());
                        surgeon.inventory.setStackInSlot(invIndex, installed.getItem().copy());
                    }

                    surgeon.installed[invIndex] = true;
                    surgeon.staged[invIndex] = false;
                    surgeon.markedForRemoval[invIndex] = false;
                }
            }

            data.recomputeHumanityBaseFromInstalled();

            // --- POST SURGERY EVENTS + DAMAGE ---
            if (didWork) {
                if (player instanceof ServerPlayer sp) {
                    NeoForge.EVENT_BUS.post(new CyberwareSurgeryEvent(sp, installs, removals, installedChanges, removedChanges));
                }

                float damage = installs * 4.0F + removals * 6.0F;
                if (damage > 0.0F) {
                    player.hurt(ModDamageSources.cyberwareSurgery(player.level(), player, null), damage);
                }
            }

            // --- SAVE + SYNC ---
            data.setDirty();
            player.syncData(ModAttachments.CYBERWARE);

            surgeon.clearSlotStates();
            surgeon.setChanged();
            player.level().sendBlockUpdated(surgeon.getBlockPos(), surgeon.getBlockState(), surgeon.getBlockState(), 3);

        } finally {
            surgeon.endSurgery();
        }
    }

    // --- Existing utility methods below remain untouched ---

    private static boolean hasAnyRequiredCyberware(PlayerCyberwareData data, RobosurgeonBlockEntity surgeon, boolean[] staged, Set<Item> required, CyberwareSlot installSlotType) {
        if (required == null || required.isEmpty()) return true;

        if (staged != null) {
            int mappedSize = RobosurgeonSlotMap.mappedSize(installSlotType);
            for (int i = 0; i < mappedSize; i++) {
                int invIndex = RobosurgeonSlotMap.toInventoryIndex(installSlotType, i);
                if (invIndex < 0 || invIndex >= surgeon.inventory.getSlots()) continue;
                if (invIndex >= staged.length) continue;

                if (!staged[invIndex]) continue;

                ItemStack st = surgeon.inventory.getStackInSlot(invIndex);
                if (st.isEmpty()) continue;

                if (required.contains(st.getItem())) return true;
            }
        }

        int mappedSize = RobosurgeonSlotMap.mappedSize(installSlotType);
        for (int i = 0; i < mappedSize; i++) {
            InstalledCyberware inst = data.get(installSlotType, i);
            if (inst == null || inst.getItem() == null || inst.getItem().isEmpty()) continue;
            if (required.contains(inst.getItem().getItem())) return true;
        }

        return false;
    }

    private static boolean hasAnyIncompatibleCyberware(
            PlayerCyberwareData data,
            RobosurgeonBlockEntity surgeon,
            boolean[] staged,
            boolean[] markedForRemoval,
            ItemStack installingStack,
            CyberwareSlot installingSlotType,
            int installingIndex
    ) {
        if (!(installingStack.getItem() instanceof ICyberwareItem installingItem)) return false;

        Set<Item> badItems = installingItem.incompatibleCyberware(installingStack, installingSlotType);
        Set<TagKey<Item>> badTags = installingItem.incompatibleCyberwareTags(installingStack, installingSlotType);

        if ((badItems == null || badItems.isEmpty()) && (badTags == null || badTags.isEmpty())) {
            badItems = Set.of();
            badTags = Set.of();
        }

        int currentInvIndex = RobosurgeonSlotMap.toInventoryIndex(installingSlotType, installingIndex);

        for (CyberwareSlot otherSlot : CyberwareSlot.values()) {
            for (int otherIndex = 0; otherIndex < otherSlot.size; otherIndex++) {

                int otherInvIndex = RobosurgeonSlotMap.toInventoryIndex(otherSlot, otherIndex);
                if (otherInvIndex < 0 || otherInvIndex >= surgeon.inventory.getSlots()) continue;

                if (otherInvIndex == currentInvIndex) continue;

                if (markedForRemoval != null
                        && otherInvIndex < markedForRemoval.length
                        && markedForRemoval[otherInvIndex]) {
                    continue;
                }

                ItemStack otherStack = ItemStack.EMPTY;

                boolean otherIsStaged = staged != null
                        && otherInvIndex < staged.length
                        && staged[otherInvIndex];

                if (otherIsStaged) {
                    otherStack = surgeon.inventory.getStackInSlot(otherInvIndex);
                } else {
                    InstalledCyberware inst = data.get(otherSlot, otherIndex);
                    if (inst != null && inst.getItem() != null) otherStack = inst.getItem();
                }

                if (otherStack == null || otherStack.isEmpty()) continue;

                if (badItems.contains(otherStack.getItem())) return true;

                for (TagKey<Item> tag : badTags) {
                    if (tag != null && otherStack.is(tag)) return true;
                }

                if (otherStack.getItem() instanceof ICyberwareItem otherCyberware) {
                    Set<Item> otherBadItems = otherCyberware.incompatibleCyberware(otherStack, otherSlot);
                    if (otherBadItems != null && otherBadItems.contains(installingStack.getItem())) return true;

                    Set<TagKey<Item>> otherBadTags = otherCyberware.incompatibleCyberwareTags(otherStack, otherSlot);
                    if (otherBadTags != null) {
                        for (TagKey<Item> tag : otherBadTags) {
                            if (tag != null && installingStack.is(tag)) return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private static int countInstalledSameInSlotType(PlayerCyberwareData data, CyberwareSlot slotType, ItemStack needle) {
        int count = 0;
        for (int i = 0; i < slotType.size; i++) {
            InstalledCyberware inst = data.get(slotType, i);
            if (inst == null || inst.getItem() == null || inst.getItem().isEmpty()) continue;
            if (ItemStack.isSameItemSameComponents(inst.getItem(), needle)) {
                count++;
            }
        }
        return count;
    }

    private static int countPlannedRemovalsSameInSlotType(PlayerCyberwareData data, CyberwareSlot slotType, RobosurgeonBlockEntity surgeon, boolean[] markedForRemoval, ItemStack needle) {
        if (markedForRemoval == null) return 0;
        int count = 0;
        for (int i = 0; i < slotType.size; i++) {
            int invIndex = RobosurgeonSlotMap.toInventoryIndex(slotType, i);
            if (invIndex < 0 || invIndex >= surgeon.inventory.getSlots()) continue;
            if (invIndex >= markedForRemoval.length) continue;
            if (!markedForRemoval[invIndex]) continue;
            InstalledCyberware inst = data.get(slotType, i);
            if (inst == null || inst.getItem() == null || inst.getItem().isEmpty()) continue;
            if (ItemStack.isSameItemSameComponents(inst.getItem(), needle)) {
                count++;
            }
        }
        return count;
    }
}
