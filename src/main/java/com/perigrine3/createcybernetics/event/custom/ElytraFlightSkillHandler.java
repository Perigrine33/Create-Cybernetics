package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.attributes.ModAttributes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class ElytraFlightSkillHandler {
    private ElytraFlightSkillHandler() {}

    private static final double BASE_MAX_HORIZ_SPEED = 1.70D; // vanilla-ish practical top speed
    private static final double MAX_HORIZ_SPEED_CAP = 5.00D;  // absolute cap to prevent insanity
    private static final double ACCEL_PER_TICK_PER_EXTRA_MULT = 0.020D; // added forward accel per (mult-1)
    private static final double STEER_LERP_PER_EXTRA_MULT = 0.15D; // steering blend per (mult-1)
    private static final double MAX_STEER_LERP = 0.65D; // don’t fully snap velocity to look

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player == null) return;

        if (!player.isFallFlying()) return;

        double speedMult = player.getAttributeValue(ModAttributes.ELYTRA_SPEED);
        double handlingMult = player.getAttributeValue(ModAttributes.ELYTRA_HANDLING);

        if (!Double.isFinite(speedMult)) speedMult = 1.0D;
        if (!Double.isFinite(handlingMult)) handlingMult = 1.0D;

        if (speedMult <= 1.0D && handlingMult <= 1.0D) return;

        Vec3 vel = player.getDeltaMovement();
        Vec3 look = player.getLookAngle();

        if (speedMult > 1.0D) {
            double extraMult = (speedMult - 1.0D);
            double accel = ACCEL_PER_TICK_PER_EXTRA_MULT * extraMult;

            vel = vel.add(look.x * accel, 0.0D, look.z * accel);
        }

        if (handlingMult > 1.0D) {
            double extraMult = (handlingMult - 1.0D);
            double steer = STEER_LERP_PER_EXTRA_MULT * extraMult;
            steer = Mth.clamp(steer, 0.0D, MAX_STEER_LERP);

            Vec3 horiz = new Vec3(vel.x, 0.0D, vel.z);
            double hLen = horiz.length();

            Vec3 lookHoriz = new Vec3(look.x, 0.0D, look.z);
            if (hLen > 1.0E-6D && lookHoriz.lengthSqr() > 1.0E-12D) {
                Vec3 desiredHoriz = lookHoriz.normalize().scale(hLen);
                Vec3 steered = horiz.lerp(desiredHoriz, steer);
                vel = new Vec3(steered.x, vel.y, steered.z);
            }
        }

        double max = BASE_MAX_HORIZ_SPEED * Math.max(1.0D, speedMult);
        max = Mth.clamp(max, 0.05D, MAX_HORIZ_SPEED_CAP);

        Vec3 horiz2 = new Vec3(vel.x, 0.0D, vel.z);
        double h2 = horiz2.length();
        if (h2 > max) {
            double scale = max / h2;
            vel = new Vec3(vel.x * scale, vel.y, vel.z * scale);
        }

        player.setDeltaMovement(vel);
    }
}
