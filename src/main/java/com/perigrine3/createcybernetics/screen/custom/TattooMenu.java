package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TattooMenu extends AbstractContainerMenu {
    private static final int PAYMENT_SLOT = 0;
    private static final int PLAYER_INV_START = 1;
    private static final int PLAYER_INV_END = PLAYER_INV_START + 36;

    private final Container payment = new SimpleContainer(1);

    private final DataSlot synthSkinInstalled = DataSlot.standalone();

    public TattooMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory);
    }

    public TattooMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.TATTOO_MENU.get(), containerId);

        synthSkinInstalled.set(hasSynthSkinInstalled(playerInventory.player) ? 1 : 0);
        addDataSlot(synthSkinInstalled);

        addSlot(new Slot(payment, 0, 41, 29) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.GOLD_INGOT);
            }

            @Override
            public int getMaxStackSize() {
                return 5;
            }
        });

        addPlayerInventory(playerInventory, 8, 84);
        addPlayerHotbar(playerInventory, 8, 142);
    }

    public boolean canChooseTattooLayer() {
        return synthSkinInstalled.get() != 0;
    }

    public ItemStack getPaymentStack() {
        return payment.getItem(0);
    }

    public boolean hasEnoughGold() {
        ItemStack stack = getPaymentStack();
        return !stack.isEmpty() && stack.is(Items.GOLD_INGOT) && stack.getCount() >= 5;
    }

    public void consumePayment(int amount) {
        if (amount <= 0) {
            return;
        }

        ItemStack stack = payment.getItem(0);
        if (stack.isEmpty()) {
            return;
        }

        stack.shrink(amount);

        if (stack.isEmpty()) {
            payment.setItem(0, ItemStack.EMPTY);
        }

        broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = payment.removeItemNoUpdate(0);
            if (!stack.isEmpty()) {
                if (!serverPlayer.getInventory().add(stack)) {
                    serverPlayer.drop(stack, false);
                }
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();

        if (index == PAYMENT_SLOT) {
            if (!moveItemStackTo(original, PLAYER_INV_START, PLAYER_INV_END, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (original.is(Items.GOLD_INGOT)) {
                if (!moveItemStackTo(original, PAYMENT_SLOT, PAYMENT_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (original.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return copy;
    }

    private static boolean hasSynthSkinInstalled(Player player) {
        if (player == null) {
            return false;
        }

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) {
            return false;
        }

        for (int i = 0; i < CyberwareSlot.SKIN.size; i++) {
            InstalledCyberware installed = data.get(CyberwareSlot.SKIN, i);
            if (installed == null) {
                continue;
            }

            ItemStack stack = installed.getItem();
            if (stack != null && stack.is(ModItems.SKINUPGRADES_SYNTHSKIN.get())) {
                return true;
            }
        }

        return false;
    }

    private void addPlayerInventory(Inventory inventory, int x, int y) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, x + column * 18, y + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory, int x, int y) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, x + column * 18, y));
        }
    }
}