package com.perigrine3.createcybernetics.world;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.ModEntities;
import com.perigrine3.createcybernetics.entity.custom.SmasherEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class MansionBossSpawner {

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        CreateCybernetics.LOGGER.info("MansionBossSpawner fired for chunk {}", event.getChunk().getPos());

        if (!(event.getChunk() instanceof net.minecraft.world.level.chunk.LevelChunk)) return;

        ChunkPos chunkPos = event.getChunk().getPos();
        BlockPos sample = chunkPos.getWorldPosition().offset(8, 64, 8);

        Structure mansion = level.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getHolderOrThrow(BuiltinStructures.WOODLAND_MANSION)
                .value();

        StructureStart start = level.structureManager().getStructureAt(sample, mansion);
        if (start == null || !start.isValid()) return;

        long mansionKey = start.getChunkPos().toLong();
        MansionBossSavedData data = MansionBossSavedData.get(level);
        if (data.hasSpawned(mansionKey)) return;

        RandomSource r = level.getRandom();
        BoundingBox box = start.getBoundingBox();

        BlockPos spawnPos = pickSpawnPosInside(level, box, r);
        if (spawnPos == null) return;

        SmasherEntity smasher = ModEntities.SMASHER.get().create(level);
        if (smasher == null) return;

        smasher.moveTo(
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                r.nextFloat() * 360.0f,
                0.0f
        );
        smasher.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.STRUCTURE, null);

        level.addFreshEntity(smasher);
        data.markSpawned(mansionKey);
    }

    private static BlockPos pickSpawnPosInside(ServerLevel level, BoundingBox box, RandomSource r) {
        for (int i = 0; i < 60; i++) {
            int x = r.nextInt(box.maxX() - box.minX() + 1) + box.minX();
            int z = r.nextInt(box.maxZ() - box.minZ() + 1) + box.minZ();

            BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
            if (!box.isInside(top)) continue;

            for (int dy = 0; dy <= 12; dy++) {
                BlockPos p = top.below(dy);
                if (!box.isInside(p)) break;

                if (!level.getBlockState(p).getCollisionShape(level, p).isEmpty()) continue; // standing inside block
                if (!level.getBlockState(p.below()).getCollisionShape(level, p.below()).isEmpty()) {
                    if (level.getMaxLocalRawBrightness(p) <= 7) return p;
                }
            }
        }

        int cx = (box.minX() + box.maxX()) / 2;
        int cz = (box.minZ() + box.maxZ()) / 2;
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(cx, 0, cz));
    }

    private MansionBossSpawner() {}
}
