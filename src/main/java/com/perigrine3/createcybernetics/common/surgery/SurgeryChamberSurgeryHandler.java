package com.perigrine3.createcybernetics.common.surgery;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.block.RobosurgeonBlock;
import com.perigrine3.createcybernetics.block.SurgeryChamberBlockBottom;
import com.perigrine3.createcybernetics.block.SurgeryChamberBlockTop;
import com.perigrine3.createcybernetics.block.entity.RobosurgeonBlockEntity;
import com.perigrine3.createcybernetics.common.damage.ModDamageSources;
import com.perigrine3.createcybernetics.common.energy.ConditionalBlockPower;
import com.perigrine3.createcybernetics.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = CreateCybernetics.MODID)
public final class SurgeryChamberSurgeryHandler {

    private SurgeryChamberSurgeryHandler() {}

    private static final int DURATION_TICKS = 20 * 5;
    private static final float CANCEL_DAMAGE = 0.0F;
    private static final double SOUND_STOP_RANGE = 32.0D;
    private static final double SOUND_STOP_RANGE_SQR = SOUND_STOP_RANGE * SOUND_STOP_RANGE;

    private static final DustParticleOptions BLOOD = new DustParticleOptions(new Vector3f(0.75f, 0.05f, 0.05f), 1.25f);

    private static final class ActiveSurgery {
        final UUID playerId;
        final BlockPos bottomPos;
        final BlockPos surgeonPos;
        final ResourceKey<Level> dimension;

        int ticksLeft = DURATION_TICKS;
        int particleTick;
        int damageTick;

        ActiveSurgery(ServerPlayer player, BlockPos bottomPos, BlockPos surgeonPos) {
            this.playerId = player.getUUID();
            this.bottomPos = bottomPos.immutable();
            this.surgeonPos = surgeonPos.immutable();
            this.dimension = player.level().dimension();
        }
    }

    private static final Map<UUID, ActiveSurgery> ACTIVE_BY_PLAYER = new HashMap<>();
    private static final Map<BlockPos, UUID> PLAYER_BY_BOTTOM_POS = new HashMap<>();

    public static boolean isActive(BlockPos bottomPos) {
        UUID playerId = PLAYER_BY_BOTTOM_POS.get(bottomPos);
        return playerId != null && ACTIVE_BY_PLAYER.containsKey(playerId);
    }

    public static void tryStartForClosedChamber(ServerLevel level, BlockPos bottomPos) {
        if (isActive(bottomPos)) return;

        BlockState bottomState = level.getBlockState(bottomPos);
        if (!(bottomState.getBlock() instanceof SurgeryChamberBlockBottom)) return;
        if (bottomState.getValue(SurgeryChamberBlockBottom.OPENED)) return;
        if (bottomState.getValue(SurgeryChamberBlockBottom.SURGERY_DONE)) return;

        BlockPos topPos = bottomPos.above();
        BlockState topState = level.getBlockState(topPos);
        if (!(topState.getBlock() instanceof SurgeryChamberBlockTop)) return;
        if (topState.getValue(SurgeryChamberBlockTop.OPENED)) return;
        if (!topState.getValue(SurgeryChamberBlockTop.CONNECTED)) return;

        BlockPos surgeonPos = topPos.above();
        if (!(level.getBlockEntity(surgeonPos) instanceof RobosurgeonBlockEntity surgeon)) return;
        if (!hasPendingSurgeryWork(surgeon)) return;

        List<ServerPlayer> players = level.getEntitiesOfClass(ServerPlayer.class, getChamberBounds(bottomPos), player -> player.isAlive() && !player.isSpectator());
        if (players.isEmpty()) return;

        ServerPlayer player = findClosestPlayer(players, bottomPos);

        startOrRefresh(player, level, bottomPos, surgeon);
    }

