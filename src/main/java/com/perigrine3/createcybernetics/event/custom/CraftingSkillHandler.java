package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.attributes.ModAttributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class CraftingSkillHandler {
    private CraftingSkillHandler() {}

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        double mult = player.getAttributeValue(ModAttributes.CRAFTING_OUTPUT);
        if (!Double.isFinite(mult) || mult <= 1.0D) return;

        ItemStack crafted = event.getCrafting();
        if (crafted.isEmpty()) return;

        int baseCount = crafted.getCount();
        if (baseCount <= 0) return;

        // Total extra items to grant beyond vanilla output
        int extraTotal = (int) Math.floor(baseCount * (mult - 1.0D));
        if (extraTotal <= 0) return;

        AbstractContainerMenu menu = player.containerMenu;
        ItemStack carried = menu.getCarried();

        if (!carried.isEmpty() && ItemStack.isSameItemSameComponents(carried, crafted)) {
            int space = carried.getMaxStackSize() - carried.getCount();
            if (space > 0) {
                int add = Math.min(space, extraTotal);
                carried.grow(add);
                menu.setCarried(carried);
                menu.broadcastChanges();
                extraTotal -= add;
            }
        }

        while (extraTotal > 0) {
            int give = Math.min(extraTotal, crafted.getMaxStackSize());
            ItemStack extra = crafted.copy();
            extra.setCount(give);

            player.addItem(extra);
            extraTotal -= give;
        }
    }
}
