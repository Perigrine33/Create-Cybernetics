package com.perigrine3.createcybernetics.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.surgery.RobosurgeonSlotMap;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class RobosurgeonScreen extends AbstractContainerScreen<RobosurgeonMenu> {
    private ViewMode viewMode = ViewMode.FULL_BODY;
    private final ModelViewer modelViewer = new ModelViewer();
    private final MarkerManager markerManager = new MarkerManager(MARKER_ICON);
    private int typingTicks = 0;
    private String animatedTitle = "";
    private static final int TYPE_DELAY = 4;
    private final Skeleton skeletonPreview = new Skeleton(EntityType.SKELETON, Minecraft.getInstance().level);
    private float modelFade = 0f;
    private final int backX = 4;
    private final int backY = 117;
    private final int backW = 20;
    private final int backH = 10;
    private static final int HUMANITY_BAR_WIDTH = 10;
    private static final int HUMANITY_BAR_HEIGHT = 75;
    private static final int WARNING_W = 12;
    private static final int WARNING_H = 12;
    private static final int WARNING_X = -15;
    private static final int WARNING_Y = 8;



    private final ItemStack renderSkin = new ItemStack(ModItems.BODYPART_SKIN.get());
    private final ItemStack renderMuscle = new ItemStack(ModItems.BODYPART_MUSCLE.get());
    private final ItemStack renderBone = new ItemStack(Items.BONE);


    // -----------------------
    // Resources
    // -----------------------
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
                    "textures/gui/robosurgeon/robosurgeon_gui.png");
    private static final ResourceLocation MARKER_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
                    "textures/gui/robosurgeon/robosurgeon_interface_marker.png");
    private static final ResourceLocation BACK_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
                    "textures/gui/robosurgeon/robosurgeon_interface_backbutton.png");
    private static final ResourceLocation SLOT_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
                    "textures/gui/robosurgeon/robosurgeon_interface_slot.png");
    private static final ResourceLocation REMOVALSLOT_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
                    "textures/gui/robosurgeon/robosurgeon_interface_slotmarkedforremoval.png");
    private static final ResourceLocation STAGEDINSTALLSLOT_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
                    "textures/gui/robosurgeon/robosurgeon_interface_stagedinstallslot.png");
    private static final ResourceLocation REMOVESLOT_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
                    "textures/gui/robosurgeon/robosurgeon_interface_removeslot.png");
    private static final ResourceLocation SLOTHOVER_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
                    "textures/gui/robosurgeon/robosurgeon_interface_slothover.png");
    private static final ResourceLocation WARNING_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID,
                    "textures/gui/robosurgeon/robosurgeon_interface_warning.png");


    public RobosurgeonScreen(RobosurgeonMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }


    // -----------------------
    // Init & Marker Setup
    // -----------------------
    @Override
    protected void init() {
        super.init();

        this.topPos -= 10;
        modelViewer.triggerZoomReset();

        registerMarkers();
        registerSlotBackgrounds();
        registerSlotViews();
    }

    private void registerMarkers() {
        markerManager.clear();
// Full body selection
        markerManager.add(new MarkerManager.Marker(-8, -78, ViewMode.FULL_BODY, ViewMode.HEAD, Component.translatable("gui.marker.head"), false));
        markerManager.add(new MarkerManager.Marker(-8, -52, ViewMode.FULL_BODY, ViewMode.TORSO, Component.translatable("gui.marker.torso"), false));
        markerManager.add(new MarkerManager.Marker(50, -52, ViewMode.FULL_BODY, ViewMode.SKIN, Component.translatable("gui.marker.skin"), false));
        markerManager.add(new MarkerManager.Marker(9, -55, ViewMode.FULL_BODY, ViewMode.LARM, Component.translatable("gui.marker.larm"), true));
        markerManager.add(new MarkerManager.Marker(-25, -55, ViewMode.FULL_BODY, ViewMode.RARM, Component.translatable("gui.marker.rarm"), true));
        markerManager.add(new MarkerManager.Marker(-2, -28, ViewMode.FULL_BODY, ViewMode.LLEG, Component.translatable("gui.marker.lleg"), true));
        markerManager.add(new MarkerManager.Marker(-14, -28, ViewMode.FULL_BODY, ViewMode.RLEG, Component.translatable("gui.marker.rleg"), true));
// Expanded head details
        markerManager.add(new MarkerManager.Marker(-35, -210, ViewMode.HEAD, ViewMode.BRAIN, Component.translatable("gui.marker.brain"), false));
        markerManager.add(new MarkerManager.Marker(-10, -197, ViewMode.HEAD, ViewMode.EYES, Component.translatable("gui.marker.eyes"), false));
        markerManager.add(new MarkerManager.Marker(15, -197, ViewMode.HEAD, ViewMode.EYES, Component.translatable("gui.marker.eyes"), false));
// Expanded torso details
        markerManager.add(new MarkerManager.Marker(0, -185, ViewMode.TORSO, ViewMode.HEART, Component.translatable("gui.marker.heart"), false));
        markerManager.add(new MarkerManager.Marker(-15, -170, ViewMode.TORSO, ViewMode.LUNGS, Component.translatable("gui.marker.lungs"), false));
        markerManager.add(new MarkerManager.Marker(-5, -135, ViewMode.TORSO, ViewMode.ORGANS, Component.translatable("gui.marker.organs"), false));
    }


    // -----------------------
    // Enum representing zoom categories
    // -----------------------
    public enum ViewMode {
        //               scale         rotate        animated        offsetY        offsetX
        FULL_BODY      (45,     true,   true,   0,      0),
        HEAD(FULL_BODY, 110,    false,  false,  150,    0),
        TORSO(FULL_BODY,135,    false,  false,  110,    0),
        SKIN(FULL_BODY, 75,     false,  true,   0,      0),
        LARM(FULL_BODY, 130,    true,   true,   110,    -40),
        RARM(FULL_BODY, 130,     true,   true,   110,    40),
        LLEG(FULL_BODY, 130,     true,   true,   20,    0),
        RLEG(FULL_BODY, 130,     true,   true,   20,    0),
        BRAIN(HEAD,     110,    false,  false,  150,    0),
        EYES(HEAD,      110,    false,  false,  150,    0),
        HEART(TORSO,    135,    false,  false,  110,    0),
        LUNGS(TORSO,    135,    false,  false,  110,    0),
        ORGANS(TORSO,   135,    false,  false,  110,    0);

        public final ViewMode parent;
        public final int baseScale;
        public final boolean allowRotation;
        public final boolean allowMarkerAnimation;

        public final int verticalOffset;
        public final int horizontalOffset;

        ViewMode(int scale, boolean rotate, boolean animate, int offsetY, int offsetX) {
            this.parent = this;
            this.baseScale = scale;
            this.allowRotation = rotate;
            this.allowMarkerAnimation = animate;
            this.verticalOffset = offsetY;
            this.horizontalOffset = offsetX;
        }

        ViewMode(ViewMode parent, int scale, boolean rotate, boolean animate, int offsetY, int offsetX) {
            this.parent = parent;
            this.baseScale = scale;
            this.allowRotation = rotate;
            this.allowMarkerAnimation = animate;
            this.verticalOffset = offsetY;
            this.horizontalOffset = offsetX;
        }
    }


    // -----------------------
    // Title Typing Animation
    // -----------------------
    private void updateTypingAnimation() {
        String full = this.title.getString();

        if (animatedTitle.length() < full.length()) {
            typingTicks++;

            if (typingTicks >= TYPE_DELAY) {
                typingTicks = 0;
                animatedTitle = full.substring(0, animatedTitle.length() + 1);
            }
        }
    }


    // -----------------------
    // Label Rendering
    // -----------------------
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        float scale = 0.75f;
        int titleWidth = this.font.width(animatedTitle);
        int scaledX = (int) ((this.imageWidth - (titleWidth * scale)) / 2);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1f);
        guiGraphics.drawString(this.font, animatedTitle, (int) (scaledX / scale), (int) (6 / scale), 65318, false);
        guiGraphics.pose().popPose();

        float invScale = 0.85f;
        int labelY = this.imageHeight - 94 + 4;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(invScale, invScale, 1f);
        guiGraphics.drawString(this.font, playerInventoryTitle, (int) (8 / invScale), (int) (labelY / invScale), 4210752, false);
        guiGraphics.pose().popPose();
    }


    // -----------------------
    // Background Rendering
    // -----------------------
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float p, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        guiGraphics.blit(GUI_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 176, 222);

        drawHumanityBar(guiGraphics);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        for (Slot slot : menu.slots) {
            if (!(slot instanceof RobosurgeonSlotItemHandler rsSlot)) continue;
            if (!isSlotVisible(rsSlot)) continue;

            int handlerIndex = rsSlot.getSlotIndex();

            int x = leftPos + rsSlot.x - 1;
            int y = topPos + rsSlot.y - 1;

            guiGraphics.blit(SLOT_ICON, x, y, 0, 0, 18, 18, 18, 18);

            if (menu.isMarkedForRemoval(handlerIndex)) {

                guiGraphics.setColor(1f, 1f, 1f, 0.6f);
                guiGraphics.blit(REMOVALSLOT_ICON, x, y, 0, 0, 18, 18, 18, 18);
                guiGraphics.blit(REMOVESLOT_ICON, x, y, 0, 0, 18, 18, 18, 18);
                guiGraphics.setColor(1f, 1f, 1f, 1f);

            } else if (menu.isStaged(handlerIndex)) {

                guiGraphics.setColor(1f, 1f, 1f, 0.6f);
                guiGraphics.blit(STAGEDINSTALLSLOT_ICON, x, y, 0, 0, 18, 18, 18, 18);
                guiGraphics.setColor(1f, 1f, 1f, 1f);
            }
        }

        RenderSystem.disableBlend();
    }


    // -----------------------
    // Humanity Bar
    // -----------------------
    private void drawHumanityBar(GuiGraphics gui) {
        Player player = minecraft.player;
        if (player == null) return;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        int humanity = calculatePreviewHumanity();
        int maxHumanity = getConfiguredBaseHumanity();
        maxHumanity = Math.max(1, maxHumanity);
        float percent = Math.max(0f, Math.min(1f, humanity / (float) maxHumanity));


        int x = leftPos + 10;
        int y = topPos + 30;

        // BACKGROUND
        gui.fill(x, y, x + HUMANITY_BAR_WIDTH, y + HUMANITY_BAR_HEIGHT, 0xFF202020);

        // FILLED HEIGHT (bottom-up)
        int filled = (int)(HUMANITY_BAR_HEIGHT * percent);
        int color = getHumanityColor(percent);

        gui.fill(x, y + (HUMANITY_BAR_HEIGHT - filled), x + HUMANITY_BAR_WIDTH, y + HUMANITY_BAR_HEIGHT, color);

        // LABEL
        gui.pose().pushPose();
        gui.pose().scale(0.5f, 0.5f, 1f);
        gui.drawString(minecraft.font, "" + humanity, (int)(x * 2), (int)((y - 7) * 2), 0xFF34D5EB, false);
        gui.pose().popPose();
    }

    private int getConfiguredBaseHumanity() {
        return com.perigrine3.createcybernetics.Config.HUMANITY.get();
    }

    private static final int NEUROPOZYNE_HUMANITY_PER_LEVEL = 25;

    private int getNeuropozyneBonusClient(Player player) {
        for (MobEffectInstance inst : player.getActiveEffects()) {
            if (inst.is(ModEffects.NEUROPOZYNE)) {
                return (inst.getAmplifier() + 1) * NEUROPOZYNE_HUMANITY_PER_LEVEL;
            }
        }
        return 0;
    }

    private int getHumanityColor(float percent) {
        if (percent > 0.66f) {
            return 0xFF2AFF00;  // green
        } else if (percent > 0.25f) {
            return 0xFFFFAA00;  // orange
        } else {
            return 0xFFFF0000;  // red
        }
    }

    private int calculatePreviewHumanity() {
        Player player = minecraft.player;
        if (player == null) return 100;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return 100;

        int humanity = data.getHumanityBase() + getNeuropozyneBonusClient(player);

        ItemStack[] guiStacks = new ItemStack[65];
        for (Slot s : menu.slots) {
            if (!(s instanceof RobosurgeonSlotItemHandler rs)) continue;
            int idx = rs.getSlotIndex();
            if (idx < 0 || idx >= guiStacks.length) continue;
            guiStacks[idx] = rs.getItem();
        }

        for (CyberwareSlot slotType : CyberwareSlot.values()) {
            for (int i = 0; i < slotType.size; i++) {

                int invIndex = RobosurgeonSlotMap.toInventoryIndex(slotType, i);
                if (invIndex < 0 || invIndex >= 65) continue;

                boolean staged = menu.isStaged(invIndex);
                boolean marked = menu.isMarkedForRemoval(invIndex);

                InstalledCyberware installed = data.get(slotType, i);
                ItemStack installedStack = (installed != null && installed.getItem() != null)
                        ? installed.getItem()
                        : ItemStack.EMPTY;

                if (marked && !installedStack.isEmpty() && installedStack.getItem() instanceof ICyberwareItem instItem) {
                    humanity += instItem.getHumanityCost();
                }

                if (staged) {
                    ItemStack stagedStack = guiStacks[invIndex];
                    if (!stagedStack.isEmpty() && stagedStack.getItem() instanceof ICyberwareItem stagedItem) {

                        if (!marked && !installedStack.isEmpty() && installedStack.getItem() instanceof ICyberwareItem instItem) {
                            humanity += instItem.getHumanityCost();
                        }

                        humanity -= stagedItem.getHumanityCost();
                    }
                }
            }
        }

        return Math.max(0, humanity);
    }

    // -----------------------
    // Slot Handling
    // -----------------------
    private record SlotBackground(int x, int y, ViewMode viewMode, ResourceLocation texture) {}
    private final List<SlotBackground> slotBackgrounds = new ArrayList<>();

    @Override
    protected void renderSlot(GuiGraphics gui, Slot slot) {
        if (!isSlotVisible(slot)) return;
        super.renderSlot(gui, slot);
    }

    private void registerSlotBackgrounds() {
        slotBackgrounds.clear();
        //BRAIN SLOTS   = 5
        {
            slotBackgrounds.add(new SlotBackground(151, 110, ViewMode.BRAIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 92, ViewMode.BRAIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 74, ViewMode.BRAIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 56, ViewMode.BRAIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 38, ViewMode.BRAIN, SLOT_ICON));
        }
        //EYE SLOTS     = 5
        {
            slotBackgrounds.add(new SlotBackground(151, 110, ViewMode.EYES, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 92, ViewMode.EYES, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 74, ViewMode.EYES, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 56, ViewMode.EYES, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 38, ViewMode.EYES, SLOT_ICON));
        }
        //HEART SLOTS   = 6
        {
            slotBackgrounds.add(new SlotBackground(151, 110, ViewMode.HEART, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 92, ViewMode.HEART, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 74, ViewMode.HEART, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 56, ViewMode.HEART, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 38, ViewMode.HEART, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 20, ViewMode.HEART, SLOT_ICON));
        }
        //LUNGS SLOTS   = 6
        {
            slotBackgrounds.add(new SlotBackground(151, 110, ViewMode.LUNGS, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 92, ViewMode.LUNGS, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 74, ViewMode.LUNGS, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 56, ViewMode.LUNGS, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 38, ViewMode.LUNGS, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 20, ViewMode.LUNGS, SLOT_ICON));
        }
        //ORGANS SLOTS  = 6
        {
            slotBackgrounds.add(new SlotBackground(151, 110, ViewMode.ORGANS, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 92, ViewMode.ORGANS, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 74, ViewMode.ORGANS, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 56, ViewMode.ORGANS, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 38, ViewMode.ORGANS, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(151, 20, ViewMode.ORGANS, SLOT_ICON));
        }
        //R ARM SLOTS   = 6
        {
            slotBackgrounds.add(new SlotBackground(43, 110, ViewMode.RARM, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(43, 92, ViewMode.RARM, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(43, 74, ViewMode.RARM, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(43, 56, ViewMode.RARM, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(43, 38, ViewMode.RARM, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(43, 20, ViewMode.RARM, SLOT_ICON));
        }
        //L ARM SLOTS   = 6
        {
            slotBackgrounds.add(new SlotBackground(115, 110, ViewMode.LARM, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(115, 92, ViewMode.LARM, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(115, 74, ViewMode.LARM, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(115, 56, ViewMode.LARM, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(115, 38, ViewMode.LARM, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(115, 20, ViewMode.LARM, SLOT_ICON));
        }
        //R LEG SLOTS   = 5
        {
            slotBackgrounds.add(new SlotBackground(43, 110, ViewMode.RLEG, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(43, 92, ViewMode.RLEG, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(43, 74, ViewMode.RLEG, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(43, 56, ViewMode.RLEG, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(43, 38, ViewMode.RLEG, SLOT_ICON));
        }
        //L LEG SLOTS   = 5
        {
            slotBackgrounds.add(new SlotBackground(115, 110, ViewMode.LLEG, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(115, 92, ViewMode.LLEG, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(115, 74, ViewMode.LLEG, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(115, 56, ViewMode.LLEG, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(115, 38, ViewMode.LLEG, SLOT_ICON));
        }
        //MUSCLE SLOTS  = 5
        {
            slotBackgrounds.add(new SlotBackground(79, 110, ViewMode.SKIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(79, 92, ViewMode.SKIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(79, 74, ViewMode.SKIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(79, 56, ViewMode.SKIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(79, 38, ViewMode.SKIN, SLOT_ICON));
        }
        //BONE SLOTS    = 5
        {
            slotBackgrounds.add(new SlotBackground(106, 110, ViewMode.SKIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(106, 92, ViewMode.SKIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(106, 74, ViewMode.SKIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(106, 56, ViewMode.SKIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(106, 38, ViewMode.SKIN, SLOT_ICON));
        }
        //SKIN SLOTS    = 5
        {
            slotBackgrounds.add(new SlotBackground(52, 110, ViewMode.SKIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(52, 92, ViewMode.SKIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(52, 74, ViewMode.SKIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(52, 56, ViewMode.SKIN, SLOT_ICON));
            slotBackgrounds.add(new SlotBackground(52, 38, ViewMode.SKIN, SLOT_ICON));
        }
    }

    private void drawSlotBackground(GuiGraphics gui, SlotBackground bg) {
        gui.blit(bg.texture, leftPos + bg.x, topPos + bg.y,
                0, 0, 18, 18, 18, 18);
    }

    private record SlotView(int slotIndex, ViewMode viewMode) {}
    private final List<SlotView> slotViews = new ArrayList<>();

    private boolean isSlotVisible(Slot slot) {
        if (!(slot instanceof RobosurgeonSlotItemHandler rsSlot)) return true;
        return isHandlerSlotVisible(rsSlot.getSlotIndex());
    }

    private void updateTeSlotActivity() {

        for (Slot slot : menu.slots) {
            if (!(slot instanceof RobosurgeonSlotItemHandler rsSlot)) continue;

            int handlerIndex = rsSlot.getSlotIndex(); // 0..64
            boolean visible = isHandlerSlotVisible(handlerIndex);
            rsSlot.setActiveFlag(visible);
        }
    }

    private boolean isHandlerSlotVisible(int handlerIndex) {
        for (SlotView view : slotViews) {
            if (view.slotIndex == handlerIndex) {
                return matchesView(view.viewMode);
            }
        }
        return true;
    }

    private boolean matchesView(ViewMode slotView) {
        ViewMode current = viewMode;
        while (true) {
            if (current == slotView) return true;
            if (current == current.parent) break;
            current = current.parent;
        }
        return false;
    }

    private void registerSlotViews() {
        slotViews.clear();
        slotViews.add(new SlotView(0, ViewMode.BRAIN));
        slotViews.add(new SlotView(1, ViewMode.BRAIN));
        slotViews.add(new SlotView(2, ViewMode.BRAIN));
        slotViews.add(new SlotView(3, ViewMode.BRAIN));
        slotViews.add(new SlotView(4, ViewMode.BRAIN));

        slotViews.add(new SlotView(5, ViewMode.EYES));
        slotViews.add(new SlotView(6, ViewMode.EYES));
        slotViews.add(new SlotView(7, ViewMode.EYES));
        slotViews.add(new SlotView(8, ViewMode.EYES));
        slotViews.add(new SlotView(9, ViewMode.EYES));

        slotViews.add(new SlotView(10, ViewMode.HEART));
        slotViews.add(new SlotView(11, ViewMode.HEART));
        slotViews.add(new SlotView(12, ViewMode.HEART));
        slotViews.add(new SlotView(13, ViewMode.HEART));
        slotViews.add(new SlotView(14, ViewMode.HEART));
        slotViews.add(new SlotView(15, ViewMode.HEART));

        slotViews.add(new SlotView(16, ViewMode.LUNGS));
        slotViews.add(new SlotView(17, ViewMode.LUNGS));
        slotViews.add(new SlotView(18, ViewMode.LUNGS));
        slotViews.add(new SlotView(19, ViewMode.LUNGS));
        slotViews.add(new SlotView(20, ViewMode.LUNGS));
        slotViews.add(new SlotView(21, ViewMode.LUNGS));

        slotViews.add(new SlotView(22, ViewMode.ORGANS));
        slotViews.add(new SlotView(23, ViewMode.ORGANS));
        slotViews.add(new SlotView(24, ViewMode.ORGANS));
        slotViews.add(new SlotView(25, ViewMode.ORGANS));
        slotViews.add(new SlotView(26, ViewMode.ORGANS));
        slotViews.add(new SlotView(27, ViewMode.ORGANS));

        slotViews.add(new SlotView(28, ViewMode.RARM));
        slotViews.add(new SlotView(29, ViewMode.RARM));
        slotViews.add(new SlotView(30, ViewMode.RARM));
        slotViews.add(new SlotView(31, ViewMode.RARM));
        slotViews.add(new SlotView(32, ViewMode.RARM));
        slotViews.add(new SlotView(33, ViewMode.RARM));

        slotViews.add(new SlotView(34, ViewMode.LARM));
        slotViews.add(new SlotView(35, ViewMode.LARM));
        slotViews.add(new SlotView(36, ViewMode.LARM));
        slotViews.add(new SlotView(37, ViewMode.LARM));
        slotViews.add(new SlotView(38, ViewMode.LARM));
        slotViews.add(new SlotView(39, ViewMode.LARM));

        slotViews.add(new SlotView(40, ViewMode.RLEG));
        slotViews.add(new SlotView(41, ViewMode.RLEG));
        slotViews.add(new SlotView(42, ViewMode.RLEG));
        slotViews.add(new SlotView(43, ViewMode.RLEG));
        slotViews.add(new SlotView(44, ViewMode.RLEG));

        slotViews.add(new SlotView(45, ViewMode.LLEG));
        slotViews.add(new SlotView(46, ViewMode.LLEG));
        slotViews.add(new SlotView(47, ViewMode.LLEG));
        slotViews.add(new SlotView(48, ViewMode.LLEG));
        slotViews.add(new SlotView(49, ViewMode.LLEG));

        slotViews.add(new SlotView(50, ViewMode.SKIN));
        slotViews.add(new SlotView(51, ViewMode.SKIN));
        slotViews.add(new SlotView(52, ViewMode.SKIN));
        slotViews.add(new SlotView(53, ViewMode.SKIN));
        slotViews.add(new SlotView(54, ViewMode.SKIN));

        slotViews.add(new SlotView(55, ViewMode.SKIN));
        slotViews.add(new SlotView(56, ViewMode.SKIN));
        slotViews.add(new SlotView(57, ViewMode.SKIN));
        slotViews.add(new SlotView(58, ViewMode.SKIN));
        slotViews.add(new SlotView(59, ViewMode.SKIN));

        slotViews.add(new SlotView(60, ViewMode.SKIN));
        slotViews.add(new SlotView(61, ViewMode.SKIN));
        slotViews.add(new SlotView(62, ViewMode.SKIN));
        slotViews.add(new SlotView(63, ViewMode.SKIN));
        slotViews.add(new SlotView(64, ViewMode.SKIN));
    }

    private ResourceLocation getSlotBackgroundTexture(Slot slot) {

        if (!(slot instanceof RobosurgeonSlotItemHandler rsSlot)) {
            return SLOT_ICON;
        }

        int teFirst = menu.getTeInventoryFirstSlotIndex();
        int handlerIndex = slot.index - teFirst;

        if (menu.isMarkedForRemoval(handlerIndex)) {
            return REMOVALSLOT_ICON;
        }

        if (menu.isStaged(handlerIndex)) {
            return SLOTHOVER_ICON;
        }

        if (menu.isInstalled(handlerIndex)) {
            return SLOT_ICON;
        }

        return SLOT_ICON;
    }


    // -----------------------
    // Apply STATIC scissor box
    // -----------------------
    private void applyScissor(GuiGraphics gui) {

        int y1 = topPos  + 15;   // TOP edge
        int x1 = leftPos + 3;   // LEFT edge
        int x2 = leftPos + 173;  // RIGHT edge
        int y2 = topPos  + 128;  // BOTTOM edge

        gui.enableScissor(x1, y1, x2, y2);
    }


    // -----------------------
    // Renderer Methods
    // -----------------------
    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        updateTeSlotActivity();

        int baseModelY = topPos + 105;

        updateTypingAnimation();
        renderBackground(gui, mouseX, mouseY, partialTick);
        super.render(gui, mouseX, mouseY, partialTick);

        if (viewMode.allowRotation) {
            modelViewer.updateRotation(mouseX);
        }

        int modelX = leftPos + 88 + viewMode.horizontalOffset;
        int modelY = baseModelY + viewMode.verticalOffset;
        boolean cropping = (viewMode != ViewMode.FULL_BODY);
        if (cropping) {
            applyScissor(gui);
        }

        boolean isHeadGroup =
                viewMode == ViewMode.HEAD ||
                        viewMode == ViewMode.BRAIN ||
                        viewMode == ViewMode.EYES;
        boolean isTorsoGroup =
                viewMode == ViewMode.TORSO ||
                        viewMode == ViewMode.HEART ||
                        viewMode == ViewMode.LUNGS ||
                        viewMode == ViewMode.ORGANS;
        boolean isLeftArm =
                viewMode == ViewMode.LARM;
        boolean isRightArm =
                viewMode == ViewMode.RARM;
        boolean isLeftLeg =
                viewMode == ViewMode.LLEG;
        boolean isRightLeg =
                viewMode == ViewMode.RLEG;
        boolean isSkin =
                viewMode == ViewMode.SKIN;


        if (isHeadGroup) {

            modelFade = Math.min(modelFade + 0.06f, 1f);
            renderHeadModeFade(gui, modelX, modelY, viewMode.baseScale, modelFade);

        }
        else if (isTorsoGroup) {

            modelFade = Math.min(modelFade + 0.06f, 1f);
            renderTorsoModeFade(gui, modelX, modelY, viewMode.baseScale, modelFade);

        }
        else if (isRightArm) {

            modelFade = Math.min(modelFade + 0.06f, 1f);
            renderRightArmModeFade(gui, modelX, modelY, viewMode.baseScale, modelFade);

        }
        else if (isLeftArm) {

            modelFade = Math.min(modelFade + 0.06f, 1f);
            renderLeftArmModeFade(gui, modelX, modelY, viewMode.baseScale, modelFade);

        }
        else if (isLeftLeg) {

            modelFade = Math.min(modelFade + 0.06f, 1f);
            renderLeftLegModeFade(gui, modelX, modelY, viewMode.baseScale, modelFade);

        }
        else if (isRightLeg) {

            modelFade = Math.min(modelFade + 0.06f, 1f);
            renderRightLegModeFade(gui, modelX, modelY, viewMode.baseScale, modelFade);

        }
        else if (isSkin) {

            modelFade = Math.min(modelFade + 0.06f, 1f);
            renderSkinModeFade(gui, modelX, modelY, viewMode.baseScale, modelFade);

        }
        else {

            modelFade = Math.max(modelFade - 0.06f, 0f);
            modelViewer.render(gui, modelX, modelY, viewMode.baseScale, minecraft.player, viewMode);

        }

        if (cropping) {
            gui.disableScissor();
        }

        if (viewMode != ViewMode.FULL_BODY) {

            gui.pose().pushPose();
            gui.pose().translate(0, 0, 300);

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            int bx = leftPos + backX;
            int by = topPos + backY;

            boolean hoveringBack =
                    mouseX >= bx && mouseX <= bx + backW &&
                            mouseY >= by && mouseY <= by + backH;

            float alpha = hoveringBack ? 1f : 0.35f;
            gui.setColor(1f, 1f, 1f, alpha);

            gui.blit(BACK_ICON, bx, by, 0, 0, backW, backH, backW, backH);

            gui.setColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
            gui.pose().popPose();
        }

        markerManager.render(gui, modelX, modelY, mouseX, mouseY,
                viewMode, modelViewer.getRotationPhase(), this.font);

        renderRemovalWarning(gui, mouseX, mouseY);
        this.renderTooltip(gui, mouseX, mouseY);


    }

    private void renderHeadModeFade(GuiGraphics gui, int x, int y, int scale, float fade) {
        Quaternionf spin = new Quaternionf()
                .rotateX((float)Math.toRadians(180))
                .rotateY((float)Math.toRadians(25));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        InventoryScreen.renderEntityInInventory(gui, x, y, scale, new Vector3f(), spin, null, skeletonPreview);
        RenderSystem.disableBlend();
    }



    private void renderTorsoModeFade(GuiGraphics gui, int x, int y, int scale, float fade) {
        Quaternionf spin = new Quaternionf()
                .rotateX((float)Math.toRadians(180))
                .rotateY((float)Math.toRadians(25));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        InventoryScreen.renderEntityInInventory(gui, x, y, scale, new Vector3f(), spin, null, skeletonPreview);
        RenderSystem.disableBlend();
    }

    private void renderRightArmModeFade(GuiGraphics gui, int x, int y, int scale, float fade) {
        Quaternionf spin = new Quaternionf()
                .rotateX((float)Math.toRadians(180))
                .rotateY((float)Math.toRadians(25));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        InventoryScreen.renderEntityInInventory(gui, x, y, scale, new Vector3f(), spin, null, skeletonPreview);
        RenderSystem.disableBlend();
    }

    private void renderLeftArmModeFade(GuiGraphics gui, int x, int y, int scale, float fade) {
        Quaternionf spin = new Quaternionf()
                .rotateX((float)Math.toRadians(180))
                .rotateY((float)Math.toRadians(25));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        InventoryScreen.renderEntityInInventory(gui, x, y, scale, new Vector3f(), spin, null, skeletonPreview);
        RenderSystem.disableBlend();
    }

    private void renderRightLegModeFade(GuiGraphics gui, int x, int y, int scale, float fade) {
        Quaternionf spin = new Quaternionf()
                .rotateX((float)Math.toRadians(180))
                .rotateY((float)Math.toRadians(25));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        InventoryScreen.renderEntityInInventory(gui, x, y, scale, new Vector3f(), spin, null, skeletonPreview);
        RenderSystem.disableBlend();
    }

    private void renderLeftLegModeFade(GuiGraphics gui, int x, int y, int scale, float fade) {
        Quaternionf spin = new Quaternionf()
                .rotateX((float)Math.toRadians(180))
                .rotateY((float)Math.toRadians(25));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        InventoryScreen.renderEntityInInventory(gui, x, y, scale, new Vector3f(), spin, null, skeletonPreview);
        RenderSystem.disableBlend();
    }

    private void renderSkinModeFade(GuiGraphics gui, int x, int y, int scale, float fade) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        gui.setColor(1f, 1f, 1f, 1f); // keep clean for UI

        gui.pose().pushPose();
        int baseX = x - 43;
        int baseY = y - 75;
        float itemScale = 1.5f;

        gui.pose().translate(baseX, baseY, 100f);
        gui.pose().scale(itemScale, itemScale, 1f);

        gui.renderItem(renderSkin, 2, -10);
        gui.pose().translate(20, 0, 0);
        gui.renderItem(renderMuscle, 0, -10);
        gui.pose().translate(20, 0, 0);
        gui.renderItem(renderBone, -2, -10);

        gui.pose().popPose();

        RenderSystem.disableBlend();
    }




    // -----------------------
    // Warning Icon Helper
    // -----------------------
    private int countMarkedForRemovalTotal() {
        int count = 0;
        for (SlotView view : slotViews) {
            if (menu.isMarkedForRemoval(view.slotIndex)) {
                count++;
            }
        }
        return count;
    }

    private int countMarkedForRemovalVisible() {
        int count = 0;
        for (SlotView view : slotViews) {
            if (!matchesView(view.viewMode)) continue;
            if (menu.isMarkedForRemoval(view.slotIndex)) {
                count++;
            }
        }

        return count;
    }

    private boolean hasDefaultOrganMarkedForRemoval() {
        for (Slot slot : menu.slots) {
            if (!(slot instanceof RobosurgeonSlotItemHandler rsSlot)) continue;

            int handlerIndex = rsSlot.getSlotIndex();
            if (!menu.isMarkedForRemoval(handlerIndex)) continue;

            ItemStack stack = rsSlot.getItem();
            if (!stack.isEmpty() && stack.is(ModTags.Items.BODY_PARTS)) {
                return true;
            }
        }
        return false;
    }

    private void renderRemovalWarning(GuiGraphics gui, int mouseX, int mouseY) {

        if (!hasDefaultOrganMarkedForRemoval()) return;

        int x = leftPos + WARNING_X;
        int y = topPos + WARNING_Y;

        gui.pose().pushPose();
        gui.pose().translate(0, 0, 400);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        gui.blit(WARNING_ICON, x, y, 0, 0, WARNING_W, WARNING_H, WARNING_W, WARNING_H);

        boolean hovering = isMouseOverRect(x, y, WARNING_W, WARNING_H, mouseX, mouseY);
        if (hovering) {

            List<Component> tip = List.of(
                    Component.translatable("gui.warning.title").withStyle(ChatFormatting.RED),
                    Component.translatable("gui.warning.desc1").withStyle(ChatFormatting.RED),
                    Component.translatable("gui.warning.desc2").withStyle(ChatFormatting.RED));

            gui.renderComponentTooltip(this.font, tip, mouseX, mouseY);
        }

        RenderSystem.disableBlend();
        gui.pose().popPose();
    }




    // -----------------------
    // Mouse Interaction
    // -----------------------
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (button == 0 && viewMode != ViewMode.FULL_BODY) {
            int bx = leftPos + backX;
            int by = topPos + backY;

            if (mouseX >= bx && mouseX <= bx + backW &&
                    mouseY >= by && mouseY <= by + backH) {

                if (minecraft.player != null) {
                    minecraft.player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.MASTER, 1f, 1f);
                }

                viewMode = (viewMode.parent != null) ? viewMode.parent : ViewMode.FULL_BODY;
                modelViewer.triggerZoomReset();
                return true;
            }
        }

        if (button == 0) {
            modelViewer.beginDrag(mouseX);
        }

        int modelX = leftPos + 88;
        int modelY = topPos + 105 + viewMode.verticalOffset;

        RobosurgeonScreen.ViewMode clicked =
                markerManager.tryClick(mouseX, mouseY,
                        modelX, modelY,
                        modelViewer.getRotationPhase(),
                        viewMode);

        if (clicked != null) {
            viewMode = clicked;
            modelViewer.triggerZoomReset();
            return true;
        }

        Slot hovered = this.getSlotUnderMouse();
        if (hovered != null && !isSlotVisible(hovered)) {
            return false;
        }


        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            modelViewer.endDrag();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean isMouseOverSlot(Slot slot, double mouseX, double mouseY) {
        int x = leftPos + slot.x;
        int y = topPos + slot.y;
        return mouseX >= x && mouseX < x + 16
                && mouseY >= y && mouseY < y + 16;
    }

    private boolean isMouseOverRect(int x, int y, int w, int h, int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + w
                && mouseY >= y && mouseY < y + h;
    }
}
