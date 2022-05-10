package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class RollerBlockEntity extends MachineBlockEntity {
	public RollerBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.ROLLER, pos, state);
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.rolling;
	}
}