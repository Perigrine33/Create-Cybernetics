package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class ICEProtocolItem extends Item implements ICyberwareItem {
    private static final float QUICKHACK_NEGATION_CHANCE = 0.75f;

    private final int humanityCost;

    public ICEProtocolItem(Properties props, int humanityCost) {
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
        return Set.of(CyberwareSlot.BRAIN);
    }

    @Override
    public boolean replacesOrgan() {
        return false;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.BRAIN);
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

    public static boolean negatesQuickhack(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return false;
        if (!serverPlayer.hasData(ModAttachments.CYBERWARE)) return false;

        PlayerCyberwareData data = serverPlayer.getData(ModAttachments.CYBERWARE);
        InstalledCyberware[] installed = data.getAll().get(CyberwareSlot.BRAIN);
        if (installed == null) return false;

        for (InstalledCyberware entry : installed) {
            if (entry == null) continue;

            ItemStack stack = entry.getItem();
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof ICEProtocolItem) {
                boolean blocked = serverPlayer.getRandom().nextFloat() < QUICKHACK_NEGATION_CHANCE;

                if (blocked) {
                    serverPlayer.displayClientMessage(
                            Component.literal("[ICE Protocol]: Quickhack Neutralized").withStyle(ChatFormatting.RED),
                            true);
                }

                return blocked;
            }
        }

        return false;
    }
}