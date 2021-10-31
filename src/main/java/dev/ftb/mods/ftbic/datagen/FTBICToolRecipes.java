package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class FTBICToolRecipes extends FTBICRecipesGen {
	public FTBICToolRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		for (Item item : ForgeRegistries.ITEMS) {
			FoodProperties f = item.getFoodProperties();

			if (f != null && f.getEffects().isEmpty() && item != FTBICItems.CANNED_FOOD.get()) {
				int cans = (f.getNutrition() + (f.isMeat() ? 8 : 3)) / 4;

				if (cans > 0) {
					MachineRecipeBuilder.canning()
							.unlockedBy("has_item", has(item))
							.inputItem(Ingredient.of(EMPTY_CAN), cans)
							.inputItem(Ingredient.of(item))
							.outputItem(new ItemStack(CANNED_FOOD, cans))
							.save(consumer, canningLoc(item.getRegistryName().getPath()));
				}
			}
		}

		MachineRecipeBuilder.canning()
				.unlockedBy("has_item", has(EMPTY_CELL))
				.inputItem(Ingredient.of(EMPTY_CELL))
				.inputItem(Ingredient.of(Tags.Items.DYES_WHITE))
				.outputItem(new ItemStack(LIGHT_SPRAY_CAN))
				.save(consumer, canningLoc("light_spray_can"));

		MachineRecipeBuilder.canning()
				.unlockedBy("has_item", has(EMPTY_CELL))
				.inputItem(Ingredient.of(EMPTY_CELL))
				.inputItem(Ingredient.of(Tags.Items.DYES_BLACK))
				.outputItem(new ItemStack(DARK_SPRAY_CAN))
				.save(consumer, canningLoc("dark_spray_can"));
	}
}