    public static void startOrRefresh(ServerPlayer player, Level level, BlockPos bottomPos, RobosurgeonBlockEntity surgeon) {
        if (!hasRequiredRobosurgeonPower(player, surgeon, false)) {
            sendUnpoweredMessage(player);
            return;
        }

        ActiveSurgery active = ACTIVE_BY_PLAYER.get(player.getUUID());

        if (active != null) {
            return;
        }

        UUID occupyingPlayerId = PLAYER_BY_BOTTOM_POS.get(bottomPos);
        if (occupyingPlayerId != null && !occupyingPlayerId.equals(player.getUUID())) {
            return;
        }

        ActiveSurgery created = new ActiveSurgery(player, bottomPos, surgeon.getBlockPos());
        ACTIVE_BY_PLAYER.put(player.getUUID(), created);
        PLAYER_BY_BOTTOM_POS.put(created.bottomPos, player.getUUID());

        player.level().playSound(null, player.blockPosition(), ModSounds.SURGERY.get(), SoundSource.BLOCKS, 0.55F, 0.8F);
    }

    public static void cancelIfActive(ServerLevel level, BlockPos bottomPos, boolean dealDamage) {
        UUID playerId = PLAYER_BY_BOTTOM_POS.remove(bottomPos);
        if (playerId == null) return;

        ActiveSurgery active = ACTIVE_BY_PLAYER.remove(playerId);
        if (active == null) return;

        stopSurgerySound(level, active.bottomPos);

        if (dealDamage) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(active.playerId);
            if (player != null) {
                player.hurt(ModDamageSources.cyberwareSurgery(level, player, null), CANCEL_DAMAGE);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        var server = event.getServer();
        if (server == null) return;

        Iterator<Map.Entry<UUID, ActiveSurgery>> it = ACTIVE_BY_PLAYER.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, ActiveSurgery> entry = it.next();
            ActiveSurgery active = entry.getValue();

            ServerLevel activeLevel = server.getLevel(active.dimension);

            ServerPlayer player = server.getPlayerList().getPlayer(active.playerId);
            if (player == null) {
                removeActiveSurgery(it, active, activeLevel, true);
                continue;
            }

            if (!(player.level() instanceof ServerLevel level)) {
                removeActiveSurgery(it, active, activeLevel, true);
                continue;
            }

            if (!player.level().dimension().equals(active.dimension)) {
                removeActiveSurgery(it, active, activeLevel, true);
                continue;
            }

            if (!isValidClosedChamber(level, active.bottomPos)) {
                removeActiveSurgery(it, active, level, true);
                continue;
            }

            if (!isPlayerInsideChamber(player, active.bottomPos)) {
                removeActiveSurgery(it, active, level, true);
                continue;
            }

            if (!(level.getBlockEntity(active.surgeonPos) instanceof RobosurgeonBlockEntity surgeon)) {
                removeActiveSurgery(it, active, level, true);
                continue;
            }

            if (!hasRequiredRobosurgeonPower(player, surgeon, true)) {
                sendUnpoweredMessage(player);
                removeActiveSurgery(it, active, level, true);
                continue;
            }

            tickBloodParticles(level, player, active);
            tickSurgeryDamage(level, player, active);

            active.ticksLeft--;
            if (active.ticksLeft > 0) continue;

            SurgeryController.performSurgery(player, surgeon);

            BlockState bottomState = level.getBlockState(active.bottomPos);
            if (bottomState.hasProperty(SurgeryChamberBlockBottom.SURGERY_DONE)) {
                level.setBlock(active.bottomPos, bottomState.setValue(SurgeryChamberBlockBottom.SURGERY_DONE, true), 3);
            }

            removeActiveSurgery(it, active, level, false);
        }
    }

    private static void removeActiveSurgery(Iterator<Map.Entry<UUID, ActiveSurgery>> iterator, ActiveSurgery active, ServerLevel level, boolean stopSound) {
        PLAYER_BY_BOTTOM_POS.remove(active.bottomPos);
        iterator.remove();

        if (stopSound && level != null) {
            stopSurgerySound(level, active.bottomPos);
        }
    }

