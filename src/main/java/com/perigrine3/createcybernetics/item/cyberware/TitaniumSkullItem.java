package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
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
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;
import java.util.Set;

public class TitaniumSkullItem extends Item implements ICyberwareItem {

    private final int humanityCost;

    public TitaniumSkullItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component
                    .translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
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
        return null;
    }

    @Override
    public void onInstalled(LivingEntity entity) {}

    @Override
    public void onRemoved(LivingEntity entity) {}

    @Override
    public void onTick(LivingEntity entity) {}

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class TitaniumSkullDamageHandler {

        private TitaniumSkullDamageHandler() {}

        @SubscribeEvent
        public static void onIncomingDamage(LivingIncomingDamageEvent event) {
            if (!(event.getEntity() instanceof LivingEntity entity)) return;
            if (entity.level().isClientSide) return;
            if (!event.getSource().is(DamageTypes.FLY_INTO_WALL)) return;
            if (!entity.isFallFlying()) return;

            boolean hasTitaniumSkull = false;

            if (entity instanceof net.minecraft.world.entity.player.Player player) {
                if (!player.hasData(ModAttachments.CYBERWARE)) return;
                PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
                if (data == null) return;
                hasTitaniumSkull = data.hasSpecificItem(ModItems.BONEUPGRADES_CYBERSKULL.get(), CyberwareSlot.BONE);
            } else {
                if (!entity.hasData(ModMobAttachments.CYBERENTITY_CYBERWARE)) return;
                EntityCyberwareData data = entity.getData(ModMobAttachments.CYBERENTITY_CYBERWARE);
                if (data == null) return;
                hasTitaniumSkull = data.hasSpecificItem(ModItems.BONEUPGRADES_CYBERSKULL.get(), CyberwareSlot.BONE);
            }

            if (!hasTitaniumSkull) return;

            event.setCanceled(true);
        }
    }
}