package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;

public class PoweredFurnaceBlockEntity extends MachineBlockEntity {
	public PoweredFurnaceBlockEntity() {
		super(FTBICElectricBlocks.POWERED_FURNACE);
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.smelting;
	}
}