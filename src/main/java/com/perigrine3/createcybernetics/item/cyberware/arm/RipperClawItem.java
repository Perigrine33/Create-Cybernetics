package com.perigrine3.createcybernetics.item.cyberware.arm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.client.model.AttachmentAnchor;
import com.perigrine3.createcybernetics.client.model.PlayerAttachmentManager;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderArmEvent;

import java.util.List;
import java.util.Set;

public class RipperClawItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public RipperClawItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.RARM, CyberwareSlot.LARM);
    }

    @Override
    public boolean replacesOrgan() {
        return false;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.RARM, CyberwareSlot.LARM);
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.LEFT_CYBERARM, ModTags.Items.RIGHT_CYBERARM);
    }

    @Override
    public Set<Item> incompatibleCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModItems.ARMUPGRADES_CLAWS.get());
    }

    @Override
    public void onInstalled(Player player) {
        CyberwareAttributeHelper.applyModifier(player, "ripperclaw_damage");
    }

    @Override
    public void onRemoved(Player player) {
        CyberwareAttributeHelper.removeModifier(player, "ripperclaw_damage");
    }

    @Override
    public void onTick(LivingEntity entity) {

    }

    private static boolean hasRipperClawInstalled(Player player, CyberwareSlot slot) {
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        return data.hasSpecificItem(ModItems.ARMUPGRADES_RIPPERCLAW.get(), slot);
    }

    private static boolean hasRipperClawInSlot(PlayerCyberwareData data, CyberwareSlot slot) {
        InstalledCyberware[] arr = data.getAll().get(slot);
        if (arr == null) return false;

        for (InstalledCyberware cw : arr) {
            if (cw == null) continue;

            ItemStack st = cw.getItem();
            if (st == null || st.isEmpty()) continue;

            if (st.is(ModItems.ARMUPGRADES_RIPPERCLAW.get())) return true;
        }

        return false;
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static final class ClientFirstPerson {
        private ClientFirstPerson() {}

        @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
        public static void onRenderArm(RenderArmEvent event) {
            AbstractClientPlayer player = event.getPlayer();
            if (player == null) return;

            Minecraft mc = Minecraft.getInstance();
            Player viewer = mc.player;

            if (viewer != null) {
                if (player.isInvisibleTo(viewer)) return;
            } else {
                if (player.isInvisible()) return;
            }

            if (mc.player == null || player.getUUID() != mc.player.getUUID()) return;

            if (!player.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            HumanoidArm arm = event.getArm();
            CyberwareSlot slot = (arm == HumanoidArm.LEFT) ? CyberwareSlot.LARM : CyberwareSlot.RARM;

            if (!hasRipperClawInSlot(data, slot)) return;

            float armX = (arm == HumanoidArm.LEFT) ? 5.0F : -5.0F;
            float armY = 2.0F;
            float armZ = 0.0F;

            PoseStack pose = event.getPoseStack();
            MultiBufferSource buffers = event.getMultiBufferSource();
            int light = event.getPackedLight();

            var model = PlayerAttachmentManager.ripperClawModel();
            var tex = PlayerAttachmentManager.RIPPER_CLAW_TEXTURE;

            pose.pushPose();
            try {
                pose.translate(armX / 17F, armY / 10.0F, armZ / 16.0F);

                pose.scale(1F, 1F, 1F);

                if (arm == player.getMainArm()) {
                    pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(5F));
                } else {
                    pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-5F));
                }

                AttachmentAnchor anchor = (arm == HumanoidArm.LEFT)
                        ? AttachmentAnchor.LEFT_ARM
                        : AttachmentAnchor.RIGHT_ARM;

                PlayerAttachmentManager.applyRipperClawTransform(pose, anchor);

                if (arm == HumanoidArm.LEFT) {
                    pose.translate(0.0F, 0.0F, 0.0F);
                    pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(8.0F));
                }

                var vc = buffers.getBuffer(model.renderType(tex));
                model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
            } finally {
                pose.popPose();
            }
        }
    }
}