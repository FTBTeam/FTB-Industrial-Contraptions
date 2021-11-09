package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.EnergyTier;

public class LVTransformerBlockEntity extends TransformerBlockEntity {
	public LVTransformerBlockEntity() {
		super(FTBICElectricBlocks.LV_TRANSFORMER.blockEntity.get());
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputEnergyTier = EnergyTier.MV;
		outputEnergyTier = EnergyTier.LV;
		energyCapacity = EnergyTier.MV.transferRate;
	}
}
