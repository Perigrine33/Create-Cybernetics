package com.perigrine3.createcybernetics.datagen;

import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {

        dropSelf(ModBlocks.SURGERY_CHAMBER_BOTTOM.get());
        dropSelf(ModBlocks.SURGERY_CHAMBER_TOP.get());
        dropSelf(ModBlocks.ROBOSURGEON.get());
        dropSelf(ModBlocks.ENGINEERING_TABLE.get());
        dropSelf(ModBlocks.CHARGING_BLOCK.get());

        dropSelf(ModBlocks.TITANIUM_BLOCK.get());

        dropSelf(ModBlocks.SMOOTH_TITANIUM.get());
        dropSelf(ModBlocks.SMOOTH_TITANIUM_STAIRS.get());
        add(ModBlocks.SMOOTH_TITANIUM_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.SMOOTH_TITANIUM_SLAB.get()));

        dropSelf(ModBlocks.TITANIUM_GRATE.get());

        dropSelf(ModBlocks.TITANIUM_CLAD_COPPER.get());
        dropSelf(ModBlocks.TITANIUM_CLAD_COPPER_STAIRS.get());
        add(ModBlocks.TITANIUM_CLAD_COPPER_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.TITANIUM_CLAD_COPPER_SLAB.get()));

        dropSelf(ModBlocks.ETCHED_TITANIUM_COPPER.get());
        dropSelf(ModBlocks.ETCHED_TITANIUM_COPPER_STAIRS.get());
        add(ModBlocks.ETCHED_TITANIUM_COPPER_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.ETCHED_TITANIUM_COPPER_SLAB.get()));

        dropSelf(ModBlocks.RAW_TITANIUM_BLOCK.get());

        add(ModBlocks.TITANIUMORE_BLOCK.get(),
                block -> createOreDrop(ModBlocks.TITANIUMORE_BLOCK.get(), ModItems.RAWTITANIUM.get()));
        add(ModBlocks.DEEPSLATE_TITANIUMORE_BLOCK.get(),
                block -> createMultipleOreDrops(ModBlocks.DEEPSLATE_TITANIUMORE_BLOCK.get(), ModItems.RAWTITANIUM.get(), 2, 5));


        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        this.add(ModBlocks.DATURA_BUSH.get(), block -> this.applyExplosionDecay(
                block,LootTable.lootTable().withPool(LootPool.lootPool().when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.DATURA_BUSH.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3)))
                                .add(LootItem.lootTableItem(ModItems.DATURA_FLOWER.get()))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 3.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE))))
                                .withPool(LootPool.lootPool().when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.DATURA_BUSH.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2)))
                                .add(LootItem.lootTableItem(ModItems.DATURA_FLOWER.get()))
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                .apply(ApplyBonusCount.addUniformBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE))))));
    }


    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registryLookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
