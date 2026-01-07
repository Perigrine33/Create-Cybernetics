package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.compat.caelus.CaelusCompat;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Set;

public class DeployableElytraItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final ResourceLocation CC_CAELUS_FLIGHT_ID =
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "deployable_elytra_flight");

    private static final String NBT_ACTIVATION_PAID = "cc_deployable_elytra_paid";

    private static final int ACTIVATION_COST = 1;
    private static final int GLIDE_COST_PER_TICK = 2;

    public DeployableElytraItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.literal("Costs 1-2 Energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getEnergyActivationCost(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return ACTIVATION_COST;
    }

    @Override
    public int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return (player != null && player.isFallFlying()) ? GLIDE_COST_PER_TICK : 0;
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.BONE_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.BONE);
    }

    @Override
    public boolean replacesOrgan() {
        return false;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of();
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class CaelusServerSync {

        @SubscribeEvent
        public static void onPlayerTick(PlayerTickEvent.Post event) {
            Player player = event.getEntity();
            if (player.level().isClientSide()) return;
            if (!CaelusCompat.isLoaded()) return;
            if (cc$hasRealChestElytra(player)) {
                CaelusCompat.removeFallFlyingModifier(player, CC_CAELUS_FLIGHT_ID);
                return;
            }

            boolean allow = shouldAllowCyberFallFlyingAndPayEnergy(player);

            if (allow) {
                CaelusCompat.addOrUpdateFallFlyingTransient(player, CC_CAELUS_FLIGHT_ID, 1.0D);
            } else {
                CaelusCompat.removeFallFlyingModifier(player, CC_CAELUS_FLIGHT_ID);
                if (player.isFallFlying()) {
                    player.stopFallFlying();
                }
            }
        }
    }

    private static boolean cc$hasRealChestElytra(Player player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty()) return false;

        if (!(chest.getItem() instanceof ElytraItem)) return false;

        return ElytraItem.isFlyEnabled(chest);
    }

    private static boolean cc$hasEnabledDeployable(Player player) {
        if (player == null) return false;
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        for (int i = 0; i < CyberwareSlot.BONE.size; i++) {
            if (!data.isInstalled(ModItems.BONEUPGRADES_ELYTRA.get(), CyberwareSlot.BONE, i)) continue;
            if (!data.isEnabled(CyberwareSlot.BONE, i)) continue;
            return true;
        }

        return false;
    }

    private static boolean shouldAllowCyberFallFlyingAndPayEnergy(Player player) {
        if (!cc$hasEnabledDeployable(player)) {
            return false;
        }

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        ItemStack enabledStack = ItemStack.EMPTY;

        for (int i = 0; i < CyberwareSlot.BONE.size; i++) {
            if (!data.isInstalled(ModItems.BONEUPGRADES_ELYTRA.get(), CyberwareSlot.BONE, i)) continue;

            var installed = data.get(CyberwareSlot.BONE, i);
            if (installed == null) continue;

            ItemStack stack = installed.getItem();
            if (stack == null || stack.isEmpty()) continue;

            boolean enabled = data.isEnabled(CyberwareSlot.BONE, i);

            if (!enabled) {
                clearActivationPaid(stack);
                continue;
            }

            if (enabledStack.isEmpty()) {
                enabledStack = stack;
            }
        }

        if (enabledStack.isEmpty()) {
            return false;
        }

        if (!ensureActivationPaid(enabledStack, data)) {
            return false;
        }

        if (player.isFallFlying()) {
            if (!data.tryConsumeEnergy(GLIDE_COST_PER_TICK)) {
                return false;
            }
        }

        return true;
    }

    private static boolean ensureActivationPaid(ItemStack stack, PlayerCyberwareData data) {
        if (isActivationPaid(stack)) {
            return true;
        }

        if (!data.tryConsumeEnergy(ACTIVATION_COST)) {
            return false;
        }

        CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> t.putBoolean(NBT_ACTIVATION_PAID, true));
        data.setDirty();
        return true;
    }

    private static boolean isActivationPaid(ItemStack stack) {
        CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
        if (cd == null || cd.isEmpty()) return false;

        CompoundTag t = cd.copyTag();
        return t.getBoolean(NBT_ACTIVATION_PAID);
    }

    private static void clearActivationPaid(ItemStack stack) {
        CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
        if (cd == null || cd.isEmpty()) return;

        if (!cd.contains(NBT_ACTIVATION_PAID)) return;

        CustomData.update(DataComponents.CUSTOM_DATA, stack, t -> t.remove(NBT_ACTIVATION_PAID));
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static final class CaelusClientStart {
        private static boolean cc$sentStartThisFall = false;

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if (player == null) return;

            if (!CaelusCompat.isLoaded()) return;

            if (cc$hasRealChestElytra(player)) {
                cc$sentStartThisFall = false;
                return;
            }

            if (!cc$hasEnabledDeployable(player)) {
                cc$sentStartThisFall = false;
                return;
            }

            if (player.onGround() || player.isFallFlying() || player.isInWaterOrBubble() || player.isInLava()) {
                cc$sentStartThisFall = false;
                return;
            }

            if (!mc.options.keyJump.isDown()) return;
            if (player.getDeltaMovement().y >= 0.0D) return;

            if (cc$sentStartThisFall) return;
            cc$sentStartThisFall = true;

            player.connection.send(new ServerboundPlayerCommandPacket(
                    player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        }
    }
}
