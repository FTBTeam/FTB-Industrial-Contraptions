package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.PowerTier;

public class HVTransformerBlockEntity extends TransformerBlockEntity {
	public HVTransformerBlockEntity() {
		super(FTBICElectricBlocks.HV_TRANSFORMER.blockEntity.get());
	}

	@Override
	public void initProperties() {
		super.initProperties();
		inputPowerTier = PowerTier.EV;
		outputPowerTier = PowerTier.HV;
		energyCapacity = PowerTier.EV.transferRate;
	}
}
