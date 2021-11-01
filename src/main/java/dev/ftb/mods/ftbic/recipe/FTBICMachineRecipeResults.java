package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class FTBICMachineRecipeResults extends MachineRecipeResults {
	private final Supplier<MachineRecipeSerializer> recipeSerializer;

	public FTBICMachineRecipeResults(Supplier<MachineRecipeSerializer> s) {
		recipeSerializer = s;
	}

	@Override
	public MachineProcessingResult createResult(Level level, ItemStack[] inputs) {
		for (MachineRecipe recipe : level.getRecipeManager().getAllRecipesFor(recipeSerializer.get().recipeType)) {
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
}
