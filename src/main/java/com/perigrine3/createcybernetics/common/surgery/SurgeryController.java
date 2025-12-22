package com.perigrine3.createcybernetics.common.surgery;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.block.entity.RobosurgeonBlockEntity;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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

                    boolean isStaged =
                            staged != null
                                    && invIndex < staged.length
                                    && staged[invIndex];

                    boolean isMarked =
                            markedForRemoval != null
                                    && invIndex < markedForRemoval.length
                                    && markedForRemoval[invIndex];

                    boolean willRemove = isMarked;
                    boolean willInstall = isStaged && !stackInGui.isEmpty();

                    // -------------------------------------------------
                    // 1) REMOVAL (including replacement cases)
                    // -------------------------------------------------
                    if (willRemove) {
                        InstalledCyberware removed = data.remove(slot, i);

                        // hook
                        if (removed != null && removed.getItem() != null && !removed.getItem().isEmpty()) {
                            if (removed.getItem().getItem() instanceof ICyberwareItem cw) {
                                cw.onRemoved(player);
                            }
                        }

                        // give back removed item at surgery time
                        if (removed != null && removed.getItem() != null && !removed.getItem().isEmpty()) {
                            ItemStack giveBack = removed.getItem().copy();
                            if (!player.getInventory().add(giveBack)) {
                                player.drop(giveBack, false);
                            }
                        }

                        // If this is not a replacement install, clear the GUI slot now and finish this slot
                        if (!willInstall) {
                            surgeon.inventory.setStackInSlot(invIndex, ItemStack.EMPTY);

                            surgeon.installed[invIndex] = false;
                            surgeon.staged[invIndex] = false;
                            surgeon.markedForRemoval[invIndex] = false;
                            continue;
                        }

                        // Else: replacement. Do NOT continue; fall through into install step.
                    }

                    // -------------------------------------------------
                    // 2) NOT STAGED => nothing else to do
                    // -------------------------------------------------
                    if (!willInstall) {
                        // if GUI is empty but staged flag got left on, clear it
                        if (isStaged && stackInGui.isEmpty()) {
                            surgeon.staged[invIndex] = false;
                        }
                        continue;
                    }

                    // -------------------------------------------------
                    // 3) INSTALL STAGED ITEM
                    // -------------------------------------------------
                    int humanityCost = 0;
                    if (stackInGui.getItem() instanceof ICyberwareItem cyberwareItem) {
                        humanityCost = cyberwareItem.getHumanityCost();
                    }

                    InstalledCyberware installed = new InstalledCyberware(stackInGui.copy(), slot, i, humanityCost);
                    installed.setPowered(true);
                    data.set(slot, i, installed);

                    // hook
                    if (stackInGui.getItem() instanceof ICyberwareItem cw) {
                        cw.onInstalled(player);
                    }

                    surgeon.installed[invIndex] = true;
                    surgeon.staged[invIndex] = false;
                    surgeon.markedForRemoval[invIndex] = false;
                }
            }

            // commit + sync
            data.setDirty();
            player.syncData(ModAttachments.CYBERWARE);

            surgeon.clearSlotStates();
            surgeon.setChanged();

            player.level().sendBlockUpdated(
                    surgeon.getBlockPos(),
                    surgeon.getBlockState(),
                    surgeon.getBlockState(),
                    3
            );

        } finally {
            surgeon.endSurgery();
        }
    }
}
