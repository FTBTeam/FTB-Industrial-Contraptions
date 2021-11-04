package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.PowerTier;

public class AdvancedMaceratorBlockEntity extends MachineBlockEntity {
	public AdvancedMaceratorBlockEntity() {
		super(FTBICElectricBlocks.ADVANCED_MACERATOR.blockEntity.get(), 2, 2);
		inputPowerTier = PowerTier.MV;
		energyCapacity = 12000;
		baseEnergyUse = 20;
	}

	@Override
	public boolean shouldAccelerate() {
		return true;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.macerating;
	}
}