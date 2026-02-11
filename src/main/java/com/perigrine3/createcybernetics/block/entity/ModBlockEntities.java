package com.perigrine3.createcybernetics.block.entity;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.block.EngineeringTableBlock;
import com.perigrine3.createcybernetics.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CreateCybernetics.MODID);


    public static final Supplier<BlockEntityType<RobosurgeonBlockEntity>> ROBOSURGEON_BLOCKENTITY =
            BLOCK_ENTITIES.register("robosurgeon_blockentity", () -> BlockEntityType.Builder.of(
                    RobosurgeonBlockEntity::new, ModBlocks.ROBOSURGEON.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EngineeringTableBlockEntity>> ENGINEERING_TABLE_BLOCKENTITY =
            (ModBlocks.ENGINEERING_TABLE == null) ? null : BLOCK_ENTITIES.register("engineering_table",
                    () -> BlockEntityType.Builder.of(EngineeringTableBlockEntity::new, ModBlocks.ENGINEERING_TABLE.get()).build(null));

    public static final Supplier<BlockEntityType<HoloprojectorBlockEntity>> HOLOPROJECTOR_BLOCKENTITY =
            BLOCK_ENTITIES.register("holoprojector_blockentity", () -> BlockEntityType.Builder.of(
                    HoloprojectorBlockEntity::new, ModBlocks.HOLOPROJECTOR.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
