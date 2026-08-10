package dev.ftb.mods.industrialcontraptions.block.entity.storage;

import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EVRectifierBlockEntity extends EnergyRectifierBlockEntity {
	public EVRectifierBlockEntity(BlockPos pos, BlockState state) {
		super(ICElectricBlocks.EV_RECTIFIER, pos, state);
	}
}
