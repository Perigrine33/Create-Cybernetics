package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.block.SurgeryTableBlock;
import com.perigrine3.createcybernetics.block.entity.SurgeryTableBlockEntity;
import com.perigrine3.createcybernetics.entity.ModEntities;
import com.perigrine3.createcybernetics.entity.custom.RipperEntity;
import com.perigrine3.createcybernetics.world.RipperClinicSpawnData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = CreateCybernetics.MODID)
public final class RipperClinicChunkLoadHandler {

    private static final double EXISTING_RIPPER_CHECK_RADIUS = 8.0D;
    private static final double SPAWN_Y_OFFSET = 0.1D;

    private RipperClinicChunkLoadHandler() {
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!event.isNewChunk()) {
            return;
        }

        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ChunkAccess access = event.getChunk();
        ChunkPos chunkPos = access.getPos();

        level.getServer().execute(() -> processNewChunk(level, chunkPos));
    }

    private static void processNewChunk(ServerLevel level, ChunkPos chunkPos) {
        LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);

        BlockPos tableHeadPos = findClinicTableHead(chunk);
        if (tableHeadPos == null) {
            return;
        }

        RipperClinicSpawnData data = RipperClinicSpawnData.get(level);
        long anchor = tableHeadPos.asLong();

        if (data.hasSpawnedAt(anchor)) {
            return;
        }

        List<RipperEntity> existing = level.getEntitiesOfClass(
                RipperEntity.class,
                new AABB(tableHeadPos).inflate(EXISTING_RIPPER_CHECK_RADIUS),
                ripper -> {
                    BlockPos home = ripper.getHomeTablePos();
                    return home != null && home.equals(tableHeadPos);
                }
        );

        if (!existing.isEmpty()) {
            data.markSpawned(anchor);
            return;
        }

        BlockState tableState = level.getBlockState(tableHeadPos);
        if (!tableState.is(ModBlocks.SURGERY_TABLE.get())
                || !tableState.hasProperty(SurgeryTableBlock.PART)
                || tableState.getValue(SurgeryTableBlock.PART) != BedPart.HEAD) {
            return;
        }

        Direction facing = tableState.getValue(SurgeryTableBlock.FACING);
        Direction spawnSide = chooseSpawnSide(level, tableHeadPos, facing);
        Vec3 spawnPos = Vec3.atBottomCenterOf(tableHeadPos.relative(spawnSide)).add(0.0D, SPAWN_Y_OFFSET, 0.0D);

        RipperEntity ripper = ModEntities.RIPPER.get().create(level);
        if (ripper == null) {
            return;
        }

        ripper.setHomeTablePos(tableHeadPos.immutable());
        ripper.setPersistenceRequired();
        ripper.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, facing.toYRot(), 0.0F);

        DifficultyInstance difficulty = level.getCurrentDifficultyAt(BlockPos.containing(spawnPos));
        ripper.finalizeSpawn(level, difficulty, MobSpawnType.STRUCTURE, null);

        if (level.addFreshEntity(ripper)) {
            data.markSpawned(anchor);
        }
    }

    @Nullable
    private static BlockPos findClinicTableHead(LevelChunk chunk) {
        for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
            BlockEntity blockEntity = entry.getValue();

            if (!(blockEntity instanceof SurgeryTableBlockEntity)) {
                continue;
            }

            BlockPos pos = entry.getKey();
            BlockState state = blockEntity.getBlockState();

            if (!state.is(ModBlocks.SURGERY_TABLE.get())) {
                continue;
            }

            if (!state.hasProperty(SurgeryTableBlock.PART) || !state.hasProperty(SurgeryTableBlock.FACING)) {
                continue;
            }

            if (state.getValue(SurgeryTableBlock.PART) == BedPart.HEAD) {
                return pos.immutable();
            }
        }

        return null;
    }

    private static Direction chooseSpawnSide(ServerLevel level, BlockPos headPos, Direction facing) {
        Direction right = facing.getClockWise();
        Direction left = facing.getCounterClockWise();
        Direction back = facing.getOpposite();

        if (isSpawnSpotUsable(level, headPos.relative(right))) {
            return right;
        }

        if (isSpawnSpotUsable(level, headPos.relative(left))) {
            return left;
        }

        if (isSpawnSpotUsable(level, headPos.relative(back))) {
            return back;
        }

        return right;
    }

    private static boolean isSpawnSpotUsable(ServerLevel level, BlockPos pos) {
        BlockState feet = level.getBlockState(pos);
        BlockState head = level.getBlockState(pos.above());

        return (feet.isAir() || feet.canBeReplaced())
                && (head.isAir() || head.canBeReplaced());
    }
}