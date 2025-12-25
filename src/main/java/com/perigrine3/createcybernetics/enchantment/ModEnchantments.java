package com.perigrine3.createcybernetics.enchantment;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.enchantment.custom.HarvesterEnchantmentEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> HARVESTER = ResourceKey.create(Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, "harvester"));



    public static void bootstrap(BootstrapContext<Enchantment> context) {
        var enchantments = context.lookup(Registries.ENCHANTMENT);
        var items = context.lookup(Registries.ITEM);

            register(context, HARVESTER, Enchantment.enchantment(Enchantment.definition(
                    items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                    items.getOrThrow(ItemTags.SWORD_ENCHANTABLE), 3, 3,
                    Enchantment.dynamicCost(5, 7),
                    Enchantment.dynamicCost(25, 8),
                    2, EquipmentSlotGroup.MAINHAND))
                    .exclusiveWith(enchantments.getOrThrow(EnchantmentTags.ON_RANDOM_LOOT))
                    .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
                            EnchantmentTarget.VICTIM, new HarvesterEnchantmentEffect()));

    }

    private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> key,
                                 Enchantment.Builder builder) {
        registry.register(key, builder.build(key.location()));
    }
}
