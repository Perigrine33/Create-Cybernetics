package com.perigrine3.createcybernetics.effect;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.entity.ModEntities;
import com.perigrine3.createcybernetics.entity.custom.GuardianBeamEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.joml.Vector3f;

import java.util.List;

public class GuardianEyeEffect extends MobEffect {
    private static final int CHARGE_TICKS = 40;
    private static final int COOLDOWN_TICKS = 60;
    private static final double RANGE = 15.0D;
    private static final float DAMAGE = 12.0F;

    private static final double BEAM_HALF_WIDTH = 0.75D;
    private static final String KEY_USE_HELD = "cc_guardianEye_useHeld";
    private static final String KEY_CHARGE = "cc_guardianEye_charge";
    private static final String KEY_END_X = "cc_guardianEye_endX";
    private static final String KEY_END_Y = "cc_guardianEye_endY";
    private static final String KEY_END_Z = "cc_guardianEye_endZ";
    private static final String KEY_LAST_FIRE_TICK = "cc_guardianEye_lastFireTick";

    private static final String KEY_PREVIEW_BEAM_ID = "cc_guardianEye_previewBeamId";

    private static final String KEY_RING_TICKS = "cc_guardianEye_ringTicks";
    private static final String KEY_RING_TOTAL = "cc_guardianEye_ringTotal";
    private static final String KEY_RING_SX = "cc_guardianEye_ringSx";
    private static final String KEY_RING_SY = "cc_guardianEye_ringSy";
    private static final String KEY_RING_SZ = "cc_guardianEye_ringSz";
    private static final String KEY_RING_EX = "cc_guardianEye_ringEx";
    private static final String KEY_RING_EY = "cc_guardianEye_ringEy";
    private static final String KEY_RING_EZ = "cc_guardianEye_ringEz";

    public GuardianEyeEffect(MobEffectCategory category, int color) {
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

        CompoundTag ptag = player.getPersistentData();
        long now = level.getGameTime();

        tickTravelRing(level, ptag);

        long lastFire = ptag.getLong(KEY_LAST_FIRE_TICK);
        boolean onCooldown = (now - lastFire) < COOLDOWN_TICKS;
        if (onCooldown) {
            ptag.putInt(KEY_CHARGE, 0);
            cleanupPreviewBeam(level, ptag);
            return true;
        }

        boolean useHeld = ptag.getBoolean(KEY_USE_HELD) || player.isUsingItem();
        boolean crouching = player.isCrouching();

        if (!useHeld || !crouching) {
            ptag.putInt(KEY_CHARGE, 0);
            cleanupPreviewBeam(level, ptag);
            return true;
        }

        int prevCharge = ptag.getInt(KEY_CHARGE);
        int charge = Mth.clamp(prevCharge + 1, 0, CHARGE_TICKS);
        ptag.putInt(KEY_CHARGE, charge);

        Vec3 start = computeMuzzle(player);
        Vec3 end = computeBeamEnd(level, player, start);

        ptag.putFloat(KEY_END_X, (float) end.x);
        ptag.putFloat(KEY_END_Y, (float) end.y);
        ptag.putFloat(KEY_END_Z, (float) end.z);

        GuardianBeamEntity preview = getOrCreatePreviewBeam(level, player, ptag, start, end);
        preview.setLife(2);
        preview.setStart(start);
        preview.setEnd(end);
        preview.moveTo(start.x, start.y, start.z, player.getYRot(), player.getXRot());

        if (charge == 1) {
            playChargeSound(level, player);
        }

        if (charge < CHARGE_TICKS) return true;

        ptag.putInt(KEY_CHARGE, 0);
        ptag.putLong(KEY_LAST_FIRE_TICK, now);

        LivingEntity hit = hitClosestLivingInBeam(level, player, start, end);
        if (hit != null) {
            end = hit.getEyePosition(1.0F);
            hit.hurt(level.damageSources().magic(), DAMAGE);
        }

        initTravelRing(ptag, start, end);

        spawnBeam(level, player, start, end, 10);
        spawnThickenerBeam(level, player, start, end, 10);

        playFireSound(level, player);

        ptag.putInt(KEY_PREVIEW_BEAM_ID, 0);

        return true;
    }

