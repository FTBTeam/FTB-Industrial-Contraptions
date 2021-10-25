package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.item.FTBICItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class FTBICNuclearRecipes extends FTBICRecipes {
	public FTBICNuclearRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(FTBICItems.REINFORCED_STONE.get(), 4)
				.unlockedBy("has_item", has(FTBICItems.ADVANCED_ALLOY.get()))
				.group(MODID + ":nuclear")
				.pattern("SSS")
				.pattern("SAS")
				.pattern("SSS")
				.define('S', Items.SMOOTH_STONE)
				.define('A', FTBICItems.ADVANCED_ALLOY.ingredient())
				.save(consumer, shapedLoc("reinforced_stone"));

		ShapedRecipeBuilder.shaped(FTBICItems.REINFORCED_GLASS.get(), 4)
				.unlockedBy("has_item", has(FTBICItems.REINFORCED_STONE.get()))
				.group(MODID + ":nuclear")
				.pattern("RGR")
				.pattern("G G")
				.pattern("RGR")
				.define('R', FTBICItems.REINFORCED_STONE.get())
				.define('G', GLASS)
				.save(consumer, shapedLoc("reinforced_glass"));
	}
}
