package com.perigrine3.createcybernetics.datagen;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.block.ModBlocks;
import com.perigrine3.createcybernetics.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
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
    }
}