    private static Vec3 computeBeamEnd(ServerLevel level, ServerPlayer player, Vec3 start) {
        Vec3 look = player.getViewVector(1.0F);
        Vec3 desiredEnd = start.add(look.scale(RANGE));

        BlockHitResult hit = level.clip(new ClipContext(start, desiredEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

        return hit.getType() == HitResult.Type.MISS ? desiredEnd : hit.getLocation();
    }

    private static LivingEntity hitClosestLivingInBeam(ServerLevel level, ServerPlayer player, Vec3 start, Vec3 end) {
        Vec3 delta = end.subtract(start);
        double len = delta.length();
        if (len < 0.001D) return null;

        Vec3 dir = delta.scale(1.0D / len);

        AABB box = new AABB(start, end).inflate(BEAM_HALF_WIDTH);
        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, box, e -> e.isAlive() && e != player);

        LivingEntity best = null;
        double bestProj = Double.MAX_VALUE;

        for (LivingEntity e : candidates) {
            Vec3 eye = e.getEyePosition(1.0F);
            Vec3 to = eye.subtract(start);

            double proj = to.dot(dir);
            if (proj < 0.0D || proj > len) continue;

            Vec3 closest = start.add(dir.scale(proj));
            double dist2 = eye.distanceToSqr(closest);
            if (dist2 > (BEAM_HALF_WIDTH * BEAM_HALF_WIDTH)) continue;

            if (proj < bestProj) {
                bestProj = proj;
                best = e;
            }
        }

        return best;
    }

    private static void spawnBeam(ServerLevel level, ServerPlayer player, Vec3 start, Vec3 end, int life) {
        GuardianBeamEntity beam = new GuardianBeamEntity(ModEntities.GUARDIAN_BEAM.get(), level);
        beam.setShooterId(player.getId());
        beam.setStart(start);
        beam.setEnd(end);
        beam.setLife(life);
        beam.moveTo(start.x, start.y, start.z, player.getYRot(), player.getXRot());
        level.addFreshEntity(beam);
    }

    private static void spawnThickenerBeam(ServerLevel level, ServerPlayer player, Vec3 start, Vec3 end, int life) {
        Vec3 delta = end.subtract(start);
        double len = delta.length();
        if (len < 0.001D) return;

        Vec3 dir = delta.scale(1.0D / len);
        Vec3 basis = Math.abs(dir.y) > 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 right = dir.cross(basis).normalize();

        double off = 0.04D;

        Vec3 s2 = start.add(right.scale(off));
        Vec3 e2 = end.add(right.scale(off));

        GuardianBeamEntity beam = new GuardianBeamEntity(ModEntities.GUARDIAN_BEAM.get(), level);
        beam.setShooterId(player.getId());
        beam.setStart(s2);
        beam.setEnd(e2);
        beam.setLife(life);
        beam.moveTo(s2.x, s2.y, s2.z, player.getYRot(), player.getXRot());
        level.addFreshEntity(beam);
    }

    private static GuardianBeamEntity getOrCreatePreviewBeam(ServerLevel level, ServerPlayer player, CompoundTag ptag, Vec3 start, Vec3 end) {
        int id = ptag.getInt(KEY_PREVIEW_BEAM_ID);
        Entity existing = id != 0 ? level.getEntity(id) : null;

        if (existing instanceof GuardianBeamEntity beam && beam.isAlive()) {
            return beam;
        }

        GuardianBeamEntity beam = new GuardianBeamEntity(ModEntities.GUARDIAN_BEAM.get(), level);
        beam.setShooterId(player.getId());
        beam.setLife(2);
        beam.setStart(start);
        beam.setEnd(end);
        beam.moveTo(start.x, start.y, start.z, player.getYRot(), player.getXRot());
        level.addFreshEntity(beam);

        ptag.putInt(KEY_PREVIEW_BEAM_ID, beam.getId());
        return beam;
    }

    private static void cleanupPreviewBeam(ServerLevel level, CompoundTag ptag) {
        int id = ptag.getInt(KEY_PREVIEW_BEAM_ID);
        if (id == 0) return;

        Entity e = level.getEntity(id);
        if (e instanceof GuardianBeamEntity beam) {
            beam.discard();
        }
        ptag.putInt(KEY_PREVIEW_BEAM_ID, 0);
    }

    private static void initTravelRing(CompoundTag ptag, Vec3 start, Vec3 end) {
        int travel = 10;

        ptag.putInt(KEY_RING_TICKS, travel);
        ptag.putInt(KEY_RING_TOTAL, travel);

        ptag.putFloat(KEY_RING_SX, (float) start.x);
        ptag.putFloat(KEY_RING_SY, (float) start.y);
        ptag.putFloat(KEY_RING_SZ, (float) start.z);

        ptag.putFloat(KEY_RING_EX, (float) end.x);
        ptag.putFloat(KEY_RING_EY, (float) end.y);
        ptag.putFloat(KEY_RING_EZ, (float) end.z);
    }

    private static void tickTravelRing(ServerLevel level, CompoundTag ptag) {
        int ticks = ptag.getInt(KEY_RING_TICKS);
        if (ticks <= 0) return;

        int total = Math.max(1, ptag.getInt(KEY_RING_TOTAL));

        Vec3 start = new Vec3(ptag.getFloat(KEY_RING_SX), ptag.getFloat(KEY_RING_SY), ptag.getFloat(KEY_RING_SZ));
        Vec3 end = new Vec3(ptag.getFloat(KEY_RING_EX), ptag.getFloat(KEY_RING_EY), ptag.getFloat(KEY_RING_EZ));

        double progress = 1.0D - (ticks / (double) total);
        progress = Mth.clamp(progress, 0.0D, 1.0D);

        Vec3 delta = end.subtract(start);
        Vec3 center = start.add(delta.scale(progress));

        Vec3 dir = delta.lengthSqr() < 0.000001D ? new Vec3(0, 0, 1) : delta.normalize();

        spawnRing(level, center, dir);

        ptag.putInt(KEY_RING_TICKS, ticks - 1);
    }

    private static void spawnRing(ServerLevel level, Vec3 center, Vec3 dir) {
        Vec3 basis = Math.abs(dir.y) > 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 right = dir.cross(basis).normalize();
        Vec3 up = dir.cross(right).normalize();

        double radius = 0.14D;
        int ringPts = 8;

        DustParticleOptions dust = new DustParticleOptions(new org.joml.Vector3f(0.42F, 0.80F, 0.24F), 0.75F);

        for (int k = 0; k < ringPts; k++) {
            double ang = (Math.PI * 2.0D) * (k / (double) ringPts);
            Vec3 off = right.scale(Math.cos(ang) * radius).add(up.scale(Math.sin(ang) * radius));
            Vec3 p = center.add(off);

            level.sendParticles(dust, p.x, p.y, p.z, 1, 0, 0, 0, 0);
        }
    }

    private static void playChargeSound(ServerLevel level, ServerPlayer player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GUARDIAN_AMBIENT, SoundSource.PLAYERS, 5, 1);
    }

