package com.perigrine3.createcybernetics.compat.corpse;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CorpseCyberwareMenu extends AbstractContainerMenu {

    public static final int COLUMNS = 10;
    public static final int ROWS = 8;

    private static final String CORPSE_ENTITY_CLASS = "de.maxhenkel.corpse.entities.CorpseEntity";

    private final Container cyberwareInventory;
    private final int corpseEntityId;

    public CorpseCyberwareMenu(int id, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(
                ModCorpseCompatMenus.CORPSE_CYBERWARE.get(),
                id,
                playerInventory,
                buf.readInt(),
                buf.readBoolean()
        );
    }

    public CorpseCyberwareMenu(MenuType<?> type, int id, Inventory playerInventory, Entity corpse, boolean editable) {
        this(type, id, playerInventory, corpse != null ? corpse.getId() : -1, editable);
    }

    private CorpseCyberwareMenu(MenuType<?> type, int id, Inventory playerInventory, int corpseEntityId, boolean editable) {
        super(type, id);
        this.corpseEntityId = corpseEntityId;

        Entity corpse = resolveCorpse(playerInventory.player, corpseEntityId);
        this.cyberwareInventory = corpse != null
                ? new CorpseCyberwareInventory(corpse)
                : new SimpleContainer(CorpseCompat.CYBERWARE_SLOT_COUNT);

        int startX = 7;
        int startY = 18;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int index = col + row * COLUMNS;
                addSlot(new Slot(cyberwareInventory, index, startX + col * 18, startY + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }
                });
            }
        }

        int playerInvY = startY + ROWS * 18 + 14;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 15 + col * 18, playerInvY + row * 18));
            }
        }

        int hotbarY = playerInvY + 58;
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 15 + col * 18, hotbarY));
        }
    }

    private static Entity resolveCorpse(Player player, int entityId) {
        if (player == null || player.level() == null || entityId < 0) {
            return null;
        }

        Entity entity = player.level().getEntity(entityId);
        return isCorpseEntity(entity) ? entity : null;
    }

    @Override
    public boolean stillValid(Player player) {
        Entity corpse = resolveCorpse(player, corpseEntityId);
        return corpse != null
                && corpse.isAlive()
                && player.distanceToSqr(corpse) <= 64.0D;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);

        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        int corpseSlots = CorpseCompat.CYBERWARE_SLOT_COUNT;
        if (index < corpseSlots) {
            if (!moveItemStackTo(stack, corpseSlots, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return copy;
    }

    private void reloadCorpseCyberwareInventory() {
        if (cyberwareInventory instanceof CorpseCyberwareInventory corpseInventory) {
            corpseInventory.reloadFromCorpse();
        }
    }

    private boolean isSameCorpseMenu(Entity corpse) {
        return corpse != null && corpse.getId() == corpseEntityId;
    }

    private static void syncOpenCorpseCyberwareMenus(Entity corpse) {
        if (corpse == null || corpse.level().isClientSide()) return;
        if (!(corpse.level() instanceof ServerLevel serverLevel)) return;

        for (ServerPlayer player : serverLevel.players()) {
            if (!(player.containerMenu instanceof CorpseCyberwareMenu menu)) continue;
            if (!menu.isSameCorpseMenu(corpse)) continue;

            menu.reloadCorpseCyberwareInventory();
            menu.broadcastChanges();
        }
    }

    private static boolean isCorpseEntity(Entity entity) {
        if (entity == null || !CorpseCompat.isLoaded()) {
            return false;
        }

        try {
            Class<?> corpseClass = Class.forName(CORPSE_ENTITY_CLASS);
            return corpseClass.isInstance(entity);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static class CorpseCyberwareInventory implements Container {
        private final Entity corpse;
        private final NonNullList<ItemStack> items;
        private boolean reloading;

        private CorpseCyberwareInventory(Entity corpse) {
            this.corpse = corpse;
            this.items = NonNullList.withSize(CorpseCompat.CYBERWARE_SLOT_COUNT, ItemStack.EMPTY);
            reloadFromCorpse();
        }

        private void reloadFromCorpse() {
            reloading = true;

            NonNullList<ItemStack> fresh = CorpseCompat.getCorpseCyberwareItems(corpse);
            for (int i = 0; i < items.size(); i++) {
                ItemStack stack = i >= 0 && i < fresh.size() ? fresh.get(i) : ItemStack.EMPTY;
                items.set(i, stack.copy());
            }

            reloading = false;
        }

        @Override
        public int getContainerSize() {
            return items.size();
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack item : items) {
                if (!item.isEmpty()) return false;
            }
            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return slot >= 0 && slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
            if (!removed.isEmpty()) {
                setChanged();
            }
            return removed;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            if (slot < 0 || slot >= items.size()) return ItemStack.EMPTY;

            ItemStack removed = items.get(slot);
            items.set(slot, ItemStack.EMPTY);
            setChanged();
            return removed;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (slot < 0 || slot >= items.size()) return;

            items.set(slot, stack == null ? ItemStack.EMPTY : stack);
            setChanged();
        }

        @Override
        public void setChanged() {
            if (reloading) return;

            CorpseCompat.writeCorpseCyberwareItems(corpse, items);
            syncOpenCorpseCyberwareMenus(corpse);
        }

        @Override
        public boolean stillValid(Player player) {
            Entity resolved = resolveCorpse(player, corpse.getId());
            return resolved != null
                    && resolved.isAlive()
                    && player.distanceToSqr(resolved) <= 64.0D;
        }

        @Override
        public void clearContent() {
            for (int i = 0; i < items.size(); i++) {
                items.set(i, ItemStack.EMPTY);
            }
            setChanged();
        }
    }
}