package com.perigrine3.createcybernetics.block.entity;

import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.screen.custom.EngineeringTableMenu;
import com.perigrine3.createcybernetics.sound.ModSounds;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EngineeringTableBlockEntity extends BlockEntity implements MenuProvider {

    public static final int GRID_SIZE = 25;

    public static final int DECONSTRUCT_INPUT_SIZE = 1;
    public static final int DECONSTRUCT_OUTPUT_SIZE = 6;

    private final ItemStackHandler crafting = new ItemStackHandler(GRID_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    // Guard to prevent recursion when we mutate handlers during deconstruction
    private boolean deconProcessing = false;

    private final ItemStackHandler deconstructInput = new ItemStackHandler(DECONSTRUCT_INPUT_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                // When server receives an input change, attempt instant deconstruct
                if (!deconProcessing && level instanceof ServerLevel sl) {
                    tryInstantDeconstruct(sl);
                }
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    private final ItemStackHandler deconstructOutputs = new ItemStackHandler(DECONSTRUCT_OUTPUT_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public EngineeringTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENGINEERING_TABLE_BLOCKENTITY.get(), pos, state);
    }

    public ItemStackHandler getCrafting() {
        return crafting;
    }

    public ItemStackHandler getDeconstructInput() {
        return deconstructInput;
    }

    public ItemStackHandler getDeconstructOutputs() {
        return deconstructOutputs;
    }

    public boolean outputsAreEmpty() {
        for (int i = 0; i < DECONSTRUCT_OUTPUT_SIZE; i++) {
            if (!deconstructOutputs.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    public static boolean isDeconstructable(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        // Accept either scavenged cyberware or regular cyberware
        return stack.is(ModTags.Items.SCAVENGED_CYBERWARE) || stack.is(ModTags.Items.CYBERWARE_ITEM);
    }

    /**
     * Server-side: consumes the input item (if valid) and fills all 6 output slots with rolled items.
     * Does nothing if outputs are not empty or input is invalid.
     */
    private void tryInstantDeconstruct(ServerLevel level) {
        ItemStack in = deconstructInput.getStackInSlot(0);
        if (in.isEmpty()) return;

        // Only deconstruct cyberware/scavenged cyberware
        if (!isDeconstructable(in)) return;

        // Don't overwrite existing outputs
        if (!outputsAreEmpty()) return;

        deconProcessing = true;
        try {
            // Consume input immediately
            deconstructInput.setStackInSlot(0, ItemStack.EMPTY);

            // Roll and fill outputs
            for (int i = 0; i < DECONSTRUCT_OUTPUT_SIZE; i++) {
                ItemStack rolled = rollFromPool(level.random, in);
                deconstructOutputs.setStackInSlot(i, rolled);
            }

            level.playSound(null, worldPosition, ModSounds.METAL_CRUSHING.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

            setChanged();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        } finally {
            deconProcessing = false;
        }
    }

    private static final class PoolEntry {
        final Item item;
        final int min;
        final int max;
        final int weight;

        PoolEntry(Item item, int min, int max, int weight) {
            this.item = item;
            this.min = min;
            this.max = max;
            this.weight = weight;
        }

        ItemStack create(RandomSource r) {
            int count = (min == max) ? min : Mth.nextInt(r, min, max);
            return new ItemStack(item, count);
        }
    }

    private ItemStack rollFromPool(RandomSource r, ItemStack originalInput) {
        boolean scavenged = originalInput.is(ModTags.Items.SCAVENGED_CYBERWARE);
        List<PoolEntry> pool = new ArrayList<>();

        if (scavenged) {
            pool.add(new PoolEntry(ModItems.COMPONENT_ACTUATOR.get(), 0, 2, 3));
            pool.add(new PoolEntry(ModItems.COMPONENT_FIBEROPTICS.get(), 0, 2, 3));
            pool.add(new PoolEntry(ModItems.COMPONENT_WIRING.get(), 0, 3, 5));
            pool.add(new PoolEntry(ModItems.COMPONENT_DIODES.get(), 0, 2, 2));
            pool.add(new PoolEntry(ModItems.COMPONENT_PLATING.get(), 0, 2, 5));
            pool.add(new PoolEntry(ModItems.COMPONENT_GRAPHICSCARD.get(), 0, 2, 2));
            pool.add(new PoolEntry(ModItems.COMPONENT_SSD.get(), 0, 2, 3));
            pool.add(new PoolEntry(ModItems.COMPONENT_STORAGE.get(), 0, 2, 4));
            pool.add(new PoolEntry(ModItems.COMPONENT_SYNTHNERVES.get(), 0, 2, 1));
            pool.add(new PoolEntry(ModItems.COMPONENT_MESH.get(), 0, 2, 2));
            if (ModItems.COMPONENT_LED != null) pool.add(new PoolEntry(ModItems.COMPONENT_LED.get(), 0, 2, 3));
            if (ModItems.COMPONENT_TITANIUMROD != null) pool.add(new PoolEntry(ModItems.COMPONENT_TITANIUMROD.get(), 0, 2, 2));
        } else {
            // Regular cyberware pool
            pool.add(new PoolEntry(ModItems.COMPONENT_ACTUATOR.get(), 0, 5, 4));
            pool.add(new PoolEntry(ModItems.COMPONENT_FIBEROPTICS.get(), 0, 5, 4));
            pool.add(new PoolEntry(ModItems.COMPONENT_WIRING.get(), 1, 5, 5));
            pool.add(new PoolEntry(ModItems.COMPONENT_DIODES.get(), 0, 5, 4));
            pool.add(new PoolEntry(ModItems.COMPONENT_PLATING.get(), 0, 5, 5));
            pool.add(new PoolEntry(ModItems.COMPONENT_GRAPHICSCARD.get(), 0, 5, 4));
            pool.add(new PoolEntry(ModItems.COMPONENT_SSD.get(), 0, 5, 4));
            pool.add(new PoolEntry(ModItems.COMPONENT_STORAGE.get(), 0, 5, 4));
            pool.add(new PoolEntry(ModItems.COMPONENT_SYNTHNERVES.get(), 0, 5, 3));
            pool.add(new PoolEntry(ModItems.COMPONENT_MESH.get(), 0, 5, 2));
            if (ModItems.COMPONENT_LED != null) pool.add(new PoolEntry(ModItems.COMPONENT_LED.get(), 0, 5, 3));
            if (ModItems.COMPONENT_TITANIUMROD != null) pool.add(new PoolEntry(ModItems.COMPONENT_TITANIUMROD.get(), 0, 5, 2));
        }

        if (pool.isEmpty()) return ItemStack.EMPTY;

        int total = 0;
        for (PoolEntry e : pool) total += e.weight;

        int roll = r.nextInt(total);
        for (PoolEntry e : pool) {
            roll -= e.weight;
            if (roll < 0) return e.create(r);
        }
        return pool.get(0).create(r);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(GRID_SIZE + DECONSTRUCT_INPUT_SIZE + DECONSTRUCT_OUTPUT_SIZE);

        for (int i = 0; i < GRID_SIZE; i++) {
            inv.setItem(i, crafting.getStackInSlot(i));
        }

        inv.setItem(GRID_SIZE, deconstructInput.getStackInSlot(0));

        for (int i = 0; i < DECONSTRUCT_OUTPUT_SIZE; i++) {
            inv.setItem(GRID_SIZE + 1 + i, deconstructOutputs.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("crafting", crafting.serializeNBT(registries));
        tag.put("deconstructInput", deconstructInput.serializeNBT(registries));
        tag.put("deconstructOutputs", deconstructOutputs.serializeNBT(registries));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        crafting.deserializeNBT(registries, tag.getCompound("crafting"));
        deconstructInput.deserializeNBT(registries, tag.getCompound("deconstructInput"));
        deconstructOutputs.deserializeNBT(registries, tag.getCompound("deconstructOutputs"));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.createcybernetics.engineering_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new EngineeringTableMenu(id, inv, this.getBlockPos());
    }
}
