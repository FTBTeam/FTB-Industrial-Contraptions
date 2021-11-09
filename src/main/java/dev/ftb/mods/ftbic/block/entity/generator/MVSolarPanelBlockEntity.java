package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;

public class MVSolarPanelBlockEntity extends SolarPanelBlockEntity {
	public MVSolarPanelBlockEntity() {
		super(FTBICElectricBlocks.MV_SOLAR_PANEL.blockEntity.get());
	}

	@Override
	public void initProperties() {
		super.initProperties();
		solarOutput = FTBICConfig.MV_SOLAR_PANEL_OUTPUT;
		energyCapacity = solarOutput * 60;
	}
}
