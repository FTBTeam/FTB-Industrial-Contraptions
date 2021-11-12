package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.EnergyTier;

public class HVTransformerBlockEntity extends TransformerBlockEntity {
	public HVTransformerBlockEntity() {
		super(FTBICElectricBlocks.HV_TRANSFORMER.blockEntity.get());
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputEnergyTier = EnergyTier.EV;
		maxEnergyOutput = EnergyTier.HV.transferRate;
		energyCapacity = EnergyTier.EV.transferRate;
	}
}
