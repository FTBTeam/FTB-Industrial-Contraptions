package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.ItemPair;
import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FTBICDualMachineRecipeResults extends MachineRecipeResults {
	private final Supplier<MachineRecipeSerializer> recipeSerializer;
	private final Map<ItemPair, MachineProcessingResult> cache;

	public FTBICDualMachineRecipeResults(Supplier<MachineRecipeSerializer> s) {
		recipeSerializer = s;
		cache = new HashMap<>();
	}

	@Override
	public int getRequiredItems() {
		return 2;
	}

	@Override
	public MachineProcessingResult createResult(Level level, ItemStack[] inputs) {
		ItemPair key = new ItemPair(inputs[0].getItem(), inputs[1].getItem());
		MachineProcessingResult result = cache.get(key);

		if (result == null) {
			result = MachineProcessingResult.NONE;

			for (MachineRecipe recipe : level.getRecipeManager().getAllRecipesFor(recipeSerializer.get().recipeType)) {
				if (recipe.inputItems.size() == 2 && recipe.outputItems.size() >= 1) {
					IngredientWithCount cA = recipe.inputItems.get(0);
					IngredientWithCount cB = recipe.inputItems.get(1);

					if (cA.ingredient.test(inputs[0]) && cB.ingredient.test(inputs[1])) {
						result = new MachineProcessingResult(recipe);
						result.consume[0] = cA.count;
						result.consume[1] = cB.count;
						break;
					} else if (cA.ingredient.test(inputs[1]) && cB.ingredient.test(inputs[0])) {
						result = new MachineProcessingResult(recipe);
						result.consume[0] = cB.count;
						result.consume[1] = cA.count;
						break;
					}
				}
			}

			cache.put(key, result);
		}

		return result;
	}
}
