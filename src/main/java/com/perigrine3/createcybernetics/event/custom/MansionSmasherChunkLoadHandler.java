package com.perigrine3.createcybernetics.event;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.ModEntities;
import com.perigrine3.createcybernetics.entity.custom.SmasherEntity;
import com.perigrine3.createcybernetics.world.MansionBossSpawnData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@EventBusSubscriber(modid = CreateCybernetics.MODID)
public final class MansionSmasherChunkLoadHandler {

    private static final double EXISTING_SMASHER_CHECK_RADIUS = 96.0D;
    private static final int SPAWN_ATTEMPTS = 160;
    private static final int MAX_DOWN_SCAN = 16;
    private static final int CHUNKS_PROCESSED_PER_TICK = 2;

    private static final Queue<PendingChunk> PENDING_CHUNKS = new ConcurrentLinkedQueue<>();

    private MansionSmasherChunkLoadHandler() {
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
        PENDING_CHUNKS.add(new PendingChunk(level.dimension(), access.getPos()));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();

        for (int i = 0; i < CHUNKS_PROCESSED_PER_TICK; i++) {
            PendingChunk pending = PENDING_CHUNKS.poll();
            if (pending == null) {
                return;
            }

            ServerLevel level = server.getLevel(pending.dimension());
            if (level == null) {
                continue;
            }

            processNewChunk(level, pending.chunkPos());
        }
    }

    private static void processNewChunk(ServerLevel level, ChunkPos chunkPos) {
        StructureStart start = findMansionStartInChunk(level, chunkPos);
        if (start == null || !start.isValid()) {
            return;
        }

        long anchor = start.getChunkPos().toLong();

        MansionBossSpawnData data = MansionBossSpawnData.get(level);
        if (data.hasSpawnedAt(anchor)) {
            return;
        }

        BoundingBox box = start.getBoundingBox();

        BlockPos boxCenter = new BlockPos(
                (box.minX() + box.maxX()) / 2,
                (box.minY() + box.maxY()) / 2,
                (box.minZ() + box.maxZ()) / 2
        );

        List<SmasherEntity> existing = level.getEntitiesOfClass(
                SmasherEntity.class,
                new AABB(boxCenter).inflate(EXISTING_SMASHER_CHECK_RADIUS)
        );

        if (!existing.isEmpty()) {
            data.markSpawned(anchor);
            return;
        }

        BlockPos spawnPosBlock = findInteriorLikeSpot(level, box, chunkPos, level.getRandom());
        if (spawnPosBlock == null) {
            return;
        }

        if (!level.isPositionEntityTicking(spawnPosBlock)) {
            return;
        }

        SmasherEntity smasher = ModEntities.SMASHER.get().create(level);
        if (smasher == null) {
            return;
        }

        RandomSource random = level.getRandom();
        Vec3 spawnPos = Vec3.atBottomCenterOf(spawnPosBlock);

        smasher.setPersistenceRequired();
        smasher.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, random.nextFloat() * 360.0F, 0.0F);

        DifficultyInstance difficulty = level.getCurrentDifficultyAt(spawnPosBlock);
        smasher.finalizeSpawn(level, difficulty, MobSpawnType.STRUCTURE, null);

