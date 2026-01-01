package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public final class CyberwareTogglePayloads {
    private CyberwareTogglePayloads() {}

    public record RequestToggleStatesPayload() implements CustomPacketPayload {
        public static final Type<RequestToggleStatesPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "toggle_states_req"));

        public static final StreamCodec<RegistryFriendlyByteBuf, RequestToggleStatesPayload> STREAM_CODEC =
                new StreamCodec<>() {
                    @Override
                    public RequestToggleStatesPayload decode(RegistryFriendlyByteBuf buf) {
                        return new RequestToggleStatesPayload();
                    }

                    @Override
                    public void encode(RegistryFriendlyByteBuf buf, RequestToggleStatesPayload value) {}
                };

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /**
     * Toggle a specific installed cyberware instance (slot + index).
     * This directly flips PlayerCyberwareData.enabled[slot][index].
     */
    public record ToggleCyberwarePayload(String slotName, int index) implements CustomPacketPayload {
        public static final Type<ToggleCyberwarePayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "toggle_cyberware"));

        public static final StreamCodec<RegistryFriendlyByteBuf, ToggleCyberwarePayload> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, ToggleCyberwarePayload::slotName,
                        ByteBufCodecs.VAR_INT, ToggleCyberwarePayload::index,
                        ToggleCyberwarePayload::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
