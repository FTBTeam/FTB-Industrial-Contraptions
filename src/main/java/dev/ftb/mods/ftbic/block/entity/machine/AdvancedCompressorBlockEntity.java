package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;

public class AdvancedCompressorBlockEntity extends MachineBlockEntity {
	public AdvancedCompressorBlockEntity() {
		super(FTBICElectricBlocks.ADVANCED_COMPRESSOR);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		shouldAccelerate = true;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.compressing;
	}
}