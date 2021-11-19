package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;

public class RollerBlockEntity extends MachineBlockEntity {
	public RollerBlockEntity() {
		super(FTBICElectricBlocks.ROLLER);
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.rolling;
	}
}