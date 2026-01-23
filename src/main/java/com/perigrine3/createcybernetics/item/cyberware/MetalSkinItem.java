package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
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

public class MetalSkinItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public MetalSkinItem(Properties props, int humanityCost) {
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
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.SKIN_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.SKIN);
    }

    @Override
    public boolean replacesOrgan() {
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.SKIN);
    }

    public int maxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) {
        return 1;
    }

    @Override
    public void onInstalled(Player player) {
    }

    @Override
    public void onRemoved(Player player) {
    }

    @Override
    public void onTick(Player player) {
        ICyberwareItem.super.onTick(player);
        if (player.level().isClientSide) return;
        if (player.isInvisible()) return;
        if (player.getRandom().nextInt(60) != 0) return;

        double px = player.getX();
        double py = player.getY() + 0.9D;
        double pz = player.getZ();

        double ox = (player.getRandom().nextDouble() - 0.5D) * 1.2D;
        double oy = (player.getRandom().nextDouble() - 0.5D) * 0.9D;
        double oz = (player.getRandom().nextDouble() - 0.5D) * 1.2D;

        double mx = (player.getRandom().nextDouble() - 0.5D) * 0.02D;
        double my = player.getRandom().nextDouble() * 0.02D;
        double mz = (player.getRandom().nextDouble() - 0.5D) * 0.02D;

        if (player.level() instanceof net.minecraft.server.level.ServerLevel sl) {
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK, px + ox, py + oy, pz + oz, 1, mx, my, mz, 0.02D);
        }
    }

}