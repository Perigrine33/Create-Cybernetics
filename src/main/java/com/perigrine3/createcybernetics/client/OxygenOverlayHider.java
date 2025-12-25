package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class OxygenOverlayHider {
    private OxygenOverlayHider() {}

    @SubscribeEvent
    public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
        if (!VanillaGuiLayers.AIR_LEVEL.equals(event.getName())) return;

        Player player = Minecraft.getInstance().player;
        boolean underwater = player.isUnderWater();
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        boolean hasGills = data.hasSpecificItem(ModItems.WETWARE_GILLS.get(), CyberwareSlot.LUNGS);

        if (player == null) return;
        if (!underwater) return;
        if (data == null) return;
        if (hasGills) {
            event.setCanceled(true); // hide oxygen UI
        }
    }
}
