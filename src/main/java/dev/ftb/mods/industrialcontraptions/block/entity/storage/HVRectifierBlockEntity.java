package dev.ftb.mods.industrialcontraptions.block.entity.storage;

import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class HVRectifierBlockEntity extends EnergyRectifierBlockEntity {
	public HVRectifierBlockEntity(BlockPos pos, BlockState state) {
		super(ICElectricBlocks.HV_RECTIFIER, pos, state);
	}
}
