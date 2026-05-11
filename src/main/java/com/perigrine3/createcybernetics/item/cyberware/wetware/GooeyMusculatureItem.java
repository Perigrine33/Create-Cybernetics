package com.perigrine3.createcybernetics.item.cyberware.wetware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.List;
import java.util.Set;

public class GooeyMusculatureItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public GooeyMusculatureItem(Properties props, int humanityCost) {
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
        return Set.of(CyberwareSlot.MUSCLE);
    }

    @Override
    public boolean replacesOrgan() {
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.MUSCLE);
    }

    @Override
    public void onInstalled(Player player) {
        CyberwareAttributeHelper.applyModifier(player, "gooeymuscle_fall");
    }

    @Override
    public void onRemoved(Player player) {
        CyberwareAttributeHelper.removeModifier(player, "gooeymuscle_fall");
    }

    @Override
    public void onTick(LivingEntity entity) {

    }

    private static boolean hasGooeyMusculature(Player player) {
        if (player == null) return false;
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);

        InstalledCyberware[] arr = data.getAll().get(CyberwareSlot.MUSCLE);
        if (arr == null) return false;

        for (InstalledCyberware installed : arr) {
            if (installed == null) continue;

            ItemStack stack = installed.getItem();
            if (stack == null || stack.isEmpty()) continue;

            if (stack.getItem() instanceof GooeyMusculatureItem) {
                return true;
            }
        }

        return false;
    }

    private static boolean isMaceDamage(DamageSource source) {
        if (source == null) return false;

        if (!(source.getEntity() instanceof LivingEntity attacker)) {
            return false;
        }

        ItemStack mainHand = attacker.getMainHandItem();
        if (!mainHand.isEmpty() && mainHand.getItem() instanceof MaceItem) {
            return true;
        }

        ItemStack offHand = attacker.getOffhandItem();
        return !offHand.isEmpty() && offHand.getItem() instanceof MaceItem;
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class ServerHandler {
        private ServerHandler() {}

        @SubscribeEvent
        public static void onIncomingDamage(LivingIncomingDamageEvent event) {
            LivingEntity entity = event.getEntity();

            if (!(entity instanceof Player player)) {
                return;
            }

            if (player.level().isClientSide) {
                return;
            }

            if (!hasGooeyMusculature(player)) {
                return;
            }

            if (!isMaceDamage(event.getSource())) {
                return;
            }

            event.setAmount(event.getAmount() * 0.5F);
        }
    }
}