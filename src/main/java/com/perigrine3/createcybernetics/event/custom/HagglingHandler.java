package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.attributes.ModAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.TradeWithVillagerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class HagglingHandler {
    private HagglingHandler() {}

    private static final Map<UUID, Map<MerchantOffer, OfferSnapshot>> SNAPSHOTS = new HashMap<>();

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        double haggle = player.getAttributeValue(ModAttributes.HAGGLING);
        if (haggle <= 1.0D) return;

        AbstractContainerMenu menu = event.getContainer();
        if (!(menu instanceof MerchantMenu merchantMenu)) return;

        MerchantOffers offers = merchantMenu.getOffers();
        if (offers == null || offers.isEmpty()) return;

        Map<MerchantOffer, OfferSnapshot> perPlayer = new HashMap<>();
        for (MerchantOffer offer : offers) {
            perPlayer.put(offer, new OfferSnapshot(
                    offer.getSpecialPriceDiff(),
                    offer.getCostA().getCount(),
                    !offer.getCostB().isEmpty(),
                    offer.getResult().getCount()));

            ItemStack currentA = offer.getCostA();
            int currentCount = currentA.getCount();
            if (currentCount > 1) {
                int discounted = Math.max(1, (int) Math.ceil(currentCount / haggle));
                int delta = discounted - currentCount;

                offer.addToSpecialPriceDiff(delta);

                int after = offer.getCostA().getCount();
                if (after < 1) {
                    offer.addToSpecialPriceDiff(1 - after);
                }
            }
        }

        SNAPSHOTS.put(player.getUUID(), perPlayer);
    }

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        AbstractContainerMenu menu = event.getContainer();
        if (!(menu instanceof MerchantMenu)) return;

        Map<MerchantOffer, OfferSnapshot> perPlayer = SNAPSHOTS.remove(player.getUUID());
        if (perPlayer == null) return;

        for (Map.Entry<MerchantOffer, OfferSnapshot> e : perPlayer.entrySet()) {
            MerchantOffer offer = e.getKey();
            OfferSnapshot snap = e.getValue();
            offer.setSpecialPriceDiff(snap.originalSpecialPriceDiff);
        }
    }

    @SubscribeEvent
    public static void onTradeCompleted(TradeWithVillagerEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        double haggle = player.getAttributeValue(ModAttributes.HAGGLING);
        if (haggle <= 1.0D) return;

        MerchantOffer offer = event.getMerchantOffer();

        OfferSnapshot snap = null;
        Map<MerchantOffer, OfferSnapshot> perPlayer = SNAPSHOTS.get(player.getUUID());
        if (perPlayer != null) snap = perPlayer.get(offer);

        ItemStack costB = offer.getCostB();
        if (!costB.isEmpty()) {
            refundDiscount(player, costB, haggle);
        }

        int originalCostA = (snap != null) ? snap.originalCostACount : offer.getCostA().getCount();
        boolean originallyHadCostB = (snap != null) ? snap.hadCostB : !offer.getCostB().isEmpty();

        if (!originallyHadCostB && originalCostA == 1) {
            ItemStack bonus = offer.getResult().copy();
            giveToPlayer(player, bonus);
        }
    }

    private static void refundDiscount(Player player, ItemStack paidCost, double haggle) {
        if (paidCost.isEmpty()) return;

        int original = paidCost.getCount();
        if (original <= 1) return;

        int discounted = Math.max(1, (int) Math.ceil(original / haggle));
        int refund = original - discounted;
        if (refund <= 0) return;

        ItemStack refundStack = paidCost.copy();
        refundStack.setCount(refund);
        giveToPlayer(player, refundStack);
    }

    private static void giveToPlayer(Player player, ItemStack stack) {
        if (stack.isEmpty()) return;

        player.addItem(stack);
        if (!stack.isEmpty()) {
            player.drop(stack, false);
        }
    }

    private record OfferSnapshot(
            int originalSpecialPriceDiff,
            int originalCostACount,
            boolean hadCostB,
            int originalResultCount
    ) {}
}
