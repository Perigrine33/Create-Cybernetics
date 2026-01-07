package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.attributes.ModAttributes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class BreedingOutputHandler {
    private BreedingOutputHandler() {}

    // queue of pending duplications to process next tick
    private static final List<PendingBreed> PENDING = new ArrayList<>();

    @SubscribeEvent
    public static void onBabySpawn(BabyEntitySpawnEvent event) {
        Player player = event.getCausedByPlayer();
        if (player == null) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        if (!(event.getParentA() instanceof Animal parentA && event.getParentB() instanceof Animal parentB)) return;
        if (!(event.getChild() instanceof AgeableMob child)) return;

        double mult = player.getAttributeValue(ModAttributes.BREEDING_MULTIPLIER);
        if (!Double.isFinite(mult) || mult <= 1.0D) return;

        int guaranteed = (int) Math.floor(mult) - 1;
        double remainder = mult - Math.floor(mult);
        if (remainder > 0 && level.random.nextDouble() < remainder) guaranteed++;
        if (guaranteed <= 0) return;

        PENDING.add(new PendingBreed(level, parentA, parentB, child, guaranteed));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (PENDING.isEmpty()) return;

        List<PendingBreed> copy = new ArrayList<>(PENDING);
        PENDING.clear();

        for (PendingBreed p : copy) {
            if (p.level.isClientSide()) continue;

            for (int i = 0; i < p.extras; i++) {
                AgeableMob extra = p.parentA.getBreedOffspring(p.level, p.parentB);
                if (extra == null) continue;

                extra.setAge(-24000);
                extra.moveTo(
                        p.child.getX() + (p.level.random.nextDouble() - 0.5D) * 0.6D,
                        p.child.getY(),
                        p.child.getZ() + (p.level.random.nextDouble() - 0.5D) * 0.6D,
                        p.level.random.nextFloat() * 360.0F,
                        0.0F
                );
                p.level.addFreshEntity(extra);
            }
        }
    }

    private record PendingBreed(ServerLevel level, Animal parentA, Animal parentB, AgeableMob child, int extras) {}
}
