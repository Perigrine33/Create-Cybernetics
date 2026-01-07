package com.perigrine3.createcybernetics.screen.container;

import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ChipwareContainer implements Container {

    private final Player player;

    public ChipwareContainer(Player player) {
        this.player = player;
    }

    private PlayerCyberwareData data() {
        return player.getData(ModAttachments.CYBERWARE);
    }

    @Override
    public int getContainerSize() {
        return PlayerCyberwareData.CHIPWARE_SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        PlayerCyberwareData d = data();
        if (d == null) return true;
        for (int i = 0; i < getContainerSize(); i++) {
            if (!d.getChipwareStack(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        PlayerCyberwareData d = data();
        if (d == null) return ItemStack.EMPTY;
        return d.getChipwareStack(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        PlayerCyberwareData d = data();
        if (d == null) return ItemStack.EMPTY;

        ItemStack current = d.getChipwareStack(slot);
        if (current.isEmpty()) return ItemStack.EMPTY;

        // Single-item slot: any remove request removes the stack
        d.setChipwareStack(slot, ItemStack.EMPTY);
        setChanged();
        return current;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        PlayerCyberwareData d = data();
        if (d == null) return ItemStack.EMPTY;

        ItemStack current = d.getChipwareStack(slot);
        if (!current.isEmpty()) {
            d.setChipwareStack(slot, ItemStack.EMPTY);
        }
        return current;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        PlayerCyberwareData d = data();
        if (d == null) return;

        d.setChipwareStack(slot, stack);
        setChanged();
    }

    @Override
    public void setChanged() {
        PlayerCyberwareData d = data();
        d.setDirty();

        if (!player.level().isClientSide) {
            // Tell the attachment system “this attachment value changed”
            player.setData(ModAttachments.CYBERWARE, d);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        PlayerCyberwareData d = data();
        if (d == null) return;

        d.clearChipwareInventory();
        setChanged();
    }
}
