package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CookingRecipeResults extends MachineRecipeResults {
	private final RecipeType<? extends AbstractCookingRecipe> recipeType;
	private List<AbstractCookingRecipe> allRecipes;
	private Set<Item> validItems;

	public CookingRecipeResults(RecipeType<? extends AbstractCookingRecipe> s) {
		recipeType = s;
	}

	public List<AbstractCookingRecipe> getAllRecipes(Level level) {
		if (allRecipes == null) {
			allRecipes = new ArrayList<>(level.getRecipeManager().getAllRecipesFor(recipeType));
		}

		return allRecipes;
	}

	@Override
	public MachineProcessingResult createResult(Level level, ItemStack[] inputs) {
		for (AbstractCookingRecipe recipe : getAllRecipes(level)) {
			if (recipe.getIngredients().get(0).test(inputs[0])) {
				return new MachineProcessingResult(new StackWithChance(recipe.getResultItem(), 1D), recipe.getCookingTime() / 2);
			}
		}

		return MachineProcessingResult.NONE;
	}

	@Override
	public boolean canInsert(Level level, int slot, ItemStack item) {
		if (slot == 0) {
			if (validItems == null) {
				validItems = new HashSet<>();

				for (AbstractCookingRecipe recipe : getAllRecipes(level)) {
					Arrays.stream(recipe.getIngredients().get(0).getItems()).map(ItemStack::getItem).forEach(validItems::add);
				}
			}

			return validItems.contains(item.getItem());
		}

		return false;
	}
}
