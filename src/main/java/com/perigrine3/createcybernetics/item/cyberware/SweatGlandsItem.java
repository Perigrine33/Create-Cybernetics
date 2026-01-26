package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.compat.coldsweat.ColdSweatCompat;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class SweatGlandsItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int MIN_POWER_REQUIRED = 5;

    public SweatGlandsItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.createcybernetics.skinupgrades_sweat.energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.SKIN_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.SKIN);
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
    public void onTick(Player player, ItemStack installedStack, CyberwareSlot slot, int index) {
        if (player.level().isClientSide) return;
        if (!player.hasData(ModAttachments.CYBERWARE)) {
            ColdSweatCompat.clearHeat(player);
            return;
        }

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) {
            ColdSweatCompat.clearHeat(player);
            return;
        }

        if (data.getEnergyStored() >= MIN_POWER_REQUIRED) {
            ColdSweatCompat.applyHeatResistance(player, 0.30);
            ColdSweatCompat.applyHeatDampening(player, 0.65);
        } else {
            ColdSweatCompat.clearHeat(player);
        }
    }

    @Override
    public void onUnpoweredTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        if (player.level().isClientSide) return;
        ColdSweatCompat.clearHeat(player);
    }

    @Override
    public void onRemoved(Player player) {
        if (player.level().isClientSide) return;
        ColdSweatCompat.clearHeat(player);
    }

    @Override
    public void onTick(Player player) {}
}
