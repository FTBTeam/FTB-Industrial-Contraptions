package dev.ftb.mods.ftbic.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

import java.util.function.Consumer;

public class FTBICNuclearRecipes extends FTBICRecipesGen {
	public FTBICNuclearRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(REINFORCED_STONE, 4)
				.unlockedBy("has_item", has(ADVANCED_ALLOY))
				.group(MODID + ":reinforced_stone")
				.pattern("SSS")
				.pattern("SAS")
				.pattern("SSS")
				.define('S', SMOOTH_STONE)
				.define('A', ADVANCED_ALLOY)
				.save(consumer, shapedLoc("reinforced_stone"));

		ShapedRecipeBuilder.shaped(REINFORCED_GLASS, 4)
				.unlockedBy("has_item", has(REINFORCED_STONE))
				.group(MODID + ":reinforced_glass")
				.pattern("RGR")
				.pattern("G G")
				.pattern("RGR")
				.define('R', REINFORCED_STONE)
				.define('G', GLASS)
				.save(consumer, shapedLoc("reinforced_glass"));

		ShapedRecipeBuilder.shaped(NUCLEAR_REACTOR)
				.unlockedBy("has_item", has(BASIC_GENERATOR))
				.group(MODID + ":nuclear_reactor")
				.pattern("RCR")
				.pattern("AGA")
				.pattern("RMR")
				.define('A', COPPER_PLATE)
				.define('R', REINFORCED_STONE)
				.define('M', ADVANCED_MACHINE_BLOCK)
				.define('C', IRIDIUM_CIRCUIT)
				.define('G', BASIC_GENERATOR)
				.save(consumer, shapedLoc("nuclear_reactor"));

		ShapedRecipeBuilder.shaped(COOLANT_10K)
				.unlockedBy("has_item", has(WATER_CELL))
				.group(MODID + ":coolant_10k")
				.pattern(" T ")
				.pattern("TCT")
				.pattern(" T ")
				.define('T', TIN_INGOT)
				.define('C', WATER_CELL)
				.save(consumer, shapedLoc("coolant_10k"));

		ShapedRecipeBuilder.shaped(COOLANT_30K)
				.unlockedBy("has_item", has(COOLANT_10K))
				.group(MODID + ":coolant_30k")
				.pattern("TTT")
				.pattern("CCC")
				.pattern("TTT")
				.define('T', TIN_INGOT)
				.define('C', COOLANT_10K)
				.save(consumer, shapedLoc("coolant_30k"));

		ShapedRecipeBuilder.shaped(COOLANT_60K)
				.unlockedBy("has_item", has(COOLANT_30K))
				.group(MODID + ":coolant_60k")
				.pattern("TCT")
				.pattern("TAT")
				.pattern("TCT")
				.define('T', TIN_INGOT)
				.define('C', COOLANT_30K)
				.define('A', COPPER_PLATE)
				.save(consumer, shapedLoc("coolant_60k"));
	}
}
