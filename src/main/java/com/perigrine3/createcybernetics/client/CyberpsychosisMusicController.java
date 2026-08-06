package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.effect.CyberpsychosisSeverity;
import com.perigrine3.createcybernetics.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.Music;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Optional;
import java.util.UUID;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CyberpsychosisMusicController {

    private static final int LEVEL_2_MIN_DELAY_TICKS = 10 * 60 * 20;
    private static final int LEVEL_2_MAX_DELAY_TICKS = 18 * 60 * 20;

    private static final int LEVEL_3_MIN_DELAY_TICKS = 6 * 60 * 20;
    private static final int LEVEL_3_MAX_DELAY_TICKS = 12 * 60 * 20;

    private static UUID trackedPlayerId;
    private static CyberpsychosisSeverity trackedSeverity = CyberpsychosisSeverity.NONE;
    private static int nextMusicTick = -1;

    private static Music descentIntoMadnessMusic;
    private static boolean playingDescentIntoMadness;

    private CyberpsychosisMusicController() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.isPaused()) {
            return;
        }

        if (trackedPlayerId == null || !trackedPlayerId.equals(player.getUUID())) {
            reset(minecraft);
            trackedPlayerId = player.getUUID();
        }

        CyberpsychosisSeverity severity = CyberpsychosisSeverity.fromPlayer(player);

        if (!severity.isAtLeast(CyberpsychosisSeverity.LEVEL_2) || !player.isAlive()) {
            stopPsychosisMusic(minecraft);
            trackedSeverity = severity;
            nextMusicTick = -1;
            return;
        }

        Music music = getDescentIntoMadnessMusic(player);

        if (music == null) {
            return;
        }

        MusicManager musicManager = minecraft.getMusicManager();

        if (playingDescentIntoMadness) {
            if (musicManager.isPlayingMusic(music)) {
                return;
            }

            playingDescentIntoMadness = false;
            scheduleNextTrack(player, severity);
            return;
        }

        if (trackedSeverity != severity || nextMusicTick < 0) {
            trackedSeverity = severity;
            scheduleNextTrack(player, severity);
            return;
        }

        if (player.tickCount < nextMusicTick) {
            return;
        }

        startPsychosisMusic(minecraft, music);
    }

    private static Music getDescentIntoMadnessMusic(LocalPlayer player) {
        if (descentIntoMadnessMusic != null) {
            return descentIntoMadnessMusic;
        }

        Optional<Holder.Reference<JukeboxSong>> songHolder = player.registryAccess()
                .registryOrThrow(Registries.JUKEBOX_SONG)
                .getHolder(ModSounds.DESCENT_INTO_MADNESS_KEY);

        if (songHolder.isEmpty()) {
            return null;
        }

        JukeboxSong song = songHolder.get().value();

        descentIntoMadnessMusic = new Music(
                song.soundEvent(),
                0,
                0,
                true
        );

        return descentIntoMadnessMusic;
    }

    private static void startPsychosisMusic(Minecraft minecraft, Music music) {
        MusicManager musicManager = minecraft.getMusicManager();

        musicManager.stopPlaying();
        musicManager.startPlaying(music);

        playingDescentIntoMadness = true;
        nextMusicTick = -1;
    }

    private static void stopPsychosisMusic(Minecraft minecraft) {
        if (!playingDescentIntoMadness) {
            return;
        }

        Music music = descentIntoMadnessMusic;

        if (music != null) {
            minecraft.getMusicManager().stopPlaying(music);
        }

        playingDescentIntoMadness = false;
    }

    private static void scheduleNextTrack(LocalPlayer player, CyberpsychosisSeverity severity) {
        int minimumDelay;
        int maximumDelay;

        if (severity == CyberpsychosisSeverity.LEVEL_3) {
            minimumDelay = LEVEL_3_MIN_DELAY_TICKS;
            maximumDelay = LEVEL_3_MAX_DELAY_TICKS;
        } else {
            minimumDelay = LEVEL_2_MIN_DELAY_TICKS;
            maximumDelay = LEVEL_2_MAX_DELAY_TICKS;
        }

        nextMusicTick = player.tickCount + player.getRandom().nextIntBetweenInclusive(minimumDelay, maximumDelay);
    }

    private static void reset(Minecraft minecraft) {
        stopPsychosisMusic(minecraft);

        trackedPlayerId = null;
        trackedSeverity = CyberpsychosisSeverity.NONE;
        nextMusicTick = -1;
        descentIntoMadnessMusic = null;
    }
}