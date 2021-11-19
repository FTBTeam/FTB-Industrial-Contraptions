package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;

public class AdvancedPoweredFurnaceBlockEntity extends MachineBlockEntity {
	public AdvancedPoweredFurnaceBlockEntity() {
		super(FTBICElectricBlocks.ADVANCED_POWERED_FURNACE);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		shouldAccelerate = true;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.smelting;
	}
}