package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Set;

public class OxygenTankItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public OxygenTankItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public int getHumanityCost() {
        return humanityCost;
    }

    @Override
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModTags.Items.LUNGS_ITEMS);
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.LUNGS);
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
        if (!player.level().isClientSide) {
            AirHandler.resetOxygenTankTracking(player);
        }
    }

    @Override
    public void onRemoved(Player player) {
        if (!player.level().isClientSide) {
            AirHandler.resetOxygenTankTracking(player);
        }
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class AirHandler {
        private static final String KEY_HAS_PREV = "cc_o2tank_has_prev";
        private static final String KEY_PREV_AIR = "cc_o2tank_prev_air";
        private static final String KEY_DECREMENT_COUNT = "cc_o2tank_dec_count";

        private AirHandler() {}

        @SubscribeEvent
        public static void onPlayerTick(PlayerTickEvent.Post event) {
            Player player = event.getEntity();

            if (player.level().isClientSide) return;

            if (!hasOxygenTankInstalled(player)) {
                resetOxygenTankTracking(player);
                return;
            }

            boolean eyesInWater = player.isEyeInFluid(FluidTags.WATER);
            int air = player.getAirSupply();
            int maxAir = player.getMaxAirSupply();

            CompoundTag data = player.getPersistentData();

            if (!eyesInWater || air >= maxAir) {
                data.putBoolean(KEY_HAS_PREV, true);
                data.putInt(KEY_PREV_AIR, air);
                data.putInt(KEY_DECREMENT_COUNT, 0);
                return;
            }

            if (!data.contains(KEY_HAS_PREV, Tag.TAG_BYTE)) {
                data.putBoolean(KEY_HAS_PREV, true);
                data.putInt(KEY_PREV_AIR, air);
                data.putInt(KEY_DECREMENT_COUNT, 0);
                return;
            }

            int prevAir = data.getInt(KEY_PREV_AIR);
            int decCount = data.getInt(KEY_DECREMENT_COUNT);

            if (air < prevAir) {
                int delta = prevAir - air;
                int refund = 0;

                for (int i = 0; i < delta; i++) {
                    decCount++;
                    if ((decCount % 4) != 0) {
                        refund++;
                    }
                }

                if (refund > 0) {
                    int newAir = Math.min(maxAir, air + refund);
                    player.setAirSupply(newAir);
                    air = newAir;
                }

                data.putInt(KEY_DECREMENT_COUNT, decCount);
            }

            data.putInt(KEY_PREV_AIR, air);
        }

        public static void resetOxygenTankTracking(Player player) {
            CompoundTag data = player.getPersistentData();
            data.remove(KEY_HAS_PREV);
            data.remove(KEY_PREV_AIR);
            data.remove(KEY_DECREMENT_COUNT);
        }

        private static boolean hasOxygenTankInstalled(Player player) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return false;

            return data.hasSpecificItem(ModItems.LUNGSUPGRADES_OXYGEN.get(), CyberwareSlot.LUNGS);
        }
    }
}
