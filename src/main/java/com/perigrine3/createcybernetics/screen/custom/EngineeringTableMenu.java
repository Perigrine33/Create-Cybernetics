package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.block.entity.EngineeringTableBlockEntity;
import com.perigrine3.createcybernetics.recipe.EngineeringTableRecipe;
import com.perigrine3.createcybernetics.recipe.EngineeringTableRecipeInput;
import com.perigrine3.createcybernetics.recipe.ModRecipes;
import com.perigrine3.createcybernetics.screen.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EngineeringTableMenu extends AbstractContainerMenu {

    private static final int CRAFT_START_X = 66;
    private static final int CRAFT_START_Y = 65;
    private static final int SLOT_SPACING = 18;

    private static final int RESULT_X = 175;
    private static final int RESULT_Y = 101;

    // Deconstruct panel (input + 6 outputs)
    private static final int DECON_INPUT_X = 28;
    private static final int DECON_INPUT_Y = 62;

    private static final int DECON_OUT_START_X = 19;
    private static final int DECON_OUT_START_Y = 97;
    private static final int DECON_OUT_COLS = 2;
    private static final int DECON_OUT_ROWS = 3;

    private static final int INV_START_X = 48;
    private static final int INV_START_Y = 162;
    private static final int HOTBAR_Y = 220;

    private static final int GRID_W = 5;
    private static final int GRID_H = 5;
    private static final int GRID_SLOTS = GRID_W * GRID_H;

    private final ContainerLevelAccess access;
    private final Level level;
    private final Player player;
    private final BlockPos pos;

    private final ItemStackHandler grid;
    private final ResultContainer resultSlots = new ResultContainer();

    private final ItemStackHandler deconstructInput;
    private final ItemStackHandler deconstructOutputs;

    private RecipeHolder<EngineeringTableRecipe> lastRecipe = null;

    public EngineeringTableMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, buf.readBlockPos());
    }

    public EngineeringTableMenu(int id, Inventory inv, BlockPos pos) {
        super(ModMenuTypes.ENGINEERING_TABLE_MENU.get(), id);

        this.player = inv.player;
        this.level = inv.player.level();
        this.pos = pos;
        this.access = ContainerLevelAccess.create(this.level, pos);

        // Default fallbacks (only used if BE isn't available for some reason)
        ItemStackHandler gridHandler = new ItemStackHandler(GRID_SLOTS);
        ItemStackHandler deconInHandler = new ItemStackHandler(EngineeringTableBlockEntity.DECONSTRUCT_INPUT_SIZE);
        ItemStackHandler deconOutHandler = new ItemStackHandler(EngineeringTableBlockEntity.DECONSTRUCT_OUTPUT_SIZE);

        // IMPORTANT: Bind handlers on BOTH sides.
        // Server: use server BE.
        // Client: use client BE (from client level).
        if (this.level.getBlockEntity(pos) instanceof EngineeringTableBlockEntity be) {
            gridHandler = be.getCrafting();
            deconInHandler = be.getDeconstructInput();
            deconOutHandler = be.getDeconstructOutputs();
        }

        this.grid = gridHandler;
        this.deconstructInput = deconInHandler;
        this.deconstructOutputs = deconOutHandler;

        // 0 = craft result
        this.addSlot(new EngineeringResultSlot(this.resultSlots, 0, RESULT_X, RESULT_Y));

        // 1..25 = craft grid
        int idx = 0;
        for (int row = 0; row < GRID_H; row++) {
            for (int col = 0; col < GRID_W; col++) {
                this.addSlot(new GridSlot(
                        this.grid,
                        idx++,
                        CRAFT_START_X + col * SLOT_SPACING,
                        CRAFT_START_Y + row * SLOT_SPACING
                ));
            }
        }

        // 26 = deconstruct input
        this.addSlot(new DeconstructInputSlot(this.deconstructInput, 0, DECON_INPUT_X, DECON_INPUT_Y));

        // 27..32 = deconstruct outputs (6 slots), 2 cols x 3 rows
        int outIndex = 0;
        for (int row = 0; row < DECON_OUT_ROWS; row++) {
            for (int col = 0; col < DECON_OUT_COLS; col++) {
                this.addSlot(new DeconstructOutputSlot(
                        this.deconstructOutputs,
                        outIndex++,
                        DECON_OUT_START_X + col * SLOT_SPACING,
                        DECON_OUT_START_Y + row * SLOT_SPACING
                ));
            }
        }

        // 33..59 = player inv (27)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(
                        inv,
                        9 + row * 9 + col,
                        INV_START_X + col * SLOT_SPACING,
                        INV_START_Y + row * SLOT_SPACING
                ));
            }
        }

        // 60..68 = hotbar (9)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(
                    inv,
                    col,
                    INV_START_X + col * SLOT_SPACING,
                    HOTBAR_Y
            ));
        }

        if (!this.level.isClientSide) {
            updateResult();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.ENGINEERING_TABLE.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        // Do NOT clear handlers; they persist in the BE.
    }

    private EngineeringTableRecipeInput buildEngineeringInput() {
        List<ItemStack> items = new ArrayList<>(GRID_SLOTS);
        for (int i = 0; i < GRID_SLOTS; i++) {
            items.add(this.grid.getStackInSlot(i).copy());
        }
        return new EngineeringTableRecipeInput(items);
    }

    private void updateResult() {
        if (this.level.isClientSide) return;

        EngineeringTableRecipeInput input = buildEngineeringInput();

        Optional<RecipeHolder<EngineeringTableRecipe>> opt =
                this.level.getRecipeManager().getRecipeFor(ModRecipes.ENGINEERING_TABLE_TYPE.get(), input, this.level);

        this.lastRecipe = opt.orElse(null);

        ItemStack out = ItemStack.EMPTY;
        if (this.lastRecipe != null) {
            out = this.lastRecipe.value().assemble(input, this.level.registryAccess());
        }

        this.resultSlots.setItem(0, out);
        this.broadcastChanges();
    }

    private boolean deconstructOutputsEmpty() {
        for (int i = 0; i < EngineeringTableBlockEntity.DECONSTRUCT_OUTPUT_SIZE; i++) {
            if (!deconstructOutputs.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return empty;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        // Slot layout:
        // 0 = craft result
        // 1..25 = craft grid
        // 26 = deconstruct input
        // 27..32 = deconstruct outputs (6)
        // 33..59 = player inv
        // 60..68 = hotbar
        final int CRAFT_RESULT = 0;
        final int CRAFT_GRID_START = 1;
        final int CRAFT_GRID_END_EXCL = 26; // 1..25
        final int DECON_INPUT = 26;
        final int DECON_OUT_START = 27;
        final int DECON_OUT_END_EXCL = 33; // 27..32
        final int PLAYER_INV_START = 33;
        final int PLAYER_INV_END_EXCL = 69; // 33..68

        if (index == CRAFT_RESULT) {
            if (!this.moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_END_EXCL, true)) return ItemStack.EMPTY;
            slot.onQuickCraft(stack, copy);
        } else if (index >= DECON_OUT_START && index < DECON_OUT_END_EXCL) {
            // shift-click outputs -> player inventory
            if (!this.moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_END_EXCL, true)) return ItemStack.EMPTY;
        } else if (index >= PLAYER_INV_START && index < PLAYER_INV_END_EXCL) {
            // shift-click from player -> craft grid ONLY (never into deconstruct input)
            if (!this.moveItemStackTo(stack, CRAFT_GRID_START, CRAFT_GRID_END_EXCL, false)) return ItemStack.EMPTY;
        } else if (index >= CRAFT_GRID_START && index < CRAFT_GRID_END_EXCL) {
            // shift-click from craft grid -> player
            if (!this.moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_END_EXCL, false)) return ItemStack.EMPTY;
        } else if (index == DECON_INPUT) {
            // If somehow shift-clicked, push back to player inventory.
            if (!this.moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_END_EXCL, false)) return ItemStack.EMPTY;
        } else {
            if (!this.moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_END_EXCL, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        if (stack.getCount() == copy.getCount()) return ItemStack.EMPTY;

        slot.onTake(player, stack);

        if (!this.level.isClientSide) {
            updateResult();
        }

        return copy;
    }

    private final class GridSlot extends SlotItemHandler {
        private GridSlot(ItemStackHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            if (!level.isClientSide) updateResult();
        }

        @Override
        public void set(ItemStack stack) {
            super.set(stack);
            if (!level.isClientSide) updateResult();
        }

        @Override
        public ItemStack remove(int amount) {
            ItemStack out = super.remove(amount);
            if (!level.isClientSide) updateResult();
            return out;
        }
    }

    private final class DeconstructInputSlot extends SlotItemHandler {
        private DeconstructInputSlot(ItemStackHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            // Only accept cyberware/scavenged, and only if outputs are empty.
            if (!EngineeringTableBlockEntity.isDeconstructable(stack)) return false;
            if (!deconstructOutputsEmpty()) return false;
            return true;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void set(ItemStack stack) {
            super.set(stack);
        }
    }

    private final class DeconstructOutputSlot extends SlotItemHandler {
        private DeconstructOutputSlot(ItemStackHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }

    private record Placement(int offX, int offY, boolean mirror) {}

    private Placement findPlacement(EngineeringTableRecipe recipe, EngineeringTableRecipeInput input) {
        int w = recipe.width();
        int h = recipe.height();
        if (w <= 0 || h <= 0 || w > GRID_W || h > GRID_H) return null;

        int maxX = GRID_W - w;
        int maxY = GRID_H - h;

        for (int offY = 0; offY <= maxY; offY++) {
            for (int offX = 0; offX <= maxX; offX++) {
                if (matchesAt(recipe, input, offX, offY, false)) return new Placement(offX, offY, false);
                if (recipe.accept_mirrored() && matchesAt(recipe, input, offX, offY, true)) return new Placement(offX, offY, true);
            }
        }
        return null;
    }

    private boolean matchesAt(EngineeringTableRecipe recipe, EngineeringTableRecipeInput input, int offX, int offY, boolean mirror) {
        int w = recipe.width();
        int h = recipe.height();
        NonNullList<Ingredient> ings = recipe.ingredients();

        for (int y = 0; y < GRID_H; y++) {
            for (int x = 0; x < GRID_W; x++) {
                int gridIndex = x + y * GRID_W;

                boolean inside =
                        x >= offX && x < offX + w &&
                                y >= offY && y < offY + h;

                ItemStack stack = input.getItem(gridIndex);

                if (!inside) {
                    if (!stack.isEmpty()) return false;
                    continue;
                }

                int relX = x - offX;
                int relY = y - offY;

                int patX = mirror ? (w - 1 - relX) : relX;
                int patIndex = patX + relY * w;

                Ingredient ing = ings.get(patIndex);
                if (!ing.test(stack)) return false;
            }
        }
        return true;
    }

    private final class EngineeringResultSlot extends Slot {
        private EngineeringResultSlot(ResultContainer container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack crafted) {
            super.onTake(player, crafted);

            if (level.isClientSide) return;

            if (lastRecipe == null) {
                updateResult();
                return;
            }

            EngineeringTableRecipe recipe = lastRecipe.value();
            EngineeringTableRecipeInput input = buildEngineeringInput();

            Placement placement = findPlacement(recipe, input);
            if (placement == null) {
                updateResult();
                return;
            }

            int w = recipe.width();
            int h = recipe.height();
            NonNullList<Ingredient> ings = recipe.ingredients();

            for (int relY = 0; relY < h; relY++) {
                for (int relX = 0; relX < w; relX++) {
                    int patX = placement.mirror ? (w - 1 - relX) : relX;
                    int patIndex = patX + relY * w;

                    Ingredient ing = ings.get(patIndex);
                    if (ing.isEmpty()) continue;

                    int gridX = placement.offX + relX;
                    int gridY = placement.offY + relY;
                    int gridIndex = gridX + gridY * GRID_W;

                    ItemStack in = grid.getStackInSlot(gridIndex);
                    if (!in.isEmpty()) {
                        in.shrink(1);
                        grid.setStackInSlot(gridIndex, in);
                    }
                }
            }

            updateResult();
        }
    }
}
