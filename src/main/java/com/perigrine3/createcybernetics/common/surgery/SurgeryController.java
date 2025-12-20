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

        // -------------------------------------------------
        // BEGIN SURGERY
        // -------------------------------------------------
        surgeon.beginSurgery();

        try {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            // -------------------------------------------------
            // PROCESS ALL SLOTS
            // -------------------------------------------------
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

                    // =================================================
                    // MARKED FOR REMOVAL → REMOVE FROM ATTACHMENT, GIVE BACK TO PLAYER
                    // =================================================
                    if (isMarked) {

                        InstalledCyberware removed = data.remove(slot, i);

                        if (removed != null && removed.getItem() != null && !removed.getItem().isEmpty()) {
                            ItemStack giveBack = removed.getItem().copy();
                            if (!player.getInventory().add(giveBack)) {
                                player.drop(giveBack, false);
                            }
                        } else {
                            if (!stackInGui.isEmpty()) {
                                ItemStack giveBack = stackInGui.copy();
                                if (!player.getInventory().add(giveBack)) {
                                    player.drop(giveBack, false);
                                }
                            }
                        }

                        surgeon.inventory.setStackInSlot(invIndex, ItemStack.EMPTY);

                        surgeon.installed[invIndex] = false;
                        surgeon.staged[invIndex] = false;
                        surgeon.markedForRemoval[invIndex] = false;
                        continue;
                    }

                    // =================================================
                    // NOT STAGED → IGNORE
                    // =================================================
                    if (!isStaged) {
                        continue;
                    }

                    if (stackInGui.isEmpty()) {
                        surgeon.staged[invIndex] = false;
                        continue;
                    }

                    // =================================================
                    // INSTALL STAGED ITEM → WRITE TO ATTACHMENT
                    // =================================================
                    int humanityCost = 0;
                    if (stackInGui.getItem() instanceof ICyberwareItem cyberwareItem) {
                        humanityCost = cyberwareItem.getHumanityCost();
                    }

                    InstalledCyberware installed = new InstalledCyberware(stackInGui.copy(), slot, i, humanityCost);
                    installed.setPowered(true);

                    data.set(slot, i, installed);

                    surgeon.installed[invIndex] = true;
                    surgeon.staged[invIndex] = false;
                    surgeon.markedForRemoval[invIndex] = false;
                }
            }

            // -------------------------------------------------
            // COMMIT + SYNC
            // -------------------------------------------------
            data.setDirty();
            player.syncData(ModAttachments.CYBERWARE);

            surgeon.clearSlotStates();
            surgeon.setChanged();

            player.level().sendBlockUpdated(surgeon.getBlockPos(), surgeon.getBlockState(), surgeon.getBlockState(), 3);

        } finally {
            surgeon.endSurgery();
        }
    }
}
