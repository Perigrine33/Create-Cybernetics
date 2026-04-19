package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public record BehindYouSoundPayload(int soundIndex, float volume, float pitch, float behindDistance) implements CustomPacketPayload {
    public static final Type<BehindYouSoundPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "behindyou_sound"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BehindYouSoundPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    BehindYouSoundPayload::soundIndex,
                    ByteBufCodecs.FLOAT,
                    BehindYouSoundPayload::volume,
                    ByteBufCodecs.FLOAT,
                    BehindYouSoundPayload::pitch,
                    ByteBufCodecs.FLOAT,
                    BehindYouSoundPayload::behindDistance,
                    BehindYouSoundPayload::new
            );

    @Override
    public Type<BehindYouSoundPayload> type() {
        return TYPE;
    }

    public static void handle(BehindYouSoundPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        if (!(mc.player instanceof LocalPlayer player)) return;

        SoundEvent sound = switch (payload.soundIndex()) {
            case 1 -> SoundEvents.ENDERMAN_STARE;
            case 2 -> SoundEvents.WARDEN_NEARBY_CLOSE;
            default -> SoundEvents.CREEPER_PRIMED;
        };

        Vec3 look = player.getLookAngle().normalize();
        Vec3 behind = player.getEyePosition().subtract(look.scale(payload.behindDistance()));

        mc.level.playLocalSound(behind.x, behind.y, behind.z,
                sound, SoundSource.HOSTILE,
                payload.volume(), payload.pitch(), false
        );
    }
}