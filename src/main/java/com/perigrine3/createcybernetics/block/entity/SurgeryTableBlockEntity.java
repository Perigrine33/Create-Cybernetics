package com.perigrine3.createcybernetics.block.entity;

import com.perigrine3.createcybernetics.screen.custom.SurgeryTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SurgeryTableBlockEntity extends BlockEntity implements MenuProvider {

    public static final int SLOT_COUNT = 65;

    public final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final boolean[] installed = new boolean[SLOT_COUNT];
    private final boolean[] staged = new boolean[SLOT_COUNT];
    private final boolean[] markedForRemoval = new boolean[SLOT_COUNT];

    @Nullable
    private UUID patientUuid;

    public SurgeryTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SURGERY_TABLE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.createcybernetics.surgery_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SurgeryTableMenu(containerId, playerInventory, this);
    }

    public boolean isInstalled(int index) {
        return index >= 0 && index < installed.length && installed[index];
    }

    public void setInstalled(int index, boolean value) {
        if (index < 0 || index >= installed.length) {
            return;
        }

        installed[index] = value;
        setChanged();
    }

    public boolean isStaged(int index) {
        return index >= 0 && index < staged.length && staged[index];
    }

    public void setStaged(int index, boolean value) {
        if (index < 0 || index >= staged.length) {
            return;
        }

        staged[index] = value;
        setChanged();
    }

    public boolean isMarkedForRemoval(int index) {
        return index >= 0 && index < markedForRemoval.length && markedForRemoval[index];
    }

    public void setMarkedForRemoval(int index, boolean value) {
        if (index < 0 || index >= markedForRemoval.length) {
            return;
        }

        markedForRemoval[index] = value;
        setChanged();
    }

    public void toggleMarkedForRemoval(int index) {
        if (index < 0 || index >= markedForRemoval.length) {
            return;
        }

        markedForRemoval[index] = !markedForRemoval[index];
        setChanged();
    }

    public void setPatient(Player player) {
        patientUuid = player.getUUID();
        setChanged();
    }

    public void clearPatient() {
        patientUuid = null;
        setChanged();
    }

    public boolean hasPatient() {
        return patientUuid != null;
    }

    @Nullable
    public Player getPatient() {
        if (patientUuid == null || !(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        return serverLevel.getPlayerByUUID(patientUuid);
    }

    public Player getPatientOr(Player fallback) {
        Player patient = getPatient();
        return patient != null ? patient : fallback;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SurgeryTableBlockEntity table) {
        Player patient = table.getPatient();
        if (patient == null) {
            return;
        }

        if (!patient.isAlive() || !patient.isSleeping()) {
            table.clearPatient();
            return;
        }

        if (patient.getSleepingPos().isEmpty() || !patient.getSleepingPos().get().equals(pos)) {
            table.clearPatient();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.put("Inventory", inventory.serializeNBT(provider));
        tag.put("InstalledFlags", writeFlags(installed));
        tag.put("StagedFlags", writeFlags(staged));
        tag.put("RemovalFlags", writeFlags(markedForRemoval));

        if (patientUuid != null) {
            tag.putUUID("PatientUuid", patientUuid);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        if (tag.contains("Inventory", Tag.TAG_COMPOUND)) {
            inventory.deserializeNBT(provider, tag.getCompound("Inventory"));
        }

        readFlags(tag.getList("InstalledFlags", Tag.TAG_BYTE), installed);
        readFlags(tag.getList("StagedFlags", Tag.TAG_BYTE), staged);
        readFlags(tag.getList("RemovalFlags", Tag.TAG_BYTE), markedForRemoval);

        if (tag.hasUUID("PatientUuid")) {
            patientUuid = tag.getUUID("PatientUuid");
        } else {
            patientUuid = null;
        }
    }

    private static ListTag writeFlags(boolean[] values) {
        ListTag list = new ListTag();

        for (boolean value : values) {
            list.add(ByteTag.valueOf(value));
        }

        return list;
    }

    private static void readFlags(ListTag list, boolean[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] = i < list.size() && ((ByteTag) list.get(i)).getAsByte() != 0;
        }
    }
}