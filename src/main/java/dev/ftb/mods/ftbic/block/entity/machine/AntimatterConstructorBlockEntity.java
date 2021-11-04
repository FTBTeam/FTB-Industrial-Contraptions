package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import dev.ftb.mods.ftbic.util.PowerTier;

public class AntimatterConstructorBlockEntity extends MachineBlockEntity {
	public AntimatterConstructorBlockEntity() {
		super(FTBICElectricBlocks.ANTIMATTER_CONSTRUCTOR.blockEntity.get(), 1, 1);
		inputPowerTier = PowerTier.EV;
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.antimatterFabricator;
	}
}