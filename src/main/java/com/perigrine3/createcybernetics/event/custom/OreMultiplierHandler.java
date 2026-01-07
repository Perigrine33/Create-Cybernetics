package com.perigrine3.createcybernetics.event.custom;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.common.attributes.ModAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

@EventBusSubscriber(modid = CreateCybernetics.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class OreMultiplierHandler {
    private OreMultiplierHandler() {}

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null || player.isCreative()) return;

        if (!(event.getLevel() instanceof ServerLevel level)) return;

        double mult = player.getAttributeValue(ModAttributes.ORE_DROP_MULTIPLIER);
        if (!Double.isFinite(mult) || mult <= 1.0D) return;

        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        BlockEntity be = level.getBlockEntity(pos);
        ItemStack tool = player.getMainHandItem();

        List<ItemStack> drops = Block.getDrops(state, level, pos, be, player, tool);

        for (ItemStack drop : drops) {
            if (drop.isEmpty()) continue;
            if (!drop.is(Tags.Items.ORES)) continue;

            int base = drop.getCount();
            if (base <= 0) continue;

            int extra = (int) Math.floor(base * (mult - 1.0D));
            if (extra <= 0) continue;

            ItemStack extraStack = drop.copy();
            extraStack.setCount(extra);

            Block.popResource(level, pos, extraStack);
        }
    }
}
