package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MachineRecipeResults {
	private final Supplier<MachineRecipeSerializer> recipeSerializer;
	private final Map<Item, MachineProcessingResult> cache;

	public MachineRecipeResults(Supplier<MachineRecipeSerializer> s) {
		recipeSerializer = s;
		cache = new HashMap<>();
	}

	public MachineProcessingResult getResult(Level level, Item item) {
		Item key = item.getItem();
		MachineProcessingResult result = cache.get(key);

		if (result == null) {
			result = MachineProcessingResult.NONE;
			ItemStack itemStack = new ItemStack(item.getItem());

			for (MachineRecipe recipe : level.getRecipeManager().getAllRecipesFor(recipeSerializer.get().recipeType)) {
				if (recipe.inputItems.size() == 1 && recipe.outputItems.size() >= 1) {
					IngredientWithCount c = recipe.inputItems.get(0);

					if (c.ingredient.test(itemStack)) {
						result = new MachineProcessingResult(recipe);
						result.consume[0] = c.count;
						break;
					}
				}
			}

			cache.put(key, result);
		}

		return result;
	}
}
