package com.perigrine3.createcybernetics.screen.custom.surgery;

import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryCancelPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryClickPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryEndPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryResultPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryRoundPayload;
import com.perigrine3.createcybernetics.network.payload.PlayerSurgeryStartPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

public class PlayerSurgeryMinigameScreen extends Screen {

    private UUID sessionId;
    private String patientName;
    private boolean implantBonus;

    private boolean countdownActive = true;
    private int countdownTicksTotal;
    private int countdownTicksElapsed;

    private boolean roundActive = false;
    private int roundIndex;
    private int roundCount;
    private int roundDurationTicks;
    private float successCenter;
    private float successWidth;
    private int localRoundTicks;
    private boolean clickedThisRound;

    private int resultTicks;
    private boolean lastSuccess;
    private int lastDamage;

    private boolean ending;
    private boolean completed;
    private boolean sentCancel;

    public PlayerSurgeryMinigameScreen(PlayerSurgeryStartPayload payload) {
        super(Component.literal("Player Surgery"));
        this.sessionId = payload.sessionId();
        this.patientName = payload.patientName();
        this.countdownTicksTotal = payload.countdownTicks();
        this.implantBonus = payload.implantBonus();
    }

    public boolean matches(UUID sessionId) {
        return this.sessionId != null && this.sessionId.equals(sessionId);
    }

    public void setRound(PlayerSurgeryRoundPayload payload) {
        this.countdownActive = false;
        this.roundActive = true;

        this.roundIndex = payload.roundIndex();
        this.roundCount = payload.roundCount();
        this.roundDurationTicks = payload.roundDurationTicks();
        this.successCenter = payload.successCenter();
        this.successWidth = payload.successWidth();
        this.implantBonus = payload.implantBonus();

        this.localRoundTicks = 0;
        this.clickedThisRound = false;
    }

    public void setResult(PlayerSurgeryResultPayload payload) {
        this.lastSuccess = payload.success();
        this.lastDamage = payload.damage();
        this.resultTicks = 18;
    }

    public void setEnd(PlayerSurgeryEndPayload payload) {
        this.ending = true;
        this.completed = payload.completed();
        this.roundActive = false;
        this.countdownActive = false;
        this.resultTicks = 30;
    }

    @Override
    public void tick() {
        super.tick();

        if (countdownActive) {
            countdownTicksElapsed++;
            if (countdownTicksElapsed > countdownTicksTotal) {
                countdownTicksElapsed = countdownTicksTotal;
            }
        }

        if (roundActive && !ending) {
            localRoundTicks++;
            if (localRoundTicks > roundDurationTicks) {
                localRoundTicks = roundDurationTicks;
            }
        }

        if (resultTicks > 0) {
            resultTicks--;

            if (ending && resultTicks == 0) {
                onClose();
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return countdownActive && !roundActive && !ending;
    }

    @Override
    public void onClose() {
        if (countdownActive && !roundActive && !ending && !sentCancel) {
            sentCancel = true;
            PacketDistributor.sendToServer(new PlayerSurgeryCancelPayload(sessionId));
        }

        super.onClose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && roundActive && !clickedThisRound && !ending) {
            clickedThisRound = true;
            PacketDistributor.sendToServer(new PlayerSurgeryClickPayload(sessionId));
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (shouldCloseOnEsc()) {
                return super.keyPressed(keyCode, scanCode, modifiers);
            }

            return true;
        }

        if ((keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)
                && roundActive
                && !clickedThisRound
                && !ending) {
            clickedThisRound = true;
            PacketDistributor.sendToServer(new PlayerSurgeryClickPayload(sessionId));
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, width, height, 0x66000000);

        int panelWidth = 240;
        int panelHeight = 104;
        int panelX = (width - panelWidth) / 2;
        int panelY = (height - panelHeight) / 2;

        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xF0101010);
        guiGraphics.fill(panelX + 1, panelY + 1, panelX + panelWidth - 1, panelY + panelHeight - 1, 0xE0181818);

        guiGraphics.drawString(font, "PLAYER SURGERY", panelX + 10, panelY + 8, 0xE6E6E6, false);
        guiGraphics.drawString(font, "Patient: " + patientName, panelX + 10, panelY + 22, 0xBDBDBD, false);

        if (implantBonus) {
            guiGraphics.drawString(font, "Assist implant active", panelX + 10, panelY + 36, 0x75FF75, false);
        } else {
            guiGraphics.drawString(font, "No assist implant", panelX + 10, panelY + 36, 0xAAAAAA, false);
        }

        if (countdownActive) {
            renderCountdown(guiGraphics, panelX, panelY, panelWidth);
        } else if (roundActive) {
            renderRound(guiGraphics, panelX, panelY, panelWidth, partialTick);
        }

        if (resultTicks > 0) {
            renderResult(guiGraphics, panelY);
        }
    }

