package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag; // ADDED
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

import java.util.List;
import java.util.Set;

public class EnderJammerItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final double JAM_RADIUS = 10.0D;
    private static final double JAM_RADIUS_SQ = JAM_RADIUS * JAM_RADIUS;

    private static final int ENERGY_PER_TICK = 5;
    private static final String NBT_POWERED = "cc_enderjammer_powered";

    public EnderJammerItem(Properties props, int humanityCost) {
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
    public boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return ENERGY_PER_TICK;
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
        return false;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of();
    }

    @Override
    public void onInstalled(Player player) {
        if (!player.level().isClientSide) {
            player.getPersistentData().putBoolean(NBT_POWERED, false);
        }
    }

    @Override
    public void onRemoved(Player player) {
        if (!player.level().isClientSide) {
            player.getPersistentData().remove(NBT_POWERED);
        }
    }

    @Override
    public void onTick(Player player) {
    }

    @Override
    public void onPowerLost(Player player, ItemStack installedStack, CyberwareSlot slot) {
        if (!player.level().isClientSide) {
            player.getPersistentData().putBoolean(NBT_POWERED, false);
        }
    }

    @Override
    public void onPowerRestored(Player player, ItemStack installedStack, CyberwareSlot slot) {
        if (!player.level().isClientSide) {
            player.getPersistentData().putBoolean(NBT_POWERED, true);
        }
    }

    @Override
    public void onUnpoweredTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        if (!player.level().isClientSide) {
            player.getPersistentData().putBoolean(NBT_POWERED, false);
        }
    }

    @Override
    public void onPoweredTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        if (!player.level().isClientSide) {
            player.getPersistentData().putBoolean(NBT_POWERED, true);
        }
    }

    private static boolean hasEnderJammerInstalled(ServerPlayer player) {
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        CompoundTag ptag = player.getPersistentData();
        if (!ptag.getBoolean(NBT_POWERED)) return false;

        return data.hasSpecificItem(ModItems.BRAINUPGRADES_ENDERJAMMER.get(), CyberwareSlot.BRAIN);
    }

    private static boolean isPointProtected(ServerLevel level, Vec3 point) {
        if (point == null) return false;

        AABB box = new AABB(
                point.x - JAM_RADIUS, point.y - JAM_RADIUS, point.z - JAM_RADIUS,
                point.x + JAM_RADIUS, point.y + JAM_RADIUS, point.z + JAM_RADIUS);

        List<ServerPlayer> players =
                level.getEntitiesOfClass(ServerPlayer.class, box, EnderJammerItem::hasEnderJammerInstalled);

        if (players.isEmpty()) return false;

        for (ServerPlayer p : players) {
            if (p.position().distanceToSqr(point) <= JAM_RADIUS_SQ) {
                return true;
            }
        }
        return false;
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {

        @SubscribeEvent
        public static void onAnyEntityTeleport(EntityTeleportEvent event) {
            Entity entity = event.getEntity();
            if (entity == null) return;
            if (!(entity.level() instanceof ServerLevel level)) return;

            Vec3 prev = event.getPrev();
            Vec3 target = event.getTarget();

            if (isPointProtected(level, prev) || isPointProtected(level, target)) {
                event.setCanceled(true);
            }
        }
    }
}
