package com.perigrine3.createcybernetics.compat.corpse;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class SkeletonCorpseOverlayRenderer {

    private static final ResourceLocation LINEAR_FRAME = ResourceLocation.fromNamespaceAndPath(
            CreateCybernetics.MODID, "textures/gui/robosurgeon/linear_frame_robosurgeonoverlay.png");
    private static final ResourceLocation TITANIUM_SKULL = ResourceLocation.fromNamespaceAndPath(
            CreateCybernetics.MODID, "textures/gui/robosurgeon/titanium_skull_robosurgeonoverlay.png");
    private static final ResourceLocation CAPACITOR_FRAME = ResourceLocation.fromNamespaceAndPath(
            CreateCybernetics.MODID, "textures/gui/robosurgeon/capacitor_frame_robosurgeonoverlay.png");
    private static final ResourceLocation MARROW_BATTERY = ResourceLocation.fromNamespaceAndPath(
            CreateCybernetics.MODID, "textures/gui/robosurgeon/marrow_battery_robosurgeonoverlay.png");
    private static final ResourceLocation BONELACING = ResourceLocation.fromNamespaceAndPath(
            CreateCybernetics.MODID, "textures/gui/robosurgeon/bonelacing_robosurgeonoverlay.png");
    private static final ResourceLocation BONEFLEX = ResourceLocation.fromNamespaceAndPath(
            CreateCybernetics.MODID, "textures/gui/robosurgeon/boneflex_robosurgeonoverlay.png");
    private static final ResourceLocation PIEZO = ResourceLocation.fromNamespaceAndPath(
            CreateCybernetics.MODID, "textures/gui/robosurgeon/piezo_robosurgeonoverlay.png");
    private static final ResourceLocation DEPLOYABLE_ELYTRA = ResourceLocation.fromNamespaceAndPath(
            CreateCybernetics.MODID, "textures/gui/robosurgeon/deployable_elytra_robosurgeonoverlay.png");
    private static final ResourceLocation SANDEVISTAN = ResourceLocation.fromNamespaceAndPath(
            CreateCybernetics.MODID, "textures/gui/robosurgeon/sandevistan_robosurgeonoverlay.png");
    private static final ResourceLocation SPINAL_INJECTOR = ResourceLocation.fromNamespaceAndPath(
            CreateCybernetics.MODID, "textures/gui/robosurgeon/spinal_injector_robosurgeonoverlay.png");

    private SkeletonCorpseOverlayRenderer() {
    }

    public static void render(
            SkeletonModel<?> model,
            PlayerCyberwareData data,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        if (model == null || data == null) return;

        if (hasItem(data, ModItems.BASECYBERWARE_LINEARFRAME.get())) {
            renderModelOverlay(model, poseStack, buffer, packedLight, LINEAR_FRAME);
        }
        if (hasItem(data, ModItems.BONEUPGRADES_CAPACITORFRAME.get())) {
            renderModelOverlay(model, poseStack, buffer, packedLight, CAPACITOR_FRAME);
        }
        if (hasItem(data, ModItems.BONEUPGRADES_BONEBATTERY.get())) {
            renderModelOverlay(model, poseStack, buffer, packedLight, MARROW_BATTERY);
        }
        if (hasItem(data, ModItems.BONEUPGRADES_BONELACING.get())) {
            renderModelOverlay(model, poseStack, buffer, packedLight, BONELACING);
        }
        if (hasItem(data, ModItems.BONEUPGRADES_BONEFLEX.get())) {
            renderModelOverlay(model, poseStack, buffer, packedLight, BONEFLEX);
        }
        if (hasItem(data, ModItems.BONEUPGRADES_PIEZO.get())) {
            renderModelOverlay(model, poseStack, buffer, packedLight, PIEZO);
        }
        if (ModItems.BONEUPGRADES_ELYTRA != null && hasItem(data, ModItems.BONEUPGRADES_ELYTRA.get())) {
            renderModelOverlay(model, poseStack, buffer, packedLight, DEPLOYABLE_ELYTRA);
        }
        if (hasItem(data, ModItems.BONEUPGRADES_SANDEVISTAN.get())) {
            renderModelOverlay(model, poseStack, buffer, packedLight, SANDEVISTAN);
        }
        if (hasItem(data, ModItems.BONEUPGRADES_SPINALINJECTOR.get())) {
            renderModelOverlay(model, poseStack, buffer, packedLight, SPINAL_INJECTOR);
        }
        if (hasItem(data, ModItems.BONEUPGRADES_CYBERSKULL.get())) {
            renderModelOverlay(model, poseStack, buffer, packedLight, TITANIUM_SKULL);
        }
    }

    private static void renderModelOverlay(
            SkeletonModel<?> model,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            ResourceLocation texture
    ) {
        model.renderToBuffer(
                poseStack,
                buffer.getBuffer(RenderType.entityTranslucent(texture)),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                0xFFFFFFFF
        );
    }

    private static boolean hasItem(PlayerCyberwareData data, Item item) {
        if (data == null || item == null) return false;

        for (CyberwareSlot slot : CyberwareSlot.values()) {
            InstalledCyberware[] arr = data.getAll().get(slot);
            if (arr == null) continue;

            for (InstalledCyberware installed : arr) {
                if (installed == null) continue;
                ItemStack stack = installed.getItem();
                if (stack == null || stack.isEmpty()) continue;
                if (stack.is(item)) return true;
            }
        }

        return false;
    }
}