    private void renderCountdown(GuiGraphics guiGraphics, int panelX, int panelY, int panelWidth) {
        int ticksLeft = Math.max(0, countdownTicksTotal - countdownTicksElapsed);
        int seconds = Math.max(1, (ticksLeft + 19) / 20);

        String text = "Starting in " + seconds + "...";
        int textW = font.width(text);
        guiGraphics.drawString(font, text, panelX + (panelWidth - textW) / 2, panelY + 66, 0xFFFFFF, false);

        String cancelText = "ESC to cancel";
        int cancelW = font.width(cancelText);
        guiGraphics.drawString(font, cancelText, panelX + (panelWidth - cancelW) / 2, panelY + 84, 0xAAAAAA, false);
    }

    private void renderRound(GuiGraphics guiGraphics, int panelX, int panelY, int panelWidth, float partialTick) {
        String roundText = "Round " + (roundIndex + 1) + " / " + roundCount;
        guiGraphics.drawString(font, roundText, panelX + 10, panelY + 52, 0xD8D8D8, false);

        int barX = panelX + 18;
        int barY = panelY + 72;
        int barW = panelWidth - 36;
        int barH = 12;

        guiGraphics.fill(barX, barY, barX + barW, barY + barH, 0xFF303030);

        int successX1 = barX + Math.round((successCenter - successWidth * 0.5F) * barW);
        int successX2 = barX + Math.round((successCenter + successWidth * 0.5F) * barW);
        guiGraphics.fill(successX1, barY, successX2, barY + barH, 0xFF2FA84F);

        float slider = getClientSliderPosition(partialTick);
        int sliderX = barX + Math.round(slider * barW);
        guiGraphics.fill(sliderX - 1, barY - 4, sliderX + 2, barY + barH + 4, 0xFFFFFFFF);

        String clickText = clickedThisRound ? "Waiting..." : "Click, SPACE, or ENTER to stop";
        guiGraphics.drawString(font, clickText, panelX + 10, panelY + 90, 0xD8D8D8, false);
    }

    private void renderResult(GuiGraphics guiGraphics, int panelY) {
        String resultText;
        int color;

        if (ending) {
            resultText = completed ? "Surgery complete" : "Surgery interrupted";
            color = completed ? 0x75FF75 : 0xFF7070;
        } else {
            resultText = lastSuccess ? "Success: 2 damage" : "Failure: " + lastDamage + " damage";
            color = lastSuccess ? 0x75FF75 : 0xFF7070;
        }

        int textW = font.width(resultText);
        guiGraphics.drawString(font, resultText, (width - textW) / 2, panelY - 18, color, false);
    }

    private float getClientSliderPosition(float partialTick) {
        float progress = Math.min(1.0F, Math.max(0.0F, (localRoundTicks + partialTick) / (float) roundDurationTicks));

        if ((roundIndex & 1) == 1) {
            return 1.0F - progress;
        }

        return progress;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}