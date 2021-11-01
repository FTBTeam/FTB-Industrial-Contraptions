package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;

public class MaceratorBlockEntity extends SimpleRecipeMachineBlockEntity {
	public MaceratorBlockEntity() {
		super(FTBICElectricBlocks.MACERATOR.blockEntity.get());
		energyCapacity = 12000;
		energyUse = 20;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.macerating;
	}
}