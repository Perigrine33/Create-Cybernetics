package com.perigrine3.createcybernetics.screen.custom.spinal_injector;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.cyberware.bone.SpinalInjectorItem;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import net.minecraft.core.HolderLookup;
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

public class SpinalInjectorMenu extends AbstractContainerMenu {

    private static final int INJECTOR_MAX = 16;

    private static final int[] INJECTOR_X = {49, 121, 49, 121};
    private static final int[] INJECTOR_Y = {29, 29, 101, 101};

    private final Player owner;
    private final Container injectorInv;
    private final HolderLookup.Provider provider;

    private final CyberwareSlot installedSlot;
    private final int installedIndex;

    private ItemStack serverFallbackSnapshot = ItemStack.EMPTY;

    private final int[] injectorCounts = new int[SpinalInjectorItem.SLOT_COUNT];

    public SpinalInjectorMenu(int id, Inventory playerInv, RegistryFriendlyByteBuf buf) {
        this(id, playerInv, CyberwareSlot.valueOf(buf.readUtf()), buf.readVarInt());
    }

    public SpinalInjectorMenu(int id, Inventory playerInv, CyberwareSlot installedSlot, int installedIndex) {
        super(ModMenuTypes.SPINAL_INJECTOR_MENU.get(), id);

        this.owner = playerInv.player;
        this.installedSlot = installedSlot;
        this.installedIndex = installedIndex;
        this.provider = playerInv.player.level().registryAccess();

        this.injectorInv = new SimpleContainer(SpinalInjectorItem.SLOT_COUNT) {
            @Override
            public boolean stillValid(Player player) {
                return true;
            }

            @Override
            public void setChanged() {
                super.setChanged();
                SpinalInjectorMenu.this.syncCountsFromStacks();
            }
        };

        for (int i = 0; i < SpinalInjectorItem.SLOT_COUNT; i++) {
            final int idx = i;
            this.addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return injectorCounts[idx];
                }

                @Override
                public void set(int value) {
                    injectorCounts[idx] = value;
                    SpinalInjectorMenu.this.applyClientCountToStack(idx, value);
                }
            });
        }

        if (owner instanceof ServerPlayer sp) {
            ItemStack real = getRealInstalledInjectorStack(sp);
            this.serverFallbackSnapshot = real.isEmpty() ? ItemStack.EMPTY : real.copy();

            SpinalInjectorItem.loadFromInstalledStack(real, provider, injectorInv, injectorCounts);
            applyStoredCountsToStacks();
            sanitizeInjectorState();
        }

        for (int i = 0; i < SpinalInjectorItem.SLOT_COUNT; i++) {
            this.addSlot(new Slot(injectorInv, i, INJECTOR_X[i], INJECTOR_Y[i]) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return SpinalInjectorItem.isInjectable(stack);
                }

                @Override
                public int getMaxStackSize(ItemStack stack) {
                    return getInjectorStackLimit(stack);
                }

                @Override
                public int getMaxStackSize() {
                    return INJECTOR_MAX;
                }

                @Override
                public void setChanged() {
                    super.setChanged();
                    SpinalInjectorMenu.this.syncCountsFromStacks();
                }
            });
        }

        int invY = 152;
        int invX = 13;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, invX + col * 18, invY + row * 18));
            }
        }

        int hotbarY = invY + 58;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, invX + col * 18, hotbarY));
        }

        syncCountsFromStacks();
    }

    public int getInjectorDisplayCount(int slot) {
        if (slot < 0 || slot >= injectorCounts.length) {
            return 0;
        }

        return injectorCounts[slot];
    }

    public int getInjectorSlotX(int i) {
        return INJECTOR_X[i];
    }

    public int getInjectorSlotY(int i) {
        return INJECTOR_Y[i];
    }

    private static int getInjectorStackLimit(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return INJECTOR_MAX;
        }

        return Math.max(1, Math.min(INJECTOR_MAX, SpinalInjectorItem.maxStackFor(stack)));
    }

    private ItemStack getRealInstalledInjectorStack(ServerPlayer sp) {
        if (!sp.hasData(ModAttachments.CYBERWARE)) {
            return ItemStack.EMPTY;
        }

        PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
        if (data == null) {
            return ItemStack.EMPTY;
        }

        var installed = data.get(installedSlot, installedIndex);
        if (installed == null) {
            return ItemStack.EMPTY;
        }

        ItemStack real = installed.getItem();
        return real == null ? ItemStack.EMPTY : real;
    }

    private void applyStoredCountsToStacks() {
        for (int i = 0; i < SpinalInjectorItem.SLOT_COUNT; i++) {
            ItemStack stack = injectorInv.getItem(i);
            int count = injectorCounts[i];

            if (stack == null || stack.isEmpty() || count <= 0) {
                injectorInv.setItem(i, ItemStack.EMPTY);
                injectorCounts[i] = 0;
                continue;
            }

            int cap = getInjectorStackLimit(stack);
            int clamped = Math.min(cap, count);

            stack.setCount(clamped);
            injectorInv.setItem(i, stack);
            injectorCounts[i] = clamped;
        }
    }

    private void applyClientCountToStack(int slot, int value) {
        if (slot < 0 || slot >= SpinalInjectorItem.SLOT_COUNT) {
            return;
        }

        ItemStack stack = injectorInv.getItem(slot);
        if (stack == null || stack.isEmpty()) {
            return;
        }

        int cap = getInjectorStackLimit(stack);
        int clamped = Math.max(0, Math.min(cap, value));

        if (clamped <= 0) {
            injectorInv.setItem(slot, ItemStack.EMPTY);
            return;
        }

        stack.setCount(clamped);
        injectorInv.setItem(slot, stack);
    }

    private void sanitizeInjectorState() {
        for (int i = 0; i < SpinalInjectorItem.SLOT_COUNT; i++) {
            ItemStack stack = injectorInv.getItem(i);

            if (stack == null || stack.isEmpty()) {
                injectorInv.setItem(i, ItemStack.EMPTY);
                injectorCounts[i] = 0;
                continue;
            }

            if (!SpinalInjectorItem.isInjectable(stack)) {
                injectorInv.setItem(i, ItemStack.EMPTY);
                injectorCounts[i] = 0;
                continue;
            }

            int cap = getInjectorStackLimit(stack);
            int count = stack.getCount();

            if (count <= 0) {
                injectorInv.setItem(i, ItemStack.EMPTY);
                injectorCounts[i] = 0;
                continue;
            }

            if (count > cap) {
                stack.setCount(cap);
                injectorInv.setItem(i, stack);
                count = cap;
            }

            injectorCounts[i] = count;
        }
    }

    private void syncCountsFromStacks() {
        for (int i = 0; i < SpinalInjectorItem.SLOT_COUNT; i++) {
            ItemStack stack = injectorInv.getItem(i);

            if (stack == null || stack.isEmpty() || !SpinalInjectorItem.isInjectable(stack)) {
                injectorCounts[i] = 0;
                continue;
            }

            injectorCounts[i] = Math.max(0, Math.min(getInjectorStackLimit(stack), stack.getCount()));
        }
    }

    private void mirrorIntoPlayerData(ServerPlayer sp, PlayerCyberwareData data) {
        for (int i = 0; i < SpinalInjectorItem.SLOT_COUNT; i++) {
            ItemStack stack = injectorInv.getItem(i);

            if (stack == null || stack.isEmpty() || !SpinalInjectorItem.isInjectable(stack)) {
                data.setSpinalInjectorStack(i, ItemStack.EMPTY);
                continue;
            }

            ItemStack copy = stack.copy();
            copy.setCount(Math.max(1, Math.min(getInjectorStackLimit(copy), stack.getCount())));

            data.setSpinalInjectorStack(i, copy);
        }
    }

    @Override
    public void broadcastChanges() {
        if (owner instanceof ServerPlayer) {
            sanitizeInjectorState();
            syncCountsFromStacks();
        }

        super.broadcastChanges();
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);

        if (container == injectorInv && owner instanceof ServerPlayer) {
            sanitizeInjectorState();
            syncCountsFromStacks();
            this.broadcastChanges();
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (player.level().isClientSide) {
            return ItemStack.EMPTY;
        }

        if (index < 0 || index >= this.slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        int injectorEnd = SpinalInjectorItem.SLOT_COUNT;
        int playerStart = injectorEnd;
        int playerEnd = this.slots.size();

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < injectorEnd) {
            if (!this.moveItemStackTo(stack, playerStart, playerEnd, true)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            sanitizeInjectorState();
            syncCountsFromStacks();
            this.broadcastChanges();

            return original;
        }

        if (!SpinalInjectorItem.isInjectable(stack)) {
            return ItemStack.EMPTY;
        }

        if (!this.moveItemStackTo(stack, 0, injectorEnd, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        sanitizeInjectorState();
        syncCountsFromStacks();
        this.broadcastChanges();

        return original;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (!(player instanceof ServerPlayer sp)) {
            return;
        }

        sanitizeInjectorState();
        syncCountsFromStacks();

        PlayerCyberwareData data = sp.hasData(ModAttachments.CYBERWARE) ? sp.getData(ModAttachments.CYBERWARE) : null;

        ItemStack real = getRealInstalledInjectorStack(sp);
        boolean stillInstalled = !real.isEmpty() && real.getItem() == ModItems.BONEUPGRADES_SPINALINJECTOR.get();

        if (stillInstalled && data != null) {
            SpinalInjectorItem.saveIntoInstalledStack(real, provider, injectorInv, injectorCounts);
            mirrorIntoPlayerData(sp, data);

            data.setDirty();
            sp.syncData(ModAttachments.CYBERWARE);
            return;
        }

        ItemStack toDrop = real.isEmpty() ? serverFallbackSnapshot : real;

        if (!toDrop.isEmpty()) {
            SpinalInjectorItem.saveIntoInstalledStack(toDrop, provider, injectorInv, injectorCounts);
            SpinalInjectorItem.dropAndClearInstalledStack(sp, provider, toDrop);
        }

        if (data != null) {
            for (int i = 0; i < SpinalInjectorItem.SLOT_COUNT; i++) {
                data.setSpinalInjectorStack(i, ItemStack.EMPTY);
            }

            data.setDirty();
            sp.syncData(ModAttachments.CYBERWARE);
        }
    }

    private boolean isInjectorMenuSlot(int slotId) {
        return slotId >= 0 && slotId < SpinalInjectorItem.SLOT_COUNT;
    }

    private boolean isInjectorSlot(Slot slot) {
        int slotId = this.slots.indexOf(slot);
        return isInjectorMenuSlot(slotId);
    }

    @Override
    public boolean canDragTo(Slot slot) {
        return !isInjectorSlot(slot) && super.canDragTo(slot);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return !isInjectorSlot(slot) && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}