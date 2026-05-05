package com.perigrine3.createcybernetics.common.surgery;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.block.SurgeryChamberBlockBottom;
import com.perigrine3.createcybernetics.block.entity.RobosurgeonBlockEntity;
import com.perigrine3.createcybernetics.common.damage.ModDamageSources;
import com.perigrine3.createcybernetics.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = CreateCybernetics.MODID)
public final class SurgeryChamberSurgeryHandler {

    private SurgeryChamberSurgeryHandler() {}

    private static final int DURATION_TICKS = 20 * 5;
    private static final int HEARTBEAT_GRACE_TICKS = 10;
    private static final float CANCEL_DAMAGE = 0.0F;

    private static final DustParticleOptions BLOOD =
            new DustParticleOptions(new Vector3f(0.75f, 0.05f, 0.05f), 1.25f);

    private static final class ActiveSurgery {
        final UUID playerId;
        final BlockPos bottomPos;
        final RobosurgeonBlockEntity surgeon;

        int ticksLeft = DURATION_TICKS;
        int particleTick;
        int damageTick;
        long lastHeartbeatGameTime;

        ActiveSurgery(ServerPlayer player, BlockPos bottomPos, RobosurgeonBlockEntity surgeon) {
            this.playerId = player.getUUID();
            this.bottomPos = bottomPos.immutable();
            this.surgeon = surgeon;
            this.lastHeartbeatGameTime = player.level().getGameTime();
        }
    }

    private static final Map<UUID, ActiveSurgery> ACTIVE_BY_PLAYER = new HashMap<>();
    private static final Map<BlockPos, UUID> PLAYER_BY_BOTTOM_POS = new HashMap<>();

    public static boolean isActive(BlockPos bottomPos) {
        UUID playerId = PLAYER_BY_BOTTOM_POS.get(bottomPos);
        return playerId != null && ACTIVE_BY_PLAYER.containsKey(playerId);
    }

    public static void startOrRefresh(ServerPlayer player, Level level, BlockPos bottomPos, RobosurgeonBlockEntity surgeon) {
        ActiveSurgery active = ACTIVE_BY_PLAYER.get(player.getUUID());

        if (active != null) {
            active.lastHeartbeatGameTime = player.level().getGameTime();
            return;
        }

        ActiveSurgery created = new ActiveSurgery(player, bottomPos, surgeon);
        ACTIVE_BY_PLAYER.put(player.getUUID(), created);
        PLAYER_BY_BOTTOM_POS.put(created.bottomPos, player.getUUID());

        player.level().playSound(null, player.blockPosition(), ModSounds.SURGERY.get(), SoundSource.BLOCKS, 0.55F, 0.8F);
    }

    public static void cancelIfActive(ServerLevel level, BlockPos bottomPos, boolean dealDamage) {
        UUID playerId = PLAYER_BY_BOTTOM_POS.remove(bottomPos);
        if (playerId == null) return;

        ActiveSurgery active = ACTIVE_BY_PLAYER.remove(playerId);
        if (active == null) return;

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

            ServerPlayer player = server.getPlayerList().getPlayer(active.playerId);
            if (player == null) {
                PLAYER_BY_BOTTOM_POS.remove(active.bottomPos);
                it.remove();
                continue;
            }

            if (!(player.level() instanceof ServerLevel level)) {
                PLAYER_BY_BOTTOM_POS.remove(active.bottomPos);
                it.remove();
                continue;
            }

            long now = level.getGameTime();
            if (now - active.lastHeartbeatGameTime > HEARTBEAT_GRACE_TICKS) {
                PLAYER_BY_BOTTOM_POS.remove(active.bottomPos);
                it.remove();
                continue;
            }

            tickBloodParticles(level, player, active);
            tickSurgeryDamage(level, player, active);

            active.ticksLeft--;
            if (active.ticksLeft > 0) continue;

            SurgeryController.performSurgery(player, active.surgeon);

            BlockState bottomState = level.getBlockState(active.bottomPos);
            if (bottomState.hasProperty(SurgeryChamberBlockBottom.SURGERY_DONE)) {
                level.setBlock(active.bottomPos, bottomState.setValue(SurgeryChamberBlockBottom.SURGERY_DONE, true), 3);
            }

            PLAYER_BY_BOTTOM_POS.remove(active.bottomPos);
            it.remove();
        }
    }

    private static void tickBloodParticles(ServerLevel level, ServerPlayer player, ActiveSurgery active) {
        active.particleTick++;
        if ((active.particleTick % 20) == 0) {
            level.sendParticles(BLOOD, player.getX(), player.getY() + 1.0, player.getZ(),
                    10, 0.2, 0.35, 0.2, 1);
        }
    }

    private static void tickSurgeryDamage(ServerLevel level, ServerPlayer player, ActiveSurgery active) {
        active.damageTick++;
        if ((active.damageTick % 20) == 0) {
            player.hurt(ModDamageSources.cyberwareSurgery(level, player, null), 2.0F);
        }
    }
}