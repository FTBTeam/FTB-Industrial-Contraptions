package dev.ftb.mods.ftbic.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.ftb.mods.ftbic.FTBIC;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

/** Optional outputs are encoded by id: the supplying mods need not be installed during datagen. */
public class FTBICCompatRecipeProvider implements DataProvider {
    private final PackOutput.PathProvider recipes;

    public FTBICCompatRecipeProvider(PackOutput output) {
        recipes = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> saves = new ArrayList<>();
        alloy(output, saves, "conductive_alloy", 2, "ingots/iron", "ingots/copper");
        alloy(output, saves, "dark_steel", 2, "ingots/iron", "dusts/coal", "obsidians");
        alloy(output, saves, "end_steel", 2, "end_stones", "ingots/dark_steel", "obsidians");
        alloy(output, saves, "energetic_alloy", 2, "dusts/redstone", "ingots/conductive_alloy", "ingots/gold");
        alloy(output, saves, "pulsating_alloy", 2, "ingots/iron", "ender_pearls");
        alloy(output, saves, "redstone_alloy", 1, "dusts/redstone", "ingots/copper");
        alloy(output, saves, "vibrant_alloy", 2, "ingots/energetic_alloy", "ender_pearls", "dusts/glowstone");
        return CompletableFuture.allOf(saves.toArray(CompletableFuture[]::new));
    }

    private void alloy(CachedOutput output, List<CompletableFuture<?>> saves, String alloy, int count, String... tags) {
        JsonObject recipe = new JsonObject();
        JsonObject condition = new JsonObject();
        condition.addProperty("type", "neoforge:mod_loaded");
        condition.addProperty("modid", "enderio");
        JsonArray conditions = new JsonArray();
        conditions.add(condition);
        recipe.add("neoforge:conditions", conditions);
        recipe.addProperty("type", "ftbic:alloy_smelting");
        JsonArray inputs = new JsonArray();
        for (String tag : tags) {
            JsonObject input = new JsonObject();
            input.addProperty("count", 1);
            input.addProperty("ingredient", "#c:" + tag);
            inputs.add(input);
        }
        recipe.add("inputs", inputs);
        JsonObject item = new JsonObject();
        item.addProperty("id", "enderio:" + alloy + "_ingot");
        if (count != 1) item.addProperty("count", count);
        JsonObject result = new JsonObject();
        result.add("item", item);
        JsonArray results = new JsonArray();
        results.add(result);
        recipe.add("outputs", results);
        saves.add(DataProvider.saveStable(
                output, recipe, recipes.json(FTBIC.id("alloy_smelting/enderio_" + alloy + "_ingot"))));
    }

    @Override
    public String getName() {
        return "FTBIC compatibility recipes";
    }
}
