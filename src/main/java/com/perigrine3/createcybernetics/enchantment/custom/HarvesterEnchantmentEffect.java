package com.perigrine3.createcybernetics.enchantment.custom;

import com.mojang.serialization.MapCodec;
import com.perigrine3.createcybernetics.item.ModItems;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record HarvesterEnchantmentEffect() implements EnchantmentEntityEffect {
    public static final MapCodec<HarvesterEnchantmentEffect> CODEC = MapCodec.unit(HarvesterEnchantmentEffect::new);

    @Override
    public void apply(ServerLevel serverLevel, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
        if (enchantmentLevel <= 0) return;
        if (!(entity instanceof LivingEntity living)) return;
        if (living.getHealth() > 0.0F) return;

        int count = switch (enchantmentLevel) {
            case 1 -> 1;
            case 2 -> (isMonsterSpecialDrop(entity) ? 1 : 2);
            case 3 -> (isMonsterSpecialDrop(entity) ? 1 : 3);
            default -> (isMonsterSpecialDrop(entity) ? 1 : 4);
        };

        ItemStack fixed = fixedDropFor(entity);
        if (!fixed.isEmpty()) {
            spawn(serverLevel, vec3, fixed);
            return;
        }

        Optional<HolderSet.Named<Item>> opt = BuiltInRegistries.ITEM.getTag(tagFor(entity));
        if (opt.isEmpty()) return;

        HolderSet.Named<Item> tagSet = opt.get();
        int size = tagSet.size();
        if (size <= 0) return;

        RandomSource rand = serverLevel.getRandom();
        for (int i = 0; i < count; i++) {
            Item item = tagSet.get(rand.nextInt(size)).value();
            if (item == null) continue;
            spawn(serverLevel, vec3, new ItemStack(item));
        }
    }

    private static boolean isMonsterSpecialDrop(Entity entity) {
        return entity instanceof Warden || entity instanceof Guardian || entity instanceof ElderGuardian || entity instanceof Ghast || entity instanceof EnderDragon;
    }

    private static ItemStack fixedDropFor(Entity entity) {
        if (entity instanceof Warden) {
            return new ItemStack(ModItems.BODYPART_WARDENESOPHAGUS.get());
        }
        if (entity instanceof Guardian || entity instanceof ElderGuardian) {
            return new ItemStack(ModItems.BODYPART_GUARDIANRETINA.get());
        }
        if (entity instanceof Ghast) {
            return new ItemStack(ModItems.BODYPART_GYROSCOPICBLADDER.get());
        }
        if (entity instanceof EnderDragon) {
            return new ItemStack(ModItems.WETWARE_FIREBREATHINGLUNGS.get());
        }
        return ItemStack.EMPTY;
    }

    private static net.minecraft.tags.TagKey<Item> tagFor(Entity entity) {
        if (entity instanceof AbstractFish) {
            return ModTags.Items.FISH_BODYPART_DROPS;
        }
        if (entity instanceof AbstractHorse || entity instanceof Sheep || entity instanceof Cow) {
            return ModTags.Items.GRASSFED_BODYPART_DROPS;
        }
        if (entity instanceof Villager || entity instanceof Pillager || entity instanceof Witch) {
            return ModTags.Items.HUMANOID_BODYPART_DROPS;
        }
        return ModTags.Items.BODYPART_DROPS;
    }

    private static void spawn(ServerLevel level, Vec3 pos, ItemStack stack) {
        ItemEntity drop = new ItemEntity(level, pos.x, pos.y, pos.z, stack);
        drop.setDefaultPickUpDelay();
        drop.setDeltaMovement(drop.getDeltaMovement().add(0.0D, 0.15D, 0.0D));
        level.addFreshEntity(drop);
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
