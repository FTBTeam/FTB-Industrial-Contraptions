package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class ReprocessingRecipeResults extends SimpleMachineRecipeResults {
	public ReprocessingRecipeResults() {
		super(FTBICRecipes.REPROCESSING);
	}

	@Override
	protected void addAdditionalRecipes(Level level, List<MachineRecipe> list) {
		MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "reprocessing/generated/scrap"));
		recipe.inputItems.add(new IngredientWithCount(Ingredient.of(ForgeRegistries.ITEMS.getValues().stream().map(ItemStack::new)), 1));
		recipe.outputItems.add(new StackWithChance(new ItemStack(FTBICItems.SCRAP.item.get()), 0.1D));
		list.add(recipe);
	}
}
