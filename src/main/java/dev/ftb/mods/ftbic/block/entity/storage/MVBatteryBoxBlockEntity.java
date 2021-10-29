package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.PowerTier;

public class MVBatteryBoxBlockEntity extends BatteryBoxBlockEntity {
	public MVBatteryBoxBlockEntity() {
		super(FTBICElectricBlocks.MV_BATTERY_BOX.blockEntity.get());
		inputPowerTier = PowerTier.MV;
		outputPowerTier = PowerTier.HV;
		energyCapacity = FTBICConfig.MV_BATTERY_BOX_CAPACITY;
	}
}
