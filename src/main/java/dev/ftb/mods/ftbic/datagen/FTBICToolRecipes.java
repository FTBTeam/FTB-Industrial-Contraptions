package dev.ftb.mods.ftbic.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

import java.util.function.Consumer;

public class FTBICToolRecipes extends FTBICRecipes {
	public FTBICToolRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(TREE_TAP)
				.unlockedBy("has_item", has(PLANKS))
				.group(MODID + ":tree_tap")
				.pattern(" P ")
				.pattern("PPP")
				.pattern("P  ")
				.define('P', PLANKS)
				.save(consumer, shapedLoc("tree_tap"));
	}
}
