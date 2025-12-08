package com.perigrine3.createcybernetics.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MarkerManager {

    public static class Marker {

        public final int offX, offY;
        public final RobosurgeonScreen.ViewMode parent;
        public final RobosurgeonScreen.ViewMode target;
        public final Component tooltip;
        public final boolean animated;

        public Marker(int x, int y, RobosurgeonScreen.ViewMode parent,
                      RobosurgeonScreen.ViewMode target, Component tip,
                      boolean animated) {

            this.offX = x;
            this.offY = y;
            this.parent = parent;
            this.target = target;
            this.tooltip = tip;
            this.animated = animated;
        }

        public Marker(int x, int y, RobosurgeonScreen.ViewMode parent,
                      RobosurgeonScreen.ViewMode target, Component tip) {

            this(x, y, parent, target, tip, true);
        }
    }


    private final List<Marker> markers = new ArrayList<>();
    private Marker hovered = null;
    private final ResourceLocation icon;

    public MarkerManager(ResourceLocation iconTexture) {
        this.icon = iconTexture;
    }

    public void add(Marker marker) {
        markers.add(marker);
    }

    public void clear() {
        markers.clear();
    }

    public Marker getHovered() {
        return hovered;
    }

    public void render(GuiGraphics gui, int modelX, int modelY,
                       double mouseX, double mouseY,
                       RobosurgeonScreen.ViewMode viewMode, float markerPhase,
                       Font font) {

        RenderSystem.setShaderTexture(0, icon);
        hovered = null;

        for (Marker marker : markers) {

            boolean visible = marker.parent == viewMode;
            if (!visible) continue;

            int px = modelX + marker.offX;
            int py = modelY + marker.offY;

            float amp =
                    (marker.parent == RobosurgeonScreen.ViewMode.FULL_BODY &&
                            (marker.target == RobosurgeonScreen.ViewMode.LARM ||
                                    marker.target == RobosurgeonScreen.ViewMode.RARM)) ? 30f :
                            (marker.parent == RobosurgeonScreen.ViewMode.FULL_BODY &&
                                    (marker.target == RobosurgeonScreen.ViewMode.LLEG ||
                                            marker.target == RobosurgeonScreen.ViewMode.RLEG)) ? 14f :
                                    4f;

            if (marker.animated && viewMode.allowMarkerAnimation) {
                float offset =
                        (marker.parent == RobosurgeonScreen.ViewMode.FULL_BODY &&
                                (marker.target == RobosurgeonScreen.ViewMode.LLEG ||
                                        marker.target == RobosurgeonScreen.ViewMode.RLEG) &&
                                marker.offX == -2)
                                ? -amp * markerPhase
                                : amp * ((marker.offX > 0) ? -markerPhase : markerPhase);

                px += (int) offset;
            }

            if (mouseX >= px && mouseX <= px + 16 && mouseY >= py && mouseY <= py + 16)
                hovered = marker;

            gui.pose().pushPose();
            gui.pose().translate(0, 0, 200);

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            float alpha = (hovered == marker) ? 1f : 0.2f;
            gui.setColor(1f, 1f, 1f, alpha);

            gui.blit(icon, px, py, 0, 0, 16, 16, 16, 16);

            gui.setColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
            gui.pose().popPose();
        }

        if (hovered != null)
            gui.renderTooltip(font, hovered.tooltip, (int) mouseX, (int) mouseY);
    }

    public RobosurgeonScreen.ViewMode tryClick(double mouseX, double mouseY,
                                               int modelX, int modelY, float markerPhase,
                                               RobosurgeonScreen.ViewMode viewMode) {

        for (Marker marker : markers) {

            boolean visible = marker.parent == viewMode;
            if (!visible) continue;

            int px = modelX + marker.offX;
            int py = modelY + marker.offY;

            float amp =
                    (marker.parent == RobosurgeonScreen.ViewMode.FULL_BODY &&
                            (marker.target == RobosurgeonScreen.ViewMode.LARM ||
                                    marker.target == RobosurgeonScreen.ViewMode.RARM)) ? 30f :
                            (marker.parent == RobosurgeonScreen.ViewMode.FULL_BODY &&
                                    (marker.target == RobosurgeonScreen.ViewMode.LLEG ||
                                            marker.target == RobosurgeonScreen.ViewMode.RLEG)) ? 14f :
                                    4f;

            if (marker.animated && viewMode.allowMarkerAnimation) {
                float offset =
                        (marker.parent == RobosurgeonScreen.ViewMode.FULL_BODY &&
                                (marker.target == RobosurgeonScreen.ViewMode.LLEG ||
                                        marker.target == RobosurgeonScreen.ViewMode.RLEG) &&
                                marker.offX == -2)
                                ? -amp * markerPhase
                                : amp * ((marker.offX > 0) ? -markerPhase : markerPhase);

                px += (int) offset;
            }

            if (mouseX >= px && mouseX <= px + 16 &&
                    mouseY >= py && mouseY <= py + 16)
                return marker.target;
        }
        return null;
    }
}
