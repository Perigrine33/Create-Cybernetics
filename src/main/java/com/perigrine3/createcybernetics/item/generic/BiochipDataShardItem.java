package com.perigrine3.createcybernetics.item.generic;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

public class BiochipDataShardItem extends DataShardItem {

    public static final long TOTAL_TICKS = 28L * 24000L;

    public static final String TAG_ID = "cc_biochip_id";
    public static final String TAG_PROGRESS = "cc_biochip_progress";
    public static final String TAG_DONE = "cc_biochip_done";
    public static final String TAG_OWNER_UUID = "cc_biochip_owner_uuid";
    public static final String TAG_OWNER_NAME = "cc_biochip_owner_name";

    public BiochipDataShardItem(Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = getTagOrNull(stack);

        boolean done = tag != null && tag.getBoolean(TAG_DONE);
        if (done) {
            String name = tag.getString(TAG_OWNER_NAME);
            if (name != null && !name.isBlank()) {
                tooltip.add(Component.literal(name + ".dhf" ).withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
            } else {
                tooltip.add(Component.translatable("item.createcybernetics.data_shard_biochip.downloaded").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
            }
            return;
        }

        long ticks = (tag == null) ? 0L : Math.max(0L, tag.getLong(TAG_PROGRESS));

        double pct = (TOTAL_TICKS <= 0) ? 0.0 : (100.0 * (double) ticks / (double) TOTAL_TICKS);
        tooltip.add(Component.translatable(String.format("item.createcybernetics.data_shard_biochip.downloading", Math.max(0.0, Math.min(100.0, pct)))).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }

    public static CompoundTag getOrCreateTag(ItemStack stack) {
        CustomData cd = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = cd.copyTag();
        return (tag == null) ? new CompoundTag() : tag;
    }

    public static CompoundTag getTagOrNull(ItemStack stack) {
        CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
        if (cd == null) return null;
        CompoundTag tag = cd.copyTag();
        return (tag == null || tag.isEmpty()) ? null : tag;
    }

    public static void setTag(ItemStack stack, CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            stack.set(DataComponents.CUSTOM_DATA, null);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }
}
