package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.EnergyTier;

public class MVTransformerBlockEntity extends TransformerBlockEntity {
	public MVTransformerBlockEntity() {
		super(FTBICElectricBlocks.MV_TRANSFORMER.blockEntity.get());
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputEnergyTier = EnergyTier.HV;
		maxEnergyOutput = EnergyTier.MV.transferRate;
		energyCapacity = EnergyTier.HV.transferRate;
	}
}
