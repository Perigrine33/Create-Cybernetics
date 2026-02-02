package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.recipe.GraftingTableRecipeInput;
import com.perigrine3.createcybernetics.recipe.ModRecipes;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public final class GraftingTableMenu extends AbstractContainerMenu {

    public static final class Layout {
        private Layout() {}

        public static final int GUI_W = 176;
        public static final int GUI_H = 166;

        public static final int IN00_X = 26, IN00_Y = 26;
        public static final int IN01_X = 44, IN01_Y = 26;
        public static final int IN10_X = 26, IN10_Y = 44;
        public static final int IN11_X = 44, IN11_Y = 44;

        public static final int MESH_X   = 71, MESH_Y   = 26;
        public static final int STRING_X = 71, STRING_Y = 44;
        public static final int TEAR_X   = 89, TEAR_Y   = 35;

        public static final int OUT_X = 131, OUT_Y = 35;

        public static final int INV_X = 8,  INV_Y = 84;
        public static final int HOT_X = 8,  HOT_Y = 142;
    }

    private static final int SLOT_IN_COUNT = 7;
    private static final int SLOT_OUT = 7;
    private static final int SLOT_PLAYER_START = 8;

    private final SimpleContainer inputs = new SimpleContainer(SLOT_IN_COUNT);
    private final SimpleContainer result = new SimpleContainer(1);

    private final ContainerLevelAccess access;

    private ItemStack computedResult = ItemStack.EMPTY;

    public GraftingTableMenu(int containerId, Inventory playerInv, ContainerLevelAccess access) {
        super(ModMenuTypes.GRAFTING_TABLE_MENU.get(), containerId);
        this.access = access;

        inputs.addListener(this::slotsChanged);

        this.addSlot(new Slot(inputs, 0, Layout.IN00_X, Layout.IN00_Y));
        this.addSlot(new Slot(inputs, 1, Layout.IN01_X, Layout.IN01_Y));
        this.addSlot(new Slot(inputs, 2, Layout.IN10_X, Layout.IN10_Y));
        this.addSlot(new Slot(inputs, 3, Layout.IN11_X, Layout.IN11_Y));
        this.addSlot(new OnlyItemSlot(inputs, 4, Layout.MESH_X, Layout.MESH_Y, ModItems.COMPONENT_MESH.get()));
        this.addSlot(new OnlyItemSlot(inputs, 5, Layout.STRING_X, Layout.STRING_Y, Items.STRING));
        this.addSlot(new OnlyItemSlot(inputs, 6, Layout.TEAR_X, Layout.TEAR_Y, Items.GHAST_TEAR));

        this.addSlot(new ResultSlot(result, 0, Layout.OUT_X, Layout.OUT_Y));

        addPlayerInventory(playerInv, Layout.INV_X, Layout.INV_Y);
        addHotbar(playerInv, Layout.HOT_X, Layout.HOT_Y);
    }

    public GraftingTableMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv, ContainerLevelAccess.NULL);
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);

        if (this.slots.isEmpty()) return;

        Player p = this.getPlayer();
        if (p == null || p.level().isClientSide) return;

        Level level = p.level();

        GraftingTableRecipeInput input = buildRecipeInput();
        computedResult = level.getRecipeManager()
                .getRecipeFor(ModRecipes.GRAFTING_TABLE_TYPE.get(), input, level)
                .map(holder -> holder.value().assemble(input, level.registryAccess()))
                .orElse(ItemStack.EMPTY);

        result.setItem(0, computedResult);
        broadcastChanges();
    }

    private Player getPlayer() {
        for (Slot s : this.slots) {
            if (s.container instanceof Inventory inv) {
                return inv.player;
            }
        }
        return null;
    }

    private GraftingTableRecipeInput buildRecipeInput() {
        return new com.perigrine3.createcybernetics.recipe.GraftingTableRecipeInput(java.util.List.of(
                inputs.getItem(0),
                inputs.getItem(1),
                inputs.getItem(2),
                inputs.getItem(3),
                inputs.getItem(4),
                inputs.getItem(5),
                inputs.getItem(6)
        ));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player.level().isClientSide) return;

        for (int i = 0; i < inputs.getContainerSize(); i++) {
            ItemStack stack = inputs.removeItemNoUpdate(i);
            if (!stack.isEmpty()) player.getInventory().placeItemBackInInventory(stack);
        }

        for (int i = 0; i < result.getContainerSize(); i++) {
            ItemStack stack = result.removeItemNoUpdate(i);
            if (!stack.isEmpty()) player.getInventory().placeItemBackInInventory(stack);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return empty;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        int playerEnd = this.slots.size();

        if (index == SLOT_OUT) {
            if (!this.moveItemStackTo(stack, SLOT_PLAYER_START, playerEnd, true)) return empty;
            slot.onQuickCraft(stack, copy);
        } else if (index >= SLOT_PLAYER_START) {
            if (stack.is(ModItems.COMPONENT_MESH.get())) {
                if (!this.moveItemStackTo(stack, 4, 5, false)) return empty;
            } else if (stack.is(Items.STRING)) {
                if (!this.moveItemStackTo(stack, 5, 6, false)) return empty;
            } else if (stack.is(Items.GHAST_TEAR)) {
                if (!this.moveItemStackTo(stack, 6, 7, false)) return empty;
            } else {
                if (!this.moveItemStackTo(stack, 0, 4, false)) return empty;
            }
        } else {
            if (!this.moveItemStackTo(stack, SLOT_PLAYER_START, playerEnd, true)) return empty;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return copy;
    }

    private void addPlayerInventory(Inventory inv, int left, int top) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, left + col * 18, top + row * 18));
            }
        }
    }

    private void addHotbar(Inventory inv, int left, int top) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, left + col * 18, top));
        }
    }

    private static final class OnlyItemSlot extends Slot {
        private final Item allowed;

        private OnlyItemSlot(Container container, int index, int x, int y, Item allowed) {
            super(container, index, x, y);
            this.allowed = allowed;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return !stack.isEmpty() && stack.is(allowed);
        }
    }

    private final class ResultSlot extends Slot {
        private ResultSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack taken) {
            super.onTake(player, taken);

            access.execute((level, pos) -> {
                com.perigrine3.createcybernetics.recipe.GraftingTableRecipeInput input = buildRecipeInput();

                var opt = level.getRecipeManager()
                        .getRecipeFor(com.perigrine3.createcybernetics.recipe.ModRecipes.GRAFTING_TABLE_TYPE.get(), input, level);

                if (opt.isEmpty()) return;

                var recipe = opt.get().value();
                var remains = recipe.getRemainingItems(input);

                for (int i = 0; i < 7; i++) {
                    ItemStack in = inputs.getItem(i);
                    if (!in.isEmpty()) {
                        in.shrink(1);
                        inputs.setItem(i, in);
                    }
                }

                for (int i = 0; i < 7; i++) {
                    ItemStack rem = remains.get(i);
                    if (!rem.isEmpty()) {
                        ItemStack cur = inputs.getItem(i);
                        if (cur.isEmpty()) {
                            inputs.setItem(i, rem);
                        } else if (!player.getInventory().add(rem)) {
                            player.drop(rem, false);
                        }
                    }
                }

                slotsChanged(inputs);
            });
        }
    }
}
