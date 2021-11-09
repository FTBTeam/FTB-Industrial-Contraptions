package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.EnergyTier;

public class ReprocessorBlockEntity extends MachineBlockEntity {
	public ReprocessorBlockEntity() {
		super(FTBICElectricBlocks.REPROCESSOR.blockEntity.get(), 1, 1);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputEnergyTier = EnergyTier.MV;
		energyCapacity = 40000;
		energyUse = 80;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.reprocessing;
	}
}