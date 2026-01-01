package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;              // ADDED
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;  // ADDED
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData; // ADDED
import com.perigrine3.createcybernetics.effect.ModEffects;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Set;

public class EyeOfDefenderItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int ENERGY_PER_TICK = 5;

    public EyeOfDefenderItem(Properties props, int humanityCost) {
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
        return false;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of();
    }

    @Override
    public int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return ENERGY_PER_TICK;
    }

    @Override
    public void onInstalled(Player player) {}

    @Override
    public void onRemoved(Player player) {
        if (!player.level().isClientSide) {
            player.removeEffect(ModEffects.PROJECTILE_DODGE_EFFECT);
        }
    }

    @Override
    public void onTick(Player player, ItemStack installedStack, CyberwareSlot slot, int index) {
        if (player.level().isClientSide) return;
        if (!player.isAlive()) return;
        if (player.isCreative() || player.isSpectator()) return;

        if (!player.hasData(ModAttachments.CYBERWARE)) return;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return;

        InstalledCyberware cw = data.get(slot, index);
        if (cw == null) return;
        if (!cw.isPowered()) {
            player.removeEffect(ModEffects.PROJECTILE_DODGE_EFFECT);
            return;
        }

        player.addEffect(new MobEffectInstance(ModEffects.PROJECTILE_DODGE_EFFECT, 20, 0, false, false, false));
    }

    @Override
    public void onTick(Player player) {
        if (player.level().isClientSide) return;
    }
}
