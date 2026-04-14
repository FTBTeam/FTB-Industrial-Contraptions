package dev.ftb.mods.ftbic.integration.jei;

import dev.ftb.mods.ftbic.FTBIC;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClientRecipeCache {
	private static IJeiRuntime runtime;
	private static final Map<RecipeType<?>, List<RecipeHolder<?>>> CACHE = new HashMap<>();

	public static synchronized void setRuntime(IJeiRuntime jeiRuntime) {
		runtime = jeiRuntime;
		dev.ftb.mods.ftbic.FTBIC.LOGGER.info("ClientRecipeCache.setRuntime: runtime set, CACHE has {} entries", CACHE.size());
		pushToJei();
	}

	public static synchronized void clearRuntime() {
		runtime = null;
	}

	public static synchronized void applySyncedRecipes(List<RecipeHolder<?>> recipes) {
		CACHE.clear();
		for (RecipeHolder<?> h : recipes) {
			RecipeType<?> t = h.value().getType();
			CACHE.computeIfAbsent(t, k -> new ArrayList<>()).add(h);
		}
		dev.ftb.mods.ftbic.FTBIC.LOGGER.info("ClientRecipeCache.applySyncedRecipes: received {} recipes across {} types",
				recipes.size(), CACHE.size());
		pushToJei();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static synchronized void showRecipesForType(RecipeType<?> vanillaType) {
		if (runtime == null) return;
		mezz.jei.api.recipe.types.IRecipeHolderType<?> jeiType = mezz.jei.api.recipe.types.IRecipeHolderType.create((RecipeType) vanillaType);
		runtime.getRecipesGui().showTypes(List.of((mezz.jei.api.recipe.types.IRecipeType) jeiType));
	}


	@SuppressWarnings({"unchecked", "rawtypes"})
	private static synchronized void pushToJei() {
		if (runtime == null) {
			dev.ftb.mods.ftbic.FTBIC.LOGGER.info("ClientRecipeCache.pushToJei: runtime is null, skipping");
			return;
		}

		for (Map.Entry<RecipeType<?>, List<RecipeHolder<?>>> entry : CACHE.entrySet()) {
			RecipeType<?> vanillaType = entry.getKey();
			List<RecipeHolder<?>> holders = entry.getValue();
			if (holders.isEmpty()) continue;

			IRecipeHolderType<?> jeiType = IRecipeHolderType.create((RecipeType) vanillaType);
			try {
				runtime.getRecipeManager().addRecipes((IRecipeType) jeiType, (List) holders);
				dev.ftb.mods.ftbic.FTBIC.LOGGER.info("ClientRecipeCache.pushToJei: pushed {} recipes for uid {}", holders.size(), jeiType.getUid());
			} catch (Throwable t) {
				dev.ftb.mods.ftbic.FTBIC.LOGGER.warn("ClientRecipeCache.pushToJei: failed for {}: {}", vanillaType, t.toString());
			}
		}
	}

	private ClientRecipeCache() {}
}
