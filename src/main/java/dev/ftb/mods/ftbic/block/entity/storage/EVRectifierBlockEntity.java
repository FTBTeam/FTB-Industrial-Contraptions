package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EVRectifierBlockEntity extends EnergyRectifierBlockEntity {
	public EVRectifierBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.EV_RECTIFIER, pos, state);
	}
}
