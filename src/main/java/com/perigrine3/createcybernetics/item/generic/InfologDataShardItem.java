package com.perigrine3.createcybernetics.item.generic;

import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class InfologDataShardItem extends DataShardItem {

    public InfologDataShardItem(Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() || !stack.is(ModTags.Items.DATA_SHARDS)) {
            return InteractionResultHolder.pass(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    public boolean isDyeable(ItemStack stack) {
        return true;
    }
}
