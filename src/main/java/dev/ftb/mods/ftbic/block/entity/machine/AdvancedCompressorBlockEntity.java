package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.PowerTier;

public class AdvancedCompressorBlockEntity extends MachineBlockEntity {
	public AdvancedCompressorBlockEntity() {
		super(FTBICElectricBlocks.ADVANCED_COMPRESSOR.blockEntity.get(), 2, 2);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputPowerTier = PowerTier.MV;
		energyCapacity = 8000;
		energyUse = 20;
		shouldAccelerate = true;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.compressing;
	}
}