package com.perigrine3.createcybernetics.item.generic;

import com.perigrine3.createcybernetics.api.CyberwareSlot;
import com.perigrine3.createcybernetics.common.capabilities.ModAttachments;
import com.perigrine3.createcybernetics.common.capabilities.PlayerCyberwareData;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DataShardItem extends Item {
    public DataShardItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.isEmpty() || !stack.is(ModTags.Items.DATA_SHARDS)) { return InteractionResultHolder.pass(stack); }
        if (level.isClientSide) { return InteractionResultHolder.success(stack); }
        if (!(player instanceof ServerPlayer sp)) { return InteractionResultHolder.pass(stack); }
        if (!sp.hasData(ModAttachments.CYBERWARE)) { return InteractionResultHolder.pass(stack); }

        PlayerCyberwareData data = sp.getData(ModAttachments.CYBERWARE);
        if (data == null) return InteractionResultHolder.pass(stack);
        if (!data.hasSpecificItem(ModItems.BRAINUPGRADES_CHIPWARESLOTS.get(), CyberwareSlot.BRAIN)) { return InteractionResultHolder.pass(stack); }

        int target = -1;
        for (int i = 0; i < PlayerCyberwareData.CHIPWARE_SLOT_COUNT; i++) {
            if (data.getChipwareStack(i).isEmpty()) {
                target = i;
                break;
            }
        }

        if (target == -1) { return InteractionResultHolder.pass(stack); }

        ItemStack one = stack.copyWithCount(1);
        data.setChipwareStack(target, one);
        stack.shrink(1);

        data.setDirty();
        sp.syncData(ModAttachments.CYBERWARE);

        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
}
