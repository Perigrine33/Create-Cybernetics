package com.perigrine3.createcybernetics.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;

public class ConditionalNameBlockItem extends BlockItem {
    private final String normalKey;
    private final String northstarKey;

    public ConditionalNameBlockItem(Block block, Item.Properties props, String normalTranslationKey, String northstarTranslationKey) {
        super(block, props);
        this.normalKey = normalTranslationKey;
        this.northstarKey = northstarTranslationKey;
    }

    @Override
    public Component getName(ItemStack stack) {
        return ModList.get().isLoaded("northstar")
                ? Component.translatable(northstarKey)
                : Component.translatable(normalKey);
    }
}
