package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.attributes.ModAttributes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.inventory.MerchantMenu;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class XpMultiplierHandler {

    private XpMultiplierHandler() {}

    private static final boolean DEBUG_XP_MESSAGES = false;

    private static void debugXp(ServerPlayer player, String source, int baseXp, int finalXp, double mult) {
        if (!DEBUG_XP_MESSAGES) return;
        if (baseXp <= 0) return;
        if (baseXp == finalXp) return;

        player.sendSystemMessage(Component.literal("[XP] " + source + ": base=" + baseXp + " -> final=" + finalXp + " (x" + mult + ")"));
    }

    @SubscribeEvent
    public static void onMobXp(LivingExperienceDropEvent event) {
        if (!(event.getAttackingPlayer() instanceof ServerPlayer player)) return;

        int baseXp = event.getDroppedExperience();
        if (baseXp <= 0) return;

        double mult = player.getAttributeValue(ModAttributes.XP_GAIN_MULTIPLIER);
        if (mult <= 1.0) return;

        int finalXp = (int) Math.floor(baseXp * mult);
        if (finalXp == baseXp) return;

        event.setDroppedExperience(finalXp);
        debugXp(player, "mob", baseXp, finalXp, mult);
    }

    @SubscribeEvent
    public static void onBlockDropsXp(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof ServerPlayer player)) return;

        int baseXp = event.getDroppedExperience();
        if (baseXp <= 0) return;

        double mult = player.getAttributeValue(ModAttributes.XP_GAIN_MULTIPLIER);
        if (mult <= 1.0) return;

        int finalXp = (int) Math.floor(baseXp * mult);
        if (finalXp == baseXp) return;

        event.setDroppedExperience(finalXp);
        debugXp(player, "block", baseXp, finalXp, mult);
    }

    @SubscribeEvent
    public static void onFishingXp(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ExperienceOrb orb)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        // Save before we modify
        int baseXp = orb.value;
        if (baseXp <= 0) return;

        for (Player p : level.getEntitiesOfClass(Player.class, orb.getBoundingBox().inflate(4))) {
            // Your heuristic: nearby player currently fishing (bobber exists and not removed)
            if (p.fishing instanceof FishingHook hook && !hook.isRemoved() && p instanceof ServerPlayer sp) {
                double mult = sp.getAttributeValue(ModAttributes.XP_GAIN_MULTIPLIER);
                if (mult <= 1.0) return;

                int finalXp = (int) Math.floor(baseXp * mult);
                if (finalXp == baseXp) return;

                orb.value = finalXp;
                debugXp(sp, "fishing", baseXp, finalXp, mult);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onBreedingXp(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ExperienceOrb orb)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        int baseXp = orb.value;
        if (baseXp <= 0) return;

        level.getEntitiesOfClass(Animal.class, orb.getBoundingBox().inflate(3)).stream().map(Animal::getLoveCause)
                .filter(p -> p instanceof ServerPlayer).map(p -> (ServerPlayer) p).findFirst()
                .ifPresent(sp -> {
                    double mult = sp.getAttributeValue(ModAttributes.XP_GAIN_MULTIPLIER);
                    if (mult <= 1.0) return;

                    int finalXp = (int) Math.floor(baseXp * mult);
                    if (finalXp == baseXp) return;

                    orb.value = finalXp;
                    debugXp(sp, "breeding", baseXp, finalXp, mult);
                });
    }

    @SubscribeEvent
    public static void onTradingXp(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ExperienceOrb orb)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        int baseXp = orb.value;
        if (baseXp <= 0) return;

        for (Player p : level.getEntitiesOfClass(Player.class, orb.getBoundingBox().inflate(4))) {
            if (p instanceof ServerPlayer sp && sp.containerMenu instanceof MerchantMenu) {
                double mult = sp.getAttributeValue(ModAttributes.XP_GAIN_MULTIPLIER);
                if (mult <= 1.0) return;

                int finalXp = (int) Math.floor(baseXp * mult);
                if (finalXp == baseXp) return;

                orb.value = finalXp;
                debugXp(sp, "trading", baseXp, finalXp, mult);
                return;
            }
        }
    }
}
