package dev.ftb.mods.ftbic.block.entity.storage;

import dev.ftb.mods.ftbic.block.FTBICElectricBlocks;
import dev.ftb.mods.ftbic.util.PowerTier;

public class MVTransformerBlockEntity extends TransformerBlockEntity {
	public MVTransformerBlockEntity() {
		super(FTBICElectricBlocks.MV_TRANSFORMER.blockEntity.get());
		inputPowerTier = PowerTier.HV;
		outputPowerTier = PowerTier.MV;
		energyCapacity = PowerTier.HV.transferRate;
	}
}
