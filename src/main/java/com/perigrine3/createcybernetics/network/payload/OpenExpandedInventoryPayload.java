package com.perigrine3.createcybernetics.network.payload;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.cyberware.CraftingHandsItem;
import com.perigrine3.createcybernetics.screen.custom.ExpandedInventoryMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenExpandedInventoryPayload() implements CustomPacketPayload {

    public static final Type<OpenExpandedInventoryPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "open_expanded_inventory"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenExpandedInventoryPayload> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> {}, buf -> new OpenExpandedInventoryPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenExpandedInventoryPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer sp)) return;
            if (sp.isCreative() || sp.isSpectator()) return;

            if (!sp.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            if (!CraftingHandsItem.hasInstalledEitherArm(data)) return;

            sp.openMenu(new SimpleMenuProvider(
                    (id, inv, player) -> new ExpandedInventoryMenu(id, inv, ContainerLevelAccess.create(player.level(), player.blockPosition())),
                    Component.translatable("container.inventory")
            ));
        });
    }
}
