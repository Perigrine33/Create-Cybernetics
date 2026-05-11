package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.cyberware.arm.ElectricArcCannonItem;
import com.perigrine3.createcybernetics.network.payload.ArcCannonFirePayload;
import com.perigrine3.createcybernetics.screen.custom.toggle_wheel.CyberwareToggleWheelScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class ArcCannonAttackInputHandler {
    private ArcCannonAttackInputHandler() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMouseButtonPre(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.getInstance();

        if (CyberwareToggleWheelScreen.isWheelOpen()) return;
        if (mc.player == null) return;
        if (mc.level == null) return;
        if (mc.screen != null) return;
        if (mc.player.isSpectator()) return;
        if (event.getAction() != GLFW.GLFW_PRESS) return;
        if (!mc.options.keyAttack.matchesMouse(event.getButton())) return;

        InteractionHand hand = chooseArcCannonHand(mc);
        if (hand == null) return;

        mc.player.swing(hand);
        PacketDistributor.sendToServer(new ArcCannonFirePayload());

        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onKey(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();

        if (CyberwareToggleWheelScreen.isWheelOpen()) return;
        if (mc.player == null) return;
        if (mc.level == null) return;
        if (mc.screen != null) return;
        if (mc.player.isSpectator()) return;
        if (event.getAction() != GLFW.GLFW_PRESS) return;
        if (!mc.options.keyAttack.matches(event.getKey(), event.getScanCode())) return;

        InteractionHand hand = chooseArcCannonHand(mc);
        if (hand == null) return;

        mc.player.swing(hand);
        PacketDistributor.sendToServer(new ArcCannonFirePayload());
    }

    private static InteractionHand chooseArcCannonHand(Minecraft mc) {
        if (mc.player == null) return null;
        if (!mc.player.hasData(ModAttachments.CYBERWARE)) return null;

        PlayerCyberwareData data = mc.player.getData(ModAttachments.CYBERWARE);
        if (data == null) return null;

        CyberwareSlot mainSlot = slotForHand(mc, InteractionHand.MAIN_HAND);
        CyberwareSlot offSlot = slotForHand(mc, InteractionHand.OFF_HAND);

        if (hasEnabledArcCannonInSlot(data, mainSlot)) {
            return InteractionHand.MAIN_HAND;
        }

        if (hasEnabledArcCannonInSlot(data, offSlot)) {
            return InteractionHand.OFF_HAND;
        }

        return null;
    }

    private static CyberwareSlot slotForHand(Minecraft mc, InteractionHand hand) {
        HumanoidArm arm = armForHand(mc, hand);
        return arm == HumanoidArm.LEFT ? CyberwareSlot.LARM : CyberwareSlot.RARM;
    }

    private static HumanoidArm armForHand(Minecraft mc, InteractionHand hand) {
        HumanoidArm main = mc.player.getMainArm();

        if (hand == InteractionHand.MAIN_HAND) {
            return main;
        }

        return main == HumanoidArm.RIGHT ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
    }

    private static boolean hasEnabledArcCannonInSlot(PlayerCyberwareData data, CyberwareSlot slot) {
        if (data == null || slot == null) return false;

        InstalledCyberware[] arr = data.getAll().get(slot);
        if (arr == null) return false;

        for (int i = 0; i < arr.length; i++) {
            InstalledCyberware installed = arr[i];
            if (installed == null) continue;

            ItemStack stack = installed.getItem();
            if (stack == null || stack.isEmpty()) continue;

            if (!(stack.getItem() instanceof ElectricArcCannonItem)) continue;
            if (!data.isEnabled(slot, i)) continue;

            return true;
        }

        return false;
    }
}