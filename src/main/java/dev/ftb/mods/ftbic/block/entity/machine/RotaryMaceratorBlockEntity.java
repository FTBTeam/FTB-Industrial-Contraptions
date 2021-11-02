package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;

public class RotaryMaceratorBlockEntity extends MachineBlockEntity {
	public RotaryMaceratorBlockEntity() {
		super(FTBICElectricBlocks.ROTARY_MACERATOR.blockEntity.get(), 2, 2);
		energyCapacity = 12000;
		baseEnergyUse = 20;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.macerating;
	}
}