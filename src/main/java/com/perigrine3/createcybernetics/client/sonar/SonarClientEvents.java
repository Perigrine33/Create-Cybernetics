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

    private SonarClientEvents() {}

    @SubscribeEvent
    public static void onPlaySoundEvent(PlaySoundEvent event) {
        if (event.getSound() == null) return;
        SonarPingManager.push(new Vec3(event.getSound().getX(), event.getSound().getY(), event.getSound().getZ()));
    }

    @SubscribeEvent
    public static void onPlaySoundSourceEvent(PlaySoundSourceEvent event) {
        if (event.getSound() == null) return;
        SonarPingManager.push(new Vec3(event.getSound().getX(), event.getSound().getY(), event.getSound().getZ()));
    }

    @SubscribeEvent
    public static void onPlayStreamingSourceEvent(PlayStreamingSourceEvent event) {
        if (event.getSound() == null) return;
        SonarPingManager.push(new Vec3(event.getSound().getX(), event.getSound().getY(), event.getSound().getZ()));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            mc.gameRenderer.shutdownEffect();
            SonarPingManager.clear();
            return;
        }

        PlayerCyberwareData data = mc.player.getData(ModAttachments.CYBERWARE);
        boolean active = data.hasSpecificItem(ModItems.WETWARE_WARDENANTLERS.get(), CyberwareSlot.BRAIN);

        if (!active) {
            mc.gameRenderer.shutdownEffect();
            SonarPingManager.clear();
            return;
        }

        // KEEP ALIVE: if MC culled the effect, immediately re-load it.
        if (getPostChain(mc.gameRenderer) == null) {
            mc.gameRenderer.loadEffect(SONAR_POST);
        }

        PostChain chain = getPostChain(mc.gameRenderer);
        if (chain == null) return;

        int w = mc.getWindow().getWidth();
        int h = mc.getWindow().getHeight();
        float aspect = (h == 0) ? 1.0f : ((float) w / (float) h);

        double fovDegrees = mc.options.fov().get();
        float tanHalfFov = (float) Math.tan(Math.toRadians(fovDegrees * 0.5));

        chain.setUniform("Aspect", aspect);
        chain.setUniform("TanHalfFov", tanHalfFov);

        chain.setUniform("RadiusBlocks", 10.0f);
        chain.setUniform("EdgeThreshold", 0.10f);
        chain.setUniform("EdgeBoost", 1.25f);
        chain.setUniform("Feather", 1.5f);

        chain.setUniform("SoundLifeSeconds", 1.0f);
        chain.setUniform("SoundRadius", 10.0f);

        Camera cam = mc.gameRenderer.getMainCamera();
        Vec3 camPos = cam.getPosition();
        Quaternionf invCamRot = new Quaternionf(cam.rotation()).conjugate();

        List<SonarPingManager.Ping> newestFirst = SonarPingManager.snapshotNewestFirst();
        int count = Math.min(SonarPingManager.MAX_PINGS, newestFirst.size());
        chain.setUniform("SoundCount", (float) count);

        long now = Util.getNanos();

        for (int i = 0; i < SonarPingManager.MAX_PINGS; i++) {
            float vx = 0, vy = 0, vz = 0;
            float ageSec = 999.0f;

            if (i < count) {
                SonarPingManager.Ping p = newestFirst.get(i);
                Vec3 d = p.worldPos.subtract(camPos);

                Vector3f v = new Vector3f((float) d.x, (float) d.y, (float) d.z);
                invCamRot.transform(v);

                vx = v.x;
                vy = v.y;
                vz = v.z;
                ageSec = (now - p.timeNanos) / 1_000_000_000.0f;
            }

            chain.setUniform("SoundPosX" + i, vx);
            chain.setUniform("SoundPosY" + i, vy);
            chain.setUniform("SoundPosZ" + i, vz);
            chain.setUniform("SoundAge" + i, ageSec);
        }
    }

    private static PostChain getPostChain(GameRenderer renderer) {
        try {
            if (postChainField == null) {
                postChainField = findPostChainField(renderer);
            }
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