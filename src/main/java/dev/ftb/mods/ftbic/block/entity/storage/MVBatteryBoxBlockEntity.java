package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.EnergyTier;

public class MVBatteryBoxBlockEntity extends BatteryBoxBlockEntity {
	public MVBatteryBoxBlockEntity() {
		super(FTBICElectricBlocks.MV_BATTERY_BOX.blockEntity.get());
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputEnergyTier = EnergyTier.MV;
		outputEnergyTier = EnergyTier.HV;
		energyCapacity = FTBICConfig.MV_BATTERY_BOX_CAPACITY;
	}
}
