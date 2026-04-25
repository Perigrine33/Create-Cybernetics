package com.perigrine3.createcybernetics.screen.custom.surgery.ripper;

import com.perigrine3.createcybernetics.entity.custom.RipperEntity;
import com.perigrine3.createcybernetics.entity.trade.RipperTradeLogic;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RipperTradeMenu extends AbstractContainerMenu {

    private static final int TRADE_INPUT_SLOT = 0;
    private static final int TRADE_RESULT_SLOT = 1;
    private static final int REFURBISH_INPUT_SLOT = 2;
    private static final int REFURBISH_PAYMENT_SLOT = 3;
    private static final int REFURBISH_RESULT_SLOT = 4;

    private static final int CUSTOM_SLOT_COUNT = 5;

    private static final int PLAYER_INV_START = CUSTOM_SLOT_COUNT;
    private static final int PLAYER_INV_END = PLAYER_INV_START + 27;
    private static final int HOTBAR_START = PLAYER_INV_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private final RipperEntity ripper;

    private final SimpleContainer tradeInput = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            RipperTradeMenu.this.slotsChanged(this);
        }
    };

    private final SimpleContainer tradeResult = new SimpleContainer(1);

    private final SimpleContainer refurbishInput = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            RipperTradeMenu.this.slotsChanged(this);
        }
    };

    private final SimpleContainer refurbishResult = new SimpleContainer(1);

    public RipperTradeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, resolveRipper(playerInventory, buf.readVarInt()));
    }

    public RipperTradeMenu(int containerId, Inventory playerInventory, RipperEntity ripper) {
        super(ModMenuTypes.RIPPER_TRADE_MENU.get(), containerId);

        if (ripper == null) {
            throw new IllegalStateException("Ripper entity not found for menu");
        }

        this.ripper = ripper;

        addTradeSlots();
        addRefurbishSlots();
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        updateOutputs();
    }

    private static RipperEntity resolveRipper(Inventory inventory, int entityId) {
        Entity entity = inventory.player.level().getEntity(entityId);
        if (!(entity instanceof RipperEntity ripper)) {
            throw new IllegalStateException("Invalid ripper entity for menu");
        }
        return ripper;
    }

    public RipperEntity getRipper() {
        return ripper;
    }

    private void addTradeSlots() {
        addSlot(new TradeInputSlot(tradeInput, 0, 53, 13));
        addSlot(new TradeResultSlot(this, tradeResult, 0, 116, 13));
    }

    private void addRefurbishSlots() {
        addSlot(new RefurbishCyberwareSlot(refurbishInput, 0, 35, 54));
        addSlot(new EmeraldPaymentSlot(refurbishInput, 1, 53, 54));
        addSlot(new RefurbishResultSlot(this, refurbishResult, 0, 116, 54));
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new Slot(
                        playerInventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18
                ));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(
                    playerInventory,
                    i,
                    8 + i * 18,
                    142
            ));
        }
    }

    @Override
    public void slotsChanged(net.minecraft.world.Container container) {
        super.slotsChanged(container);
        updateOutputs();
    }

    private void updateOutputs() {
        if (ripper.level().isClientSide) {
            return;
        }

        updateTradeOutput();
        updateRefurbishOutput();
        broadcastChanges();
    }

    private void updateTradeOutput() {
        ItemStack input = tradeInput.getItem(0);
        ItemStack result = RipperTradeLogic.createTradeOutput(ripper.getRandom(), input);
        tradeResult.setItem(0, result);
    }

    private void updateRefurbishOutput() {
        ItemStack cyberware = refurbishInput.getItem(0);
        ItemStack emeralds = refurbishInput.getItem(1);

        if (!RipperTradeLogic.isScavengedCyberware(cyberware)) {
            refurbishResult.setItem(0, ItemStack.EMPTY);
            return;
        }

        if (emeralds.isEmpty() || !emeralds.is(Items.EMERALD) || emeralds.getCount() < 20) {
            refurbishResult.setItem(0, ItemStack.EMPTY);
            return;
        }

        refurbishResult.setItem(0, RipperTradeLogic.createRefurbishOutput(cyberware));
    }

    void onTradeOutputTaken(Player player) {
        ItemStack input = tradeInput.getItem(0);
        if (!input.isEmpty()) {
            tradeInput.removeItem(0, 1);
            ripper.markTradeOccurred();
            ripper.addTrustworthiness(10);
        }

        updateOutputs();
    }

    void onRefurbishOutputTaken(Player player) {
        ItemStack cyberware = refurbishInput.getItem(0);
        ItemStack emeralds = refurbishInput.getItem(1);

        if (!cyberware.isEmpty()) {
            refurbishInput.removeItem(0, 1);
        }

        if (!emeralds.isEmpty() && emeralds.is(Items.EMERALD)) {
            refurbishInput.removeItem(1, 20);
        }

        ripper.markTradeOccurred();
        ripper.addTrustworthiness(15);

        updateOutputs();
    }

    @Override
    public void removed(Player player) {
        if (!player.level().isClientSide) {
            clearInputSlotToPlayerOrDrop(player, tradeInput, 0);
            clearInputSlotToPlayerOrDrop(player, refurbishInput, 0);
            clearInputSlotToPlayerOrDrop(player, refurbishInput, 1);

            tradeResult.setItem(0, ItemStack.EMPTY);
            refurbishResult.setItem(0, ItemStack.EMPTY);

            if (ripper.getCurrentTradeCustomer() == player) {
                ripper.setCurrentTradeCustomer(null);
            }
        }

        super.removed(player);
    }

    private static void clearInputSlotToPlayerOrDrop(Player player, SimpleContainer container, int slotIndex) {
        ItemStack stack = container.getItem(slotIndex);
        if (stack.isEmpty()) {
            return;
        }

        ItemStack toReturn = stack.copy();
        container.setItem(slotIndex, ItemStack.EMPTY);

        player.getInventory().placeItemBackInInventory(toReturn);
    }

    @Override
    public boolean stillValid(Player player) {
        return ripper.isAlive() && player.distanceToSqr(ripper) <= 64.0D;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copy = sourceStack.copy();

        if (index == TRADE_RESULT_SLOT) {
            if (!moveItemStackTo(sourceStack, PLAYER_INV_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }

            int moved = copy.getCount() - sourceStack.getCount();
            if (moved <= 0) {
                return ItemStack.EMPTY;
            }

            sourceSlot.onQuickCraft(sourceStack, copy);
            onTradeOutputTaken(player);
            return copy;
        }

        if (index == REFURBISH_RESULT_SLOT) {
            if (!moveItemStackTo(sourceStack, PLAYER_INV_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }

            int moved = copy.getCount() - sourceStack.getCount();
            if (moved <= 0) {
                return ItemStack.EMPTY;
            }

            sourceSlot.onQuickCraft(sourceStack, copy);
            onRefurbishOutputTaken(player);
            return copy;
        }

        if (index >= PLAYER_INV_START) {
            if (RipperTradeLogic.isCyberware(sourceStack)) {
                if (RipperTradeLogic.isScavengedCyberware(sourceStack)) {
                    if (!slots.get(REFURBISH_INPUT_SLOT).hasItem()
                            && moveItemStackTo(sourceStack, REFURBISH_INPUT_SLOT, REFURBISH_INPUT_SLOT + 1, false)) {
                        return copy;
                    }
                }

                if (!slots.get(TRADE_INPUT_SLOT).hasItem()
                        && moveItemStackTo(sourceStack, TRADE_INPUT_SLOT, TRADE_INPUT_SLOT + 1, false)) {
                    return copy;
                }

                if (RipperTradeLogic.isScavengedCyberware(sourceStack)
                        && !slots.get(REFURBISH_INPUT_SLOT).hasItem()
                        && moveItemStackTo(sourceStack, REFURBISH_INPUT_SLOT, REFURBISH_INPUT_SLOT + 1, false)) {
                    return copy;
                }
            }

            if (sourceStack.is(Items.EMERALD)) {
                if (moveItemStackTo(sourceStack, REFURBISH_PAYMENT_SLOT, REFURBISH_PAYMENT_SLOT + 1, false)) {
                    return copy;
                }
            }

            return ItemStack.EMPTY;
        }

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

    private static class TradeInputSlot extends Slot {
        public TradeInputSlot(SimpleContainer container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return RipperTradeLogic.isCyberware(stack);
        }
    }

    private static class RefurbishCyberwareSlot extends Slot {
        public RefurbishCyberwareSlot(SimpleContainer container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return RipperTradeLogic.isScavengedCyberware(stack);
        }
    }

    private static class EmeraldPaymentSlot extends Slot {
        public EmeraldPaymentSlot(SimpleContainer container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(Items.EMERALD);
        }
    }

    private static class TradeResultSlot extends Slot {
        private final RipperTradeMenu menu;

        public TradeResultSlot(RipperTradeMenu menu, SimpleContainer container, int slot, int x, int y) {
            super(container, slot, x, y);
            this.menu = menu;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            super.onTake(player, stack);
            menu.onTradeOutputTaken(player);
        }
    }

    private static class RefurbishResultSlot extends Slot {
        private final RipperTradeMenu menu;

        public RefurbishResultSlot(RipperTradeMenu menu, SimpleContainer container, int slot, int x, int y) {
            super(container, slot, x, y);
            this.menu = menu;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            super.onTake(player, stack);
            menu.onRefurbishOutputTaken(player);
        }
    }
}