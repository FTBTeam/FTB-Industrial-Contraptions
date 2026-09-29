package dev.ftb.mods.ftbic.client;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.item.RefiningItem;
import dev.ftb.mods.ftbic.material.RefiningCatalog;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.RefiningMaterialRecipe;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public final class ClientRecipeCache {
    private static RecipeViewerBridge viewer;
    private static final List<ItemStack> variants = new ArrayList<>();
    private static final Map<RecipeType<?>, List<RecipeHolder<?>>> CACHE = new HashMap<>();

    public static synchronized RecipeViewerBridge viewer() {
        return viewer;
    }

    public static synchronized void setViewer(RecipeViewerBridge bridge) {
        viewer = bridge;
        if (viewer != null) viewer.add(variants, CACHE);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static synchronized void applySyncedRecipes(List<RecipeHolder<?>> recipes) {
        if (viewer != null) viewer.remove(variants);
        RefiningCatalog.update(recipes);
        variants.clear();
        for (var id : RefiningCatalog.materials().keySet().stream().sorted().toList()) {
            for (var item : List.of(
                    FTBICItems.CRUSHED_ORE.get(), FTBICItems.WASHED_ORE.get(), FTBICItems.REFINED_CONCENTRATE.get())) {
                variants.add(RefiningItem.stack(item, id, 1));
            }
        }
        CACHE.clear();
        RecipeType<?> ftbicSmelting = FTBICRecipes.SMELTING.TYPE.get();
        double baseTicks = FTBICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get();
        for (RecipeHolder<?> h : recipes) {
            if (h.value() instanceof RefiningMaterialRecipe) continue;
            if (h.value() instanceof SmeltingRecipe sr) {
                ItemStack result = sr.assemble(new SingleRecipeInput(ItemStack.EMPTY));
                if (result.isEmpty()) continue;
                ItemStackTemplate template =
                        new ItemStackTemplate(result.typeHolder(), result.getCount(), result.getComponentsPatch());
                MachineRecipe mr = new MachineRecipe(
                        FTBICRecipes.SMELTING,
                        List.of(new IngredientWithCount(sr.input(), 1)),
                        List.of(),
                        List.of(new StackWithChance(template, 1D)),
                        List.of(),
                        sr.cookingTime() / baseTicks,
                        false);
                CACHE.computeIfAbsent(ftbicSmelting, k -> new ArrayList<>()).add(new RecipeHolder(h.id(), mr));
            } else {
                RecipeType<?> t = h.value().getType();
                CACHE.computeIfAbsent(t, k -> new ArrayList<>()).add(h);
            }
        }
        if (viewer != null) viewer.add(variants, CACHE);
    }

    public static synchronized void showRecipesForType(RecipeType<?> vanillaType) {
        showRecipesForTypes(List.of(vanillaType));
    }

    public static synchronized void setSearchFilter(String text) {
        if (viewer != null) viewer.setSearchFilter(text);
    }

    public static synchronized void showRecipesForTypes(List<RecipeType<?>> vanillaTypes) {
        if (viewer != null) viewer.showTypes(vanillaTypes);
    }

    public static synchronized void disconnect() {
        viewer = null;
        CACHE.clear();
        variants.clear();
        RefiningCatalog.update(List.of());
    }

    private ClientRecipeCache() {}
}
