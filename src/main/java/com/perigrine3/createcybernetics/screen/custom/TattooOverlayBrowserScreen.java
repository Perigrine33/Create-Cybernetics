package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.network.payload.TattooApproveC2SPayload;
import com.perigrine3.createcybernetics.network.payload.TattooPendingListRequestC2SPayload;
import com.perigrine3.createcybernetics.network.payload.TattooRejectC2SPayload;
import com.perigrine3.createcybernetics.network.payload.TattooRemoveApprovedC2SPayload;
import com.perigrine3.createcybernetics.network.payload.TattooUploadC2SPayload;
import com.perigrine3.createcybernetics.tattoo.client.ClientPendingTattooRegistry;
import com.perigrine3.createcybernetics.tattoo.client.ClientTattooAccess;
import com.perigrine3.createcybernetics.tattoo.client.ClientTattooRegistry;
import com.perigrine3.createcybernetics.tattoo.client.ClientTattooTextureCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TattooOverlayBrowserScreen extends Screen {
    private static final int TILE_WIDTH = 92;
    private static final int TILE_HEIGHT = 122;
    private static final int TILE_GAP = 10;
    private static final int TOP_MARGIN = 42;
    private static final int BOTTOM_MARGIN = 18;

    private final Screen parent;

    private Mode mode = Mode.APPROVED;
    private ResourceLocation selectedTattooId;
    private int scrollOffset;

    private Button uploadButton;
    private Button pendingButton;
    private Button approveButton;
    private Button rejectButton;
    private Button removeButton;
    private Button backButton;

    public TattooOverlayBrowserScreen(Screen parent) {
        super(Component.literal("Tattoo Overlays"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        uploadButton = Button.builder(Component.literal("UPLOAD"), button -> upload())
                .bounds(12, 12, 72, 20)
                .build();

        pendingButton = Button.builder(Component.literal("PENDING"), button -> openPending())
                .bounds(88, 12, 76, 20)
                .build();

        approveButton = Button.builder(Component.literal("APPROVE"), button -> approveSelected())
                .bounds(168, 12, 76, 20)
                .build();

        rejectButton = Button.builder(Component.literal("REJECT"), button -> rejectSelected())
                .bounds(248, 12, 76, 20)
                .build();

        removeButton = Button.builder(Component.literal("REMOVE"), button -> removeSelectedApproved())
                .bounds(248, 12, 76, 20)
                .build();

        backButton = Button.builder(Component.literal("CLOSE"), button -> back())
                .bounds(width - 84, 12, 72, 20)
                .build();

        addRenderableWidget(uploadButton);
        addRenderableWidget(pendingButton);
        addRenderableWidget(approveButton);
        addRenderableWidget(rejectButton);
        addRenderableWidget(removeButton);
        addRenderableWidget(backButton);

        updateButtons();
    }

    private void upload() {
        if (!ClientTattooAccess.canUpload()) {
            return;
        }

        String path;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer filters = stack.mallocPointer(1);
            filters.put(stack.UTF8("*.png"));
            filters.flip();

            path = TinyFileDialogs.tinyfd_openFileDialog(
                    "Select tattoo overlay PNG",
                    "",
                    filters,
                    "PNG files",
                    false
            );
        }

        if (path == null || path.isBlank()) {
            return;
        }

        try {
            Path file = Path.of(path);
            byte[] bytes = Files.readAllBytes(file);
            PacketDistributor.sendToServer(new TattooUploadC2SPayload(file.getFileName().toString(), bytes));

            if (minecraft != null && minecraft.player != null) {
                minecraft.player.sendSystemMessage(Component.literal("Uploaded tattoo overlay: " + file.getFileName()));
            }

            if (mode == Mode.PENDING && ClientTattooAccess.canModerate()) {
                PacketDistributor.sendToServer(new TattooPendingListRequestC2SPayload());
            }
        } catch (Exception ex) {
            if (minecraft != null && minecraft.player != null) {
                minecraft.player.sendSystemMessage(Component.literal("Failed to upload tattoo PNG."));
            }
        }
    }

    private void openPending() {
        if (!ClientTattooAccess.canModerate()) {
            return;
        }

        mode = Mode.PENDING;
        selectedTattooId = null;
        scrollOffset = 0;

        PacketDistributor.sendToServer(new TattooPendingListRequestC2SPayload());

        updateButtons();
    }

    private void approveSelected() {
        if (mode != Mode.PENDING || selectedTattooId == null || !ClientTattooAccess.canModerate()) {
            return;
        }

        PacketDistributor.sendToServer(new TattooApproveC2SPayload(selectedTattooId));
        selectedTattooId = null;
        updateButtons();
    }

    private void rejectSelected() {
        if (mode != Mode.PENDING || selectedTattooId == null || !ClientTattooAccess.canModerate()) {
            return;
        }

        PacketDistributor.sendToServer(new TattooRejectC2SPayload(selectedTattooId));
        selectedTattooId = null;
        updateButtons();
    }

    private void removeSelectedApproved() {
        if (mode != Mode.APPROVED || selectedTattooId == null || !ClientTattooAccess.canModerate()) {
            return;
        }

        PacketDistributor.sendToServer(new TattooRemoveApprovedC2SPayload(selectedTattooId));
        selectedTattooId = null;
        updateButtons();
    }

    private void back() {
        if (mode == Mode.PENDING) {
            mode = Mode.APPROVED;
            selectedTattooId = null;
            scrollOffset = 0;
            updateButtons();
            return;
        }

        Minecraft.getInstance().setScreen(parent);
    }

    private void updateButtons() {
        if (uploadButton != null) {
            uploadButton.active = ClientTattooAccess.canUpload();
            uploadButton.visible = ClientTattooAccess.canUpload();
        }

        if (pendingButton != null) {
            pendingButton.active = ClientTattooAccess.canModerate() && mode == Mode.APPROVED;
            pendingButton.visible = ClientTattooAccess.canModerate();
        }

        if (approveButton != null) {
            approveButton.visible = mode == Mode.PENDING && ClientTattooAccess.canModerate();
            approveButton.active = selectedTattooId != null;
        }

        if (rejectButton != null) {
            rejectButton.visible = mode == Mode.PENDING && ClientTattooAccess.canModerate();
            rejectButton.active = selectedTattooId != null;
        }

        if (removeButton != null) {
            removeButton.visible = mode == Mode.APPROVED && ClientTattooAccess.canModerate();
            removeButton.active = selectedTattooId != null;
        }

        if (backButton != null) {
            backButton.setMessage(Component.literal(mode == Mode.PENDING ? "BACK" : "CLOSE"));
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (mode == Mode.PENDING && selectedTattooId != null && ClientPendingTattooRegistry.get(selectedTattooId) == null) {
            selectedTattooId = null;
        }

        if (mode == Mode.APPROVED && selectedTattooId != null && ClientTattooRegistry.get(selectedTattooId) == null) {
            selectedTattooId = null;
        }

        int max = maxScroll();
        if (scrollOffset > max) {
            scrollOffset = max;
        }

        updateButtons();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderPlainBackground(graphics);
        renderTiles(graphics, mouseX, mouseY);
        renderWidgets(graphics, mouseX, mouseY, partialTick);
    }

    private void renderPlainBackground(GuiGraphics graphics) {
        graphics.fill(0, 0, width, height, 0xFF101010);
    }

    private void renderWidgets(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        for (var renderable : this.renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    private void renderTiles(GuiGraphics graphics, int mouseX, int mouseY) {
        List<ClientTattooRegistry.ClientTattooEntry> entries = visibleEntries();

        int columns = Math.max(1, (width - TILE_GAP) / (TILE_WIDTH + TILE_GAP));
        int contentWidth = columns * TILE_WIDTH + (columns - 1) * TILE_GAP;
        int startX = (width - contentWidth) / 2;

        int visibleTop = TOP_MARGIN;
        int visibleBottom = height - BOTTOM_MARGIN;

        graphics.enableScissor(0, visibleTop, width, visibleBottom);

        for (int i = 0; i < entries.size(); i++) {
            int row = i / columns;
            int column = i % columns;

            int x = startX + column * (TILE_WIDTH + TILE_GAP);
            int y = TOP_MARGIN + row * (TILE_HEIGHT + TILE_GAP) - scrollOffset;

            if (y + TILE_HEIGHT < visibleTop || y > visibleBottom) {
                continue;
            }

            renderTile(graphics, entries.get(i), x, y, mouseX, mouseY);
        }

        graphics.disableScissor();

        if (entries.isEmpty()) {
            graphics.drawCenteredString(
                    font,
                    mode == Mode.APPROVED ? "No approved overlays." : "No pending overlays.",
                    width / 2,
                    height / 2,
                    0xA0A0A0
            );
        }
    }

    private void renderTile(GuiGraphics graphics, ClientTattooRegistry.ClientTattooEntry entry, int x, int y, int mouseX, int mouseY) {
        boolean selected = entry.id().equals(selectedTattooId);
        boolean hovered = mouseX >= x && mouseX < x + TILE_WIDTH && mouseY >= y && mouseY < y + TILE_HEIGHT;

        int bg = selected ? 0xFF6F6F8F : hovered ? 0xFF4F4F5F : 0xFF303038;
        int border = selected ? 0xFFFFFFFF : 0xFF909090;

        graphics.fill(x, y, x + TILE_WIDTH, y + TILE_HEIGHT, border);
        graphics.fill(x + 1, y + 1, x + TILE_WIDTH - 1, y + TILE_HEIGHT - 1, bg);

        renderTilePreview(graphics, entry, x + 14, y + 8, 64, 84);

        String name = font.plainSubstrByWidth(entry.displayName(), TILE_WIDTH - 8);
        graphics.drawCenteredString(font, name, x + TILE_WIDTH / 2, y + TILE_HEIGHT - 18, 0xFFFFFF);
    }

    private void renderTilePreview(GuiGraphics graphics, ClientTattooRegistry.ClientTattooEntry entry, int x, int y, int previewWidth, int previewHeight) {
        ResourceLocation texture = ClientTattooTextureCache.getTexture(entry.id(), entry.sha256());

        if (texture == null) {
            graphics.fill(x, y, x + previewWidth, y + previewHeight, 0xFF202020);
            graphics.drawCenteredString(font, "Loading", x + previewWidth / 2, y + previewHeight / 2 - 4, 0xA0A0A0);
            return;
        }

        graphics.fill(x, y, x + previewWidth, y + previewHeight, 0xFF202020);
        graphics.blit(texture, x, y + 10, 0, 0, 64, 64, 64, 64);
    }

    private List<ClientTattooRegistry.ClientTattooEntry> visibleEntries() {
        return mode == Mode.APPROVED
                ? ClientTattooRegistry.approvedTattoos()
                : ClientPendingTattooRegistry.pendingTattoos();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && clickTile(mouseX, mouseY)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean clickTile(double mouseX, double mouseY) {
        List<ClientTattooRegistry.ClientTattooEntry> entries = visibleEntries();

        int columns = Math.max(1, (width - TILE_GAP) / (TILE_WIDTH + TILE_GAP));
        int contentWidth = columns * TILE_WIDTH + (columns - 1) * TILE_GAP;
        int startX = (width - contentWidth) / 2;

        for (int i = 0; i < entries.size(); i++) {
            int row = i / columns;
            int column = i % columns;

            int x = startX + column * (TILE_WIDTH + TILE_GAP);
            int y = TOP_MARGIN + row * (TILE_HEIGHT + TILE_GAP) - scrollOffset;

            if (mouseX >= x && mouseX < x + TILE_WIDTH && mouseY >= y && mouseY < y + TILE_HEIGHT) {
                selectedTattooId = entries.get(i).id();
                updateButtons();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxScroll = maxScroll();

        if (scrollY < 0) {
            scrollOffset = Math.min(maxScroll, scrollOffset + 24);
        } else if (scrollY > 0) {
            scrollOffset = Math.max(0, scrollOffset - 24);
        }

        return true;
    }

    private int maxScroll() {
        int entries = visibleEntries().size();
        int columns = Math.max(1, (width - TILE_GAP) / (TILE_WIDTH + TILE_GAP));
        int rows = (int) Math.ceil(entries / (double) columns);
        int contentHeight = rows * TILE_HEIGHT + Math.max(0, rows - 1) * TILE_GAP;
        int visibleHeight = height - TOP_MARGIN - BOTTOM_MARGIN;

        return Math.max(0, contentHeight - visibleHeight);
    }

    private enum Mode {
        APPROVED,
        PENDING
    }
}