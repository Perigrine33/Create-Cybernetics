package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.CyberwareAttributeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Set;

public class CybereyeItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    public CybereyeItem(Properties props, int humanityCost) {
        super(props);
        this.humanityCost = humanityCost;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createcybernetics.humanity", humanityCost)
                    .withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.literal("Consumes 5 Energy/tick").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public boolean isDyeable(ItemStack stack, CyberwareSlot slot) {
        return slot == CyberwareSlot.EYES;
    }

    @Override
    public boolean isDyeable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnergyUsedPerTick(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return 5;
    }

    @Override
    public boolean requiresEnergyToFunction(Player player, ItemStack installedStack, CyberwareSlot slot) {
        return true;
    }

    @Override public int getHumanityCost() { return humanityCost; }
    @Override public Set<CyberwareSlot> getSupportedSlots() { return Set.of(CyberwareSlot.EYES); }
    @Override public boolean replacesOrgan() { return true; }
    @Override public Set<CyberwareSlot> getReplacedOrgans() { return Set.of(CyberwareSlot.EYES); }

    @Override
    public int maxStacksPerSlotType(ItemStack stack, CyberwareSlot slotType) {
        return 3;
    }

    @Override
    public void onInstalled(Player player) {

    }

    @Override
    public void onRemoved(Player player) {
        // Your mappings: MobEffects.* are Holder<MobEffect>
        player.removeEffect(MobEffects.BLINDNESS);
        player.removeEffect(MobEffects.DARKNESS);
    }

    @Override
    public void onTick(Player player) {
        ICyberwareItem.super.onTick(player);
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class PowerFailHooks {
        private PowerFailHooks() {}

        private static final int DURATION = 220;
        private static final int BLINDNESS_AMPLIFIER = 1;
        private static final int DARKNESS_AMPLIFIER = 0;

        /**
         * LOWEST is intentional: EnergyController also runs on PlayerTickEvent.Post and is what sets cw.setPowered(...).
         * We want to run after it has decided power for the tick.
         */
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onPlayerTick(PlayerTickEvent.Post event) {
            Player player = event.getEntity();
            if (player.level().isClientSide) return;

            if (!player.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            EyesStatus status = getEyesStatus(player, data);

            if (status.hasCybereyes && status.unpowered) {
                refreshEffect(player, MobEffects.BLINDNESS, DURATION, BLINDNESS_AMPLIFIER);
                refreshEffect(player, MobEffects.DARKNESS, DURATION, DARKNESS_AMPLIFIER);
            } else {
                player.removeEffect(MobEffects.BLINDNESS);
                player.removeEffect(MobEffects.DARKNESS);
            }
        }

        private record EyesStatus(boolean hasCybereyes, boolean unpowered) {}

        private static EyesStatus getEyesStatus(Player player, PlayerCyberwareData data) {
            InstalledCyberware[] arr = data.getAll().get(CyberwareSlot.EYES);
            if (arr == null) return new EyesStatus(false, false);

            boolean hasEyes = false;

            for (int idx = 0; idx < arr.length; idx++) {
                InstalledCyberware installed = arr[idx];
                if (installed == null) continue;

                ItemStack st = installed.getItem();
                if (st == null || st.isEmpty()) continue;

                if (!(st.getItem() instanceof CybereyeItem)) continue;
                if (!data.isEnabled(CyberwareSlot.EYES, idx)) continue;

                hasEyes = true;

                // EnergyController is authoritative: if it couldn't pay, it sets powered=false.
                // If your InstalledCyberware doesn't expose isPowered(), this line will fail to compile.
                // In that case tell me and I'll swap in the same reflection reader you used in Cyberarm hooks.
                if (!installed.isPowered()) {
                    return new EyesStatus(true, true);
                }
            }

            return new EyesStatus(hasEyes, false);
        }

        private static void refreshEffect(Player player, Holder<MobEffect> effect, int duration, int amplifier) {
            MobEffectInstance cur = player.getEffect(effect);

            // Refresh only if missing / near expiry / amp mismatch
            if (cur == null || cur.getDuration() < 40 || cur.getAmplifier() != amplifier) {
                player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, false));
            }
        }
    }
}
