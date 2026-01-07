package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record SetChipwareShardPayload(int slot, ItemStack stack) implements CustomPacketPayload {

    public static final Type<SetChipwareShardPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "set_chipware_shard"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetChipwareShardPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, SetChipwareShardPayload::slot,
                    ItemStack.STREAM_CODEC, SetChipwareShardPayload::stack,
                    SetChipwareShardPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
