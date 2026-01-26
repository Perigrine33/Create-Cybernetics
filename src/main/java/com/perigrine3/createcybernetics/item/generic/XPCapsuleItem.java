package com.perigrine3.createcybernetics.item.generic;

import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import java.util.List;

public class XPCapsuleItem extends Item {

    public static final String NBT_XP_POINTS = "cc_xp_capsule_points";
    public static final String NBT_OWNER = "cc_xp_capsule_owner";

    public XPCapsuleItem(Properties props) {
        super(props);
    }

    public static ItemStack makeCapsule(String ownerName, int xpPoints) {
        ItemStack stack = new ItemStack(ModItems.XP_CAPSULE.get());

        int clamped = Math.max(0, xpPoints);

        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putInt(NBT_XP_POINTS, clamped);
            tag.putString(NBT_OWNER, ownerName);
        });

        stack.set(
                DataComponents.CUSTOM_NAME,
                Component.translatable(
                        "item.createcybernetics.expcapsule.playername", ownerName
                ).withStyle(ChatFormatting.GREEN)
        );


        return stack;
    }

    public static int getStoredXp(ItemStack stack) {
        CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
        if (cd == null || cd.isEmpty()) return 0;

        CompoundTag tag = cd.copyTag();
        return Math.max(0, tag.getInt(NBT_XP_POINTS));
    }

    public static String getOwner(ItemStack stack) {
        CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
        if (cd == null || cd.isEmpty()) return "";
        return cd.copyTag().getString(NBT_OWNER);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }

        if (!(player instanceof ServerPlayer sp)) {
            return InteractionResultHolder.pass(stack);
        }

        int xp = getStoredXp(stack);
        if (xp <= 0) {
            return InteractionResultHolder.pass(stack);
        }

        sp.giveExperiencePoints(xp);
        stack.shrink(1);

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int xp = getStoredXp(stack);
        if (xp > 0) {
            tooltip.add(Component.translatable("item.createcybernetics.expcapsule.stored" + xp).withStyle(ChatFormatting.GRAY));
        }

        String owner = getOwner(stack);
        if (!owner.isEmpty()) {
            tooltip.add(Component.translatable("item.createcybernetics.expcapsule.owner" + owner).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
