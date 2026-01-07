package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import com.perigrine3.createcybernetics.screen.slot.DataShardSlot;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.BooleanSupplier;

public class ChipwareMiniMenu extends AbstractContainerMenu {

    // ===== Coordinates pulled from your provided 256x256 PNG =====

    public static final int CHIP_X = 146;
    public static final int CHIP_Y0 = 81;
    public static final int CHIP_SPACING = 18;

    public static final int INV_X0 = 48;
    public static final int INV_Y0 = 142;
    public static final int HOTBAR_Y = 200;

    public static final int SLOT_SPACING = 18;

    private final Container chipInv;

    public ChipwareMiniMenu(int containerId, Inventory playerInv, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInv, new SimpleContainer(PlayerCyberwareData.CHIPWARE_SLOT_COUNT));
    }

    public ChipwareMiniMenu(int containerId, Inventory playerInv, Container chipInv) {
        super(ModMenuTypes.CHIPWARE_MINI_MENU.get(), containerId);
        this.chipInv = chipInv;

        checkContainerSize(chipInv, PlayerCyberwareData.CHIPWARE_SLOT_COUNT);
        chipInv.startOpen(playerInv.player);

        BooleanSupplier active = () -> true;

        addSlot(new DataShardSlot(chipInv, 0, CHIP_X, CHIP_Y0, active));
        addSlot(new DataShardSlot(chipInv, 1, CHIP_X, CHIP_Y0 + CHIP_SPACING, active));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int idx = 9 + col + row * 9;
                int x = INV_X0 + col * SLOT_SPACING;
                int y = INV_Y0 + row * SLOT_SPACING;
                addSlot(new Slot(playerInv, idx, x, y));
            }
        }

        for (int col = 0; col < 9; col++) {
            int x = INV_X0 + col * SLOT_SPACING;
            addSlot(new Slot(playerInv, col, x, HOTBAR_Y));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        chipInv.stopOpen(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = getSlot(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        final int CHIP_START = 0;
        final int CHIP_END = 2;

        final int PLAYER_START = CHIP_END;
        final int PLAYER_END = PLAYER_START + 36;

        boolean moved;

        if (index >= CHIP_START && index < CHIP_END) {
            moved = moveItemStackTo(stack, PLAYER_START, PLAYER_END, false);
        }
        else if (stack.is(ModTags.Items.DATA_SHARDS)) {
            moved = moveItemStackTo(stack, CHIP_START, CHIP_END, false);
        }
        else {
            final int INV_START = PLAYER_START;
            final int INV_END = INV_START + 27;
            final int HOTBAR_START = INV_END;
            final int HOTBAR_END = PLAYER_END;

            if (index >= INV_START && index < INV_END) {
                moved = moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false);
            } else if (index >= HOTBAR_START && index < HOTBAR_END) {
                moved = moveItemStackTo(stack, INV_START, INV_END, false);
            } else {
                moved = false;
            }
        }

        if (!moved) return ItemStack.EMPTY;

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        slot.onTake(player, stack);
        return copy;
    }

    public static boolean canOpen(Player player) {
        if (player == null) return false;
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        return data.hasSpecificItem(ModItems.BRAINUPGRADES_CHIPWARESLOTS.get(), CyberwareSlot.BRAIN);
    }
}
