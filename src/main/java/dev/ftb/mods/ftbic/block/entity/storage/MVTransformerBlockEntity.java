package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.PowerTier;

public class MVTransformerBlockEntity extends TransformerBlockEntity {
	public MVTransformerBlockEntity() {
		super(FTBICElectricBlocks.MV_TRANSFORMER.blockEntity.get());
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputPowerTier = PowerTier.HV;
		outputPowerTier = PowerTier.MV;
		energyCapacity = PowerTier.HV.transferRate;
	}
}
