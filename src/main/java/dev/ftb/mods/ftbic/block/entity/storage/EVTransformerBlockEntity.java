package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.EnergyTier;

public class EVTransformerBlockEntity extends TransformerBlockEntity {
	public EVTransformerBlockEntity() {
		super(FTBICElectricBlocks.HV_TRANSFORMER.blockEntity.get());
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputEnergyTier = EnergyTier.XV;
		maxEnergyOutput = EnergyTier.EV.transferRate;
		energyCapacity = EnergyTier.XV.transferRate;
	}
}
