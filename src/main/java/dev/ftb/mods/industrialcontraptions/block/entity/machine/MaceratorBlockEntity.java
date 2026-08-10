package dev.ftb.mods.industrialcontraptions.block.entity.machine;

import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import dev.ftb.mods.industrialcontraptions.recipe.ICRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MaceratorBlockEntity extends MachineBlockEntity {
	public MaceratorBlockEntity(BlockPos pos, BlockState state) {
		super(ICElectricBlocks.MACERATOR, ICRecipes.MACERATING, pos, state);
	}
}
