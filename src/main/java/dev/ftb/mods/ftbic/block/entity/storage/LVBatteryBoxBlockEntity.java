package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.EnergyTier;

public class LVBatteryBoxBlockEntity extends BatteryBoxBlockEntity {
	public LVBatteryBoxBlockEntity() {
		super(FTBICElectricBlocks.LV_BATTERY_BOX.blockEntity.get());
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputEnergyTier = EnergyTier.LV;
		outputEnergyTier = EnergyTier.MV;
		energyCapacity = FTBICConfig.LV_BATTERY_BOX_CAPACITY;
	}
}
