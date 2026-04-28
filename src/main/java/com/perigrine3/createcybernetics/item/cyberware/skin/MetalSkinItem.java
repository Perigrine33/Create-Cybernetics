package com.perigrine3.createcybernetics.item.cyberware.skin;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
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
    public boolean isDyeable(ItemStack stack, CyberwareSlot slot) {
        return slot == CyberwareSlot.SKIN;
    }

    @Override
    public boolean isDyeable(ItemStack stack) {
        return true;
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

    @Override
    public int maxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) {
        return 1;
    }

    @Override
    public void onInstalled(LivingEntity entity) {
    }

    @Override
    public void onRemoved(LivingEntity entity) {
    }

    @Override
    public void onTick(LivingEntity entity) {
        ICyberwareItem.super.onTick(entity);
        if (entity.level().isClientSide) return;
        if (entity.isInvisible()) return;
        if (entity.getRandom().nextInt(60) != 0) return;

        double px = entity.getX();
        double py = entity.getY() + 0.9D;
        double pz = entity.getZ();

        double ox = (entity.getRandom().nextDouble() - 0.5D) * 1.2D;
        double oy = (entity.getRandom().nextDouble() - 0.5D) * 0.9D;
        double oz = (entity.getRandom().nextDouble() - 0.5D) * 1.2D;

        double mx = (entity.getRandom().nextDouble() - 0.5D) * 0.02D;
        double my = entity.getRandom().nextDouble() * 0.02D;
        double mz = (entity.getRandom().nextDouble() - 0.5D) * 0.02D;

        if (entity.level() instanceof net.minecraft.server.level.ServerLevel sl) {
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK, px + ox, py + oy, pz + oz, 1, mx, my, mz, 0.02D);
        }
    }
}