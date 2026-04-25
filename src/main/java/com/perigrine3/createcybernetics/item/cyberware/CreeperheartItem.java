package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.advancement.ModCriteria;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.EntityCyberwareData;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.ModMobAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

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
    public void onInstalled(LivingEntity entity) {
    }

    @Override
    public void onRemoved(LivingEntity entity) {
    }

    @Override
    public void onTick(LivingEntity entity) {
    }

    @Override
    public boolean dropsOnDeath(ItemStack installedStack, CyberwareSlot slot) {
        return false;
    }

    private static boolean hasSpecificItem(LivingEntity entity, Item item, CyberwareSlot slot) {
        if (entity instanceof Player player) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            return data != null && data.hasSpecificItem(item, slot);
        }

        EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
        return data != null && data.hasSpecificItem(item, slot);
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            LivingEntity living = event.getEntity();
            if (living.level().isClientSide) return;
            if (!(living.level() instanceof ServerLevel level)) return;

            Entity killer = event.getSource().getEntity();
            if (killer == null) return;

            if (!hasSpecificItem(living, ModItems.HEARTUPGRADES_CREEPERHEART.get(), CyberwareSlot.HEART)) return;

            if (hasSpecificItem(living, ModItems.ORGANSUPGRADES_MAGICCATALYST.get(), CyberwareSlot.ORGANS)) {
                level.explode(living, living.getX(), living.getY() - 2, living.getZ(), 50, true, Level.ExplosionInteraction.MOB);

                if (living instanceof ServerPlayer sp) {
                    ModCriteria.DESTROYER_OF_WORLDS.get().trigger(sp);
                }
            } else if (hasSpecificItem(living, ModItems.ORGANSUPGRADES_DUALISTICCONVERTER.get(), CyberwareSlot.ORGANS)) {
                level.explode(living, living.getX(), living.getY() - 2, living.getZ(), 25, true, Level.ExplosionInteraction.MOB);
            } else {
                level.explode(living, living.getX(), living.getY(), living.getZ(), 6, false, Level.ExplosionInteraction.MOB);
            }
        }

        private Events() {}
    }
}