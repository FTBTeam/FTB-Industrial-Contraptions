package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MVSolarPanelBlockEntity extends SolarPanelBlockEntity {
	public MVSolarPanelBlockEntity(BlockPos pos, BlockState state) {
		super(FTBICElectricBlocks.MV_SOLAR_PANEL, pos, state);
	}
}
