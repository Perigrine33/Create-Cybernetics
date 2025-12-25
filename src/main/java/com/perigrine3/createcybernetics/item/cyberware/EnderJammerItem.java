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
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

import java.util.List;
import java.util.Set;

public class EnderJammerItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final double JAM_RADIUS = 10.0D;
    private static final double JAM_RADIUS_SQ = JAM_RADIUS * JAM_RADIUS;

    public EnderJammerItem(Properties props, int humanityCost) {
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
    public void onInstalled(Player player) { }

    @Override
    public void onRemoved(Player player) { }

    @Override
    public void onTick(Player player) {
        if (player.level().isClientSide) return;
    }

    private static boolean hasEnderJammerInstalled(ServerPlayer player) {
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        return data.hasSpecificItem(ModItems.BRAINUPGRADES_ENDERJAMMER.get(), CyberwareSlot.BRAIN);
    }

    private static boolean isPointProtected(ServerLevel level, Vec3 point) {
        if (point == null) return false;

        AABB box = new AABB(
                point.x - JAM_RADIUS, point.y - JAM_RADIUS, point.z - JAM_RADIUS,
                point.x + JAM_RADIUS, point.y + JAM_RADIUS, point.z + JAM_RADIUS
        );

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
