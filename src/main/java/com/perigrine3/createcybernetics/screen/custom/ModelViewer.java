package com.perigrine3.createcybernetics.screen.custom;

import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ModelViewer {
    private float rotation = 180f;
    private float spinVelocity = 0f;
    private float introScale = 0f;
    private int itemTick = 0;
    private final int itemDisplayTime = 20;

    private boolean dragging = false;
    private int lastMouseX = 0;

    private static final float FRICTION = 0.92f;

    private final ItemStack renderSkin   = new ItemStack(ModItems.BODYPART_SKIN.get());
    private final ItemStack renderMuscle = new ItemStack(ModItems.BODYPART_MUSCLE.get());
    private final ItemStack renderBone   = new ItemStack(Items.BONE);

    public void beginDrag(double mouseX) {
        dragging = true;
        lastMouseX = (int) mouseX;
    }

    public void endDrag() {
        dragging = false;
    }

    public void updateRotation(int mouseX) {
        if (dragging) {
            int dx = mouseX - lastMouseX;
            spinVelocity = dx * 1.2f;
            rotation += spinVelocity;
            lastMouseX = mouseX;
        } else {
            spinVelocity *= FRICTION;
            rotation += spinVelocity;
            if (Math.abs(spinVelocity) < 0.1f) {
                rotation += 0.3f;
            }
        }
    }

    public Quaternionf getSpinQuaternion() {
        return new Quaternionf()
                .rotateX((float) Math.toRadians(180))
                .rotateY((float) Math.toRadians(rotation));
    }

    public void triggerZoomReset() {
        introScale = 0.4f;
        spinVelocity = 2f;
    }

    public float getRotationPhase() {
        float phase = (rotation % 360f) / 360f;
        phase = Math.abs(2f * phase - 1f);
        return phase * (0.92f + phase * 0.05f);
    }

    public void render(GuiGraphics gui, int modelX, int modelY, int baseScale, Player player, RobosurgeonScreen.ViewMode viewMode) {
        gui.enableScissor(modelX - 78, modelY - 85, modelX + 72, modelY + 75);

        if (introScale < 1f) {
            introScale += (1f - introScale) * 0.1f;
            if (introScale > 1f) introScale = 1f;
        }

        final float slideY = (1f - introScale) * 20f;

        Quaternionf spin = new Quaternionf()
                .rotateX((float) Math.toRadians(180))
                .rotateY((float) Math.toRadians(rotation));

        float b1 = player.yBodyRot, b2 = player.yBodyRotO;
        float h1 = player.yHeadRot, h2 = player.yHeadRotO;
        float yaw = player.getYRot(), yawO = player.yRotO;
        float pitch = player.getXRot(), pitchO = player.xRotO;

        player.yBodyRot = player.yBodyRotO = 180f;
        player.yHeadRot = player.yHeadRotO = 180f;
        player.setYRot(180f);
        player.yRotO = 180f;
        player.setXRot(0f);
        player.xRotO = 0f;

        float entityScale = player.getScale();
        if (!Float.isFinite(entityScale) || entityScale <= 0f) entityScale = 1f;

        float counterScale = 1f / entityScale;
        float uiScale = introScale * counterScale;
        uiScale = Mth.clamp(uiScale, 0.0001f, 1000f);

        final int entityRenderScale = baseScale;

        gui.pose().pushPose();
        gui.pose().translate(modelX, modelY + slideY, 0f);
        gui.pose().scale(uiScale, uiScale, 1f);
        gui.pose().translate(-modelX, -modelY, 0f);

        InventoryScreen.renderEntityInInventory(gui, modelX, modelY, entityRenderScale, new Vector3f(), spin, null, player);

        gui.pose().popPose();

        itemTick++;
        ItemStack[] itemsToCycle = new ItemStack[]{renderSkin, renderMuscle, renderBone};
        int currentIndex = (itemTick / itemDisplayTime) % itemsToCycle.length;
        ItemStack currentItem = itemsToCycle[currentIndex];

        gui.pose().pushPose();
        int itemX = modelX + 43;
        int itemY = modelY - 57;
        gui.pose().translate(itemX, itemY, 100f);
        float itemScale = 1.75f;
        gui.pose().scale(itemScale, itemScale, 1f);
        gui.renderItem(currentItem, 0, 0);
        gui.pose().popPose();

        player.yBodyRot = b1; player.yBodyRotO = b2;
        player.yHeadRot = h1; player.yHeadRotO = h2;
        player.setYRot(yaw); player.yRotO = yawO;
        player.setXRot(pitch); player.xRotO = pitchO;

        gui.disableScissor();
    }
}
