package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;

public class CompressorBlockEntity extends SimpleRecipeMachineBlockEntity {
	public CompressorBlockEntity() {
		super(FTBICElectricBlocks.COMPRESSOR.blockEntity.get());
		energyCapacity = 8000;
		energyUse = 20;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.compressing;
	}
}