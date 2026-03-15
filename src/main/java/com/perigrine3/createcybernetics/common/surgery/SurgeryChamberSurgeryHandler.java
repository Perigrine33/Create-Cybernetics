package com.perigrine3.createcybernetics.common.surgery;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.block.SurgeryChamberBlockBottom;
import com.perigrine3.createcybernetics.block.SurgeryChamberBlockTop;
import com.perigrine3.createcybernetics.block.entity.RobosurgeonBlockEntity;
import com.perigrine3.createcybernetics.common.damage.ModDamageSources;
import com.perigrine3.createcybernetics.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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

    private static final int DURATION_TICKS = 20 * 5; // 5 seconds
    private static final float CANCEL_DAMAGE = 0.0F;

    private static final DustParticleOptions BLOOD =
            new DustParticleOptions(new Vector3f(0.75f, 0.05f, 0.05f), 1.25f);

    private static final class ActiveSurgery {
        final UUID playerId;
        final BlockPos bottomPos;
        final BlockPos topPos;
        final BlockPos surgeonPos;
        final AABB chamberBox;

        int ticksLeft;
        int particleTick;
        int damageTick;

        ActiveSurgery(UUID playerId, BlockPos bottomPos, BlockPos topPos, BlockPos surgeonPos) {
            this.playerId = playerId;
            this.bottomPos = bottomPos.immutable();
            this.topPos = topPos.immutable();
            this.surgeonPos = surgeonPos.immutable();
            this.chamberBox = new AABB(this.bottomPos).expandTowards(0, 2, 0).inflate(0.1);
            this.ticksLeft = DURATION_TICKS;
            this.particleTick = 0;
            this.damageTick = 0;
        }
    }

    private static final Map<BlockPos, ActiveSurgery> ACTIVE = new HashMap<>();

    public static boolean isActive(BlockPos bottomPos) {
        return ACTIVE.containsKey(bottomPos);
    }

    private static boolean isPlayerInActiveSurgery(UUID playerId) {
        for (ActiveSurgery as : ACTIVE.values()) {
            if (as.playerId.equals(playerId)) {
                return true;
            }
        }
        return false;
    }

    public static void start(ServerPlayer player, ServerLevel level, BlockPos bottomPos, BlockPos topPos, BlockPos surgeonPos) {
        if (ACTIVE.containsKey(bottomPos)) return;
        if (isPlayerInActiveSurgery(player.getUUID())) return;

        ACTIVE.put(bottomPos, new ActiveSurgery(player.getUUID(), bottomPos, topPos, surgeonPos));
        level.playSound(null, player.blockPosition(), ModSounds.SURGERY.get(), SoundSource.BLOCKS, 0.55f, 0.8f);
    }

    public static void cancelIfActive(ServerLevel level, BlockPos bottomPos, boolean dealDamage) {
        ActiveSurgery as = ACTIVE.remove(bottomPos);
        if (as == null) return;

        if (dealDamage) {
            ServerPlayer sp = level.getServer().getPlayerList().getPlayer(as.playerId);
            if (sp != null) {
                sp.hurt(ModDamageSources.cyberwareSurgery(level, sp, null), CANCEL_DAMAGE);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        var server = event.getServer();
        if (server == null) return;

        Iterator<Map.Entry<BlockPos, ActiveSurgery>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, ActiveSurgery> entry = it.next();
            ActiveSurgery as = entry.getValue();

            ServerPlayer sp = server.getPlayerList().getPlayer(as.playerId);
            if (sp == null) {
                it.remove();
                continue;
            }

            if (!(sp.level() instanceof ServerLevel level)) {
                it.remove();
                continue;
            }

            BlockState bottomState = level.getBlockState(as.bottomPos);
            BlockState topState = level.getBlockState(as.topPos);

            if (!bottomState.is(ModBlocks.SURGERY_CHAMBER_BOTTOM.get()) || !topState.is(ModBlocks.SURGERY_CHAMBER_TOP.get())) {
                sp.hurt(ModDamageSources.cyberwareSurgery(level, sp, null), CANCEL_DAMAGE);
                it.remove();
                continue;
            }

            boolean bottomOpened = bottomState.getValue(SurgeryChamberBlockBottom.OPENED);
            boolean topOpened = topState.getValue(SurgeryChamberBlockTop.OPENED);
            if (bottomOpened || topOpened) {
                sp.hurt(ModDamageSources.cyberwareSurgery(level, sp, null), CANCEL_DAMAGE);
                it.remove();
                continue;
            }

            boolean connected = topState.getValue(SurgeryChamberBlockTop.CONNECTED);
            if (!connected) {
                it.remove();
                continue;
            }

            if (!sp.getBoundingBox().intersects(as.chamberBox)) {
                it.remove();
                continue;
            }

            tickBloodParticles(level, sp, as);
            tickSurgeryDamage(level, sp, as);

            as.ticksLeft--;
            if (as.ticksLeft > 0) continue;

            if (!(level.getBlockEntity(as.surgeonPos) instanceof RobosurgeonBlockEntity surgeon)) {
                it.remove();
                continue;
            }

            SurgeryController.performSurgery(sp, surgeon);

            BlockState nowBottom = level.getBlockState(as.bottomPos);
            if (nowBottom.is(ModBlocks.SURGERY_CHAMBER_BOTTOM.get())) {
                level.setBlock(as.bottomPos, nowBottom.setValue(SurgeryChamberBlockBottom.SURGERY_DONE, true), 3);
            }

            it.remove();
        }
    }

    private static void tickBloodParticles(ServerLevel level, ServerPlayer player, ActiveSurgery as) {
        as.particleTick++;
        if ((as.particleTick % 20) == 0) {
            level.sendParticles(BLOOD, player.getX(), player.getY() + 1.0, player.getZ(),
                    10, 0.2, 0.35, 0.2, 1);
        }
    }

    private static void tickSurgeryDamage(ServerLevel level, ServerPlayer player, ActiveSurgery as) {
        as.damageTick++;
        if ((as.damageTick % 20) == 0) {
            player.hurt(ModDamageSources.cyberwareSurgery(level, player, null), 2.0F);
        }
    }
}