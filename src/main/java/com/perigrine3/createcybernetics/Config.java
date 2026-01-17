package com.perigrine3.createcybernetics;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // --- Humanity ---
    public static final ModConfigSpec.IntValue HUMANITY = BUILDER
            .comment("Base Humanity Value")
            .defineInRange("humanity", 100, 50, 1000);




    public static final ModConfigSpec SPEC = BUILDER.build();

}
