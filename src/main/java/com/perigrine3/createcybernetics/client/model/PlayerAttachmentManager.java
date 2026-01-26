package com.perigrine3.createcybernetics.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.awt.datatransfer.Clipboard;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PlayerAttachmentManager {

    private PlayerAttachmentManager() {}

    private static final Map<UUID, PlayerAttachmentState> STATES = new HashMap<>();

    // =========================
    // CLAWS
    // =========================
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

    // =========================
    // DRILL FIST
    // =========================
    private static final ResourceLocation DRILL_FIST_ITEM_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "armupgrades_drillfist");

    public static final ResourceLocation DRILL_FIST_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/drill_fist.png");

    private static DrillFistAttachmentModel DRILL_MODEL;

    public static DrillFistAttachmentModel drillModel() {
        if (DRILL_MODEL == null) {
            var baked = Minecraft.getInstance().getEntityModels().bakeLayer(DrillFistAttachmentModel.LAYER);
            DRILL_MODEL = new DrillFistAttachmentModel(baked);
        }
        return DRILL_MODEL;
    }

    private static Item drillFistItemOrNull() {
        if (!BuiltInRegistries.ITEM.containsKey(DRILL_FIST_ITEM_ID)) return null;
        Item item = BuiltInRegistries.ITEM.get(DRILL_FIST_ITEM_ID);
        return item == null ? null : item;
    }

    // =========================
    // OCELOT PAWS
    // =========================
    private static final ResourceLocation OCELOT_PAWS_ITEM_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "legupgrades_ocelotpaws");

    public static final ResourceLocation OCELOT_PAWS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/ocelot_paws.png");
    public static final ResourceLocation OCELOT_PAWS_DYED =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/ocelot_paws_dyed.png");

    private static OcelotPawsAttachmentModel PAWS_MODEL;

    public static OcelotPawsAttachmentModel pawsModel() {
        if (PAWS_MODEL == null) {
            var baked = Minecraft.getInstance().getEntityModels().bakeLayer(OcelotPawsAttachmentModel.LAYER);
            PAWS_MODEL = new OcelotPawsAttachmentModel(baked);
        }
        return PAWS_MODEL;
    }

    private static Item ocelotPawsItemOrNull() {
        if (!BuiltInRegistries.ITEM.containsKey(OCELOT_PAWS_ITEM_ID)) return null;
        Item item = BuiltInRegistries.ITEM.get(OCELOT_PAWS_ITEM_ID);
        return item == null ? null : item;
    }

    // =========================
    // CALF PROPELLER
    // =========================
    private static final ResourceLocation CALF_PROPELLER_ITEM_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "legupgrades_propellers");

    public static final ResourceLocation CALF_PROPELLER_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/calf_propeller.png");

    private static CalfPropellerAttachmentModel CALF_PROPELLER_MODEL;

    public static CalfPropellerAttachmentModel calfPropellerModel() {
        if (CALF_PROPELLER_MODEL == null) {
            var baked = Minecraft.getInstance().getEntityModels().bakeLayer(CalfPropellerAttachmentModel.LAYER);
            CALF_PROPELLER_MODEL = new CalfPropellerAttachmentModel(baked);
        }
        return CALF_PROPELLER_MODEL;
    }

    private static Item calfPropellerItemOrNull() {
        if (!BuiltInRegistries.ITEM.containsKey(CALF_PROPELLER_ITEM_ID)) return null;
        Item item = BuiltInRegistries.ITEM.get(CALF_PROPELLER_ITEM_ID);
        return item == null ? null : item;
    }

    // =========================
    // SPURS
    // =========================
    private static final ResourceLocation SPUR_ITEM_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "legupgrades_spurs");

    public static final ResourceLocation SPUR_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/spurs.png");

    private static SpursAttachmentModel SPUR_MODEL;

    public static SpursAttachmentModel spurModel() {
        if (SPUR_MODEL == null) {
            var baked = Minecraft.getInstance().getEntityModels().bakeLayer(SpursAttachmentModel.LAYER);
            SPUR_MODEL = new SpursAttachmentModel(baked);
        }
        return SPUR_MODEL;
    }

    private static Item spurItemOrNull() {
        if (!BuiltInRegistries.ITEM.containsKey(SPUR_ITEM_ID)) return null;
        Item item = BuiltInRegistries.ITEM.get(SPUR_ITEM_ID);
        return item == null ? null : item;
    }

    // =========================
    // GUARDIAN EYE
    // =========================
    private static final ResourceLocation GUARDIAN_EYE_ITEM_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "wetware_guardianeye");

    public static final ResourceLocation GUARDIAN_EYE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/guardian_eye.png");

    private static GuardianEyeAttachmentModel GUARDIAN_EYE_MODEL;

    public static GuardianEyeAttachmentModel guardianEyeModel() {
        if (GUARDIAN_EYE_MODEL == null) {
            var baked = Minecraft.getInstance().getEntityModels().bakeLayer(GuardianEyeAttachmentModel.LAYER);
            GUARDIAN_EYE_MODEL = new GuardianEyeAttachmentModel(baked);
        }
        return GUARDIAN_EYE_MODEL;
    }

    private static Item guardianEyeItemOrNull() {
        if (!BuiltInRegistries.ITEM.containsKey(GUARDIAN_EYE_ITEM_ID)) return null;
        Item item = BuiltInRegistries.ITEM.get(GUARDIAN_EYE_ITEM_ID);
        return item == null ? null : item;
    }

    // =========================
    // STATE BUILD
    // =========================
    public static PlayerAttachmentState getState(AbstractClientPlayer player) {
        if (!player.hasData(ModAttachments.CYBERWARE)) return null;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return null;

        PlayerAttachmentState state = STATES.computeIfAbsent(player.getUUID(), id -> new PlayerAttachmentState());
        state.clear();

        Item clawsItem = clawsItemOrNull();
        Item drillItem = drillFistItemOrNull();
        Item pawsItem = ocelotPawsItemOrNull();
        Item calfPropellerItem = calfPropellerItemOrNull();
        Item spurItem = spurItemOrNull();
        Item guardianEyeItem = guardianEyeItemOrNull();

        if (clawsItem == null && drillItem == null && pawsItem == null && calfPropellerItem == null && spurItem == null && guardianEyeItem == null) return state;

        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;
            AttachmentAnchor anchor = mapSlotToAnchor(slot);
            if (anchor == null) continue;
            for (int idx = 0; idx < arr.length; idx++) {
                InstalledCyberware cw = arr[idx];
                if (cw == null) continue;
                ItemStack stack = cw.getItem();
                if (stack == null || stack.isEmpty()) continue;
                if (!data.isEnabled(slot, idx)) continue;


                if (clawsItem != null && stack.is(clawsItem)) {
                    state.add(new ClawAttachment(anchor));
                    continue;
                }

                if (drillItem != null && stack.is(drillItem)) {
                    state.add(new DrillFistAttachment(anchor));
                }

                if (pawsItem != null && stack.is(pawsItem)) {
                    state.add(new OcelotPawsAttachment(anchor));
                }

                if (calfPropellerItem != null && stack.is(calfPropellerItem)) {
                    state.add(new CalfPropellerAttachment(anchor));
                }

                if (spurItem != null && stack.is(spurItem)) {
                    state.add(new SpursAttachment(anchor));
                }

                if (guardianEyeItem != null && stack.is(guardianEyeItem)) {
                    if (player.isCrouching()) {
                        state.add(new GuardianEyeAttachment(anchor));
                    }
                }
            }
        }

        return state;
    }

    private static AttachmentAnchor mapSlotToAnchor(CyberwareSlot slot) {
        if (slot == CyberwareSlot.LARM) return AttachmentAnchor.LEFT_ARM;
        if (slot == CyberwareSlot.RARM) return AttachmentAnchor.RIGHT_ARM;
        if (slot == CyberwareSlot.LLEG) return AttachmentAnchor.LEFT_LEG;
        if (slot == CyberwareSlot.RLEG) return AttachmentAnchor.RIGHT_LEG;
        if (slot == CyberwareSlot.EYES) return AttachmentAnchor.HEAD;
        return null;
    }

    // =========================
    // TRANSFORMS
    // =========================
    public static void applyKnuckleClawTransform(PoseStack pose, AttachmentAnchor armAnchor) {
        pose.translate(0.0F, 0.6, 0.0F);
        pose.translate(0.15F, 0.0F, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(90.0F));

        if (armAnchor == AttachmentAnchor.LEFT_ARM) {
            pose.translate(-0.0168F, 0.0F, -0.3F);
            pose.mulPose(Axis.ZP.rotationDegrees(10.0F));
            pose.mulPose(Axis.YP.rotationDegrees(-180.0F));
        } else if (armAnchor == AttachmentAnchor.RIGHT_ARM) {
            pose.translate(0.0172F, 0.0F, 0.0F);
            pose.mulPose(Axis.ZP.rotationDegrees(-10.0F));
        }

        pose.scale(1F, 1F, 1F);
    }

    public static void applyDrillFistTransform(PoseStack pose, AttachmentAnchor armAnchor) {
        pose.translate(0.05F, -0.15F, 0.39F);
        pose.mulPose(Axis.YP.rotationDegrees(90.0F));

        if (armAnchor == AttachmentAnchor.LEFT_ARM) {
            pose.translate(-0.02F, 0.0F, 0.0F);
            pose.mulPose(Axis.ZP.rotationDegrees(0.0F));
            pose.mulPose(Axis.YP.rotationDegrees(-180.0F));
        } else if (armAnchor == AttachmentAnchor.RIGHT_ARM) {
            pose.translate(0.8F, 0.0F, -0.1F);
            pose.mulPose(Axis.ZP.rotationDegrees(0.0F));
        }

        pose.scale(1.1F, 1.1F, 1.1F);
    }

    public static void applyOcelotPawsTransform(PoseStack pose, AttachmentAnchor legAnchor) {
        pose.translate(0.0F, 0.77F, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(0.0F));

        if (legAnchor == AttachmentAnchor.LEFT_LEG) {
            pose.translate(0.0F, 0.0F, 0.0F);
            pose.mulPose(Axis.ZP.rotationDegrees(0.0F));
            pose.mulPose(Axis.YP.rotationDegrees(0.0F));
        } else if (legAnchor == AttachmentAnchor.RIGHT_LEG) {
            pose.translate(0.0F, 0.0F, 0.0F);
            pose.mulPose(Axis.ZP.rotationDegrees(0.0F));
        }

        pose.scale(1, 1, 1);
    }

    public static void applyCalfPropellerTransform(PoseStack pose, AttachmentAnchor legAnchor) {
        pose.translate(0.0F, -0.7F, -0.45F);
        pose.mulPose(Axis.XN.rotationDegrees(-25.0F));
        pose.mulPose(Axis.YP.rotationDegrees(0.0F));

        if (legAnchor == AttachmentAnchor.LEFT_LEG) {
            pose.translate(0.0F, 0.0F, 0.0F);
            pose.mulPose(Axis.ZP.rotationDegrees(0.0F));
            pose.mulPose(Axis.YP.rotationDegrees(0.0F));
        } else if (legAnchor == AttachmentAnchor.RIGHT_LEG) {
            pose.translate(0.0F, 0.0F, 0.0F);
            pose.mulPose(Axis.ZP.rotationDegrees(0.0F));
        }

        pose.scale(1, 1, 1);
    }

    public static void applySpursTransform(PoseStack pose, AttachmentAnchor legAnchor) {
        pose.translate(0.0F, 0.0F, 0.0F);
        pose.mulPose(Axis.XN.rotationDegrees(0.0F));
        pose.mulPose(Axis.YP.rotationDegrees(0.0F));

        if (legAnchor == AttachmentAnchor.LEFT_LEG) {
            pose.translate(0.0F, 0.0F, 0.0F);
            pose.mulPose(Axis.ZP.rotationDegrees(0.0F));
            pose.mulPose(Axis.YP.rotationDegrees(0.0F));
        } else if (legAnchor == AttachmentAnchor.RIGHT_LEG) {
            pose.translate(0.0F, 0.0F, 0.0F);
            pose.mulPose(Axis.ZP.rotationDegrees(0.0F));
        }

        pose.scale(1, 1, 1);
    }

    public static void applyGuardianEyeTransform(PoseStack pose, AttachmentAnchor legAnchor) {
        pose.translate(0.0F, -0.25F, -0.205F);
        pose.mulPose(Axis.XN.rotationDegrees(0.0F));
        pose.mulPose(Axis.YP.rotationDegrees(0.0F));

        if (legAnchor == AttachmentAnchor.LEFT_LEG) {
            pose.translate(0.0F, 0.0F, 0.0F);
            pose.mulPose(Axis.ZP.rotationDegrees(0.0F));
            pose.mulPose(Axis.YP.rotationDegrees(0.0F));
        } else if (legAnchor == AttachmentAnchor.RIGHT_LEG) {
            pose.translate(0.0F, 0.0F, 0.0F);
            pose.mulPose(Axis.ZP.rotationDegrees(0.0F));
        }

        pose.scale(1, 1, 1);
    }

    // =========================
    // ATTACHMENTS
    // =========================
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

    private static final class DrillFistAttachment implements PlayerAttachment {
        private final AttachmentAnchor anchor;

        private DrillFistAttachment(AttachmentAnchor anchor) {
            this.anchor = anchor;
        }

        @Override
        public AttachmentAnchor anchor() {
            return anchor;
        }

        @Override
        public ResourceLocation texture(PlayerSkin.Model modelType) {
            return DRILL_FIST_TEXTURE;
        }

        @Override
        public Model model(PlayerSkin.Model modelType) {
            return drillModel();
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
            applyDrillFistTransform(poseStack, anchor);
        }
    }

    private static final class OcelotPawsAttachment implements PlayerAttachment {
        private final AttachmentAnchor anchor;

        // cached each frame from setupPose; avoids relying on Minecraft.getInstance().player
        private @Nullable AbstractClientPlayer renderPlayer;

        private OcelotPawsAttachment(AttachmentAnchor anchor) {
            this.anchor = anchor;
        }

        @Override
        public AttachmentAnchor anchor() {
            return anchor;
        }

        private CyberwareSlot slotForThisSide() {
            return (anchor == AttachmentAnchor.LEFT_LEG) ? CyberwareSlot.LLEG : CyberwareSlot.RLEG;
        }

        private @Nullable PlayerCyberwareData dataOrNull() {
            Player p = (renderPlayer != null) ? renderPlayer : Minecraft.getInstance().player;
            if (p == null || !p.hasData(ModAttachments.CYBERWARE)) return null;
            return p.getData(ModAttachments.CYBERWARE);
        }

        @Override
        public ResourceLocation texture(PlayerSkin.Model modelType) {
            PlayerCyberwareData data = dataOrNull();
            if (data == null) return OCELOT_PAWS_TEXTURE;

            Item item = ModItems.LEGUPGRADES_OCELOTPAWS.get();
            CyberwareSlot slot = slotForThisSide();

            return data.isDyed(item, slot) ? OCELOT_PAWS_DYED : OCELOT_PAWS_TEXTURE;
        }

        @Override
        public Model model(PlayerSkin.Model modelType) {
            return pawsModel();
        }

        @Override
        public int color() {
            PlayerCyberwareData data = dataOrNull();
            if (data == null) return 0xFFFFFFFF;

            Item item = ModItems.LEGUPGRADES_OCELOTPAWS.get();
            CyberwareSlot slot = slotForThisSide();

            if (!data.isDyed(item, slot)) return 0xFFFFFFFF;

            return data.dyeColor(item, slot);
        }

        @Override
        public boolean thirdPersonOnly() {
            return true;
        }

        @Override
        public void setupPose(
                PoseStack poseStack,
                AbstractClientPlayer player,
                PlayerModel<AbstractClientPlayer> parentModel,
                PlayerSkin.Model modelType,
                float partialTick
        ) {
            this.renderPlayer = player;
            applyOcelotPawsTransform(poseStack, anchor);
        }
    }


    private static final class CalfPropellerAttachment implements PlayerAttachment {
        private final AttachmentAnchor anchor;

        private CalfPropellerAttachment(AttachmentAnchor anchor) {
            this.anchor = anchor;
        }

        @Override
        public AttachmentAnchor anchor() {
            return anchor;
        }

        @Override
        public ResourceLocation texture(PlayerSkin.Model modelType) {
            return CALF_PROPELLER_TEXTURE;
        }

        @Override
        public Model model(PlayerSkin.Model modelType) {
            return calfPropellerModel();
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
            applyCalfPropellerTransform(poseStack, anchor);
        }
    }

    private static final class SpursAttachment implements PlayerAttachment {
        private final AttachmentAnchor anchor;

        private SpursAttachment(AttachmentAnchor anchor) {
            this.anchor = anchor;
        }

        @Override
        public AttachmentAnchor anchor() {
            return anchor;
        }

        @Override
        public ResourceLocation texture(PlayerSkin.Model modelType) {
            return SPUR_TEXTURE;
        }

        @Override
        public Model model(PlayerSkin.Model modelType) {
            return spurModel();
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
            applySpursTransform(poseStack, anchor);
        }
    }

    private static final class GuardianEyeAttachment implements PlayerAttachment {
        private final AttachmentAnchor anchor;

        private GuardianEyeAttachment(AttachmentAnchor anchor) {
            this.anchor = anchor;
        }

        @Override
        public AttachmentAnchor anchor() {
            return anchor;
        }

        @Override
        public ResourceLocation texture(PlayerSkin.Model modelType) {
            return GUARDIAN_EYE_TEXTURE;
        }

        @Override
        public Model model(PlayerSkin.Model modelType) {
            return guardianEyeModel();
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
            applyGuardianEyeTransform(poseStack, anchor);
        }
    }
}
