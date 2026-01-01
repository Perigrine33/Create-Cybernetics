package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public final class ArmCannonWheelPayloads {
    private ArmCannonWheelPayloads() {}

    public record RequestOpenArmCannonWheelPayload() implements CustomPacketPayload {
        public static final Type<RequestOpenArmCannonWheelPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "arm_cannon_wheel_open_req"));

        public static final StreamCodec<RegistryFriendlyByteBuf, RequestOpenArmCannonWheelPayload> STREAM_CODEC =
                new StreamCodec<>() {
                    @Override public RequestOpenArmCannonWheelPayload decode(RegistryFriendlyByteBuf buf) {
                        return new RequestOpenArmCannonWheelPayload();
                    }
                    @Override public void encode(RegistryFriendlyByteBuf buf, RequestOpenArmCannonWheelPayload value) {}
                };

        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record OpenArmCannonWheelClientPayload(int segments, int selectedIndex) implements CustomPacketPayload {
        public static final Type<OpenArmCannonWheelClientPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "arm_cannon_wheel_open"));

        public static final StreamCodec<RegistryFriendlyByteBuf, OpenArmCannonWheelClientPayload> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_INT, OpenArmCannonWheelClientPayload::segments,
                        ByteBufCodecs.VAR_INT, OpenArmCannonWheelClientPayload::selectedIndex,
                        OpenArmCannonWheelClientPayload::new
                );

        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record SelectArmCannonAmmoSlotPayload(int slotIndex) implements CustomPacketPayload {
        public static final Type<SelectArmCannonAmmoSlotPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "arm_cannon_wheel_select"));

        public static final StreamCodec<RegistryFriendlyByteBuf, SelectArmCannonAmmoSlotPayload> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_INT, SelectArmCannonAmmoSlotPayload::slotIndex,
                        SelectArmCannonAmmoSlotPayload::new
                );

        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
}
