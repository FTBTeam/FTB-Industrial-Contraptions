package dev.ftb.mods.ftbic.block.entity.generator;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.EnergyTier;

public class MVSolarPanelBlockEntity extends SolarPanelBlockEntity {
	public MVSolarPanelBlockEntity() {
		super(FTBICElectricBlocks.MV_SOLAR_PANEL.blockEntity.get());
	}

	@Override
	public void initProperties() {
		super.initProperties();
		outputEnergyTier = EnergyTier.MV;
		solarOutput = FTBICConfig.MV_SOLAR_PANEL_OUTPUT;
		energyCapacity = solarOutput * 60;
	}
}
