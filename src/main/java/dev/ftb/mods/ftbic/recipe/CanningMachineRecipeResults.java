package dev.ftb.mods.ftbic.recipe;

import dev.ftb.mods.ftbic.FTBIC;
import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.item.FTBICItems;
import dev.ftb.mods.ftbic.util.FTBICUtils;
import dev.ftb.mods.ftbic.util.IngredientWithCount;
import dev.ftb.mods.ftbic.util.ItemPair;
import dev.ftb.mods.ftbic.util.MachineProcessingResult;
import dev.ftb.mods.ftbic.util.StackWithChance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CanningMachineRecipeResults extends MachineRecipeResults {
	private List<MachineRecipe> allRecipes;
	private Set<Item> validLeftItems, validRightItems;

	@Override
	public int getRequiredItems() {
		return 2;
	}

	@Override
	public Object createKey(ItemStack[] inputs) {
		return new ItemPair(inputs[0].getItem(), inputs[1].getItem());
	}

	@Override
	public List<MachineRecipe> getAllRecipes(Level level) {
		if (allRecipes == null) {
			allRecipes = new ArrayList<>(level.getRecipeManager().getAllRecipesFor(FTBICRecipes.CANNING.get().recipeType));

			if (FTBICConfig.ADD_CANNED_FOOD_RECIPES) {
				Ingredient canIngredient = Ingredient.of(FTBICItems.EMPTY_CAN.item.get());

				for (Item item : ForgeRegistries.ITEMS) {
					FoodProperties f = item.getFoodProperties();

					if (f != null && f.getNutrition() > 1 && f.getEffects().isEmpty() && !FTBICUtils.UNCANNABLE_FOOD.contains(item)) {
						int cans = (f.getNutrition() + (f.isMeat() ? 8 : 3)) / 4;

						if (cans > 0) {
							ResourceLocation id = item.getRegistryName();
							MachineRecipe recipe = new MachineRecipe(FTBICRecipes.CANNING.get(), new ResourceLocation(FTBIC.MOD_ID, "canning/generated/canned_food/" + id.getNamespace() + "/" + id.getPath()));
							recipe.inputItems.add(new IngredientWithCount(canIngredient, cans));
							recipe.inputItems.add(new IngredientWithCount(Ingredient.of(item), 1));
							recipe.outputItems.add(new StackWithChance(new ItemStack(FTBICItems.CANNED_FOOD.get(), cans), 1D));
							allRecipes.add(recipe);
						}
					}
				}
			}
		}

		return allRecipes;
	}

	@Override
	public MachineProcessingResult createResult(Level level, ItemStack[] inputs) {
		for (MachineRecipe recipe : getAllRecipes(level)) {
			if (recipe.inputItems.size() == 2 && recipe.outputItems.size() >= 1) {
				IngredientWithCount cA = recipe.inputItems.get(0);
				IngredientWithCount cB = recipe.inputItems.get(1);

				if (cA.ingredient.test(inputs[0]) && cB.ingredient.test(inputs[1])) {
					MachineProcessingResult result = new MachineProcessingResult(recipe);
					result.consume[0] = cA.count;
					result.consume[1] = cB.count;
					return result;
				}
			}
		}

		return MachineProcessingResult.NONE;
	}

	@Override
	public boolean canInsert(Level level, int slot, ItemStack item) {
		if (slot == 0) {
			if (validLeftItems == null) {
				validLeftItems = new HashSet<>();

				for (MachineRecipe recipe : getAllRecipes(level)) {
					if (recipe.inputItems.size() == 2 && recipe.outputItems.size() >= 1) {
						Arrays.stream(recipe.inputItems.get(0).ingredient.getItems()).map(ItemStack::getItem).forEach(validLeftItems::add);
					}
				}
			}

			return validLeftItems.contains(item.getItem());
		} else if (slot == 1) {
			if (validRightItems == null) {
				validRightItems = new HashSet<>();

				for (MachineRecipe recipe : getAllRecipes(level)) {
					if (recipe.inputItems.size() == 2 && recipe.outputItems.size() >= 1) {
						Arrays.stream(recipe.inputItems.get(1).ingredient.getItems()).map(ItemStack::getItem).forEach(validRightItems::add);
					}
				}
			}

			return validRightItems.contains(item.getItem());
		}

		return false;
	}
}
