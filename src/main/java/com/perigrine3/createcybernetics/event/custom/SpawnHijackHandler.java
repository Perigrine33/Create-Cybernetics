package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.ConfigValues;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class SpawnHijackHandler {

    private static final String NBT_NO_HIJACK = "cc_no_hijack";
    private static final int VANILLA_REPLACEMENT_WEIGHT = 100;

    private SpawnHijackHandler() {}

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        Entity source = event.getEntity();

        if (source.getPersistentData().getBoolean(NBT_NO_HIJACK)) return;
        if (!(source instanceof Monster sourceMob)) return;

        ReplacementDefinition definition = getReplacementDefinition(source);
        if (definition == null) return;
        if (definition.configuredWeight() <= 0) return;

        MobSpawnType spawnType = getSpawnType(sourceMob);
        if (!canReplaceSpawn(spawnType)) return;
        if (!passesConfiguredWeight(level, definition.configuredWeight())) return;

        Mob replacement = definition.replacementType().create(level);
        if (replacement == null) return;

        replacement.getPersistentData().putBoolean(NBT_NO_HIJACK, true);

        copyEntityState(sourceMob, replacement);

        BlockPos spawnPos = replacement.blockPosition();
        MobSpawnType finalizeType = spawnType != null ? spawnType : MobSpawnType.NATURAL;
        SpawnGroupData spawnGroupData = replacement.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), finalizeType, null);

        event.setCanceled(true);
        source.discard();

        level.addFreshEntity(replacement);
    }

    @Nullable
    private static ReplacementDefinition getReplacementDefinition(Entity source) {
        if (source.getType() == EntityType.ZOMBIE && source instanceof Zombie) {
            return new ReplacementDefinition(ModEntities.CYBERZOMBIE.get(), ConfigValues.CYBERZOMBIE_SPAWN_WEIGHT);
        }

        if (source.getType() == EntityType.SKELETON && source instanceof Skeleton) {
            return new ReplacementDefinition(ModEntities.CYBERSKELETON.get(), ConfigValues.CYBERSKELETON_SPAWN_WEIGHT);
        }

        return null;
    }

    private static boolean passesConfiguredWeight(ServerLevel level, int configuredWeight) {
        int cyberWeight = Math.max(0, configuredWeight);
        if (cyberWeight == 0) return false;

        long totalWeight = (long) VANILLA_REPLACEMENT_WEIGHT + cyberWeight;
        long roll = Math.floorMod(level.getRandom().nextLong(), totalWeight);

        return roll < cyberWeight;
    }

    private static boolean canReplaceSpawn(@Nullable MobSpawnType spawnType) {
        if (spawnType == null) return true;

        return spawnType != MobSpawnType.CONVERSION
                && spawnType != MobSpawnType.COMMAND
                && spawnType != MobSpawnType.SPAWN_EGG
                && spawnType != MobSpawnType.BUCKET
                && spawnType != MobSpawnType.DISPENSER
                && spawnType != MobSpawnType.TRIGGERED;
    }

    @Nullable
    private static MobSpawnType getSpawnType(Monster mob) {
        try {
            return mob.getSpawnType();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static void copyEntityState(Mob source, Mob replacement) {
        replacement.moveTo(source.getX(), source.getY(), source.getZ(), source.getYRot(), source.getXRot());
        replacement.setYHeadRot(source.getYHeadRot());
        replacement.setYBodyRot(source.yBodyRot);
        replacement.setDeltaMovement(source.getDeltaMovement());

        replacement.setSilent(source.isSilent());
        replacement.setNoGravity(source.isNoGravity());
        replacement.setInvulnerable(source.isInvulnerable());
        replacement.setGlowingTag(source.isCurrentlyGlowing());
        replacement.setRemainingFireTicks(source.getRemainingFireTicks());
        replacement.setPortalCooldown();

        replacement.setCanPickUpLoot(source.canPickUpLoot());
        replacement.setLeftHanded(source.isLeftHanded());
        replacement.setNoAi(source.isNoAi());

        if (source.isPersistenceRequired()) {
            replacement.setPersistenceRequired();
        }

        if (source.hasCustomName()) {
            replacement.setCustomName(source.getCustomName());
            replacement.setCustomNameVisible(source.isCustomNameVisible());
        }

        copyEquipment(source, replacement);
    }

    private static void copyEquipment(Mob source, Mob replacement) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = source.getItemBySlot(slot);
            if (stack.isEmpty()) continue;

            replacement.setItemSlot(slot, stack.copy());
            source.setItemSlot(slot, ItemStack.EMPTY);
        }
    }

    private record ReplacementDefinition(EntityType<? extends Mob> replacementType, int configuredWeight) {
    }
}