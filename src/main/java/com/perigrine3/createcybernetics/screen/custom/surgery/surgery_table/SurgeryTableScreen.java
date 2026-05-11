package com.perigrine3.createcybernetics.screen.custom.surgery.surgery_table;

import com.mojang.blaze3d.systems.RenderSystem;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.client.render.RobosurgeonPreviewOverlayContext;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.common.surgery.RobosurgeonSlotMap;
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.screen.custom.surgery.robosurgeon.RobosurgeonSlotItemHandler;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SurgeryTableScreen extends AbstractContainerScreen<SurgeryTableMenu> {
    private ViewMode viewMode = ViewMode.FULL_BODY;
    private final SurgeryTableModelViewer modelViewer = new SurgeryTableModelViewer();
    private final SurgeryTableMarkerManager markerManager = new SurgeryTableMarkerManager(MARKER_ICON);
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

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/robosurgeon_gui.png");
    private static final ResourceLocation MARKER_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/robosurgeon_interface_marker.png");
    private static final ResourceLocation BACK_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/robosurgeon_interface_backbutton.png");
    private static final ResourceLocation SLOT_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/robosurgeon_interface_slot.png");
    private static final ResourceLocation REMOVALSLOT_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/robosurgeon_interface_slotmarkedforremoval.png");
    private static final ResourceLocation STAGEDINSTALLSLOT_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/robosurgeon_interface_stagedinstallslot.png");
    private static final ResourceLocation REMOVESLOT_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/robosurgeon_interface_removeslot.png");
    private static final ResourceLocation SLOTHOVER_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/robosurgeon_interface_slothover.png");
    private static final ResourceLocation WARNING_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/robosurgeon_interface_warning.png");

    private static final ResourceLocation LINEAR_FRAME =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/linear_frame_robosurgeonoverlay.png");
    private static final ResourceLocation TITANIUM_SKULL =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/titanium_skull_robosurgeonoverlay.png");
    private static final ResourceLocation CAPACITOR_FRAME =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/capacitor_frame_robosurgeonoverlay.png");
    private static final ResourceLocation MARROW_BATTERY =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/marrow_battery_robosurgeonoverlay.png");
    private static final ResourceLocation BONELACING =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/bonelacing_robosurgeonoverlay.png");
    private static final ResourceLocation BONEFLEX =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/boneflex_robosurgeonoverlay.png");
    private static final ResourceLocation PIEZO =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/piezo_robosurgeonoverlay.png");
    private static final ResourceLocation DEPLOYABLE_ELYTRA =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/deployable_elytra_robosurgeonoverlay.png");
    private static final ResourceLocation SANDEVISTAN =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/sandevistan_robosurgeonoverlay.png");
    private static final ResourceLocation SPINAL_INJECTOR =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/robosurgeon/spinal_injector_robosurgeonoverlay.png");

    private static final int ROT_TOGGLE_W = 12;
    private static final int ROT_TOGGLE_H = 12;
    private static final int ROT_TOGGLE_PAD_X = 8;
    private static final int ROT_TOGGLE_PAD_Y = 2;

    private static final int MARKER_TOGGLE_W = ROT_TOGGLE_W;
    private static final int MARKER_TOGGLE_H = ROT_TOGGLE_H;
    private static final int MARKER_TOGGLE_SPACING_Y = -4;

    private static final float TOGGLE_LABEL_SCALE = 0.5f;
    private static final int TOGGLE_LABEL_GAP_PX = 3;
    private static final int TOGGLE_LABEL_COLOR = 0xFFE7E7E7;

    private static final ResourceLocation ROT_TOGGLE_ON_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/on_toggle.png");
    private static final ResourceLocation ROT_TOGGLE_OFF_ICON =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/gui/off_toggle.png");

    public SurgeryTableScreen(SurgeryTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void init() {
        super.init();

        this.topPos -= 10;
        modelViewer.triggerZoomReset();
        modelViewer.setAutoRotateEnabled(true);
        markerManager.setEnabled(true);

        registerMarkers();
        registerSlotBackgrounds();
        registerSlotViews();
    }

    private void registerMarkers() {
        markerManager.clear();
        markerManager.add(new SurgeryTableMarkerManager.Marker(-8, -78, ViewMode.FULL_BODY, ViewMode.HEAD, Component.translatable("gui.marker.head"), false));
        markerManager.add(new SurgeryTableMarkerManager.Marker(-8, -52, ViewMode.FULL_BODY, ViewMode.TORSO, Component.translatable("gui.marker.torso"), false));
        markerManager.add(new SurgeryTableMarkerManager.Marker(50, -52, ViewMode.FULL_BODY, ViewMode.SKIN, Component.translatable("gui.marker.skin"), false));
        markerManager.add(new SurgeryTableMarkerManager.Marker(9, -55, ViewMode.FULL_BODY, ViewMode.LARM, Component.translatable("gui.marker.larm"), true));
        markerManager.add(new SurgeryTableMarkerManager.Marker(-25, -55, ViewMode.FULL_BODY, ViewMode.RARM, Component.translatable("gui.marker.rarm"), true));
        markerManager.add(new SurgeryTableMarkerManager.Marker(-2, -28, ViewMode.FULL_BODY, ViewMode.LLEG, Component.translatable("gui.marker.lleg"), true));
        markerManager.add(new SurgeryTableMarkerManager.Marker(-14, -28, ViewMode.FULL_BODY, ViewMode.RLEG, Component.translatable("gui.marker.rleg"), true));

        markerManager.add(new SurgeryTableMarkerManager.Marker(-35, -210, ViewMode.HEAD, ViewMode.BRAIN, Component.translatable("gui.marker.brain"), false));
        markerManager.add(new SurgeryTableMarkerManager.Marker(-10, -197, ViewMode.HEAD, ViewMode.EYES, Component.translatable("gui.marker.eyes"), false));
        markerManager.add(new SurgeryTableMarkerManager.Marker(15, -197, ViewMode.HEAD, ViewMode.EYES, Component.translatable("gui.marker.eyes"), false));

        markerManager.add(new SurgeryTableMarkerManager.Marker(0, -185, ViewMode.TORSO, ViewMode.HEART, Component.translatable("gui.marker.heart"), false));
        markerManager.add(new SurgeryTableMarkerManager.Marker(-15, -170, ViewMode.TORSO, ViewMode.LUNGS, Component.translatable("gui.marker.lungs"), false));
        markerManager.add(new SurgeryTableMarkerManager.Marker(-5, -135, ViewMode.TORSO, ViewMode.ORGANS, Component.translatable("gui.marker.organs"), false));
    }

    public enum ViewMode {
        FULL_BODY(45, true, true, 0, 0),
        HEAD(FULL_BODY, 110, false, false, 150, 0),
        TORSO(FULL_BODY, 135, false, false, 110, 0),
        SKIN(FULL_BODY, 75, false, true, 0, 0),
        LARM(FULL_BODY, 130, true, true, 110, -40),
        RARM(FULL_BODY, 130, true, true, 110, 40),
        LLEG(FULL_BODY, 130, true, true, 20, 0),
        RLEG(FULL_BODY, 130, true, true, 20, 0),
        BRAIN(HEAD, 110, false, false, 150, 0),
        EYES(HEAD, 110, false, false, 150, 0),
        HEART(TORSO, 135, false, false, 110, 0),
        LUNGS(TORSO, 135, false, false, 110, 0),
        ORGANS(TORSO, 135, false, false, 110, 0);

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

    private void drawHumanityBar(GuiGraphics gui) {
        Player operator = minecraft.player;
        if (operator == null) return;

        Player patient = menu.getTargetPatient(operator);
        PlayerCyberwareData data = patient.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        int humanity = calculatePreviewHumanity();
        int maxHumanity = getConfiguredBaseHumanity();
        maxHumanity = Math.max(1, maxHumanity);

        float raw = humanity / (float) maxHumanity;
        float percent = Mth.clamp(raw, 0f, 1f);

        int x = leftPos + 10;
        int y = topPos + 30;

        gui.fill(x, y, x + HUMANITY_BAR_WIDTH, y + HUMANITY_BAR_HEIGHT, 0xFF202020);

        int filled = (int) (HUMANITY_BAR_HEIGHT * percent);
        int color = getHumanityColor(percent);

        gui.fill(x, y + (HUMANITY_BAR_HEIGHT - filled), x + HUMANITY_BAR_WIDTH, y + HUMANITY_BAR_HEIGHT, color);

        String text = Integer.toString(humanity);

        float labelScale = 0.5f;
        int textW = this.font.width(text);
        int textX = x + (HUMANITY_BAR_WIDTH / 2) - Math.round((textW * labelScale) / 2f);

        gui.pose().pushPose();
        gui.pose().scale(labelScale, labelScale, 1f);
        gui.drawString(minecraft.font, text, (int) (textX / labelScale), (int) ((y - 7) / labelScale), 0xFF34D5EB, false);
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
        if (percent > 0.66f) return 0xFF2AFF00;
        if (percent > 0.25f) return 0xFFFFAA00;
        return 0xFFFF0000;
    }

    private int calculatePreviewHumanity() {
        Player operator = minecraft.player;
        if (operator == null) return 100;

        Player patient = menu.getTargetPatient(operator);
        PlayerCyberwareData data = patient.getData(ModAttachments.CYBERWARE);
        if (data == null) return 100;

        int humanity = data.getHumanity(patient);
        humanity += getNeuropozyneBonusClient(patient);

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

        return humanity;
    }

    private record SlotBackground(int x, int y, ViewMode viewMode, ResourceLocation texture) {}
    private final List<SlotBackground> slotBackgrounds = new ArrayList<>();

    @Override
    protected void renderSlot(GuiGraphics gui, Slot slot) {
        if (!isSlotVisible(slot)) return;
        super.renderSlot(gui, slot);
    }

    private void registerSlotBackgrounds() {
        slotBackgrounds.clear();

        slotBackgrounds.add(new SlotBackground(151, 110, ViewMode.BRAIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 92, ViewMode.BRAIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 74, ViewMode.BRAIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 56, ViewMode.BRAIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 38, ViewMode.BRAIN, SLOT_ICON));

        slotBackgrounds.add(new SlotBackground(151, 110, ViewMode.EYES, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 92, ViewMode.EYES, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 74, ViewMode.EYES, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 56, ViewMode.EYES, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 38, ViewMode.EYES, SLOT_ICON));

        slotBackgrounds.add(new SlotBackground(151, 110, ViewMode.HEART, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 92, ViewMode.HEART, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 74, ViewMode.HEART, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 56, ViewMode.HEART, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 38, ViewMode.HEART, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 20, ViewMode.HEART, SLOT_ICON));

        slotBackgrounds.add(new SlotBackground(151, 110, ViewMode.LUNGS, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 92, ViewMode.LUNGS, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 74, ViewMode.LUNGS, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 56, ViewMode.LUNGS, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 38, ViewMode.LUNGS, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 20, ViewMode.LUNGS, SLOT_ICON));

        slotBackgrounds.add(new SlotBackground(151, 110, ViewMode.ORGANS, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 92, ViewMode.ORGANS, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 74, ViewMode.ORGANS, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 56, ViewMode.ORGANS, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 38, ViewMode.ORGANS, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(151, 20, ViewMode.ORGANS, SLOT_ICON));

        slotBackgrounds.add(new SlotBackground(43, 110, ViewMode.RARM, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(43, 92, ViewMode.RARM, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(43, 74, ViewMode.RARM, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(43, 56, ViewMode.RARM, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(43, 38, ViewMode.RARM, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(43, 20, ViewMode.RARM, SLOT_ICON));

        slotBackgrounds.add(new SlotBackground(115, 110, ViewMode.LARM, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(115, 92, ViewMode.LARM, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(115, 74, ViewMode.LARM, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(115, 56, ViewMode.LARM, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(115, 38, ViewMode.LARM, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(115, 20, ViewMode.LARM, SLOT_ICON));

        slotBackgrounds.add(new SlotBackground(43, 110, ViewMode.RLEG, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(43, 92, ViewMode.RLEG, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(43, 74, ViewMode.RLEG, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(43, 56, ViewMode.RLEG, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(43, 38, ViewMode.RLEG, SLOT_ICON));

        slotBackgrounds.add(new SlotBackground(115, 110, ViewMode.LLEG, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(115, 92, ViewMode.LLEG, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(115, 74, ViewMode.LLEG, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(115, 56, ViewMode.LLEG, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(115, 38, ViewMode.LLEG, SLOT_ICON));

        slotBackgrounds.add(new SlotBackground(79, 110, ViewMode.SKIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(79, 92, ViewMode.SKIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(79, 74, ViewMode.SKIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(79, 56, ViewMode.SKIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(79, 38, ViewMode.SKIN, SLOT_ICON));

        slotBackgrounds.add(new SlotBackground(106, 110, ViewMode.SKIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(106, 92, ViewMode.SKIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(106, 74, ViewMode.SKIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(106, 56, ViewMode.SKIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(106, 38, ViewMode.SKIN, SLOT_ICON));

        slotBackgrounds.add(new SlotBackground(52, 110, ViewMode.SKIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(52, 92, ViewMode.SKIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(52, 74, ViewMode.SKIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(52, 56, ViewMode.SKIN, SLOT_ICON));
        slotBackgrounds.add(new SlotBackground(52, 38, ViewMode.SKIN, SLOT_ICON));
    }

    private void drawSlotBackground(GuiGraphics gui, SlotBackground bg) {
        gui.blit(bg.texture, leftPos + bg.x, topPos + bg.y, 0, 0, 18, 18, 18, 18);
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

            int handlerIndex = rsSlot.getSlotIndex();
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

        for (int i = 0; i <= 4; i++) slotViews.add(new SlotView(i, ViewMode.BRAIN));
        for (int i = 5; i <= 9; i++) slotViews.add(new SlotView(i, ViewMode.EYES));
        for (int i = 10; i <= 15; i++) slotViews.add(new SlotView(i, ViewMode.HEART));
        for (int i = 16; i <= 21; i++) slotViews.add(new SlotView(i, ViewMode.LUNGS));
        for (int i = 22; i <= 27; i++) slotViews.add(new SlotView(i, ViewMode.ORGANS));
        for (int i = 28; i <= 33; i++) slotViews.add(new SlotView(i, ViewMode.RARM));
        for (int i = 34; i <= 39; i++) slotViews.add(new SlotView(i, ViewMode.LARM));
        for (int i = 40; i <= 44; i++) slotViews.add(new SlotView(i, ViewMode.RLEG));
        for (int i = 45; i <= 49; i++) slotViews.add(new SlotView(i, ViewMode.LLEG));
        for (int i = 50; i <= 54; i++) slotViews.add(new SlotView(i, ViewMode.SKIN));
        for (int i = 55; i <= 59; i++) slotViews.add(new SlotView(i, ViewMode.SKIN));
        for (int i = 60; i <= 64; i++) slotViews.add(new SlotView(i, ViewMode.SKIN));
    }

    private void applyScissor(GuiGraphics gui) {
        int y1 = topPos + 15;
        int x1 = leftPos + 3;
        int x2 = leftPos + 173;
        int y2 = topPos + 128;
        gui.enableScissor(x1, y1, x2, y2);
    }

    private void renderRotationToggle(GuiGraphics gui, int mouseX, int mouseY) {
        int x = leftPos + imageWidth - ROT_TOGGLE_PAD_X - ROT_TOGGLE_W;
        int y = topPos + ROT_TOGGLE_PAD_Y;

        boolean hovering = isMouseOverRect(x, y, ROT_TOGGLE_W, ROT_TOGGLE_H, mouseX, mouseY);

        gui.pose().pushPose();
        gui.pose().translate(0, 0, 350);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        float alpha = hovering ? 1f : 0.75f;
        gui.setColor(1f, 1f, 1f, alpha);

        ResourceLocation icon = modelViewer.isAutoRotateEnabled() ? ROT_TOGGLE_ON_ICON : ROT_TOGGLE_OFF_ICON;
        gui.blit(icon, x, y, 0, 0, ROT_TOGGLE_W, ROT_TOGGLE_H, ROT_TOGGLE_W, ROT_TOGGLE_H);

        gui.setColor(1f, 1f, 1f, 1f);

        RenderSystem.disableBlend();
        gui.pose().popPose();
    }

    private boolean clickRotationToggle(double mouseX, double mouseY) {
        int x = leftPos + imageWidth - ROT_TOGGLE_PAD_X - ROT_TOGGLE_W;
        int y = topPos + ROT_TOGGLE_PAD_Y;

        if (mouseX >= x && mouseX < x + ROT_TOGGLE_W && mouseY >= y && mouseY < y + ROT_TOGGLE_H) {
            boolean next = !modelViewer.isAutoRotateEnabled();
            modelViewer.setAutoRotateEnabled(next);

            if (minecraft.player != null) {
                minecraft.player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.MASTER, 1f, 1f);
            }
            return true;
        }

        return false;
    }

    private void renderMarkerToggle(GuiGraphics gui, int mouseX, int mouseY) {
        int x = leftPos + imageWidth - ROT_TOGGLE_PAD_X - ROT_TOGGLE_W;
        int y = topPos + ROT_TOGGLE_PAD_Y + ROT_TOGGLE_H + MARKER_TOGGLE_SPACING_Y;

        boolean hovering = isMouseOverRect(x, y, MARKER_TOGGLE_W, MARKER_TOGGLE_H, mouseX, mouseY);

        gui.pose().pushPose();
        gui.pose().translate(0, 0, 350);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        float alpha = hovering ? 1f : 0.75f;
        gui.setColor(1f, 1f, 1f, alpha);

        ResourceLocation icon = markerManager.isEnabled() ? ROT_TOGGLE_ON_ICON : ROT_TOGGLE_OFF_ICON;
        gui.blit(icon, x, y, 0, 0, MARKER_TOGGLE_W, MARKER_TOGGLE_H, MARKER_TOGGLE_W, MARKER_TOGGLE_H);

        gui.setColor(1f, 1f, 1f, 1f);

        RenderSystem.disableBlend();
        gui.pose().popPose();
    }

    private boolean clickMarkerToggle(double mouseX, double mouseY) {
        int x = leftPos + imageWidth - ROT_TOGGLE_PAD_X - ROT_TOGGLE_W;
        int y = topPos + ROT_TOGGLE_PAD_Y + ROT_TOGGLE_H + MARKER_TOGGLE_SPACING_Y;

        if (mouseX >= x && mouseX < x + MARKER_TOGGLE_W && mouseY >= y && mouseY < y + MARKER_TOGGLE_H) {
            markerManager.setEnabled(!markerManager.isEnabled());

            if (minecraft.player != null) {
                minecraft.player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.MASTER, 1f, 1f);
            }
            return true;
        }

        return false;
    }

    private void renderRotationToggleLabel(GuiGraphics gui) {
        int toggleX = leftPos + imageWidth - ROT_TOGGLE_PAD_X - ROT_TOGGLE_W;
        int toggleY = topPos + ROT_TOGGLE_PAD_Y;
        renderToggleLabel(gui, "ROTATION:", toggleX, toggleY, ROT_TOGGLE_H);
    }

    private void renderMarkerToggleLabel(GuiGraphics gui) {
        int toggleX = leftPos + imageWidth - ROT_TOGGLE_PAD_X - ROT_TOGGLE_W;
        int toggleY = topPos + ROT_TOGGLE_PAD_Y + ROT_TOGGLE_H + MARKER_TOGGLE_SPACING_Y;
        renderToggleLabel(gui, "MARKERS:", toggleX, toggleY, MARKER_TOGGLE_H);
    }

    private void renderToggleLabel(GuiGraphics gui, String text, int toggleX, int toggleY, int toggleH) {
        int textW = this.font.width(text);
        int x = toggleX - TOGGLE_LABEL_GAP_PX - Math.round(textW * TOGGLE_LABEL_SCALE);
        int y = toggleY + (toggleH / 2) - Math.round((this.font.lineHeight * TOGGLE_LABEL_SCALE) / 2f);

        gui.pose().pushPose();
        gui.pose().translate(0, 0, 350);
        gui.pose().scale(TOGGLE_LABEL_SCALE, TOGGLE_LABEL_SCALE, 1f);
        gui.drawString(this.font, text, (int) (x / TOGGLE_LABEL_SCALE), (int) (y / TOGGLE_LABEL_SCALE), TOGGLE_LABEL_COLOR, false);
        gui.pose().popPose();
    }

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

        boolean isHeadGroup = viewMode == ViewMode.HEAD || viewMode == ViewMode.BRAIN || viewMode == ViewMode.EYES;
        boolean isTorsoGroup = viewMode == ViewMode.TORSO || viewMode == ViewMode.HEART || viewMode == ViewMode.LUNGS || viewMode == ViewMode.ORGANS;
        boolean isLeftArm = viewMode == ViewMode.LARM;
        boolean isRightArm = viewMode == ViewMode.RARM;
        boolean isLeftLeg = viewMode == ViewMode.LLEG;
        boolean isRightLeg = viewMode == ViewMode.RLEG;
        boolean isSkin = viewMode == ViewMode.SKIN;

        if (isHeadGroup) {
            modelFade = Math.min(modelFade + 0.06f, 1f);
            renderHeadModeFade(gui, modelX, modelY, viewMode.baseScale, modelFade);
        } else if (isTorsoGroup) {
            modelFade = Math.min(modelFade + 0.06f, 1f);
            renderTorsoModeFade(gui, modelX, modelY, viewMode.baseScale, modelFade);
        } else if (isRightArm) {
            modelFade = Math.min(modelFade + 0.06f, 1f);
            renderRightArmModeFade(gui, modelX, modelY, viewMode.baseScale, modelFade);
        } else if (isLeftArm) {
            modelFade = Math.min(modelFade + 0.06f, 1f);
            renderLeftArmModeFade(gui, modelX, modelY, viewMode.baseScale, modelFade);
        } else if (isLeftLeg) {
            modelFade = Math.min(modelFade + 0.06f, 1f);
            renderLeftLegModeFade(gui, modelX, modelY, viewMode.baseScale, modelFade);
        } else if (isRightLeg) {
            modelFade = Math.min(modelFade + 0.06f, 1f);
            renderRightLegModeFade(gui, modelX, modelY, viewMode.baseScale, modelFade);
        } else if (isSkin) {
            modelFade = Math.min(modelFade + 0.06f, 1f);
            renderSkinModeFade(gui, modelX, modelY, viewMode.baseScale, modelFade);
        } else {
            modelFade = Math.max(modelFade - 0.06f, 0f);
            if (minecraft != null && minecraft.player != null) {
                modelViewer.render(gui, modelX, modelY, viewMode.baseScale, menu.getTargetPatient(minecraft.player), viewMode);
            }
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

            boolean hoveringBack = mouseX >= bx && mouseX <= bx + backW &&
                    mouseY >= by && mouseY <= by + backH;

            float alpha = hoveringBack ? 1f : 0.35f;
            gui.setColor(1f, 1f, 1f, alpha);

            gui.blit(BACK_ICON, bx, by, 0, 0, backW, backH, backW, backH);

            gui.setColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
            gui.pose().popPose();
        }

        markerManager.render(gui, modelX, modelY, mouseX, mouseY, viewMode, modelViewer.getRotationPhase(), this.font);

        renderRotationToggleLabel(gui);
        renderRotationToggle(gui, mouseX, mouseY);

        renderMarkerToggleLabel(gui);
        renderMarkerToggle(gui, mouseX, mouseY);

        renderRemovalWarning(gui, mouseX, mouseY);
        this.renderTooltip(gui, mouseX, mouseY);
    }

    private boolean hasInstalledCyberware(Player patient, Item item, CyberwareSlot... slots) {
        PlayerCyberwareData data = patient.getData(ModAttachments.CYBERWARE);
        return data != null && data.hasSpecificItem(item, slots);
    }

    private void addInstalledOverlay(List<RobosurgeonPreviewOverlayContext.Entry> overlays,
                                     ResourceLocation texture,
                                     float fade,
                                     Player patient,
                                     Item item,
                                     CyberwareSlot... slots) {
        if (texture == null || item == null || fade <= 0f) return;
        if (!hasInstalledCyberware(patient, item, slots)) return;

        overlays.add(RobosurgeonPreviewOverlayContext.entry(texture, fade));
    }

    private List<RobosurgeonPreviewOverlayContext.Entry> collectBoneOverlays(float fade) {
        List<RobosurgeonPreviewOverlayContext.Entry> overlays = new ArrayList<>();
        if (fade <= 0f || minecraft == null || minecraft.player == null) return overlays;

        Player patient = menu.getTargetPatient(minecraft.player);

        addInstalledOverlay(overlays, LINEAR_FRAME, fade, patient, ModItems.BASECYBERWARE_LINEARFRAME.get(), CyberwareSlot.BONE);
        addInstalledOverlay(overlays, TITANIUM_SKULL, fade, patient, ModItems.BONEUPGRADES_CYBERSKULL.get(), CyberwareSlot.BONE);
        addInstalledOverlay(overlays, CAPACITOR_FRAME, fade, patient, ModItems.BONEUPGRADES_CAPACITORFRAME.get(), CyberwareSlot.BONE);
        addInstalledOverlay(overlays, MARROW_BATTERY, fade, patient, ModItems.BONEUPGRADES_BONEBATTERY.get(), CyberwareSlot.BONE);
        addInstalledOverlay(overlays, BONELACING, fade, patient, ModItems.BONEUPGRADES_BONELACING.get(), CyberwareSlot.BONE);
        addInstalledOverlay(overlays, BONEFLEX, fade, patient, ModItems.BONEUPGRADES_BONEFLEX.get(), CyberwareSlot.BONE);
        addInstalledOverlay(overlays, PIEZO, fade, patient, ModItems.BONEUPGRADES_PIEZO.get(), CyberwareSlot.BONE);
        addInstalledOverlay(overlays, SPINAL_INJECTOR, fade, patient, ModItems.BONEUPGRADES_SPINALINJECTOR.get(), CyberwareSlot.BONE);
        addInstalledOverlay(overlays, SANDEVISTAN, fade, patient, ModItems.BONEUPGRADES_SANDEVISTAN.get(), CyberwareSlot.BONE);

        if (ModItems.BONEUPGRADES_ELYTRA != null) {
            addInstalledOverlay(overlays, DEPLOYABLE_ELYTRA, fade, patient, ModItems.BONEUPGRADES_ELYTRA.get(), CyberwareSlot.BONE);
        }

        return overlays;
    }

    private void renderSkeletonPreviewWithOverlays(GuiGraphics gui, int x, int y, int scale,
                                                   Quaternionf spin,
                                                   List<RobosurgeonPreviewOverlayContext.Entry> overlays) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RobosurgeonPreviewOverlayContext.begin(skeletonPreview, overlays);

        try {
            InventoryScreen.renderEntityInInventory(gui, x, y, scale, new Vector3f(), spin, null, skeletonPreview);
        } finally {
            RobosurgeonPreviewOverlayContext.end();
            RenderSystem.disableBlend();
        }
    }

    private void renderBoneOverlayModeFade(GuiGraphics gui, int x, int y, int scale, float fade) {
        Quaternionf spin = new Quaternionf()
                .rotateX((float) Math.toRadians(180))
                .rotateY((float) Math.toRadians(25));

        renderSkeletonPreviewWithOverlays(gui, x, y, scale, spin, collectBoneOverlays(fade));
    }

    private void renderHeadModeFade(GuiGraphics gui, int x, int y, int scale, float fade) {
        renderBoneOverlayModeFade(gui, x, y, scale, fade);
    }

    private void renderTorsoModeFade(GuiGraphics gui, int x, int y, int scale, float fade) {
        renderBoneOverlayModeFade(gui, x, y, scale, fade);
    }

    private void renderRightArmModeFade(GuiGraphics gui, int x, int y, int scale, float fade) {
        renderBoneOverlayModeFade(gui, x, y, scale, fade);
    }

    private void renderLeftArmModeFade(GuiGraphics gui, int x, int y, int scale, float fade) {
        renderBoneOverlayModeFade(gui, x, y, scale, fade);
    }

    private void renderRightLegModeFade(GuiGraphics gui, int x, int y, int scale, float fade) {
        renderBoneOverlayModeFade(gui, x, y, scale, fade);
    }

    private void renderLeftLegModeFade(GuiGraphics gui, int x, int y, int scale, float fade) {
        renderBoneOverlayModeFade(gui, x, y, scale, fade);
    }

    private void renderSkinModeFade(GuiGraphics gui, int x, int y, int scale, float fade) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        gui.setColor(1f, 1f, 1f, 1f);

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
            gui.renderTooltip(this.font, Component.translatable("gui.createcybernetics.robosurgeon.warning.default_organ_removal"), mouseX, mouseY);
        }

        RenderSystem.disableBlend();
        gui.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (clickRotationToggle(mouseX, mouseY)) {
            return true;
        }

        if (clickMarkerToggle(mouseX, mouseY)) {
            return true;
        }

        if (viewMode != ViewMode.FULL_BODY) {
            int bx = leftPos + backX;
            int by = topPos + backY;

            if (mouseX >= bx && mouseX <= bx + backW && mouseY >= by && mouseY <= by + backH) {
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

        SurgeryTableScreen.ViewMode clicked =
                markerManager.tryClick(mouseX, mouseY, modelX, modelY, modelViewer.getRotationPhase(), viewMode);

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
        return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;
    }

    private boolean isMouseOverRect(double x, double y, int w, int h, double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        Slot hovered = this.getSlotUnderMouse();
        if (hovered != null && !isSlotVisible(hovered)) {
            hovered = null;
        }

        if (hovered != null && hovered.hasItem() && isMouseOverSlot(hovered, x, y)) {
            ItemStack stack = hovered.getItem();
            List<Component> tooltip = getTooltipFromContainerItem(stack);
            guiGraphics.renderTooltip(this.font, tooltip, stack.getTooltipImage(), x, y);
            return;
        }

        super.renderTooltip(guiGraphics, x, y);
    }
}