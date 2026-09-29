package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.client.RecipeViewerBridge;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

public final class JeiRecipeViewer implements RecipeViewerBridge {
    private final IJeiRuntime runtime;
    private final Map<RecipeType<?>, List<RecipeHolder<?>>> pushed = new HashMap<>();
    private final Set<ResourceKey<Recipe<?>>> pushedKeys = new HashSet<>();

    public JeiRecipeViewer(IJeiRuntime runtime) {
        this.runtime = runtime;
    }

    public IJeiRuntime runtime() {
        return runtime;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void add(List<ItemStack> variants, Map<RecipeType<?>, List<RecipeHolder<?>>> recipes) {
        runtime.getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, variants);
        for (Map.Entry<RecipeType<?>, List<RecipeHolder<?>>> entry : recipes.entrySet()) {
            RecipeType<?> vanillaType = entry.getKey();
            if (entry.getValue().isEmpty()) continue;
            List<RecipeHolder<?>> holders = new ArrayList<>();
            for (RecipeHolder<?> h : entry.getValue()) {
                ResourceKey<Recipe<?>> key = jeiKey(h.id());
                holders.add(key == h.id() ? h : new RecipeHolder(key, h.value()));
            }
            pushed.put(vanillaType, holders);

            IRecipeHolderType<?> jeiType = IRecipeHolderType.create((RecipeType) vanillaType);
            try {
                runtime.getRecipeManager().addRecipes((IRecipeType) jeiType, (List) holders);
            } catch (Throwable t) {
                FTBIC.LOGGER.warn("JeiRecipeViewer.add: failed for {}: {}", vanillaType, t.toString());
            }
        }
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void remove(List<ItemStack> variants) {
        runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, variants);
        for (var entry : pushed.entrySet()) {
            runtime.getRecipeManager()
                    .hideRecipes((IRecipeType) IRecipeHolderType.create((RecipeType) entry.getKey()), (List)
                            entry.getValue());
        }
        pushed.clear();
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void showTypes(List<RecipeType<?>> types) {
        List<IRecipeType<?>> jeiTypes = new ArrayList<>();
        for (RecipeType<?> t : types) {
            jeiTypes.add((IRecipeType) IRecipeHolderType.create((RecipeType) t));
        }
        runtime.getRecipesGui().showTypes(jeiTypes);
    }

    @Override
    public void setSearchFilter(String text) {
        runtime.getIngredientFilter().setFilterText(text);
    }

    // JEI can hide recipes but cannot remove them. RecipeHolder equality uses only its ID,
    // so each synced snapshot needs fresh presentation IDs to replace changed recipes.
    private ResourceKey<Recipe<?>> jeiKey(ResourceKey<Recipe<?>> id) {
        ResourceKey<Recipe<?>> key = id;
        for (int n = 1; !pushedKeys.add(key); n++) {
            key = ResourceKey.create(Registries.RECIPE, id.identifier().withSuffix("/ftbic_jei_" + n));
        }
        return key;
    }
}
