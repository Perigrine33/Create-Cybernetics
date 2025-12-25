package com.perigrine3.createcybernetics.common.surgery;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.block.entity.RobosurgeonBlockEntity;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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

                    if (willRemove) {
                        InstalledCyberware removed = data.remove(slot, i);

                        if (removed != null && removed.getItem() != null && !removed.getItem().isEmpty()) {
                            if (removed.getItem().getItem() instanceof ICyberwareItem cw) {
                                cw.onRemoved(player);
                            }
                        }

                        if (removed != null && removed.getItem() != null && !removed.getItem().isEmpty()) {
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

                    int humanityCost = 0;
                    if (stackInGui.getItem() instanceof ICyberwareItem cyberwareItem) {
                        humanityCost = cyberwareItem.getHumanityCost();
                    }

                    InstalledCyberware installed = new InstalledCyberware(stackInGui.copy(), slot, i, humanityCost);
                    installed.setPowered(true);
                    data.set(slot, i, installed);

                    if (stackInGui.getItem() instanceof ICyberwareItem cw) {
                        cw.onInstalled(player, installed.getItem());

                        surgeon.inventory.setStackInSlot(invIndex, installed.getItem().copy());
                    }

                    surgeon.installed[invIndex] = true;
                    surgeon.staged[invIndex] = false;
                    surgeon.markedForRemoval[invIndex] = false;
                }
            }

            data.setDirty();
            player.syncData(ModAttachments.CYBERWARE);

            surgeon.clearSlotStates();
            surgeon.setChanged();

            player.level().sendBlockUpdated(surgeon.getBlockPos(), surgeon.getBlockState(), surgeon.getBlockState(), 3);

        } finally {
            surgeon.endSurgery();
        }
    }

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
