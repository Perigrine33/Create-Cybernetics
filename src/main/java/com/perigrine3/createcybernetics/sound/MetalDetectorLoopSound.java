package com.perigrine3.createcybernetics.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class MetalDetectorLoopSound extends AbstractTickableSoundInstance {

    private final Player player;
    private float targetVolume = 1.0F;

    public MetalDetectorLoopSound(Player player, SoundEvent soundEvent) {
        super(soundEvent, SoundSource.PLAYERS, player.getRandom());
        this.player = player;

        this.looping = true;
        this.delay = 0;

        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();

        this.volume = 1.0F;
        this.pitch = 1.0F;
    }

    public void setTargetVolume(float volume) {
        this.targetVolume = volume;
    }

    @Override
    public void tick() {
        if (player == null || player.isRemoved() || !player.isAlive()) {
            this.stop();
            return;
        }

        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();

        this.volume = this.targetVolume;
    }
}
