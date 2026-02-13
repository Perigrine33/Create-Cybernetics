package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record InfologSaveChipwarePayload(int chipwareSlot, String text, boolean locked) implements CustomPacketPayload {

    public static final Type<InfologSaveChipwarePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "infolog_save_chipware"));

    public static final StreamCodec<RegistryFriendlyByteBuf, InfologSaveChipwarePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, InfologSaveChipwarePayload::chipwareSlot,
                    ByteBufCodecs.STRING_UTF8, InfologSaveChipwarePayload::text,
                    ByteBufCodecs.BOOL, InfologSaveChipwarePayload::locked,
                    InfologSaveChipwarePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
