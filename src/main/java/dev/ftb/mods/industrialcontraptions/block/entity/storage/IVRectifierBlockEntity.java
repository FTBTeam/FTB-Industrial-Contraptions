package dev.ftb.mods.industrialcontraptions.block.entity.storage;

import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class IVRectifierBlockEntity extends EnergyRectifierBlockEntity {
	public IVRectifierBlockEntity(BlockPos pos, BlockState state) {
		super(ICElectricBlocks.IV_RECTIFIER, pos, state);
	}
}
