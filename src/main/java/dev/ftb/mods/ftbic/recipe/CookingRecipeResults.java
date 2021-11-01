package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class CookingRecipeResults extends MachineRecipeResults {
	private final RecipeType<? extends AbstractCookingRecipe> recipeType;

	public CookingRecipeResults(RecipeType<? extends AbstractCookingRecipe> s) {
		recipeType = s;
	}

	@Override
	public MachineProcessingResult createResult(Level level, ItemStack[] inputs) {
		for (AbstractCookingRecipe recipe : level.getRecipeManager().getAllRecipesFor(recipeType)) {
			if (recipe.getIngredients().get(0).test(inputs[0])) {
				return new MachineProcessingResult(new StackWithChance(recipe.getResultItem(), 1D), recipe.getCookingTime() / 2);
			}
		}

		return MachineProcessingResult.NONE;
	}
}
