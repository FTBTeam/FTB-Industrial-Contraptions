package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.EnergyTier;

public class EVBatteryBoxBlockEntity extends BatteryBoxBlockEntity {
	public EVBatteryBoxBlockEntity() {
		super(FTBICElectricBlocks.EV_BATTERY_BOX.blockEntity.get());
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputEnergyTier = EnergyTier.EV;
		maxEnergyOutput = EnergyTier.EV.transferRate;
		energyCapacity = FTBICConfig.EV_BATTERY_BOX_CAPACITY;
	}
}
