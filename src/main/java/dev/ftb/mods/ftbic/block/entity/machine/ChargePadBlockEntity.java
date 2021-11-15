package dev.ftb.mods.ftbic.block.entity.machine;

import dev.ftb.mods.ftbic.FTBICConfig;
import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.block.entity.ElectricBlockEntity;
import dev.ftb.mods.ftbic.util.EnergyTier;

public class ChargePadBlockEntity extends ElectricBlockEntity {
	public ChargePadBlockEntity() {
		super(FTBICElectricBlocks.CHARGE_PAD.blockEntity.get(), 0, 0);
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputEnergyTier = EnergyTier.XV;
		energyCapacity = FTBICConfig.CHARGE_PAD_CAPACITY;
	}
}