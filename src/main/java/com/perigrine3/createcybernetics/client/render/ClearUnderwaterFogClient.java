package com.perigrine3.createcybernetics.client.render;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class ClearUnderwaterFogClient {

    private ClearUnderwaterFogClient() {}

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (event.getType() != FogType.WATER) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        InstalledLoc loc = findInstalled(data);
        if (loc == null) return;

        if (loc.stack.is(ModTags.Items.TOGGLEABLE_CYBERWARE) && !data.isEnabled(loc.slot, loc.index)) return;

        event.setNearPlaneDistance(0.0F);
        event.setFarPlaneDistance(event.getFarPlaneDistance() * 8.0F);
        event.setCanceled(true);
    }

    private static InstalledLoc findInstalled(PlayerCyberwareData data) {
        Item item = ModItems.EYEUPGRADES_UNDERWATERVISION.get();

        InstalledCyberware[] arr = data.getAll().get(CyberwareSlot.EYES);
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware cw = arr[i];
                if (cw == null) continue;
                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;
                if (st.getItem() == item) return new InstalledLoc(CyberwareSlot.EYES, i, st);
            }
        }

        return null;
    }

    private record InstalledLoc(CyberwareSlot slot, int index, ItemStack stack) {}
}
