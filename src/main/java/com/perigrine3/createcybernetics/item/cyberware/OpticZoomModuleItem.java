package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.screen.custom.CyberwareToggleWheelScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OpticZoomModuleItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int ENERGY_PER_TICK = 2;
    private static final int[] LEVELS = new int[]{5, 15, 25};
    private static final Map<UUID, Integer> CLIENT_LEVEL = new ConcurrentHashMap<>();

    public OpticZoomModuleItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.literal("Costs 2 Energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<Item> requiresCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModItems.BASECYBERWARE_CYBEREYES.get());
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.EYES);
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
    public int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return isEnabledByWheel(player) ? ENERGY_PER_TICK : 0;
    }

    @Override
    public boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public void onTick(Player player) {
    }

    private static int getLevelIndex(Player player) {
        if (player == null) return 0;
        return Mth.clamp(CLIENT_LEVEL.getOrDefault(player.getUUID(), 0), 0, LEVELS.length - 1);
    }

    private static void setLevelIndex(Player player, int idx) {
        if (player == null) return;
        CLIENT_LEVEL.put(player.getUUID(), Mth.clamp(idx, 0, LEVELS.length - 1));
    }

    private static int getZoomFactor(Player player) {
        return LEVELS[getLevelIndex(player)];
    }

    private static double getFovMultiplier(Player player) {
        return 1.0 / (double) getZoomFactor(player);
    }

    private static boolean isZoomEnabledForPlayer(Player player) {
        if (player == null) return false;

        Item it = ModItems.EYEUPGRADES_ZOOM.get();
        if (!(it instanceof ICyberwareItem cw)) return false;

        return cw.isEnabledByWheel(player);
    }

    private static boolean isZoomPoweredForPlayer(Player player) {
        if (player == null) return false;
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        Item target = ModItems.EYEUPGRADES_ZOOM.get();

        for (int i = 0; i < CyberwareSlot.EYES.size; i++) {
            InstalledCyberware cw = data.get(CyberwareSlot.EYES, i);
            if (cw == null) continue;

            ItemStack st = cw.getItem();
            if (st == null || st.isEmpty()) continue;

            if (st.getItem() != target) continue;

            return cw.isPowered();
        }

        return false;
    }

    private static boolean isZoomActiveForPlayer(Player player) {
        return isZoomEnabledForPlayer(player) && isZoomPoweredForPlayer(player);
    }

    private static void cycleZoomLevel(Player player) {
        int cur = getLevelIndex(player);
        int next = (cur + 1) % LEVELS.length;
        setLevelIndex(player, next);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == player) {
            player.displayClientMessage(
                    Component.literal("Zoom: " + LEVELS[next] + "x").withStyle(ChatFormatting.AQUA),
                    true
            );
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static final class ClientEvents {
        @SubscribeEvent
        public static void onMouseButton(InputEvent.MouseButton.Pre event) {
            if (event.getAction() != GLFW.GLFW_PRESS) return;
            if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_RIGHT) return;

            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            if (CyberwareToggleWheelScreen.isWheelOpen()) return;
            if (mc.screen != null) return;
            if (!isZoomActiveForPlayer(mc.player)) return;

            cycleZoomLevel(mc.player);

            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onComputeFov(ViewportEvent.ComputeFov event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            if (!isZoomActiveForPlayer(mc.player)) return;

            event.setFOV(event.getFOV() * getFovMultiplier(mc.player));
        }
    }
}
