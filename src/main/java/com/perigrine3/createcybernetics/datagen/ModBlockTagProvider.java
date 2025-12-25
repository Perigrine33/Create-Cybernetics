package com.perigrine3.createcybernetics.datagen;

import com.momosoftworks.coldsweat.data.tag.ModBlockTags;
import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CreateCybernetics.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.TITANIUM_BLOCK.get())
                .add(ModBlocks.RAW_TITANIUM_BLOCK.get())
                .add(ModBlocks.TITANIUMORE_BLOCK.get())
                .add(ModBlocks.DEEPSLATE_TITANIUMORE_BLOCK.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.TITANIUM_BLOCK.get())
                .add(ModBlocks.RAW_TITANIUM_BLOCK.get())
                .add(ModBlocks.TITANIUMORE_BLOCK.get())
                .add(ModBlocks.DEEPSLATE_TITANIUMORE_BLOCK.get());

        tag(ModTags.Blocks.METAL_DETECTABLE)
                .add(ModBlocks.TITANIUM_BLOCK.get())
                .add(ModBlocks.RAW_TITANIUM_BLOCK.get())
                .add(ModBlocks.TITANIUMORE_BLOCK.get())
                .add(ModBlocks.DEEPSLATE_TITANIUMORE_BLOCK.get())
                .add(Blocks.IRON_ORE)
                .add(Blocks.DEEPSLATE_IRON_ORE)
                .add(Blocks.GOLD_ORE)
                .add(Blocks.DEEPSLATE_GOLD_ORE)
                .add(Blocks.COPPER_ORE)
                .add(Blocks.DEEPSLATE_COPPER_ORE)
                .add(Blocks.NETHER_GOLD_ORE)
                .add(Blocks.IRON_BLOCK)
                .add(Blocks.GOLD_BLOCK)
                .add(Blocks.COPPER_BLOCK)
                .add(Blocks.RAW_IRON_BLOCK)
                .add(Blocks.RAW_GOLD_BLOCK)
                .add(Blocks.RAW_COPPER_BLOCK)
                .add(Blocks.NETHERITE_BLOCK)
                .addOptional(ResourceLocation.fromNamespaceAndPath("create", "zinc_ore"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("create", "zinc_block"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("create", "raw_zinc_block"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("create", "brass_block"))
                .addOptional(ResourceLocation.fromNamespaceAndPath("create", "industrial_iron_block"));


    }
}
