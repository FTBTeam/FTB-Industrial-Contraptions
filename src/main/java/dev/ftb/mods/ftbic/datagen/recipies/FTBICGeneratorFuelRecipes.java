package dev.ftb.mods.ftbic.datagen.recipies;

import dev.ftb.mods.ftbic.datagen.FTBICRecipesGen;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;
import java.util.function.Function;

public class FTBICGeneratorFuelRecipes extends FTBICRecipesGen {
	public FTBICGeneratorFuelRecipes(DataGenerator generator) {
		super(generator);
	}

	public static ResourceLocation basicGeneratorFuelLoc(String s) {
		return modLoc("basic_generator_fuel/" + s);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		Function<TagKey<Item>, InventoryChangeTrigger.TriggerInstance> tagKeyHas = (e) -> RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of(e).build());

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(COAL), 1600)
				.unlockedBy("has_item", tagKeyHas.apply(COAL))
				.save(consumer, basicGeneratorFuelLoc("coal"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(COAL_BLOCK), 14400)
				.unlockedBy("has_item", tagKeyHas.apply(COAL_BLOCK))
				.save(consumer, basicGeneratorFuelLoc("coal_block"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(SCRAP), 348)
				.unlockedBy("has_item", has(SCRAP))
				.save(consumer, basicGeneratorFuelLoc("scrap"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(PLANKS), 300)
				.unlockedBy("has_item", tagKeyHas.apply(PLANKS))
				.save(consumer, basicGeneratorFuelLoc("planks"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(LOGS_THAT_BURN), 300)
				.unlockedBy("has_item", tagKeyHas.apply(LOGS_THAT_BURN))
				.save(consumer, basicGeneratorFuelLoc("log"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(STICK), 100)
				.unlockedBy("has_item", tagKeyHas.apply(STICK))
				.save(consumer, basicGeneratorFuelLoc("stick"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(SAPLING), 100)
				.unlockedBy("has_item", tagKeyHas.apply(SAPLING))
				.save(consumer, basicGeneratorFuelLoc("sapling"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(SUGAR_CANE), 48)
				.unlockedBy("has_item", has(SUGAR_CANE))
				.save(consumer, basicGeneratorFuelLoc("sugar_cane"));

		MachineFuelRecipeBuilder.basicGenerator(Ingredient.of(CACTUS), 48)
				.unlockedBy("has_item", has(CACTUS))
				.save(consumer, basicGeneratorFuelLoc("cactus"));
	}
}
