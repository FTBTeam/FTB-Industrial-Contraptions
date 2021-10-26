package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;

public class LVSolarPanelBlockEntity extends SolarPanelBlockEntity {
	public LVSolarPanelBlockEntity() {
		super(FTBICElectricBlocks.LV_SOLAR_PANEL.blockEntity.get());
	}

	@Override
	public int getGeneration() {
		return FTBICConfig.LV_SOLAR_PANEL_GENERATION;
	}
}
