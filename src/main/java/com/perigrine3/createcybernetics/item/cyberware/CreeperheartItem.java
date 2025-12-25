package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics; // ADDED
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments; // ADDED
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData; // ADDED
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel; // ADDED
import net.minecraft.world.entity.Entity; // ADDED
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level; // ADDED
import net.neoforged.bus.api.SubscribeEvent; // ADDED
import net.neoforged.fml.common.EventBusSubscriber; // ADDED
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent; // ADDED

import java.util.List;
import java.util.Set;

public class CreeperheartItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public CreeperheartItem(Properties props, int humanityCost) {
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
    public Set<Item> requiresCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModItems.BODYPART_HEART.get());
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.HEART);
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
    }

    @Override
    public void onRemoved(Player player) {
    }

    @Override
    public void onTick(Player player) {
    }

    @Override
    public boolean dropsOnDeath(ItemStack installedStack, CyberwareSlot slot) {
        return false;
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {
        private static final float EXPLOSION_POWER = 6.0F;

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;
            if (!(player.level() instanceof ServerLevel level)) return;

            Entity killer = event.getSource().getEntity();
            if (killer == null) return;

            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;
            if (!hasCreeperHeartInstalled(data)) return;

            level.explode(player, player.getX(), player.getY(), player.getZ(), EXPLOSION_POWER, false, Level.ExplosionInteraction.MOB);
        }

        private static boolean hasCreeperHeartInstalled(PlayerCyberwareData data) {
            return data.hasSpecificItem(ModItems.HEARTUPGRADES_CREEPERHEART.get(), CyberwareSlot.HEART);
        }

        private Events() {}
    }
}
