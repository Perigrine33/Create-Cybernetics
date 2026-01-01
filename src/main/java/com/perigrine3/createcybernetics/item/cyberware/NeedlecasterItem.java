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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;
import java.util.Set;

public class NeedlecasterItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final int MAX_LEVELS_TRANSFER = 14;
    private static final int XP_PER_LEVEL_DEATH_DROP = 7;
    private static final String NBT_STORED_LEVELS = "cc_needlecaster_storedLevels";

    public NeedlecasterItem(Properties props, int humanityCost) {
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
    public Set<Item> incompatibleCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return Set.of(ModItems.BRAINUPGRADES_CORTICALSTACK.get());
    }

    @Override
    public void onInstalled(Player player) { }

    @Override
    public void onRemoved(Player player) { }

    @Override
    public void onTick(Player player) {
        if (player.level().isClientSide) return;
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID)
    public static final class Events {

        @SubscribeEvent
        public static void onPlayerDeath(LivingDeathEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player)) return;
            if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;
            if (!hasNeedlecasterInstalled(player)) return;

            int toStore = Math.min(MAX_LEVELS_TRANSFER, player.experienceLevel);
            if (toStore <= 0) return;

            player.getPersistentData().putInt(NBT_STORED_LEVELS, toStore);
        }

        @SubscribeEvent
        public static void onExperienceDrop(LivingExperienceDropEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player)) return;
            if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;
            if (!hasNeedlecasterInstalled(player)) return;

            int levelAtDeath = player.experienceLevel;

            if (levelAtDeath <= MAX_LEVELS_TRANSFER) {
                event.setDroppedExperience(0);
                return;
            }

            int reduceBy = MAX_LEVELS_TRANSFER * XP_PER_LEVEL_DEATH_DROP; // 98
            int newDrop = Math.max(0, event.getDroppedExperience() - reduceBy);
            event.setDroppedExperience(newDrop);
        }

        @SubscribeEvent
        public static void onPlayerClone(PlayerEvent.Clone event) {
            if (!event.isWasDeath()) return;

            if (!(event.getEntity() instanceof ServerPlayer newPlayer)) return;
            if (!(event.getOriginal() instanceof ServerPlayer oldPlayer)) return;

            if (newPlayer.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;
            if (!hasNeedlecasterInstalled(oldPlayer)) return;

            int stored = oldPlayer.getPersistentData().getInt(NBT_STORED_LEVELS);
            if (stored <= 0) return;

            oldPlayer.getPersistentData().remove(NBT_STORED_LEVELS);
            newPlayer.giveExperienceLevels(stored);
        }

        private static boolean hasNeedlecasterInstalled(ServerPlayer player) {
            PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
            if (data == null) return false;

            return data.hasSpecificItem(ModItems.BRAINUPGRADES_CONSCIOUSNESSTRANSMITTER.get(), CyberwareSlot.BRAIN);
        }
    }
}
