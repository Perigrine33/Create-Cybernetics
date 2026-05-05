package com.perigrine3.createcybernetics.block.entity;

import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.block.SurgeryTableBlock;
import com.perigrine3.createcybernetics.common.surgery.SurgeryController;
import com.perigrine3.createcybernetics.screen.custom.surgery.surgery_table.SurgeryTableMenu;
import com.perigrine3.createcybernetics.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SurgeryTableBlockEntity extends BlockEntity implements MenuProvider {

    public static final int SLOT_COUNT = 65;
    private static final int TIMED_SURGERY_DURATION_TICKS = 10 * 20;
    private static final int EFFECT_INTERVAL_TICKS = 20;

    private static final String TAG_DISPLAYED_TARGET_UUID = "DisplayedTargetUuid";
    private static final String TAG_REMOVAL_STATE_BY_TARGET = "RemovalStateByTarget";
    private static final String TAG_TARGET_UUID = "TargetUuid";
    private static final String TAG_REMOVAL_FLAGS = "RemovalFlags";

    private static final DustParticleOptions BLOOD =
            new DustParticleOptions(new Vector3f(0.75f, 0.05f, 0.05f), 1.25f);

    public final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            ItemStack stack = getStackInSlot(slot);

            if (stack.isEmpty()) {
                staged[slot] = false;
                markedForRemoval[slot] = false;
                sync();
                return;
            }

            if (surgeryApplying) {
                sync();
                return;
            }

            if (!installed[slot]) {
                staged[slot] = true;
                markedForRemoval[slot] = false;
            }

            sync();
        }
    };

    private final boolean[] installed = new boolean[SLOT_COUNT];
    private final boolean[] staged = new boolean[SLOT_COUNT];
    private final boolean[] markedForRemoval = new boolean[SLOT_COUNT];

    private final Map<UUID, boolean[]> removalFlagsByTarget = new HashMap<>();

    @Nullable
    private UUID patientUuid;

    @Nullable
    private UUID displayedTargetUuid;

    private boolean timedSurgeryInProgress = false;
    private boolean surgeryApplying = false;
    private int timedSurgeryTicksRemaining = 0;
    private int timedSurgeryEffectTicks = 0;

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
        SurgeryTableBlockEntity controller = getController();
        return new SurgeryTableMenu(containerId, playerInventory, controller != null ? controller : this);
    }

    public boolean isInstalled(int index) {
        return index >= 0 && index < installed.length && installed[index];
    }

    public void setInstalled(int index, boolean value) {
        if (index < 0 || index >= installed.length) {
            return;
        }

        installed[index] = value;
        if (!value) {
            markedForRemoval[index] = false;
        }
        sync();
    }

    public boolean isStaged(int index) {
        return index >= 0 && index < staged.length && staged[index];
    }

    public void setStaged(int index, boolean value) {
        if (index < 0 || index >= staged.length) {
            return;
        }

        staged[index] = value;
        if (!value) {
            markedForRemoval[index] = false;
        }
        sync();
    }

    public boolean isMarkedForRemoval(int index) {
        return index >= 0 && index < markedForRemoval.length && markedForRemoval[index];
    }

    public void setMarkedForRemoval(int index, boolean value) {
        if (index < 0 || index >= markedForRemoval.length) {
            return;
        }

        markedForRemoval[index] = value;
        sync();
    }

    public void toggleMarkedForRemoval(int index) {
        if (index < 0 || index >= markedForRemoval.length) {
            return;
        }

        if (!installed[index]) {
            return;
        }

        markedForRemoval[index] = !markedForRemoval[index];
        sync();
    }

    public boolean[] getInstalledFlags() {
        return installed;
    }

    public boolean[] getStagedFlags() {
        return staged;
    }

    public boolean[] getMarkedForRemovalFlags() {
        return markedForRemoval;
    }

    public void clearSlotStates() {
        for (int i = 0; i < SLOT_COUNT; i++) {
            staged[i] = false;
            markedForRemoval[i] = false;
        }
        sync();
    }

    public void beginSurgery() {
        surgeryApplying = true;
    }

    public void endSurgery() {
        surgeryApplying = false;
    }

    @Nullable
    public UUID getDisplayedTargetUuid() {
        SurgeryTableBlockEntity controller = getController();
        if (controller != null && controller != this) {
            return controller.getDisplayedTargetUuid();
        }

        return displayedTargetUuid;
    }

    public void switchDisplayedTarget(UUID targetUuid) {
        SurgeryTableBlockEntity controller = getController();
        if (controller != null && controller != this) {
            controller.switchDisplayedTarget(targetUuid);
            return;
        }

        if (displayedTargetUuid != null && displayedTargetUuid.equals(targetUuid)) {
            return;
        }

        if (displayedTargetUuid != null) {
            removalFlagsByTarget.put(displayedTargetUuid, markedForRemoval.clone());
        }

        displayedTargetUuid = targetUuid;

        boolean[] savedRemovalFlags = removalFlagsByTarget.get(targetUuid);
        for (int i = 0; i < SLOT_COUNT; i++) {
            markedForRemoval[i] = savedRemovalFlags != null && i < savedRemovalFlags.length && savedRemovalFlags[i];
        }

        sync();
    }

    public void forgetDisplayedTarget() {
        SurgeryTableBlockEntity controller = getController();
        if (controller != null && controller != this) {
            controller.forgetDisplayedTarget();
            return;
        }

        if (displayedTargetUuid != null) {
            removalFlagsByTarget.put(displayedTargetUuid, markedForRemoval.clone());
        }

        displayedTargetUuid = null;
        sync();
    }

    public boolean isTimedSurgeryInProgress() {
        SurgeryTableBlockEntity controller = getController();
        if (controller != null && controller != this) {
            return controller.isTimedSurgeryInProgress();
        }
        return timedSurgeryInProgress;
    }

    public void startTimedSurgery(Player player) {
        SurgeryTableBlockEntity controller = getController();
        if (controller != null && controller != this) {
            controller.startTimedSurgery(player);
            return;
        }

        patientUuid = player.getUUID();
        timedSurgeryInProgress = true;
        timedSurgeryTicksRemaining = TIMED_SURGERY_DURATION_TICKS;
        timedSurgeryEffectTicks = EFFECT_INTERVAL_TICKS;

        level.playSound(null, worldPosition, ModSounds.SURGERY_TABLE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        sync();
    }

    public void cancelTimedSurgery() {
        SurgeryTableBlockEntity controller = getController();
        if (controller != null && controller != this) {
            controller.cancelTimedSurgery();
            return;
        }

        timedSurgeryInProgress = false;
        timedSurgeryTicksRemaining = 0;
        timedSurgeryEffectTicks = 0;
        sync();
    }

    public void setPatient(Player player) {
        SurgeryTableBlockEntity controller = getController();
        if (controller != null && controller != this) {
            controller.setPatient(player);
            return;
        }

        patientUuid = player.getUUID();
        sync();
    }

    public void clearPatient() {
        SurgeryTableBlockEntity controller = getController();
        if (controller != null && controller != this) {
            controller.clearPatient();
            return;
        }

        patientUuid = null;
        sync();
    }

    public boolean hasPatient() {
        SurgeryTableBlockEntity controller = getController();
        if (controller != null && controller != this) {
            return controller.hasPatient();
        }

        return patientUuid != null;
    }

    @Nullable
    public Player getPatient() {
        SurgeryTableBlockEntity controller = getController();
        if (controller != null && controller != this) {
            return controller.getPatient();
        }

        if (patientUuid == null || level == null) {
            return null;
        }

        return level.getPlayerByUUID(patientUuid);
    }

    public Player getPatientOr(Player fallback) {
        Player patient = getPatient();
        return patient != null ? patient : fallback;
    }

    public boolean isController() {
        BlockState state = getBlockState();
        return state.is(ModBlocks.SURGERY_TABLE.get())
                && state.hasProperty(SurgeryTableBlock.PART)
                && state.getValue(SurgeryTableBlock.PART) == BedPart.HEAD;
    }

    public BlockPos getControllerPos() {
        BlockState state = getBlockState();

        if (!state.is(ModBlocks.SURGERY_TABLE.get()) || !state.hasProperty(SurgeryTableBlock.PART)) {
            return worldPosition;
        }

        if (state.getValue(SurgeryTableBlock.PART) == BedPart.HEAD) {
            return worldPosition;
        }

        Direction facing = state.getValue(SurgeryTableBlock.FACING);
        return worldPosition.relative(facing);
    }

    @Nullable
    public SurgeryTableBlockEntity getController() {
        if (level == null) {
            return this;
        }

        BlockPos controllerPos = getControllerPos();
        BlockEntity be = level.getBlockEntity(controllerPos);
        if (be instanceof SurgeryTableBlockEntity table) {
            return table;
        }

        return this;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SurgeryTableBlockEntity table) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (state.hasProperty(SurgeryTableBlock.PART) && state.getValue(SurgeryTableBlock.PART) != BedPart.HEAD) {
            return;
        }

        Player patient = table.getPatient();

        if (table.timedSurgeryInProgress) {
            if (patient == null || !patient.isAlive() || !patient.isSleeping()) {
                table.cancelTimedSurgery();
                table.clearPatient();
                return;
            }

            table.timedSurgeryTicksRemaining--;
            table.timedSurgeryEffectTicks--;

            if (table.timedSurgeryEffectTicks <= 0) {
                table.timedSurgeryEffectTicks = EFFECT_INTERVAL_TICKS;
                table.applyTimedSurgeryEffects(serverLevel, patient);
            }

            if (table.timedSurgeryTicksRemaining <= 0) {
                table.timedSurgeryInProgress = false;
                table.timedSurgeryTicksRemaining = 0;
                table.timedSurgeryEffectTicks = 0;

                SurgeryController.performSurgery(patient, table);

                if (patient.isSleeping()) {
                    patient.stopSleeping();
                }

                table.clearPatient();
                table.sync();
            }

            return;
        }

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

    private void applyTimedSurgeryEffects(ServerLevel level, Player patient) {
        level.sendParticles(BLOOD, patient.getX(), patient.getY() + 1.0, patient.getZ(),
                10, 0.2, 0.35, 0.2, 1);

        RandomSource random = level.random;
        float damage = random.nextBoolean() ? 1.0F : 1.5F;
        DamageSource source = patient.damageSources().generic();

        if (patient instanceof ServerPlayer serverPlayer) {
            serverPlayer.hurt(source, damage);
        } else {
            patient.hurt(source, damage);
        }
    }

    private void sync() {
        setChanged();

        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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

        if (displayedTargetUuid != null) {
            tag.putUUID(TAG_DISPLAYED_TARGET_UUID, displayedTargetUuid);
            removalFlagsByTarget.put(displayedTargetUuid, markedForRemoval.clone());
        }

        ListTag savedRemovalStates = new ListTag();
        for (Map.Entry<UUID, boolean[]> entry : removalFlagsByTarget.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID(TAG_TARGET_UUID, entry.getKey());
            entryTag.put(TAG_REMOVAL_FLAGS, writeFlags(entry.getValue()));
            savedRemovalStates.add(entryTag);
        }
        tag.put(TAG_REMOVAL_STATE_BY_TARGET, savedRemovalStates);

        tag.putBoolean("TimedSurgeryInProgress", timedSurgeryInProgress);
        tag.putInt("TimedSurgeryTicksRemaining", timedSurgeryTicksRemaining);
        tag.putInt("TimedSurgeryEffectTicks", timedSurgeryEffectTicks);
        tag.putBoolean("SurgeryApplying", surgeryApplying);
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

        if (tag.hasUUID(TAG_DISPLAYED_TARGET_UUID)) {
            displayedTargetUuid = tag.getUUID(TAG_DISPLAYED_TARGET_UUID);
        } else {
            displayedTargetUuid = null;
        }

        removalFlagsByTarget.clear();
        if (tag.contains(TAG_REMOVAL_STATE_BY_TARGET, Tag.TAG_LIST)) {
            ListTag savedRemovalStates = tag.getList(TAG_REMOVAL_STATE_BY_TARGET, Tag.TAG_COMPOUND);
            for (int i = 0; i < savedRemovalStates.size(); i++) {
                CompoundTag entryTag = savedRemovalStates.getCompound(i);
                if (!entryTag.hasUUID(TAG_TARGET_UUID)) {
                    continue;
                }

                boolean[] flags = new boolean[SLOT_COUNT];
                readFlags(entryTag.getList(TAG_REMOVAL_FLAGS, Tag.TAG_BYTE), flags);
                removalFlagsByTarget.put(entryTag.getUUID(TAG_TARGET_UUID), flags);
            }
        }

        timedSurgeryInProgress = tag.getBoolean("TimedSurgeryInProgress");
        timedSurgeryTicksRemaining = tag.getInt("TimedSurgeryTicksRemaining");
        timedSurgeryEffectTicks = tag.getInt("TimedSurgeryEffectTicks");
        surgeryApplying = tag.getBoolean("SurgeryApplying");
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
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