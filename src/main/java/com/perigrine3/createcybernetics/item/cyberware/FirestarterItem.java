package com.perigrine3.createcybernetics.item.cyberware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.api.InstalledCyberware;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FirestarterItem extends Item implements ICyberwareItem {

    private final int humanityCost;
    private static final int ENERGY_COST_PER_USE = 3;
    private static final int FIRE_SECONDS_ON_ENTITY = 100;

    private static final ConcurrentHashMap<UUID, Long> LAST_USE_TICK = new ConcurrentHashMap<>();

    public FirestarterItem(Properties props, int humanityCost) {
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
    public Set<TagKey<Item>> requiresCyberwareTags(ItemStack installedStack, CyberwareSlot slot) {
        return switch (slot) {
            case RLEG -> Set.of(ModTags.Items.RIGHTARM_ITEMS);
            case LLEG -> Set.of(ModTags.Items.LEFTARM_ITEMS);
            default -> Set.of();
        };
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.LARM, CyberwareSlot.RARM);
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
    public void onInstalled(Player player) {}

    @Override
    public void onRemoved(Player player) {}

    @Override
    public void onTick(Player player) {}

    private record InstalledRef(CyberwareSlot slot, int index) {}

    private static InstalledRef findEnabledInstalledFirestarter(ServerPlayer player, PlayerCyberwareData data) {
        for (var entry : data.getAll().entrySet()) {
            CyberwareSlot slot = entry.getKey();
            InstalledCyberware[] arr = entry.getValue();
            if (arr == null) continue;

            for (int i = 0; i < arr.length; i++) {
                InstalledCyberware inst = arr[i];
                if (inst == null) continue;

                ItemStack st = inst.getItem();
                if (st == null || st.isEmpty()) continue;

                if (!(st.getItem() instanceof FirestarterItem)) continue;
                if (!data.isEnabled(slot, i)) continue;

                return new InstalledRef(slot, i);
            }
        }
        return null;
    }

    private static boolean canPlaceFire(Level level, BlockPos pos) {
        if (!level.isInWorldBounds(pos)) return false;
        if (!level.getBlockState(pos).isAir()) return false;

        BlockState fireState = BaseFireBlock.getState(level, pos);
        if (fireState == null) return false;
        return fireState.canSurvive(level, pos);
    }

    private static boolean tryConsumeOncePerTick(ServerPlayer sp) {
        long tick = sp.level().getGameTime();
        UUID id = sp.getUUID();
        Long last = LAST_USE_TICK.get(id);
        if (last != null && last == tick) return false;
        LAST_USE_TICK.put(id, tick);
        return true;
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class Events {

        @SubscribeEvent
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            if (!(event.getEntity() instanceof ServerPlayer sp)) return;
            if (event.getHand() != InteractionHand.MAIN_HAND) return;

            if (!sp.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            if (findEnabledInstalledFirestarter(sp, data) == null) return;

            BlockPos placePos = event.getPos().relative(event.getFace());
            if (!canPlaceFire(sp.level(), placePos)) return;

            if (!data.tryConsumeEnergy(ENERGY_COST_PER_USE)) return;
            if (!tryConsumeOncePerTick(sp)) return;

            BlockState fireState = BaseFireBlock.getState(sp.level(), placePos);
            if (fireState == null || !fireState.canSurvive(sp.level(), placePos)) return;

            sp.level().setBlock(placePos, fireState, 11);

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }

        @SubscribeEvent
        public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
            if (!(event.getEntity() instanceof ServerPlayer sp)) return;
            if (event.getHand() != InteractionHand.MAIN_HAND) return;

            Entity target = event.getTarget();
            if (target == null) return;
            if (target.fireImmune()) return;
            if (target.isOnFire()) return;

            if (!sp.hasData(ModAttachments.CYBERWARE)) return;
            PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
            if (data == null) return;

            if (findEnabledInstalledFirestarter(sp, data) == null) return;

            if (!data.tryConsumeEnergy(ENERGY_COST_PER_USE)) return;
            if (!tryConsumeOncePerTick(sp)) return;

            target.setRemainingFireTicks(FIRE_SECONDS_ON_ENTITY);

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