    private static void stopSurgerySound(ServerLevel level, BlockPos bottomPos) {
        ClientboundStopSoundPacket packet = new ClientboundStopSoundPacket(ModSounds.SURGERY.get().getLocation(), SoundSource.BLOCKS);

        double centerX = bottomPos.getX() + 0.5D;
        double centerY = bottomPos.getY() + 1.0D;
        double centerZ = bottomPos.getZ() + 0.5D;

        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(centerX, centerY, centerZ) <= SOUND_STOP_RANGE_SQR) {
                player.connection.send(packet);
            }
        }
    }

    private static boolean isValidClosedChamber(ServerLevel level, BlockPos bottomPos) {
        BlockState bottomState = level.getBlockState(bottomPos);
        if (!(bottomState.getBlock() instanceof SurgeryChamberBlockBottom)) return false;
        if (bottomState.getValue(SurgeryChamberBlockBottom.OPENED)) return false;

        BlockState topState = level.getBlockState(bottomPos.above());
        if (!(topState.getBlock() instanceof SurgeryChamberBlockTop)) return false;
        if (topState.getValue(SurgeryChamberBlockTop.OPENED)) return false;
        if (!topState.getValue(SurgeryChamberBlockTop.CONNECTED)) return false;

        return true;
    }

    private static boolean isPlayerInsideChamber(ServerPlayer player, BlockPos bottomPos) {
        return player.getBoundingBox().intersects(getChamberBounds(bottomPos));
    }

    private static AABB getChamberBounds(BlockPos bottomPos) {
        return new AABB(bottomPos.getX(), bottomPos.getY(), bottomPos.getZ(), bottomPos.getX() + 1.0D, bottomPos.getY() + 2.0D, bottomPos.getZ() + 1.0D);
    }

    private static ServerPlayer findClosestPlayer(List<ServerPlayer> players, BlockPos bottomPos) {
        double centerX = bottomPos.getX() + 0.5D;
        double centerY = bottomPos.getY() + 1.0D;
        double centerZ = bottomPos.getZ() + 0.5D;

        ServerPlayer closest = players.getFirst();
        double closestDistance = closest.distanceToSqr(centerX, centerY, centerZ);

        for (int i = 1; i < players.size(); i++) {
            ServerPlayer candidate = players.get(i);
            double candidateDistance = candidate.distanceToSqr(centerX, centerY, centerZ);

            if (candidateDistance < closestDistance) {
                closest = candidate;
                closestDistance = candidateDistance;
            }
        }

        return closest;
    }

    private static boolean hasPendingSurgeryWork(RobosurgeonBlockEntity surgeon) {
        boolean[] marked = surgeon.markedForRemoval;
        if (marked != null) {
            for (boolean value : marked) {
                if (value) return true;
            }
        }

        boolean[] staged = surgeon.staged;
        if (staged != null) {
            int slots = surgeon.inventory.getSlots();
            int length = Math.min(staged.length, slots);

            for (int i = 0; i < length; i++) {
                if (!staged[i]) continue;
                if (!surgeon.inventory.getStackInSlot(i).isEmpty()) return true;
            }
        }

        return false;
    }

    private static boolean hasRequiredRobosurgeonPower(ServerPlayer player, RobosurgeonBlockEntity surgeon, boolean consume) {
        if (player == null || surgeon == null) {
            return false;
        }

        if (player.level().isClientSide) {
            return false;
        }

        Level level = surgeon.getLevel();

        if (level == null || level.isClientSide) {
            return false;
        }

        if (consume) {
            return ConditionalBlockPower.consumeRequiredPower(level, surgeon.getBlockPos(), surgeon.getMutableEnergyStorage(), RobosurgeonBlock.ENERGY_USED_PER_GUI_TICK);
        }

        return ConditionalBlockPower.hasRequiredPower(level, surgeon.getBlockPos(), surgeon.getMutableEnergyStorage(), RobosurgeonBlock.ENERGY_REQUIRED_TO_OPEN);
    }

    private static void sendUnpoweredMessage(ServerPlayer player) {
        if (ConditionalBlockPower.shouldUseEnergyInsteadOfRedstone()) {
            player.displayClientMessage(Component.translatable("message.createcybernetics.block.requires_energy"), true);
        } else {
            player.displayClientMessage(Component.translatable("message.createcybernetics.block.requires_redstone"), true);
        }
    }

    private static void tickBloodParticles(ServerLevel level, ServerPlayer player, ActiveSurgery active) {
        active.particleTick++;
        if ((active.particleTick % 20) == 0) {
            level.sendParticles(BLOOD, player.getX(), player.getY() + 1.0D, player.getZ(), 10, 0.2D, 0.35D, 0.2D, 1.0D);
        }
    }

    private static void tickSurgeryDamage(ServerLevel level, ServerPlayer player, ActiveSurgery active) {
        active.damageTick++;
        if ((active.damageTick % 20) == 0) {
            player.hurt(ModDamageSources.cyberwareSurgery(level, player, null), 2.0F);
        }
    }
}