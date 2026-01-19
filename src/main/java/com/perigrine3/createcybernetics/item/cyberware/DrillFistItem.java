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
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Set;

public class DrillFistItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final float DIAMOND_PICK_SPEED = 8.0F;

    public DrillFistItem(Properties props, int humanityCost) {
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

    @Override public int getHumanityCost() { return humanityCost; }

    @Override
    public Set<Item> requiresCyberware(ItemStack installedStack, CyberwareSlot slot) {
        return switch (slot) {
            case RARM -> Set.of(ModItems.BASECYBERWARE_RIGHTARM.get());
            case LARM -> Set.of(ModItems.BASECYBERWARE_LEFTARM.get());
            default -> Set.of();
        };
    }

    @Override
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.RARM, CyberwareSlot.LARM);
    }

    @Override public boolean replacesOrgan() { return false; }
    @Override public Set<CyberwareSlot> getReplacedOrgans() { return Set.of(); }

    private static boolean hasDrillInstalled(Player player, CyberwareSlot slot) {
        if (!player.hasData(ModAttachments.CYBERWARE)) return false;

        PlayerCyberwareData data = player.getData(ModAttachments.CYBERWARE);
        if (data == null) return false;

        return data.hasSpecificItem(ModItems.ARMUPGRADES_DRILLFIST.get(), slot);
    }

    private static boolean drillBlocksMainHand(Player player) {
        HumanoidArm mainArm = player.getMainArm();
        boolean rightInstalled = hasDrillInstalled(player, CyberwareSlot.RARM);
        boolean leftInstalled  = hasDrillInstalled(player, CyberwareSlot.LARM);

        return (mainArm == HumanoidArm.RIGHT) ? rightInstalled : leftInstalled;
    }

    private static boolean drillBlocksOffhand(Player player) {
        HumanoidArm mainArm = player.getMainArm();
        boolean rightInstalled = hasDrillInstalled(player, CyberwareSlot.RARM);
        boolean leftInstalled  = hasDrillInstalled(player, CyberwareSlot.LARM);

        return (mainArm == HumanoidArm.RIGHT) ? leftInstalled : rightInstalled;
    }

    private static boolean drillBlocksHand(Player player, InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? drillBlocksMainHand(player) : drillBlocksOffhand(player);
    }

    private static void dropAndClearHand(ServerSideDropper dropper, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) return;

        ItemStack toDrop = stack.copy();
        player.setItemInHand(hand, ItemStack.EMPTY);
        dropper.drop(player, toDrop);
    }

    @FunctionalInterface
    private interface ServerSideDropper {
        void drop(Player player, ItemStack stack);
    }

    @EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static final class DrillHooks {
        private DrillHooks() {}

        @SubscribeEvent
        public static void onPlayerTick(PlayerTickEvent.Post event) {
            Player player = event.getEntity();
            if (player.level().isClientSide()) return;

            boolean blocksMain = drillBlocksMainHand(player);
            boolean blocksOff  = drillBlocksOffhand(player);
            if (!blocksMain && !blocksOff) return;

            ServerSideDropper dropper = (p, stack) -> p.drop(stack, true);

            if (blocksMain) {
                dropAndClearHand(dropper, player, InteractionHand.MAIN_HAND);
            }
            if (blocksOff) {
                dropAndClearHand(dropper, player, InteractionHand.OFF_HAND);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            Player player = event.getEntity();
            Level level = player.level();
            if (level.isClientSide()) return;

            if (!drillBlocksHand(player, event.getHand())) return;

            BlockState state = level.getBlockState(event.getPos());
            boolean isUiBlock = state.getMenuProvider(level, event.getPos()) != null;
            if (!isUiBlock) return;

            if (player.getRandom().nextBoolean()) return;

            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }

        @SubscribeEvent
        public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
            Player player = event.getEntity();

            if (!drillBlocksMainHand(player)) return;

            event.setCanHarvest(true);
        }

        @SubscribeEvent
        public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
            Player player = event.getEntity();

            if (!drillBlocksMainHand(player)) return;

            float original = event.getOriginalSpeed();
            float newSpeed = Math.max(original, DIAMOND_PICK_SPEED);

            event.setNewSpeed(newSpeed);
        }
    }
}
