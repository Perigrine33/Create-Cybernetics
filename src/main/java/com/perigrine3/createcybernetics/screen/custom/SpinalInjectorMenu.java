package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.item.cyberware.SpinalInjectorItem;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SpinalInjectorMenu extends AbstractContainerMenu {

    private static final int INJECTOR_MAX = 16;

    private static final int[] INJECTOR_X = { 49, 121, 49, 121 };
    private static final int[] INJECTOR_Y = { 29, 29, 101, 101 };

    private final Player owner;
    private final Container injectorInv;
    private final HolderLookup.Provider provider;

    private final CyberwareSlot installedSlot;
    private final int installedIndex;

    // Server-only snapshot for “uninstalled while open” drop behavior.
    private ItemStack serverFallbackSnapshot = ItemStack.EMPTY;

    // Synced UI-only counts (authoritative server values).
    private final int[] injectorCounts = new int[SpinalInjectorItem.SLOT_COUNT];

    public int getInjectorDisplayCount(int slot) {
        if (slot < 0 || slot >= injectorCounts.length) return 0;
        return injectorCounts[slot];
    }

    public int getInjectorSlotX(int i) { return INJECTOR_X[i]; }
    public int getInjectorSlotY(int i) { return INJECTOR_Y[i]; }

    public SpinalInjectorMenu(int id, Inventory playerInv, RegistryFriendlyByteBuf buf) {
        this(id, playerInv,
                CyberwareSlot.valueOf(buf.readUtf()),
                buf.readVarInt());
    }

    public SpinalInjectorMenu(int id, Inventory playerInv, CyberwareSlot installedSlot, int installedIndex) {
        super(ModMenuTypes.SPINAL_INJECTOR_MENU.get(), id);

        this.owner = playerInv.player;
        this.installedSlot = installedSlot;
        this.installedIndex = installedIndex;

        this.provider = playerInv.player.level().registryAccess();

        this.injectorInv = new SimpleContainer(SpinalInjectorItem.SLOT_COUNT) {
            @Override public boolean stillValid(Player p) { return true; }
        };

        // DataSlots: sync each injector count server -> client.
        for (int i = 0; i < SpinalInjectorItem.SLOT_COUNT; i++) {
            final int idx = i;
            this.addDataSlot(new DataSlot() {
                @Override public int get() { return injectorCounts[idx]; }
                @Override public void set(int value) { injectorCounts[idx] = value; }
            });
        }

        // Load from the REAL installed stack on the server.
        if (owner instanceof ServerPlayer sp) {
            ItemStack real = getRealInstalledInjectorStack(sp);

            this.serverFallbackSnapshot = real.isEmpty() ? ItemStack.EMPTY : real.copy();

            SpinalInjectorItem.loadFromInstalledStack(real, provider, injectorInv, injectorCounts);
            sanitizeInjectorState(); // keep representative stacks at count=1 and clamp counts
        }

        // Injector slots
        for (int i = 0; i < SpinalInjectorItem.SLOT_COUNT; i++) {
            this.addSlot(new Slot(injectorInv, i, INJECTOR_X[i], INJECTOR_Y[i]) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return SpinalInjectorItem.isInjectable(stack);
                }

                @Override
                public int getMaxStackSize(ItemStack stack) {
                    return Math.min(INJECTOR_MAX, SpinalInjectorItem.maxStackFor(stack));
                }

                @Override
                public int getMaxStackSize() {
                    return INJECTOR_MAX;
                }
            });
        }

        // Player inventory
        int invY = 152;
        int invX = 13;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, invX + col * 18, invY + row * 18));
            }
        }

        // Hotbar
        int hotbarY = invY + 58;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, invX + col * 18, hotbarY));
        }
    }

    private ItemStack getRealInstalledInjectorStack(ServerPlayer sp) {
        if (!sp.hasData(ModAttachments.CYBERWARE)) return ItemStack.EMPTY;

        PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
        if (data == null) return ItemStack.EMPTY;

        var inst = data.get(installedSlot, installedIndex);
        if (inst == null) return ItemStack.EMPTY;

        ItemStack real = inst.getItem();
        return real == null ? ItemStack.EMPTY : real;
    }

    private void sanitizeInjectorState() {
        for (int i = 0; i < SpinalInjectorItem.SLOT_COUNT; i++) {
            ItemStack st = injectorInv.getItem(i);

            if (st.isEmpty()) {
                injectorCounts[i] = 0;
                continue;
            }

            if (!SpinalInjectorItem.isInjectable(st)) {
                injectorInv.setItem(i, ItemStack.EMPTY);
                injectorCounts[i] = 0;
                continue;
            }

            // Representative stack always count 1
            if (st.getCount() != 1) st.setCount(1);

            int cap = Math.min(INJECTOR_MAX, SpinalInjectorItem.maxStackFor(st));
            int c = injectorCounts[i];
            if (c <= 0) c = 1; // if an item exists, minimum stored count is 1
            injectorCounts[i] = Math.min(cap, c);
        }
    }

    @Override
    public void broadcastChanges() {
        if (owner instanceof ServerPlayer) {
            sanitizeInjectorState();
        }
        super.broadcastChanges();
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);

        if (container == injectorInv && owner instanceof ServerPlayer) {
            this.broadcastChanges();
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        // Client: default
        if (player.level().isClientSide) {
            super.clicked(slotId, button, clickType, player);
            return;
        }

        // Server: custom handle injector slots for PICKUP (your intent)
        if (clickType == ClickType.PICKUP && slotId >= 0 && slotId < SpinalInjectorItem.SLOT_COUNT) {
            Slot target = this.slots.get(slotId);
            ItemStack carried = this.getCarried();
            ItemStack inSlot = target.getItem();

            // Take ONE out if cursor empty
            if (carried.isEmpty()) {
                if (inSlot.isEmpty()) return;

                int cur = injectorCounts[slotId];
                if (cur <= 0) cur = 1;

                ItemStack one = inSlot.copy();
                one.setCount(1);
                this.setCarried(one);

                cur -= 1;
                injectorCounts[slotId] = cur;

                if (cur <= 0) {
                    target.set(ItemStack.EMPTY);
                    injectorCounts[slotId] = 0;
                }

                target.setChanged();
                this.broadcastChanges();
                return;
            }

            // Insert/merge
            if (!carried.isEmpty() && SpinalInjectorItem.isInjectable(carried)) {
                int want = (button == 1) ? 1 : carried.getCount();
                int cap = Math.min(INJECTOR_MAX, SpinalInjectorItem.maxStackFor(carried));

                if (inSlot.isEmpty()) {
                    int move = Math.min(want, cap);
                    if (move <= 0) return;

                    ItemStack rep = carried.copy();
                    rep.setCount(1);

                    target.set(rep);
                    injectorCounts[slotId] = move;

                    carried.shrink(move);
                    this.setCarried(carried);

                    target.setChanged();
                    this.broadcastChanges();
                    return;
                }

                if (ItemStack.isSameItemSameComponents(inSlot, carried)) {
                    int cur = injectorCounts[slotId];
                    if (cur <= 0) cur = 1;

                    int space = cap - cur;
                    int move = Math.min(space, want);
                    if (move <= 0) return;

                    injectorCounts[slotId] = cur + move;

                    carried.shrink(move);
                    this.setCarried(carried);

                    target.setChanged();
                    this.broadcastChanges();
                    return;
                }

                // Different potion: fall through to vanilla behavior
            }
        }

        super.clicked(slotId, button, clickType, player);
    }

    private boolean movePotionIntoInjector(ItemStack stack) {
        if (stack.isEmpty() || !SpinalInjectorItem.isInjectable(stack)) return false;

        boolean movedAny = false;

        // Merge into existing first
        for (int i = 0; i < SpinalInjectorItem.SLOT_COUNT && !stack.isEmpty(); i++) {
            Slot s = this.slots.get(i);
            ItemStack curStack = s.getItem();
            if (curStack.isEmpty()) continue;

            if (ItemStack.isSameItemSameComponents(curStack, stack)) {
                int cap = Math.min(INJECTOR_MAX, SpinalInjectorItem.maxStackFor(curStack));
                int cur = injectorCounts[i];
                if (cur <= 0) cur = 1;

                int space = cap - cur;
                if (space <= 0) continue;

                int move = Math.min(space, stack.getCount());
                if (move > 0) {
                    injectorCounts[i] = cur + move;
                    stack.shrink(move);
                    s.setChanged();
                    movedAny = true;
                }
            }
        }

        // Then fill empties
        for (int i = 0; i < SpinalInjectorItem.SLOT_COUNT && !stack.isEmpty(); i++) {
            Slot s = this.slots.get(i);
            if (!s.getItem().isEmpty()) continue;

            int cap = Math.min(INJECTOR_MAX, SpinalInjectorItem.maxStackFor(stack));
            int move = Math.min(cap, stack.getCount());
            if (move <= 0) continue;

            ItemStack rep = stack.copy();
            rep.setCount(1);

            s.set(rep);
            injectorCounts[i] = move;

            stack.shrink(move);
            s.setChanged();
            movedAny = true;
        }

        if (movedAny) this.broadcastChanges();
        return movedAny;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (player.level().isClientSide) return ItemStack.EMPTY;

        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        int injectorEnd = SpinalInjectorItem.SLOT_COUNT;
        int playerStart = injectorEnd;
        int playerEnd = this.slots.size();

        // Injector -> player: move up to stored count as singles
        if (index < injectorEnd) {
            ItemStack base = slot.getItem();
            int cur = injectorCounts[index];
            if (cur <= 0) cur = 1;

            int moved = 0;
            for (int n = 0; n < cur; n++) {
                ItemStack one = base.copy();
                one.setCount(1);

                if (!this.moveItemStackTo(one, playerStart, playerEnd, true)) break;
                moved++;
            }

            if (moved == 0) return ItemStack.EMPTY;

            injectorCounts[index] = cur - moved;
            if (injectorCounts[index] <= 0) {
                injectorCounts[index] = 0;
                slot.set(ItemStack.EMPTY);
            }

            slot.setChanged();
            this.broadcastChanges();
            return base.copy();
        }

        // Player -> injector
        ItemStack in = slot.getItem();
        if (!SpinalInjectorItem.isInjectable(in)) return ItemStack.EMPTY;

        ItemStack original = in.copy();

        if (!movePotionIntoInjector(in)) return ItemStack.EMPTY;

        if (in.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        this.broadcastChanges();
        return original;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (!(player instanceof ServerPlayer sp)) return;

        PlayerCyberwareData data = sp.hasData(ModAttachments.CYBERWARE) ? sp.getData(ModAttachments.CYBERWARE) : null;

        ItemStack real = getRealInstalledInjectorStack(sp);
        boolean stillInstalled = !real.isEmpty() && real.getItem() == ModItems.BONEUPGRADES_SPINALINJECTOR.get();

        if (stillInstalled && data != null) {
            SpinalInjectorItem.saveIntoInstalledStack(real, provider, injectorInv, injectorCounts);
            data.setDirty();
            sp.syncData(ModAttachments.CYBERWARE);
            return;
        }

        // Not installed anymore: drop and clear.
        // If real is empty, fall back to snapshot BUT first write the latest GUI state into it.
        ItemStack toDrop = real.isEmpty() ? serverFallbackSnapshot : real;

        if (!toDrop.isEmpty()) {
            SpinalInjectorItem.saveIntoInstalledStack(toDrop, provider, injectorInv, injectorCounts);
            SpinalInjectorItem.dropAndClearInstalledStack(sp, provider, toDrop);
        }

        if (data != null) {
            data.setDirty();
            sp.syncData(ModAttachments.CYBERWARE);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
