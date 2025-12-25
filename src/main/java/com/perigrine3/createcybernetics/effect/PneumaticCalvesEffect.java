package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

public class PneumaticCalvesEffect extends MobEffect {

    private static final String NBT_LAST_USE_TICK = "cc_calves_lastSprintJumpTick";
    private static final int COOLDOWN_TICKS = 5;
    private static final double FORWARD_BOOST = 0.9;
    private static final double VERTICAL_BOOST = 0.35;
    private static final double WIND_BEHIND_DIST = 0.8;
    private static final double WIND_Y_OFFSET = 0.2;

    private static final String NBT_LAST_CROUCH_JUMP_TICK = "cc_calves_lastCrouchJumpTick";
    private static final int CROUCH_COOLDOWN_TICKS = 8;
    private static final double CROUCH_VERTICAL_BOOST = 1;
    private static final double CROUCH_UPWARD_ONLY_DAMPEN = 0.0;


    public PneumaticCalvesEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return false;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        return true;
    }

    private static boolean hasThisEffect(Player player) {
        return player.hasEffect(ModEffects.PNEUMATIC_CALVES_EFFECT);
    }

    private static void resetState(Player player) {
        player.getPersistentData().remove(NBT_LAST_USE_TICK);
    }

    private static void clearState(Player player) {
        player.getPersistentData().remove(NBT_LAST_USE_TICK);
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {

        @SubscribeEvent
        public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;
            if (!hasThisEffect(player)) return;

            if (player.level().isClientSide) return;
            if (!hasThisEffect(player)) return;

            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            CompoundTag tag = player.getPersistentData();
            long now = player.level().getGameTime();

            //CROUCH JUMP
            if (player.isCrouching()) {
                long last = tag.getLong(NBT_LAST_CROUCH_JUMP_TICK);
                if (now - last >= CROUCH_COOLDOWN_TICKS) {
                    tag.putLong(NBT_LAST_CROUCH_JUMP_TICK, now);

                    if (!data.hasSpecificItem(ModItems.LEGUPGRADES_OCELOTPAWS.get(), CyberwareSlot.RLEG)
                            && !data.hasSpecificItem(ModItems.LEGUPGRADES_OCELOTPAWS.get(), CyberwareSlot.LLEG)) {
                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.PISTON_EXTEND, SoundSource.PLAYERS, 0.40F, 1.50F);
                    }

                    if (player.level() instanceof ServerLevel sl) {
                        sl.sendParticles(ParticleTypes.CLOUD,
                                player.getX(), player.getY() + 0.05, player.getZ(),
                                10, 0.20, 0.05, 0.20, 0.02);
                    }

                    doCrouchJumpBoost(player);
                    return;
                }
            }

            //SPRINT JUMP
            if (!player.isSprinting()) return;

            if (player.getVehicle() != null) return;
            if (player.isInWaterOrBubble()) return;
            if (player.onClimbable()) return;

            long last = tag.getLong(NBT_LAST_USE_TICK);
            if (now - last < COOLDOWN_TICKS) return;
            tag.putLong(NBT_LAST_USE_TICK, now);

            if (!data.hasSpecificItem(ModItems.LEGUPGRADES_OCELOTPAWS.get(), CyberwareSlot.RLEG)
                    && !data.hasSpecificItem(ModItems.LEGUPGRADES_OCELOTPAWS.get(), CyberwareSlot.LLEG)) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PISTON_EXTEND, SoundSource.PLAYERS, 0.25F, 1.35F);
            }

            Vec3 look = player.getLookAngle();
            Vec3 forward = new Vec3(look.x, 0.0, look.z);
            if (forward.lengthSqr() < 1.0E-6) {
                forward = Vec3.directionFromRotation(0.0F, player.getYRot());
            } else {
                forward = forward.normalize();
            }

            Vec3 behind = forward.scale(-1.0);
            Vec3 windPos = player.position().add(behind.scale(WIND_BEHIND_DIST)).add(0.0, WIND_Y_OFFSET, 0.0);

            if (player.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.CLOUD, windPos.x, windPos.y, windPos.z,
                        8, 0.18, 0.10, 0.18, 0.02);
            }

            Vec3 impulse = new Vec3(forward.x * FORWARD_BOOST, VERTICAL_BOOST, forward.z * FORWARD_BOOST);
            player.setDeltaMovement(player.getDeltaMovement().add(impulse));

            player.hasImpulse = true;
            if (player instanceof ServerPlayer sp) {
                sp.hurtMarked = true;
            }

        }

        @SubscribeEvent
        public static void onEffectAdded(MobEffectEvent.Added event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;
            if (event.getEffectInstance() == null) return;
            if (event.getEffectInstance().getEffect() != ModEffects.PNEUMATIC_CALVES_EFFECT.value()) return;
            resetState(player);
        }

        @SubscribeEvent
        public static void onEffectRemoved(MobEffectEvent.Remove event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;
            if (event.getEffectInstance() == null) return;
            if (event.getEffectInstance().getEffect() != ModEffects.PNEUMATIC_CALVES_EFFECT.value()) return;
            clearState(player);
        }

        @SubscribeEvent
        public static void onEffectExpired(MobEffectEvent.Expired event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;
            if (event.getEffectInstance() == null) return;
            if (event.getEffectInstance().getEffect() != ModEffects.PNEUMATIC_CALVES_EFFECT.value()) return;
            clearState(player);
        }

        private static void doCrouchJumpBoost(Player player) {
            Vec3 impulse = new Vec3(0.0, CROUCH_VERTICAL_BOOST, 0.0);

            if (CROUCH_UPWARD_ONLY_DAMPEN > 0.0) {
                Vec3 look = player.getLookAngle();
                Vec3 forward = new Vec3(look.x, 0.0, look.z);
                if (forward.lengthSqr() >= 1.0E-6) {
                    forward = forward.normalize();
                    impulse = impulse.add(forward.x * CROUCH_UPWARD_ONLY_DAMPEN, 0.0, forward.z * CROUCH_UPWARD_ONLY_DAMPEN);
                }
            }

            player.setDeltaMovement(player.getDeltaMovement().add(impulse));
            player.hasImpulse = true;

            if (player instanceof ServerPlayer sp) {
                sp.hurtMarked = true;
            }

        }

        private Events() {}
    }
}
