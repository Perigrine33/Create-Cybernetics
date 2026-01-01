package com.perigrine3.createcybernetics.common.events;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.network.payload.TargetingHighlightPayload;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class TargetingModuleServerEvents {

    private static final int DURATION_TICKS = 45 * 20;

    private TargetingModuleServerEvents() {}

    @SubscribeEvent
    public static void onDamageDealt(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        var src = event.getSource();
        if (src == null) return;

        if (!(src.getEntity() instanceof ServerPlayer player)) return;

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        InstalledLoc targeting = findInstalled(data, ModItems.EYEUPGRADES_TARGETING.get(), CyberwareSlot.EYES);
        if (targeting == null) return;

        InstalledLoc hudjack = findInstalledAny(data, ModItems.EYEUPGRADES_HUDJACK.get());
        if (hudjack == null) return;

        if (!isEnabledIfToggleable(data, targeting)) return;
        if (!isEnabledIfToggleable(data, hudjack)) return;

        PacketDistributor.sendToPlayer(player, new TargetingHighlightPayload(target.getId(), DURATION_TICKS));
    }

    private static boolean isEnabledIfToggleable(PlayerCyberwareData data, InstalledLoc loc) {
        if (loc.stack == null || loc.stack.isEmpty()) return false;
        if (!loc.stack.is(ModTags.Items.TOGGLEABLE_CYBERWARE)) return true;
        return data.isEnabled(loc.slot, loc.index);
    }

    private static InstalledLoc findInstalled(PlayerCyberwareData data, Item item, CyberwareSlot slot) {
        InstalledCyberware[] arr = data.getAll().get(slot);
        if (arr == null) return null;

        for (int i = 0; i < arr.length; i++) {
            InstalledCyberware cw = arr[i];
            if (cw == null) continue;

            ItemStack st = cw.getItem();
            if (st == null || st.isEmpty()) continue;

            if (st.getItem() == item) return new InstalledLoc(slot, i, st);
        }
        return null;
    }

    private static InstalledLoc findInstalledAny(PlayerCyberwareData data, Item item) {
        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware cw = arr[i];
                if (cw == null) continue;

                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;

                if (st.getItem() == item) return new InstalledLoc(slot, i, st);
            }
        }
        return null;
    }

    private record InstalledLoc(CyberwareSlot slot, int index, ItemStack stack) {}
}
