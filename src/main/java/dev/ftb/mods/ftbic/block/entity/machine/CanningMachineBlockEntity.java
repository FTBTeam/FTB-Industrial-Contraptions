package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.MachineRecipeSerializer;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CanningMachineBlockEntity extends MachineBlockEntity {
	public CanningMachineBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.CANNING_MACHINE, pos, state);
	}

	@Override
	public MachineRecipeSerializer getRecipeSerializer() {
		return FTBICRecipes.CANNING.get();
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.canning;
	}

	@Override
	public void shiftInputs() {
	}
}