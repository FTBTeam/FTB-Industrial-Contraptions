package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class SimpleMachineRecipeResults extends MachineRecipeResults {
	private final Supplier<MachineRecipeSerializer> recipeSerializer;
	private List<MachineRecipe> allRecipes;
	private Set<Item> validItems;

	public SimpleMachineRecipeResults(Supplier<MachineRecipeSerializer> s) {
		recipeSerializer = s;
	}

	public List<MachineRecipe> getAllRecipes(Level level) {
		if (allRecipes == null) {
			allRecipes = new ArrayList<>(level.getRecipeManager().getAllRecipesFor(recipeSerializer.get().recipeType));
		}

		return allRecipes;
	}

	@Override
	public MachineProcessingResult createResult(Level level, ItemStack[] inputs) {
		for (MachineRecipe recipe : getAllRecipes(level)) {
			if (recipe.inputItems.size() == 1 && recipe.outputItems.size() >= 1) {
				IngredientWithCount c = recipe.inputItems.get(0);

				if (c.ingredient.test(inputs[0])) {
					MachineProcessingResult result = new MachineProcessingResult(recipe);
					result.consume[0] = c.count;
					return result;
				}
			}
		}

		return MachineProcessingResult.NONE;
	}

	@Override
	public boolean canInsert(Level level, int slot, ItemStack item) {
		if (slot == 0) {
			if (validItems == null) {
				validItems = new HashSet<>();

				for (MachineRecipe recipe : getAllRecipes(level)) {
					if (recipe.inputItems.size() == 1 && recipe.outputItems.size() >= 1) {
						Arrays.stream(recipe.inputItems.get(0).ingredient.getItems()).map(ItemStack::getItem).forEach(validItems::add);
					}
				}
			}

			return validItems.contains(item.getItem());
		}

		return false;
	}
}
