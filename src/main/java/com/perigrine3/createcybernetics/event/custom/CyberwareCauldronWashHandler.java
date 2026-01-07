package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.api.ICyberwareItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class CyberwareCauldronWashHandler {
    private CyberwareCauldronWashHandler() {}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level == null) return;
        BlockState state = level.getBlockState(event.getPos());
        if (!state.is(Blocks.WATER_CAULDRON)) return;

        InteractionHand hand = event.getHand();
        ItemStack stack = event.getEntity().getItemInHand(hand);
        if (stack.isEmpty()) return;
        if (!(stack.getItem() instanceof ICyberwareItem cw)) return;
        if (!cw.isDyeable(stack)) return;
        if (!stack.has(DataComponents.DYED_COLOR)) return;
        if (!level.isClientSide) {
            stack.remove(DataComponents.DYED_COLOR);

            if (!event.getEntity().getAbilities().instabuild) {
                LayeredCauldronBlock.lowerFillLevel(state, level, event.getPos());
            }
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
    }
}
