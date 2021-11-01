package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.ItemPair;
import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DualMachineRecipeResults {
	private final Supplier<MachineRecipeSerializer> recipeSerializer;
	private final Map<ItemPair, MachineProcessingResult> cache;

	public DualMachineRecipeResults(Supplier<MachineRecipeSerializer> s) {
		recipeSerializer = s;
		cache = new HashMap<>();
	}

	public MachineProcessingResult getResult(Level level, Item itemA, Item itemB) {
		ItemPair key = new ItemPair(itemA, itemB);
		MachineProcessingResult result = cache.get(key);

		if (result == null) {
			result = MachineProcessingResult.NONE;
			ItemStack itemStackA = new ItemStack(itemA);
			ItemStack itemStackB = new ItemStack(itemB);

			for (MachineRecipe recipe : level.getRecipeManager().getAllRecipesFor(recipeSerializer.get().recipeType)) {
				if (recipe.inputItems.size() == 2 && recipe.outputItems.size() >= 1) {
					IngredientWithCount cA = recipe.inputItems.get(0);
					IngredientWithCount cB = recipe.inputItems.get(1);

					if (cA.ingredient.test(itemStackA) && cB.ingredient.test(itemStackB)) {
						result = new MachineProcessingResult(recipe);
						result.consume[0] = cA.count;
						result.consume[1] = cB.count;
						break;
					} else if (cA.ingredient.test(itemStackB) && cB.ingredient.test(itemStackA)) {
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
