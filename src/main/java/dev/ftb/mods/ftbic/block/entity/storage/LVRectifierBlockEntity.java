package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LVRectifierBlockEntity extends EnergyRectifierBlockEntity {
	public LVRectifierBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.LV_RECTIFIER, pos, state);
	}
}
