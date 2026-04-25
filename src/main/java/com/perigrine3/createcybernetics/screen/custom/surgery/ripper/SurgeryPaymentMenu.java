package com.perigrine3.createcybernetics.screen.custom.surgery.ripper;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.block.entity.SurgeryTableBlockEntity;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.surgery.RobosurgeonSlotMap;
import com.perigrine3.createcybernetics.entity.custom.RipperEntity;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SurgeryPaymentMenu extends AbstractContainerMenu {

    public static final int IMAGE_WIDTH = 176;
    public static final int IMAGE_HEIGHT = 222;

    public static final int PAYMENT_SLOT_X = 80;
    public static final int PAYMENT_SLOT_Y = 93;

    public static final int PLAYER_INV_X = 8;
    public static final int PLAYER_INV_Y = 140;
    public static final int HOTBAR_X = 8;
    public static final int HOTBAR_Y = 198;

    private static final int PAYMENT_SLOT = 0;
    private static final int CUSTOM_SLOT_COUNT = 1;
    private static final int HIDDEN_SYNC_SLOT_COUNT = SurgeryTableBlockEntity.SLOT_COUNT;

    private static final int PLAYER_INV_START = CUSTOM_SLOT_COUNT + HIDDEN_SYNC_SLOT_COUNT;
    private static final int PLAYER_INV_END = PLAYER_INV_START + 27;
    private static final int HOTBAR_START = PLAYER_INV_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private static final int PRICE_PER_ACTION = 3;

    private final SurgeryTableBlockEntity blockEntity;
    private final RipperEntity ripper;
    private final Player patient;

    private final int[] installedFlags = new int[SurgeryTableBlockEntity.SLOT_COUNT];
    private final int[] stagedFlags = new int[SurgeryTableBlockEntity.SLOT_COUNT];
    private final int[] removalFlags = new int[SurgeryTableBlockEntity.SLOT_COUNT];

    private final SimpleContainer paymentContainer = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            SurgeryPaymentMenu.this.slotsChanged(this);
        }
    };

    private boolean confirmed = false;

    public SurgeryPaymentMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(
                containerId,
                playerInventory,
                resolveTable(playerInventory, buf.readBlockPos()),
                resolveRipper(playerInventory, buf.readVarInt())
        );
    }

    public SurgeryPaymentMenu(int containerId, Inventory playerInventory, SurgeryTableBlockEntity blockEntity, RipperEntity ripper) {
        super(ModMenuTypes.SURGERY_PAYMENT_MENU.get(), containerId);

        if (blockEntity == null) {
            throw new IllegalStateException("Invalid surgery payment block entity");
        }
        if (ripper == null) {
            throw new IllegalStateException("Invalid ripper entity");
        }

        this.blockEntity = blockEntity;
        this.ripper = ripper;
        this.patient = blockEntity.getPatientOr(playerInventory.player);

        addSlot(new PaymentSlot(paymentContainer, 0, PAYMENT_SLOT_X, PAYMENT_SLOT_Y));
        addHiddenSyncSlots();
        addFlagDataSlots();
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        refreshFlagsFromBlockEntity();
    }

    private static SurgeryTableBlockEntity resolveTable(Inventory inventory, BlockPos pos) {
        BlockEntity be = inventory.player.level().getBlockEntity(pos);
        if (!(be instanceof SurgeryTableBlockEntity table)) {
            throw new IllegalStateException("Invalid surgery table block entity");
        }
        return table;
    }

    private static RipperEntity resolveRipper(Inventory inventory, int entityId) {
        Entity entity = inventory.player.level().getEntity(entityId);
        if (!(entity instanceof RipperEntity ripper)) {
            throw new IllegalStateException("Invalid ripper entity");
        }
        return ripper;
    }

    private void addHiddenSyncSlots() {
        for (int i = 0; i < SurgeryTableBlockEntity.SLOT_COUNT; i++) {
            addSlot(new HiddenSyncSlot(blockEntity.inventory, i));
        }
    }

    private void addFlagDataSlots() {
        for (int i = 0; i < SurgeryTableBlockEntity.SLOT_COUNT; i++) {
            final int idx = i;
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return installedFlags[idx];
                }

                @Override
                public void set(int value) {
                    installedFlags[idx] = value;
                }
            });
        }

        for (int i = 0; i < SurgeryTableBlockEntity.SLOT_COUNT; i++) {
            final int idx = i;
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return stagedFlags[idx];
                }

                @Override
                public void set(int value) {
                    stagedFlags[idx] = value;
                }
            });
        }

        for (int i = 0; i < SurgeryTableBlockEntity.SLOT_COUNT; i++) {
            final int idx = i;
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return removalFlags[idx];
                }

                @Override
                public void set(int value) {
                    removalFlags[idx] = value;
                }
            });
        }
    }

    @Override
    public void broadcastChanges() {
        if (!patient.level().isClientSide) {
            refreshFlagsFromBlockEntity();
        }
        super.broadcastChanges();
    }

    private void refreshFlagsFromBlockEntity() {
        for (int i = 0; i < SurgeryTableBlockEntity.SLOT_COUNT; i++) {
            installedFlags[i] = blockEntity.isInstalled(i) ? 1 : 0;
            stagedFlags[i] = blockEntity.isStaged(i) ? 1 : 0;
            removalFlags[i] = blockEntity.isMarkedForRemoval(i) ? 1 : 0;
        }
    }

    public boolean isInstalled(int index) {
        return index >= 0 && index < installedFlags.length && installedFlags[index] != 0;
    }

    public boolean isStaged(int index) {
        return index >= 0 && index < stagedFlags.length && stagedFlags[index] != 0;
    }

    public boolean isMarkedForRemoval(int index) {
        return index >= 0 && index < removalFlags.length && removalFlags[index] != 0;
    }

    public SurgeryTableBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public RipperEntity getRipper() {
        return ripper;
    }

    public Player getPatient() {
        return patient;
    }

    public List<String> getInstallEntries() {
        List<String> entries = new ArrayList<>();

        for (int invIndex = 0; invIndex < SurgeryTableBlockEntity.SLOT_COUNT; invIndex++) {
            if (!isStaged(invIndex)) {
                continue;
            }

            ItemStack staged = blockEntity.inventory.getStackInSlot(invIndex);
            if (staged.isEmpty()) {
                continue;
            }

            entries.add(staged.getHoverName().getString());
        }

        return entries;
    }

    public List<String> getRemovalEntries() {
        List<String> entries = new ArrayList<>();
        PlayerCyberwareData data = patient.getData(ModAttachments.CYBERWARE);
        if (data == null) {
            return Collections.emptyList();
        }

        for (CyberwareSlot slotType : CyberwareSlot.values()) {
            for (int i = 0; i < slotType.size; i++) {
                int invIndex = RobosurgeonSlotMap.toInventoryIndex(slotType, i);
                if (invIndex < 0) {
                    continue;
                }

                if (!isMarkedForRemoval(invIndex)) {
                    continue;
                }

                InstalledCyberware installed = data.get(slotType, i);
                if (installed == null || installed.getItem() == null || installed.getItem().isEmpty()) {
                    continue;
                }

                entries.add(installed.getItem().getHoverName().getString());
            }
        }

        return entries;
    }

    public int getInstallCount() {
        int count = 0;
        for (int invIndex = 0; invIndex < SurgeryTableBlockEntity.SLOT_COUNT; invIndex++) {
            if (isStaged(invIndex)) {
                ItemStack staged = blockEntity.inventory.getStackInSlot(invIndex);
                if (!staged.isEmpty()) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getRemovalCount() {
        int count = 0;
        for (int invIndex = 0; invIndex < SurgeryTableBlockEntity.SLOT_COUNT; invIndex++) {
            if (isMarkedForRemoval(invIndex)) {
                count++;
            }
        }
        return count;
    }

    public int getTotalPrice() {
        return PRICE_PER_ACTION * (getInstallCount() + getRemovalCount());
    }

    public int getTrustworthiness() {
        return ripper.getTrustworthiness();
    }

    public boolean hasSufficientPayment() {
        ItemStack payment = paymentContainer.getItem(0);
        return payment.is(Items.EMERALD) && payment.getCount() >= getTotalPrice();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return false;
            }

            int totalPrice = getTotalPrice();
            if (totalPrice <= 0 || !hasSufficientPayment()) {
                return false;
            }

            paymentContainer.removeItem(PAYMENT_SLOT, totalPrice);
            confirmed = true;

            blockEntity.startTimedSurgery(player);
            serverPlayer.closeContainer();
            return true;
        }

        if (id == 1) {
            if (player.isSleeping()) {
                player.stopSleeping();
            }
            player.closeContainer();
            return true;
        }

        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        if (!ripper.isAlive()) {
            return false;
        }

        if (blockEntity.getPatient() != player) {
            return false;
        }

        if (!player.isSleeping()) {
            return false;
        }

        return player.distanceToSqr(ripper) <= 64.0D;
    }

    @Override
    public void removed(Player player) {
        if (!player.level().isClientSide) {
            ItemStack payment = paymentContainer.getItem(0);
            if (!payment.isEmpty()) {
                paymentContainer.setItem(0, ItemStack.EMPTY);
                player.getInventory().placeItemBackInInventory(payment);
            }
        }

        super.removed(player);
    }

    @Override
    public void slotsChanged(net.minecraft.world.Container container) {
        super.slotsChanged(container);
        broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        if (index == PAYMENT_SLOT) {
            if (!moveItemStackTo(sourceStack, PLAYER_INV_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }

            if (sourceStack.isEmpty()) {
                sourceSlot.set(ItemStack.EMPTY);
            } else {
                sourceSlot.setChanged();
            }

            return copy;
        }

        if (index >= PLAYER_INV_START) {
            if (sourceStack.is(Items.EMERALD)) {
                if (moveItemStackTo(sourceStack, PAYMENT_SLOT, PAYMENT_SLOT + 1, false)) {
                    return copy;
                }
            }
            return ItemStack.EMPTY;
        }

        return ItemStack.EMPTY;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new Slot(
                        playerInventory,
                        col + row * 9 + 9,
                        PLAYER_INV_X + col * 18,
                        PLAYER_INV_Y + row * 18
                ));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(
                    playerInventory,
                    i,
                    HOTBAR_X + i * 18,
                    HOTBAR_Y
            ));
        }
    }

    private static class PaymentSlot extends Slot {
        public PaymentSlot(SimpleContainer container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(Items.EMERALD);
        }
    }

    private static class HiddenSyncSlot extends SlotItemHandler {
        public HiddenSyncSlot(net.neoforged.neoforge.items.ItemStackHandler handler, int index) {
            super(handler, index, -1000, -1000);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean isActive() {
            return false;
        }
    }
}