package dev.ftb.mods.ftbic.recipe.machines;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipe;
import dev.ftb.mods.ftbic.recipe.SimpleMachineRecipeResults;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
		if (FTBICConfig.MACHINES.SCRAP_CHANCE.get() <= 0D) {
			return;
		}

		MachineRecipe recipe = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "reprocessing/generated/scrap"));
		recipe.inputItems.add(new IngredientWithCount(Ingredient.of(ForgeRegistries.ITEMS.getValues().stream().map(ItemStack::new)), 1));
		recipe.outputItems.add(new StackWithChance(new ItemStack(FTBICItems.SCRAP.item.get()), FTBICConfig.MACHINES.SCRAP_CHANCE.get()));
		recipe.hideFromJEI = true;
		list.add(recipe);

		ItemStack stack = new ItemStack(Items.SNOW_BLOCK);
		stack.setHoverName(new TranslatableComponent("ftbic.any_item"));

		MachineRecipe recipeForJEI = new MachineRecipe(recipeSerializer.get(), new ResourceLocation(FTBIC.MOD_ID, "reprocessing/generated/scrap_jei"));
		recipeForJEI.inputItems.add(new IngredientWithCount(Ingredient.of(stack), 1));
		recipeForJEI.outputItems.add(new StackWithChance(new ItemStack(FTBICItems.SCRAP.item.get()), FTBICConfig.MACHINES.SCRAP_CHANCE.get()));
		list.add(recipeForJEI);
	}
}
