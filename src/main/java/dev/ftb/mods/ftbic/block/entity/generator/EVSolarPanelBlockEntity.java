package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;

public class EVSolarPanelBlockEntity extends SolarPanelBlockEntity {
	public EVSolarPanelBlockEntity() {
		super(FTBICElectricBlocks.EV_SOLAR_PANEL.blockEntity.get());
	}

	@Override
	public void initProperties() {
		super.initProperties();
		solarOutput = FTBICConfig.EV_SOLAR_PANEL_OUTPUT;
		energyCapacity = solarOutput * 60;
	}
}