    private static void playFireSound(ServerLevel level, ServerPlayer player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GUARDIAN_ATTACK, SoundSource.PLAYERS, 1.5F, 1.0F);
    }

    private static boolean hasGuardianEyeEffect(Player player) {
        for (var inst : player.getActiveEffects()) {
            if (inst.getEffect().value() instanceof GuardianEyeEffect) return true;
        }
        return false;
    }

    private static Vec3 computeMuzzle(ServerPlayer player) {
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);
        return eye.add(look.scale(0.35D)).subtract(0.0D, 0.18D, 0.0D);
    }


    public static void setUseHeld(ServerPlayer player, boolean held) {
        if (!hasGuardianEyeEffect(player)) return;
        player.getPersistentData().putBoolean(KEY_USE_HELD, held);
    }

    public record GuardianEyeUseHeldPayload(boolean held) implements CustomPacketPayload {
        public static final Type<GuardianEyeUseHeldPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "guardian_eye_use_held"));

        public static final StreamCodec<RegistryFriendlyByteBuf, GuardianEyeUseHeldPayload> STREAM_CODEC =
                StreamCodec.composite(ByteBufCodecs.BOOL, GuardianEyeUseHeldPayload::held, GuardianEyeUseHeldPayload::new);

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

            r.playToServer(GuardianEyeUseHeldPayload.TYPE, GuardianEyeUseHeldPayload.STREAM_CODEC, (payload, ctx) -> {
                if (ctx.player() instanceof ServerPlayer sp) {
                    setUseHeld(sp, payload.held());
                }
            });
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static final class ClientInput {
        private static boolean lastUseHeld = false;

        private ClientInput() {}

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null) return;

            if (!hasGuardianEyeEffect(player)) {
                if (lastUseHeld) {
                    lastUseHeld = false;
                    PacketDistributor.sendToServer(new GuardianEyeUseHeldPayload(false));
                }
                return;
            }

            boolean useHeld = mc.options.keyUse.isDown();
            if (useHeld != lastUseHeld) {
                lastUseHeld = useHeld;
                PacketDistributor.sendToServer(new GuardianEyeUseHeldPayload(useHeld));
            }
        }
    }
}
