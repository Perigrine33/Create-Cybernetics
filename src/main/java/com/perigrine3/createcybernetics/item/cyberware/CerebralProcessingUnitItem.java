package com.perigrine3.createcybernetics.item.cyberware;

import com.mojang.blaze3d.systems.RenderSystem;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.advancement.ModCriteria;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.network.payload.CerebralShutdownStatePayload;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Set;

public class CerebralProcessingUnitItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int ENERGY_PER_TICK = 5;

    // ===== Client-synced shutdown flag (set by CerebralShutdownStatePayload.handle) =====
    private static volatile boolean CLIENT_SHUTDOWN_ACTIVE = false;

    public static void setClientShutdownActive(boolean active) {
        CLIENT_SHUTDOWN_ACTIVE = active;
    }

    public static boolean clientShutdownActive() {
        return CLIENT_SHUTDOWN_ACTIVE;
    }

    // ===== PersistentData keys (server) =====
    private static final String NBT_SHUTDOWN_ACTIVE = "cc_cpu_shutdown_active";

    private static final String NBT_ANCHOR_SET = "cc_cpu_shutdown_anchor";
    private static final String NBT_AX = "cc_cpu_shutdown_ax";
    private static final String NBT_AY = "cc_cpu_shutdown_ay";
    private static final String NBT_AZ = "cc_cpu_shutdown_az";
    private static final String NBT_AYAW = "cc_cpu_shutdown_yaw";
    private static final String NBT_APITCH = "cc_cpu_shutdown_pitch";

    public CerebralProcessingUnitItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.literal("Costs 5 Energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.BRAIN_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.BRAIN);
    }

    @Override
    public boolean replacesOrgan() {
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.BRAIN);
    }

    @Override
    public int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return ENERGY_PER_TICK;
    }

    @Override
    public void onInstalled(Player player) {
        CyberwareAttributeHelper.applyModifier(player, "cyberbrain_learn");
        CyberwareAttributeHelper.applyModifier(player, "cyberbrain_insomnia");
    }

    @Override
    public void onRemoved(Player player) {
        CyberwareAttributeHelper.removeModifier(player, "cyberbrain_learn");
        CyberwareAttributeHelper.removeModifier(player, "cyberbrain_insomnia");
    }

    @Override
    public void onTick(Player player, ItemStack installedStack, CyberwareSlot slot, int index) {
        // Shutdown is enforced via event hooks below; no per-item ticking required here.
    }

    @Override
    public void onTick(Player player) {
        // unused
    }

    /* ============================================================
       Shutdown conditions
       ============================================================ */

    /**
     * SERVER-side: authoritative "CPU installed+enabled and unpowered".
     * This must be computed after EnergyController has set InstalledCyberware#setPowered(...) for the tick.
     */
    private static boolean cpuInstalledEnabledAndUnpowered(PlayerCyberwareData data) {
        InstalledCyberware[] arr = data.getAll().get(CyberwareSlot.BRAIN);
        if (arr == null) return false;

        for (int idx = 0; idx < arr.length; idx++) {
            InstalledCyberware installed = arr[idx];
            if (installed == null) continue;

            ItemStack st = installed.getItem();
            if (st == null || st.isEmpty()) continue;

            if (!(st.getItem() instanceof CerebralProcessingUnitItem)) continue;
            if (!data.isEnabled(CyberwareSlot.BRAIN, idx)) continue;

            return !installed.isPowered();
        }
        return false;
    }

    /**
     * Client helper: you requested the overlay should be active if
     * (hasCybereyesInstalledAndEnabled) OR (shutdown).
     *
     * IMPORTANT: this controls ONLY the overlay, not the full-body lock.
     */
    private static boolean clientOverlayActive(Player player) {
        if (player == null) return false;

        // Always show overlay during shutdown.
        if (CLIENT_SHUTDOWN_ACTIVE) return true;

        // Otherwise, only show overlay for cybereyes if the player is actually blinded/darkened.
        PlayerCyberwareData data = player.hasData(ModAttachments.CYBERWARE) ? player.getData(ModAttachments.CYBERWARE) : null;
        if (data == null) return false;

        boolean eyesEnabled = hasCybereyesInstalledAndEnabled(data);
        if (!eyesEnabled) return false;

        return player.hasEffect(MobEffects.BLINDNESS) || player.hasEffect(MobEffects.DARKNESS);
    }


    private static boolean hasCybereyesInstalledAndEnabled(PlayerCyberwareData data) {
        InstalledCyberware[] arr = data.getAll().get(CyberwareSlot.EYES);
        if (arr == null) return false;

        for (int idx = 0; idx < arr.length; idx++) {
            InstalledCyberware installed = arr[idx];
            if (installed == null) continue;

            ItemStack st = installed.getItem();
            if (st == null || st.isEmpty()) continue;

            if (!(st.getItem() instanceof CybereyeItem)) continue;
            if (!data.isEnabled(CyberwareSlot.EYES, idx)) continue;

            return true;
        }
        return false;
    }

    /* ============================================================
       SERVER: decide + sync shutdown after EnergyController (Post, LOWEST)
       ============================================================ */

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class ShutdownServerDecision {
        private ShutdownServerDecision() {}

        /**
         * LOWEST is intentional: EnergyController also runs on PlayerTickEvent.Post and decides cw.setPowered(...).
         * We run after it, read InstalledCyberware#isPowered(), then sync to client if changed.
         */
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onTickPost(PlayerTickEvent.Post event) {
            Player p = event.getEntity();
            if (p.level().isClientSide) return;
            if (!(p instanceof ServerPlayer sp)) return;

            if (!sp.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            boolean shutdownNow = cpuInstalledEnabledAndUnpowered(data);

            CompoundTag pt = sp.getPersistentData();
            boolean prev = pt.getBoolean(NBT_SHUTDOWN_ACTIVE);

            if (shutdownNow != prev) {
                pt.putBoolean(NBT_SHUTDOWN_ACTIVE, shutdownNow);

                if (shutdownNow) {
                    ModCriteria.THOUGHTS_NOT_FOUND.get().trigger(sp);
                }

                if (!shutdownNow) {
                    pt.remove(NBT_ANCHOR_SET);
                } else {
                    pt.putBoolean(NBT_ANCHOR_SET, false);
                }

                PacketDistributor.sendToPlayer(sp, new CerebralShutdownStatePayload(shutdownNow));
            }
        }
    }

    /* ============================================================
       SERVER: enforce hard lock BEFORE movement (Pre, HIGHEST)
       ============================================================ */

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class ShutdownServerEnforce {
        private ShutdownServerEnforce() {}

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onTickPre(PlayerTickEvent.Pre event) {
            Player p = event.getEntity();
            if (p.level().isClientSide) return;
            if (!(p instanceof ServerPlayer sp)) return;

            CompoundTag pt = sp.getPersistentData();
            if (!pt.getBoolean(NBT_SHUTDOWN_ACTIVE)) return;

            if (!pt.getBoolean(NBT_ANCHOR_SET)) {
                pt.putBoolean(NBT_ANCHOR_SET, true);
                pt.putDouble(NBT_AX, sp.getX());
                pt.putDouble(NBT_AY, sp.getY());
                pt.putDouble(NBT_AZ, sp.getZ());
                pt.putFloat(NBT_AYAW, sp.getYRot());
                pt.putFloat(NBT_APITCH, sp.getXRot());
            }

            double ax = pt.getDouble(NBT_AX);
            double ay = pt.getDouble(NBT_AY);
            double az = pt.getDouble(NBT_AZ);
            float yaw = pt.getFloat(NBT_AYAW);
            float pitch = pt.getFloat(NBT_APITCH);

            sp.connection.teleport(ax, ay, az, yaw, pitch);

            sp.setDeltaMovement(Vec3.ZERO);
            sp.fallDistance = 0;
            sp.setSprinting(false);
            sp.stopUsingItem();
        }

        @SubscribeEvent
        public static void onAttackEntity(AttackEntityEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer sp)) return;
            if (sp.getPersistentData().getBoolean(NBT_SHUTDOWN_ACTIVE)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
            if (!(event.getEntity() instanceof ServerPlayer sp)) return;
            if (sp.getPersistentData().getBoolean(NBT_SHUTDOWN_ACTIVE)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            if (!(event.getEntity() instanceof ServerPlayer sp)) return;
            if (sp.getPersistentData().getBoolean(NBT_SHUTDOWN_ACTIVE)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
            if (!(event.getEntity() instanceof ServerPlayer sp)) return;
            if (sp.getPersistentData().getBoolean(NBT_SHUTDOWN_ACTIVE)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
            if (!(event.getEntity() instanceof ServerPlayer sp)) return;
            if (sp.getPersistentData().getBoolean(NBT_SHUTDOWN_ACTIVE)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
            if (!(event.getEntity() instanceof ServerPlayer sp)) return;
            if (sp.getPersistentData().getBoolean(NBT_SHUTDOWN_ACTIVE)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
            if (!(event.getEntity() instanceof ServerPlayer sp)) return;
            if (sp.getPersistentData().getBoolean(NBT_SHUTDOWN_ACTIVE)) {
                event.setNewSpeed(0.0F);
            }
        }
    }

    /* ============================================================
       CLIENT: movement + click suppression + camera lock + overlay
       ============================================================ */

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static final class ShutdownClientHooks {
        private ShutdownClientHooks() {}

        private static boolean anchorSet = false;
        private static float anchorYaw;
        private static float anchorPitch;

        @SubscribeEvent
        public static void onMove(MovementInputUpdateEvent event) {
            // Full-body lock should ONLY happen during CPU shutdown (not merely cybereyes enabled).
            if (!CLIENT_SHUTDOWN_ACTIVE) return;

            Input in = event.getInput();
            in.leftImpulse = 0.0F;
            in.forwardImpulse = 0.0F;

            in.up = false;
            in.down = false;
            in.left = false;
            in.right = false;

            in.jumping = false;
            in.shiftKeyDown = false;
        }

        @SubscribeEvent
        public static void onInteract(InputEvent.InteractionKeyMappingTriggered event) {
            if (!CLIENT_SHUTDOWN_ACTIVE) return;

            // Cancels attack/use/pick pipelines clientside.
            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Pre event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            if (!CLIENT_SHUTDOWN_ACTIVE) {
                anchorSet = false;
                return;
            }

            if (!anchorSet) {
                anchorSet = true;
                anchorYaw = mc.player.getYRot();
                anchorPitch = mc.player.getXRot();
            }

            // Snap view back every tick
            mc.player.setYRot(anchorYaw);
            mc.player.setXRot(anchorPitch);

            mc.player.yRotO = anchorYaw;
            mc.player.xRotO = anchorPitch;

            mc.player.yHeadRot = anchorYaw;
            mc.player.yHeadRotO = anchorYaw;
            mc.player.yBodyRot = anchorYaw;
            mc.player.yBodyRotO = anchorYaw;
        }

        @SubscribeEvent
        public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            if (mc.screen != null) return;

            // You requested: overlay active if cybereyes enabled OR shutdown.
            if (!clientOverlayActive(mc.player)) return;

            GuiGraphics gg = event.getGuiGraphics();
            int w = mc.getWindow().getGuiScaledWidth();
            int h = mc.getWindow().getGuiScaledHeight();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            gg.fill(0, 0, w, h, 0xFF000000);

            RenderSystem.disableBlend();
        }
    }
}
