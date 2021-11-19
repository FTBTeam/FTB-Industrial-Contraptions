package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;

public class AntimatterConstructorBlockEntity extends MachineBlockEntity {
	public AntimatterConstructorBlockEntity() {
		super(FTBICElectricBlocks.ANTIMATTER_CONSTRUCTOR);
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.reconstructing;
	}
}