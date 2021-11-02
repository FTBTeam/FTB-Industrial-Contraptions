package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;

public class LVSolarPanelBlockEntity extends SolarPanelBlockEntity {
	public LVSolarPanelBlockEntity() {
		super(FTBICElectricBlocks.LV_SOLAR_PANEL.blockEntity.get());
		solarOutput = FTBICConfig.LV_SOLAR_PANEL_GENERATION;
		energyCapacity = solarOutput * 4;
	}
}
