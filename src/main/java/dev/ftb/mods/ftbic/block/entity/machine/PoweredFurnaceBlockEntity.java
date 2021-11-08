package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;

public class PoweredFurnaceBlockEntity extends MachineBlockEntity {
	public PoweredFurnaceBlockEntity() {
		super(FTBICElectricBlocks.POWERED_FURNACE.blockEntity.get(), 1, 1);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		energyCapacity = 4160;
		energyUse = 30;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.smelting;
	}
}