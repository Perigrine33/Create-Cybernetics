package com.perigrine3.createcybernetics.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;
import java.util.Locale;

public final class CyberentityAttachmentManager {

    private enum RigType {
        DEFAULT_HUMANOID,
        SMASHER
    }

    private static final ResourceLocation CLAWS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/claws.png");
    private static final ResourceLocation DRILL_FIST_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/drill_fist.png");
    private static final ResourceLocation OCELOT_PAWS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/ocelot_paws.png");
    private static final ResourceLocation CALF_PROPELLER_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/calf_propeller.png");
    private static final ResourceLocation SPURS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/spurs.png");
    private static final ResourceLocation GUARDIAN_EYE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/guardian_eye.png");
    private static final ResourceLocation WARDEN_ANTLERS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/warden_antlers.png");
    private static final ResourceLocation NEURAL_PROCESSOR_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "textures/entity/neural_processor_port.png");

    private final Model clawsModel;
    private final Model drillFistModel;
    private final Model ocelotPawsModel;
    private final Model calfPropellerModel;
    private final Model spursModel;
    private final Model guardianEyeModel;
    private final Model wardenAntlersModel;
    private final Model neuralProcessorModel;

    public CyberentityAttachmentManager(net.minecraft.client.renderer.entity.EntityRendererProvider.Context context) {
        this.clawsModel = new ClawAttachmentModel(context.bakeLayer(ClawAttachmentModel.LAYER));
        this.drillFistModel = new DrillFistAttachmentModel(context.bakeLayer(DrillFistAttachmentModel.LAYER));
        this.ocelotPawsModel = new OcelotPawsAttachmentModel(context.bakeLayer(OcelotPawsAttachmentModel.LAYER));
        this.calfPropellerModel = new CalfPropellerAttachmentModel(context.bakeLayer(CalfPropellerAttachmentModel.LAYER));
        this.spursModel = new SpursAttachmentModel(context.bakeLayer(SpursAttachmentModel.LAYER));
        this.guardianEyeModel = new GuardianEyeAttachmentModel(context.bakeLayer(GuardianEyeAttachmentModel.LAYER));
        this.wardenAntlersModel = new WardenAntlersAttachmentModel(context.bakeLayer(WardenAntlersAttachmentModel.LAYER));
        this.neuralProcessorModel = new NeuralProcessorAttachmentModel(context.bakeLayer(NeuralProcessorAttachmentModel.LAYER));
    }

    public CyberentityAttachmentState buildState(LivingEntity entity) {
        CyberentityAttachmentState state = new CyberentityAttachmentState();

        if (entity == null) return state;
        if (!entity.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) return state;

        EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        if (data == null) return state;

        RigType rigType = getRigType(entity);

        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware installed = arr[i];
                if (installed == null) continue;

                ItemStack stack = installed.getItem();
                if (stack == null || stack.isEmpty()) continue;
                if (!data.isEnabled(slot, i)) continue;

                CyberentityAttachment attachment = createAttachment(slot, stack, rigType);
                if (attachment != null) {
                    state.add(attachment);
                }
            }
        }

        return state;
    }

    public <T extends LivingEntity, M extends EntityModel<T>> void renderAttachments(
            T entity,
            M parentModel,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
        CyberentityAttachmentState state = buildState(entity);
        if (state.isEmpty()) return;

        for (CyberentityAttachment attachment : state.all()) {
            ModelPart anchorPart = resolveAnchorPart(entity, parentModel, attachment.anchor());
            if (anchorPart == null) continue;

            poseStack.pushPose();
            try {
                anchorPart.translateAndRotate(poseStack);

                if (attachment.tuner() != null) {
                    attachment.tuner().apply(poseStack, entity);
                }

                int light = attachment.fullBright() ? 0xF000F0 : packedLight;
                var vc = buffer.getBuffer(RenderType.entityCutoutNoCull(attachment.texture()));
                attachment.model().renderToBuffer(
                        poseStack,
                        vc,
                        light,
                        OverlayTexture.NO_OVERLAY,
                        0xFFFFFFFF
                );
            } finally {
                poseStack.popPose();
            }
        }
    }

    private CyberentityAttachment createAttachment(CyberwareSlot slot, ItemStack stack, RigType rigType) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (key == null) return null;
        if (!CreateCybernetics.MODID.equals(key.getNamespace())) return null;

        String path = key.getPath();
        AttachmentAnchor anchor = mapSlotToAnchor(slot);
        if (anchor == null) return null;

        return switch (path) {
            case "armupgrades_claws" -> new CyberentityAttachment(
                    anchor,
                    clawsModel,
                    CLAWS_TEXTURE,
                    0xFFFFFFFF,
                    false,
                    rigType == RigType.SMASHER
                            ? (pose, living) -> applyKnuckleClawTransformSmasher(pose, anchor)
                            : (pose, living) -> applyKnuckleClawTransformDefault(pose, anchor)
            );

            case "armupgrades_drillfist" -> new CyberentityAttachment(
                    anchor,
                    drillFistModel,
                    DRILL_FIST_TEXTURE,
                    0xFFFFFFFF,
                    false,
                    rigType == RigType.SMASHER
                            ? (pose, living) -> applyDrillFistTransformSmasher(pose, anchor)
                            : (pose, living) -> applyDrillFistTransformDefault(pose, anchor)
            );

            case "legupgrades_ocelotpaws" -> new CyberentityAttachment(
                    anchor,
                    ocelotPawsModel,
                    OCELOT_PAWS_TEXTURE,
                    0xFFFFFFFF,
                    false,
                    rigType == RigType.SMASHER
                            ? (pose, living) -> applyOcelotPawsTransformSmasher(pose, anchor)
                            : (pose, living) -> applyOcelotPawsTransformDefault(pose, anchor)
            );

            case "legupgrades_propellers" -> new CyberentityAttachment(
                    anchor,
                    calfPropellerModel,
                    CALF_PROPELLER_TEXTURE,
                    0xFFFFFFFF,
                    false,
                    rigType == RigType.SMASHER
                            ? (pose, living) -> applyCalfPropellerTransformSmasher(pose, anchor)
                            : (pose, living) -> applyCalfPropellerTransformDefault(pose, anchor)
            );

            case "legupgrades_spurs" -> new CyberentityAttachment(
                    anchor,
                    spursModel,
                    SPURS_TEXTURE,
                    0xFFFFFFFF,
                    false,
                    rigType == RigType.SMASHER
                            ? (pose, living) -> applySpursTransformSmasher(pose, anchor)
                            : (pose, living) -> applySpursTransformDefault(pose, anchor)
            );

            case "wetware_guardianeye" -> new CyberentityAttachment(
                    anchor,
                    guardianEyeModel,
                    GUARDIAN_EYE_TEXTURE,
                    0xFFFFFFFF,
                    false,
                    rigType == RigType.SMASHER
                            ? (pose, living) -> applyGuardianEyeTransformSmasher(pose, anchor)
                            : (pose, living) -> applyGuardianEyeTransformDefault(pose, anchor)
            );

            case "wetware_wardenantlers", "sculkedupgrades_wardenantlers" -> new CyberentityAttachment(
                    anchor,
                    wardenAntlersModel,
                    WARDEN_ANTLERS_TEXTURE,
                    0xFFFFFFFF,
                    false,
                    rigType == RigType.SMASHER
                            ? (pose, living) -> applyWardenAntlersTransformSmasher(pose, anchor)
                            : (pose, living) -> applyWardenAntlersTransformDefault(pose, anchor)
            );

            case "brainupgrades_neuralprocessor" -> new CyberentityAttachment(
                    anchor,
                    neuralProcessorModel,
                    NEURAL_PROCESSOR_TEXTURE,
                    0xFFFFFFFF,
                    false,
                    rigType == RigType.SMASHER
                            ? (pose, living) -> applyNeuralProcessorTransformSmasher(pose, anchor)
                            : (pose, living) -> applyNeuralProcessorTransformDefault(pose, anchor)
            );

            default -> null;
        };
    }

    private AttachmentAnchor mapSlotToAnchor(CyberwareSlot slot) {
        if (slot == CyberwareSlot.LARM) return AttachmentAnchor.LEFT_ARM;
        if (slot == CyberwareSlot.RARM) return AttachmentAnchor.RIGHT_ARM;
        if (slot == CyberwareSlot.LLEG) return AttachmentAnchor.LEFT_LEG;
        if (slot == CyberwareSlot.RLEG) return AttachmentAnchor.RIGHT_LEG;
        if (slot == CyberwareSlot.ORGANS) return AttachmentAnchor.BODY;
        if (slot == CyberwareSlot.HEART) return AttachmentAnchor.BODY;
        if (slot == CyberwareSlot.LUNGS) return AttachmentAnchor.BODY;
        if (slot == CyberwareSlot.EYES) return AttachmentAnchor.HEAD;
        if (slot == CyberwareSlot.BRAIN) return AttachmentAnchor.HEAD;
        return null;
    }

    private <T extends LivingEntity, M extends EntityModel<T>> ModelPart resolveAnchorPart(
            T entity,
            M parentModel,
            AttachmentAnchor anchor
    ) {
        if (parentModel instanceof HumanoidModel<?> humanoid) {
            return resolveHumanoidAnchor(humanoid, anchor);
        }

        if (isSmasher(entity)) {
            return resolveSmasherAnchor(parentModel, anchor);
        }

        return resolveGenericByField(parentModel, anchor);
    }

    private ModelPart resolveHumanoidAnchor(HumanoidModel<?> model, AttachmentAnchor anchor) {
        return switch (anchor) {
            case HEAD -> model.head;
            case BODY -> model.body;
            case LEFT_ARM -> model.leftArm;
            case RIGHT_ARM -> model.rightArm;
            case LEFT_LEG -> model.leftLeg;
            case RIGHT_LEG -> model.rightLeg;
        };
    }

    private <T extends LivingEntity, M extends EntityModel<T>> ModelPart resolveSmasherAnchor(M parentModel, AttachmentAnchor anchor) {
        ModelPart direct = switch (anchor) {
            case HEAD -> findModelPartByAnyName(parentModel, "head2", "head", "helmet", "skull");
            case BODY -> findModelPartByAnyName(parentModel, "body", "Torso", "torso", "chest", "upperBody");
            case LEFT_ARM -> findModelPartByAnyName(parentModel, "arm0", "leftArm", "larm", "armLeft");
            case RIGHT_ARM -> findModelPartByAnyName(parentModel, "arm1", "rightArm", "rarm", "armRight");
            case LEFT_LEG -> findModelPartByAnyName(parentModel, "leg0", "leftLeg", "lleg", "legLeft");
            case RIGHT_LEG -> findModelPartByAnyName(parentModel, "leg1", "rightLeg", "rleg", "legRight");
        };

        if (direct != null) return direct;
        return resolveGenericByField(parentModel, anchor);
    }

    private <T extends LivingEntity, M extends EntityModel<T>> ModelPart resolveGenericByField(M parentModel, AttachmentAnchor anchor) {
        return switch (anchor) {
            case HEAD -> findModelPartByAnyName(parentModel, "head", "head2");
            case BODY -> findModelPartByAnyName(parentModel, "body", "Torso", "torso");
            case LEFT_ARM -> findModelPartByAnyName(parentModel, "leftArm", "larm", "arm0");
            case RIGHT_ARM -> findModelPartByAnyName(parentModel, "rightArm", "rarm", "arm1");
            case LEFT_LEG -> findModelPartByAnyName(parentModel, "leftLeg", "lleg", "leg0");
            case RIGHT_LEG -> findModelPartByAnyName(parentModel, "rightLeg", "rleg", "leg1");
        };
    }

    private ModelPart findModelPartByAnyName(Object model, String... names) {
        Class<?> c = model.getClass();

        while (c != null) {
            for (String wanted : names) {
                for (Field f : c.getDeclaredFields()) {
                    if (!ModelPart.class.isAssignableFrom(f.getType())) continue;
                    if (!f.getName().equalsIgnoreCase(wanted)) continue;

                    try {
                        f.setAccessible(true);
                        Object value = f.get(model);
                        if (value instanceof ModelPart part) {
                            return part;
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
            c = c.getSuperclass();
        }

        return null;
    }

    private RigType getRigType(LivingEntity entity) {
        return isSmasher(entity) ? RigType.SMASHER : RigType.DEFAULT_HUMANOID;
    }

    private boolean isSmasher(LivingEntity entity) {
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (key == null) return false;

        String path = key.getPath().toLowerCase(Locale.ROOT);
        return path.equals("smasher") || path.equals("cybersmasher");
    }

    private static void applyKnuckleClawTransformDefault(PoseStack pose, AttachmentAnchor armAnchor) {
        pose.translate(0.0F, 0.6F, 0.0F);
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

        pose.scale(1.0F, 1.0F, 1.0F);
    }

    private static void applyDrillFistTransformDefault(PoseStack pose, AttachmentAnchor armAnchor) {
        pose.translate(0.07F, -0.25F, 0.45F);
        pose.mulPose(Axis.YP.rotationDegrees(90.0F));
        pose.scale(1.15F, 1.15F, 1.15F);

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

    private static void applyOcelotPawsTransformDefault(PoseStack pose, AttachmentAnchor legAnchor) {
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

        pose.scale(1.0F, 1.0F, 1.0F);
    }

    private static void applyCalfPropellerTransformDefault(PoseStack pose, AttachmentAnchor legAnchor) {
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

        pose.scale(1.0F, 1.0F, 1.0F);
    }

    private static void applySpursTransformDefault(PoseStack pose, AttachmentAnchor legAnchor) {
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

        pose.scale(1.0F, 1.0F, 1.0F);
    }

    private static void applyGuardianEyeTransformDefault(PoseStack pose, AttachmentAnchor headAnchor) {
        pose.translate(0.0F, -0.25F, -0.205F);
        pose.mulPose(Axis.XN.rotationDegrees(0.0F));
        pose.mulPose(Axis.YP.rotationDegrees(0.0F));
        pose.scale(1.0F, 1.0F, 1.0F);
    }

    private static void applyWardenAntlersTransformDefault(PoseStack pose, AttachmentAnchor headAnchor) {
        pose.translate(0.0F, 0.0F, 0.0F);
        pose.mulPose(Axis.XN.rotationDegrees(0.0F));
        pose.mulPose(Axis.YP.rotationDegrees(0.0F));
        pose.scale(1.0F, 1.0F, 1.0F);
    }

    private static void applyNeuralProcessorTransformDefault(PoseStack pose, AttachmentAnchor headAnchor) {
        pose.translate(0.0F, 0.0F, 0.0F);
        pose.mulPose(Axis.XN.rotationDegrees(0.0F));
        pose.mulPose(Axis.YP.rotationDegrees(0.0F));
        pose.scale(1.0F, 1.0F, 1.0F);
    }





    private static void applyKnuckleClawTransformSmasher(PoseStack pose, AttachmentAnchor armAnchor) {
        pose.translate(0.0F, 1.25F, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(90.0F));

        if (armAnchor == AttachmentAnchor.LEFT_ARM) {
            pose.translate(0.15F, 0.0F, -0.45F);
            pose.mulPose(Axis.ZP.rotationDegrees(-10.0F));
        } else if (armAnchor == AttachmentAnchor.RIGHT_ARM) {
            pose.translate(0.1F, 0.0F, 0.45F);
            pose.mulPose(Axis.ZP.rotationDegrees(10.0F));
            pose.mulPose(Axis.YP.rotationDegrees(-180.0F));
        }

        pose.scale(1.75F, 1.75F, 1.75F);
    }

    private static void applyDrillFistTransformSmasher(PoseStack pose, AttachmentAnchor armAnchor) {
        pose.translate(0.00F, -0.5F, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(90.0F));
        pose.scale(1.15F, 1.15F, 1.15F);

        if (armAnchor == AttachmentAnchor.LEFT_ARM) {
            pose.translate(-0.625F, 0.0F, -0.675F);
            pose.mulPose(Axis.ZP.rotationDegrees(0.0F));
            pose.mulPose(Axis.YP.rotationDegrees(-180.0F));
        } else if (armAnchor == AttachmentAnchor.RIGHT_ARM) {
            pose.translate(0.86F, 0.0F, 0.675F);
            pose.mulPose(Axis.ZP.rotationDegrees(0.0F));
        }

        pose.scale(2F, 2F, 2F);
    }

    private static void applyOcelotPawsTransformSmasher(PoseStack pose, AttachmentAnchor legAnchor) {
        pose.translate(-0.03F, 0.9F, 0.0F);
        pose.mulPose(Axis.YP.rotationDegrees(0.0F));

        if (legAnchor == AttachmentAnchor.LEFT_LEG) {
            pose.translate(0.0F, 0.0F, 0.0F);
            pose.mulPose(Axis.ZP.rotationDegrees(0.0F));
            pose.mulPose(Axis.YP.rotationDegrees(0.0F));
        } else if (legAnchor == AttachmentAnchor.RIGHT_LEG) {
            pose.translate(0.0F, 0.0F, 0.0F);
            pose.mulPose(Axis.ZP.rotationDegrees(0.0F));
        }

        pose.scale(1.5F, 1.5F, 1.5F);
    }

    private static void applyCalfPropellerTransformSmasher(PoseStack pose, AttachmentAnchor legAnchor) {
        pose.translate(-0.03F, -1.6F, -0.85F);
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

        pose.scale(1.75F, 1.75F, 1.75F);
    }

    private static void applySpursTransformSmasher(PoseStack pose, AttachmentAnchor legAnchor) {
        pose.translate(-0.03F, 0.0F, 0.06F);
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

        pose.scale(1.0F, 1.0F, 1.0F);
    }

    private static void applyGuardianEyeTransformSmasher(PoseStack pose, AttachmentAnchor headAnchor) {
        pose.translate(0.0F, -0.25F, -0.205F);
        pose.mulPose(Axis.XN.rotationDegrees(0.0F));
        pose.mulPose(Axis.YP.rotationDegrees(0.0F));
        pose.scale(1.0F, 1.0F, 1.0F);
    }

    private static void applyWardenAntlersTransformSmasher(PoseStack pose, AttachmentAnchor headAnchor) {
        pose.translate(0.0F, -0.5F, -0.1F);
        pose.mulPose(Axis.XN.rotationDegrees(0.0F));
        pose.mulPose(Axis.YP.rotationDegrees(0.0F));
        pose.scale(1.0F, 1.0F, 1.0F);
    }

    private static void applyNeuralProcessorTransformSmasher(PoseStack pose, AttachmentAnchor headAnchor) {
        pose.translate(0.0F, 0.0F, 0.0F);
        pose.mulPose(Axis.XN.rotationDegrees(0.0F));
        pose.mulPose(Axis.YP.rotationDegrees(0.0F));
        pose.scale(1.0F, 1.0F, 1.0F);
    }
}