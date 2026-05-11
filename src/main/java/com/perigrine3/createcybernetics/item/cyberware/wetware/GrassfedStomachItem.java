package com.perigrine3.createcybernetics.item.cyberware.wetware;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GrassfedStomachItem extends Item implements ICyberwareItem {
    private final int humanityCost;

    private static final String NBT_INSTALLED = "cc_grassfed_stomach_installed";

    private static final String NBT_EATING_WHEAT = "cc_grassfed_stomach_eating_wheat";
    private static final String NBT_EATING_WHEAT_TICKS = "cc_grassfed_stomach_eating_wheat_ticks";
    private static final String NBT_EATING_WHEAT_HAND = "cc_grassfed_stomach_eating_wheat_hand";

    private static final int WHEAT_EAT_TICKS = 32;

    private static final int GRASS_FOOD = 4;
    private static final float GRASS_SAT = 0.6F;

    public GrassfedStomachItem(Properties props, int humanityCost) {
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
    public Set<CyberwareSlot> getSupportedSlots() {
        return Set.of(CyberwareSlot.ORGANS);
    }

    @Override
    public boolean replacesOrgan() {
        return true;
    }

    @Override
    public Set<CyberwareSlot> getReplacedOrgans() {
        return Set.of(CyberwareSlot.ORGANS);
    }

    @Override
    public TagKey<Item> getReplacedOrganItemTag(ItemStack installedStack, CyberwareSlot slot) {
        return ModTags.Items.INTESTINES_ITEMS;
    }

    @Override
    public void onInstalled(LivingEntity entity) {
        if (!(entity instanceof Player player)) return;

        if (!player.level().isClientSide) {
            player.getPersistentData().putBoolean(NBT_INSTALLED, true);
        }
    }

    @Override
    public void onRemoved(LivingEntity entity) {
        if (!(entity instanceof Player player)) return;

        if (!player.level().isClientSide) {
            player.getPersistentData().putBoolean(NBT_INSTALLED, false);
            clearWheatEating(player);
        }
    }

    @Override
    public void onTick(LivingEntity entity) {
    }

    private static boolean isInstalled(Player player) {
        return player.getPersistentData().getBoolean(NBT_INSTALLED);
    }

    private static void fillHunger(Player player) {
        FoodData food = player.getFoodData();
        food.setFoodLevel(20);
        food.setSaturation(20.0F);
    }

    private static boolean isEatingWheat(Player player) {
        return player.getPersistentData().getBoolean(NBT_EATING_WHEAT);
    }

    private static void startEatingWheat(Player player, InteractionHand hand) {
        CompoundTag tag = player.getPersistentData();

        tag.putBoolean(NBT_EATING_WHEAT, true);
        tag.putInt(NBT_EATING_WHEAT_TICKS, 0);
        tag.putString(NBT_EATING_WHEAT_HAND, hand == InteractionHand.OFF_HAND ? "offhand" : "mainhand");

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.GENERIC_EAT,
                SoundSource.PLAYERS,
                0.45F,
                1.15F
        );
    }

    private static void clearWheatEating(Player player) {
        CompoundTag tag = player.getPersistentData();

        tag.remove(NBT_EATING_WHEAT);
        tag.remove(NBT_EATING_WHEAT_TICKS);
        tag.remove(NBT_EATING_WHEAT_HAND);
    }

    private static InteractionHand wheatEatingHand(Player player) {
        String hand = player.getPersistentData().getString(NBT_EATING_WHEAT_HAND);
        return "offhand".equals(hand) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    private static ItemStack wheatEatingStack(Player player) {
        return player.getItemInHand(wheatEatingHand(player));
    }

    @SubscribeEvent
    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (!isInstalled(player)) return;

        ItemStack using = event.getItem();
        boolean edible = using.getItem().getFoodProperties(using, player) != null;
        if (!edible) return;

        if (!using.is(Items.WHEAT)) {
            event.setCanceled(true);
            player.stopUsingItem();
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (!isInstalled(player)) return;

        ItemStack stack = event.getItemStack();
        if (!stack.is(Items.WHEAT)) return;

        event.setCanceled(true);

        if (!player.canEat(false)) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        if (isEatingWheat(player)) {
            event.setCancellationResult(InteractionResult.CONSUME);
            return;
        }

        if (player.level().isClientSide) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        startEatingWheat(player, event.getHand());
        event.setCancellationResult(InteractionResult.CONSUME);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        if (!isEatingWheat(player)) return;

        if (!isInstalled(player)) {
            clearWheatEating(player);
            return;
        }

        ItemStack stack = wheatEatingStack(player);
        if (stack == null || stack.isEmpty() || !stack.is(Items.WHEAT)) {
            clearWheatEating(player);
            return;
        }

        if (!player.canEat(false)) {
            clearWheatEating(player);
            return;
        }

        CompoundTag tag = player.getPersistentData();
        int ticks = tag.getInt(NBT_EATING_WHEAT_TICKS) + 1;
        tag.putInt(NBT_EATING_WHEAT_TICKS, ticks);

        if (ticks % 6 == 0) {
            player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.GENERIC_EAT,
                    SoundSource.PLAYERS,
                    0.35F,
                    0.9F + player.getRandom().nextFloat() * 0.2F
            );
        }

        if (ticks < WHEAT_EAT_TICKS) {
            return;
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        fillHunger(player);

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.PLAYER_BURP,
                SoundSource.PLAYERS,
                0.6F,
                1.0F
        );

        clearWheatEating(player);
    }

    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (!isInstalled(player)) return;

        ItemStack used = event.getItem();
        if (!used.is(Items.WHEAT)) return;

        if (!player.level().isClientSide) {
            fillHunger(player);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();

        if (player.isCreative()) return;
        if (!isInstalled(player)) return;
        if (isEatingWheat(player)) return;

        if (!player.getMainHandItem().isEmpty()) return;
        if (!level.getBlockState(pos).is(Blocks.GRASS_BLOCK)) return;
        if (!player.canEat(false)) return;

        if (level.isClientSide) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        if (!level.mayInteract(player, pos)) return;

        level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
        player.getFoodData().eat(GRASS_FOOD, GRASS_SAT);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}