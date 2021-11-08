package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.PowerTier;

public class AdvancedPoweredFurnaceBlockEntity extends MachineBlockEntity {
	public AdvancedPoweredFurnaceBlockEntity() {
		super(FTBICElectricBlocks.ADVANCED_POWERED_FURNACE.blockEntity.get(), 2, 2);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputPowerTier = PowerTier.MV;
		energyCapacity = 4160;
		energyUse = 30;
		shouldAccelerate = true;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.smelting;
	}
}