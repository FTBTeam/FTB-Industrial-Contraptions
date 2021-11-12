package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.EnergyTier;

public class HVBatteryBoxBlockEntity extends BatteryBoxBlockEntity {
	public HVBatteryBoxBlockEntity() {
		super(FTBICElectricBlocks.HV_BATTERY_BOX.blockEntity.get());
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputEnergyTier = EnergyTier.HV;
		maxEnergyOutput = EnergyTier.HV.transferRate;
		energyCapacity = FTBICConfig.HV_BATTERY_BOX_CAPACITY;
	}
}
