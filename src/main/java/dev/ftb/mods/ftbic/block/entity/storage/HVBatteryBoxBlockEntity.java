package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.PowerTier;

public class HVBatteryBoxBlockEntity extends BatteryBoxBlockEntity {
	public HVBatteryBoxBlockEntity() {
		super(FTBICElectricBlocks.HV_BATTERY_BOX.blockEntity.get());
		inputPowerTier = PowerTier.HV;
		outputPowerTier = PowerTier.EV;
		energyCapacity = FTBICConfig.HV_BATTERY_BOX_CAPACITY;
	}
}
