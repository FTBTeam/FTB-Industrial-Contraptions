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

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static synchronized void applySyncedRecipes(List<RecipeHolder<?>> recipes) {
		CACHE.clear();
		RecipeType<?> ftbicSmelting = dev.ftb.mods.ftbic.recipe.FTBICRecipes.SMELTING.TYPE.get();
		double baseTicks = dev.ftb.mods.ftbic.FTBICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get();
		for (RecipeHolder<?> h : recipes) {
			if (h.value() instanceof net.minecraft.world.item.crafting.SmeltingRecipe sr) {
				net.minecraft.world.item.ItemStack result = sr.assemble(new net.minecraft.world.item.crafting.SingleRecipeInput(net.minecraft.world.item.ItemStack.EMPTY));
				if (result.isEmpty()) continue;
				net.minecraft.world.item.ItemStackTemplate template = new net.minecraft.world.item.ItemStackTemplate(
						result.typeHolder(), result.getCount(), result.getComponentsPatch());
				dev.ftb.mods.ftbic.recipe.MachineRecipe mr = new dev.ftb.mods.ftbic.recipe.MachineRecipe(
						dev.ftb.mods.ftbic.recipe.FTBICRecipes.SMELTING,
						List.of(new dev.ftb.mods.ftbic.util.IngredientWithCount(sr.input(), 1)),
						List.of(),
						List.of(new dev.ftb.mods.ftbic.util.StackWithChance(template, 1D)),
						List.of(),
						sr.cookingTime() / baseTicks,
						false);
				CACHE.computeIfAbsent(ftbicSmelting, k -> new ArrayList<>()).add(new RecipeHolder(h.id(), mr));
			} else {
				RecipeType<?> t = h.value().getType();
				CACHE.computeIfAbsent(t, k -> new ArrayList<>()).add(h);
			}
		}
		dev.ftb.mods.ftbic.FTBIC.LOGGER.info("ClientRecipeCache.applySyncedRecipes: received {} recipes across {} types",
				recipes.size(), CACHE.size());
		pushToJei();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static synchronized void showRecipesForType(RecipeType<?> vanillaType) {
		showRecipesForTypes(java.util.List.of(vanillaType));
	}

	/** Opens JEI's ingredient panel filtered by {@code text}. No-op when JEI isn't loaded. */
	public static synchronized void setSearchFilter(String text) {
		if (runtime == null) return;
		runtime.getIngredientFilter().setFilterText(text);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static synchronized void showRecipesForTypes(List<RecipeType<?>> vanillaTypes) {
		if (runtime == null) {
			dev.ftb.mods.ftbic.FTBIC.LOGGER.info("ClientRecipeCache.showRecipesForTypes: runtime is null");
			return;
		}
		List<mezz.jei.api.recipe.types.IRecipeType<?>> jeiTypes = new ArrayList<>();
		for (RecipeType<?> t : vanillaTypes) {
			jeiTypes.add((mezz.jei.api.recipe.types.IRecipeType) mezz.jei.api.recipe.types.IRecipeHolderType.create((RecipeType) t));
		}
		runtime.getRecipesGui().showTypes(jeiTypes);
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
