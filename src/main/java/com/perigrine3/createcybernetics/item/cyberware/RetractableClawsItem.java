package com.perigrine3.createcybernetics.item.cyberware;

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
import com.perigrine3.createcybernetics.sound.ModSounds;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RetractableClawsItem extends Item implements ICyberwareItem {

    private final int humanityCost;

    public RetractableClawsItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.LARM, CyberwareSlot.RARM);
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return switch (slot) {
            case RARM -> Set.of(ModTags.Items.RIGHTARM_REPLACEMENTS);
            case LARM -> Set.of(ModTags.Items.LEFTARM_REPLACEMENTS);
            default -> Set.of();
        };
    }

    @Override
    public boolean replacesOrgan() {
        return false;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of();
    }

    @Override
    public void onTick(Player player) {
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class ServerHandler {

        @SubscribeEvent
        public static void onPlayerTick(PlayerTickEvent.Post event) {
            Player player = event.getEntity();
            if (player.level().isClientSide) return;

            if (!player.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            boolean leftEnabled = isLeftEnabled(data);
            boolean rightEnabled = isRightEnabled(data);

            java.util.UUID id = player.getUUID();

            boolean prevLeft = LAST_LEFT.getOrDefault(id, false);
            if (leftEnabled != prevLeft) {
                LAST_LEFT.put(id, leftEnabled);
                playToggleSound(player, leftEnabled);
            }

            boolean prevRight = LAST_RIGHT.getOrDefault(id, false);
            if (rightEnabled != prevRight) {
                LAST_RIGHT.put(id, rightEnabled);
                playToggleSound(player, rightEnabled);
            }

            boolean anyEnabled = leftEnabled || rightEnabled;
            boolean weaponEquipped = isHoldingWeapon(player);

            if (anyEnabled && !weaponEquipped) {
                if (rightEnabled && leftEnabled) {
                    CyberwareAttributeHelper.applyModifier(player, "claws_attack1");
                    CyberwareAttributeHelper.applyModifier(player, "claws_attack2");
                } else {
                    CyberwareAttributeHelper.applyModifier(player, "claws_attack1");
                    CyberwareAttributeHelper.removeModifier(player, "claws_attack2");
                }
            } else {
                CyberwareAttributeHelper.removeModifier(player, "claws_attack1");
                CyberwareAttributeHelper.removeModifier(player, "claws_attack2");
            }
        }

        private static boolean isHoldingWeapon(Player player) {
            return isWeaponLike(player.getMainHandItem()) || isWeaponLike(player.getOffhandItem());
        }

        private static boolean isWeaponLike(ItemStack stack) {
            if (stack == null || stack.isEmpty()) return false;

            Item it = stack.getItem();
            if (it instanceof BowItem || it instanceof CrossbowItem || it instanceof TridentItem) return true;
            if (it instanceof SwordItem || it instanceof AxeItem || it instanceof MaceItem || it instanceof DiggerItem) return true;

            ItemAttributeModifiers mods = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
            if (mods == null) return false;

            for (ItemAttributeModifiers.Entry e : mods.modifiers()) {
                Holder<Attribute> attr = e.attribute();

                boolean attackDamage = attr != null && attr.value() == Attributes.ATTACK_DAMAGE;
                boolean mainhand = (e.slot() == EquipmentSlotGroup.MAINHAND) || e.slot().test(EquipmentSlot.MAINHAND);

                if (attackDamage && mainhand && e.modifier().amount() != 0.0) {
                    return true;
                }
            }

            return false;
        }

        private static final java.util.Map<java.util.UUID, Boolean> LAST_LEFT = new java.util.HashMap<>();
        private static final java.util.Map<java.util.UUID, Boolean> LAST_RIGHT = new java.util.HashMap<>();

        private static void playToggleSound(Player player, boolean enabledNow) {
            SoundEvent snd = clawsToggleSound();
            if (snd == null) return;

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), snd,
                    SoundSource.PLAYERS, 0.9F, enabledNow ? 1.15F : 0.95F);
        }

        private static SoundEvent clawsToggleSound() {
            return ModSounds.RETRACTABLE_CLAWS_SNIKT.get();
        }

        @SubscribeEvent
        public static void onLogout(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
            java.util.UUID id = event.getEntity().getUUID();
            LAST_LEFT.remove(id);
            LAST_RIGHT.remove(id);
        }

        private static boolean isLeftEnabled(PlayerCyberwareData data) {
            return hasEnabledInSlot(data, CyberwareSlot.LARM);
        }

        private static boolean isRightEnabled(PlayerCyberwareData data) {
            return hasEnabledInSlot(data, CyberwareSlot.RARM);
        }

        private static boolean hasEnabledInSlot(PlayerCyberwareData data, CyberwareSlot slot) {
            InstalledCyberware[] arr = data.getAll().get(slot);
            if (arr == null) return false;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware cw = arr[i];
                if (cw == null) continue;

                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;

                if (!(st.getItem() instanceof RetractableClawsItem)) continue;

                if (data.isEnabled(slot, i)) return true;
            }

            return false;
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static final class ClientFirstPerson {

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

            if (!player.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            HumanoidArm arm = event.getArm();
            CyberwareSlot slot = (arm == HumanoidArm.LEFT) ? CyberwareSlot.LARM : CyberwareSlot.RARM;
            if (!hasEnabledClawsInSlot(data, slot)) return;

            var renderer = mc.getEntityRenderDispatcher().getRenderer(player);
            if (!(renderer instanceof net.minecraft.client.renderer.entity.player.PlayerRenderer pr)) return;

            PlayerModel<?> pm = pr.getModel();
            var armPart = (arm == HumanoidArm.LEFT) ? pm.leftArm : pm.rightArm;

            PoseStack pose = event.getPoseStack();
            MultiBufferSource buffers = event.getMultiBufferSource();
            int light = event.getPackedLight();

            var model = PlayerAttachmentManager.clawsModel();
            var tex = PlayerAttachmentManager.CLAWS_TEXTURE;

            pose.pushPose();
            try {
                pose.translate(armPart.x / 16.0F, armPart.y / 16.0F, armPart.z / 16.0F);

                pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(/* pitch */ 0.0F));
                pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(/* yaw   */ 0.0F));
                pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(/* roll  */ 5F));

                AttachmentAnchor anchor = (arm == HumanoidArm.LEFT)
                        ? AttachmentAnchor.LEFT_ARM
                        : AttachmentAnchor.RIGHT_ARM;

                PlayerAttachmentManager.applyKnuckleClawTransform(pose, anchor);

                var vc = buffers.getBuffer(model.renderType(tex));
                model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
            } finally {
                pose.popPose();
            }
        }
    }


    private static boolean hasEnabledClawsInSlot(PlayerCyberwareData data, CyberwareSlot slot) {
        InstalledCyberware[] arr = data.getAll().get(slot);
        if (arr == null) return false;

        for (int i = 0; i < arr.length; i++) {
            InstalledCyberware cw = arr[i];
            if (cw == null) continue;

            ItemStack st = cw.getItem();
            if (st == null || st.isEmpty()) continue;

            if (!(st.getItem() instanceof RetractableClawsItem)) continue;

            if (data.isEnabled(slot, i)) return true;
        }

        return false;
    }
}
