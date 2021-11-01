package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.PowerTier;

public class AntimatterFabricatorBlockEntity extends MachineBlockEntity {
	public AntimatterFabricatorBlockEntity() {
		super(FTBICElectricBlocks.ANTIMATTER_FABRICATOR.blockEntity.get(), 1, 1);
		inputPowerTier = PowerTier.EV;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.antimatterFabricator;
	}
}