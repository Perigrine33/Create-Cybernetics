package com.perigrine3.createcybernetics.entity.trade;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public final class RipperTradeLogic {

    private static final List<Item> RANDOM_INGOTS = List.of(
            Items.IRON_INGOT,
            Items.GOLD_INGOT,
            Items.COPPER_INGOT
    );

    private RipperTradeLogic() {
    }

    public static boolean isCyberware(ItemStack stack) {
        return !stack.isEmpty() && (
                stack.is(ModTags.Items.CYBERWARE_ITEM) ||
                        stack.is(ModTags.Items.SCAVENGED_CYBERWARE)
        );
    }

    public static boolean isScavengedCyberware(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModTags.Items.SCAVENGED_CYBERWARE);
    }

    public static boolean isFreshCyberware(ItemStack stack) {
        return !stack.isEmpty() && stack.is(ModTags.Items.CYBERWARE_ITEM);
    }

    public static ItemStack createTradeOutput(RandomSource random, ItemStack input) {
        if (input.isEmpty() || !isCyberware(input)) {
            return ItemStack.EMPTY;
        }

        if (isScavengedCyberware(input)) {
            return randomIngot(random, 1);
        }

        if (isFreshCyberware(input)) {
            if (random.nextBoolean()) {
                return new ItemStack(Items.EMERALD, 5);
            }
            return randomIngot(random, 5);
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack createRefurbishOutput(ItemStack scavengedInput) {
        if (!isScavengedCyberware(scavengedInput)) {
            return ItemStack.EMPTY;
        }

        Item fresh = getFreshVersion(scavengedInput.getItem());
        if (fresh == null || fresh == Items.AIR) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(fresh);
    }

    public static Item getFreshVersion(Item scavengedItem) {
        if (scavengedItem == null) {
            return null;
        }

        ResourceLocation key = BuiltInRegistries.ITEM.getKey(scavengedItem);
        if (key == null) {
            return null;
        }

        if (!CreateCybernetics.MODID.equals(key.getNamespace())) {
            return null;
        }

        String path = key.getPath();
        if (path == null || path.isBlank() || !path.startsWith("scavenged_")) {
            return null;
        }

        String suffix = path.substring("scavenged_".length());
        if (suffix.isBlank()) {
            return null;
        }

        for (Item item : BuiltInRegistries.ITEM) {
            if (item == null || item == Items.AIR) {
                continue;
            }

            ItemStack candidateStack = new ItemStack(item);

            if (!candidateStack.is(ModTags.Items.CYBERWARE_ITEM)) {
                continue;
            }

            if (candidateStack.is(ModTags.Items.SCAVENGED_CYBERWARE)) {
                continue;
            }

            if (candidateStack.is(ModTags.Items.BODY_PARTS)) {
                continue;
            }

            ResourceLocation candidateKey = BuiltInRegistries.ITEM.getKey(item);
            if (candidateKey == null) {
                continue;
            }

            if (!CreateCybernetics.MODID.equals(candidateKey.getNamespace())) {
                continue;
            }

            String candidatePath = candidateKey.getPath();
            if (candidatePath == null || candidatePath.isBlank()) {
                continue;
            }

            int firstUnderscore = candidatePath.indexOf('_');
            if (firstUnderscore < 0 || firstUnderscore + 1 >= candidatePath.length()) {
                continue;
            }

            String candidateSuffix = candidatePath.substring(firstUnderscore + 1);
            if (candidateSuffix.equals(suffix)) {
                return item;
            }
        }

        return null;
    }

    private static ItemStack randomIngot(RandomSource random, int count) {
        Item item = RANDOM_INGOTS.get(random.nextInt(RANDOM_INGOTS.size()));
        return new ItemStack(item, count);
    }
}