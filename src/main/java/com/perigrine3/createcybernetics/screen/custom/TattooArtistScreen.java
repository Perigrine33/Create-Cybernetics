package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.network.payload.TattooApplyC2SPayload;
import com.perigrine3.createcybernetics.tattoo.TattooLayer;
import com.perigrine3.createcybernetics.tattoo.client.ClientTattooRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class TattooArtistScreen extends AbstractContainerScreen<TattooMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            CreateCybernetics.MODID,
            "textures/gui/tattoo_artist_gui.png"
    );

    private static final int LIST_X = 74;
    private static final int LIST_Y = 12;
    private static final int LIST_WIDTH = 86;
    private static final int LIST_HEIGHT = 57;

    private static final int ROW_HEIGHT = 12;
    private static final int LIST_PADDING_X = 3;
    private static final int LIST_PADDING_Y = 2;

    private static final int SCROLLBAR_X = 162;
    private static final int SCROLLBAR_Y = 12;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_HEIGHT = 57;

    private static final int CONFIRM_X = 20;
    private static final int CONFIRM_Y = 52;
    private static final int CONFIRM_WIDTH = 55;
    private static final int CONFIRM_HEIGHT = 18;

    private static final int LAYER_X = 77;
    private static final int LAYER_Y = 70;
    private static final int LAYER_WIDTH = 91;
    private static final int LAYER_HEIGHT = 12;

    private static final int LIST_BORDER_DARK = 0xFF303030;
    private static final int LIST_BORDER_LIGHT = 0xFFE0E0E0;
    private static final int LIST_BACKGROUND = 0xFFC0C0C0;
    private static final int LIST_ROW_SELECTED = 0x90FFFFFF;
    private static final int LIST_ROW_HOVERED = 0x50FFFFFF;
    private static final int LIST_TEXT = 0xFF202020;
    private static final int LIST_TEXT_SELECTED = 0xFF000000;

    private static final int SCROLLBAR_TRACK = 0xFF707070;
    private static final int SCROLLBAR_THUMB = 0xFFE4E4E4;
    private static final int SCROLLBAR_THUMB_DARK = 0xFF505050;
    private static final int SCROLLBAR_THUMB_LIGHT = 0xFFFFFFFF;

    private ResourceLocation selectedTattooId;
    private TattooLayer selectedLayer = TattooLayer.UNDER_CYBERWARE;

    private int scrollOffset;

    private Button confirmButton;
    private Button layerButton;

    public TattooArtistScreen(TattooMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 176;
        imageHeight = 166;
        inventoryLabelY = 74;
        titleLabelX = 8;
        titleLabelY = 6;
    }

    @Override
    protected void init() {
        super.init();

        confirmButton = Button.builder(Component.literal("CONFIRM"), button -> confirm())
                .bounds(leftPos + CONFIRM_X, topPos + CONFIRM_Y, CONFIRM_WIDTH, CONFIRM_HEIGHT)
                .build();

        layerButton = Button.builder(Component.literal(selectedLayer.displayName()), button -> toggleLayer())
                .bounds(leftPos + LAYER_X, topPos + LAYER_Y, LAYER_WIDTH, LAYER_HEIGHT)
                .build();

        addRenderableWidget(confirmButton);
        addRenderableWidget(layerButton);

        clampScrollOffset();
        updateButtons();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        clampScrollOffset();
        updateButtons();
    }

    private void confirm() {
        if (selectedTattooId == null) {
            return;
        }

        TattooLayer layer = menu.canChooseTattooLayer()
                ? selectedLayer
                : TattooLayer.UNDER_CYBERWARE;

        PacketDistributor.sendToServer(new TattooApplyC2SPayload(selectedTattooId, layer));
    }

    private void toggleLayer() {
        if (!menu.canChooseTattooLayer()) {
            selectedLayer = TattooLayer.UNDER_CYBERWARE;
            updateButtons();
            return;
        }

        selectedLayer = selectedLayer.next();
        updateButtons();
    }

    private void updateButtons() {
        if (confirmButton != null) {
            confirmButton.active = selectedTattooId != null && menu.hasEnoughGold();
        }

        if (layerButton != null) {
            if (!menu.canChooseTattooLayer()) {
                selectedLayer = TattooLayer.UNDER_CYBERWARE;
                layerButton.setMessage(Component.literal("Under Cyberware"));
                layerButton.active = false;
                layerButton.visible = true;
            } else {
                layerButton.setMessage(Component.literal(selectedLayer.displayName()));
                layerButton.active = true;
                layerButton.visible = true;
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        renderTattooListFrame(graphics);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTattooList(graphics, mouseX, mouseY);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void renderTattooListFrame(GuiGraphics graphics) {
        int x = leftPos + LIST_X;
        int y = topPos + LIST_Y;

        graphics.fill(x, y, x + LIST_WIDTH, y + LIST_HEIGHT, LIST_BORDER_DARK);
        graphics.fill(x + 1, y + 1, x + LIST_WIDTH - 1, y + LIST_HEIGHT - 1, LIST_BORDER_LIGHT);
        graphics.fill(x + 2, y + 2, x + LIST_WIDTH - 2, y + LIST_HEIGHT - 2, LIST_BACKGROUND);

        int scrollX = leftPos + SCROLLBAR_X;
        int scrollY = topPos + SCROLLBAR_Y;

        graphics.fill(scrollX, scrollY, scrollX + SCROLLBAR_WIDTH, scrollY + SCROLLBAR_HEIGHT, LIST_BORDER_DARK);
        graphics.fill(scrollX + 1, scrollY + 1, scrollX + SCROLLBAR_WIDTH - 1, scrollY + SCROLLBAR_HEIGHT - 1, SCROLLBAR_TRACK);
    }

    private void renderTattooList(GuiGraphics graphics, int mouseX, int mouseY) {
        List<ClientTattooRegistry.ClientTattooEntry> tattoos = ClientTattooRegistry.approvedTattoos();

        int contentX = leftPos + LIST_X + LIST_PADDING_X;
        int contentY = topPos + LIST_Y + LIST_PADDING_Y;
        int contentWidth = LIST_WIDTH - LIST_PADDING_X * 2;
        int contentHeight = LIST_HEIGHT - LIST_PADDING_Y * 2;

        graphics.enableScissor(contentX, contentY, contentX + contentWidth, contentY + contentHeight);

        for (int visibleIndex = 0; visibleIndex < getVisibleRows(); visibleIndex++) {
            int entryIndex = scrollOffset + visibleIndex;
            if (entryIndex < 0 || entryIndex >= tattoos.size()) {
                continue;
            }

            ClientTattooRegistry.ClientTattooEntry entry = tattoos.get(entryIndex);

            int rowX = contentX;
            int rowY = contentY + visibleIndex * ROW_HEIGHT;
            int rowRight = contentX + contentWidth;
            int rowBottom = rowY + ROW_HEIGHT;

            boolean selected = entry.id().equals(selectedTattooId);
            boolean hovered = mouseX >= rowX && mouseX < rowRight && mouseY >= rowY && mouseY < rowBottom;

            if (selected) {
                graphics.fill(rowX, rowY, rowRight, rowBottom, LIST_ROW_SELECTED);
                graphics.fill(rowX, rowY, rowRight, rowY + 1, 0xFFFFFFFF);
                graphics.fill(rowX, rowBottom - 1, rowRight, rowBottom, 0xFF606060);
            } else if (hovered) {
                graphics.fill(rowX, rowY, rowRight, rowBottom, LIST_ROW_HOVERED);
            }

            String name = font.plainSubstrByWidth(entry.fileName(), contentWidth - 4);
            graphics.drawString(font, name, rowX + 2, rowY + 2, selected ? LIST_TEXT_SELECTED : LIST_TEXT, false);
        }

        if (tattoos.isEmpty()) {
            graphics.drawString(font, "No Tattoos", contentX + 4, contentY + 4, 0xFF606060, false);
        }

        graphics.disableScissor();

        renderScrollbar(graphics, tattoos.size());
    }

    private void renderScrollbar(GuiGraphics graphics, int totalRows) {
        int x = leftPos + SCROLLBAR_X;
        int y = topPos + SCROLLBAR_Y;

        int trackX = x + 1;
        int trackY = y + 1;
        int trackWidth = SCROLLBAR_WIDTH - 2;
        int trackHeight = SCROLLBAR_HEIGHT - 2;

        if (totalRows <= getVisibleRows()) {
            graphics.fill(trackX, trackY, trackX + trackWidth, trackY + trackHeight, 0xFF999999);
            return;
        }

        int visibleRows = getVisibleRows();
        int maxScroll = Math.max(1, totalRows - visibleRows);

        int thumbHeight = Math.max(10, trackHeight * visibleRows / totalRows);
        int travel = Math.max(1, trackHeight - thumbHeight);
        int thumbY = trackY + Math.round((scrollOffset / (float) maxScroll) * travel);

        graphics.fill(trackX, thumbY, trackX + trackWidth, thumbY + thumbHeight, SCROLLBAR_THUMB);
        graphics.fill(trackX, thumbY, trackX + trackWidth, thumbY + 1, SCROLLBAR_THUMB_LIGHT);
        graphics.fill(trackX, thumbY + thumbHeight - 1, trackX + trackWidth, thumbY + thumbHeight, SCROLLBAR_THUMB_DARK);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && clickedTattooList(mouseX, mouseY)) {
            return true;
        }

        if (button == 0 && clickedScrollbar(mouseX, mouseY)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean clickedTattooList(double mouseX, double mouseY) {
        int x = leftPos + LIST_X + LIST_PADDING_X;
        int y = topPos + LIST_Y + LIST_PADDING_Y;
        int width = LIST_WIDTH - LIST_PADDING_X * 2;
        int height = LIST_HEIGHT - LIST_PADDING_Y * 2;

        if (mouseX < x || mouseX >= x + width || mouseY < y || mouseY >= y + height) {
            return false;
        }

        int visibleIndex = ((int) mouseY - y) / ROW_HEIGHT;
        int entryIndex = scrollOffset + visibleIndex;

        List<ClientTattooRegistry.ClientTattooEntry> tattoos = ClientTattooRegistry.approvedTattoos();

        if (entryIndex < 0 || entryIndex >= tattoos.size()) {
            return true;
        }

        selectedTattooId = tattoos.get(entryIndex).id();
        updateButtons();
        return true;
    }

    private boolean clickedScrollbar(double mouseX, double mouseY) {
        int x = leftPos + SCROLLBAR_X;
        int y = topPos + SCROLLBAR_Y;

        if (mouseX < x || mouseX >= x + SCROLLBAR_WIDTH || mouseY < y || mouseY >= y + SCROLLBAR_HEIGHT) {
            return false;
        }

        List<ClientTattooRegistry.ClientTattooEntry> tattoos = ClientTattooRegistry.approvedTattoos();
        int maxScroll = Math.max(0, tattoos.size() - getVisibleRows());

        if (maxScroll <= 0) {
            scrollOffset = 0;
            return true;
        }

        float relative = (float) ((mouseY - y) / SCROLLBAR_HEIGHT);
        scrollOffset = Math.round(relative * maxScroll);
        clampScrollOffset();
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isMouseOverTattooList(mouseX, mouseY) || isMouseOverScrollbar(mouseX, mouseY)) {
            List<ClientTattooRegistry.ClientTattooEntry> tattoos = ClientTattooRegistry.approvedTattoos();
            int maxScroll = Math.max(0, tattoos.size() - getVisibleRows());

            if (scrollY < 0) {
                scrollOffset = Math.min(maxScroll, scrollOffset + 1);
            } else if (scrollY > 0) {
                scrollOffset = Math.max(0, scrollOffset - 1);
            }

            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private boolean isMouseOverTattooList(double mouseX, double mouseY) {
        int x = leftPos + LIST_X;
        int y = topPos + LIST_Y;

        return mouseX >= x && mouseX < x + LIST_WIDTH && mouseY >= y && mouseY < y + LIST_HEIGHT;
    }

    private boolean isMouseOverScrollbar(double mouseX, double mouseY) {
        int x = leftPos + SCROLLBAR_X;
        int y = topPos + SCROLLBAR_Y;

        return mouseX >= x && mouseX < x + SCROLLBAR_WIDTH && mouseY >= y && mouseY < y + SCROLLBAR_HEIGHT;
    }

    private int getVisibleRows() {
        return Math.max(1, (LIST_HEIGHT - LIST_PADDING_Y * 2) / ROW_HEIGHT);
    }

    private void clampScrollOffset() {
        List<ClientTattooRegistry.ClientTattooEntry> tattoos = ClientTattooRegistry.approvedTattoos();
        int maxScroll = Math.max(0, tattoos.size() - getVisibleRows());

        if (scrollOffset < 0) {
            scrollOffset = 0;
        } else if (scrollOffset > maxScroll) {
            scrollOffset = maxScroll;
        }

        if (selectedTattooId != null && ClientTattooRegistry.get(selectedTattooId) == null) {
            selectedTattooId = null;
        }
    }
}