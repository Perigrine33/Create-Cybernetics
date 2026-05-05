package com.perigrine3.createcybernetics.worldgen;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.worldgen.structure.NetherCavernJigsawStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModStructureTypes {

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, CreateCybernetics.MODID);

    public static final Supplier<StructureType<NetherCavernJigsawStructure>> NETHER_CAVERN_JIGSAW =
            STRUCTURE_TYPES.register("nether_cavern_jigsaw", () -> () -> NetherCavernJigsawStructure.CODEC);



    public static void register(IEventBus modEventBus) {
        STRUCTURE_TYPES.register(modEventBus);
    }

}