package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

public class SculkLungsEffect extends MobEffect {

    private static final int CHARGE_TICKS = 34;
    private static final int COOLDOWN_AFTER_FIRE_TICKS = 300;
    private static final double RANGE = 20.0D;
    private static final float DAMAGE = 10.0F;
    private static final double KNOCKBACK_HORIZONTAL = 2.5D;
    private static final double KNOCKBACK_VERTICAL = 0.5D;
    private static final double KNOCKBACK_Y_CAP = 0.5D;
    private static final String KEY_USE_HELD = "cc_sonic_useHeld";
    private static final String KEY_CHARGE = "cc_sonic_charge";
    private static final String KEY_LAST_FIRE_TICK = "cc_sonic_lastFireTick";

    public SculkLungsEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        if (!(living instanceof ServerPlayer player)) return true;
        if (!(player.level() instanceof ServerLevel level)) return true;

        CompoundTag tag = player.getPersistentData();
        long now = level.getGameTime();

        boolean useHeld = tag.getBoolean(KEY_USE_HELD);
        boolean crouching = player.isCrouching();

        long lastFire = tag.getLong(KEY_LAST_FIRE_TICK);
        boolean onCooldown = (now - lastFire) < COOLDOWN_AFTER_FIRE_TICKS;

        if (!useHeld || !crouching || onCooldown) {
            tag.putInt(KEY_CHARGE, 0);
            return true;
        }

        int charge = tag.getInt(KEY_CHARGE) + 1;
        if (charge == 1) {
            playChargeSound(level, player);
        }

        if (charge < CHARGE_TICKS) {
            tag.putInt(KEY_CHARGE, charge);
            return true;
        }

        tag.putInt(KEY_CHARGE, 0);

        Vec3 start = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = start.add(look.scale(RANGE));

        hitEntitiesInBeam(level, player, start, end);

        spawnSonicParticles(level, start, end);
        playBoomSound(level, player);

        tag.putLong(KEY_LAST_FIRE_TICK, now);

        return true;
    }

    private static void hitEntitiesInBeam(ServerLevel level, ServerPlayer player, Vec3 start, Vec3 end) {
        Vec3 delta = end.subtract(start);
        double len = delta.length();
        if (len < 0.001D) return;

        Vec3 dir = delta.scale(1.0D / len);

        double halfWidth = 1.25D;
        AABB beamBox = new AABB(start, end).inflate(halfWidth);

        List<LivingEntity> hits = level.getEntitiesOfClass(
                LivingEntity.class,
                beamBox,
                e -> e.isAlive() && e != player
        );

        for (LivingEntity target : hits) {
            Vec3 toTarget = target.getEyePosition(1.0F).subtract(start);
            double proj = toTarget.dot(dir);
            if (proj < 0.0D || proj > len) continue;

            Vec3 closest = start.add(dir.scale(proj));
            double dist2 = target.getEyePosition(1.0F).distanceToSqr(closest);
            if (dist2 > (halfWidth * halfWidth)) continue;

            target.hurt(level.damageSources().sonicBoom(player), DAMAGE);

            Vec3 knockDir = target.position().subtract(player.position()).normalize();
            double ky = Mth.clamp(KNOCKBACK_VERTICAL, -KNOCKBACK_Y_CAP, KNOCKBACK_Y_CAP);
            target.push(knockDir.x * KNOCKBACK_HORIZONTAL, ky, knockDir.z * KNOCKBACK_HORIZONTAL);
            target.hurtMarked = true;
        }
    }

    private static void spawnSonicParticles(ServerLevel level, Vec3 start, Vec3 end) {
        Vec3 delta = end.subtract(start);
        double len = delta.length();
        if (len < 0.001D) return;

        Vec3 dir = delta.scale(1.0D / len);

        int steps = Mth.clamp((int) (len * 8.0D), 8, 256);
        double step = len / steps;

        for (int i = 0; i <= steps; i++) {
            Vec3 p = start.add(dir.scale(i * step));
            level.sendParticles(ParticleTypes.SONIC_BOOM, p.x, p.y, p.z, 0,
                    dir.x * 0.35D, dir.y * 0.35D, dir.z * 0.35D, 1.0D);
        }
    }

    private static void playChargeSound(ServerLevel level, ServerPlayer player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                net.minecraft.sounds.SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.PLAYERS, 1.0F, 1.25F);
    }

    private static void playBoomSound(ServerLevel level, ServerPlayer player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                net.minecraft.sounds.SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0F, 1.25F);
    }

    private static boolean hasThisEffect(Level level, ServerPlayer player) {
        for (var inst : player.getActiveEffects()) {
            if (inst.getEffect().value() instanceof SculkLungsEffect) return true;
        }
        return false;
    }

    public static void setUseHeld(ServerPlayer player, boolean held) {
        if (!hasThisEffect(player.level(), player)) return;
        player.getPersistentData().putBoolean(KEY_USE_HELD, held);
    }

    public record SonicUseHeldPayload(boolean held) implements CustomPacketPayload {
        public static final Type<SonicUseHeldPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "sonic_use_held"));

        public static final StreamCodec<RegistryFriendlyByteBuf, SonicUseHeldPayload> STREAM_CODEC =
                StreamCodec.composite(ByteBufCodecs.BOOL, SonicUseHeldPayload::held, SonicUseHeldPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static final class Payloads {
        private Payloads() {}

        @SubscribeEvent
        public static void registerPayloads(RegisterPayloadHandlersEvent event) {
            PayloadRegistrar r = event.registrar(CreateCybernetics.MODID);

            r.playToServer(SonicUseHeldPayload.TYPE, SonicUseHeldPayload.STREAM_CODEC, (payload, ctx) -> {
                if (ctx.player() instanceof ServerPlayer sp) {
                    setUseHeld(sp, payload.held());
                }
            });
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static final class ClientInput {
        private static boolean lastSentHeld = false;

        private ClientInput() {}

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            net.minecraft.world.entity.player.Player player = mc.player;
            if (player == null) return;

            boolean hasEffect = false;
            for (var inst : player.getActiveEffects()) {
                if (inst.getEffect().value() instanceof SculkLungsEffect) {
                    hasEffect = true;
                    break;
                }
            }

            if (!hasEffect) {
                if (lastSentHeld) {
                    lastSentHeld = false;
                    PacketDistributor.sendToServer(new SonicUseHeldPayload(false));
                }
                return;
            }

            boolean held = mc.options.keyUse.isDown();
            if (held != lastSentHeld) {
                lastSentHeld = held;
                PacketDistributor.sendToServer(new SonicUseHeldPayload(held));
            }
        }
    }
}
