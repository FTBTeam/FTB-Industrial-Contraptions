package dev.ftb.mods.industrialcontraptions.block.entity.generator;

import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EVSolarPanelBlockEntity extends SolarPanelBlockEntity {
	public EVSolarPanelBlockEntity(BlockPos pos, BlockState state) {
		super(ICElectricBlocks.EV_SOLAR_PANEL, pos, state);
	}
}
