package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public final class HeatEngineMenu extends AbstractContainerMenu {
    // --- Slot positions (absolute in GUI) ---
    public static final int INPUT_X = 72;
    public static final int INPUT_Y = 19;

    public static final int OUTPUT_X = 96;
    public static final int OUTPUT_Y = 19;

    public static final int FUEL_X = 84;
    public static final int FUEL_Y = 39;

    public static final int SLOT_SIZE = 18;

    // --- Data slot indices ---
    private static final int IDX_BURN = 0;
    private static final int IDX_BURN_TOTAL = 1;
    private static final int IDX_COOK = 2;
    private static final int IDX_COOK_TOTAL = 3;

    private final Player player;
    private final PlayerCyberwareData data; // can be null on client if attachment not present

    private final Container heatContainer;

    // client-side mirror updated via ContainerData#set
    private int cBurn;
    private int cBurnTotal;
    private int cCook;
    private int cCookTotal;

    private final ContainerData syncedData = new ContainerData() {
        @Override
        public int get(int index) {
            // Server uses attachment. Client uses mirrored ints (set via packets).
            if (!player.level().isClientSide && data != null) {
                return switch (index) {
                    case IDX_BURN -> data.getHeatEngineBurnTime();
                    case IDX_BURN_TOTAL -> data.getHeatEngineBurnTimeTotal();
                    case IDX_COOK -> data.getHeatEngineCookTime();
                    case IDX_COOK_TOTAL -> data.getHeatEngineCookTimeTotal();
                    default -> 0;
                };
            }

            return switch (index) {
                case IDX_BURN -> cBurn;
                case IDX_BURN_TOTAL -> cBurnTotal;
                case IDX_COOK -> cCook;
                case IDX_COOK_TOTAL -> cCookTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // Called client-side when server syncs data slots
            switch (index) {
                case IDX_BURN -> cBurn = value;
                case IDX_BURN_TOTAL -> cBurnTotal = value;
                case IDX_COOK -> cCook = value;
                case IDX_COOK_TOTAL -> cCookTotal = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    // REQUIRED by IContainerFactory<T> used by IMenuTypeExtension.create(...)
    public HeatEngineMenu(int containerId, Inventory inv, FriendlyByteBuf buf) {
        this(containerId, inv);
    }

    public HeatEngineMenu(int containerId, Inventory inv) {
        super(ModMenuTypes.HEAT_ENGINE_MENU.get(), containerId);

        this.player = inv.player;

        // Be defensive: attachment may not be present client-side in some setups
        PlayerCyberwareData maybe = null;
        if (player.hasData(ModAttachments.CYBERWARE)) {
            maybe = player.getData(ModAttachments.CYBERWARE);
        }
        this.data = maybe;

        this.heatContainer = new HeatBackedContainer(this.data);

        // Menu slots (0..2)
        this.addSlot(new InputSlot(player.level(), heatContainer, PlayerCyberwareData.HEAT_ENGINE_INPUT, INPUT_X, INPUT_Y));          // index 0
        this.addSlot(new OutputSlot(heatContainer, PlayerCyberwareData.HEAT_ENGINE_OUTPUT, OUTPUT_X, OUTPUT_Y));                     // index 1
        this.addSlot(new FuelSlot(heatContainer, PlayerCyberwareData.HEAT_ENGINE_FUEL, FUEL_X, FUEL_Y));                             // index 2

        // Player inventory
        int invX = 12;
        int invY = 81;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, 9 + row * 9 + col, invX + col * 18, invY + row * 18));
            }
        }

        // Hotbar
        int hotbarY = 139;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, invX + col * 18, hotbarY));
        }

        this.addDataSlots(syncedData);
    }

    // --- Client helpers (read from synced data, not raw fields) ---

    public boolean isActiveClient() {
        return syncedData.get(IDX_BURN) > 0;
    }

    public int getBurnTimeClient() {
        return Math.max(0, syncedData.get(IDX_BURN));
    }

    public int getBurnTimeTotalClient() {
        return Math.max(1, syncedData.get(IDX_BURN_TOTAL));
    }

    public int getCookTimeClient() {
        return Math.max(0, syncedData.get(IDX_COOK));
    }

    public int getCookTimeTotalClient() {
        return Math.max(1, syncedData.get(IDX_COOK_TOTAL));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return empty;

        ItemStack source = slot.getItem();
        ItemStack copy = source.copy();

        final int HEAT_SLOT_COUNT = 3;
        final int PLAYER_INV_START = HEAT_SLOT_COUNT;
        final int PLAYER_INV_END = this.slots.size();

        if (index < PLAYER_INV_START) {
            // from heat slots -> player inv
            if (!this.moveItemStackTo(source, PLAYER_INV_START, PLAYER_INV_END, true)) return empty;
        } else {
            // from player -> to heat slots
            if (AbstractFurnaceBlockEntity.isFuel(source)) {
                // fuel slot = 2
                if (!this.moveItemStackTo(source, 2, 3, false)) return empty;
            } else if (isSmeltable(player.level(), source)) {
                // input slot = 0
                if (!this.moveItemStackTo(source, 0, 1, false)) return empty;
            } else {
                return empty;
            }
        }

        if (source.isEmpty()) slot.set(empty);
        else slot.setChanged();

        slot.onTake(player, source);
        return copy;
    }

    private static boolean isSmeltable(Level level, ItemStack stack) {
        if (stack.isEmpty()) return false;
        return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level).isPresent();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    private static final class HeatBackedContainer extends SimpleContainer {
        private final PlayerCyberwareData data;

        private HeatBackedContainer(PlayerCyberwareData data) {
            super(PlayerCyberwareData.HEAT_ENGINE_SLOT_COUNT);
            this.data = data;
        }

        @Override
        public ItemStack getItem(int slot) {
            if (data == null) return ItemStack.EMPTY;
            return data.getHeatEngineStack(slot);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (data == null) return;
            data.setHeatEngineStack(slot, stack);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            if (data == null) return ItemStack.EMPTY;
            return data.removeHeatEngineStack(slot, amount);
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return slot != PlayerCyberwareData.HEAT_ENGINE_OUTPUT;
        }

        @Override
        public void setChanged() {
            if (data != null) data.setDirty();
            super.setChanged();
        }
    }

    private static final class FuelSlot extends Slot {
        private FuelSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return AbstractFurnaceBlockEntity.isFuel(stack);
        }
    }

    private static final class InputSlot extends Slot {
        private final Level level;

        private InputSlot(Level level, Container container, int index, int x, int y) {
            super(container, index, x, y);
            this.level = level;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (stack.isEmpty()) return false;
            return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level).isPresent();
        }
    }

    private static final class OutputSlot extends Slot {
        private OutputSlot(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}
