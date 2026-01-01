package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CyberwareEnabledStatePayload(String slotName, int index, boolean enabled) implements CustomPacketPayload {

    public static final Type<CyberwareEnabledStatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "cyberware_enabled_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CyberwareEnabledStatePayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8, CyberwareEnabledStatePayload::slotName, ByteBufCodecs.VAR_INT,
                    CyberwareEnabledStatePayload::index, ByteBufCodecs.BOOL, CyberwareEnabledStatePayload::enabled, CyberwareEnabledStatePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CyberwareEnabledStatePayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            if (mc.player == null) return;
            if (!mc.player.hasData(ModAttachments.CYBERWARE)) return;

            PlayerCyberwareData data = mc.player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            CyberwareSlot slot;
            try {
                slot = CyberwareSlot.valueOf(msg.slotName());
            } catch (IllegalArgumentException ex) {
                return;
            }

            data.setEnabled(slot, msg.index(), msg.enabled());
        });
    }
}
