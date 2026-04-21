package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class IVRectifierBlockEntity extends EnergyRectifierBlockEntity {
	public IVRectifierBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.IV_RECTIFIER, pos, state);
	}
}
