package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MissingSkinRender {

    private MissingSkinRender() {}

    private static final Map<UUID, boolean[]> PREV_WEAR_VIS = new HashMap<>();

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static final class Layers {
        @SubscribeEvent
        public static void addLayers(EntityRenderersEvent.AddLayers event) {
            PlayerRenderer wide = event.getSkin(PlayerSkin.Model.WIDE);
            if (wide != null) wide.addLayer(new MissingSkinLayer(wide));

            PlayerRenderer slim = event.getSkin(PlayerSkin.Model.SLIM);
            if (slim != null) slim.addLayer(new MissingSkinLayer(slim));
        }
    }


    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT)
    public static final class Wear {
        @SubscribeEvent
        public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
            if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;

            if (!player.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            boolean hasSkin = data.hasAnyTagged(ModTags.Items.SKIN_ITEMS, CyberwareSlot.SKIN);
            if (hasSkin) return;

            PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();

            PREV_WEAR_VIS.put(player.getUUID(), new boolean[]{
                    model.jacket.visible,
                    model.leftSleeve.visible,
                    model.rightSleeve.visible,
                    model.leftPants.visible,
                    model.rightPants.visible,
                    model.hat.visible
            });

            model.jacket.visible = false;
            model.leftSleeve.visible = false;
            model.rightSleeve.visible = false;
            model.leftPants.visible = false;
            model.rightPants.visible = false;
            model.hat.visible = false;
        }

        @SubscribeEvent
        public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
            if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;

            boolean[] prev = PREV_WEAR_VIS.remove(player.getUUID());
            if (prev == null) return;

            PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();

            model.jacket.visible = prev[0];
            model.leftSleeve.visible = prev[1];
            model.rightSleeve.visible = prev[2];
            model.leftPants.visible = prev[3];
            model.rightPants.visible = prev[4];
            model.hat.visible = prev[5];
        }
    }
}
