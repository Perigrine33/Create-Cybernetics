package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware; // ADDED
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments; // ADDED
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData; // ADDED
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.sound.MetalDetectorLoopSound;
import com.perigrine3.createcybernetics.sound.ModSounds;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Set;

public class MetalDetectorItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int ENERGY_PER_TICK = 3; // ADDED
    private static MetalDetectorLoopSound activeLoop;

    public MetalDetectorItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.literal("Costs 3 Energy").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return ENERGY_PER_TICK;
    }

    @Override
    public boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override
    public Set<Item> requiresCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return switch (slot) {
            case RLEG -> Set.of(ModItems.BASECYBERWARE_RIGHTLEG.get());
            case LLEG -> Set.of(ModItems.BASECYBERWARE_LEFTLEG.get());
            default -> Set.of();
        };
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.RLEG, CyberwareSlot.LLEG);
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
        stopLoopIfPlaying();
    }

    @Override
    public void onTick(Player player) {
        Level level = player.level();
        if (!level.isClientSide) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.player != player) return;

        if (!isAnyMetalDetectorPowered(player)) {
            stopLoopIfPlaying();
            return;
        }

        BlockPos onPos = BlockPos.containing(player.getX(), player.getY() - 0.05D, player.getZ());

        int bestDy = Integer.MAX_VALUE;
        boolean bestDirectColumn = false;

        BlockPos firstHitPos = null;
        BlockState firstHitState = null;

        for (int dy = 0; dy <= 15; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos checkPos = onPos.offset(dx, -dy, dz);
                    BlockState state = level.getBlockState(checkPos);

                    if (state.is(ModTags.Blocks.METAL_DETECTABLE)) {
                        if (firstHitPos == null) {
                            firstHitPos = checkPos;
                            firstHitState = state;
                        }

                        boolean direct = (dx == 0 && dz == 0);

                        if (dy < bestDy || (dy == bestDy && direct && !bestDirectColumn)) {
                            bestDy = dy;
                            bestDirectColumn = direct;
                        }
                    }
                }
            }

            if (bestDy == 0 && bestDirectColumn) {
                break;
            }
        }

        boolean detectedAny = (bestDy != Integer.MAX_VALUE);

        if (detectedAny) {
            startLoopIfNeeded(player);

            if (activeLoop != null) {
                float maxVol = 1.0F;
                float minVol = 0.2F;

                float t = 1.0F - (bestDy / 15.0F);
                float volume = minVol + (maxVol - minVol) * t;

                if (!bestDirectColumn) {
                    volume *= 0.5F;
                }

                activeLoop.setTargetVolume(volume);
            }
        } else {
            stopLoopIfPlaying();
        }
    }

    private boolean isAnyMetalDetectorPowered(Player player) {
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;
        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        for (CyberwareSlot slot : getSupportedSlots()) {
            for (int i = 0; i < slot.size; i++) {
                InstalledCyberware cw = data.get(slot, i);
                if (cw == null) continue;

                ItemStack st = cw.getItem();
                if (st == null || st.isEmpty()) continue;

                if (st.getItem() != this) continue;

                if (cw.isPowered()) return true;
            }
        }

        return false;
    }

    private static void startLoopIfNeeded(Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSoundManager() == null) return;

        if (activeLoop != null && mc.getSoundManager().isActive(activeLoop)) return;

        activeLoop = new MetalDetectorLoopSound(player, ModSounds.METAL_DETECTOR_BEEPS.get());
        mc.getSoundManager().play(activeLoop);
    }

    private static void stopLoopIfPlaying() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSoundManager() == null) return;

        if (activeLoop != null) {
            mc.getSoundManager().stop(activeLoop);
            activeLoop = null;
        }
    }
}
