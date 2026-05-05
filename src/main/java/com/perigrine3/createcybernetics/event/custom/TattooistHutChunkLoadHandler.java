package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.ModEntities;
import com.perigrine3.createcybernetics.entity.custom.TatHogEntity;
import com.perigrine3.createcybernetics.world.TattooistHutSpawnData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@EventBusSubscriber(modid = CreateCybernetics.MODID)
public final class TattooistHutChunkLoadHandler {

    private static final double EXISTING_TATHOG_CHECK_RADIUS = 8.0D;
    private static final double SPAWN_Y_OFFSET = 0.1D;

    private TattooistHutChunkLoadHandler() {
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

        BlockPos anchor = findTattooistHutAnchor(level, chunk);
        if (anchor == null) {
            return;
        }

        TattooistHutSpawnData data = TattooistHutSpawnData.get(level);
        long anchorKey = anchor.asLong();

        if (data.hasSpawnedAt(anchorKey)) {
            return;
        }

        List<TatHogEntity> existing = level.getEntitiesOfClass(
                TatHogEntity.class,
                new AABB(anchor).inflate(EXISTING_TATHOG_CHECK_RADIUS)
        );

        if (!existing.isEmpty()) {
            data.markSpawned(anchorKey);
            return;
        }

        BlockPos spawnPosBlock = findSpawnSpot(level, anchor);
        if (spawnPosBlock == null) {
            return;
        }

        TatHogEntity tatHog = ModEntities.TATHOG.get().create(level);
        if (tatHog == null) {
            return;
        }

        Vec3 spawnPos = Vec3.atBottomCenterOf(spawnPosBlock).add(0.0D, SPAWN_Y_OFFSET, 0.0D);

        tatHog.setPersistenceRequired();
        tatHog.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, 180.0F, 0.0F);

        DifficultyInstance difficulty = level.getCurrentDifficultyAt(spawnPosBlock);
        tatHog.finalizeSpawn(level, difficulty, MobSpawnType.STRUCTURE, null);

        if (level.addFreshEntity(tatHog)) {
            data.markSpawned(anchorKey);
        }
    }

    @Nullable
    private static BlockPos findTattooistHutAnchor(ServerLevel level, LevelChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    mutable.set(minX + localX, y, minZ + localZ);

                    BlockState state = level.getBlockState(mutable);
                    if (!state.is(Blocks.WARPED_DOOR)) {
                        continue;
                    }

                    if (!state.hasProperty(DoorBlock.HALF)) {
                        continue;
                    }

                    if (state.getValue(DoorBlock.HALF) != DoubleBlockHalf.LOWER) {
                        continue;
                    }

                    if (looksLikeTattooistHut(level, mutable)) {
                        return mutable.immutable();
                    }
                }
            }
        }

        return null;
    }

    private static boolean looksLikeTattooistHut(ServerLevel level, BlockPos doorPos) {
        BlockState upperDoor = level.getBlockState(doorPos.above());
        if (!upperDoor.is(Blocks.WARPED_DOOR)) {
            return false;
        }

        if (!upperDoor.hasProperty(DoorBlock.HALF)) {
            return false;
        }

        if (upperDoor.getValue(DoorBlock.HALF) != DoubleBlockHalf.UPPER) {
            return false;
        }

        return hasNearby(level, doorPos, Blocks.WARPED_STEM, 4)
                && hasNearby(level, doorPos, Blocks.STRIPPED_WARPED_STEM, 4)
                && hasNearby(level, doorPos, Blocks.NETHERRACK, 5)
                && hasNearby(level, doorPos, Blocks.SHROOMLIGHT, 5);
    }

    private static boolean hasNearby(ServerLevel level, BlockPos center, net.minecraft.world.level.block.Block block, int radius) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -3; dy <= 5; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    mutable.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);

                    if (level.getBlockState(mutable).is(block)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Nullable
    private static BlockPos findSpawnSpot(ServerLevel level, BlockPos anchor) {
        Direction[] directions = new Direction[] {
                Direction.WEST,
                Direction.EAST,
                Direction.NORTH,
                Direction.SOUTH
        };

        for (Direction direction : directions) {
            BlockPos candidate = anchor.relative(direction);
            if (isSpawnSpotUsable(level, candidate)) {
                return candidate.immutable();
            }
        }

        for (Direction direction : directions) {
            BlockPos candidate = anchor.relative(direction, 2);
            if (isSpawnSpotUsable(level, candidate)) {
                return candidate.immutable();
            }
        }

        return null;
    }

    private static boolean isSpawnSpotUsable(ServerLevel level, BlockPos pos) {
        BlockState floor = level.getBlockState(pos.below());
        BlockState feet = level.getBlockState(pos);
        BlockState head = level.getBlockState(pos.above());

        return !floor.isAir()
                && (feet.isAir() || feet.canBeReplaced())
                && (head.isAir() || head.canBeReplaced());
    }
}