package dev.ftb.mods.ftbic.datagen.recipes;

import dev.ftb.mods.ftbic.datagen.FTBICRecipesGen;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class FTBICToolRecipes extends FTBICRecipesGen {
	public FTBICToolRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		MachineRecipeBuilder.canning()
				.unlockedBy("has_item", has(FLUID_CELL.get()))
				.inputItem(waterCell())
				.inputItem(Ingredient.of(Tags.Items.DYES_WHITE))
				.outputItem(new ItemStack(LIGHT_SPRAY_CAN.get()))
				.save(consumer, canningLoc("light_spray_can"));

		MachineRecipeBuilder.canning()
				.unlockedBy("has_item", has(FLUID_CELL.get()))
				.inputItem(waterCell())
				.inputItem(Ingredient.of(Tags.Items.DYES_BLACK))
				.outputItem(new ItemStack(DARK_SPRAY_CAN.get()))
				.save(consumer, canningLoc("dark_spray_can"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(CANNED_FOOD.get()))
				.inputItem(Ingredient.of(CANNED_FOOD.get()))
				.outputItem(new ItemStack(PROTEIN_BAR.get()))
				.save(consumer, compressingLoc("protein_bar"));

		/*
		ShapedRecipeBuilder.shaped(LOCATION_CARD)
				.unlockedBy("has_item", has(ELECTRONIC_CIRCUIT))
				.group(MODID + ":location_card")
				.pattern(" P ")
				.pattern("PCP")
				.pattern(" P ")
				.define('C', ELECTRONIC_CIRCUIT)
				.define('P', Items.PAPER)
				.save(consumer, shapedLoc("location_card"));
		 */
	}
}
