package com.perigrine3.createcybernetics.tattoo;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.screen.custom.TattooMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class TattooApplicationService {
    public static final int GOLD_COST = 5;

    private TattooApplicationService() {
    }

    public static ApplyResult apply(ServerPlayer player, TattooMenu menu, ResourceLocation tattooId, TattooLayer requestedLayer) {
        if (player == null || menu == null) {
            return ApplyResult.failure(Component.literal("Tattoo application failed."));
        }

        if (tattooId == null) {
            return ApplyResult.failure(Component.literal("No tattoo selected."));
        }

        TattooEntry tattoo = ServerTattooRegistry.getApproved(tattooId);
        if (tattoo == null) {
            return ApplyResult.failure(Component.literal("Selected tattoo is no longer approved."));
        }

        ItemStack payment = menu.getPaymentStack();
        if (payment.isEmpty() || !payment.is(Items.GOLD_INGOT) || payment.getCount() < GOLD_COST) {
            return ApplyResult.failure(Component.literal("Insert 5 gold ingots."));
        }

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) {
            return ApplyResult.failure(Component.literal("Cyberware data unavailable."));
        }

        TattooableSkin target = findTattooableInstalledSkin(data);
        if (target.stack().isEmpty()) {
            return ApplyResult.failure(Component.literal("You need skin or synthskin installed."));
        }

        TattooLayer finalLayer = target.synthSkin()
                ? requestedLayer == null ? TattooLayer.UNDER_CYBERWARE : requestedLayer
                : TattooLayer.UNDER_CYBERWARE;

        TattooData.write(target.stack(), tattoo, finalLayer);

        menu.consumePayment(GOLD_COST);

        data.setDirty();
        ModAttachments.syncCyberware(player);
        player.syncData(ModAttachments.CYBERWARE);

        return ApplyResult.success(Component.literal("Tattoo applied: " + tattoo.displayName()));
    }

    public static boolean hasTattooableInstalledSkin(ServerPlayer player) {
        if (player == null) {
            return false;
        }

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        return data != null && !findTattooableInstalledSkin(data).stack().isEmpty();
    }

    public static boolean hasSynthSkinInstalled(ServerPlayer player) {
        if (player == null) {
            return false;
        }

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        return data != null && findTattooableInstalledSkin(data).synthSkin();
    }

    private static TattooableSkin findTattooableInstalledSkin(PlayerCyberwareData data) {
        for (int i = 0; i < CyberwareSlot.SKIN.size; i++) {
            InstalledCyberware installed = data.get(CyberwareSlot.SKIN, i);
            if (installed == null) {
                continue;
            }

            ItemStack stack = installed.getItem();
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            if (stack.is(ModItems.SKINUPGRADES_SYNTHSKIN.get())) {
                return new TattooableSkin(stack, true);
            }

            if (stack.is(ModItems.BODYPART_SKIN.get())) {
                return new TattooableSkin(stack, false);
            }
        }

        return new TattooableSkin(ItemStack.EMPTY, false);
    }

    private record TattooableSkin(ItemStack stack, boolean synthSkin) {
    }

    public record ApplyResult(boolean success, Component message) {
        public static ApplyResult success(Component message) {
            return new ApplyResult(true, message);
        }

        public static ApplyResult failure(Component message) {
            return new ApplyResult(false, message);
        }
    }
}