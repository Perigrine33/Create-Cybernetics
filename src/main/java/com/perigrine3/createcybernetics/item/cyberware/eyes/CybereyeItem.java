package com.perigrine3.createcybernetics.item.cyberware.eyes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.List;
import java.util.Set;

public class CybereyeItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int OVERLAY_ALPHA = 0xFF;

    public CybereyeItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.basecyberware_cybereye.energy")
                    .withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public boolean isDyeable(ItemStack stack, CyberwareSlot slot) {
        return slot == CyberwareSlot.EYES;
    }

    @Override
    public boolean isDyeable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnergyUsedPerTick(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return 5;
    }

    @Override
    public boolean requiresEnergyToFunction(LivingEntity entity, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.EYES);
    }

    @Override
    public boolean replacesOrgan() {
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.EYES);
    }

    @Override
    public int maxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) {
        return 3;
    }

    @Override
    public void onInstalled(LivingEntity entity) {
    }

    @Override
    public void onRemoved(LivingEntity entity) {
    }

    @Override
    public void onTick(LivingEntity entity) {
        ICyberwareItem.super.onTick(entity);
    }

    private static boolean enabledCybereyesInstalledAndUnpowered(PlayerCyberwareData data) {
        if (data == null) {
            return false;
        }

        InstalledCyberware[] arr = data.getAll().get(CyberwareSlot.EYES);
        if (arr == null) {
            return false;
        }

        boolean hasEnabledCybereyes = false;
        boolean hasPoweredEnabledCybereyes = false;

        for (int idx = 0; idx < arr.length; idx++) {
            InstalledCyberware installed = arr[idx];
            if (installed == null) {
                continue;
            }

            ItemStack stack = installed.getItem();
            if (stack == null || stack.isEmpty()) {
                continue;
            }

            if (!(stack.getItem() instanceof CybereyeItem)) {
                continue;
            }

            if (!data.isEnabled(CyberwareSlot.EYES, idx)) {
                continue;
            }

            hasEnabledCybereyes = true;

            if (installed.isPowered()) {
                hasPoweredEnabledCybereyes = true;
            }
        }

        return hasEnabledCybereyes && !hasPoweredEnabledCybereyes;
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static final class PowerFailOverlayHooks {
        private PowerFailOverlayHooks() {
        }

        @SubscribeEvent
        public static void onRenderGuiPost(RenderGuiEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                return;
            }

            if (mc.screen != null) {
                return;
            }

            if (!mc.player.hasData(ModAttachments.CYBERWARE)) {
                return;
            }

            PlayerCyberwareData data = mc.player.getData(ModAttachments.CYBERWARE);
            if (!enabledCybereyesInstalledAndUnpowered(data)) {
                return;
            }

            renderBlackOverlay(event.getGuiGraphics(), mc);
        }

        private static void renderBlackOverlay(GuiGraphics guiGraphics, Minecraft mc) {
            int w = mc.getWindow().getGuiScaledWidth();
            int h = mc.getWindow().getGuiScaledHeight();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            int argb = (OVERLAY_ALPHA << 24) | 0x000000;
            guiGraphics.fill(0, 0, w, h, argb);

            RenderSystem.disableBlend();
        }
    }
}