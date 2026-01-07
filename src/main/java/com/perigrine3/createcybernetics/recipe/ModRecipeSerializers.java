package com.perigrine3.createcybernetics.recipe;

import com.perigrine3.createcybernetics.CreateCybernetics;
import com.perigrine3.createcybernetics.recipe.CyberwarePrimaryDyeRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeSerializers {
    private ModRecipeSerializers() {}

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, CreateCybernetics.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CyberwarePrimaryDyeRecipe>> CYBERWARE_PRIMARY_DYE =
            SERIALIZERS.register("cyberware_primary_dye",
                    () -> new SimpleCraftingRecipeSerializer<>(CyberwarePrimaryDyeRecipe::new));

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
