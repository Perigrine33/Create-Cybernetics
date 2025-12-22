package com.perigrine3.createcybernetics.block.entity;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.surgery.DefaultOrgans;
import com.perigrine3.createcybernetics.screen.custom.RobosurgeonMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class RobosurgeonBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler inventory = new ItemStackHandler(65) {

        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            ItemStack stack = getStackInSlot(slot);

            // ----------------------------------------
            // SLOT BECAME EMPTY → CLEAR STATES
            // ----------------------------------------
            if (stack.isEmpty()) {
                staged[slot] = false;
                markedForRemoval[slot] = false;
                setChanged();
                return;
            }

            if (surgeryInProgress) {
                setChanged();
                return;
            }

            if (!installed[slot]) {
                staged[slot] = true;
                markedForRemoval[slot] = false;
            }


            setChanged();

            if (!level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private boolean surgeryInProgress = false;
    public final boolean[] installed = new boolean[65];
    public final boolean[] staged = new boolean[65];
    public final boolean[] markedForRemoval = new boolean[65];

    public RobosurgeonBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ROBOSURGEON_BLOCKENTITY.get(), pos, blockState);
    }

    public boolean isInstalled(int i) {
        return i >= 0 && i < installed.length && installed[i];
    }

    public boolean isStaged(int i) {
        return i >= 0 && i < staged.length && staged[i];
    }

    public boolean isMarkedForRemoval(int i) {
        return i >= 0 && i < markedForRemoval.length && markedForRemoval[i];
    }

    public void setInstalled(int i, boolean value) {
        if (i < 0 || i >= installed.length) return;
        installed[i] = value;
        if (!value) markedForRemoval[i] = false;
        setChanged();
    }

    public void setStaged(int i, boolean value) {
        if (i < 0 || i >= staged.length) return;
        staged[i] = value;
        if (!value) markedForRemoval[i] = false;
        setChanged();
    }

    public void toggleMarkedForRemoval(int i) {
        if (i < 0 || i >= markedForRemoval.length) return;
        if (!installed[i]) return;
        markedForRemoval[i] = !markedForRemoval[i];
        if (markedForRemoval[i]) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
            }
        }
        setChanged();
    }


    public void clearSlotStates() {
        for (int i = 0; i < 65; i++) {
            staged[i] = false;
            markedForRemoval[i] = false;
        }
        setChanged();
    }

    public void beginSurgery() {
        surgeryInProgress = true;
    }

    public void endSurgery() {
        surgeryInProgress = false;
    }

    public void clearContents() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public void drops() {
        if (level == null || level.isClientSide) return;

        for (int i = 0; i < inventory.getSlots(); i++) {

            if (!staged[i]) continue;

            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);

            inventory.setStackInSlot(i, ItemStack.EMPTY);
            staged[i] = false;
        }

        setChanged();
    }


    private boolean isDefaultOrgan(ItemStack stack) {
        if (stack.isEmpty()) return false;

        for (CyberwareSlot slot : CyberwareSlot.values()) {
            for (int i = 0; i < slot.size; i++) {
                ItemStack defaultStack = DefaultOrgans.get(slot, i);
                if (!defaultStack.isEmpty() && stack.is(defaultStack.getItem())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static byte[] encode(boolean[] data) {
        byte[] out = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = (byte) (data[i] ? 1 : 0);
        }
        return out;
    }

    private static void decode(byte[] src, boolean[] target) {
        int len = Math.min(src.length, target.length);
        for (int i = 0; i < len; i++) {
            target[i] = src[i] != 0;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("inventory", inventory.serializeNBT(registries));

        tag.putByteArray("Installed", encode(installed));
        tag.putByteArray("Staged", encode(staged));
        tag.putByteArray("Marked", encode(markedForRemoval));
    }



    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        }

        if (tag.contains("Installed")) {
            decode(tag.getByteArray("Installed"), installed);
        }

        if (tag.contains("Staged")) {
            decode(tag.getByteArray("Staged"), staged);
        }

        if (tag.contains("Marked")) {
            decode(tag.getByteArray("Marked"), markedForRemoval);
        }
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Robosurgeon Interface");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new RobosurgeonMenu(i, inventory, this);
    }

    public AbstractContainerMenu getMenu(int containerId, Inventory inventory, Player player) {
        return new RobosurgeonMenu(containerId, inventory, this);
    }
}