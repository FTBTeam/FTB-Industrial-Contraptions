package dev.ftb.mods.ftbic.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;

import java.util.function.Consumer;

public class FTBICCableRecipes extends FTBICRecipesGen {
	public FTBICCableRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		// Wire from metal

		ShapedRecipeBuilder.shaped(COPPER_WIRE, 6)
				.unlockedBy("has_item", has(COPPER_INGOT))
				.group(MODID + ":cable")
				.pattern("MMM")
				.define('M', COPPER_INGOT)
				.save(consumer, shapedLoc("copper_wire"));

		ShapedRecipeBuilder.shaped(GOLD_WIRE, 6)
				.unlockedBy("has_item", has(GOLD_INGOT))
				.group(MODID + ":cable")
				.pattern("MMM")
				.define('M', GOLD_INGOT)
				.save(consumer, shapedLoc("gold_wire"));

		ShapedRecipeBuilder.shaped(ALUMINUM_WIRE, 3)
				.unlockedBy("has_item", has(ALUMINUM_INGOT))
				.group(MODID + ":cable")
				.pattern("MMM")
				.define('M', ALUMINUM_INGOT)
				.save(consumer, shapedLoc("aluminum_wire"));

		// Cable from metal + rubber

		ShapedRecipeBuilder.shaped(COPPER_CABLE, 6)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":cable")
				.pattern("RRR")
				.pattern("MMM")
				.pattern("RRR")
				.define('R', RUBBER)
				.define('M', COPPER_INGOT)
				.save(consumer, shapedLoc("copper_cable"));

		ShapedRecipeBuilder.shaped(GOLD_CABLE, 6)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":cable")
				.pattern("RRR")
				.pattern("MMM")
				.pattern("RRR")
				.define('R', RUBBER)
				.define('M', GOLD_INGOT)
				.save(consumer, shapedLoc("gold_cable"));

		ShapedRecipeBuilder.shaped(ALUMINUM_CABLE, 3)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":cable")
				.pattern("RRR")
				.pattern("MMM")
				.pattern("RRR")
				.define('R', RUBBER)
				.define('M', ALUMINUM_INGOT)
				.save(consumer, shapedLoc("aluminum_cable"));

		// Cable from wire + rubber

		ShapelessRecipeBuilder.shapeless(COPPER_CABLE)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":cable")
				.requires(COPPER_WIRE)
				.requires(RUBBER)
				.save(consumer, shapelessLoc("copper_cable"));

		ShapelessRecipeBuilder.shapeless(GOLD_CABLE)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":cable")
				.requires(GOLD_WIRE)
				.requires(RUBBER)
				.save(consumer, shapelessLoc("gold_cable"));

		ShapelessRecipeBuilder.shapeless(ALUMINUM_CABLE)
				.unlockedBy("has_item", has(RUBBER))
				.group(MODID + ":cable")
				.requires(ALUMINUM_WIRE)
				.requires(RUBBER)
				.requires(RUBBER)
				.save(consumer, shapelessLoc("aluminum_cable"));

		// Glass cable

		ShapedRecipeBuilder.shaped(GLASS_CABLE, 6)
				.unlockedBy("has_item", has(ENERGY_CRYSTAL))
				.group(MODID + ":cable")
				.pattern("GGG")
				.pattern(" C ")
				.pattern("GGG")
				.define('G', GLASS)
				.define('C', ENERGY_CRYSTAL)
				.save(consumer, shapedLoc("glass_cable"));
	}
}
