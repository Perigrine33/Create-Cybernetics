package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class SpawnHijackHandler {
    private SpawnHijackHandler() {}

    private static final String NBT_NO_HIJACK = "cc_no_hijack";
    private static final float ZOMBIE_REPLACE_CHANCE   = 0.15f;
    private static final float SKELETON_REPLACE_CHANCE = 0.10f;

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        Entity entity = event.getEntity();

        if (entity.getPersistentData().getBoolean(NBT_NO_HIJACK)) return;
        if (entity.getType() == ModEntities.CYBERZOMBIE.get()) return;
        if (entity.getType() == ModEntities.CYBERSKELETON.get()) return;

        final boolean isZombie = entity.getType() == EntityType.ZOMBIE && entity instanceof Zombie;
        final boolean isSkeleton = entity.getType() == EntityType.SKELETON && entity instanceof Skeleton;

        if (!isZombie && !isSkeleton) return;

        float chance = isZombie ? ZOMBIE_REPLACE_CHANCE : SKELETON_REPLACE_CHANCE;
        if (level.getRandom().nextFloat() >= chance) return;

        @Nullable MobSpawnType spawnType = null;
        try {
            spawnType = ((Monster) entity).getSpawnType();
        } catch (Throwable ignored) {
        }

        if (spawnType == MobSpawnType.CONVERSION) return;
        if (spawnType == MobSpawnType.COMMAND) return;
        if (spawnType == MobSpawnType.SPAWN_EGG) return;

        Mob replacement = isZombie
                ? ModEntities.CYBERZOMBIE.get().create(level)
                : ModEntities.CYBERSKELETON.get().create(level);

        if (replacement == null) return;

        replacement.getPersistentData().putBoolean(NBT_NO_HIJACK, true);

        replacement.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
        replacement.setDeltaMovement(entity.getDeltaMovement());

        if (entity.hasCustomName()) {
            replacement.setCustomName(entity.getCustomName());
            replacement.setCustomNameVisible(entity.isCustomNameVisible());
        }
        if (entity.isSilent()) replacement.setSilent(true);

        BlockPos at = BlockPos.containing(replacement.position());
        SpawnGroupData groupData = null;
        MobSpawnType finalizeType = (spawnType != null) ? spawnType : MobSpawnType.NATURAL;
        replacement.finalizeSpawn(level, level.getCurrentDifficultyAt(at), finalizeType, groupData);

        event.setCanceled(true);
        entity.discard();

        level.addFreshEntity(replacement);
    }
}
