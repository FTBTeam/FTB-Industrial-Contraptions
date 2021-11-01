package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;

public class ElectricFurnaceBlockEntity extends MachineBlockEntity {
	public ElectricFurnaceBlockEntity() {
		super(FTBICElectricBlocks.ELECTRIC_FURNACE.blockEntity.get(), 1, 1);
		energyCapacity = 4160;
		baseEnergyUse = 30;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.smelting;
	}
}