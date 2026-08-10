package dev.ftb.mods.industrialcontraptions.integration.jei;

import dev.ftb.mods.industrialcontraptions.IC;
import dev.ftb.mods.industrialcontraptions.ICConfig;
import dev.ftb.mods.industrialcontraptions.recipe.ICRecipes;
import dev.ftb.mods.industrialcontraptions.recipe.MachineRecipe;
import dev.ftb.mods.industrialcontraptions.util.IngredientWithCount;
import dev.ftb.mods.industrialcontraptions.util.StackWithChance;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClientRecipeCache {
	private static IJeiRuntime runtime;
	private static final Map<RecipeType<?>, List<RecipeHolder<?>>> CACHE = new HashMap<>();

	public static synchronized void setRuntime(IJeiRuntime jeiRuntime) {
		runtime = jeiRuntime;
		pushToJei();
	}

	public static synchronized void clearRuntime() {
		runtime = null;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static synchronized void applySyncedRecipes(List<RecipeHolder<?>> recipes) {
		CACHE.clear();
		RecipeType<?> icSmelting = ICRecipes.SMELTING.TYPE.get();
		double baseTicks = ICConfig.MACHINES.MACHINE_RECIPE_BASE_TICKS.get();
		for (RecipeHolder<?> h : recipes) {
			if (h.value() instanceof SmeltingRecipe sr) {
				ItemStack result = sr.assemble(new SingleRecipeInput(ItemStack.EMPTY));
				if (result.isEmpty()) continue;
				ItemStackTemplate template = new ItemStackTemplate(
						result.typeHolder(), result.getCount(), result.getComponentsPatch());
				MachineRecipe mr = new MachineRecipe(
						ICRecipes.SMELTING,
						List.of(new IngredientWithCount(sr.input(), 1)),
						List.of(),
						List.of(new StackWithChance(template, 1D)),
						List.of(),
						sr.cookingTime() / baseTicks,
						false);
				CACHE.computeIfAbsent(icSmelting, k -> new ArrayList<>()).add(new RecipeHolder(h.id(), mr));
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

		for (Map.Entry<RecipeType<?>, List<RecipeHolder<?>>> entry : CACHE.entrySet()) {
			RecipeType<?> vanillaType = entry.getKey();
			List<RecipeHolder<?>> holders = entry.getValue();
			if (holders.isEmpty()) continue;

			IRecipeHolderType<?> jeiType = IRecipeHolderType.create((RecipeType) vanillaType);
			try {
				runtime.getRecipeManager().addRecipes((IRecipeType) jeiType, (List) holders);
			} catch (Throwable t) {
				IC.LOGGER.warn("ClientRecipeCache.pushToJei: failed for {}: {}", vanillaType, t.toString());
			}
		}
	}

	private ClientRecipeCache() {}
}
