package dev.ftb.mods.ftbic.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

import java.util.function.Consumer;

public class FTBICCircuitRecipes extends FTBICRecipesGen {
	public FTBICCircuitRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(ELECTRONIC_CIRCUIT)
				.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL))
				.group(MODID + ":electronic_circuit")
				.pattern("CCC")
				.pattern("RMR")
				.pattern("CCC")
				.define('C', COPPER_CABLE)
				.define('R', REDSTONE)
				.define('M', INDUSTRIAL_GRADE_METAL)
				.save(consumer, shapedLoc("electronic_circuit_h"));

		ShapedRecipeBuilder.shaped(ELECTRONIC_CIRCUIT)
				.unlockedBy("has_item", has(INDUSTRIAL_GRADE_METAL))
				.group(MODID + ":electronic_circuit")
				.pattern("CRC")
				.pattern("CMC")
				.pattern("CRC")
				.define('C', COPPER_CABLE)
				.define('R', REDSTONE)
				.define('M', INDUSTRIAL_GRADE_METAL)
				.save(consumer, shapedLoc("electronic_circuit_v"));

		ShapedRecipeBuilder.shaped(ADVANCED_CIRCUIT)
				.unlockedBy("has_item", has(ELECTRONIC_CIRCUIT))
				.group(MODID + ":advanced_circuit")
				.pattern("RGR")
				.pattern("QCQ")
				.pattern("RGR")
				.define('R', REDSTONE)
				.define('G', GLOWSTONE)
				.define('Q', SILICON)
				.define('C', ELECTRONIC_CIRCUIT)
				.save(consumer, shapedLoc("advanced_circuit_h"));

		ShapedRecipeBuilder.shaped(ADVANCED_CIRCUIT)
				.unlockedBy("has_item", has(ELECTRONIC_CIRCUIT))
				.group(MODID + ":advanced_circuit")
				.pattern("RQR")
				.pattern("GCG")
				.pattern("RQR")
				.define('R', REDSTONE)
				.define('G', GLOWSTONE)
				.define('Q', SILICON)
				.define('C', ELECTRONIC_CIRCUIT)
				.save(consumer, shapedLoc("advanced_circuit_v"));

		ShapedRecipeBuilder.shaped(IRIDIUM_CIRCUIT, 2)
				.unlockedBy("has_item", has(ADVANCED_CIRCUIT))
				.group(MODID + ":iridium_circuit")
				.pattern("AGA")
				.pattern("CIC")
				.pattern("AGA")
				.define('A', ADVANCED_ALLOY)
				.define('G', GRAPHENE)
				.define('I', IRIDIUM_ALLOY)
				.define('C', ADVANCED_CIRCUIT)
				.save(consumer, shapedLoc("iridium_circuit_h"));

		ShapedRecipeBuilder.shaped(IRIDIUM_CIRCUIT, 2)
				.unlockedBy("has_item", has(ADVANCED_CIRCUIT))
				.group(MODID + ":iridium_circuit")
				.pattern("ACA")
				.pattern("GIG")
				.pattern("ACA")
				.define('A', ADVANCED_ALLOY)
				.define('G', GRAPHENE)
				.define('I', IRIDIUM_ALLOY)
				.define('C', ADVANCED_CIRCUIT)
				.save(consumer, shapedLoc("iridium_circuit_v"));
	}
}
