package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;

public class ReprocessorBlockEntity extends MachineBlockEntity {
	public ReprocessorBlockEntity() {
		super(FTBICElectricBlocks.REPROCESSOR);
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.reprocessing;
	}
}