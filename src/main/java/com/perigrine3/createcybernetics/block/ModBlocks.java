package com.perigrine3.createcybernetics.block;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.item.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(CreateCybernetics.MODID);



//OREBLOCKS
    public static final DeferredBlock<Block> TITANIUMORE_BLOCK = registerBlock("titaniumore_block",
            () -> new DropExperienceBlock(UniformInt.of(2, 4), BlockBehaviour.Properties.of()
                    .strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)), true);
    public static final DeferredBlock<Block> DEEPSLATE_TITANIUMORE_BLOCK = registerBlock("deepslate_titaniumore_block",
            () -> new DropExperienceBlock(UniformInt.of(3, 6), BlockBehaviour.Properties.of()
                    .strength(3f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE)), true);

//BUILDING BLOCKS
    public static final DeferredBlock<Block> TITANIUM_BLOCK = registerBlock("titanium_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(6f).requiresCorrectToolForDrops().sound(SoundType.METAL)), true);
    public static final DeferredBlock<Block> RAW_TITANIUM_BLOCK = registerBlock("raw_titanium_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE)), true);

//FUNCTIONAL BLOCKS
    public static final DeferredBlock<Block> SURGERY_CHAMBER_BOTTOM = registerBlock("surgery_chamber",
        () -> new SurgeryChamberBlockBottom(BlockBehaviour.Properties.of()
                .noOcclusion().sound(SoundType.METAL)), true);
    public static final DeferredBlock<Block> SURGERY_CHAMBER_TOP = registerBlock("surgery_chamber_top",
            () -> new SurgeryChamberBlockTop(BlockBehaviour.Properties.of()
                    .noOcclusion().sound(SoundType.METAL)), false);




    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block, boolean registerItem) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);

        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
