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
		maxEnergyOutput = FTBICConfig.EV_SOLAR_PANEL_OUTPUT;
		energyCapacity = maxEnergyOutput * 60;
	}
}
