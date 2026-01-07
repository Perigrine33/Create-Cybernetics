package com.perigrine3.createcybernetics.item.generic;

import com.perigrine3.createcybernetics.entity.projectile.EmpGrenadeProjectile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class EmpGrenadeItem extends Item {

    public EmpGrenadeItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return throwGrenade(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        // Right-clicking a block should still throw the grenade.
        InteractionResultHolder<ItemStack> res = throwGrenade(context.getLevel(), player, context.getHand());
        return res.getResult();
    }

    private InteractionResultHolder<ItemStack> throwGrenade(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);

        level.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW,
                SoundSource.PLAYERS,
                0.6F,
                0.9F + (level.getRandom().nextFloat() * 0.2F)
        );

        if (!level.isClientSide) {
            ItemStack projStack = held.copyWithCount(1);
            projStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));

            EmpGrenadeProjectile grenade = new EmpGrenadeProjectile(level, player);
            grenade.setItem(projStack);

            // Same trajectory you already used
            grenade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.25F, 0.85F);

            level.addFreshEntity(grenade);
        }

        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }

        player.getCooldowns().addCooldown(this, 10);
        return InteractionResultHolder.sidedSuccess(held, level.isClientSide);
    }
}
