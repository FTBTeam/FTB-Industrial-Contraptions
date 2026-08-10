package dev.ftb.mods.industrialcontraptions.block.entity.storage;

import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MVRectifierBlockEntity extends EnergyRectifierBlockEntity {
	public MVRectifierBlockEntity(BlockPos pos, BlockState state) {
		super(ICElectricBlocks.MV_RECTIFIER, pos, state);
	}
}