        if (level.addFreshEntity(smasher)) {
            data.markSpawned(anchor);
        }
    }

    @Nullable
    private static StructureStart findMansionStartInChunk(ServerLevel level, ChunkPos chunkPos) {
        Structure mansion = level.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getHolderOrThrow(BuiltinStructures.WOODLAND_MANSION)
                .value();

        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        int maxX = chunkPos.getMaxBlockX();
        int maxZ = chunkPos.getMaxBlockZ();
        int midX = chunkPos.getMiddleBlockX();
        int midZ = chunkPos.getMiddleBlockZ();

        BlockPos[] samples = new BlockPos[] {
                surfacePosIfLoaded(level, midX, midZ),
                surfacePosIfLoaded(level, minX + 2, minZ + 2),
                surfacePosIfLoaded(level, maxX - 2, minZ + 2),
                surfacePosIfLoaded(level, minX + 2, maxZ - 2),
                surfacePosIfLoaded(level, maxX - 2, maxZ - 2),
                surfacePosIfLoaded(level, midX, minZ + 2),
                surfacePosIfLoaded(level, midX, maxZ - 2),
                surfacePosIfLoaded(level, minX + 2, midZ),
                surfacePosIfLoaded(level, maxX - 2, midZ)
        };

        for (BlockPos sample : samples) {
            if (sample == null) {
                continue;
            }

            StructureStart start = level.structureManager().getStructureAt(sample, mansion);
            if (start != null && start.isValid()) {
                return start;
            }
        }

        return null;
    }

    @Nullable
    private static BlockPos surfacePosIfLoaded(ServerLevel level, int x, int z) {
        if (!isBlockColumnLoaded(level, x, z)) {
            return null;
        }

        return level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, new BlockPos(x, 0, z));
    }

    @Nullable
    private static BlockPos findInteriorLikeSpot(ServerLevel level, BoundingBox box, ChunkPos chunkPos, RandomSource random) {
        int minX = Math.max(box.minX(), chunkPos.getMinBlockX());
        int minZ = Math.max(box.minZ(), chunkPos.getMinBlockZ());
        int maxX = Math.min(box.maxX(), chunkPos.getMaxBlockX());
        int maxZ = Math.min(box.maxZ(), chunkPos.getMaxBlockZ());

        if (minX > maxX || minZ > maxZ) {
            return null;
        }

        int yRange = Math.max(1, box.maxY() - box.minY() + 1);

        for (int i = 0; i < SPAWN_ATTEMPTS; i++) {
            int x = random.nextInt(maxX - minX + 1) + minX;
            int z = random.nextInt(maxZ - minZ + 1) + minZ;
            int y = random.nextInt(yRange) + box.minY();

            BlockPos start = new BlockPos(x, y, z);

            for (int down = 0; down <= MAX_DOWN_SCAN; down++) {
                BlockPos candidate = start.below(down);

                if (!box.isInside(candidate)) {
                    break;
                }

                if (isInteriorSpawnSpot(level, candidate)) {
                    return candidate.immutable();
                }
            }
        }

        return null;
    }

    private static boolean isInteriorSpawnSpot(ServerLevel level, BlockPos pos) {
        return isTwoTallAir(level, pos)
                && hasSolidFloor(level, pos)
                && hasCeilingSoon(level, pos)
                && isSomewhatEnclosed(level, pos);
    }

    private static boolean isTwoTallAir(ServerLevel level, BlockPos pos) {
        return isNonCollidingIfLoaded(level, pos)
                && isNonCollidingIfLoaded(level, pos.above());
    }

    private static boolean hasSolidFloor(ServerLevel level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState state = getBlockStateIfLoaded(level, below);
        return state != null && !isNonColliding(state, level, below);
    }

    private static boolean hasCeilingSoon(ServerLevel level, BlockPos pos) {
        BlockPos head = pos.above();

        for (int i = 1; i <= 4; i++) {
            BlockPos check = head.above(i);
            BlockState state = getBlockStateIfLoaded(level, check);

            if (state == null) {
                return false;
            }

            if (!isNonColliding(state, level, check)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isSomewhatEnclosed(ServerLevel level, BlockPos pos) {
        int solid = 0;

        if (isSolidIfLoaded(level, pos.north())) solid++;
        if (isSolidIfLoaded(level, pos.south())) solid++;
        if (isSolidIfLoaded(level, pos.west())) solid++;
        if (isSolidIfLoaded(level, pos.east())) solid++;

        return solid >= 2;
    }

    private static boolean isSolidIfLoaded(ServerLevel level, BlockPos pos) {
        BlockState state = getBlockStateIfLoaded(level, pos);
        return state != null && !isNonColliding(state, level, pos);
    }

    private static boolean isNonCollidingIfLoaded(ServerLevel level, BlockPos pos) {
        BlockState state = getBlockStateIfLoaded(level, pos);
        return state != null && isNonColliding(state, level, pos);
    }

    @Nullable
    private static BlockState getBlockStateIfLoaded(ServerLevel level, BlockPos pos) {
        if (level.isOutsideBuildHeight(pos)) {
            return null;
        }

        LevelChunk chunk = getLoadedChunk(level, pos);
        if (chunk == null) {
            return null;
        }

        return chunk.getBlockState(pos);
    }

    @Nullable
    private static LevelChunk getLoadedChunk(ServerLevel level, BlockPos pos) {
        int chunkX = SectionPos.blockToSectionCoord(pos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
        return level.getChunkSource().getChunkNow(chunkX, chunkZ);
    }

    private static boolean isBlockColumnLoaded(ServerLevel level, int x, int z) {
        int chunkX = SectionPos.blockToSectionCoord(x);
        int chunkZ = SectionPos.blockToSectionCoord(z);
        return level.getChunkSource().getChunkNow(chunkX, chunkZ) != null;
    }

    private static boolean isNonColliding(BlockState state, ServerLevel level, BlockPos pos) {
        return state.getCollisionShape(level, pos).isEmpty();
    }

    private record PendingChunk(ResourceKey<Level> dimension, ChunkPos chunkPos) {
    }
}