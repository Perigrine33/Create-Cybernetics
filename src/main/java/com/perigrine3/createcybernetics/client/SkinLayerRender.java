package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
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

public final class SkinLayerRender {
    private SkinLayerRender() {}

    private static final Map<UUID, boolean[]> PREV_WEAR_VIS = new HashMap<>();
    private static final Map<UUID, SkinModifierState> SKIN_STATES = new HashMap<>();

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static final class Layers {
        @SubscribeEvent
        public static void addLayers(EntityRenderersEvent.AddLayers event) {
            PlayerRenderer wide = event.getSkin(PlayerSkin.Model.WIDE);
            if (wide != null) wide.addLayer(new SkinLayerHandler(wide));

            PlayerRenderer slim = event.getSkin(PlayerSkin.Model.SLIM);
            if (slim != null) slim.addLayer(new SkinLayerHandler(slim));
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT)
    public static final class Wear {
        @SubscribeEvent
        public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
            if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;

            SkinModifierState state = SkinModifierManager.getPlayerSkinState(player);
            if (state == null || !state.shouldHideVanillaLayers()) return;

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