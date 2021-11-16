package dev.ftb.mods.ftbic.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.UpgradeRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class FTBICBatteryRecipes extends FTBICRecipesGen {
	public FTBICBatteryRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(SINGLE_USE_BATTERY)
				.unlockedBy("has_item", has(COAL))
				.group(MODID + ":single_use_battery")
				.pattern("C")
				.pattern("R")
				.pattern("O")
				.define('C', LV_CABLE)
				.define('O', COAL)
				.define('R', REDSTONE)
				.save(consumer, shapedLoc("single_use_battery"));

		ShapedRecipeBuilder.shaped(BATTERY)
				.unlockedBy("has_item", has(TIN_INGOT))
				.group(MODID + ":battery")
				.pattern(" C ")
				.pattern("TRT")
				.pattern("TST")
				.define('C', LV_CABLE)
				.define('T', TIN_INGOT)
				.define('R', REDSTONE)
				.define('S', SULFUR_DUST)
				.save(consumer, shapedLoc("battery"));

		UpgradeRecipeBuilder.smithing(Ingredient.of(BATTERY), Ingredient.of(ENERGY_CRYSTAL), CRYSTAL_BATTERY)
				.unlocks("has_item", has(ENERGY_CRYSTAL))
				.save(consumer, smithingLoc("crystal_battery"));

		UpgradeRecipeBuilder.smithing(Ingredient.of(CRYSTAL_BATTERY), Ingredient.of(GRAPHENE), GRAPHENE_BATTERY)
				.unlocks("has_item", has(GRAPHENE))
				.save(consumer, smithingLoc("graphene_battery"));

		UpgradeRecipeBuilder.smithing(Ingredient.of(GRAPHENE_BATTERY), Ingredient.of(IRIDIUM_ALLOY), IRIDIUM_BATTERY)
				.unlocks("has_item", has(IRIDIUM_ALLOY))
				.save(consumer, smithingLoc("iridium_battery"));
	}
}
