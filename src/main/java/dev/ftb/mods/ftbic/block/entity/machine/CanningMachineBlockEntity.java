package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.PowerTier;

public class CanningMachineBlockEntity extends MachineBlockEntity {
	public CanningMachineBlockEntity() {
		super(FTBICElectricBlocks.CANNING_MACHINE.blockEntity.get(), 2, 1);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputPowerTier = PowerTier.MV;
		energyCapacity = 8000;
		energyUse = 10;
	}

	@Override
	public MachineRecipeSerializer getRecipeSerializer() {
		return FTBICRecipes.CANNING.get();
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.canning;
	}

	@Override
	public void shiftInputs() {
	}
}