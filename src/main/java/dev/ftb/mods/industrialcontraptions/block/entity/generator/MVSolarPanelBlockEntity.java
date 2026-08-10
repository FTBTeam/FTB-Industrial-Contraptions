package dev.ftb.mods.industrialcontraptions.block.entity.generator;

import dev.ftb.mods.industrialcontraptions.block.ICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MVSolarPanelBlockEntity extends SolarPanelBlockEntity {
	public MVSolarPanelBlockEntity(BlockPos pos, BlockState state) {
		super(ICElectricBlocks.MV_SOLAR_PANEL, pos, state);
	}
}
