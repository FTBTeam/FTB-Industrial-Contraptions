package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;

public class HVSolarPanelBlockEntity extends SolarPanelBlockEntity {
	public HVSolarPanelBlockEntity() {
		super(FTBICElectricBlocks.HV_SOLAR_PANEL.blockEntity.get());
	}

	@Override
	public void initProperties() {
		super.initProperties();
		maxEnergyOutput = FTBICConfig.HV_SOLAR_PANEL_OUTPUT;
		energyCapacity = maxEnergyOutput * 60;
	}
}
