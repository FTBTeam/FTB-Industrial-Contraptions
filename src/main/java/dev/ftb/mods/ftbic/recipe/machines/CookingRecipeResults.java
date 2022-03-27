package dev.ftb.mods.ftbic.recipe.machines;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import dev.ftb.mods.ftbic.recipe.SimpleMachineRecipeResults;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Supplier;

public class CookingRecipeResults extends SimpleMachineRecipeResults {
	private final String type;
	private final RecipeType<? extends AbstractCookingRecipe> recipeType;

	public CookingRecipeResults(Supplier<MachineRecipeSerializer> s, String ty, RecipeType<? extends AbstractCookingRecipe> t) {
		super(s);
		type = ty;
		recipeType = t;
	}

	@Override
	protected void addAdditionalRecipes(Level level, List<MachineRecipe> list) {
		for (AbstractCookingRecipe cookingRecipe : level.getRecipeManager().getAllRecipesFor(recipeType)) {
			ResourceLocation id = cookingRecipe.getId();
			MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, type + "/generated/" + id.getNamespace() + "/" + id.getPath()));
			recipe.inputItems.add(new IngredientWithCount(cookingRecipe.getIngredients().get(0), 1));
			recipe.outputItems.add(new StackWithChance(cookingRecipe.getResultItem(), 1D));
			recipe.processingTime = cookingRecipe.getCookingTime() / 400D;
			list.add(recipe);
		}
	}
}