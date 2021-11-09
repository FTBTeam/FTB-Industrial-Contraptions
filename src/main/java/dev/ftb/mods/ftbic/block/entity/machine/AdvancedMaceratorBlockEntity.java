package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.EnergyTier;

public class AdvancedMaceratorBlockEntity extends MachineBlockEntity {
	public AdvancedMaceratorBlockEntity() {
		super(FTBICElectricBlocks.ADVANCED_MACERATOR.blockEntity.get(), 2, 2);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputEnergyTier = EnergyTier.MV;
		energyCapacity = FTBICConfig.ADVANCED_MACERATOR_CAPACITY;
		energyUse = FTBICConfig.ADVANCED_MACERATOR_USE;
		shouldAccelerate = true;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.macerating;
	}
}