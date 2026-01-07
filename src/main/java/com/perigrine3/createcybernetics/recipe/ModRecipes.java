package com.perigrine3.createcybernetics.recipe;

import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, CreateCybernetics.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, CreateCybernetics.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EngineeringTableRecipe>> ENGINEERING_TABLE_SERIALIZER =
            SERIALIZERS.register("engineering_table", EngineeringTableRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<EngineeringTableRecipe>> ENGINEERING_TABLE_TYPE =
            TYPES.register("engineering_table", () -> new RecipeType<EngineeringTableRecipe>() {
                @Override
                public String toString() {
                    return "engineering_table";
                }
            });


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
