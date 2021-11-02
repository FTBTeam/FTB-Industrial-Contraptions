package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;

public class CentrifugeExtractorBlockEntity extends MachineBlockEntity {
	public CentrifugeExtractorBlockEntity() {
		super(FTBICElectricBlocks.CENTRIFUGE_EXTRACTOR.blockEntity.get(), 2, 2);
		energyCapacity = 8000;
		baseEnergyUse = 20;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.extracting;
	}
}