package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.PowerTier;

public class AdvancedMaceratorBlockEntity extends MachineBlockEntity {
	public AdvancedMaceratorBlockEntity() {
		super(FTBICElectricBlocks.ADVANCED_MACERATOR.blockEntity.get(), 2, 2);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputPowerTier = PowerTier.MV;
		energyCapacity = 12000;
		energyUse = 20;
		shouldAccelerate = true;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.macerating;
	}
}