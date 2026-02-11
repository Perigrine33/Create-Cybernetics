package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Set;

public class MetalDetectorItem extends Item implements ICyberwareItem {
    private final int humanityCost;
    private static final int ENERGY_PER_TICK = 3;

    public MetalDetectorItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        // SAFE OPTION: no Screen.hasShiftDown() here.
        tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.createcybernetics.legupgrades_metaldetector.energy").withStyle(ChatFormatting.RED));
    }

    @Override public int getHumanityCost() { return humanityCost; }
    @Override public int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) { return ENERGY_PER_TICK; }
    @Override public boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) { return true; }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return switch (slot) {
            case RLEG -> Set.of(ModTags.Items.RIGHTLEG_REPLACEMENTS);
            case LLEG -> Set.of(ModTags.Items.LEFTLEG_REPLACEMENTS);
            default -> Set.of();
        };
    }

    @Override public Set<CyberwareSlot> getSupportedSlots() { return Set.of(CyberwareSlot.RLEG, CyberwareSlot.LLEG); }
    @Override public boolean replacesOrgan() { return false; }
    @Override public Set<CyberwareSlot> getReplacedOrgans() { return Set.of(); }
    @Override public void onInstalled(Player player) {}
    @Override public void onRemoved(Player player) {}

    // Keep this helper PUBLIC/STATIC so the client ticker can reuse it without duplicating logic
    public static DetectionResult scanForMetal(Level level, Player player) {
        BlockPos onPos = BlockPos.containing(player.getX(), player.getY() - 0.05D, player.getZ());

        int bestDy = Integer.MAX_VALUE;
        boolean bestDirectColumn = false;

        for (int dy = 0; dy <= 15; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos checkPos = onPos.offset(dx, -dy, dz);
                    BlockState state = level.getBlockState(checkPos);

                    if (state.is(ModTags.Blocks.METAL_DETECTABLE)) {
                        boolean direct = (dx == 0 && dz == 0);
                        if (dy < bestDy || (dy == bestDy && direct && !bestDirectColumn)) {
                            bestDy = dy;
                            bestDirectColumn = direct;
                        }
                    }
                }
            }
            if (bestDy == 0 && bestDirectColumn) break;
        }

        return new DetectionResult(bestDy != Integer.MAX_VALUE, bestDy, bestDirectColumn);
    }

    public static boolean isAnyMetalDetectorPowered(Player player) {
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        for (CyberwareSlot slot : Set.of(CyberwareSlot.RLEG, CyberwareSlot.LLEG)) {
            for (int i = 0; i < slot.size; i++) {
                InstalledCyberware cw = data.get(slot, i);
                if (cw == null) continue;

                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;

                if (st.getItem() instanceof MetalDetectorItem && cw.isPowered()) return true;
            }
        }
        return false;
    }

    public record DetectionResult(boolean detected, int dy, boolean direct) {}
}
