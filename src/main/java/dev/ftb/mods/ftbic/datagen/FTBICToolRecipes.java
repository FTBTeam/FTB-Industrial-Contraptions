package dev.ftb.mods.ftbic.datagen;

import dev.ftb.mods.ftbic.item.FluidCellItem;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class FTBICToolRecipes extends FTBICRecipesGen {
	public FTBICToolRecipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	public void add(Consumer<FinishedRecipe> consumer) {
		MachineRecipeBuilder.canning()
				.unlockedBy("has_item", has(FLUID_CELL))
				.inputItem(new NBTIngredientExt(FluidCellItem.setFluid(new ItemStack(FLUID_CELL), Fluids.WATER)))
				.inputItem(Ingredient.of(Tags.Items.DYES_WHITE))
				.outputItem(new ItemStack(LIGHT_SPRAY_CAN))
				.save(consumer, canningLoc("light_spray_can"));

		MachineRecipeBuilder.canning()
				.unlockedBy("has_item", has(FLUID_CELL))
				.inputItem(new NBTIngredientExt(FluidCellItem.setFluid(new ItemStack(FLUID_CELL), Fluids.WATER)))
				.inputItem(Ingredient.of(Tags.Items.DYES_BLACK))
				.outputItem(new ItemStack(DARK_SPRAY_CAN))
				.save(consumer, canningLoc("dark_spray_can"));

		MachineRecipeBuilder.compressing()
				.unlockedBy("has_item", has(CANNED_FOOD))
				.inputItem(Ingredient.of(CANNED_FOOD))
				.outputItem(new ItemStack(PROTEIN_BAR))
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
