package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class AerostasisGyrobladderEffect extends MobEffect {
    static final String NBT_ACTIVE = "cc_gyro_active";
    static final String NBT_OLD_FLY_SPEED = "cc_gyro_oldFlySpeedBits";
    static final String NBT_OLD_MAYFLY = "cc_gyro_oldMayfly";
    static final String NBT_JUMP_HELD = "cc_gyro_jumpHeld";

    private static final float GYRO_FLY_SPEED = 0.025F;
    private static final double FLOAT_UP_Y = 0.04D;
    private static final double START_FLOAT_Y = 0.04D;

    public AerostasisGyrobladderEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;
        if (!(entity instanceof Player player)) return true;
        if (player.isCreative() || player.isSpectator()) return true;

        int air = AerostasisGyrobladderAirHandler.getO2(player);
        boolean jumpHeld = isJumpHeldServer(player);

        if (air <= 0) {
            if (isGyroActive(player)) stopGyroFlight(player);
            CyberwareAttributeHelper.removeModifier(player, "gyrobladder_speed");
            return true;
        }

        if (jumpHeld) {
            ensureGyroFlightConfigured(player);
            setFlying(player, true);

            Vec3 v = player.getDeltaMovement();
            player.setDeltaMovement(v.x, Math.max(v.y, FLOAT_UP_Y), v.z);
            player.fallDistance = 0.0F;

            CyberwareAttributeHelper.applyModifier(player, "gyrobladder_speed");
        } else {
            if (isGyroActive(player)) stopGyroFlight(player);
            CyberwareAttributeHelper.removeModifier(player, "gyrobladder_speed");
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    static boolean isJumpHeldServer(Player player) {
        return player.getPersistentData().getBoolean(NBT_JUMP_HELD);
    }

    static void setJumpHeldServer(Player player, boolean held) {
        player.getPersistentData().putBoolean(NBT_JUMP_HELD, held);
    }

    static boolean isGyroActive(Player player) {
        return player.getPersistentData().getBoolean(NBT_ACTIVE);
    }

    static void ensureGyroFlightConfigured(Player player) {
        CompoundTag tag = player.getPersistentData();

        if (!tag.getBoolean(NBT_ACTIVE)) {
            tag.putInt(NBT_OLD_FLY_SPEED, Float.floatToIntBits(player.getAbilities().getFlyingSpeed()));
            tag.putBoolean(NBT_OLD_MAYFLY, player.getAbilities().mayfly);
            tag.putBoolean(NBT_ACTIVE, true);
        }

        player.getAbilities().mayfly = true;
        player.getAbilities().setFlyingSpeed(GYRO_FLY_SPEED);
        player.onUpdateAbilities();
    }

    static void stopGyroFlight(Player player) {
        CompoundTag tag = player.getPersistentData();

        player.getAbilities().flying = false;

        boolean oldMayfly = tag.getBoolean(NBT_OLD_MAYFLY);
        player.getAbilities().mayfly = oldMayfly;

        if (tag.getBoolean(NBT_ACTIVE)) {
            float oldSpeed = Float.intBitsToFloat(tag.getInt(NBT_OLD_FLY_SPEED));
            player.getAbilities().setFlyingSpeed(oldSpeed);
        }

        tag.remove(NBT_ACTIVE);
        tag.remove(NBT_OLD_FLY_SPEED);
        tag.remove(NBT_OLD_MAYFLY);

        player.onUpdateAbilities();
    }

    static void setFlying(Player player, boolean flying) {
        if (player.getAbilities().flying == flying) return;
        player.getAbilities().flying = flying;
        player.onUpdateAbilities();
    }

    private static boolean hasThisEffect(Player player) {
        return player.hasEffect(ModEffects.AEROSTASIS_GYROBLADDER_EFFECT);
    }

    public record GyroJumpHeldPayload(boolean held) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<GyroJumpHeldPayload> TYPE =
                new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "gyro_jump_held"));

        public static final StreamCodec<RegistryFriendlyByteBuf, GyroJumpHeldPayload> STREAM_CODEC =
                StreamCodec.composite(ByteBufCodecs.BOOL, GyroJumpHeldPayload::held, GyroJumpHeldPayload::new);

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void handleJumpHeldPayload(net.minecraft.server.level.ServerPlayer player, boolean held) {
        if (player == null) return;

        if (player.hasEffect(ModEffects.AEROSTASIS_GYROBLADDER_EFFECT)) {
            setJumpHeldServer(player, held);
        } else {
            setJumpHeldServer(player, false);
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static final class ClientInput {
        private static boolean lastSent = false;
        private static boolean lastHasEffect = false;

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            boolean hasEffect = hasThisEffect(mc.player);
            boolean heldNow = hasEffect && mc.options.keyJump.isDown();

            if (hasEffect && !lastHasEffect) {
                PacketDistributor.sendToServer(new GyroJumpHeldPayload(heldNow));
                lastSent = heldNow;
            } else if (heldNow != lastSent) {
                PacketDistributor.sendToServer(new GyroJumpHeldPayload(heldNow));
                lastSent = heldNow;
            }

            if (!hasEffect) {
                if (lastSent) {
                    PacketDistributor.sendToServer(new GyroJumpHeldPayload(false));
                }
                lastSent = false;
            }

            lastHasEffect = hasEffect;
        }

        private ClientInput() {}
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {

        @SubscribeEvent
        public static void onEffectAdded(MobEffectEvent.Added event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;

            if (event.getEffectInstance() == null) return;
            if (event.getEffectInstance().getEffect() != ModEffects.AEROSTASIS_GYROBLADDER_EFFECT.value()) return;

            setJumpHeldServer(player, false);

            if (isGyroActive(player)) stopGyroFlight(player);
        }

        @SubscribeEvent
        public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;

            if (player.isCreative() || player.isSpectator()) return;
            if (!hasThisEffect(player)) return;

            if (AerostasisGyrobladderAirHandler.getO2(player) <= 0) return;

            setJumpHeldServer(player, true);

            ensureGyroFlightConfigured(player);
            setFlying(player, true);

            Vec3 v = player.getDeltaMovement();
            player.setDeltaMovement(v.x, START_FLOAT_Y, v.z);
            player.fallDistance = 0.0F;
        }

        @SubscribeEvent
        public static void onEffectRemoved(MobEffectEvent.Remove event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;

            if (event.getEffectInstance() == null) return;
            if (event.getEffectInstance().getEffect() != ModEffects.AEROSTASIS_GYROBLADDER_EFFECT.value()) return;

            setJumpHeldServer(player, false);
            if (isGyroActive(player)) stopGyroFlight(player);
            CyberwareAttributeHelper.removeModifier(player, "gyrobladder_speed");
        }

        @SubscribeEvent
        public static void onEffectExpired(MobEffectEvent.Expired event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;

            if (event.getEffectInstance() == null) return;
            if (event.getEffectInstance().getEffect() != ModEffects.AEROSTASIS_GYROBLADDER_EFFECT.value()) return;

            setJumpHeldServer(player, false);
            if (isGyroActive(player)) stopGyroFlight(player);
            CyberwareAttributeHelper.removeModifier(player, "gyrobladder_speed");
        }

        private Events() {}
    }
}