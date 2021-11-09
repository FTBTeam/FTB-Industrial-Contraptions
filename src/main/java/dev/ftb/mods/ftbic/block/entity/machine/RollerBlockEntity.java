package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;

public class RollerBlockEntity extends MachineBlockEntity {
	public RollerBlockEntity() {
		super(FTBICElectricBlocks.ROLLER.blockEntity.get(), 1, 1);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		energyCapacity = FTBICConfig.ROLLER_CAPACITY;
		energyUse = FTBICConfig.ROLLER_USE;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.rolling;
	}
}