package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.MachineRecipeResults;
import dev.ftb.mods.ftbic.recipe.RecipeCache;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PoweredFurnaceBlockEntity extends MachineBlockEntity {
	public PoweredFurnaceBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.POWERED_FURNACE, pos, state);
	}

	@Override
	public MachineRecipeResults getRecipes(RecipeCache cache) {
		return cache.smelting;
	}
}