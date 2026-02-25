package com.perigrine3.createcybernetics.client.sonar;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundSourceEvent;
import net.neoforged.neoforge.client.event.sound.PlayStreamingSourceEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.List;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT)
public final class SonarClientEvents {
    private static final ResourceLocation SONAR_POST =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "shaders/post/sonar.json");

    private static Field postChainField;

    // ---- tuning constants ----
    private static final float RADIUS_BLOCKS = 10.0f;     // capture radius AND shader radius concept
    private static final float LIFE_SECONDS  = 10.0f;     // how long pings exist
    private static final float WAVE_SPEED    = 2.0f;      // blocks/sec
    private static final float WAVE_THICK    = 2.5f;      // blocks
    private static final float WAVE_FEATHER  = 1.5f;      // blocks
    private static final float WAVE_MAX_R    = 60.0f;     // blocks
    private static final float SKY_EPS       = 0.0005f;   // you can keep this but shader won’t early-return now

    private SonarClientEvents() {}

    // ---- Sound capture (ANY sound) ----
    @SubscribeEvent
    public static void onPlaySoundEvent(PlaySoundEvent event) {
        if (event.getSound() == null) return;
        pushSound(event.getSound().getX(), event.getSound().getY(), event.getSound().getZ(), event.getSound().isRelative());
    }

    @SubscribeEvent
    public static void onPlaySoundSourceEvent(PlaySoundSourceEvent event) {
        if (event.getSound() == null) return;
        pushSound(event.getSound().getX(), event.getSound().getY(), event.getSound().getZ(), event.getSound().isRelative());
    }

    @SubscribeEvent
    public static void onPlayStreamingSourceEvent(PlayStreamingSourceEvent event) {
        if (event.getSound() == null) return;
        pushSound(event.getSound().getX(), event.getSound().getY(), event.getSound().getZ(), event.getSound().isRelative());
    }

    /** Normalize world position for relative sounds + radius gate. */
    private static void pushSound(double x, double y, double z, boolean isRelative) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Vec3 pos = isRelative ? mc.player.position() : new Vec3(x, y, z);

        // radius gate (your requirement)
        if (pos.distanceTo(mc.player.position()) > RADIUS_BLOCKS) return;

        // also reject NaNs/infs
        if (!Double.isFinite(pos.x) || !Double.isFinite(pos.y) || !Double.isFinite(pos.z)) return;

        SonarPingManager.push(pos);
    }

    // ---- Tick: gate effect + set uniforms ----
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            mc.gameRenderer.shutdownEffect();
            SonarPingManager.clear();
            return;
        }

        PlayerCyberwareData data = mc.player.getData(ModAttachments.CYBERWARE);
        boolean active = data.hasSpecificItem(ModItems.WETWARE_WARDENANTLERS.get(), CyberwareSlot.EYES);

        if (!active) {
            mc.gameRenderer.shutdownEffect();
            SonarPingManager.clear();
            return;
        }

        // keep effect loaded
        if (getPostChain(mc.gameRenderer) == null) {
            mc.gameRenderer.loadEffect(SONAR_POST);
        }

        PostChain chain = getPostChain(mc.gameRenderer);
        if (chain == null) return;

        // prune dead pings every tick
        SonarPingManager.prune(LIFE_SECONDS);

        // common uniforms
        int w = mc.getWindow().getWidth();
        int h = mc.getWindow().getHeight();
        float aspect = (h == 0) ? 1.0f : (float) w / (float) h;

        double fovDegrees = mc.options.fov().get();
        float tanHalfFov = (float) Math.tan(Math.toRadians(fovDegrees * 0.5));

        chain.setUniform("Aspect", aspect);
        chain.setUniform("TanHalfFov", tanHalfFov);

        chain.setUniform("EdgeThreshold", 0.10f);
        chain.setUniform("EdgeBoost", 1.25f);

        chain.setUniform("SoundLifeSeconds", LIFE_SECONDS);

        chain.setUniform("WaveSpeed", WAVE_SPEED);
        chain.setUniform("WaveThickness", WAVE_THICK);
        chain.setUniform("WaveFeather", WAVE_FEATHER);
        chain.setUniform("WaveMaxRadius", WAVE_MAX_R);
        chain.setUniform("SkyDepthEps", SKY_EPS);

        // fill ping uniforms in VIEW SPACE
        Camera cam = mc.gameRenderer.getMainCamera();
        Vec3 camPos = cam.getPosition();

        // This is the correct mapping for your existing shader reconstruction:
        // view-space = rotate(worldDelta) by inverse camera rotation.
        Quaternionf invCamRot = new Quaternionf(cam.rotation()).conjugate();

        List<SonarPingManager.Ping> pings = SonarPingManager.snapshotNewestFirst();
        int count = Math.min(SonarPingManager.MAX_PINGS, pings.size());
        chain.setUniform("SoundCount", (float) count);

        long now = Util.getNanos();

        for (int i = 0; i < SonarPingManager.MAX_PINGS; i++) {
            float vx = 0, vy = 0, vz = 0;
            float ageSec = 999.0f;

            if (i < count) {
                SonarPingManager.Ping p = pings.get(i);

                Vec3 d = p.worldPos.subtract(camPos);
                Vector3f v = new Vector3f((float) d.x, (float) d.y, (float) d.z);
                invCamRot.transform(v);

                // sanity clamp: if transform explodes, ignore
                if (v.length() > 256.0f) {
                    ageSec = 999.0f;
                } else {
                    vx = v.x;
                    vy = v.y;
                    vz = v.z;
                    ageSec = (now - p.timeNanos) / 1_000_000_000.0f;
                }
            }

            chain.setUniform("SoundPosX" + i, vx);
            chain.setUniform("SoundPosY" + i, vy);
            chain.setUniform("SoundPosZ" + i, vz);
            chain.setUniform("SoundAge" + i, ageSec);
        }
    }

    private static PostChain getPostChain(GameRenderer renderer) {
        try {
            if (postChainField == null) postChainField = findPostChainField(renderer);
            if (postChainField == null) return null;
            Object val = postChainField.get(renderer);
            return (val instanceof PostChain pc) ? pc : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static Field findPostChainField(GameRenderer renderer) {
        Class<?> c = renderer.getClass();
        while (c != null && c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                if (PostChain.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    return f;
                }
            }
            c = c.getSuperclass();
        }
        return null;
    }
}