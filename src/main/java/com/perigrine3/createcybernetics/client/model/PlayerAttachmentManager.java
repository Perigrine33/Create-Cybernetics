package com.perigrine3.createcybernetics.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerAttachmentManager {

    private PlayerAttachmentManager() {}

    private static final Map<UUID, PlayerAttachmentState> STATES = new HashMap<>();

    private static final ResourceLocation CLAWS_ITEM_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "armupgrades_claws");
    public static final ResourceLocation CLAWS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/claws.png");

    private static ClawAttachmentModel CLAW_MODEL;

    public static ClawAttachmentModel clawsModel() {
        if (CLAW_MODEL == null) {
            var baked = Minecraft.getInstance().getEntityModels().bakeLayer(ClawAttachmentModel.LAYER);
            CLAW_MODEL = new ClawAttachmentModel(baked);
        }
        return CLAW_MODEL;
    }

    private static Item clawsItemOrNull() {
        if (!BuiltInRegistries.ITEM.containsKey(CLAWS_ITEM_ID)) return null;
        Item item = BuiltInRegistries.ITEM.get(CLAWS_ITEM_ID);
        return item == null ? null : item;
    }

    public static PlayerAttachmentState getState(AbstractClientPlayer player) {
        if (!player.hasData(ModAttachments.CYBERWARE)) return null;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return null;

        PlayerAttachmentState state = STATES.computeIfAbsent(player.getUUID(), id -> new PlayerAttachmentState());
        state.clear();

        Item clawsItem = clawsItemOrNull();
        if (clawsItem == null) return state;

        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int idx = 0; idx < arr.length; idx++) {
                InstalledCyberware cw = arr[idx];
                if (cw == null) continue;

                ItemStack stack = cw.getItem();
                if (stack == null || stack.isEmpty()) continue;
                if (!stack.is(clawsItem)) continue;

                if (!data.isEnabled(slot, idx)) continue;

                AttachmentAnchor anchor = mapSlotToAnchor(slot);
                if (anchor == null) continue;

                state.add(new ClawAttachment(anchor));
            }
        }

        return state;
    }

    private static AttachmentAnchor mapSlotToAnchor(CyberwareSlot slot) {
        if (slot == CyberwareSlot.LARM) return AttachmentAnchor.LEFT_ARM;
        if (slot == CyberwareSlot.RARM) return AttachmentAnchor.RIGHT_ARM;
        return null;
    }

    public static void applyKnuckleClawTransform(PoseStack pose, AttachmentAnchor armAnchor) {
        // 1) Move from shoulder pivot down to hand/knuckle area.
        pose.translate(0.0F, 0.6, 0.0F);
        // 2) Push forward so claws sit on/just in front of knuckles.
        pose.translate(0.15F, 0.0F, 0.);
        // 3) Rotate so they extend forward (depends on how the model was authored).
        pose.mulPose(Axis.YP.rotationDegrees(90.0F));
        // 4) Small side bias + slight splay per arm.
        if (armAnchor == AttachmentAnchor.LEFT_ARM) {
            pose.translate(-0.0168F, 0.0F, -0.3F);
            pose.mulPose(Axis.ZP.rotationDegrees(10.0F));
            pose.mulPose(Axis.YP.rotationDegrees(-180.0F));
        } else if (armAnchor == AttachmentAnchor.RIGHT_ARM) {
            pose.translate(0.0172F, 0.0F, 0.0F);
            pose.mulPose(Axis.ZP.rotationDegrees(-10.0F));
        }
        // 5) Scale to taste
        pose.scale(1F, 1F, 1F);
    }

    private static final class ClawAttachment implements PlayerAttachment {
        private final AttachmentAnchor anchor;
        private ClawAttachment(AttachmentAnchor anchor) {
            this.anchor = anchor;
        }
        @Override
        public AttachmentAnchor anchor() {
            return anchor;
        }
        @Override
        public ResourceLocation texture(PlayerSkin.Model modelType) {
            return CLAWS_TEXTURE;
        }
        @Override
        public Model model(PlayerSkin.Model modelType) {
            return clawsModel();
        }
        @Override
        public int color() {
            return 0xFFFFFFFF;
        }
        @Override
        public boolean thirdPersonOnly() {
            return true;
        }
        @Override
        public void setupPose(PoseStack poseStack, AbstractClientPlayer player, PlayerModel<AbstractClientPlayer> parentModel, PlayerSkin.Model modelType, float partialTick) {
            applyKnuckleClawTransform(poseStack, anchor);
        }
    }
}
