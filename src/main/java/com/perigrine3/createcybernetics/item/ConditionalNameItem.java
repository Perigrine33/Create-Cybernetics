package com.perigrine3.createcybernetics.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public class ConditionalNameItem extends Item {
    private final String normalKey;
    private final String northstarKey;

    public ConditionalNameItem(Properties props, String normalTranslationKey, String northstarTranslationKey) {
        super(props);
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
