package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;

public class RollerBlockEntity extends MachineBlockEntity {
	public RollerBlockEntity() {
		super(FTBICElectricBlocks.ROLLER.blockEntity.get(), 1, 1);
		energyCapacity = 8000;
		baseEnergyUse = 20;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.rolling;
	}
}