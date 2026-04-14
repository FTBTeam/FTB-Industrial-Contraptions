package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class RollerBlockEntity extends MachineBlockEntity {
	public RollerBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.ROLLER, FTBICRecipes.ROLLING, pos, state);
	}
}
