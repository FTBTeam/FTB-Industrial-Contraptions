package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;

public class ExtruderBlockEntity extends MachineBlockEntity {
	public ExtruderBlockEntity() {
		super(FTBICElectricBlocks.EXTRUDER.blockEntity.get(), 1, 1);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		energyCapacity = 8000;
		energyUse = 20;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.extruding;
	}
}