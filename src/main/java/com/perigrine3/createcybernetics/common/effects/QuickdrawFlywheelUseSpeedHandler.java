package com.perigrine3.createcybernetics.common.effects;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class QuickdrawFlywheelUseSpeedHandler {

    private static final float CHARGE_SPEED_MULTIPLIER = 2f;

    private QuickdrawFlywheelUseSpeedHandler() {}

    @SubscribeEvent
    public static void onUseItemTick(LivingEntityUseItemEvent.Tick event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!isQuickdrawFlywheelPowered(player)) return;

        ItemStack using = event.getItem();
        if (!(using.getItem() instanceof BowItem) && !(using.getItem() instanceof CrossbowItem)) return;

        if (using.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(using)) return;

        int total = using.getUseDuration(player);
        if (total <= 0) return;

        int remaining = event.getDuration();
        remaining = Math.min(remaining, total);

        int used = Math.max(0, total - remaining);
        int boostedUsed = Math.min(total, Math.round(used * CHARGE_SPEED_MULTIPLIER));
        int boostedRemaining = Math.max(0, total - boostedUsed);

        if (boostedRemaining < remaining) {
            event.setDuration(boostedRemaining);
        }
    }

    private static boolean isQuickdrawFlywheelPowered(Player player) {
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        Item target = ModItems.ARMUPGRADES_FLYWHEEL.get();

        boolean hasRight = false;
        for (int i = 0; i < CyberwareSlot.RARM.size; i++) {
            InstalledCyberware cw = data.get(CyberwareSlot.RARM, i);
            if (cw == null) continue;
            ItemStack st = cw.getItem();
            if (st == null || st.isEmpty()) continue;
            if (st.getItem() == target) {
                hasRight = true;
                break;
            }
        }

        CyberwareSlot primary = hasRight ? CyberwareSlot.RARM : CyberwareSlot.LARM;

        for (int i = 0; i < primary.size; i++) {
            InstalledCyberware cw = data.get(primary, i);
            if (cw == null) continue;
            ItemStack st = cw.getItem();
            if (st == null || st.isEmpty()) continue;
            if (st.getItem() != target) continue;
            return cw.isPowered();
        }

        return false;
    }
}
