package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.recipe.FTBICRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AdvancedPoweredFurnaceBlockEntity extends MachineBlockEntity {
	public AdvancedPoweredFurnaceBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.ADVANCED_POWERED_FURNACE, FTBICRecipes.SMELTING, pos, state);
	}
}
