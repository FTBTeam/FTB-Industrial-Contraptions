package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.FTBIC;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public final class ClientRecipeCache {
    private static IJeiRuntime runtime;
    private static final List<ItemStack> variants = new ArrayList<>();
    private static final Map<RecipeType<?>, List<RecipeHolder<?>>> CACHE = new HashMap<>();
    private static final Map<RecipeType<?>, List<RecipeHolder<?>>> PUSHED = new HashMap<>();
    private static final Set<ResourceKey<Recipe<?>>> PUSHED_KEYS = new HashSet<>();

    public static synchronized void setRuntime(IJeiRuntime jeiRuntime) {
        if (runtime == jeiRuntime) return;
        runtime = jeiRuntime;
        PUSHED.clear();
        PUSHED_KEYS.clear();
        pushToJei();
    }

    public static synchronized void clearRuntime() {
        runtime = null;
        PUSHED.clear();
        PUSHED_KEYS.clear();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static synchronized void applySyncedRecipes(List<RecipeHolder<?>> recipes) {
        removeFromJei();
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
        pushToJei();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static synchronized void showRecipesForType(RecipeType<?> vanillaType) {
        showRecipesForTypes(List.of(vanillaType));
    }

    public static synchronized void setSearchFilter(String text) {
        if (runtime == null) return;
        runtime.getIngredientFilter().setFilterText(text);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static synchronized void showRecipesForTypes(List<RecipeType<?>> vanillaTypes) {
        if (runtime == null) {
            return;
        }
        List<IRecipeType<?>> jeiTypes = new ArrayList<>();
        for (RecipeType<?> t : vanillaTypes) {
            jeiTypes.add((IRecipeType) IRecipeHolderType.create((RecipeType) t));
        }
        runtime.getRecipesGui().showTypes(jeiTypes);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static synchronized void pushToJei() {
        if (runtime == null) {
            return;
        }

        runtime.getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, variants);
        for (Map.Entry<RecipeType<?>, List<RecipeHolder<?>>> entry : CACHE.entrySet()) {
            RecipeType<?> vanillaType = entry.getKey();
            if (entry.getValue().isEmpty()) continue;
            List<RecipeHolder<?>> holders = new ArrayList<>();
            for (RecipeHolder<?> h : entry.getValue()) {
                ResourceKey<Recipe<?>> key = jeiKey(h.id());
                holders.add(key == h.id() ? h : new RecipeHolder(key, h.value()));
            }
            PUSHED.put(vanillaType, holders);

            IRecipeHolderType<?> jeiType = IRecipeHolderType.create((RecipeType) vanillaType);
            try {
                runtime.getRecipeManager().addRecipes((IRecipeType) jeiType, (List) holders);
            } catch (Throwable t) {
                FTBIC.LOGGER.warn("ClientRecipeCache.pushToJei: failed for {}: {}", vanillaType, t.toString());
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void removeFromJei() {
        if (runtime == null) return;
        runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, variants);
        for (var entry : PUSHED.entrySet()) {
            runtime.getRecipeManager()
                    .hideRecipes((IRecipeType) IRecipeHolderType.create((RecipeType) entry.getKey()), (List)
                            entry.getValue());
        }
        PUSHED.clear();
    }

    // JEI can hide recipes but cannot remove them. RecipeHolder equality uses only its ID,
    // so each synced snapshot needs fresh presentation IDs to replace changed recipes.
    private static ResourceKey<Recipe<?>> jeiKey(ResourceKey<Recipe<?>> id) {
        ResourceKey<Recipe<?>> key = id;
        for (int n = 1; !PUSHED_KEYS.add(key); n++) {
            key = ResourceKey.create(Registries.RECIPE, id.identifier().withSuffix("/ftbic_jei_" + n));
        }
        return key;
    }

    public static synchronized void disconnect() {
        runtime = null;
        CACHE.clear();
        PUSHED.clear();
        PUSHED_KEYS.clear();
        variants.clear();
        RefiningCatalog.update(List.of());
    }

    private ClientRecipeCache() {}
}
