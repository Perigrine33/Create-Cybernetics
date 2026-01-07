package com.perigrine3.createcybernetics.client.container;

import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CreativeChipwareContainer implements Container {

    private final Player player;

    public CreativeChipwareContainer(Player player) {
        this.player = player;
    }

    private PlayerCyberwareData dataOrNull() {
        if (player == null) return null;
        if (!player.hasData(ModAttachments.CYBERWARE)) return null;
        return player.getData(ModAttachments.CYBERWARE);
    }

    @Override
    public int getContainerSize() {
        return PlayerCyberwareData.CHIPWARE_SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        PlayerCyberwareData d = dataOrNull();
        if (d == null) return true;

        for (int i = 0; i < getContainerSize(); i++) {
            if (!d.getChipwareStack(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        PlayerCyberwareData d = dataOrNull();
        if (d == null) return ItemStack.EMPTY;
        return d.getChipwareStack(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        PlayerCyberwareData d = dataOrNull();
        if (d == null) return ItemStack.EMPTY;

        ItemStack cur = d.getChipwareStack(slot);
        if (cur.isEmpty()) return ItemStack.EMPTY;

        d.setChipwareStack(slot, ItemStack.EMPTY);
        setChanged();
        return cur;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        PlayerCyberwareData d = dataOrNull();
        if (d == null) return ItemStack.EMPTY;

        ItemStack cur = d.getChipwareStack(slot);
        if (!cur.isEmpty()) {
            d.setChipwareStack(slot, ItemStack.EMPTY);
        }
        return cur;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        PlayerCyberwareData d = dataOrNull();
        if (d == null) return;

        d.setChipwareStack(slot, stack);
        setChanged();
    }

    @Override
    public void setChanged() {
        PlayerCyberwareData d = dataOrNull();
        if (d == null) return;

        d.setDirty();
        player.setData(ModAttachments.CYBERWARE, d);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        PlayerCyberwareData d = dataOrNull();
        if (d == null) return;

        d.clearChipwareInventory();
        setChanged();
    }
}
