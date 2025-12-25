package com.perigrine3.createcybernetics.item.generic;

import com.perigrine3.createcybernetics.common.FaceplateAliasHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

public class FaceplateMaskItem extends Item {

    public FaceplateMaskItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        if (!(player instanceof ServerPlayer sp)) {
            return InteractionResultHolder.pass(stack);
        }

        ItemStack one = stack.copy();
        one.setCount(1);

        if (!FaceplateAliasHandler.apply(sp, one)) {
            return InteractionResultHolder.pass(stack);
        }

        if (!sp.isCreative()) {
            stack.shrink(1);
        }

        sp.getInventory().setChanged();
        sp.inventoryMenu.broadcastChanges();

        return InteractionResultHolder.sidedSuccess(stack, false);
    }

}
