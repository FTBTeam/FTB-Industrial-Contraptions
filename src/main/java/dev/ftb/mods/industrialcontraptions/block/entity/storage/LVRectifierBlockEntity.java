package dev.ftb.mods.industrialcontraptions.block.entity.storage;

import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LVRectifierBlockEntity extends EnergyRectifierBlockEntity {
	public LVRectifierBlockEntity(BlockPos pos, BlockState state) {
		super(ICElectricBlocks.LV_RECTIFIER, pos, state);
	}
}
