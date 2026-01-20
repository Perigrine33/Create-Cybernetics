package com.perigrine3.createcybernetics.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.perigrine3.createcybernetics.CreateCybernetics;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class FarmersDelightCompatRecipeProvider implements DataProvider {
    private final PackOutput output;
    private final PackOutput.PathProvider recipePath;

    public FarmersDelightCompatRecipeProvider(PackOutput output) {
        this.output = output;
        this.recipePath = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<ResourceLocation, JsonObject> toWrite = new LinkedHashMap<>();
        build(toWrite);

        return CompletableFuture.allOf(toWrite.entrySet().stream().map(e ->
                save(cache, e.getKey(), e.getValue())).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Cybernetic's Farmer's Delight Compat Recipes";
    }

    private CompletableFuture<?> save(CachedOutput cache, ResourceLocation id, JsonObject json) {
        Path path = recipePath.json(id);
        return DataProvider.saveStable(cache, json, path);
    }

    private void build(Map<ResourceLocation, JsonObject> out) {



// COOKING POT
        {// BRAIN STEW
            ResourceLocation id = cc("compat/farmersdelight/brain_stew");

            JsonArray ingredients = new JsonArray();
            ingredients.add(item("createcybernetics:cooked_brain"));
            ingredients.add(item("minecraft:potato"));
            ingredients.add(item("farmersdelight:onion"));

            JsonObject recipe = cookingPot("meals",
                    ingredients,
                    stack("createcybernetics:brain_stew", 1),
                    stack("minecraft:bowl", 1),
                    200, 0.35);
            out.put(id, withModLoaded(recipe, "farmersdelight"));
        }



// CUTTING BOARD
        {// GROUND OFFAL
            ResourceLocation id = cc("compat/farmersdelight/ground_offal_cuttingboard");
            JsonArray ingredients = new JsonArray();
            ingredients.add(tag("createcybernetics:offal"));
            JsonObject tool = tag("c:tools/knives");

            JsonArray results = new JsonArray();
            results.add(result("createcybernetics:ground_offal", 1, 1.0));

            JsonObject recipe = cuttingBoard(ingredients, tool, results, "farmersdelight:block.cutting_board.knife");
            out.put(id, withModLoaded(recipe, "farmersdelight"));
        }
    }




    // ---------------- JSON builders ----------------

    private static JsonObject withModLoaded(JsonObject root, String modid) {
        JsonArray conds = new JsonArray();
        conds.add(modLoaded(modid));
        root.add("neoforge:conditions", conds);
        return root;
    }

    private static JsonObject modLoaded(String modid) {
        JsonObject c = new JsonObject();
        c.addProperty("type", "neoforge:mod_loaded");
        c.addProperty("modid", modid);
        return c;
    }

    private static JsonObject registered(String registry, String value) {
        JsonObject c = new JsonObject();
        c.addProperty("type", "neoforge:registered");
        c.addProperty("registry", registry);
        c.addProperty("value", value);
        return c;
    }

    private static JsonObject cookingPot(String recipeBookTab, JsonArray ingredients, JsonObject result, JsonObject container, int cookingTime, double experience) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "farmersdelight:cooking");
        root.addProperty("recipe_book_tab", recipeBookTab);
        root.add("ingredients", ingredients);
        root.add("result", result);
        root.add("container", container);
        root.addProperty("cookingtime", cookingTime);
        root.addProperty("experience", experience);
        return root;
    }

    private static JsonObject cuttingBoard(JsonArray ingredients, JsonObject tool, JsonArray results, String sound) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "farmersdelight:cutting");
        root.add("ingredients", ingredients);
        root.add("tool", tool);
        root.add("result", results);
        root.addProperty("sound", sound);
        return root;
    }

    private static JsonObject item(String id) {
        JsonObject o = new JsonObject();
        o.addProperty("item", id);
        return o;
    }

    private static JsonObject tag(String tagId) {
        JsonObject o = new JsonObject();
        o.addProperty("tag", tagId);
        return o;
    }

    private static JsonObject stack(String id, int count) {
        JsonObject o = new JsonObject();
        o.addProperty("id", id);
        o.addProperty("count", count);
        return o;
    }

    private static JsonObject result(String id, int count, double chance) {
        JsonObject o = new JsonObject();
        o.addProperty("item", id);
        o.addProperty("count", count);
        if (chance < 1.0) o.addProperty("chance", chance);
        return o;
    }

    private static ResourceLocation cc(String path) {
        return ResourceLocation.fromNamespaceAndPath(CreateCybernetics.MODID, path);
    }
}
