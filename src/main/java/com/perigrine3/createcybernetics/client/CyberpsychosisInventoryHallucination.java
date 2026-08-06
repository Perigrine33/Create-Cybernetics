package com.perigrine3.createcybernetics.client;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class CyberpsychosisInventoryHallucination {

    private static final int MIN_SCREEN_OPEN_TICKS = 20;

    private static final int LEVEL_1_MIN_INTERVAL_TICKS = 4 * 60 * 20;
    private static final int LEVEL_1_MAX_INTERVAL_TICKS = 8 * 60 * 20;

    private static final int LEVEL_2_MIN_INTERVAL_TICKS = 2 * 60 * 20;
    private static final int LEVEL_2_MAX_INTERVAL_TICKS = 5 * 60 * 20;

    private static final int LEVEL_3_MIN_INTERVAL_TICKS = 1 * 60 * 20;
    private static final int LEVEL_3_MAX_INTERVAL_TICKS = 3 * 60 * 20;

    private static final int LEVEL_1_MIN_DURATION_TICKS = 10;
    private static final int LEVEL_1_MAX_DURATION_TICKS = 18;

    private static final int LEVEL_2_MIN_DURATION_TICKS = 12;
    private static final int LEVEL_2_MAX_DURATION_TICKS = 28;

    private static final int LEVEL_3_MIN_DURATION_TICKS = 16;
    private static final int LEVEL_3_MAX_DURATION_TICKS = 40;

    private static final float LEVEL_1_APPEARANCE_CHANCE = 0.80F;
    private static final float LEVEL_2_APPEARANCE_CHANCE = 0.72F;
    private static final float LEVEL_3_APPEARANCE_CHANCE = 0.65F;

    private static final List<Item> GENERIC_FAKE_ITEMS = List.of(
            Items.IRON_SWORD,
            Items.IRON_PICKAXE,
            Items.IRON_AXE,
            Items.IRON_SHOVEL,
            Items.SHEARS,
            Items.FLINT_AND_STEEL,
            Items.SPIDER_EYE,
            Items.ENDER_PEARL,
            Items.CLOCK,
            Items.COMPASS
    );

    private static UUID trackedPlayerId;
    private static AbstractContainerScreen<?> trackedScreen;

    private static int trackedSeverity;
    private static int screenOpenTicks;
    private static int nextEventTick = -1;
    private static int hallucinationEndTick = -1;

    private static Slot appearanceSlot;
    private static ItemStack fakeAppearance = ItemStack.EMPTY;

    private static Slot movementSlot;
    private static int movementOffsetX;
    private static int movementOffsetY;

    private CyberpsychosisInventoryHallucination() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.isPaused()) {
            return;
        }

        if (trackedPlayerId == null || !trackedPlayerId.equals(player.getUUID())) {
            resetAll();
            trackedPlayerId = player.getUUID();
        }

        MobEffectInstance rejection = getEffect(player, ModEffects.CYBERWARE_REJECTION);

        if (rejection == null || !player.isAlive()) {
            resetHallucination();
            trackedSeverity = 0;
            nextEventTick = -1;
            trackedScreen = null;
            screenOpenTicks = 0;
            return;
        }

        int severity = Mth.clamp(rejection.getAmplifier() + 1, 1, 3);

        if (trackedSeverity != severity || nextEventTick < 0) {
            trackedSeverity = severity;
            scheduleNextEvent(player, severity);
        }

        if (!(minecraft.screen instanceof AbstractContainerScreen<?> screen)) {
            trackedScreen = null;
            screenOpenTicks = 0;
            resetHallucination();
            return;
        }

        if (trackedScreen != screen) {
            trackedScreen = screen;
            screenOpenTicks = 0;
            resetHallucination();
        } else {
            screenOpenTicks++;
        }

        int now = player.tickCount;

        if (hallucinationEndTick >= 0) {
            if (now >= hallucinationEndTick || shouldAbort(screen)) {
                resetHallucination();
                scheduleNextEvent(player, severity);
            }

            return;
        }

        if (now < nextEventTick) {
            return;
        }

        if (screenOpenTicks < MIN_SCREEN_OPEN_TICKS) {
            return;
        }

        if (shouldAbort(screen)) {
            return;
        }

        beginHallucination(player, screen, severity);
    }

    public static ItemStack getRenderedStack(Slot slot, ItemStack realStack) {
        if (slot == null || realStack == null || realStack.isEmpty()) {
            return realStack;
        }

        if (slot != appearanceSlot || fakeAppearance.isEmpty()) {
            return realStack;
        }

        return fakeAppearance;
    }

    public static int getRenderOffsetX(Slot slot) {
        if (slot == movementSlot) {
            return movementOffsetX;
        }

        return 0;
    }

    public static int getRenderOffsetY(Slot slot) {
        if (slot == movementSlot) {
            return movementOffsetY;
        }

        return 0;
    }

    public static boolean hasRenderOffset(Slot slot) {
        return slot == movementSlot && (movementOffsetX != 0 || movementOffsetY != 0);
    }

    private static void beginHallucination(LocalPlayer player, AbstractContainerScreen<?> screen, int severity) {
        List<Slot> candidates = getCandidateSlots(player, screen);

        if (candidates.isEmpty()) {
            scheduleRetry(player);
            return;
        }

        Slot selected = candidates.get(player.getRandom().nextInt(candidates.size()));
        int duration = getDuration(player, severity);
        float appearanceChance = getAppearanceChance(severity);

        if (player.getRandom().nextFloat() < appearanceChance) {
            beginAppearanceHallucination(player, selected);
        } else {
            beginMovementHallucination(player, selected, severity);
        }

        hallucinationEndTick = player.tickCount + duration;
    }

    private static void beginAppearanceHallucination(LocalPlayer player, Slot slot) {
        ItemStack realStack = slot.getItem();
        Item fakeItem = chooseFakeItem(player, realStack);

        if (fakeItem == realStack.getItem()) {
            beginMovementHallucination(player, slot, trackedSeverity);
            return;
        }

        ItemStack renderedStack = new ItemStack(fakeItem);
        renderedStack.setCount(realStack.getCount());

        appearanceSlot = slot;
        fakeAppearance = renderedStack;

        movementSlot = null;
        movementOffsetX = 0;
        movementOffsetY = 0;
    }

    private static void beginMovementHallucination(LocalPlayer player, Slot slot, int severity) {
        int maximumOffset = severity >= 3 ? 3 : 2;

        int offsetX;
        int offsetY;

        do {
            offsetX = Mth.nextInt(player.getRandom(), -maximumOffset, maximumOffset);
            offsetY = Mth.nextInt(player.getRandom(), -maximumOffset, maximumOffset);
        } while (offsetX == 0 && offsetY == 0);

        movementSlot = slot;
        movementOffsetX = offsetX;
        movementOffsetY = offsetY;

        appearanceSlot = null;
        fakeAppearance = ItemStack.EMPTY;
    }

    private static List<Slot> getCandidateSlots(LocalPlayer player, AbstractContainerScreen<?> screen) {
        List<Slot> candidates = new ArrayList<>();
        Slot hoveredSlot = screen.getSlotUnderMouse();

        for (Slot slot : screen.getMenu().slots) {
            if (slot == null || slot == hoveredSlot) {
                continue;
            }

            if (slot.container != player.getInventory()) {
                continue;
            }

            if (!slot.isActive() || !slot.hasItem()) {
                continue;
            }

            ItemStack stack = slot.getItem();

            if (stack.isEmpty()) {
                continue;
            }

            candidates.add(slot);
        }

        return candidates;
    }

    private static Item chooseFakeItem(LocalPlayer player, ItemStack realStack) {
        Item realItem = realStack.getItem();

        if (realItem == Items.STICK || realItem == Items.BLAZE_ROD) {
            return randomSword(player);
        }

        if (realItem == Items.BONE) {
            return player.getRandom().nextBoolean() ? Items.WITHER_SKELETON_SKULL : Items.IRON_SWORD;
        }

        if (realItem == Items.ROTTEN_FLESH) {
            return player.getRandom().nextBoolean() ? Items.COOKED_BEEF : Items.SPIDER_EYE;
        }

        if (realItem == Items.SPIDER_EYE) {
            return player.getRandom().nextBoolean() ? Items.ENDER_PEARL : Items.ROTTEN_FLESH;
        }

        if (realItem == Items.IRON_INGOT) {
            return Items.NETHERITE_INGOT;
        }

        if (realItem == Items.GOLD_INGOT) {
            return Items.COPPER_INGOT;
        }

        if (realItem == Items.COPPER_INGOT) {
            return Items.GOLD_INGOT;
        }

        if (realItem == Items.COAL) {
            return Items.CHARCOAL;
        }

        if (realItem == Items.CHARCOAL) {
            return Items.COAL;
        }

        if (realItem == Items.FLINT) {
            return Items.COAL;
        }

        if (realItem == Items.BREAD) {
            return player.getRandom().nextBoolean() ? Items.ROTTEN_FLESH : Items.SPIDER_EYE;
        }

        if (realItem == Items.APPLE) {
            return player.getRandom().nextBoolean() ? Items.POISONOUS_POTATO : Items.SPIDER_EYE;
        }

        if (realItem == Items.POTATO) {
            return Items.POISONOUS_POTATO;
        }

        if (realItem == Items.CARROT) {
            return Items.GOLDEN_CARROT;
        }

        if (realItem == Items.WOODEN_SWORD) {
            return Items.IRON_SWORD;
        }

        if (realItem == Items.WOODEN_PICKAXE) {
            return Items.IRON_PICKAXE;
        }

        if (realItem == Items.WOODEN_AXE) {
            return Items.IRON_AXE;
        }

        if (realItem == Items.WOODEN_SHOVEL) {
            return Items.IRON_SHOVEL;
        }

        if (realItem == Items.STONE_SWORD) {
            return Items.DIAMOND_SWORD;
        }

        if (realItem == Items.STONE_PICKAXE) {
            return Items.DIAMOND_PICKAXE;
        }

        if (realItem == Items.STONE_AXE) {
            return Items.DIAMOND_AXE;
        }

        if (realItem == Items.STONE_SHOVEL) {
            return Items.DIAMOND_SHOVEL;
        }

        if (realStack.is(ItemTags.SWORDS)) {
            return randomDifferentItem(player, realItem, Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.DIAMOND_SWORD);
        }

        if (realStack.is(ItemTags.PICKAXES)) {
            return randomDifferentItem(player, realItem, Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE);
        }

        if (realStack.is(ItemTags.AXES)) {
            return randomDifferentItem(player, realItem, Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.DIAMOND_AXE);
        }

        if (realStack.is(ItemTags.SHOVELS)) {
            return randomDifferentItem(player, realItem, Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL, Items.DIAMOND_SHOVEL);
        }

        Item selected;

        do {
            selected = GENERIC_FAKE_ITEMS.get(player.getRandom().nextInt(GENERIC_FAKE_ITEMS.size()));
        } while (selected == realItem);

        return selected;
    }

    private static Item randomSword(LocalPlayer player) {
        return switch (player.getRandom().nextInt(4)) {
            case 0 -> Items.WOODEN_SWORD;
            case 1 -> Items.STONE_SWORD;
            case 2 -> Items.IRON_SWORD;
            default -> Items.DIAMOND_SWORD;
        };
    }

    private static Item randomDifferentItem(LocalPlayer player, Item realItem, Item... candidates) {
        Item selected;

        do {
            selected = candidates[player.getRandom().nextInt(candidates.length)];
        } while (selected == realItem && candidates.length > 1);

        return selected;
    }

    private static boolean shouldAbort(AbstractContainerScreen<?> screen) {
        if (!screen.getMenu().getCarried().isEmpty()) {
            return true;
        }

        if (screen.isDragging()) {
            return true;
        }

        Slot hoveredSlot = screen.getSlotUnderMouse();

        return hoveredSlot != null && (hoveredSlot == appearanceSlot || hoveredSlot == movementSlot);
    }

    private static int getDuration(LocalPlayer player, int severity) {
        if (severity <= 1) {
            return Mth.nextInt(player.getRandom(), LEVEL_1_MIN_DURATION_TICKS, LEVEL_1_MAX_DURATION_TICKS);
        }

        if (severity == 2) {
            return Mth.nextInt(player.getRandom(), LEVEL_2_MIN_DURATION_TICKS, LEVEL_2_MAX_DURATION_TICKS);
        }

        return Mth.nextInt(player.getRandom(), LEVEL_3_MIN_DURATION_TICKS, LEVEL_3_MAX_DURATION_TICKS);
    }

    private static float getAppearanceChance(int severity) {
        return switch (severity) {
            case 1 -> LEVEL_1_APPEARANCE_CHANCE;
            case 2 -> LEVEL_2_APPEARANCE_CHANCE;
            default -> LEVEL_3_APPEARANCE_CHANCE;
        };
    }

    private static void scheduleNextEvent(LocalPlayer player, int severity) {
        int minimum;
        int maximum;

        if (severity <= 1) {
            minimum = LEVEL_1_MIN_INTERVAL_TICKS;
            maximum = LEVEL_1_MAX_INTERVAL_TICKS;
        } else if (severity == 2) {
            minimum = LEVEL_2_MIN_INTERVAL_TICKS;
            maximum = LEVEL_2_MAX_INTERVAL_TICKS;
        } else {
            minimum = LEVEL_3_MIN_INTERVAL_TICKS;
            maximum = LEVEL_3_MAX_INTERVAL_TICKS;
        }

        nextEventTick = player.tickCount + Mth.nextInt(player.getRandom(), minimum, maximum);
    }

    private static void scheduleRetry(LocalPlayer player) {
        nextEventTick = player.tickCount + Mth.nextInt(player.getRandom(), 20 * 20, 40 * 20);
    }

    private static MobEffectInstance getEffect(LocalPlayer player, Holder<MobEffect> effect) {
        for (MobEffectInstance instance : player.getActiveEffects()) {
            if (instance != null && instance.is(effect)) {
                return instance;
            }
        }

        return null;
    }

    private static void resetHallucination() {
        hallucinationEndTick = -1;

        appearanceSlot = null;
        fakeAppearance = ItemStack.EMPTY;

        movementSlot = null;
        movementOffsetX = 0;
        movementOffsetY = 0;
    }

    private static void resetAll() {
        trackedPlayerId = null;
        trackedScreen = null;

        trackedSeverity = 0;
        screenOpenTicks = 0;
        nextEventTick = -1;

        resetHallucination();
    }
}