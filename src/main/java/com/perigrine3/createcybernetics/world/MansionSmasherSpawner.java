package com.perigrine3.createcybernetics.world;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.ModEntities;
import com.perigrine3.createcybernetics.entity.custom.SmasherEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class MansionSmasherSpawner {

    private static final float CHANCE_PER_MANSION = 0.25f;
    private static final int CHECK_EVERY_TICKS = 100;

    private MansionSmasherSpawner() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        if (player.isSpectator()) return;

        if ((player.tickCount % CHECK_EVERY_TICKS) != 0) return;

        Structure mansion = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(BuiltinStructures.WOODLAND_MANSION).value();

        BlockPos playerPos = player.blockPosition();
        StructureStart start = level.structureManager().getStructureAt(playerPos, mansion);
        if (start == null || !start.isValid()) return;

        long key = start.getChunkPos().toLong();
        MansionBossSavedData data = MansionBossSavedData.get(level);

        if (data.isSpawned(key) || data.isRolledFail(key)) return;
        if (!data.isPending(key)) {
            RandomSource r = level.getRandom();
            if (r.nextFloat() >= CHANCE_PER_MANSION) {
                data.markRolledFail(key);
                return;
            }
            data.markPending(key);
        }

        BoundingBox box = start.getBoundingBox();
        RandomSource r = level.getRandom();

        BlockPos spawnPos = findInteriorLikeSpot(level, box, playerPos, r);
        if (spawnPos == null) return;
        if (!level.isPositionEntityTicking(spawnPos)) return;

        SmasherEntity smasher = ModEntities.SMASHER.get().create(level);
        if (smasher == null) return;

        smasher.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, r.nextFloat() * 360.0f, 0.0f);

        smasher.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.STRUCTURE, null);
        level.addFreshEntity(smasher);

        data.markSpawned(key);

        CreateCybernetics.LOGGER.info("Spawned Smasher in Woodland Mansion at {} (key={})", spawnPos, key);
    }

    private static BlockPos findInteriorLikeSpot(ServerLevel level, BoundingBox box, BlockPos around, RandomSource r) {
        for (int i = 0; i < 180; i++) {
            int dx = r.nextInt(33) - 16;
            int dz = r.nextInt(33) - 16;
            int dy = r.nextInt(9) - 4;

            BlockPos p = around.offset(dx, dy, dz);
            if (!box.isInside(p)) continue;

            if (!isTwoTallAir(level, p)) continue;
            if (!hasSolidFloor(level, p)) continue;
            if (!hasCeilingSoon(level, p)) continue;
            if (!isSomewhatEnclosed(level, p)) continue;

            return p;
        }

        int minX = box.minX(), maxX = box.maxX();
        int minY = box.minY(), maxY = box.maxY();
        int minZ = box.minZ(), maxZ = box.maxZ();

        for (int i = 0; i < 260; i++) {
            int x = r.nextInt(maxX - minX + 1) + minX;
            int z = r.nextInt(maxZ - minZ + 1) + minZ;
            int y = r.nextInt(Math.max(1, (maxY - minY + 1))) + minY;

            BlockPos p = new BlockPos(x, y, z);
            if (!box.isInside(p)) continue;

            for (int d = 0; d <= 12; d++) {
                BlockPos q = p.below(d);
                if (!box.isInside(q)) break;

                if (!isTwoTallAir(level, q)) continue;
                if (!hasSolidFloor(level, q)) continue;
                if (!hasCeilingSoon(level, q)) continue;
                if (!isSomewhatEnclosed(level, q)) continue;

                return q;
            }
        }

        return null;
    }

    private static boolean isTwoTallAir(ServerLevel level, BlockPos p) {
        return isNonColliding(level.getBlockState(p), level, p)
                && isNonColliding(level.getBlockState(p.above()), level, p.above());
    }

    private static boolean hasSolidFloor(ServerLevel level, BlockPos p) {
        BlockPos below = p.below();
        return !isNonColliding(level.getBlockState(below), level, below);
    }

    private static boolean hasCeilingSoon(ServerLevel level, BlockPos p) {
        BlockPos head = p.above();
        for (int i = 1; i <= 4; i++) {
            BlockPos c = head.above(i);
            if (!isNonColliding(level.getBlockState(c), level, c)) return true;
        }
        return false;
    }

    private static boolean isSomewhatEnclosed(ServerLevel level, BlockPos p) {
        int solid = 0;
        if (!isNonColliding(level.getBlockState(p.north()), level, p.north())) solid++;
        if (!isNonColliding(level.getBlockState(p.south()), level, p.south())) solid++;
        if (!isNonColliding(level.getBlockState(p.west()), level, p.west())) solid++;
        if (!isNonColliding(level.getBlockState(p.east()), level, p.east())) solid++;
        return solid >= 2;
    }

    private static boolean isNonColliding(BlockState state, ServerLevel level, BlockPos pos) {
        return state.getCollisionShape(level, pos).isEmpty();
    }
}
