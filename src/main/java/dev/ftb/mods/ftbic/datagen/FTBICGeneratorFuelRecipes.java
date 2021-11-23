package dev.ftb.mods.ftbic.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class FTBICGeneratorFuelRecipes extends FTBICRecipesGen {
	public FTBICGeneratorFuelRecipes(DataGenerator generator) {
		super(generator);
	}

	public static ResourceLocation basicGeneratorFuelLoc(String s) {
		return modLoc("basic_generator_fuel/" + s);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(COAL), 1600)
				.unlockedBy("has_item", has(COAL))
				.save(consumer, basicGeneratorFuelLoc("coal"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(COAL_BLOCK), 14400)
				.unlockedBy("has_item", has(COAL_BLOCK))
				.save(consumer, basicGeneratorFuelLoc("coal_block"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(SCRAP), 348)
				.unlockedBy("has_item", has(SCRAP))
				.save(consumer, basicGeneratorFuelLoc("scrap"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(PLANKS), 300)
				.unlockedBy("has_item", has(PLANKS))
				.save(consumer, basicGeneratorFuelLoc("planks"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(LOGS_THAT_BURN), 300)
				.unlockedBy("has_item", has(LOGS_THAT_BURN))
				.save(consumer, basicGeneratorFuelLoc("log"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(STICK), 100)
				.unlockedBy("has_item", has(STICK))
				.save(consumer, basicGeneratorFuelLoc("stick"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(SAPLING), 100)
				.unlockedBy("has_item", has(SAPLING))
				.save(consumer, basicGeneratorFuelLoc("sapling"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(SUGAR_CANE), 48)
				.unlockedBy("has_item", has(SUGAR_CANE))
				.save(consumer, basicGeneratorFuelLoc("sugar_cane"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(CACTUS), 48)
				.unlockedBy("has_item", has(CACTUS))
				.save(consumer, basicGeneratorFuelLoc("cactus"));
	}
}
