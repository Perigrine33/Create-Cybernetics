package com.perigrine3.createcybernetics.common.surgery;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
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
            // -------------------------------------------------
            // GET PLAYER CYBERWARE DATA
            // -------------------------------------------------
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);

            if (data == null) {
                return;
            }

            // -------------------------------------------------
            // PROCESS ALL SLOTS
            // -------------------------------------------------
            for (CyberwareSlot slot : CyberwareSlot.values()) {
                for (int i = 0; i < slot.size; i++) {

                    int invIndex = RobosurgeonSlotMap.toInventoryIndex(slot, i);
                    if (invIndex < 0 || invIndex >= surgeon.inventory.getSlots()) {
                        continue;
                    }

                    ItemStack stack = surgeon.inventory.getStackInSlot(invIndex);

                    boolean isStaged =
                            staged != null
                                    && invIndex < staged.length
                                    && staged[invIndex];

                    boolean isMarked =
                            markedForRemoval != null
                                    && invIndex < markedForRemoval.length
                                    && markedForRemoval[invIndex];

                    // =================================================
                    // MARKED FOR REMOVAL → GIVE BACK TO PLAYER
                    // =================================================
                    if (isMarked) {

                        if (!stack.isEmpty()) {
                            ItemStack removed = stack.copy();
                            surgeon.inventory.setStackInSlot(invIndex, ItemStack.EMPTY);

                            if (!player.getInventory().add(removed)) {
                                player.drop(removed, false);
                            }
                        }

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

                    // Staged flag but empty stack → cleanup
                    if (stack.isEmpty()) {
                        surgeon.staged[invIndex] = false;
                        continue;
                    }

                    // =================================================
                    // INSTALL STAGED ITEM
                    // =================================================
                    InstalledCyberware installed =
                            new InstalledCyberware(stack.copy(), slot, i, 0);

                    data.set(slot, i, installed);

                    surgeon.installed[invIndex] = true;
                    surgeon.staged[invIndex] = false;
                    surgeon.markedForRemoval[invIndex] = false;

                }
            }

            data.setDirty();
            surgeon.clearSlotStates();
            surgeon.setChanged();

            if (!player.level().isClientSide) {
                player.level().sendBlockUpdated(surgeon.getBlockPos(), surgeon.getBlockState(), surgeon.getBlockState(), 3);
            }

        } finally {
            surgeon.endSurgery();
        }
    }
}
