package com.perigrine3.createcybernetics.event;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.ModEntities;
import com.perigrine3.createcybernetics.entity.custom.SmasherEntity;
import com.perigrine3.createcybernetics.world.MansionBossSpawnData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

@EventBusSubscriber(modid = CreateCybernetics.MODID)
public final class MansionSmasherChunkLoadHandler {

    private static final double EXISTING_SMASHER_CHECK_RADIUS = 96.0D;
    private static final int SPAWN_ATTEMPTS = 420;

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
        ChunkPos chunkPos = access.getPos();

        level.getServer().execute(() -> processNewChunk(level, chunkPos));
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

        BlockPos spawnPosBlock = findInteriorLikeSpot(level, box, level.getRandom());
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
                surfacePos(level, midX, midZ),
                surfacePos(level, minX + 2, minZ + 2),
                surfacePos(level, maxX - 2, minZ + 2),
                surfacePos(level, minX + 2, maxZ - 2),
                surfacePos(level, maxX - 2, maxZ - 2),
                surfacePos(level, midX, minZ + 2),
                surfacePos(level, midX, maxZ - 2),
                surfacePos(level, minX + 2, midZ),
                surfacePos(level, maxX - 2, midZ)
        };

        for (BlockPos sample : samples) {
            StructureStart start = level.structureManager().getStructureAt(sample, mansion);
            if (start != null && start.isValid()) {
                return start;
            }
        }

        return null;
    }

    private static BlockPos surfacePos(ServerLevel level, int x, int z) {
        return level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, new BlockPos(x, 0, z));
    }

    @Nullable
    private static BlockPos findInteriorLikeSpot(ServerLevel level, BoundingBox box, RandomSource random) {
        for (int i = 0; i < SPAWN_ATTEMPTS; i++) {
            int x = random.nextInt(box.maxX() - box.minX() + 1) + box.minX();
            int z = random.nextInt(box.maxZ() - box.minZ() + 1) + box.minZ();
            int y = random.nextInt(Math.max(1, box.maxY() - box.minY() + 1)) + box.minY();

            BlockPos start = new BlockPos(x, y, z);

            for (int down = 0; down <= 16; down++) {
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
        return isNonColliding(level.getBlockState(pos), level, pos)
                && isNonColliding(level.getBlockState(pos.above()), level, pos.above());
    }

    private static boolean hasSolidFloor(ServerLevel level, BlockPos pos) {
        BlockPos below = pos.below();
        return !isNonColliding(level.getBlockState(below), level, below);
    }

    private static boolean hasCeilingSoon(ServerLevel level, BlockPos pos) {
        BlockPos head = pos.above();

        for (int i = 1; i <= 4; i++) {
            BlockPos check = head.above(i);
            if (!isNonColliding(level.getBlockState(check), level, check)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isSomewhatEnclosed(ServerLevel level, BlockPos pos) {
        int solid = 0;

        if (!isNonColliding(level.getBlockState(pos.north()), level, pos.north())) solid++;
        if (!isNonColliding(level.getBlockState(pos.south()), level, pos.south())) solid++;
        if (!isNonColliding(level.getBlockState(pos.west()), level, pos.west())) solid++;
        if (!isNonColliding(level.getBlockState(pos.east()), level, pos.east())) solid++;

        return solid >= 2;
    }

    private static boolean isNonColliding(BlockState state, ServerLevel level, BlockPos pos) {
        return state.getCollisionShape(level, pos).isEmpty();
    }
}